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
package com.openelements.maven.initializer.backend.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.openelements.maven.initializer.backend.exception.ProjectServiceException;
import org.junit.jupiter.api.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

public class XmlFormatterTest {

  @Test
  void testFormatXmlValidPom() throws Exception {
    String inputXml =
        """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0"><modelVersion>4.0.0</modelVersion><groupId>dev.parsick.maven.samples</groupId>
            <artifactId>simple-single-module-project</artifactId><version>1.0.0-SNAPSHOT</version><packaging>jar</packaging>
            <name>test-project</name><description>This is my test project</description><properties>
            <maven.compiler.release>21</maven.compiler.release><project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
            </properties><build><plugins><plugin><groupId>org.apache.maven.plugins</groupId><artifactId>maven-compiler-plugin</artifactId>
            <version>3.14.0</version></plugin><plugin><groupId>org.apache.maven.plugins</groupId><artifactId>maven-resources-plugin</artifactId>
            <version>3.3.1</version></plugin><plugin><groupId>org.apache.maven.plugins</groupId><artifactId>maven-surefire-plugin</artifactId>
            <version>3.5.4</version></plugin><plugin><groupId>org.apache.maven.plugins</groupId><artifactId>maven-jar-plugin</artifactId>
            <version>3.4.2</version></plugin><plugin><groupId>org.apache.maven.plugins</groupId><artifactId>maven-install-plugin</artifactId>
            <version>3.1.4</version></plugin><plugin><groupId>org.apache.maven.plugins</groupId><artifactId>maven-deploy-plugin</artifactId>
            <version>3.1.4</version></plugin></plugins></build></project>
            """;

    String expectedXml =
        """
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0">
                <modelVersion>4.0.0</modelVersion>

                <groupId>dev.parsick.maven.samples</groupId>
                <artifactId>simple-single-module-project</artifactId>
                <version>1.0.0-SNAPSHOT</version>
                <packaging>jar</packaging>
                <name>test-project</name>
                <description>This is my test project</description>

                <properties>
                    <maven.compiler.release>21</maven.compiler.release>
                    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
                </properties>

                <build>
                    <plugins>
                        <plugin>
                            <groupId>org.apache.maven.plugins</groupId>
                            <artifactId>maven-compiler-plugin</artifactId>
                            <version>3.14.0</version>
                        </plugin>
                        <plugin>
                            <groupId>org.apache.maven.plugins</groupId>
                            <artifactId>maven-resources-plugin</artifactId>
                            <version>3.3.1</version>
                        </plugin>
                        <plugin>
                            <groupId>org.apache.maven.plugins</groupId>
                            <artifactId>maven-surefire-plugin</artifactId>
                            <version>3.5.4</version>
                        </plugin>
                        <plugin>
                            <groupId>org.apache.maven.plugins</groupId>
                            <artifactId>maven-jar-plugin</artifactId>
                            <version>3.4.2</version>
                        </plugin>
                        <plugin>
                            <groupId>org.apache.maven.plugins</groupId>
                            <artifactId>maven-install-plugin</artifactId>
                            <version>3.1.4</version>
                        </plugin>
                        <plugin>
                            <groupId>org.apache.maven.plugins</groupId>
                            <artifactId>maven-deploy-plugin</artifactId>
                            <version>3.1.4</version>
                        </plugin>
                    </plugins>
                </build>
            </project>
            """;

    String formattedXml = XmlFormatter.formatXml(inputXml);

    Diff diff =
        DiffBuilder.compare(expectedXml)
            .withTest(formattedXml)
            .ignoreWhitespace()
            .normalizeWhitespace()
            .checkForSimilar()
            .build();

    assertFalse(diff.hasDifferences(), "Formatted XML does not match expected XML: " + diff);
  }

  @Test
  void testFormatXmlEmptyInput() {
    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> XmlFormatter.formatXml(""));
    assertTrue(exception.getMessage().contains("Input source cannot be empty"));
  }

  @Test
  void testFormatXmlInvalidXml() {
    String invalidXml =
        "<project><modelVersion>4.0.0</modelVersion><groupId>test</groupId></project";
    Exception exception =
        assertThrows(ProjectServiceException.class, () -> XmlFormatter.formatXml(invalidXml));
    assertTrue(exception.getMessage().contains("Invalid XML content"));
  }
}
