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

import { Normalized } from "../normalization/normalize";
import { BpmnDiagramEdgeData } from "../diagram/BpmnDiagramDomain";
import {
  BPMN20__tDefinitions,
  BPMN20__tProcess,
  BPMNDI__BPMNEdge,
} from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { addOrGetProcessAndDiagramElements } from "./addOrGetProcessAndDiagramElements";
import { FoundElement, visitFlowElementsAndArtifacts } from "./_elementVisitor";
import { ElementFilter } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import { updateGatewayDirection } from "./addEdge";

export function deleteEdge({
  definitions,
  __readonly_edgeId,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  __readonly_edgeId: string;
}): {
  deletedBpmnEdge: BPMNDI__BPMNEdge | undefined;
  deletedBpmnElement: BpmnDiagramEdgeData["bpmnElement"] | undefined;
} {
  const { process, diagramElements } = addOrGetProcessAndDiagramElements({ definitions });

  // BPMN Element (<sequenceFlow> or <association>)
  let foundBpmnElement:
    | undefined
    | FoundElement<
        | Normalized<ElementFilter<Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>, "sequenceFlow">>
        | Normalized<ElementFilter<Unpacked<NonNullable<BPMN20__tProcess["artifact"]>>, "association">>
      >;

  visitFlowElementsAndArtifacts(process, ({ element, ...args }) => {
    if (
      (element.__$$element === "sequenceFlow" || element.__$$element === "association") &&
      element["@_id"] === __readonly_edgeId
    ) {
      foundBpmnElement = { element, ...args };
      return false; // Will stop visiting.
    }
  });

  if (!foundBpmnElement) {
    throw new Error(`BPMN MUTATION: Can't find BPMN Element with ID '${__readonly_edgeId}'`);
  }

  foundBpmnElement.array.splice(foundBpmnElement.index, 1);

  // BPMNEdge
  const bpmnEdgeIndex = (diagramElements ?? []).findIndex((e) => e["@_bpmnElement"] === __readonly_edgeId);
  if (bpmnEdgeIndex < 0) {
    throw new Error(`BPMN MUTATION: Can't find BPMNEdge with referencing a BPMN element with ID ${__readonly_edgeId}`);
  }
  const deletedBpmnEdge = diagramElements?.splice(bpmnEdgeIndex, 1)[0] as BPMNDI__BPMNEdge | undefined;

  // <incoming> and <outgoing> elements of Flow Elements
  visitFlowElementsAndArtifacts(process, ({ element, ...args }) => {
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
      if (element["@_id"] === foundBpmnElement?.element?.["@_sourceRef"]) {
        element.outgoing = element.outgoing?.filter((s) => s.__$$text !== __readonly_edgeId);
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
      else if (element["@_id"] === foundBpmnElement?.element?.["@_targetRef"]) {
        element.incoming = element.incoming?.filter((s) => s.__$$text !== __readonly_edgeId);
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

      element.outgoing = element.outgoing?.filter((s) => s.__$$text !== __readonly_edgeId) ?? [];
      return false; // Will stop visiting.
    }
  });

  return {
    deletedBpmnEdge,
    deletedBpmnElement: foundBpmnElement.element,
  };
}
