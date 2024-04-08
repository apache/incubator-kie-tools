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
import { DrgEdge } from "../diagram/graph/graph";
import { EdgeDeletionMode, deleteEdge } from "./deleteEdge";
import { Computed } from "../store/Store";
import { computeContainingDecisionServiceHrefsByDecisionHrefs } from "../store/computed/computeContainingDecisionServiceHrefsByDecisionHrefs.ts";
import { xmlHrefToQName } from "../xml/xmlHrefToQName";

export enum NodeDeletionMode {
  FROM_DRG_AND_ALL_DRDS,
  FROM_CURRENT_DRD_ONLY,
}

export function deleteNode({
  definitions,
  drgEdges,
  drdIndex,
  nodeNature,
  dmnObjectId,
  dmnObjectQName,
  dmnObjectNamespace,
  externalDmnsIndex,
  mode,
}: {
  definitions: DMN15__tDefinitions;
  drgEdges: DrgEdge[];
  drdIndex: number;
  nodeNature: NodeNature;
  externalDmnsIndex: ReturnType<Computed["getExternalModelTypesByNamespace"]>["dmns"];
  dmnObjectNamespace: string;
  dmnObjectId: string | undefined;
  dmnObjectQName: XmlQName;
  mode: NodeDeletionMode;
}): {
  deletedDmnObject: Unpacked<DMN15__tDefinitions["drgElement" | "artifact"]> | undefined;
  deletedDmnShapeOnCurrentDrd: DMNDI15__DMNShape | undefined;
} {
  if (
    mode === NodeDeletionMode.FROM_CURRENT_DRD_ONLY &&
    !canRemoveNodeFromDrdOnly({
      definitions,
      drdIndex,
      dmnObjectNamespace,
      dmnObjectId,
      externalDmnsIndex,
    })
  ) {
    console.warn("DMN MUTATION: Cannot hide a Decision that's contained by a Decision Service from a DRD.");
    return { deletedDmnObject: undefined, deletedDmnShapeOnCurrentDrd: undefined };
  }

  if (mode === NodeDeletionMode.FROM_DRG_AND_ALL_DRDS) {
    // Delete Edges
    // A DRD doesn't necessarily renders all edges of the DRG, so we need to look for what DRG edges to delete when deleting a node from any DRD.
    const nodeId = buildXmlHref({ namespace: dmnObjectNamespace, id: dmnObjectId! });
    for (let i = 0; i < drgEdges.length; i++) {
      const drgEdge = drgEdges[i];
      // Only delete edges that end at or start from the node being deleted.
      if (drgEdge.sourceId === nodeId || drgEdge.targetId === nodeId) {
        deleteEdge({
          definitions,
          drdIndex,
          mode: EdgeDeletionMode.FROM_DRG_AND_ALL_DRDS,
          edge: {
            id: drgEdge.id,
            dmnObject: drgEdge.dmnObject,
          },
        });
      }
    }

    // Delete from containing Decision Services
    const drgElements = definitions.drgElement ?? [];
    for (let i = 0; i < drgElements.length; i++) {
      const drgElement = drgElements[i];
      if (drgElement.__$$element !== "decisionService") {
        continue;
      }

      drgElement.outputDecision = drgElement.outputDecision?.filter((od) => od["@_href"] !== nodeId);
      drgElement.encapsulatedDecision = drgElement.encapsulatedDecision?.filter((ed) => ed["@_href"] !== nodeId);
    }
  }

  let dmnObject: Unpacked<DMN15__tDefinitions["drgElement" | "artifact"]> | undefined;

  // External or unknown nodes don't have a dmnObject associated with it, just the shape..
  if (!dmnObjectQName.prefix) {
    // Delete the dmnObject itself
    if (nodeNature === NodeNature.ARTIFACT) {
      if (mode === NodeDeletionMode.FROM_DRG_AND_ALL_DRDS) {
        const nodeIndex = (definitions.artifact ?? []).findIndex((a) => a["@_id"] === dmnObjectId);
        dmnObject = definitions.artifact?.splice(nodeIndex, 1)?.[0];
      } else {
        throw new Error(`DMN MUTATION: Can't hide an artifact node.`);
      }
    } else if (nodeNature === NodeNature.DRG_ELEMENT) {
      const nodeIndex = (definitions.drgElement ?? []).findIndex((d) => d["@_id"] === dmnObjectId);
      dmnObject =
        mode === NodeDeletionMode.FROM_DRG_AND_ALL_DRDS
          ? definitions.drgElement?.splice(nodeIndex, 1)?.[0]
          : definitions.drgElement?.[nodeIndex];
    } else if (nodeNature === NodeNature.UNKNOWN) {
      // Ignore. There's no dmnObject here.
    } else {
      throw new Error(`DMN MUTATION: Unknown node nature '${nodeNature}'.`);
    }

    if (!dmnObject) {
      throw new Error(`DMN MUTATION: Can't delete DMN object that doesn't exist: ID=${dmnObjectId}`);
    }
  }

  const shapeDmnElementRef = buildXmlQName(dmnObjectQName);

  // Deleting the DMNShape's
  let deletedDmnShapeOnCurrentDrd: DMNDI15__DMNShape | undefined;

  const deletedIdsOnDmnObjectTree = dmnObject
    ? getNewDmnIdRandomizer()
        .ack({ json: [dmnObject], type: "DMN15__tDefinitions", attr: "drgElement" })
        .getOriginalIds()
    : new Set<string>();

  const drdCount = (definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"] ?? []).length;
  for (let i = 0; i < drdCount; i++) {
    if (mode === NodeDeletionMode.FROM_CURRENT_DRD_ONLY && i !== drdIndex) {
      continue;
    }

    const { diagramElements, widthsExtension } = addOrGetDrd({ definitions, drdIndex: i });
    const dmnShapeIndex = (diagramElements ?? []).findIndex((d) => d["@_dmnElementRef"] === shapeDmnElementRef);
    if (dmnShapeIndex >= 0) {
      if (i === drdIndex) {
        deletedDmnShapeOnCurrentDrd = diagramElements[dmnShapeIndex];
      }

      diagramElements?.splice(dmnShapeIndex, 1);
    }

    // Delete widths
    widthsExtension["kie:ComponentWidths"] = widthsExtension["kie:ComponentWidths"]?.filter(
      (w) => !deletedIdsOnDmnObjectTree.has(w["@_dmnElementRef"]!) // Only works because xsd:IDs are exactly the same as QNames when the QName is not prefixed.
    );
  }

  repopulateInputDataAndDecisionsOnAllDecisionServices({ definitions });

  return {
    deletedDmnObject: mode === NodeDeletionMode.FROM_DRG_AND_ALL_DRDS ? dmnObject : undefined,
    deletedDmnShapeOnCurrentDrd,
  };
}

export function canRemoveNodeFromDrdOnly({
  definitions,
  drdIndex,
  dmnObjectNamespace,
  dmnObjectId,
  externalDmnsIndex,
}: {
  dmnObjectNamespace: string;
  dmnObjectId: string | undefined;
  definitions: DMN15__tDefinitions;
  drdIndex: number;
  externalDmnsIndex: ReturnType<Computed["getExternalModelTypesByNamespace"]>["dmns"];
}) {
  const { diagramElements } = addOrGetDrd({ definitions, drdIndex });

  const dmnObjectHref = buildXmlHref({
    namespace: dmnObjectNamespace === definitions["@_namespace"] ? "" : dmnObjectNamespace,
    id: dmnObjectId!,
  });

  const drgElements =
    definitions["@_namespace"] === dmnObjectNamespace
      ? definitions.drgElement ?? []
      : externalDmnsIndex.get(dmnObjectNamespace)?.model.definitions.drgElement ?? [];

  const containingDecisionServiceHrefsByDecisionHrefsRelativeToThisDmn =
    computeContainingDecisionServiceHrefsByDecisionHrefs({
      thisDmnsNamespace: definitions["@_namespace"],
      drgElementsNamespace: dmnObjectNamespace,
      drgElements,
    });

  const containingDecisionServiceHrefs =
    containingDecisionServiceHrefsByDecisionHrefsRelativeToThisDmn.get(dmnObjectHref) ?? [];

  const isContainedByDecisionService = containingDecisionServiceHrefs.length > 0;

  const isContainingDecisionServiceInExpandedFormPresentInTheDrd = containingDecisionServiceHrefs.some((dsHref) =>
    diagramElements.some(
      (e) =>
        e.__$$element === "dmndi:DMNShape" &&
        e["@_dmnElementRef"] === xmlHrefToQName(dsHref, definitions) &&
        !(e["@_isCollapsed"] ?? false)
    )
  );

  return !isContainedByDecisionService || !isContainingDecisionServiceInExpandedFormPresentInTheDrd;
}
