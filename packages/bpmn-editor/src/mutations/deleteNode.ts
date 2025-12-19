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

import {
  BPMN20__tDefinitions,
  BPMN20__tProcess,
  BPMNDI__BPMNShape,
} from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { BpmnDiagramEdgeData, BpmnNodeElement } from "../diagram/BpmnDiagramDomain";
import { Normalized } from "../normalization/normalize";
import { addOrGetProcessAndDiagramElements } from "./addOrGetProcessAndDiagramElements";
import { deleteEdge } from "./deleteEdge";
import { FoundElement, visitFlowElementsAndArtifacts } from "./_elementVisitor";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import { ElementExclusion } from "@kie-tools/xml-parser-ts/dist/elementFilter";

export function deleteNode({
  definitions,
  __readonly_bpmnEdgeData,
  __readonly_bpmnElementId,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  __readonly_bpmnEdgeData: BpmnDiagramEdgeData[];
  __readonly_bpmnElementId: string | undefined;
}): {
  deletedBpmnElement: BpmnNodeElement | undefined;
  deletedBpmnShape: Normalized<BPMNDI__BPMNShape> | undefined;
} {
  const { process, diagramElements } = addOrGetProcessAndDiagramElements({ definitions });

  // Delete Edges
  const nodeId = __readonly_bpmnElementId;

  for (let i = 0; i < __readonly_bpmnEdgeData.length; i++) {
    const drgEdge = __readonly_bpmnEdgeData[i];
    // Only delete edges that end at or start from the node being deleted.
    if (drgEdge.bpmnEdge?.["@_sourceElement"] === nodeId || drgEdge.bpmnEdge?.["@_targetElement"] === nodeId) {
      deleteEdge({ definitions, __readonly_edgeId: drgEdge["@_id"] });
    }
  }

  // Delete the bpmnElement

  let laneIndex: number;

  let foundElement:
    | undefined
    | FoundElement<
        | ElementExclusion<Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>, "sequenceFlow">
        | ElementExclusion<Unpacked<NonNullable<BPMN20__tProcess["artifact"]>>, "association">
      >;

  let deletedBpmnElement: BpmnNodeElement | undefined = undefined;

  visitFlowElementsAndArtifacts(process, ({ element, ...args }) => {
    if (
      element["@_id"] === __readonly_bpmnElementId &&
      element.__$$element !== "sequenceFlow" &&
      element.__$$element !== "association"
    ) {
      foundElement = { element, ...args };
      return false; // Will stop visiting.
    }
  });

  // if flowElement or artifact
  if (foundElement) {
    deletedBpmnElement = foundElement.array.splice(foundElement.index, 1)?.[0] as BpmnNodeElement | undefined;
  }

  // if lane
  else if (
    (laneIndex = (process.laneSet?.[0].lane ?? []).findIndex((d) => d["@_id"] === __readonly_bpmnElementId)) >= 0
  ) {
    const deletedLane = (process.laneSet?.[0].lane ?? [])?.splice(laneIndex, 1)?.[0];
    deletedBpmnElement = deletedLane ? { ...deletedLane, __$$element: "lane" } : undefined;
  }

  // or warn
  else {
    console.warn(`BPMN MUTATION: Cannot find any BPMN Element with ID '${__readonly_bpmnElementId}'.`);
    return {
      deletedBpmnElement: undefined,
      deletedBpmnShape: undefined,
    };
  }

  // Delete the BPMNShape

  let deletedBpmnShape: Normalized<BPMNDI__BPMNShape> | undefined;
  const bpmnShapeIndex = (diagramElements ?? []).findIndex((d) => d["@_bpmnElement"] === __readonly_bpmnElementId);
  if (bpmnShapeIndex >= 0) {
    deletedBpmnShape = diagramElements[bpmnShapeIndex] as typeof deletedBpmnShape;
    diagramElements.splice(bpmnShapeIndex, 1);
  }

  return {
    deletedBpmnElement,
    deletedBpmnShape,
  };
}
