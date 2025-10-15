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
import eu.maveniverse.maven.toolbox.shared.ToolboxCommando;
import eu.maveniverse.maven.toolbox.shared.internal.PomSuppliers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProjectGeneratorService {

  private static final Logger logger = LoggerFactory.getLogger(ProjectGeneratorService.class);
  private final ToolboxCommando toolboxCommando;
  private final ProjectZipperService zipperService;
  private final ProjectStructureService structureService;

  private static final List<String> DEFAULT_DEPENDENCIES = List.of("commons-io:commons-io:2.15.1");

  private static final List<String> DEFAULT_PLUGINS =
      List.of(
          "org.apache.maven.plugins:maven-compiler-plugin:3.14.0",
          "org.apache.maven.plugins:maven-resources-plugin:3.3.1",
          "org.apache.maven.plugins:maven-surefire-plugin:3.5.4",
          "org.apache.maven.plugins:maven-jar-plugin:3.4.2",
          "org.apache.maven.plugins:maven-install-plugin:3.1.4",
          "org.apache.maven.plugins:maven-deploy-plugin:3.1.4");

  public ProjectGeneratorService(
      ToolboxCommando toolboxCommando,
      ProjectZipperService zipperService,
      ProjectStructureService structureService) {
    this.toolboxCommando = toolboxCommando;
    this.zipperService = zipperService;
    this.structureService = structureService;
  }

  public String generateProject(ProjectRequestDTO request) {
    logger.info("Starting project generation for: {}", request);

    try {
      Path projectDir = Paths.get("target", request.getArtifactId());
      Files.createDirectories(projectDir);

      Path pomFile = projectDir.resolve("pom.xml");
      String pomContent =
          PomSuppliers.empty400(
              request.getGroupId(), request.getArtifactId(), request.getVersion());
      Files.writeString(pomFile, pomContent);

      try (ToolboxCommando.EditSession editSession = toolboxCommando.createEditSession(pomFile)) {
        toolboxCommando.editPom(
            editSession,
            Collections.singletonList(
                s -> {
                  s.setPackaging("jar");
                  DEFAULT_DEPENDENCIES.forEach(
                      dep -> s.updateDependency(true, new DefaultArtifact(dep)));
                  DEFAULT_PLUGINS.forEach(
                      plugin -> s.updatePlugin(true, new DefaultArtifact(plugin)));
                }));
      }

      // Create project structure (directories, main class, .gitignore)
      structureService.createStructure(projectDir, request);

      logger.info("✅ Project generated successfully at: {}", projectDir);
      return projectDir.toString();

    } catch (Exception e) {
      logger.error("❌ Project generation failed", e);
      throw new RuntimeException("Failed to generate project: " + e.getMessage(), e);
    }
  }

  public byte[] generateProjectZip(ProjectRequestDTO request) {
    String projectPath = generateProject(request);
    try {
      return zipperService.createProjectZip(projectPath);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create ZIP: " + e.getMessage(), e);
    }
  }
}
