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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FileUtilsService {

  private static final Logger logger = LoggerFactory.getLogger(FileUtilsService.class);

  public void recreateDirectory(Path dir) throws IOException {
    deleteDirectory(dir);
    Files.createDirectories(dir);
  }

  public void deleteDirectory(Path dir) throws IOException {
    if (!Files.exists(dir)) return;
    Files.walk(dir)
        .sorted((a, b) -> b.compareTo(a))
        .forEach(
            path -> {
              try {
                Files.delete(path);
              } catch (IOException e) {
                logger.warn("Failed to delete {}: {}", path, e.getMessage());
              }
            });
  }

  public Path locateGeneratedProject(Path tempDir, String artifactId) throws IOException {
    Optional<Path> found =
        Files.walk(tempDir, 2)
            .filter(Files::isDirectory)
            .filter(p -> p.getFileName().toString().contains(artifactId))
            .findFirst();

    if (found.isPresent()) return found.get();

    Path pom = tempDir.resolve("pom.xml");
    if (Files.exists(pom)) return tempDir;

    throw new IOException("No generated project found in: " + tempDir);
  }

  public void moveDirectory(Path source, Path target) throws IOException {
    if (Files.exists(target)) deleteDirectory(target);
    Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
    logger.info("Moved project to: {}", target);
  }
}
