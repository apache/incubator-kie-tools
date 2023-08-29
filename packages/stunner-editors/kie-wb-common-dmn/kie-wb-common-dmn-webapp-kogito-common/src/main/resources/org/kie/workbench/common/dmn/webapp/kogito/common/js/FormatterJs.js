/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
FormatterJs = {
  cachedXsltProcessor: null,

  format: function (xmlDocument) {
    if (this.cachedXsltProcessor === null) {
      this.cachedXsltProcessor = this.newXsltProcessor();
    }
    var resultDocument = this.cachedXsltProcessor.transformToDocument(xmlDocument);
    return new XMLSerializer().serializeToString(resultDocument);
  },

  newXsltProcessor: function newXsltProcessor() {
    var xsltDoc = new DOMParser().parseFromString(
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
        "</xsl:stylesheet>",
      ].join("\n"),
      "application/xml"
    );

    var xsltProcessor = new XSLTProcessor();
    xsltProcessor.importStylesheet(xsltDoc);
    return xsltProcessor;
  },
};
