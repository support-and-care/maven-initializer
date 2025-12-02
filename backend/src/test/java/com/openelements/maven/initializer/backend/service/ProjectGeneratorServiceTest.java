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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.openelements.maven.initializer.backend.config.MavenToolboxConfig;
import com.openelements.maven.initializer.backend.domain.ProjectGenerationResult;
import com.openelements.maven.initializer.backend.dto.ProjectRequestDTO;
import com.openelements.maven.initializer.backend.exception.ProjectServiceException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectGeneratorServiceTest {

  private ProjectGeneratorService projectGeneratorServiceUnderTest;
  @Mock private ProjectStructureService projectStructureServiceMock;
  @Mock private ArtifactVersionService artifactVersionService;
  @Mock private MavenWrapperService mavenWrapperService;

  private ProjectGeneratorService configureProjectGeneratorService() {
    MavenToolboxConfig mavenToolboxConfig = new MavenToolboxConfig();
    var toolbox = mavenToolboxConfig.toolboxCommando(mavenToolboxConfig.mavenContext());
    return new ProjectGeneratorService(
        toolbox, projectStructureServiceMock, artifactVersionService, mavenWrapperService);
  }

  @Test
  void testProjectGenerationWithoutIssues() {
    // Given
    projectGeneratorServiceUnderTest = configureProjectGeneratorService();
    final ProjectRequestDTO validRequest = createValidRequest();

    // When
    final ProjectGenerationResult result =
        projectGeneratorServiceUnderTest.generateProject(validRequest);

    // Then
    assertNotNull(result);
    assertTrue(result.projectPath().contains(validRequest.getArtifactId()));
    assertTrue(Files.exists(Paths.get(result.projectPath())));
    assertEquals(ProjectGenerationResult.Status.NO_ISSUES, result.status());
  }

  @Test
  void testProjectGenerationUsingFallbackVersion() {
    // Given
    Mockito.when(
            artifactVersionService.resolveLatestDependencyBomVersion(
                Mockito.anyString(), Mockito.anyString()))
        .thenReturn("TODO");
    Mockito.when(
            artifactVersionService.resolveLatestPluginVersion(
                Mockito.anyString(), Mockito.anyString()))
        .thenReturn("TODO");
    projectGeneratorServiceUnderTest = configureProjectGeneratorService();

    final ProjectRequestDTO validRequest = createValidRequest();

    // When
    final ProjectGenerationResult result =
        projectGeneratorServiceUnderTest.generateProject(validRequest);

    // Then
    assertNotNull(result);
    assertTrue(result.projectPath().contains(validRequest.getArtifactId()));
    assertTrue(Files.exists(Paths.get(result.projectPath())));
    assertEquals(ProjectGenerationResult.Status.FALLBACK_VERSION, result.status());
  }

  @Test
  void testProjectGenerationFailing() {
    // Given
    projectGeneratorServiceUnderTest = configureProjectGeneratorService();

    // When & Then
    assertThrows(
        IllegalArgumentException.class,
        () -> projectGeneratorServiceUnderTest.generateProject(null),
        "Expected exception for null ProjectRequestDTO");
  }

  @Test
  void testProjectZipCreation() {
    // Given
    projectGeneratorServiceUnderTest = configureProjectGeneratorService();
    String validProjectPath = new File("src/test/resources/validTestProject").getAbsolutePath();

    // When
    final byte[] zipBytes = projectGeneratorServiceUnderTest.createProjectZip(validProjectPath);

    // Then
    assertNotNull(zipBytes);
    assertTrue(zipBytes.length > 0);
  }

  @Test
  void testProjectZipCreationFailing() {
    // Given
    final String invalidProjectPath = "/non/existent/path";
    projectGeneratorServiceUnderTest = configureProjectGeneratorService();

    // When & Then
    assertThrows(
        IllegalArgumentException.class,
        () -> projectGeneratorServiceUnderTest.createProjectZip(null));
    assertThrows(
        ProjectServiceException.class,
        () -> projectGeneratorServiceUnderTest.createProjectZip(invalidProjectPath));
  }

  @Test
  void testPomFileContainsExpectedElements() throws IOException {

    // Given
    projectGeneratorServiceUnderTest = configureProjectGeneratorService();
    ProjectRequestDTO validRequest = createValidRequest();

    // When
    String projectPath =
        projectGeneratorServiceUnderTest.generateProject(validRequest).projectPath();

    Path pomFile = Path.of(projectPath, "pom.xml");
    assertTrue(Files.exists(pomFile));

    String pomContent = Files.readString(pomFile);

    // Then
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
    projectGeneratorServiceUnderTest = configureProjectGeneratorService();

    // When
    String projectPath =
        projectGeneratorServiceUnderTest.generateProject(validRequest).projectPath();
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
            "maven-deploy-plugin",
            "jacoco-maven-plugin");

    List<String> missingPlugins =
        defaultPlugins.stream().filter(plugin -> !pomContent.contains(plugin)).toList();

    assertTrue(missingPlugins.isEmpty(), "Missing plugins in pom.xml: " + missingPlugins);
  }

  @Test
  void testPomContainsDependenciesAndDependencyManagement() throws Exception {
    // Given
    Mockito.when(
            artifactVersionService.resolveLatestDependencyBomVersion(
                Mockito.anyString(), Mockito.anyString()))
        .thenReturn("TODO");
    Mockito.when(
            artifactVersionService.resolveLatestPluginVersion(
                Mockito.anyString(), Mockito.anyString()))
        .thenReturn("TODO");
    projectGeneratorServiceUnderTest = configureProjectGeneratorService();
    ProjectRequestDTO validRequest = createValidRequest();

    // When
    String projectPath =
        projectGeneratorServiceUnderTest.generateProject(validRequest).projectPath();
    Path pomFile = Path.of(projectPath, "pom.xml");

    // Then
    assertTrue(Files.exists(pomFile), "POM file should exist");
    String pomContent = Files.readString(pomFile);

    // Dependency Management BOM imports
    assertTrue(pomContent.contains("<groupId>org.junit</groupId>"));
    assertTrue(pomContent.contains("<artifactId>junit-bom</artifactId>"));
    assertTrue(pomContent.contains("<version>TODO</version>"));
    assertTrue(pomContent.contains("<type>pom</type>"));
    assertTrue(pomContent.contains("<scope>import</scope>"));
    assertTrue(pomContent.contains("<groupId>org.assertj</groupId>"));
    assertTrue(pomContent.contains("<artifactId>assertj-bom</artifactId>"));
    assertTrue(pomContent.contains("<artifactId>assertj-core</artifactId>"));
    assertTrue(pomContent.contains("<groupId>org.junit.jupiter</groupId>"));
    assertTrue(pomContent.contains("<artifactId>junit-jupiter</artifactId>"));
    assertTrue(pomContent.contains("<scope>test</scope>"));
  }

  @Test
  void testPomContainsJacocoPluginConfiguration() throws Exception {
    // Given
    ProjectRequestDTO validRequest = createValidRequest();
    projectGeneratorServiceUnderTest = configureProjectGeneratorService();

    // When
    String projectPath =
        projectGeneratorServiceUnderTest.generateProject(validRequest).projectPath();
    Path pomFile = Path.of(projectPath, "pom.xml");

    // Then
    assertTrue(Files.exists(pomFile), "POM file should exist");
    String pomContent = Files.readString(pomFile);

    // Verify jacoco plugin is present
    assertTrue(
        pomContent.contains("<groupId>org.jacoco</groupId>"),
        "POM should contain jacoco plugin groupId");
    assertTrue(
        pomContent.contains("<artifactId>jacoco-maven-plugin</artifactId>"),
        "POM should contain jacoco-maven-plugin artifactId");

    // Verify executions configuration
    assertTrue(
        pomContent.contains("<executions>"),
        "POM should contain executions element for jacoco plugin");
    assertTrue(
        pomContent.contains("<execution>"),
        "POM should contain execution element for jacoco plugin");

    // Verify goals
    assertTrue(
        pomContent.contains("<goal>prepare-agent</goal>"),
        "POM should contain prepare-agent goal for jacoco plugin");
    assertTrue(
        pomContent.contains("<goal>report</goal>"),
        "POM should contain report goal for jacoco plugin");
  }

  @Test
  void testResolvedPluginVersionIsApplied() throws Exception {
    // Given
    Mockito.when(
            artifactVersionService.resolveLatestPluginVersion(
                Mockito.anyString(), Mockito.anyString()))
        .thenReturn("TODO");
    Mockito.when(
            artifactVersionService.resolveLatestPluginVersion("org.jacoco", "jacoco-maven-plugin"))
        .thenReturn("9.9.9");
    projectGeneratorServiceUnderTest = configureProjectGeneratorService();

    ProjectRequestDTO validRequest = createValidRequest();

    // When
    String projectPath =
        projectGeneratorServiceUnderTest.generateProject(validRequest).projectPath();
    Path pomFile = Path.of(projectPath, "pom.xml");

    String pomContent = Files.readString(pomFile);

    // Then
    assertTrue(
        pomContent.contains("<artifactId>jacoco-maven-plugin</artifactId>"),
        "Jacoco plugin should be present");
    assertTrue(
        pomContent.contains("<version>9.9.9</version>"),
        "Jacoco plugin version should match resolved value");
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
