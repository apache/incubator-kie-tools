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

import { buildXmlQName } from "@kie-tools/xml-parser-ts/dist/qNames";
import { parseXmlHref } from "./xmlHrefs";
import { getXmlNamespaceDeclarationName } from "./xmlNamespaceDeclarations";
import { XmlParserTsRootElementBaseType } from "@kie-tools/xml-parser-ts";

export function xmlHrefToQName(hrefString: string, rootElement: XmlParserTsRootElementBaseType | undefined) {
  const href = parseXmlHref(hrefString);

  const qNamePrefix = href.namespace
    ? getXmlNamespaceDeclarationName({ rootElement, namespace: href.namespace })
    : undefined;

  if (href.namespace && !qNamePrefix) {
    throw new Error(`Can't find namespace declaration for namespace '${href.namespace}'`);
  }

  return buildXmlQName({
    type: "xml-qname",
    localPart: href.id,
    prefix: qNamePrefix,
  });
}
