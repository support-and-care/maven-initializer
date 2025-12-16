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
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.FileOutput;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ResourceTemplateEngine {

  private static final Logger logger = LoggerFactory.getLogger(ResourceTemplateEngine.class);
  private final TemplateEngine templateEngine;

  public ResourceTemplateEngine() {
    this.templateEngine = TemplateEngine.createPrecompiled(ContentType.Plain);
    logger.debug("Initialized JTE with precompiled templates");
  }

  /**
   * Renders the README.md template to the specified file path. This is a low-level template
   * rendering method that handles the technical aspects of template compilation and rendering.
   *
   * @param data the project data to render in the template
   * @param filePath the target file path where the rendered content will be written
   * @throws IOException if file writing fails
   */
  public void createReadmeFile(ProjectRequestDTO data, Path filePath) throws IOException {
    try (FileOutput output = new FileOutput(filePath, Charset.forName("UTF-8"))) {
      templateEngine.render("README.md.jte", data, output);
    }
  }
}
