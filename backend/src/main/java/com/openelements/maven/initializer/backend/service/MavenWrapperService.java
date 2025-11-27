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

import com.openelements.maven.initializer.backend.exception.ProjectServiceException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Service responsible for adding Maven Wrapper (mvnw) files to a generated project. */
@Service
public class MavenWrapperService {

  private static final Logger logger = LoggerFactory.getLogger(MavenWrapperService.class);

  private static final String MAVEN_WRAPPER_VERSION = "3.3.4";
  private static final String MAVEN_VERSION = "3.9.11";

  private static final String MAVEN_WRAPPER_DISTRIBUTION_URL =
      "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper-distribution/"
          + MAVEN_WRAPPER_VERSION
          + "/maven-wrapper-distribution-"
          + MAVEN_WRAPPER_VERSION
          + "-only-script.zip";

  private static final String MAVEN_DISTRIBUTION_URL =
      "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/"
          + MAVEN_VERSION
          + "/apache-maven-"
          + MAVEN_VERSION
          + "-bin.zip";

  /**
   * Adds Maven Wrapper files to the specified project directory.
   *
   * <p>This includes:
   *
   * <ul>
   *   <li>Creating the {@code .mvn/wrapper} directory
   *   <li>Downloading and extracting {@code mvnw} and {@code mvnw.cmd} scripts
   *   <li>Generating {@code maven-wrapper.properties} with the correct distribution URL
   * </ul>
   *
   * <p>Uses the "only-script" distribution type, which avoids including {@code maven-wrapper.jar}
   * and downloads Maven directly on first use.
   *
   * @param projectRoot the root directory of the generated project
   * @throws ProjectServiceException if adding the Maven Wrapper fails due to I/O or network issues
   */
  public void addMavenWrapper(Path projectRoot) {
    logger.info("Adding Maven Wrapper to project at: {}", projectRoot);

    try {
      Path wrapperDir = projectRoot.resolve(".mvn/wrapper");
      Files.createDirectories(wrapperDir);

      downloadAndExtractWrapper(projectRoot);
      createWrapperProperties(wrapperDir);

      logger.info("Maven Wrapper added successfully");
    } catch (IOException e) {
      logger.error("Failed to add Maven Wrapper", e);
      throw new ProjectServiceException("Failed to add Maven Wrapper: " + e.getMessage(), e);
    }
  }

  /**
   * Downloads the Maven Wrapper "only-script" distribution and extracts only the necessary
   * executable scripts ({@code mvnw} and {@code mvnw.cmd}).
   *
   * <p>Debug variants ({@code mvnwDebug}, {@code mvnwDebug.cmd}) are intentionally excluded.
   *
   * @param projectRoot the target project root directory
   * @throws IOException if download or extraction fails
   */
  private void downloadAndExtractWrapper(Path projectRoot) throws IOException {
    logger.debug("Downloading Maven Wrapper distribution from: {}", MAVEN_WRAPPER_DISTRIBUTION_URL);

    Path tempZip = Files.createTempFile("maven-wrapper-", ".zip");

    try (InputStream in = URI.create(MAVEN_WRAPPER_DISTRIBUTION_URL).toURL().openStream()) {
      Files.copy(in, tempZip, StandardCopyOption.REPLACE_EXISTING);
    }

    try (FileSystem zipFileSystem =
        FileSystems.newFileSystem(URI.create("jar:" + tempZip.toUri()), Collections.emptyMap())) {
      Path zipRoot = zipFileSystem.getPath("/");

      // Extract Unix wrapper script
      Path mvnwSource = zipRoot.resolve("mvnw");
      if (Files.exists(mvnwSource)) {
        Path mvnwTarget = projectRoot.resolve("mvnw");
        Files.copy(mvnwSource, mvnwTarget, StandardCopyOption.REPLACE_EXISTING);
        setExecutablePermissions(mvnwTarget);
      }

      // Extract Windows wrapper script
      Path mvnwCmdSource = zipRoot.resolve("mvnw.cmd");
      if (Files.exists(mvnwCmdSource)) {
        Path mvnwCmdTarget = projectRoot.resolve("mvnw.cmd");
        Files.copy(mvnwCmdSource, mvnwCmdTarget, StandardCopyOption.REPLACE_EXISTING);
      }
    } finally {
      Files.deleteIfExists(tempZip);
    }
  }

  /**
   * Creates the {@code maven-wrapper.properties} file in the {@code .mvn/wrapper} directory.
   *
   * <p>Configures the wrapper to use the "only-script" distribution type and points to the official
   * Apache Maven binary distribution.
   *
   * @param wrapperDir the {@code .mvn/wrapper} directory
   * @throws IOException if writing the properties file fails
   */
  private void createWrapperProperties(Path wrapperDir) throws IOException {
    Path propertiesFile = wrapperDir.resolve("maven-wrapper.properties");
    String propertiesContent =
        "wrapperVersion="
            + MAVEN_WRAPPER_VERSION
            + "\n"
            + "distributionType=only-script\n"
            + "distributionUrl="
            + MAVEN_DISTRIBUTION_URL
            + "\n";

    Files.writeString(propertiesFile, propertiesContent);
    logger.debug("Created maven-wrapper.properties at: {}", propertiesFile);
  }

  /**
   * Sets executable permissions on a file.
   *
   * @param filePath the file to make executable
   * @throws IOException if setting permissions fails
   */
  private void setExecutablePermissions(Path filePath) throws IOException {
    try {
      Set<PosixFilePermission> perms = Files.getPosixFilePermissions(filePath);
      perms.add(PosixFilePermission.OWNER_EXECUTE);
      perms.add(PosixFilePermission.GROUP_EXECUTE);
      perms.add(PosixFilePermission.OTHERS_EXECUTE);
      Files.setPosixFilePermissions(filePath, perms);
    } catch (UnsupportedOperationException e) {
      boolean success = filePath.toFile().setExecutable(true, false);
      if (!success) {
        logger.warn("Failed to set executable permission for {}", filePath);
      }
    }
  }
}
