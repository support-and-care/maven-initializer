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

import com.openelements.maven.initializer.backend.domain.AssertionLibrary;
import com.openelements.maven.initializer.backend.domain.ProjectGenerationResult;
import com.openelements.maven.initializer.backend.dto.ProjectRequestDTO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ProjectGeneratorITTest {

  @Autowired private ProjectGeneratorService projectGeneratorService;

  @Test
  void testMavenWrapperFilesHaveExecutablePermissions() {
    // Given
    ProjectRequestDTO request = createValidRequest();
    request.setIncludeMavenWrapper(true);

    // When
    ProjectGenerationResult result = projectGeneratorService.generateProject(request);
    Path projectPath = Paths.get(result.projectPath());
    Path mvnw = projectPath.resolve("mvnw");

    // Then
    assertTrue(Files.exists(mvnw), "mvnw should exist");
    assertTrue(Files.isExecutable(mvnw), "mvnw should have executable permissions");
  }

  @Test
  void testGeneratedProjectBuildsSuccessfully() throws IOException, InterruptedException {
    // Given
    ProjectRequestDTO request = createValidRequest();
    request.setIncludeMavenWrapper(true);

    // When - Generate project
    ProjectGenerationResult result = projectGeneratorService.generateProject(request);
    Path projectPath = Paths.get(result.projectPath());

    // Then
    int exitCode = executeMavenBuild(projectPath);
    assertEquals(0, exitCode, "Maven build should succeed with exit code 0");
  }

  @Test
  void testGeneratedProjectWithAssertJBuildsSuccessfully()
      throws IOException, InterruptedException {
    // Given
    ProjectRequestDTO request = createValidRequest();
    request.setIncludeMavenWrapper(true);
    request.setAssertionLibrary(AssertionLibrary.ASSERTJ);

    // When - Generate project
    ProjectGenerationResult result = projectGeneratorService.generateProject(request);
    Path projectPath = Paths.get(result.projectPath());

    // Then
    int exitCode = executeMavenBuild(projectPath);
    assertEquals(0, exitCode, "Maven build with AssertJ should succeed with exit code 0");
  }

  @Test
  void testGeneratedProjectWithHamcrestBuildsSuccessfully()
      throws IOException, InterruptedException {
    // Given
    ProjectRequestDTO request = createValidRequest();
    request.setIncludeMavenWrapper(true);
    request.setAssertionLibrary(AssertionLibrary.HAMCREST);

    // When - Generate project
    ProjectGenerationResult result = projectGeneratorService.generateProject(request);
    Path projectPath = Paths.get(result.projectPath());

    // Then
    int exitCode = executeMavenBuild(projectPath);
    assertEquals(0, exitCode, "Maven build with Hamcrest should succeed with exit code 0");
  }

  @Test
  void testGeneratedProjectWithNoAssertionLibraryBuildsSuccessfully()
      throws IOException, InterruptedException {
    // Given - default is "none" (JUnit only)
    ProjectRequestDTO request = createValidRequest();
    request.setIncludeMavenWrapper(true);
    request.setAssertionLibrary(AssertionLibrary.NONE);

    // When - Generate project
    ProjectGenerationResult result = projectGeneratorService.generateProject(request);
    Path projectPath = Paths.get(result.projectPath());

    // Then
    int exitCode = executeMavenBuild(projectPath);
    assertEquals(0, exitCode, "Maven build with JUnit only should succeed with exit code 0");
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

  private int executeMavenBuild(Path projectPath) throws IOException, InterruptedException {
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

    int exitCode = process.waitFor();
    if (exitCode != 0) {
      System.err.println("Maven build failed with exit code: " + exitCode);
      System.err.println("Build output:\n" + output);
    }

    return exitCode;
  }
}
