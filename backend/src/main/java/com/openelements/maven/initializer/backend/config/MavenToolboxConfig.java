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
package com.openelements.maven.initializer.backend.config;

import eu.maveniverse.maven.mima.context.Context;
import eu.maveniverse.maven.mima.context.ContextOverrides;
import eu.maveniverse.maven.mima.context.Runtimes;
import eu.maveniverse.maven.toolbox.shared.ToolboxCommando;
import eu.maveniverse.maven.toolbox.shared.output.LoggerOutput;
import eu.maveniverse.maven.toolbox.shared.output.Output;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MavenToolboxConfig {

  @Bean
  public ToolboxCommando toolboxCommando(Context context) {
    return ToolboxCommando.create(
        new LoggerOutput(
            LoggerFactory.getLogger(MavenToolboxConfig.class), true, Output.Verbosity.NORMAL),
        context);
  }

  @Bean
  public Context mavenContext() {
    ContextOverrides contextOverrides = ContextOverrides.create().withUserSettings(true).build();
    return Runtimes.INSTANCE.getRuntime().create(contextOverrides);
  }
}
