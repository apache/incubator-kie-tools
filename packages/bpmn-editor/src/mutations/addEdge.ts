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

import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";

import { Normalized } from "../normalization/normalize";
import {
  BPMN20__tAssociation,
  BPMN20__tDefinitions,
  BPMN20__tDocumentation,
  BPMN20__tProcess,
  BPMN20__tSequenceFlow,
  BPMNDI__BPMNEdge,
} from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { BPMN_GRAPH_STRUCTURE, BpmnEdgeType, BpmnNodeType, EDGE_TYPES } from "../diagram/BpmnDiagramDomain";
import { DC__Bounds } from "@kie-tools/xyflow-react-kie-diagram/dist/maths/model";
import { PositionalNodeHandleId } from "@kie-tools/xyflow-react-kie-diagram/dist/nodes/PositionalNodeHandles";
import { AutoPositionedEdgeMarker } from "@kie-tools/xyflow-react-kie-diagram/dist/edges/AutoPositionedEdgeMarker";
import { _checkIsValidConnection } from "@kie-tools/xyflow-react-kie-diagram/dist/graph/isValidConnection";
import { addOrGetProcessAndDiagramElements } from "./addOrGetProcessAndDiagramElements";
import {
  getDiscreteAutoPositioningEdgeIdMarker,
  getPointForHandle,
} from "@kie-tools/xyflow-react-kie-diagram/dist/maths/DcMaths";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import { visitFlowElementsAndArtifacts } from "./_elementVisitor";
import { ElementFilter } from "@kie-tools/xml-parser-ts/dist/elementFilter";

export function addEdge({
  definitions,
  __readonly_sourceNode,
  __readonly_targetNode,
  __readonly_edge,
  __readonly_keepWaypoints,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  __readonly_sourceNode: {
    type: BpmnNodeType;
    href: string;
    bounds: DC__Bounds;
    shapeId: string | undefined;
  };
  __readonly_targetNode: {
    type: BpmnNodeType;
    href: string;
    bounds: DC__Bounds;
    shapeId: string | undefined;
  };
  __readonly_edge: {
    type: BpmnEdgeType;
    name: string | undefined;
    documentation: Normalized<BPMN20__tDocumentation[]> | undefined;
    targetHandle: PositionalNodeHandleId;
    sourceHandle: PositionalNodeHandleId;
    autoPositionedEdgeMarker: AutoPositionedEdgeMarker | undefined;
  };
  __readonly_keepWaypoints: boolean;
}) {
  if (
    !_checkIsValidConnection(BPMN_GRAPH_STRUCTURE, __readonly_sourceNode, __readonly_targetNode, __readonly_edge.type)
  ) {
    throw new Error(
      `BPMN MUTATION: Invalid structure: (${__readonly_sourceNode.type}) --${__readonly_edge.type}--> (${__readonly_targetNode.type}) `
    );
  }

  const newEdgeId = generateUuid();

  const { process, diagramElements } = addOrGetProcessAndDiagramElements({ definitions });

  let existingEdgeId: string | undefined = undefined;

  // Associations
  if (__readonly_edge.type === EDGE_TYPES.association) {
    process.artifact ??= [];

    const newAssociation: Normalized<BPMN20__tAssociation> = {
      "@_id": newEdgeId,
      "@_associationDirection": "Both",
      "@_sourceRef": __readonly_sourceNode.href,
      "@_targetRef": __readonly_targetNode.href,
    };

    // Remove previously existing association
    const removed = removeFirstMatchIfPresent(
      process.artifact,
      (a) => a.__$$element === "association" && areEdgesEquivalent(a, newAssociation)
    );
    existingEdgeId = removed?.["@_id"];

    // Replace with the new one.
    process.artifact?.push({
      __$$element: "association",
      ...newAssociation,
      "@_id": tryKeepingEdgeId(existingEdgeId, newEdgeId),
    });
  }

  // Sequence Flows
  else {
    process.flowElement ??= [];

    const newSequenceFlow: Normalized<BPMN20__tSequenceFlow> = {
      "@_id": newEdgeId,
      "@_sourceRef": __readonly_sourceNode.href,
      "@_targetRef": __readonly_targetNode.href,
      "@_name": __readonly_edge.name,
      documentation: __readonly_edge.documentation,
    };

    // Remove previously existing association
    const removed = removeFirstMatchIfPresent(
      process.flowElement,
      (a) => a.__$$element === "sequenceFlow" && areEdgesEquivalent(a, newSequenceFlow)
    );
    existingEdgeId = removed?.["@_id"];

    // Replace with the new one.
    process.flowElement?.push({
      __$$element: "sequenceFlow",
      ...newSequenceFlow,
      "@_id": tryKeepingEdgeId(existingEdgeId, newEdgeId),
    });

    // <incoming> and <outgoing> elements of Flow Elements
    visitFlowElementsAndArtifacts(process, ({ element }) => {
      if (
        element.__$$element !== "association" &&
        element.__$$element !== "group" &&
        element.__$$element !== "textAnnotation" &&
        element.__$$element !== "sequenceFlow" &&
        element.__$$element !== "dataStoreReference" &&
        element.__$$element !== "dataObject" &&
        element.__$$element !== "dataObjectReference"
      ) {
        // outgoing
        if (element["@_id"] === newSequenceFlow["@_sourceRef"]) {
          element.outgoing ??= [];
          element.outgoing.push({ __$$text: newSequenceFlow["@_id"] });

          if (
            element.__$$element === "complexGateway" ||
            element.__$$element === "parallelGateway" ||
            element.__$$element === "exclusiveGateway" ||
            element.__$$element === "inclusiveGateway" ||
            element.__$$element === "eventBasedGateway"
          ) {
            updateGatewayDirection(element);
          }
        }

        // incoming
        else if (element["@_id"] === newSequenceFlow["@_targetRef"]) {
          element.incoming ??= [];
          element.incoming.push({ __$$text: newSequenceFlow["@_id"] });
          if (
            element.__$$element === "complexGateway" ||
            element.__$$element === "parallelGateway" ||
            element.__$$element === "exclusiveGateway" ||
            element.__$$element === "inclusiveGateway" ||
            element.__$$element === "eventBasedGateway"
          ) {
            updateGatewayDirection(element);
          }
        }

        // ignore
        else {
          // empty on purpose
        }
      }
    });
  }

  // Remove existing
  const removedBpmnEdge = removeFirstMatchIfPresent(
    diagramElements,
    (e) => e.__$$element === "bpmndi:BPMNEdge" && e["@_bpmnElement"] === existingEdgeId
  ) as Normalized<BPMNDI__BPMNEdge> | undefined;

  const newWaypoints = __readonly_keepWaypoints
    ? [
        getPointForHandle({ bounds: __readonly_sourceNode.bounds, handle: __readonly_edge.sourceHandle }),
        ...(removedBpmnEdge?.["di:waypoint"] ?? []).slice(1, -1), // Slicing an empty array will always return an empty array, so it's ok.
        getPointForHandle({ bounds: __readonly_targetNode.bounds, handle: __readonly_edge.targetHandle }),
      ]
    : [
        getPointForHandle({ bounds: __readonly_sourceNode.bounds, handle: __readonly_edge.sourceHandle }),
        getPointForHandle({ bounds: __readonly_targetNode.bounds, handle: __readonly_edge.targetHandle }),
      ];

  const newBpmnEdge: Unpacked<typeof diagramElements> = {
    __$$element: "bpmndi:BPMNEdge",
    "@_id":
      withoutDiscreteAutoPosinitioningMarker(removedBpmnEdge?.["@_id"] ?? generateUuid()) +
      (__readonly_edge.autoPositionedEdgeMarker ?? ""),
    "@_bpmnElement": existingEdgeId ?? newEdgeId,
    "@_sourceElement": __readonly_sourceNode.shapeId,
    "@_targetElement": __readonly_targetNode.shapeId,
    "di:waypoint": newWaypoints,
  };

  // Replace with the new one.
  diagramElements.push(newBpmnEdge);

  return { newBpmnEdge };
}

function areEdgesEquivalent(
  a: Normalized<BPMN20__tAssociation | BPMN20__tSequenceFlow>,
  b: Normalized<BPMN20__tAssociation | BPMN20__tSequenceFlow>
) {
  return (
    (a["@_sourceRef"] === b["@_sourceRef"] && a["@_targetRef"] === b["@_targetRef"]) ||
    (a["@_sourceRef"] === b["@_targetRef"] && a["@_targetRef"] === b["@_sourceRef"])
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

export function updateGatewayDirection(
  gatewayElement: Normalized<
    ElementFilter<
      Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>,
      "complexGateway" | "parallelGateway" | "exclusiveGateway" | "inclusiveGateway" | "eventBasedGateway"
    >
  >
) {
  if ((gatewayElement.incoming ?? []).length <= 1 && (gatewayElement.outgoing ?? []).length > 1) {
    gatewayElement["@_gatewayDirection"] = "Diverging";
  } else if ((gatewayElement.incoming ?? []).length > 1 && (gatewayElement.outgoing ?? []).length <= 1) {
    gatewayElement["@_gatewayDirection"] = "Converging";
    // FIXME: Mixed gateway direction not supported by Workflow Engine yet
    // else if ((gatewayElement.incoming ?? []).length > 1 && (gatewayElement.outgoing ?? []).length > 1) {
    //   gatewayElement["@_gatewayDirection"] = "Mixed";
    // } else if ((gatewayElement.incoming ?? []).length == 1 && (gatewayElement.outgoing ?? []).length == 1) {
    //   // this handles the 1:1 case of the diverging gateways
  } else {
    gatewayElement["@_gatewayDirection"] = "Unspecified";
  }
}
