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

import eu.maveniverse.maven.toolbox.shared.ArtifactVersionMatcher;
import eu.maveniverse.maven.toolbox.shared.ToolboxCommando;
import eu.maveniverse.maven.toolbox.shared.ToolboxResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.version.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ArtifactVersionService {

  private static final Logger logger = LoggerFactory.getLogger(ArtifactVersionService.class);
  private static final ArtifactVersionMatcher VERSION_MATCHER =
      ArtifactVersionMatcher.noSnapshotsAndPreviews();

  private final ToolboxResolver toolboxResolver;

  public ArtifactVersionService(ToolboxCommando toolboxCommando) {
    this.toolboxResolver = toolboxCommando.getToolboxResolver();
  }

  public String resolveLatestPluginVersion(String groupId, String artifactId) {
    return resolveLatestVersion(groupId, artifactId, "", "jar");
  }

  public String resolveLatestDependencyBomVersion(String groupId, String artifactId) {
    return resolveLatestVersion(groupId, artifactId, "", "pom");
  }

  public String resolveLatestDependencyVersion(String groupId, String artifactId) {
    return resolveLatestVersion(groupId, artifactId, "", "jar");
  }

  private String resolveLatestVersion(
      String groupId, String artifactId, String classifier, String extension) {
    String fallback = "TODO";
    try {
      Version newestVersion =
          toolboxResolver.findNewestVersion(
              new DefaultArtifact(groupId, artifactId, classifier, extension, "LATEST"),
              VERSION_MATCHER);
      if (newestVersion != null) {
        String versionAsString = newestVersion.toString();
        logger.debug("Resolved latest version {}:{} -> {}", groupId, artifactId, versionAsString);
        return versionAsString;
      }
    } catch (VersionRangeResolutionException ex) {
      logger.warn(
          "Failed to resolve latest version for {}:{} (using fallback {})",
          groupId,
          artifactId,
          fallback,
          ex);
    }
    return fallback;
  }
}
