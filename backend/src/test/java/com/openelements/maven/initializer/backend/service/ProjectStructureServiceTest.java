/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.openelements.maven.initializer.backend.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.openelements.maven.initializer.backend.domain.AssertionLibrary;
import com.openelements.maven.initializer.backend.dto.ProjectRequestDTO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ProjectStructureServiceTest {

  @TempDir Path tempDir;

  private ProjectStructureService projectStructureService;
  private ProjectRequestDTO validRequest;

  @BeforeEach
  void setUp() {
    ResourceTemplateEngine resourceTemplateEngine = new ResourceTemplateEngine();
    projectStructureService = new ProjectStructureService(resourceTemplateEngine);
    validRequest = createValidRequest();
  }

  @Test
  void testProjectStructureCreation() {
    // When
    assertDoesNotThrow(() -> projectStructureService.createStructure(tempDir, validRequest));

    Path mainClassFile = tempDir.resolve("src/main/java/com/example/Testproject.java");
    Path testClassFile = tempDir.resolve("src/test/java/com/example/TestprojectTest.java");
    Path gitignoreFile = tempDir.resolve(".gitignore");

    // Then
    assertAll(
        () -> assertTrue(Files.exists(mainClassFile), "Main class file should exist"),
        () -> assertTrue(Files.exists(testClassFile), "Test class file should exist"),
        () -> assertTrue(Files.exists(gitignoreFile), ".gitignore file should exist"));

    assertThrows(
        RuntimeException.class,
        () -> projectStructureService.createStructure(tempDir, null),
        "Expected exception for null ProjectRequestDTO");
  }

  @Test
  void testMainClassContent() throws IOException {
    // When
    projectStructureService.createStructure(tempDir, validRequest);

    Path mainClassFile = tempDir.resolve("src/main/java/com/example/Testproject.java");
    assertTrue(Files.exists(mainClassFile), "Main class file should exist");

    String content = Files.readString(mainClassFile);

    assertAll(
        () ->
            assertTrue(
                content.contains("package com.example;"),
                "Package should match group and artifact ID"),
        () ->
            assertTrue(
                content.contains("public class Testproject"),
                "Class declaration should match artifact name"),
        () ->
            assertTrue(content.contains("Hello, Testproject!"), "Should include greeting message"),
        () ->
            assertTrue(
                content.contains("Test project description"),
                "JavaDoc should include project description"));
  }

  @Test
  void testTestClassContent() throws IOException {
    projectStructureService.createStructure(tempDir, validRequest);

    Path testClassFile = tempDir.resolve("src/test/java/com/example/TestprojectTest.java");
    assertTrue(Files.exists(testClassFile), "Test class file should exist");

    String content = Files.readString(testClassFile);

    assertAll(
        () -> assertTrue(content.contains("package com.example;"), "Package should match group ID"),
        () ->
            assertTrue(
                content.contains("import org.junit.jupiter.api.Test;"),
                "Should import JUnit test annotation"),
        () ->
            assertTrue(
                content.contains("class TestprojectTest"),
                "Class name should match main class + 'Test'"),
        () ->
            assertTrue(
                content.contains("assertTrue(true)"),
                "Test should contain JUnit assertion (default is none)"),
        () -> assertTrue(content.contains("@Test"), "Should include @Test annotation"));
  }

  @Test
  void testTestClassContentWithHamcrest() throws IOException {
    // Given
    validRequest.setAssertionLibrary(AssertionLibrary.HAMCREST);

    // When
    projectStructureService.createStructure(tempDir, validRequest);

    Path testClassFile = tempDir.resolve("src/test/java/com/example/TestprojectTest.java");
    assertTrue(Files.exists(testClassFile), "Test class file should exist");

    String content = Files.readString(testClassFile);

    // Then
    assertAll(
        () -> assertTrue(content.contains("package com.example;"), "Package should match group ID"),
        () ->
            assertTrue(
                content.contains("import org.junit.jupiter.api.Test;"),
                "Should import JUnit test annotation"),
        () ->
            assertTrue(
                content.contains("import static org.hamcrest.MatcherAssert.assertThat;"),
                "Should import Hamcrest MatcherAssert"),
        () ->
            assertTrue(
                content.contains("assertThat(true, is(true))"),
                "Test should contain Hamcrest assertion"));
  }

  @Test
  void testTestClassContentWithNoAssertionLibrary() throws IOException {
    // Given
    validRequest.setAssertionLibrary(AssertionLibrary.NONE);

    // When
    projectStructureService.createStructure(tempDir, validRequest);

    Path testClassFile = tempDir.resolve("src/test/java/com/example/TestprojectTest.java");
    assertTrue(Files.exists(testClassFile), "Test class file should exist");

    String content = Files.readString(testClassFile);

    // Then
    assertAll(
        () -> assertTrue(content.contains("package com.example;"), "Package should match group ID"),
        () ->
            assertTrue(
                content.contains("import org.junit.jupiter.api.Test;"),
                "Should import JUnit test annotation"),
        () ->
            assertTrue(
                content.contains("import static org.junit.jupiter.api.Assertions.assertTrue;"),
                "Should import JUnit Assertions"),
        () ->
            assertTrue(
                content.contains("assertTrue(true)"), "Test should contain JUnit assertion"));
  }

  @Test
  void testTestDirectoryStructure() {
    projectStructureService.createStructure(tempDir, validRequest);

    Path testJavaDir = tempDir.resolve("src/test/java/com/example");
    Path testClassFile = testJavaDir.resolve("TestprojectTest.java");

    assertAll(
        () -> assertTrue(Files.isDirectory(testJavaDir), "Test Java directory should exist"),
        () ->
            assertTrue(
                Files.exists(testClassFile), "Test class file should be placed in test directory"));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "target/",
        "pom.xml.tag",
        "dependency-reduced-pom.xml",
        "release.properties",
        "pom.xml.releaseBackup",
        "pom.xml.next",
        "buildNumber.properties",
        "pom.xml.versionsBackup"
      })
  void testGitignoreContainsExpectedEntries(String entry) throws IOException {
    // When
    projectStructureService.createStructure(tempDir, validRequest);

    Path gitignoreFile = tempDir.resolve(".gitignore");
    assertTrue(Files.exists(gitignoreFile), ".gitignore file should exist");

    // Then
    String content = Files.readString(gitignoreFile);
    assertTrue(content.contains(entry), "Missing .gitignore entry: " + entry);
  }

  @Test
  void testDirectoryStructureCreation() throws IOException {
    // When
    projectStructureService.createStructure(tempDir, validRequest);

    Path mainJavaDir = tempDir.resolve("src/main/java/com/example");

    assertAll(() -> assertTrue(Files.isDirectory(mainJavaDir), "Main Java directory should exist"));

    Path mainClassFile = mainJavaDir.resolve("Testproject.java");
    assertTrue(Files.exists(mainClassFile), "Main class should be correctly placed");
  }

  @Test
  void testReadmeCreationWithMavenWrapper() {
    // Given
    validRequest.setIncludeMavenWrapper(true);

    // When
    projectStructureService.createReadmeFile(tempDir, validRequest);

    // Then
    Path readmeFile = tempDir.resolve("README.md");
    assertTrue(Files.exists(readmeFile), "README.md file should exist");
  }

  @Test
  void testReadmeCreationWithoutMavenWrapper() {
    // Given
    validRequest.setIncludeMavenWrapper(false);

    // When
    projectStructureService.createReadmeFile(tempDir, validRequest);

    // Then
    Path readmeFile = tempDir.resolve("README.md");
    assertTrue(Files.exists(readmeFile), "README.md file should exist");
  }

  private ProjectRequestDTO createValidRequest() {
    ProjectRequestDTO request = new ProjectRequestDTO();
    request.setGroupId("com.example");
    request.setArtifactId("testproject");
    request.setVersion("1.0.0-SNAPSHOT");
    request.setName("Test Project");
    request.setDescription("Test project description");
    request.setJavaVersion("17");
    return request;
  }
}
