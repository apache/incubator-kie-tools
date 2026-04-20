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

import { BPMN20__tDefinitions } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { Normalized } from "../normalization/normalize";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { addOrGetProcessAndDiagramElements } from "./addOrGetProcessAndDiagramElements";
import { visitFlowElementsAndArtifacts, visitLanes } from "./_elementVisitor";

/**
 * Regenerates the ID of a BPMN element (flow element or lane) and updates all references to it.
 * This includes updating:
 * - The element's @_id
 * - BPMNShape @_bpmnElement reference
 * - BPMNEdge @_bpmnElement references (for sequence flows and associations)
 * - Incoming/outgoing references in connected elements
 * - Boundary event attachments
 */
export function regenerateElementId({
  definitions,
  currentId,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  currentId: string;
}): string {
  const { process, diagramElements } = addOrGetProcessAndDiagramElements({ definitions });
  const newId = generateUuid();

  let elementFound = false;
  let isLane = false;

  // Update flow elements and artifacts
  visitFlowElementsAndArtifacts(process, ({ element, array, index }) => {
    if (element["@_id"] === currentId) {
      elementFound = true;
      array[index] = { ...element, "@_id": newId };

      // Update BPMNShape reference
      const shapeIndex = diagramElements?.findIndex((d) => d["@_bpmnElement"] === currentId);
      if (shapeIndex !== undefined && shapeIndex >= 0 && diagramElements) {
        diagramElements[shapeIndex] = { ...diagramElements[shapeIndex], "@_bpmnElement": newId };
      }

      // If it's a sequence flow or association, update BPMNEdge reference
      if (element.__$$element === "sequenceFlow" || element.__$$element === "association") {
        const edgeIndex = diagramElements?.findIndex((d) => d["@_bpmnElement"] === currentId);
        if (edgeIndex !== undefined && edgeIndex >= 0 && diagramElements) {
          diagramElements[edgeIndex] = { ...diagramElements[edgeIndex], "@_bpmnElement": newId };
        }
      }

      return false; // Stop visiting
    }
  });

  // If not found in flow elements, check lanes
  if (!elementFound) {
    visitLanes(process, ({ lane }) => {
      if (lane["@_id"] === currentId) {
        elementFound = true;
        isLane = true;
        lane["@_id"] = newId;

        // Update BPMNShape reference for lane
        const shapeIndex = diagramElements?.findIndex((d) => d["@_bpmnElement"] === currentId);
        if (shapeIndex !== undefined && shapeIndex >= 0 && diagramElements) {
          diagramElements[shapeIndex] = { ...diagramElements[shapeIndex], "@_bpmnElement": newId };
        }

        return false; // Stop visiting
      }
    });
  }

  if (!elementFound) {
    throw new Error(`BPMN MUTATION: Element with id ${currentId} not found`);
  }

  // Update all references to the old ID in other elements
  if (!isLane) {
    visitFlowElementsAndArtifacts(process, ({ element }) => {
      // Update incoming references (only for flow nodes)
      if ("incoming" in element && element.incoming) {
        element.incoming = element.incoming.map((incoming: { __$$text: string }) =>
          incoming.__$$text === currentId ? { __$$text: newId } : incoming
        );
      }

      // Update outgoing references (only for flow nodes)
      if ("outgoing" in element && element.outgoing) {
        element.outgoing = element.outgoing.map((outgoing: { __$$text: string }) =>
          outgoing.__$$text === currentId ? { __$$text: newId } : outgoing
        );
      }

      // Update boundary event attachments
      if (element.__$$element === "boundaryEvent" && element["@_attachedToRef"] === currentId) {
        element["@_attachedToRef"] = newId;
      }

      // Update sequence flow source/target references
      if (element.__$$element === "sequenceFlow") {
        if (element["@_sourceRef"] === currentId) {
          element["@_sourceRef"] = newId;
        }
        if (element["@_targetRef"] === currentId) {
          element["@_targetRef"] = newId;
        }
      }

      // Update association source/target references
      if (element.__$$element === "association") {
        if (element["@_sourceRef"] === currentId) {
          element["@_sourceRef"] = newId;
        }
        if (element["@_targetRef"] === currentId) {
          element["@_targetRef"] = newId;
        }
      }
    });
  }

  return newId;
}
