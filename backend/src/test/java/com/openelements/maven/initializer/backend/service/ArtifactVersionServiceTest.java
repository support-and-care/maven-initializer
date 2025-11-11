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

import static org.junit.jupiter.api.Assertions.assertEquals;

import eu.maveniverse.maven.toolbox.shared.ToolboxCommando;
import eu.maveniverse.maven.toolbox.shared.ToolboxResolver;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.version.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArtifactVersionServiceTest {

  @Mock private ToolboxCommando toolboxCommando;
  @Mock private ToolboxResolver toolboxResolver;
  @Mock private Version version;

  private ArtifactVersionService artifactVersionService;

  @BeforeEach
  void setUp() {
    Mockito.when(toolboxCommando.getToolboxResolver()).thenReturn(toolboxResolver);
    artifactVersionService = new ArtifactVersionService(toolboxCommando);
  }

  @Test
  void resolvesLatestJarVersionWhenAvailable() throws Exception {
    Mockito.when(toolboxResolver.findNewestVersion(Mockito.any(), Mockito.any()))
        .thenReturn(version);
    Mockito.when(version.toString()).thenReturn("1.2.3");

    String resolved = artifactVersionService.resolveLatestJarVersion("g", "a", "0.0.1");

    assertEquals("1.2.3", resolved);

    ArgumentCaptor<org.eclipse.aether.artifact.Artifact> artifactCaptor =
        ArgumentCaptor.forClass(org.eclipse.aether.artifact.Artifact.class);
    Mockito.verify(toolboxResolver).findNewestVersion(artifactCaptor.capture(), Mockito.any());
    assertEquals("g", artifactCaptor.getValue().getGroupId());
    assertEquals("a", artifactCaptor.getValue().getArtifactId());
  }

  @Test
  void fallsBackToDefaultVersionOnFailure() throws Exception {
    Mockito.when(toolboxResolver.findNewestVersion(Mockito.any(), Mockito.any()))
        .thenThrow(new VersionRangeResolutionException(null, "resolution failed"));

    String resolved = artifactVersionService.resolveLatestPomVersion("g", "a", "5.6.7");

    assertEquals("5.6.7", resolved);
  }
}
