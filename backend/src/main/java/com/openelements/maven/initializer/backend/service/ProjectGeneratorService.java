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

import com.openelements.maven.initializer.backend.domain.MavenDependency;
import com.openelements.maven.initializer.backend.domain.MavenPlugin;
import com.openelements.maven.initializer.backend.domain.ProjectGenerationResult;
import com.openelements.maven.initializer.backend.dto.ProjectRequestDTO;
import com.openelements.maven.initializer.backend.exception.ProjectServiceException;
import com.openelements.maven.initializer.backend.util.XmlFormatter;
import eu.maveniverse.domtrip.Element;
import eu.maveniverse.maven.toolbox.shared.ToolboxCommando;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.maveniverse.domtrip.maven.MavenPomElements;
import org.maveniverse.domtrip.maven.PomEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProjectGeneratorService {

  private static final Logger logger = LoggerFactory.getLogger(ProjectGeneratorService.class);
  private final ToolboxCommando toolboxCommando;
  private final ProjectStructureService structureService;

  private static final List<MavenDependency> DEFAULT_DEPENDENCIES =
      List.of(
          new MavenDependency("org.assertj", "assertj-core"),
          new MavenDependency("org.junit.jupiter", "junit-jupiter"));

  private List<MavenDependency> dependencyManagement;

  private List<MavenPlugin> plugins;

  public ProjectGeneratorService(
      ToolboxCommando toolboxCommando,
      ProjectStructureService structureService,
      ArtifactVersionService artifactVersionService) {
    this.toolboxCommando = toolboxCommando;
    this.structureService = structureService;

    fillDependencyManagement(artifactVersionService);
    fillPlugins(artifactVersionService);
  }

  private void fillPlugins(ArtifactVersionService artifactVersionService) {
    plugins =
        List.of(
            new MavenPlugin(
                "org.apache.maven.plugins", "maven-compiler-plugin", artifactVersionService),
            new MavenPlugin(
                "org.apache.maven.plugins", "maven-resources-plugin", artifactVersionService),
            new MavenPlugin(
                "org.apache.maven.plugins", "maven-surefire-plugin", artifactVersionService),
            new MavenPlugin("org.apache.maven.plugins", "maven-jar-plugin", artifactVersionService),
            new MavenPlugin(
                "org.apache.maven.plugins", "maven-install-plugin", artifactVersionService),
            new MavenPlugin(
                "org.apache.maven.plugins", "maven-deploy-plugin", artifactVersionService),
            new MavenPlugin("org.jacoco", "jacoco-maven-plugin", artifactVersionService));
  }

  private void fillDependencyManagement(ArtifactVersionService artifactVersionService) {
    dependencyManagement =
        List.of(
            new MavenDependency("org.junit", "junit-bom", artifactVersionService),
            new MavenDependency("org.assertj", "assertj-bom", artifactVersionService));
  }

  public ProjectGenerationResult generateProject(ProjectRequestDTO request) {
    logger.info("Starting project generation for: {}", request);

    if (request == null) {
      throw new IllegalArgumentException("ProjectRequestDTO cannot be null");
    }

    Path projectDir = createTempDirectory(request.getArtifactId());
    structureService.createStructure(projectDir, request);
    boolean hasResolvedVersion = generatePomFile(projectDir, request);

    logger.info("Project generated successfully at: {}", projectDir);
    return ProjectGenerationResult.create(hasResolvedVersion, projectDir.toString());
  }

  public byte[] createProjectZip(String projectPath) {
    if (projectPath == null) {
      throw new IllegalArgumentException("Project path cannot be null");
    }
    Path projectDir = Paths.get(projectPath);
    if (!Files.exists(projectDir)) {
      throw new ProjectServiceException("Project directory does not exist: " + projectPath, null);
    }

    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try (ZipOutputStream zos = new ZipOutputStream(baos)) {
        Files.walk(projectDir)
            .filter(Files::isRegularFile)
            .forEach(
                path -> {
                  try {
                    Path rel = projectDir.relativize(path);
                    zos.putNextEntry(new ZipEntry(rel.toString()));
                    Files.copy(path, zos);
                    zos.closeEntry();
                  } catch (IOException e) {
                    logger.error("Failed to add file to ZIP: {}", path, e);
                    throw new ProjectServiceException("Failed to add file to ZIP: " + path, e);
                  }
                });
      }

      logger.info("Created ZIP for project: {} ({} bytes)", projectPath, baos.size());
      return baos.toByteArray();
    } catch (IOException e) {
      throw new ProjectServiceException("Failed to create ZIP ", e);
    }
  }

  private String createEmptyPom(String groupId, String artifactId, String version) {
    PomEditor pomEditor = new PomEditor();
    pomEditor.createMavenDocument("project");
    pomEditor.insertMavenElement(
        pomEditor.root(),
        MavenPomElements.Elements.MODEL_VERSION,
        MavenPomElements.ModelVersions.MODEL_VERSION_4_0_0);
    pomEditor.insertMavenElement(pomEditor.root(), MavenPomElements.Elements.GROUP_ID, groupId);
    pomEditor.insertMavenElement(
        pomEditor.root(), MavenPomElements.Elements.ARTIFACT_ID, artifactId);
    pomEditor.insertMavenElement(pomEditor.root(), MavenPomElements.Elements.VERSION, version);
    return pomEditor.toXml();
  }

  /**
   * @param projectDir
   * @param request
   * @return true, if version resolving was successful - false, if a fallback version is used
   */
  private boolean generatePomFile(Path projectDir, ProjectRequestDTO request) {
    try {
      Path pomFile = projectDir.resolve("pom.xml");
      String pomContent =
          createEmptyPom(request.getGroupId(), request.getArtifactId(), request.getVersion());
      Files.writeString(pomFile, pomContent);

      try (ToolboxCommando.EditSession editSession = toolboxCommando.createEditSession(pomFile)) {
        toolboxCommando.editPom(
            editSession,
            Collections.singletonList(
                s -> {
                  s.setPackaging("jar");
                  s.updateProperty(true, "maven.compiler.release", request.getJavaVersion());
                  s.updateProperty(true, "project.build.sourceEncoding", "UTF-8");
                  s.editor()
                      .insertMavenElement(
                          s.editor().root(), "description", request.getDescription());
                  s.editor().insertMavenElement(s.editor().root(), "name", request.getName());

                  // Add default dependencies
                  addDefaultDependencies(s.editor());

                  // Add dependency management
                  addDefaultDependencyManagement(s.editor(), dependencyManagement);

                  plugins.forEach(
                      plugin -> s.updatePlugin(true, new DefaultArtifact(plugin.toString())));

                  // Add jacoco plugin configuration with executions
                  addJacocoPluginConfiguration(s.editor());
                }));
      }

      // Format the XML properly
      String formattedXml = XmlFormatter.formatXml(Files.readString(pomFile));
      Files.writeString(pomFile, formattedXml);
      return !formattedXml.contains("TODO");
    } catch (Exception e) {
      throw new ProjectServiceException("Failed to generate POM file: " + e.getMessage(), e);
    }
  }

  private void addDefaultDependencies(PomEditor editor) {
    var root = editor.root();
    var depsTmp = editor.findChildElement(root, MavenPomElements.Elements.DEPENDENCIES);

    if (depsTmp == null) {
      depsTmp = editor.insertMavenElement(root, MavenPomElements.Elements.DEPENDENCIES);
    }

    final var deps = depsTmp;
    DEFAULT_DEPENDENCIES.forEach(
        dependency -> {
          var depEl = editor.insertMavenElement(deps, MavenPomElements.Elements.DEPENDENCY);
          editor.insertMavenElement(
              depEl, MavenPomElements.Elements.GROUP_ID, dependency.groupId());
          editor.insertMavenElement(
              depEl, MavenPomElements.Elements.ARTIFACT_ID, dependency.artifactId());
          editor.insertMavenElement(depEl, MavenPomElements.Elements.SCOPE, "test");
        });
  }

  private void addDefaultDependencyManagement(
      PomEditor editor, List<MavenDependency> dependencyManagement) {
    var root = editor.root();
    var dm = editor.findChildElement(root, MavenPomElements.Elements.DEPENDENCY_MANAGEMENT);

    if (dm == null) {
      dm = editor.insertMavenElement(root, MavenPomElements.Elements.DEPENDENCY_MANAGEMENT);
    }

    var dmsTmp = editor.findChildElement(dm, MavenPomElements.Elements.DEPENDENCIES);
    if (dmsTmp == null) {
      dmsTmp = editor.insertMavenElement(dm, MavenPomElements.Elements.DEPENDENCIES);
    }

    final var dms = dmsTmp;
    dependencyManagement.forEach(
        bom -> {
          var depEl = editor.insertMavenElement(dms, MavenPomElements.Elements.DEPENDENCY);
          editor.insertMavenElement(depEl, MavenPomElements.Elements.GROUP_ID, bom.groupId());
          editor.insertMavenElement(depEl, MavenPomElements.Elements.ARTIFACT_ID, bom.artifactId());
          editor.insertMavenElement(depEl, MavenPomElements.Elements.VERSION, bom.version());
          editor.insertMavenElement(depEl, MavenPomElements.Elements.TYPE, "pom");
          editor.insertMavenElement(depEl, MavenPomElements.Elements.SCOPE, "import");
        });
  }

  private void addJacocoPluginConfiguration(PomEditor editor) {
    var root = editor.root();
    var build = editor.findChildElement(root, MavenPomElements.Elements.BUILD);
    if (build == null) {
      return;
    }

    var plugins = editor.findChildElement(build, MavenPomElements.Elements.PLUGINS);
    if (plugins == null) {
      return;
    }

    // Find the jacoco plugin
    var jacocoPlugin =
        plugins
            .children(MavenPomElements.Elements.PLUGIN)
            .filter(
                plugin ->
                    "org.jacoco"
                            .equals(
                                plugin
                                    .child(MavenPomElements.Elements.GROUP_ID)
                                    .map(Element::textContent)
                                    .orElse(null))
                        && "jacoco-maven-plugin"
                            .equals(
                                plugin
                                    .child(MavenPomElements.Elements.ARTIFACT_ID)
                                    .map(Element::textContent)
                                    .orElse(null)))
            .findFirst()
            .orElse(null);

    if (jacocoPlugin == null) {
      return;
    }

    // Add executions element if not present
    var executions = editor.findChildElement(jacocoPlugin, MavenPomElements.Elements.EXECUTIONS);
    if (executions == null) {
      executions = editor.insertMavenElement(jacocoPlugin, MavenPomElements.Elements.EXECUTIONS);
    }

    var execution = editor.insertMavenElement(executions, "execution");

    var goals = editor.insertMavenElement(execution, MavenPomElements.Elements.GOALS);

    var prepareAgentGoal = editor.insertMavenElement(goals, "goal");
    prepareAgentGoal.textContent("prepare-agent");

    var reportGoal = editor.insertMavenElement(goals, "goal");
    reportGoal.textContent("report");
  }

  private Path createTempDirectory(String artifactId) {
    try {
      Path projectDir = Files.createTempDirectory("project-" + artifactId + "-");
      logger.debug("Created temporary project directory at: {}", projectDir);
      return projectDir;
    } catch (IOException e) {
      throw new ProjectServiceException("Failed to create temporary project directory", e);
    }
  }
}
