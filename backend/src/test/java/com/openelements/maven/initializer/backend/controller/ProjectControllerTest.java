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
package com.openelements.maven.initializer.backend.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.openelements.maven.initializer.backend.dto.ProjectRequestDTO;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ProjectControllerTest {

  @Autowired private ProjectController projectController;
  private ProjectRequestDTO validRequest;

  @BeforeEach
  void setUp() {
    validRequest = createValidRequest();
  }

  @Test
  void testProjectGenerationSuccess() {
    // When
    ResponseEntity<byte[]> response = projectController.generateProject(validRequest);

    // Then
    assertAll(
        () -> assertNotNull(response, "Response should not be null"),
        () -> assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK"),
        () -> assertNotNull(response.getBody(), "Response body should not be null"),
        () -> {
          assertNotNull(response.getBody());
          assertTrue(response.getBody().length > 0, "Response body should contain ZIP data");
        },
        () ->
            assertTrue(
                response.getHeaders().getFirst("Content-Disposition") != null,
                "Should contain Content-Disposition header"),
        () ->
            assertTrue(
                Objects.requireNonNull(response.getHeaders().getFirst("Content-Disposition"))
                    .contains("testproject.zip"),
                "Filename should match artifact ID"));
  }

  @Test
  void testProjectGenerationFailure() {
    // Given
    ProjectRequestDTO invalidRequest = new ProjectRequestDTO();

    // Then
    assertThrows(
        RuntimeException.class,
        () -> projectController.generateProject(invalidRequest),
        "Expected exception for invalid request");
  }

  @ParameterizedTest
  @ValueSource(strings = {"my-project", "test-app", "sample-service", "demo-backend"})
  void testProjectGenerationWithDifferentArtifactIds(String artifactId) {
    // Given
    ProjectRequestDTO request = createValidRequest();
    request.setArtifactId(artifactId);

    // When
    ResponseEntity<byte[]> response = projectController.generateProject(request);

    // Then
    assertAll(
        () -> assertNotNull(response, "Response should not be null"),
        () -> assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK"),
        () -> assertNotNull(response.getBody(), "Response body should not be null"),
        () -> {
          assertNotNull(response.getBody());
          assertTrue(response.getBody().length > 0, "Response body should contain ZIP data");
        },
        () ->
            assertTrue(
                Objects.requireNonNull(response.getHeaders().getFirst("Content-Disposition"))
                    .contains(artifactId + ".zip"),
                "Filename should match artifact ID: " + artifactId));
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
