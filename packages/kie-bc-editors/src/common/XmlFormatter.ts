/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { TextFormatter } from "./TextFormatter";

let cachedXsltProcessor: XSLTProcessor;

function newXsltProcessor() {
  const xsltDoc = new DOMParser().parseFromString(
    [
      '<?xml version="1.0" encoding="UTF-8"?>' +
        '<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:drools="http://www.jboss.org/drools" xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" version="3.0">',
      '  <xsl:strip-space elements="*"/>',
      '  <xsl:template match="para[content-style][not(text())]">',
      '    <xsl:value-of select="normalize-space(.)"/>',
      "  </xsl:template>",
      '  <xsl:template match="node()|@*">',
      '    <xsl:copy><xsl:apply-templates select="node()|@*"/></xsl:copy>',
      "  </xsl:template>",
      // indent="yes" prettifies output
      // cdata-section-elements="list of nodes with cdata separated by space"
      '  <xsl:output indent="yes" version="1.0" method="xml" encoding="UTF-8" omit-xml-declaration="no" cdata-section-elements="bpmn2:completionCondition bpmn2:condition bpmn2:conditionExpression bpmn2:from bpmn2:to bpmn2:documentation drools:metaValue drools:script"/>',
      "</xsl:stylesheet>",
    ].join("\n"),
    "application/xml"
  );

  const xsltProcessor = new XSLTProcessor();
  xsltProcessor.importStylesheet(xsltDoc);
  return xsltProcessor;
}

export class XmlFormatter implements TextFormatter {
  public format(text: string) {
    cachedXsltProcessor = cachedXsltProcessor ?? newXsltProcessor();

    const xmlDoc = new DOMParser().parseFromString(text, "application/xml");
    const resultDoc = cachedXsltProcessor.transformToDocument(xmlDoc);
    let output = new XMLSerializer().serializeToString(resultDoc);

    // XSLTProcessor browser implementation ignores some <xsl:output> attributes
    // we need to add xml declaration prolog manually
    if (!output.startsWith("<?xml")) {
      output = '<?xml version="1.0" encoding="UTF-8"?>\n' + output;
    }

    return output;
  }
}
