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
   * @throws Exception if formatting fails
   */
  public static String formatXml(String xml) throws Exception {
    if (xml == null || xml.trim().isEmpty()) {
      throw new ProjectServiceException("Input source cannot be empty", null);
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

    TransformerFactory factory = TransformerFactory.newInstance();
    Transformer transformer = factory.newTransformer(new StreamSource(new StringReader(xslt)));
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

    StringWriter writer = new StringWriter();
    try {
      transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(writer));
    } catch (Exception e) {
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

    // Add empty lines after some specific elements
    xml = xml.replace("</modelVersion>", "</modelVersion>\n");
    xml = xml.replace("</description>", "</description>\n");
    xml = xml.replace("</properties>", "</properties>\n");

    return xml;
  }
}
