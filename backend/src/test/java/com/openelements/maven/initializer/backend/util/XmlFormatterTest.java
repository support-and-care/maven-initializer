package com.openelements.maven.initializer.backend.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Test class for XmlFormatter utility. */
class XmlFormatterTest {

  @Test
  void testFormatXmlWithValidAndComplexXml() throws Exception {
    String simpleXml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><project><modelVersion>4.0.0</modelVersion><groupId>com.example</groupId><artifactId>test</artifactId><version>1.0.0</version></project>";

    String complexXml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><project><modelVersion>4.0.0</modelVersion><groupId>com.example</groupId><artifactId>test</artifactId><version>1.0.0</version><properties><maven.compiler.source>17</maven.compiler.source><maven.compiler.target>17</maven.compiler.target></properties><build><plugins><plugin><groupId>org.apache.maven.plugins</groupId><artifactId>maven-compiler-plugin</artifactId><version>3.11.0</version></plugin></plugins></build></project>";

    String formattedSimple = XmlFormatter.formatXml(simpleXml);
    String formattedComplex = XmlFormatter.formatXml(complexXml);

    assertNotNull(formattedSimple);
    assertTrue(formattedSimple.contains("<project>"));
    assertTrue(formattedComplex.contains("<build>"));
    assertTrue(formattedComplex.contains("<plugin>"));
  }

  @Test
  void testCustomFormattingRulesApplied() throws Exception {
    String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><project><modelVersion>4.0.0</modelVersion><description>Test project</description><properties><maven.compiler.source>17</maven.compiler.source></properties></project>";

    String formatted = XmlFormatter.formatXml(xml);

    assertNotNull(formatted);
    assertTrue(formatted.contains("\n<project>"), "Project tag should start on its own line");
    assertTrue(formatted.contains("</modelVersion>\n"));
    assertTrue(formatted.contains("</description>\n"));
    assertTrue(formatted.contains("</properties>\n"));
  }

  @Test
  void testInvalidOrEmptyXmlThrowsException() {
    String[] invalidInputs = {
      null, "", "   \n\t  ", "<project><modelVersion>4.0.0</modelVersion><unclosed-tag></project>"
    };

    for (String input : invalidInputs) {
      assertThrows(Exception.class, () -> XmlFormatter.formatXml(input));
    }
  }

  @Test
  void testFormatXmlPreservesContent() throws Exception {
    String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><project><modelVersion>4.0.0</modelVersion><groupId>com.example</groupId><artifactId>my-project</artifactId><version>1.0.0-SNAPSHOT</version><name>My Project</name><description>This is a test project</description></project>";

    String formatted = XmlFormatter.formatXml(xml);

    assertTrue(formatted.contains("my-project"));
    assertTrue(formatted.contains("1.0.0-SNAPSHOT"));
    assertTrue(formatted.contains("My Project"));
    assertTrue(formatted.contains("This is a test project"));
  }

  @Test
  void testFormatXmlPreservesSpecialCharacters() throws Exception {
    String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><project><modelVersion>4.0.0</modelVersion><description>Test &amp; Development Project</description></project>";

    String formatted = XmlFormatter.formatXml(xml);

    assertTrue(formatted.contains("Test &amp; Development Project"));
  }
}
