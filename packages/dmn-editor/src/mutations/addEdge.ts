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
  DC__Bounds,
  DMN15__tAssociation,
  DMN15__tAuthorityRequirement,
  DMN15__tDecision,
  DMN15__tDefinitions,
  DMN15__tInformationRequirement,
  DMN15__tKnowledgeRequirement,
  DMNDI15__DMNEdge,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { PositionalNodeHandleId } from "../diagram/connections/PositionalNodeHandles";
import { EdgeType, NodeType } from "../diagram/connections/graphStructure";
import { _checkIsValidConnection } from "../diagram/connections/isValidConnection";
import { EDGE_TYPES } from "../diagram/edges/EdgeTypes";
import { getDiscreteAutoPositioningEdgeIdMarker, getPointForHandle } from "../diagram/maths/DmnMaths";
import { getRequirementsFromEdge } from "./addConnectedNode";
import { addOrGetDrd } from "./addOrGetDrd";
import { Unpacked } from "../tsExt/tsExt";
import { repopulateInputDataAndDecisionsOnAllDecisionServices } from "./repopulateInputDataAndDecisionsOnDecisionService";
import { DmnDiagramNodeData } from "../diagram/nodes/Nodes";
import { AutoPositionedEdgeMarker } from "../diagram/edges/AutoPositionedEdgeMarker";

export function addEdge({
  definitions,
  drdIndex,
  sourceNode,
  targetNode,
  edge,
  keepWaypoints,
}: {
  definitions: DMN15__tDefinitions;
  drdIndex: number;
  sourceNode: {
    type: NodeType;
    data: DmnDiagramNodeData;
    href: string;
    bounds: DC__Bounds;
    shapeId: string | undefined;
  };
  targetNode: {
    type: NodeType;
    data: DmnDiagramNodeData;
    href: string;
    bounds: DC__Bounds;
    shapeId: string | undefined;
    index: number;
  };
  edge: {
    type: EdgeType;
    targetHandle: PositionalNodeHandleId;
    sourceHandle: PositionalNodeHandleId;
    autoPositionedEdgeMarker: AutoPositionedEdgeMarker | undefined;
  };
  keepWaypoints: boolean;
}) {
  if (!_checkIsValidConnection(sourceNode, targetNode, edge.type)) {
    throw new Error(`DMN MUTATION: Invalid structure: (${sourceNode.type}) --${edge.type}--> (${targetNode.type}) `);
  }

  const newEdgeId = generateUuid();

  let existingEdgeId: string | undefined = undefined;

  // Associations
  if (edge.type === EDGE_TYPES.association) {
    definitions.artifact ??= [];

    const newAssociation: DMN15__tAssociation = {
      "@_id": newEdgeId,
      "@_associationDirection": "Both",
      sourceRef: { "@_href": `${sourceNode.href}` },
      targetRef: { "@_href": `${targetNode.href}` },
    };

    // Remove previously existing association
    const removed = removeFirstMatchIfPresent(
      definitions.artifact,
      (a) => a.__$$element === "association" && areAssociationsEquivalent(a, newAssociation)
    );
    existingEdgeId = removed?.["@_id"];

    // Replace with the new one.
    definitions.artifact?.push({
      __$$element: "association",
      ...newAssociation,
      "@_id": tryKeepingEdgeId(existingEdgeId, newEdgeId),
    });
  }
  // Requirements
  else {
    const requirements = getRequirementsFromEdge(sourceNode, newEdgeId, edge.type);
    const drgElement = definitions.drgElement![targetNode.index] as DMN15__tDecision; // We cast to tDecision here because it has all three types of requirement.
    if (requirements?.informationRequirement) {
      drgElement.informationRequirement ??= [];
      const removed = removeFirstMatchIfPresent(drgElement.informationRequirement, (ir) =>
        doesInformationRequirementsPointTo(ir, sourceNode.href)
      );
      existingEdgeId = removed?.["@_id"];
      drgElement.informationRequirement?.push(
        ...requirements.informationRequirement.map((s) => ({
          ...s,
          "@_id": tryKeepingEdgeId(existingEdgeId, newEdgeId),
        }))
      );
    }
    //
    else if (requirements?.knowledgeRequirement) {
      drgElement.knowledgeRequirement ??= [];
      const removed = removeFirstMatchIfPresent(drgElement.knowledgeRequirement, (kr) =>
        doesKnowledgeRequirementsPointTo(kr, sourceNode.href)
      );
      existingEdgeId = removed?.["@_id"];
      drgElement.knowledgeRequirement?.push(
        ...requirements.knowledgeRequirement.map((s) => ({
          ...s,
          "@_id": tryKeepingEdgeId(existingEdgeId, newEdgeId),
        }))
      );
    }
    //
    else if (requirements?.authorityRequirement) {
      drgElement.authorityRequirement ??= [];
      const removed = removeFirstMatchIfPresent(drgElement.authorityRequirement, (ar) =>
        doesAuthorityRequirementsPointTo(ar, sourceNode.href)
      );
      existingEdgeId = removed?.["@_id"];
      drgElement.authorityRequirement?.push(
        ...requirements.authorityRequirement.map((s) => ({
          ...s,
          "@_id": tryKeepingEdgeId(existingEdgeId, newEdgeId),
        }))
      );
    }
  }

  const { diagramElements } = addOrGetDrd({ definitions, drdIndex });

  // Remove existing
  const removedDmnEdge: DMNDI15__DMNEdge | undefined = removeFirstMatchIfPresent(
    diagramElements,
    (e) => e.__$$element === "dmndi:DMNEdge" && e["@_dmnElementRef"] === existingEdgeId
  );

  const newWaypoints = keepWaypoints
    ? [
        getPointForHandle({ bounds: sourceNode.bounds, handle: edge.sourceHandle }),
        ...(removedDmnEdge?.["di:waypoint"] ?? []).slice(1, -1), // Slicing an empty array will always return an empty array, so it's ok.
        getPointForHandle({ bounds: targetNode.bounds, handle: edge.targetHandle }),
      ]
    : [
        getPointForHandle({ bounds: sourceNode.bounds, handle: edge.sourceHandle }),
        getPointForHandle({ bounds: targetNode.bounds, handle: edge.targetHandle }),
      ];

  const newDmnEdge: Unpacked<typeof diagramElements> = {
    __$$element: "dmndi:DMNEdge",
    "@_id":
      withoutDiscreteAutoPosinitioningMarker(removedDmnEdge?.["@_id"] ?? generateUuid()) +
      (edge.autoPositionedEdgeMarker ?? ""),
    "@_dmnElementRef": existingEdgeId ?? newEdgeId,
    "@_sourceElement": sourceNode.shapeId,
    "@_targetElement": targetNode.shapeId,
    "di:waypoint": newWaypoints,
  };

  // Replace with the new one.
  diagramElements.push(newDmnEdge);

  repopulateInputDataAndDecisionsOnAllDecisionServices({ definitions });

  return { newDmnEdge };
}

function doesInformationRequirementsPointTo(a: DMN15__tInformationRequirement, nodeId: string) {
  return (
    a.requiredInput?.["@_href"] === `${nodeId}` || //
    a.requiredDecision?.["@_href"] === `${nodeId}`
  );
}

function doesKnowledgeRequirementsPointTo(a: DMN15__tKnowledgeRequirement, nodeId: string) {
  return a.requiredKnowledge?.["@_href"] === `${nodeId}`;
}

function doesAuthorityRequirementsPointTo(a: DMN15__tAuthorityRequirement, nodeId: string) {
  return (
    a.requiredInput?.["@_href"] === `${nodeId}` ||
    a.requiredDecision?.["@_href"] === `${nodeId}` ||
    a.requiredAuthority?.["@_href"] === `${nodeId}`
  );
}

function areAssociationsEquivalent(a: DMN15__tAssociation, b: DMN15__tAssociation) {
  return (
    (a.sourceRef["@_href"] === b.sourceRef["@_href"] && a.targetRef["@_href"] === b.targetRef["@_href"]) ||
    (a.sourceRef["@_href"] === b.targetRef["@_href"] && a.targetRef["@_href"] === b.sourceRef["@_href"])
  );
}

function removeFirstMatchIfPresent<T>(arr: T[], predicate: Parameters<Array<T>["findIndex"]>[0]): T | undefined {
  const index = arr.findIndex(predicate);
  const removed = arr[index] ?? undefined;
  arr.splice(index, index >= 0 ? 1 : 0);
  return removed;
}

function tryKeepingEdgeId(existingEdgeId: string | undefined, newEdgeId: string) {
  return existingEdgeId ?? newEdgeId;
}
function withoutDiscreteAutoPosinitioningMarker(edgeId: string) {
  const marker = getDiscreteAutoPositioningEdgeIdMarker(edgeId);
  return marker ? edgeId.replace(`${marker}`, "") : edgeId;
}
