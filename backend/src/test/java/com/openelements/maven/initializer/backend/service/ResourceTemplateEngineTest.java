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

import static org.assertj.core.api.Assertions.assertThat;

import com.openelements.maven.initializer.backend.dto.ProjectRequestDTO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ResourceTemplateEngineTest {

  @Test
  void testWithoutMvnWrapper() throws IOException {
    ResourceTemplateEngine engine = new ResourceTemplateEngine();
    ProjectRequestDTO data =
        new ProjectRequestDTO(
            "groupid", "artifactId", "Version", "description", "25", "my-project");
    data.setIncludeMavenWrapper(false);
    Path outputFilePath = Path.of("target/jteTest/withoutMvnWrapper.md");
    engine.createReadmeFile(data, outputFilePath);

    assertThat(outputFilePath.toFile().exists()).isTrue();
    assertThat(outputFilePath.toFile())
        .hasSameTextualContentAs(new File("src/test/resources/jte/withoutMvnWrapper.md"));
  }

  @Test
  void testWithSpotless(@TempDir Path tempDir) throws IOException {
    ResourceTemplateEngine engine = new ResourceTemplateEngine();
    ProjectRequestDTO data =
        new ProjectRequestDTO(
            "groupid", "artifactId", "Version", "description", "25", "my-project");
    data.setIncludeSpotless(true);
    Path outputFilePath = tempDir.resolve("withSpotless.md");
    engine.createReadmeFile(data, outputFilePath);

    assertThat(outputFilePath.toFile().exists()).isTrue();
    String content = Files.readString(outputFilePath);
    assertThat(content).contains("Spotless Maven Plugin");
    assertThat(content).contains("Code Formatting Plugins");
  }

  @Test
  void testWithCheckstyle() throws IOException {
    ResourceTemplateEngine engine = new ResourceTemplateEngine();
    ProjectRequestDTO data =
        new ProjectRequestDTO(
            "groupid", "artifactId", "Version", "description", "25", "my-project");
    data.setIncludeCheckstyle(true);
    Path outputFilePath = Path.of("target/jteTest/withCheckstyle.md");
    engine.createReadmeFile(data, outputFilePath);

    assertThat(outputFilePath.toFile().exists()).isTrue();
    assertThat(outputFilePath.toFile())
        .hasSameTextualContentAs(new File("src/test/resources/jte/withCheckstyle.md"));
  }

  @Test
  void testWithSpotless() throws IOException {
    ResourceTemplateEngine engine = new ResourceTemplateEngine();
    ProjectRequestDTO data =
        new ProjectRequestDTO(
            "groupid", "artifactId", "Version", "description", "25", "my-project");
    data.setIncludeSpotless(true);
    Path outputFilePath = Path.of("target/jteTest/withSpotless.md");
    engine.createReadmeFile(data, outputFilePath);

    assertThat(outputFilePath.toFile().exists()).isTrue();
    assertThat(outputFilePath.toFile())
        .hasSameTextualContentAs(new File("src/test/resources/jte/withSpotless.md"));
  }

  @Test
  void testincludeMvnWrapper() throws IOException {
    ResourceTemplateEngine engine = new ResourceTemplateEngine();
    ProjectRequestDTO data =
        new ProjectRequestDTO(
            "groupid", "artifactId", "Version", "description", "25", "my-project");
    data.setIncludeMavenWrapper(true);
    Path outputFilePath = Path.of("target/jteTest/includeMvnWrapper.md");
    engine.createReadmeFile(data, outputFilePath);

    assertThat(outputFilePath.toFile().exists()).isTrue();
    assertThat(outputFilePath.toFile())
        .hasSameTextualContentAs(new File("src/test/resources/jte/includedMvnWrapper.md"));
  }

  @Test
  void testIncludeEverything() throws IOException {
    ResourceTemplateEngine engine = new ResourceTemplateEngine();
    ProjectRequestDTO data =
        new ProjectRequestDTO(
            "groupid", "artifactId", "Version", "description", "25", "my-project");
    data.setIncludeMavenWrapper(true);
    data.setIncludeSpotless(true);
    data.setIncludeCheckstyle(true);
    Path outputFilePath = Path.of("target/jteTest/generalReadme.md");
    engine.createReadmeFile(data, outputFilePath);

    assertThat(outputFilePath.toFile().exists()).isTrue();
    assertThat(outputFilePath.toFile())
        .hasSameTextualContentAs(new File("src/test/resources/jte/generalReadme.md"));
  }
}
