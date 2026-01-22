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
import eu.maveniverse.domtrip.maven.Coordinates;
import eu.maveniverse.domtrip.maven.MavenPomElements;
import eu.maveniverse.domtrip.maven.PomEditor;
import eu.maveniverse.maven.toolbox.shared.ToolboxCommando;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProjectGeneratorService {

  private static final Logger logger = LoggerFactory.getLogger(ProjectGeneratorService.class);
  private final ToolboxCommando toolboxCommando;
  private final ProjectStructureService structureService;
  private final MavenWrapperService mavenWrapperService;
  private final ArtifactVersionService artifactVersionService;

  private List<MavenDependency> dependencyManagement;

  public ProjectGeneratorService(
      ToolboxCommando toolboxCommando,
      ProjectStructureService structureService,
      ArtifactVersionService artifactVersionService,
      MavenWrapperService mavenWrapperService) {
    this.toolboxCommando = toolboxCommando;
    this.structureService = structureService;
    this.mavenWrapperService = mavenWrapperService;
    this.artifactVersionService = artifactVersionService;
  }

  private List<MavenPlugin> fillPlugins(ProjectRequestDTO request) {
    List<MavenPlugin> pluginList =
        new ArrayList<>(
            List.of(
                new MavenPlugin(
                    "org.apache.maven.plugins", "maven-clean-plugin", artifactVersionService),
                new MavenPlugin(
                    "org.apache.maven.plugins", "maven-compiler-plugin", artifactVersionService),
                new MavenPlugin(
                    "org.apache.maven.plugins", "maven-resources-plugin", artifactVersionService),
                new MavenPlugin(
                    "org.apache.maven.plugins", "maven-surefire-plugin", artifactVersionService),
                new MavenPlugin(
                    "org.apache.maven.plugins", "maven-jar-plugin", artifactVersionService),
                new MavenPlugin(
                    "org.apache.maven.plugins", "maven-install-plugin", artifactVersionService),
                new MavenPlugin(
                    "org.apache.maven.plugins", "maven-deploy-plugin", artifactVersionService),
                new MavenPlugin("org.jacoco", "jacoco-maven-plugin", artifactVersionService)));

    // Add code formatting plugins if selected
    if (request.isIncludeSpotless()) {
      pluginList.add(
          new MavenPlugin(
              "com.diffplug.spotless", "spotless-maven-plugin", artifactVersionService));
    }
    if (request.isIncludeCheckstyle()) {
      pluginList.add(
          new MavenPlugin(
              "org.apache.maven.plugins", "maven-checkstyle-plugin", artifactVersionService));
    }

    return pluginList;
  }

  private void fillDependencyManagement(
      ArtifactVersionService artifactVersionService, ProjectRequestDTO request) {
    List<MavenDependency> deps = new ArrayList<>();
    deps.add(new MavenDependency("org.junit", "junit-bom", artifactVersionService));

    String assertionLib = request.getAssertionLibrary();
    if ("assertj".equals(assertionLib)) {
      deps.add(new MavenDependency("org.assertj", "assertj-bom", artifactVersionService));
    }

    dependencyManagement = deps;
  }

  public ProjectGenerationResult generateProject(ProjectRequestDTO request) {
    logger.info("Starting project generation for: {}", request);

    if (request == null) {
      throw new IllegalArgumentException("ProjectRequestDTO cannot be null");
    }

    Path projectDir = createTempDirectory(request.getArtifactId());
    structureService.createStructure(projectDir, request);
    boolean hasResolvedVersion = generatePomFile(projectDir, request);

    // Add Maven Wrapper if requested
    if (request.isIncludeMavenWrapper()) {
      mavenWrapperService.addMavenWrapper(projectDir);
    }

    // Generate README.md
    structureService.createReadmeFile(projectDir, request);

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

    return createZipArchive(projectDir, projectPath);
  }

  private byte[] createZipArchive(Path projectDir, String projectPath) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos)) {
        Files.walk(projectDir)
            .filter(Files::isRegularFile)
            .forEach(
                path -> {
                  try {
                    Path rel = projectDir.relativize(path);
                    ZipArchiveEntry entry = new ZipArchiveEntry(rel.toString());

                    setZipEntryPermissions(entry, path);

                    zos.putArchiveEntry(entry);
                    Files.copy(path, zos);
                    zos.closeArchiveEntry();
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

      List<MavenPlugin> plugins = fillPlugins(request);

      try (ToolboxCommando.EditSession editSession = toolboxCommando.createEditSession(pomFile)) {
        toolboxCommando.editPom(
            editSession,
            Collections.singletonList(
                s -> {
                  s.setPackaging("jar");
                  s.properties()
                      .updateProperty(true, "maven.compiler.release", request.getJavaVersion());
                  s.properties().updateProperty(true, "project.build.sourceEncoding", "UTF-8");
                  s.insertMavenElement(s.root(), "description", request.getDescription());
                  s.insertMavenElement(s.root(), "name", request.getName());

                  // Add dependency management
                  fillDependencyManagement(artifactVersionService, request);
                  addDefaultDependencyManagement(s);

                  // Add default dependencies
                  addDefaultDependencies(s, request);

                  plugins.forEach(plugin -> s.plugins().updatePlugin(true, toCoordinates(plugin)));

                  // Add jacoco plugin configuration with executions
                  addJacocoPluginConfiguration(s);

                  if (request.isIncludeSpotless()) {
                    addSpotlessPluginConfiguration(s);
                  }
                  if (request.isIncludeCheckstyle()) {
                    addCheckstylePluginConfiguration(s);
                  }
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

  private Coordinates toCoordinates(MavenPlugin plugin) {
    return Coordinates.of(
        plugin.groupId(), plugin.artifactId(), plugin.version(), "", "maven-plugin");
  }

  private void addDefaultDependencies(PomEditor editor, ProjectRequestDTO request) {
    var root = editor.root();
    var depsTmp = editor.findChildElement(root, MavenPomElements.Elements.DEPENDENCIES);

    if (depsTmp == null) {
      depsTmp = editor.insertMavenElement(root, MavenPomElements.Elements.DEPENDENCIES);
    }

    final var deps = depsTmp;

    List<MavenDependency> DEFAULT_DEPENDENCIES = new ArrayList<>();

    // Default deps
    DEFAULT_DEPENDENCIES.add(new MavenDependency("org.junit.jupiter", "junit-jupiter"));

    // Add assertion library based on selection
    String assertionLib = request.getAssertionLibrary();
    if ("assertj".equals(assertionLib)) {
      DEFAULT_DEPENDENCIES.add(new MavenDependency("org.assertj", "assertj-core"));
    } else if ("hamcrest".equals(assertionLib)) {
      DEFAULT_DEPENDENCIES.add(new MavenDependency("org.hamcrest", "hamcrest"));
    }
    // If "none", only JUnit is added (no assertion library)

    DEFAULT_DEPENDENCIES.forEach(
        dependency -> {
          var depEl = editor.insertMavenElement(deps, MavenPomElements.Elements.DEPENDENCY);
          editor.insertMavenElement(
              depEl, MavenPomElements.Elements.GROUP_ID, dependency.groupId());
          editor.insertMavenElement(
              depEl, MavenPomElements.Elements.ARTIFACT_ID, dependency.artifactId());
          editor.insertMavenElement(depEl, MavenPomElements.Elements.SCOPE, "test");

          // Hamcrest needs explicit version (not managed by BOM)
          if ("org.hamcrest".equals(dependency.groupId())
              && "hamcrest".equals(dependency.artifactId())) {
            String hamcrestVersion =
                artifactVersionService.resolveLatestDependencyVersion(
                    dependency.groupId(), dependency.artifactId());
            editor.insertMavenElement(depEl, MavenPomElements.Elements.VERSION, hamcrestVersion);
          }
        });
  }

  private void addDefaultDependencyManagement(PomEditor editor) {
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

  private void configurePlugin(
      PomEditor editor,
      String groupId,
      String artifactId,
      List<String> goals,
      boolean addEmptyConfiguration) {

    var root = editor.root();
    var build = editor.findChildElement(root, MavenPomElements.Elements.BUILD);
    if (build == null) return;

    var plugins = editor.findChildElement(build, MavenPomElements.Elements.PLUGINS);
    if (plugins == null) return;

    var plugin =
        plugins
            .children(MavenPomElements.Elements.PLUGIN)
            .filter(
                p ->
                    groupId.equals(
                            p.child(MavenPomElements.Elements.GROUP_ID)
                                .map(Element::textContent)
                                .orElse(null))
                        && artifactId.equals(
                            p.child(MavenPomElements.Elements.ARTIFACT_ID)
                                .map(Element::textContent)
                                .orElse(null)))
            .findFirst()
            .orElse(null);

    if (plugin == null) return;

    var executions = editor.findChildElement(plugin, MavenPomElements.Elements.EXECUTIONS);
    if (executions == null) {
      executions = editor.insertMavenElement(plugin, MavenPomElements.Elements.EXECUTIONS);
    }

    var execution = editor.insertMavenElement(executions, "execution");
    var goalsEl = editor.insertMavenElement(execution, MavenPomElements.Elements.GOALS);
    goals.forEach(
        goal -> {
          var goalEl = editor.insertMavenElement(goalsEl, "goal");
          goalEl.textContent(goal);
        });

    if (addEmptyConfiguration) {
      if (editor.findChildElement(plugin, MavenPomElements.Elements.CONFIGURATION) == null) {
        Element configurationElement =
            editor.insertMavenElement(plugin, MavenPomElements.Elements.CONFIGURATION);
        editor.addComment(configurationElement, "TODO: Please add a configuration");
      }
    }
  }

  private void addJacocoPluginConfiguration(PomEditor editor) {
    configurePlugin(
        editor, "org.jacoco", "jacoco-maven-plugin", List.of("prepare-agent", "report"), false);
  }

  private void addSpotlessPluginConfiguration(PomEditor editor) {
    configurePlugin(
        editor, "com.diffplug.spotless", "spotless-maven-plugin", List.of("check"), true);
  }

  private void addCheckstylePluginConfiguration(PomEditor editor) {
    configurePlugin(
        editor, "org.apache.maven.plugins", "maven-checkstyle-plugin", List.of("check"), true);
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

  /**
   * Sets file permissions in ZIP entry to preserve executable permissions.
   *
   * @param entry the ZIP archive entry to set permissions for
   * @param filePath the file path to check permissions from
   */
  private void setZipEntryPermissions(ZipArchiveEntry entry, Path filePath) {
    try {
      int mode = Files.isExecutable(filePath) ? 0755 : 0644;
      entry.setUnixMode(mode);
    } catch (Exception e) {
      entry.setUnixMode(0644);
      logger.debug("Using default permissions for {}", filePath);
    }
  }
}
