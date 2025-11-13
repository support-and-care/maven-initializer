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
import java.util.Objects;
import org.jspecify.annotations.NonNull;

/** Represents a Maven plugin with groupId, artifactId, and version. */
public final class MavenPlugin {
  private final String groupId;
  private final String artifactId;
  private final ArtifactVersionService artifactVersionService;

  /** */
  public MavenPlugin(
      String groupId, String artifactId, ArtifactVersionService artifactVersionService) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.artifactVersionService = artifactVersionService;
  }

  @Override
  @NonNull
  public String toString() {
    return groupId + ":" + artifactId + ":" + version();
  }

  public String groupId() {
    return groupId;
  }

  public String artifactId() {
    return artifactId;
  }

  public String version() {
    return artifactVersionService.resolveLatestPluginVersion(groupId, artifactId);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (MavenPlugin) obj;
    return Objects.equals(this.groupId, that.groupId)
        && Objects.equals(this.artifactId, that.artifactId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupId, artifactId);
  }
}
