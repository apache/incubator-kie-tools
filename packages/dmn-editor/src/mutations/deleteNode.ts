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

import { DMN15__tDefinitions, DMNDI15__DMNShape } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { NodeNature } from "./NodeNature";
import { addOrGetDrd } from "./addOrGetDrd";
import { repopulateInputDataAndDecisionsOnAllDecisionServices } from "./repopulateInputDataAndDecisionsOnDecisionService";
import { XmlQName, buildXmlQName } from "@kie-tools/xml-parser-ts/dist/qNames";
import { getNewDmnIdRandomizer } from "../idRandomizer/dmnIdRandomizer";
import { buildXmlHref } from "../xml/xmlHrefs";
import { Unpacked } from "../tsExt/tsExt";

export enum NodeDeletionMode {
  FORM_DRG_AND_DRD,
  FROM_DRD_ONLY,
}

export function deleteNode({
  definitions,
  drdIndex,
  nodeNature,
  dmnObjectId,
  dmnObjectQName,
  dmnObjectNamespace,
  mode,
}: {
  definitions: DMN15__tDefinitions;
  drdIndex: number;
  nodeNature: NodeNature;
  dmnObjectNamespace: string | undefined;
  dmnObjectId: string | undefined;
  dmnObjectQName: XmlQName;
  mode: NodeDeletionMode;
}): {
  deletedDmnObject: Unpacked<DMN15__tDefinitions["drgElement" | "artifact"]> | undefined;
  deletedShape: DMNDI15__DMNShape | undefined;
} {
  const { diagramElements, widthsExtension } = addOrGetDrd({ definitions, drdIndex });

  //
  // NOTE
  //
  // Edges are NOT deleted here. Edges need to be deleted by separate calls to `deleteEdge`.

  if (
    mode === NodeDeletionMode.FROM_DRD_ONLY &&
    !canRemoveNodeFromDrdOnly({
      definitions,
      drdIndex,
      dmnObjectNamespace,
      dmnObjectId,
    })
  ) {
    console.warn("DMN MUTATION: Cannot hide a Decision that's contained by a Decision Service from a DRD.");
    return { deletedDmnObject: undefined, deletedShape: undefined };
  }

  // delete the DMNShape
  const shapeDmnElementRef = buildXmlQName(dmnObjectQName);
  const deletedShape = diagramElements?.splice(
    (diagramElements ?? []).findIndex((d) => d["@_dmnElementRef"] === shapeDmnElementRef),
    1
  )[0];

  let deletedDmnObject: Unpacked<DMN15__tDefinitions["drgElement" | "artifact"]>[] | undefined;

  // External or unknown nodes don't have a dmnObject associated with it, just the shape..
  if (!dmnObjectQName.prefix) {
    // Delete the dmnObject itself
    if (nodeNature === NodeNature.ARTIFACT) {
      if (mode === NodeDeletionMode.FORM_DRG_AND_DRD) {
        const nodeIndex = (definitions.artifact ?? []).findIndex((a) => a["@_id"] === dmnObjectId);
        deletedDmnObject = definitions.artifact?.splice(nodeIndex, 1);
      }
    } else if (nodeNature === NodeNature.DRG_ELEMENT) {
      const nodeIndex = (definitions.drgElement ?? []).findIndex((d) => d["@_id"] === dmnObjectId);
      const node =
        mode === NodeDeletionMode.FORM_DRG_AND_DRD
          ? (deletedDmnObject = definitions.drgElement?.splice(nodeIndex, 1))
          : [definitions.drgElement?.[nodeIndex]];

      const deletedIdsOnDrgElementTree = getNewDmnIdRandomizer()
        .ack({ json: node, type: "DMN15__tDefinitions", attr: "drgElement" })
        .getOriginalIds();

      // Delete widths
      widthsExtension["kie:ComponentWidths"] = widthsExtension["kie:ComponentWidths"]?.filter(
        (w) => !deletedIdsOnDrgElementTree.has(w["@_dmnElementRef"]!)
      );
    } else if (nodeNature === NodeNature.UNKNOWN) {
      // Ignore. There's no dmnObject here.
    } else {
      throw new Error(`Unknown node nature '${nodeNature}'.`);
    }
  }

  repopulateInputDataAndDecisionsOnAllDecisionServices({ definitions });

  return { deletedDmnObject: deletedDmnObject?.[0], deletedShape };
}
export function canRemoveNodeFromDrdOnly({
  definitions,
  drdIndex,
  dmnObjectNamespace,
  dmnObjectId,
}: {
  dmnObjectNamespace: string | undefined;
  dmnObjectId: string | undefined;
  definitions: DMN15__tDefinitions;
  drdIndex: number;
}) {
  const { diagramElements } = addOrGetDrd({ definitions, drdIndex });

  const dmnObjectHref = buildXmlHref({ namespace: dmnObjectNamespace, id: dmnObjectId! });

  const containingDecisionService = definitions.drgElement?.find(
    (drgElement) =>
      drgElement.__$$element === "decisionService" &&
      [...(drgElement.encapsulatedDecision ?? []), ...(drgElement.outputDecision ?? [])].some(
        (dd) => dd["@_href"] === dmnObjectHref
      )
  );

  const isContainingDecisionServicePresentInTheDrd =
    containingDecisionService &&
    diagramElements.some(
      (s) =>
        s.__$$element === "dmndi:DMNShape" &&
        s["@_dmnElementRef"] === buildXmlQName({ type: "xml-qname", localPart: containingDecisionService["@_id"]! })
    );
  return !containingDecisionService || !isContainingDecisionServicePresentInTheDrd;
}
