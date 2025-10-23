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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProjectStructureService {

  private static final Logger logger = LoggerFactory.getLogger(ProjectStructureService.class);

  public void createStructure(Path projectRoot, ProjectRequestDTO request) throws IOException {
    logger.info("Creating project structure for: {}", request.getArtifactId());

    createDirectories(projectRoot, request.getGroupId(), request.getArtifactId());
    createGitignoreFile(projectRoot);
    createMainClass(projectRoot, request);

    logger.info("✅ Project structure created successfully");
  }

  private void createDirectories(Path root, String groupId, String artifactId) throws IOException {
    String packagePath = groupId.replace(".", "/");
    Path javaDir = root.resolve("src/main/java/" + packagePath + "/" + artifactId.toLowerCase());

    Files.createDirectories(javaDir);

    logger.debug("Created directory structure for package: {}", packagePath);
  }

  private void createGitignoreFile(Path root) throws IOException {
    String content =
        """
        target/
        pom.xml.tag
        pom.xml.releaseBackup
        pom.xml.versionsBackup
        pom.xml.next
        release.properties
        dependency-reduced-pom.xml
        buildNumber.properties
        """;
    Files.writeString(root.resolve(".gitignore"), content);
    logger.debug("Created .gitignore file");
  }

  private void createMainClass(Path root, ProjectRequestDTO request) throws IOException {
    String pkg = request.getGroupId();
    String artifactId = request.getArtifactId();
    String className = convertToJavaClassName(artifactId);
    String packagePath = pkg.replace(".", "/");
    Path javaDir = root.resolve("src/main/java/" + packagePath + "/" + artifactId.toLowerCase());
    Path mainClassFile = javaDir.resolve(className + ".java");

    String content =
        String.format(
            """
        package %s;

        /**
         * %s
         */
        public class %s {
            public static void main(String[] args) {
                System.out.println("Hello, %s!");
            }
        }
        """,
            pkg,
            request.getDescription() != null ? request.getDescription() : "Generated Maven Project",
            className,
            className);

    Files.writeString(mainClassFile, content);
    logger.debug("Created main class: {}", className);
  }

  private String convertToJavaClassName(String artifactId) {
    String[] parts = artifactId.split("-");
    StringBuilder className = new StringBuilder();

    for (String part : parts) {
      if (!part.isEmpty()) {
        // Capitalize first letter of each part
        className.append(Character.toUpperCase(part.charAt(0)));
        if (part.length() > 1) {
          className.append(part.substring(1));
        }
      }
    }

    // Ensure the class name starts with a letter
    if (className.isEmpty() || !Character.isLetter(className.charAt(0))) {
      className.insert(0, "Project");
    }

    return className.toString();
  }
}
