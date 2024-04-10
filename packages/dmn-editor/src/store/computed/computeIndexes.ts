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

import { DMNDI15__DMNEdge, DMNDI15__DMNShape } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { XmlQName, parseXmlQName } from "@kie-tools/xml-parser-ts/dist/qNames";
import { KIE_DMN_UNKNOWN_NAMESPACE } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";
import { buildXmlHref } from "../../xml/xmlHrefs";
import { State } from "../Store";

export function computeIndexedDrd(
  thisDmnsNamespace: string,
  definitions: State["dmn"]["model"]["definitions"],
  drdIndex: State["diagram"]["drdIndex"]
) {
  const dmnEdgesByDmnElementRef = new Map<string, DMNDI15__DMNEdge & { index: number }>();
  const dmnShapesByHref = new Map<string, DMNDI15__DMNShape & { index: number; dmnElementRefQName: XmlQName }>();
  const hrefsOfDmnElementRefsOfShapesPointingToExternalDmnObjects = new Set<string>();

  const diagramElements =
    definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.[drdIndex]?.["dmndi:DMNDiagramElement"] ?? [];
  for (let i = 0; i < diagramElements.length; i++) {
    const e = diagramElements[i];
    // DMNEdge
    if (e.__$$element === "dmndi:DMNEdge") {
      dmnEdgesByDmnElementRef.set(e["@_dmnElementRef"], { ...e, index: i });
    }

    // DMNShape
    else if (e.__$$element === "dmndi:DMNShape") {
      let href: string;
      // @_dmnElementRef is a xsd:QName, meaning it can be prefixed with a namespace name.
      // If we find the namespace as a namespace declaration on the `definitions` object, then this shape represents a node from an included model.
      // Therefore, we need to add it to `dmnShapesForExternalNodesByDmnRefId`, so we can draw these nodes.
      // Do not skip adding it to the regular `dmnShapesByHref`, as nodes will query this.
      const dmnElementRefQName = parseXmlQName(e["@_dmnElementRef"]);
      if (dmnElementRefQName.prefix) {
        const namespace = definitions[`@_xmlns:${dmnElementRefQName.prefix}`] ?? KIE_DMN_UNKNOWN_NAMESPACE;
        href = buildXmlHref({ namespace, id: dmnElementRefQName.localPart });
        hrefsOfDmnElementRefsOfShapesPointingToExternalDmnObjects.add(href);
      } else {
        href = buildXmlHref({
          namespace: definitions["@_namespace"] === thisDmnsNamespace ? "" : definitions["@_namespace"],
          id: dmnElementRefQName.localPart,
        });
      }

      dmnShapesByHref.set(href, { ...e, index: i, dmnElementRefQName });
    } else {
      // Ignore anything that is unknown.
    }
  }
  return {
    dmnEdgesByDmnElementRef,
    dmnShapesByHref,
    hrefsOfDmnElementRefsOfShapesPointingToExternalDmnObjects,
  };
}
