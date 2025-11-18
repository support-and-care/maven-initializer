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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MavenWrapperService {

  private static final Logger logger = LoggerFactory.getLogger(MavenWrapperService.class);
  private static final String MAVEN_WRAPPER_PLUGIN_VERSION = "3.3.4";
  private static final String MAVEN_WRAPPER_PLUGIN_GOAL =
      "org.apache.maven.plugins:maven-wrapper-plugin:" + MAVEN_WRAPPER_PLUGIN_VERSION + ":wrapper";

  /**
   * Adds Maven Wrapper to the project by executing the Maven Wrapper Plugin via ProcessBuilder.
   * This automatically generates all wrapper files (mvnw, mvnw.cmd, .mvn/wrapper/*).
   *
   * @param projectRoot the root directory of the generated project
   * @throws ProjectServiceException if wrapper generation fails
   */
  public void addMavenWrapper(Path projectRoot) {
    logger.info("Adding Maven Wrapper to project at: {}", projectRoot);

    try {
      // Execute Maven Wrapper Plugin
      ProcessBuilder builder = new ProcessBuilder("mvn", "-N", MAVEN_WRAPPER_PLUGIN_GOAL);
      builder.directory(projectRoot.toFile());
      builder.redirectErrorStream(true);

      Process process = builder.start();

      // Read output for logging
      StringBuilder output = new StringBuilder();
      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          output.append(line).append("\n");
          logger.debug("Maven Wrapper Plugin: {}", line);
        }
      }

      int exitCode = process.waitFor();

      if (exitCode != 0) {
        logger.error(
            "Maven Wrapper Plugin failed with exit code: {}\nOutput: {}", exitCode, output);
        throw new ProjectServiceException(
            "Failed to generate Maven Wrapper. Exit code: " + exitCode + "\nOutput: " + output,
            null);
      }

      logger.info("Maven Wrapper added successfully");
    } catch (IOException e) {
      logger.error("IO error while executing Maven Wrapper Plugin", e);
      throw new ProjectServiceException(
          "Failed to execute Maven Wrapper Plugin: " + e.getMessage(), e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      logger.error("Interrupted while executing Maven Wrapper Plugin", e);
      throw new ProjectServiceException("Maven Wrapper Plugin execution was interrupted", e);
    }
  }
}
