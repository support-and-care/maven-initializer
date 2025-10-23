package com.openelements.maven.initializer.backend.util;

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
    // Create XSLT to strip whitespace and format properly
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
    transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(writer));

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
