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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ProjectZipperServiceTest {

  @TempDir Path tempDir;

  private ProjectZipperService projectZipperService;

  @BeforeEach
  void setUp() {
    projectZipperService = new ProjectZipperService();
  }

  @Test
  void shouldCreateValidZip() throws IOException {
    // Given
    Path projectDir = tempDir.resolve("test-project");
    Files.createDirectories(projectDir);

    // Create some test files
    Files.writeString(projectDir.resolve("pom.xml"), "<project></project>");
    Files.writeString(projectDir.resolve("README.md"), "# Test Project");
    Files.createDirectories(projectDir.resolve("src/main/java"));
    Files.writeString(projectDir.resolve("src/main/java/App.java"), "public class App {}");
    Files.writeString(projectDir.resolve("gitignore"), "target/\n*.gitignore");

    // When
    byte[] zipBytes = projectZipperService.createProjectZip(projectDir.toString());

    // Then
    assertNotNull(zipBytes);
    assertTrue(zipBytes.length > 0);

    // Verify zip contents
    try (ZipInputStream zis = new ZipInputStream(new java.io.ByteArrayInputStream(zipBytes))) {
      ZipEntry entry;
      boolean foundPom = false;
      boolean foundReadme = false;
      boolean foundJavaFile = false;
      boolean foundGitignore = false;

      while ((entry = zis.getNextEntry()) != null) {
        if (entry.getName().equals("pom.xml")) foundPom = true;
        if (entry.getName().equals("README.md")) foundReadme = true;
        if (entry.getName().equals("gitignore")) foundGitignore = true;
        if (entry.getName().equals("src/main/java/App.java")) foundJavaFile = true;
      }

      assertTrue(foundPom, "pom.xml should be in zip");
      assertTrue(foundReadme, "README.md should be in zip");
      assertTrue(foundJavaFile, "App.java should be in zip");
      assertTrue(foundGitignore, "gitignore should be in zip");
    }
  }

  @Test
  void shouldThrowWhenPathMissing() {
    // Given
    String nonExistentPath = "/non/existent/path";

    // When & Then
    IOException exception =
        assertThrows(
            IOException.class,
            () -> {
              projectZipperService.createProjectZip(nonExistentPath);
            });

    assertTrue(exception.getMessage().contains("Project directory does not exist"));
  }
}
