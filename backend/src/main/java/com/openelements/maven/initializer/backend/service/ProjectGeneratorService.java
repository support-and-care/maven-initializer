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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProjectGeneratorService {

  private static final Logger logger = LoggerFactory.getLogger(ProjectGeneratorService.class);
  private final ToolboxCommando toolboxCommando;
  private final ProjectStructureService structureService;

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

    try {
      Path projectDir = Files.createTempDirectory("project-" + request.getArtifactId() + "-");
      logger.debug("Created temporary project directory at: {}", projectDir);

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
                  s.updateProperty(true, "maven.compiler.release", request.getJavaVersion());
                  s.updateProperty(true, "project.build.sourceEncoding", "UTF-8");
                  s.editor()
                      .insertMavenElement(
                          s.editor().root(), "description", request.getDescription());
                  s.editor().insertMavenElement(s.editor().root(), "name", request.getName());
                  DEFAULT_PLUGINS.forEach(
                      plugin -> s.updatePlugin(true, new DefaultArtifact(plugin)));
                }));
      }

      // Format the XML properly
      String formattedXml = formatXml(Files.readString(pomFile));
      Files.writeString(pomFile, formattedXml);

      structureService.createStructure(projectDir, request);

      logger.info("Project generated successfully at: {}", projectDir);
      return projectDir.toString();

    } catch (Exception e) {
      logger.error("Project generation failed", e);
      throw new RuntimeException("Failed to generate project: " + e.getMessage(), e);
    }
  }

  public byte[] createProjectZip(String projectPath) {
    try {
      Path projectDir = Paths.get(projectPath);
      if (!Files.exists(projectDir)) {
        throw new IOException("Project directory does not exist: " + projectPath);
      }

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
                  }
                });
      }

      logger.info("Created ZIP for project: {} ({} bytes)", projectPath, baos.size());
      return baos.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException("Failed to create ZIP: " + e.getMessage(), e);
    }
  }

  public byte[] generateProjectZip(ProjectRequestDTO request) {
    String projectPath = generateProject(request);
    return createProjectZip(projectPath);
  }

  private String formatXml(String xml) throws Exception {
    // Create XSLT to strip whitespace and format properly
    String xslt =
        """
                <xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                    <xsl:strip-space elements="*"/>
                    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
                    <xsl:template match="@*|node()">
                        <xsl:copy>
                            <xsl:apply-templates select="@*|node()"/>
                        </xsl:copy>
                    </xsl:template>
                </xsl:stylesheet>
                """;

    TransformerFactory factory = TransformerFactory.newInstance();
    Transformer transformer = factory.newTransformer(new StreamSource(new StringReader(xslt)));
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

    StringWriter writer = new StringWriter();
    transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(writer));

    // Apply custom formatting rules
    String formatted = writer.toString();
    return applyCustomFormatting(formatted);
  }

  private String applyCustomFormatting(String xml) {
    // Ensure <project> tag is on its own line
    xml = xml.replaceFirst("(<project[^>]*>)", "\n$1");

    // Add empty lines after some specific elements
    xml = xml.replace("</modelVersion>", "</modelVersion>\n");
    xml = xml.replace("</description>", "</description>\n");
    xml = xml.replace("</properties>", "</properties>\n");

    return xml;
  }
}
