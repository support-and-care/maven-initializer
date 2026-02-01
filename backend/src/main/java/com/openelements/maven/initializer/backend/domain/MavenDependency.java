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
package com.openelements.maven.initializer.backend.domain;

import com.openelements.maven.initializer.backend.service.ArtifactVersionService;
import java.util.List;
import java.util.Objects;

/** Represents a Maven dependency with groupId, artifactId, and version. */
public final class MavenDependency {
  private final String groupId;
  private final String artifactId;
  private final DependencyType dependencyType;
  private final ArtifactVersionService artifactVersionService;

  public MavenDependency(String groupId, String artifactId) {
    this(groupId, artifactId, DependencyType.NORMAL, null);
  }

  public MavenDependency(
      String groupId, String artifactId, ArtifactVersionService artifactVersionService) {
    this(groupId, artifactId, DependencyType.BOM, artifactVersionService);
  }

  public MavenDependency(
      String groupId,
      String artifactId,
      DependencyType dependencyType,
      ArtifactVersionService artifactVersionService) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.dependencyType = dependencyType;
    this.artifactVersionService = artifactVersionService;
  }

  public String groupId() {
    return groupId;
  }

  public String artifactId() {
    return artifactId;
  }

  public DependencyType dependencyType() {
    return dependencyType;
  }

  public String version() {
    if (artifactVersionService != null) {
      if (dependencyType == DependencyType.BOM) {
        return artifactVersionService.resolveLatestDependencyBomVersion(groupId, artifactId);
      } else {
        return artifactVersionService.resolveLatestDependencyVersion(groupId, artifactId);
      }
    }
    return "";
  }

  /**
   * Determines if this dependency should include a version element in the POM. Versions are only
   * included for normal dependencies that are not managed by any BOM.
   *
   * @param dependencyManagement the list of BOMs in dependency management
   * @return true if a version should be included, false otherwise
   */
  public boolean shouldIncludeVersion(List<MavenDependency> dependencyManagement) {
    if (dependencyType != DependencyType.NORMAL) {
      return false; // BOMs don't need versions in dependencies section
    }
    return !isManagedByBom(dependencyManagement);
  }

  /**
   * Returns the version string to include in the POM, or null if no version should be included.
   * This method encapsulates all the logic for determining whether and what version to include: -
   * Only normal dependencies that are not managed by BOMs should include versions - The version
   * must be non-null, non-empty, and not "TODO"
   *
   * @param dependencyManagement the list of BOMs in dependency management
   * @return the version string to include, or null if no version should be included
   */
  public String getVersionToInclude(List<MavenDependency> dependencyManagement) {
    if (!shouldIncludeVersion(dependencyManagement)) {
      return null;
    }
    String version = version();
    if (version != null && !version.isEmpty() && !version.equals("TODO")) {
      return version;
    }
    return null;
  }

  /**
   * Checks if this dependency is managed by any BOM in the dependency management list.
   *
   * @param dependencyManagement the list of BOMs in dependency management
   * @return true if the dependency is managed by a BOM, false otherwise
   */
  private boolean isManagedByBom(List<MavenDependency> dependencyManagement) {
    return dependencyManagement.stream()
        .filter(bom -> bom.dependencyType() == DependencyType.BOM)
        .anyMatch(
            bom -> {
              String bomGroupId = bom.groupId();

              // Special case: junit-bom (org.junit) manages org.junit.jupiter dependencies
              if ("org.junit".equals(bomGroupId) && groupId.startsWith("org.junit")) {
                return true;
              }

              return bomGroupId.equals(groupId);
            });
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (MavenDependency) obj;
    return Objects.equals(this.groupId, that.groupId)
        && Objects.equals(this.artifactId, that.artifactId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupId, artifactId);
  }

  @Override
  public String toString() {
    return "MavenDependency["
        + "groupId="
        + groupId
        + ", "
        + "artifactId="
        + artifactId
        + ", "
        + "version="
        + version()
        + ']';
  }
}
