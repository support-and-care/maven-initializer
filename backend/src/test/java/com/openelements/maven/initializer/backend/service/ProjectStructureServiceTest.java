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

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.openelements.maven.initializer.backend.dto.ProjectRequestDTO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ProjectStructureServiceTest {

  @TempDir Path tempDir;

  private ProjectStructureService projectStructureService;

  @BeforeEach
  void setUp() {
    projectStructureService = new ProjectStructureService();
  }

  @Test
  void shouldCreateRequiredFiles() throws IOException {
    // Given
    ProjectRequestDTO request = createTestRequest();

    // When
    projectStructureService.createStructure(tempDir, request);

    // Then
    Path mainClassFile = tempDir.resolve("src/main/java/com/example/testproject/testproject.java");
    assertTrue(Files.exists(mainClassFile));

    Path gitignoreFile = tempDir.resolve(".gitignore");
    assertTrue(Files.exists(gitignoreFile));
  }

  @Test
  void shouldWriteMainClassContent() throws IOException {
    // Given
    ProjectRequestDTO request = createTestRequest();

    // When
    projectStructureService.createStructure(tempDir, request);

    // Then
    Path mainClassFile = tempDir.resolve("src/main/java/com/example/testproject/testproject.java");
    String content = Files.readString(mainClassFile);

    assertTrue(content.contains("package com.example;"));
    assertTrue(content.contains("public class testproject"));
    assertTrue(content.contains("Hello, testproject!"));
    assertTrue(content.contains("Test project description"));
  }

  @Test
  void shouldWriteGitignoreContent() throws IOException {
    // Given
    ProjectRequestDTO request = createTestRequest();

    // When
    projectStructureService.createStructure(tempDir, request);

    // Then
    Path gitignoreFile = tempDir.resolve(".gitignore");
    String content = Files.readString(gitignoreFile);

    assertTrue(content.contains("target/"));
    assertTrue(content.contains("pom.xml.tag"));
    assertTrue(content.contains("dependency-reduced-pom.xml"));
    assertTrue(content.contains("release.properties"));
    assertTrue(content.contains("pom.xml.releaseBackup"));
    assertTrue(content.contains("pom.xml.next"));
    assertTrue(content.contains("buildNumber.properties"));
    assertTrue(content.contains("pom.xml.versionsBackup"));
  }

  private ProjectRequestDTO createTestRequest() {
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
