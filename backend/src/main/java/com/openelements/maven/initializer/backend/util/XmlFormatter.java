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

import com.openelements.maven.initializer.backend.exception.ProjectServiceException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/** Utility class for formatting XML content with custom formatting rules. */
public class XmlFormatter {

  /**
   * Formats XML content with proper indentation and custom formatting rules.
   *
   * @param xml the XML content to format
   * @return the formatted XML content
   */
  public static String formatXml(String xml) {
    if (xml == null || xml.trim().isEmpty()) {
      throw new IllegalArgumentException("Input source cannot be empty");
    }

    String xslt =
        """
                <xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                    <xsl:strip-space elements="*"/>
                    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
                    <xsl:template match="@*|node()">
                        <xsl:copy>
                            <xsl:apply-templates select="@*|node()"/>
                        </xsl:copy>
                    </xsl:template>
                </xsl:stylesheet>
                """;

    Transformer transformer;
    try {
      TransformerFactory factory = TransformerFactory.newInstance();
      transformer = factory.newTransformer(new StreamSource(new StringReader(xslt)));
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    } catch (TransformerConfigurationException e) {
      throw new ProjectServiceException("Failed to configure XML transformer", e);
    }

    StringWriter writer = new StringWriter();
    try {
      transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(writer));
    } catch (TransformerException e) {
      throw new ProjectServiceException("Invalid XML content", e);
    }

    // Apply custom formatting rules
    String formatted = writer.toString();
    return applyCustomFormatting(formatted);
  }

  /**
   * Applies custom formatting rules to the XML content.
   *
   * @param xml the XML content to apply custom formatting to
   * @return the XML content with custom formatting applied
   */
  private static String applyCustomFormatting(String xml) {
    // Ensure <project> tag is on its own line
    xml = xml.replaceFirst("(<project[^>]*>)", "\n$1");

    // Add empty lines after specific elements for readability
    xml = xml.replace("</modelVersion>", "</modelVersion>\n");
    xml = xml.replace("</description>", "</description>\n");
    xml = xml.replace("</properties>", "</properties>\n");
    xml = xml.replace("</dependencies>", "</dependencies>\n");

    // Remove extra blank line directly BEFORE </dependencyManagement>
    xml = xml.replaceAll("\\n\\s*\\n(\\s*</dependencyManagement>)", "\n$1");

    // Ensure exactly one blank line AFTER </dependencyManagement>
    xml = xml.replaceAll("(</dependencyManagement>)(\\s*\\n)+", "$1\n\n");

    return xml;
  }

  /**
   * Adds a TODO comment to the configuration element of the specified plugin.
   *
   * @param xml the XML content
   * @param pluginArtifactId the artifact ID of the plugin
   * @return the XML content with the TODO comment added
   */
  public static String addFormatingConfigurationComment(String xml, String pluginArtifactId) {
    // Pattern to match empty configuration element for the plugin
    // This regex finds <configuration></configuration> or <configuration/> within the plugin block
    // It handles both self-closing and separate opening/closing tags
    String pattern =
        "(<plugin>[\\s\\S]*?<artifactId>"
            + pluginArtifactId
            + "</artifactId>[\\s\\S]*?<configuration>)\\s*(</configuration>)";
    String replacement =
        "$1\n                    <!-- TODO please define your configuration -->\n                $2";
    String result = xml.replaceAll(pattern, replacement);

    // Also handle self-closing configuration tags
    pattern =
        "(<plugin>[\\s\\S]*?<artifactId>"
            + pluginArtifactId
            + "</artifactId>[\\s\\S]*?<configuration)\\s*/>";
    replacement =
        "$1>\n                    <!-- TODO please define your configuration -->\n                </configuration>";
    result = result.replaceAll(pattern, replacement);

    return result;
  }
}
