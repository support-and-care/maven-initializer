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

import com.openelements.maven.initializer.backend.dto.ProjectRequestDTO;
import com.openelements.maven.initializer.backend.exception.ProjectServiceException;
import com.openelements.maven.initializer.backend.util.XmlFormatter;
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

  private static final List<String> DEFAULT_DEPENDENCIES =
      List.of("org.assertj:assertj-core", "org.junit.jupiter:junit-jupiter");

  private static final List<String> DEFAULT_DEPENDENCY_MANAGEMENT =
      List.of("org.junit:junit-bom:6.0.0", "org.assertj:assertj-bom:3.27.5");

  private static final List<String> DEFAULT_PLUGINS =
      List.of(
          "org.apache.maven.plugins:maven-compiler-plugin:3.14.0",
          "org.apache.maven.plugins:maven-resources-plugin:3.3.1",
          "org.apache.maven.plugins:maven-surefire-plugin:3.5.4",
          "org.apache.maven.plugins:maven-jar-plugin:3.4.2",
          "org.apache.maven.plugins:maven-install-plugin:3.1.4",
          "org.apache.maven.plugins:maven-deploy-plugin:3.1.4");

  public ProjectGeneratorService(
      ToolboxCommando toolboxCommando, ProjectStructureService structureService) {
    this.toolboxCommando = toolboxCommando;
    this.structureService = structureService;
  }

  public String generateProject(ProjectRequestDTO request) {
    logger.info("Starting project generation for: {}", request);

    if (request == null) {
      throw new IllegalArgumentException("ProjectRequestDTO cannot be null");
    }

    Path projectDir = createTempDirectory(request.getArtifactId());
    generatePomFile(projectDir, request);
    structureService.createStructure(projectDir, request);

    logger.info("Project generated successfully at: {}", projectDir);
    return projectDir.toString();
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

  private void generatePomFile(Path projectDir, ProjectRequestDTO request) {
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

                  // Add test-scoped dependencies without explicit versions
                  {
                    var editor = s.editor();
                    var root = editor.root();
                    var depsTmp =
                        editor.findChildElement(root, MavenPomElements.Elements.DEPENDENCIES);
                    if (depsTmp == null) {
                      depsTmp =
                          editor.insertMavenElement(root, MavenPomElements.Elements.DEPENDENCIES);
                    }
                    final var deps = depsTmp;
                    DEFAULT_DEPENDENCIES.forEach(
                        ga -> {
                          String[] parts = ga.split(":", 2);
                          if (parts.length == 2) {
                            var depEl =
                                editor.insertMavenElement(
                                    deps, MavenPomElements.Elements.DEPENDENCY);
                            editor.insertMavenElement(
                                depEl, MavenPomElements.Elements.GROUP_ID, parts[0]);
                            editor.insertMavenElement(
                                depEl, MavenPomElements.Elements.ARTIFACT_ID, parts[1]);
                            editor.insertMavenElement(
                                depEl, MavenPomElements.Elements.SCOPE, "test");
                          }
                        });
                  }

                  // Add dependencyManagement
                  {
                    var editor = s.editor();
                    var root = editor.root();
                    var dm =
                        editor.findChildElement(
                            root, MavenPomElements.Elements.DEPENDENCY_MANAGEMENT);
                    if (dm == null) {
                      dm =
                          editor.insertMavenElement(
                              root, MavenPomElements.Elements.DEPENDENCY_MANAGEMENT);
                    }
                    var dmsTmp =
                        editor.findChildElement(dm, MavenPomElements.Elements.DEPENDENCIES);
                    if (dmsTmp == null) {
                      dmsTmp =
                          editor.insertMavenElement(dm, MavenPomElements.Elements.DEPENDENCIES);
                    }
                    final var dms = dmsTmp;
                    DEFAULT_DEPENDENCY_MANAGEMENT.forEach(
                        bom -> {
                          String[] parts = bom.split(":", 3);
                          if (parts.length == 3) {
                            var depEl =
                                editor.insertMavenElement(
                                    dms, MavenPomElements.Elements.DEPENDENCY);
                            editor.insertMavenElement(
                                depEl, MavenPomElements.Elements.GROUP_ID, parts[0]);
                            editor.insertMavenElement(
                                depEl, MavenPomElements.Elements.ARTIFACT_ID, parts[1]);
                            editor.insertMavenElement(
                                depEl, MavenPomElements.Elements.VERSION, parts[2]);
                            editor.insertMavenElement(depEl, MavenPomElements.Elements.TYPE, "pom");
                            editor.insertMavenElement(
                                depEl, MavenPomElements.Elements.SCOPE, "import");
                          }
                        });
                  }

                  DEFAULT_PLUGINS.forEach(
                      plugin -> s.updatePlugin(true, new DefaultArtifact(plugin)));
                }));
      }

      // Format the XML properly
      String formattedXml = XmlFormatter.formatXml(Files.readString(pomFile));
      Files.writeString(pomFile, formattedXml);
    } catch (Exception e) {
      throw new ProjectServiceException("Failed to generate POM file: " + e.getMessage(), e);
    }
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
