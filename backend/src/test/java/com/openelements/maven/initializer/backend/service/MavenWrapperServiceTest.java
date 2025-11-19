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
 * software distributed under this License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.openelements.maven.initializer.backend.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.openelements.maven.initializer.backend.exception.ProjectServiceException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MavenWrapperServiceTest {

  private MavenWrapperService mavenWrapperService;

  @BeforeEach
  void setUp() {
    mavenWrapperService = new MavenWrapperService();
  }

  @Test
  void testAddMavenWrapperSuccess(@TempDir Path tempDir) throws IOException {
    // Given
    copyValidTestProject(tempDir);

    // When
    assertDoesNotThrow(() -> mavenWrapperService.addMavenWrapper(tempDir));

    // Then
    assertAll(
        () -> assertTrue(Files.exists(tempDir.resolve("mvnw")), "mvnw file should exist"),
        () -> assertTrue(Files.exists(tempDir.resolve("mvnw.cmd")), "mvnw.cmd file should exist"),
        () ->
            assertTrue(
                Files.exists(tempDir.resolve(".mvn/wrapper/maven-wrapper.properties")),
                "maven-wrapper.properties should exist"),
        () ->
            assertTrue(
                Files.isDirectory(tempDir.resolve(".mvn/wrapper")),
                ".mvn/wrapper directory should exist"));

    // The jar file may or may not exist depending on Maven wrapper plugin version
    // Newer versions may download it on-demand
    Path jarPath = tempDir.resolve(".mvn/wrapper/maven-wrapper.jar");
    if (Files.exists(jarPath)) {
      assertTrue(
          Files.isRegularFile(jarPath), "maven-wrapper.jar should be a regular file if it exists");
    }
  }

  @Test
  void testAddMavenWrapperWithNonExistentDirectory() {
    // Given
    Path nonExistentPath = Paths.get("/non/existent/directory/path");

    // When & Then
    ProjectServiceException exception =
        assertThrows(
            ProjectServiceException.class,
            () -> mavenWrapperService.addMavenWrapper(nonExistentPath),
            "Should throw ProjectServiceException for non-existent directory");

    assertNotNull(exception.getMessage());
    assertTrue(
        exception.getMessage().contains("Failed"), "Exception message should indicate failure");
  }

  @Test
  void testAddMavenWrapperWithNullPath() {
    // When & Then
    assertThrows(
        NullPointerException.class,
        () -> mavenWrapperService.addMavenWrapper(null),
        "Should throw NullPointerException for null path");
  }

  @Test
  void testAddMavenWrapperCreatesCorrectWrapperStructure(@TempDir Path tempDir) throws IOException {
    // Given
    copyValidTestProject(tempDir);

    // When
    mavenWrapperService.addMavenWrapper(tempDir);

    // Then - Verify wrapper directory structure
    Path wrapperDir = tempDir.resolve(".mvn/wrapper");
    assertAll(
        () -> assertTrue(Files.isDirectory(wrapperDir), ".mvn/wrapper directory should exist"),
        () ->
            assertTrue(
                Files.exists(wrapperDir.resolve("maven-wrapper.properties")),
                "maven-wrapper.properties should exist in .mvn/wrapper"));

    // Verify wrapper properties file content
    String propertiesContent = Files.readString(wrapperDir.resolve("maven-wrapper.properties"));
    assertTrue(
        propertiesContent.contains("wrapperVersion")
            || propertiesContent.contains("distributionUrl"),
        "Properties file should contain wrapper configuration");
  }

  @Test
  void testAddMavenWrapperWithExistingWrapperFiles(@TempDir Path tempDir) throws IOException {
    // Given
    copyValidTestProject(tempDir);
    mavenWrapperService.addMavenWrapper(tempDir);

    // When - Add wrapper again (should overwrite/update)
    assertDoesNotThrow(() -> mavenWrapperService.addMavenWrapper(tempDir));

    // Then
    assertAll(
        () ->
            assertTrue(
                Files.exists(tempDir.resolve("mvnw")), "mvnw should still exist after re-run"),
        () ->
            assertTrue(
                Files.exists(tempDir.resolve("mvnw.cmd")),
                "mvnw.cmd should still exist after re-run"),
        () ->
            assertTrue(
                Files.exists(tempDir.resolve(".mvn/wrapper/maven-wrapper.properties")),
                "maven-wrapper.properties should still exist after re-run"));
  }

  /**
   * Copies the valid test project from test resources to the target directory.
   *
   * @param projectDir the directory where the project should be copied
   * @throws IOException if file copy fails
   */
  private void copyValidTestProject(Path projectDir) throws IOException {
    Path testProjectPath =
        Paths.get("src/test/resources/validTestProject").toAbsolutePath().normalize();

    if (!Files.exists(testProjectPath)) {
      throw new IOException(
          "Test project not found at: "
              + testProjectPath
              + ". Make sure validTestProject exists in test resources.");
    }

    // Copy the entire directory structure
    try (Stream<Path> paths = Files.walk(testProjectPath)) {
      paths.forEach(
          source -> {
            try {
              Path target = projectDir.resolve(testProjectPath.relativize(source));
              if (Files.isDirectory(source)) {
                Files.createDirectories(target);
              } else {
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
              }
            } catch (IOException e) {
              throw new RuntimeException("Failed to copy test project files", e);
            }
          });
    }
  }
}
