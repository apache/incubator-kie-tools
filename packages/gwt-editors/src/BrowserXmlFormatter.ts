/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { XmlFormatter } from "./XmlFormatter";

export class BrowserXmlFormatter implements XmlFormatter {
  private static XSLT_DOC: Document;

  private xsltDoc() {
    if (BrowserXmlFormatter.XSLT_DOC) {
      return BrowserXmlFormatter.XSLT_DOC;
    }

    return new DOMParser().parseFromString(
      [
        '<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="3.0">',
        '  <xsl:strip-space elements="*"/>',
        '  <xsl:template match="para[content-style][not(text())]">',
        '    <xsl:value-of select="normalize-space(.)"/>',
        "  </xsl:template>",
        '  <xsl:template match="node()|@*">',
        '    <xsl:copy><xsl:apply-templates select="node()|@*"/></xsl:copy>',
        "  </xsl:template>",
        '  <xsl:output indent="yes"/>',
        "</xsl:stylesheet>"
      ].join("\n"),
      "application/xml"
    );
  }

  public format(xml: string) {
    const xmlDoc = new DOMParser().parseFromString(xml, "application/xml");
    const xsltProcessor = new XSLTProcessor();
    xsltProcessor.importStylesheet(this.xsltDoc());
    const resultDoc = xsltProcessor.transformToDocument(xmlDoc);
    return new XMLSerializer().serializeToString(resultDoc);
  }
}
