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

import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import { Normalized } from "../normalization/normalize";
import { State } from "../store/Store";
import { addOrGetProcessAndDiagramElements } from "./addOrGetProcessAndDiagramElements";
import { ElementExclusion } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { BPMN20__tProcess } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { isSubProcessElement, findSubProcessRecursively, shouldMoveSequenceFlow } from "./moveNodesOutOfSubProcess";

export function moveNodesInsideSubProcess({
  definitions,
  __readonly_subProcessId,
  __readonly_nodeIds,
}: {
  definitions: State["bpmn"]["model"]["definitions"];
  __readonly_subProcessId: string | undefined;
  __readonly_nodeIds: string[];
}) {
  const { process } = addOrGetProcessAndDiagramElements({ definitions });

  const subProcess = findSubProcessRecursively(process.flowElement ?? [], __readonly_subProcessId ?? "");
  if (!subProcess) {
    throw new Error(`Cannot find subprocess with ID: ${__readonly_subProcessId}`);
  }

  const flowElementsToMove: Normalized<Unpacked<Normalized<BPMN20__tProcess>["flowElement"]>>[] = [];
  const artifactsToMove: Normalized<
    ElementExclusion<Unpacked<Normalized<BPMN20__tProcess>["artifact"]>, "association">
  >[] = [];

  const nodeIdsToMoveInside = new Set(__readonly_nodeIds);
  const subProcessNodes = new Set<string>();
  subProcess.flowElement?.forEach((flowElement: Normalized<Unpacked<typeof subProcess.flowElement>>) => {
    if (flowElement.__$$element !== "sequenceFlow") {
      subProcessNodes.add(flowElement["@_id"]);
    }
  });

  const toMove: Normalized<Unpacked<Normalized<BPMN20__tProcess>["flowElement"]>>[] = [];

  const collectElements = (flowElements: Normalized<BPMN20__tProcess>["flowElement"]): void => {
    if (!flowElements) {
      return;
    }
    for (let i = flowElements.length - 1; i >= 0; i--) {
      const flowElement = flowElements[i];
      if (
        nodeIdsToMoveInside.has(flowElement["@_id"]) ||
        (flowElement.__$$element === "boundaryEvent" &&
          flowElement["@_attachedToRef"] &&
          nodeIdsToMoveInside.has(flowElement["@_attachedToRef"]))
      ) {
        toMove.push(...flowElements.splice(i, 1));
      } else if (shouldMoveSequenceFlow(flowElement, nodeIdsToMoveInside, subProcessNodes)) {
        // If the source and target are both outside of the sub-process
        // or if the source and target is already in the sub process the sequenceFlow must be copied
        toMove.push(...flowElements.splice(i, 1));
      } else if (isSubProcessElement(flowElement) && flowElement.flowElement) {
        collectElements(flowElement.flowElement);
      }
    }
  };

  collectElements(process.flowElement ?? []);
  flowElementsToMove.push(...toMove);

  for (let i = 0; i < (process.artifact ?? []).length; i++) {
    const artifact = (process.artifact ?? [])[i];
    if (artifact.__$$element !== "association" && nodeIdsToMoveInside.has(artifact["@_id"])) {
      const spliced = process.artifact?.splice(i, 1) ?? [];
      artifactsToMove.push(...spliced.filter((a) => a.__$$element !== "association"));
      i--; // repeat one index because we just altered the array we're iterating over.
    }
  }

  // BPMN 2.0 Spec: Regular embedded sub-processes can only have "None" Start Events
  // If moving a Start Event with event definitions into a regular (non-event) sub-process,
  // automatically convert it to a "None" Start Event
  // Event Sub-Processes keep existing event definitions (user can morph via UI if needed)
  const isEventSubProcess = subProcess["@_triggeredByEvent"] ?? false;
  if (!isEventSubProcess) {
    for (const flowElement of flowElementsToMove) {
      if (flowElement.__$$element === "startEvent" && flowElement.eventDefinition) {
        flowElement.eventDefinition = undefined;
      }
    }
  }

  subProcess.flowElement ??= [];
  subProcess.flowElement.push(...flowElementsToMove);
  subProcess.artifact ??= [];
  subProcess.artifact.push(...artifactsToMove);
}
