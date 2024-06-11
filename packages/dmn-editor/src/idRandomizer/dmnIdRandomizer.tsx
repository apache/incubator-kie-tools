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

import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import {
  elements as dmn15elements,
  meta as dmn15meta,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/meta";
import { XmlParserTsIdRandomizer, XmlParserTsIdRandomizerMatcher } from "@kie-tools/xml-parser-ts/dist/idRandomizer";
import { buildXmlHref, parseXmlHref } from "../xml/xmlHrefs";

export function getNewDmnIdRandomizer() {
  return new XmlParserTsIdRandomizer({
    meta: dmn15meta,
    elements: dmn15elements,
    newIdGenerator: generateUuid,
    matchers: [tDmnElementReferenceIdRandomizerMatcher],
  });
}

export const tDmnElementReferenceIdRandomizerMatcher: XmlParserTsIdRandomizerMatcher<typeof dmn15meta> = ({
  parentJson,
  metaTypeName,
  attr,
}) => {
  if (metaTypeName === "DMN15__tDMNElementReference" && attr === "@_href") {
    const href = parseXmlHref(parentJson[attr]);
    return [
      href.id,
      ({ newId }) => {
        console.debug(
          `ID RANDOMIZER: [anyURI] Updating id from ${href.id} to ${newId} @ (${String(metaTypeName)}.${String(
            attr
          )}: ${parentJson[attr]})`
        );
        parentJson[attr] = buildXmlHref({ ...href, id: newId });
      },
    ];
  }
};
