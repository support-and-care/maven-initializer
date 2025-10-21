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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.openelements.maven.initializer.backend.dto.ProjectRequestDTO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ProjectGeneratorServiceTest {

  @Autowired private ProjectGeneratorService projectGeneratorService;

  @Test
  void testProjectGeneration() {
    // Given
    final ProjectRequestDTO validRequest = createValidRequest();

    // When
    assertDoesNotThrow(
        () -> {
          final String result = projectGeneratorService.generateProject(validRequest);
          assertNotNull(result);
          assertTrue(result.contains(validRequest.getArtifactId()));
          assertTrue(java.nio.file.Files.exists(java.nio.file.Paths.get(result)));
        });

    assertThrows(RuntimeException.class, () -> projectGeneratorService.generateProject(null));
  }

  @Test
  void testProjectZipCreation() {
    // Given
    final ProjectRequestDTO validRequest = createValidRequest();
    final String validProjectPath = projectGeneratorService.generateProject(validRequest);
    final String invalidProjectPath = "/non/existent/path";

    // When
    assertDoesNotThrow(
        () -> {
          final byte[] zipBytes = projectGeneratorService.createProjectZip(validProjectPath);
          assertNotNull(zipBytes);
          assertTrue(zipBytes.length > 0);
        });

    // When
    assertThrows(RuntimeException.class, () -> projectGeneratorService.createProjectZip(null));
    assertThrows(
        RuntimeException.class, () -> projectGeneratorService.createProjectZip(invalidProjectPath));
  }

  @Test
  void testProjectZipGeneration() {
    // Given
    final ProjectRequestDTO validRequest = createValidRequest();

    // When
    assertDoesNotThrow(
        () -> {
          final byte[] result = projectGeneratorService.generateProjectZip(validRequest);
          assertNotNull(result);
          assertTrue(result.length > 0);
        });

    assertThrows(RuntimeException.class, () -> projectGeneratorService.generateProjectZip(null));
  }

  @Test
  void testPomFileContainsExpectedElements() throws IOException {

    // Given
    ProjectRequestDTO validRequest = createValidRequest();

    // When
    String projectPath = projectGeneratorService.generateProject(validRequest);

    Path pomFile = Path.of(projectPath, "pom.xml");
    assertTrue(Files.exists(pomFile));

    String pomContent = Files.readString(pomFile);
    assertTrue(pomContent.contains("<groupId>" + validRequest.getGroupId() + "</groupId>"));
    assertTrue(
        pomContent.contains("<artifactId>" + validRequest.getArtifactId() + "</artifactId>"));
    assertTrue(pomContent.contains("<version>" + validRequest.getVersion() + "</version>"));
    assertTrue(pomContent.contains("<name>" + validRequest.getName() + "</name>"));
    assertTrue(
        pomContent.contains("<description>" + validRequest.getDescription() + "</description>"));
    assertTrue(
        pomContent.contains(
            "<maven.compiler.release>"
                + validRequest.getJavaVersion()
                + "</maven.compiler.release>"));
  }

  @Test
  void testPomContainsAllDefaultPlugins() throws Exception {

    // Given
    ProjectRequestDTO validRequest = createValidRequest();

    // When
    String projectPath = projectGeneratorService.generateProject(validRequest);
    Path pomFile = Path.of(projectPath, "pom.xml");

    // Then
    assertTrue(Files.exists(pomFile), "POM file should exist");

    String pomContent = Files.readString(pomFile);
    List<String> defaultPlugins =
        List.of(
            "maven-compiler-plugin",
            "maven-resources-plugin",
            "maven-surefire-plugin",
            "maven-jar-plugin",
            "maven-install-plugin",
            "maven-deploy-plugin");

    List<String> missingPlugins =
        defaultPlugins.stream().filter(plugin -> !pomContent.contains(plugin)).toList();

    assertTrue(missingPlugins.isEmpty(), "Missing plugins in pom.xml: " + missingPlugins);
  }

  private ProjectRequestDTO createValidRequest() {
    final ProjectRequestDTO request = new ProjectRequestDTO();
    request.setGroupId("com.example");
    request.setArtifactId("test-project");
    request.setVersion("1.0.0-SNAPSHOT");
    request.setName("Test Project");
    request.setDescription("Test project description");
    request.setJavaVersion("17");
    return request;
  }
}
