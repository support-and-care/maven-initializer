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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.openelements.maven.initializer.backend.config.MavenToolboxConfig;
import com.openelements.maven.initializer.backend.domain.ProjectGenerationResult;
import com.openelements.maven.initializer.backend.dto.ProjectRequestDTO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectGeneratorITTest {

  private ProjectGeneratorService projectGeneratorServiceUnderTest;
  @Mock private ProjectStructureService projectStructureServiceMock;
  @Mock private ArtifactVersionService artifactVersionService;

  @Test
  void testMavenWrapperFilesHaveExecutablePermissions() {
    // Given
    MavenToolboxConfig mavenToolboxConfig = new MavenToolboxConfig();
    var toolbox = mavenToolboxConfig.toolboxCommando(mavenToolboxConfig.mavenContext());
    MavenWrapperService realMavenWrapperService = new MavenWrapperService();
    projectGeneratorServiceUnderTest =
        new ProjectGeneratorService(
            toolbox, projectStructureServiceMock, artifactVersionService, realMavenWrapperService);

    ProjectRequestDTO request = createValidRequest();
    request.setIncludeMavenWrapper(true);

    // When
    ProjectGenerationResult result = projectGeneratorServiceUnderTest.generateProject(request);
    Path projectPath = Paths.get(result.projectPath());
    Path mvnw = projectPath.resolve("mvnw");

    // Then
    assertTrue(Files.exists(mvnw), "mvnw should exist");
    assertTrue(Files.isExecutable(mvnw), "mvnw should have executable permissions");
  }

  @Test
  void testGeneratedProjectBuildsSuccessfully() throws IOException, InterruptedException {
    // Given
    MavenToolboxConfig mavenToolboxConfig = new MavenToolboxConfig();
    var toolbox = mavenToolboxConfig.toolboxCommando(mavenToolboxConfig.mavenContext());
    ArtifactVersionService realArtifactVersionService = new ArtifactVersionService(toolbox);
    ResourceTemplateEngine resourceTemplateEngine = new ResourceTemplateEngine();
    ProjectStructureService realProjectStructureService =
        new ProjectStructureService(resourceTemplateEngine);
    MavenWrapperService realMavenWrapperService = new MavenWrapperService();
    projectGeneratorServiceUnderTest =
        new ProjectGeneratorService(
            toolbox,
            realProjectStructureService,
            realArtifactVersionService,
            realMavenWrapperService);

    ProjectRequestDTO request = createValidRequest();
    request.setIncludeMavenWrapper(true);

    // When - Generate project
    ProjectGenerationResult result = projectGeneratorServiceUnderTest.generateProject(request);
    Path projectPath = Paths.get(result.projectPath());
    Path mvnw = projectPath.resolve("mvnw");

    assertTrue(Files.exists(mvnw), "mvnw should exist");
    assertTrue(Files.isExecutable(mvnw), "mvnw should be executable");

    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.directory(projectPath.toFile());

    String os = System.getProperty("os.name").toLowerCase();
    String mvnwCommand = os.contains("win") ? "mvnw.cmd" : "./mvnw";

    processBuilder.command(mvnwCommand, "clean", "verify");
    processBuilder.redirectErrorStream(true);

    Process process = processBuilder.start();

    StringBuilder output = new StringBuilder();
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        output.append(line).append("\n");
      }
    }

    // Then
    int exitCode = process.waitFor();
    if (exitCode != 0) {
      System.err.println("Maven build failed with exit code: " + exitCode);
      System.err.println("Build output:\n" + output);
    }

    assertEquals(0, exitCode, "Maven build should succeed with exit code 0. Output: " + output);
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
