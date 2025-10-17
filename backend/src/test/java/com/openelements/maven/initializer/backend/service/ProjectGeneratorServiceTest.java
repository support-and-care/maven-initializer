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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openelements.maven.initializer.backend.dto.ProjectRequestDTO;
import eu.maveniverse.maven.toolbox.shared.ToolboxCommando;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectGeneratorServiceTest {

  @Mock private ToolboxCommando toolboxCommando;
  @Mock private ProjectZipperService zipperService;
  @Mock private ProjectStructureService structureService;
  @Mock private ToolboxCommando.EditSession editSession;

  private ProjectGeneratorService projectGeneratorService;

  @BeforeEach
  void setUp() {
    projectGeneratorService =
        new ProjectGeneratorService(toolboxCommando, zipperService, structureService);
  }

  @Test
  void shouldGenerateProject() throws Exception {
    // Given
    ProjectRequestDTO request = createTestRequest();
    when(toolboxCommando.createEditSession(any())).thenReturn(editSession);

    // When
    String result = projectGeneratorService.generateProject(request);

    // Then
    assertNotNull(result);
    assertTrue(result.contains(request.getArtifactId()));
    verify(toolboxCommando).createEditSession(any());
    verify(toolboxCommando).editPom(eq(editSession), any());
    verify(structureService).createStructure(any(), eq(request));
  }

  @Test
  void shouldGenerateZip() throws Exception {
    // Given
    ProjectRequestDTO request = createTestRequest();
    byte[] expectedZip = "test-zip-content".getBytes();
    when(toolboxCommando.createEditSession(any())).thenReturn(editSession);
    when(zipperService.createProjectZip(any())).thenReturn(expectedZip);

    // When
    byte[] result = projectGeneratorService.generateProjectZip(request);

    // Then
    assertNotNull(result);
    assertArrayEquals(expectedZip, result);
    verify(zipperService).createProjectZip(any());
  }

  @Test
  void shouldFailWhenToolboxErrors() throws Exception {
    // Given
    ProjectRequestDTO request = createTestRequest();
    when(toolboxCommando.createEditSession(any())).thenThrow(new RuntimeException("Toolbox error"));

    // When & Then
    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              projectGeneratorService.generateProject(request);
            });

    assertTrue(exception.getMessage().contains("Failed to generate project"));
  }

  private ProjectRequestDTO createTestRequest() {
    ProjectRequestDTO request = new ProjectRequestDTO();
    request.setGroupId("com.example");
    request.setArtifactId("test-project");
    request.setVersion("1.0.0-SNAPSHOT");
    request.setName("Test Project");
    request.setDescription("Test project description");
    request.setJavaVersion("17");
    return request;
  }
}
