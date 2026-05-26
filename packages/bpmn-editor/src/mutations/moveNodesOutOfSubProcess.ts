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

import { BPMN20__tProcess } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { ElementExclusion } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import { Normalized } from "../normalization/normalize";
import { State } from "../store/Store";
import { addOrGetProcessAndDiagramElements } from "./addOrGetProcessAndDiagramElements";

export type SubProcessElement = Normalized<
  Extract<
    Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>,
    { __$$element: "subProcess" | "adHocSubProcess" | "transaction" }
  >
>;

export function isSubProcessElement(element: { __$$element?: string }): element is SubProcessElement {
  return (
    element.__$$element === "subProcess" ||
    element.__$$element === "adHocSubProcess" ||
    element.__$$element === "transaction"
  );
}

export function findSubProcessRecursively(
  flowElements: NonNullable<BPMN20__tProcess["flowElement"]>,
  subProcessId: string
): SubProcessElement | undefined {
  for (const element of flowElements) {
    if (element["@_id"] === subProcessId && isSubProcessElement(element)) {
      return element;
    }
    if (isSubProcessElement(element) && element.flowElement) {
      const found = findSubProcessRecursively(element.flowElement, subProcessId);
      if (found) {
        return found;
      }
    }
  }
  return undefined;
}

function findParentFlowElements(
  flowElements: NonNullable<BPMN20__tProcess["flowElement"]>,
  subProcessId: string
): NonNullable<BPMN20__tProcess["flowElement"]> | undefined {
  for (const element of flowElements) {
    if (element["@_id"] === subProcessId) {
      return flowElements;
    }
    if (isSubProcessElement(element) && element.flowElement) {
      const found = findParentFlowElements(element.flowElement, subProcessId);
      if (found) {
        return found;
      }
    }
  }
  return undefined;
}

function findParentSubProcess(
  flowElements: NonNullable<BPMN20__tProcess["flowElement"]>,
  subProcessId: string
): SubProcessElement | undefined {
  for (const element of flowElements) {
    if (isSubProcessElement(element) && element.flowElement) {
      // Check if this subprocess directly contains the target
      for (const child of element.flowElement) {
        if (child["@_id"] === subProcessId) {
          return element;
        }
      }
      // Recurse into nested subprocesses
      const found = findParentSubProcess(element.flowElement, subProcessId);
      if (found) {
        return found;
      }
    }
  }
  return undefined;
}

export function shouldMoveSequenceFlow(
  flowElement: Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>,
  nodeIds: Set<string>,
  existingNodesIds: Set<string>
): boolean {
  return (
    flowElement.__$$element === "sequenceFlow" &&
    !!flowElement["@_sourceRef"] &&
    !!flowElement["@_targetRef"] &&
    ((nodeIds.has(flowElement["@_sourceRef"]) && nodeIds.has(flowElement["@_targetRef"])) ||
      (existingNodesIds.has(flowElement["@_sourceRef"]) && nodeIds.has(flowElement["@_targetRef"])) ||
      (nodeIds.has(flowElement["@_sourceRef"]) && existingNodesIds.has(flowElement["@_targetRef"])))
  );
}

export function moveNodesOutOfSubProcess({
  definitions,
  __readonly_subProcessId,
  __readonly_nodeIds,
  __readonly_targetSubProcessId,
}: {
  definitions: State["bpmn"]["model"]["definitions"];
  __readonly_subProcessId: string | undefined;
  __readonly_nodeIds: string[];
  __readonly_targetSubProcessId?: string | null;
}) {
  const { process } = addOrGetProcessAndDiagramElements({ definitions });

  const subProcess = findSubProcessRecursively(process.flowElement ?? [], __readonly_subProcessId ?? "");
  if (!subProcess) {
    throw new Error(`Cannot find subprocess with ID: ${__readonly_subProcessId}`);
  }

  let parentFlowElements: NonNullable<BPMN20__tProcess["flowElement"]>;
  let targetSubProcess: SubProcessElement | undefined;

  if (__readonly_targetSubProcessId) {
    targetSubProcess = findSubProcessRecursively(process.flowElement ?? [], __readonly_targetSubProcessId);
    if (!targetSubProcess) {
      throw new Error(`Cannot find target subprocess with ID: ${__readonly_targetSubProcessId}`);
    }
    targetSubProcess.flowElement ??= [];
    parentFlowElements = targetSubProcess.flowElement;
  } else if (__readonly_targetSubProcessId === null) {
    process.flowElement ??= [];
    parentFlowElements = process.flowElement;
  } else {
    parentFlowElements =
      findParentFlowElements(process.flowElement ?? [], __readonly_subProcessId ?? "") ?? process.flowElement ?? [];
  }

  const flowElementsToMove: Normalized<Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>>[] = [];
  const artifactsToMove: Normalized<
    ElementExclusion<Unpacked<NonNullable<BPMN20__tProcess["artifact"]>>, "association">
  >[] = [];

  const nodeIdsToMoveOut = new Set(__readonly_nodeIds);

  const subProcessNodes = new Set<string>();
  const collectSubProcessNodeIds = (flowElements: NonNullable<BPMN20__tProcess["flowElement"]>): void => {
    for (const flowElement of flowElements) {
      if (flowElement.__$$element !== "sequenceFlow" && flowElement["@_id"]) {
        subProcessNodes.add(flowElement["@_id"]);
      }
      if (isSubProcessElement(flowElement) && flowElement.flowElement) {
        collectSubProcessNodeIds(flowElement.flowElement);
      }
    }
  };
  collectSubProcessNodeIds(subProcess.flowElement ?? []);

  const isEventSubProcess = subProcess.__$$element === "subProcess" && (subProcess["@_triggeredByEvent"] ?? false);

  for (let i = (subProcess.flowElement ?? []).length - 1; i >= 0; i--) {
    const flowElement = subProcess.flowElement![i];
    if (
      (flowElement["@_id"] && nodeIdsToMoveOut.has(flowElement["@_id"])) ||
      (flowElement.__$$element === "boundaryEvent" &&
        flowElement["@_attachedToRef"] &&
        nodeIdsToMoveOut.has(flowElement["@_attachedToRef"]))
    ) {
      flowElementsToMove.push(...subProcess.flowElement!.splice(i, 1));
    } else if (shouldMoveSequenceFlow(flowElement, nodeIdsToMoveOut, subProcessNodes)) {
      // If the source and target are both outside of the sub-process
      // or if the source and target is already in the sub process the sequenceFlow must be copied

      flowElementsToMove.push(...subProcess.flowElement!.splice(i, 1));
    }
  }

  // BPMN 2.0 Spec: When moving Start Events from Event Sub-Process to top level,
  // only convert event types that are NOT supported at top level
  // Top-level allows: None, Message, Timer, Conditional, Signal
  // Top-level does NOT allow: Error, Escalation, Compensation, Link, Terminate
  if (isEventSubProcess) {
    for (const flowElement of flowElementsToMove) {
      if (
        flowElement.__$$element === "startEvent" &&
        flowElement.eventDefinition &&
        flowElement.eventDefinition.length > 0
      ) {
        const eventDefType = flowElement.eventDefinition[0].__$$element;
        const unsupportedAtTopLevel =
          eventDefType === "errorEventDefinition" ||
          eventDefType === "escalationEventDefinition" ||
          eventDefType === "compensateEventDefinition" ||
          eventDefType === "linkEventDefinition" ||
          eventDefType === "terminateEventDefinition";

        if (unsupportedAtTopLevel) {
          // Convert to "None" Start Event because this event type is not allowed at top level
          flowElement.eventDefinition = undefined;
        }
        // Otherwise keep the event definition (Message, Timer, Conditional, Signal are valid at top level)
      }
    }
  }

  for (let i = 0; i < (subProcess.artifact ?? []).length; i++) {
    const artifact = (subProcess.artifact ?? [])[i];
    if (artifact.__$$element !== "association" && nodeIdsToMoveOut.has(artifact["@_id"])) {
      const spliced = subProcess.artifact?.splice(i, 1) ?? [];
      artifactsToMove.push(...spliced.filter((a) => a.__$$element !== "association"));
      i--; // repeat one index because we just altered the array we're iterating over.
    }
  }

  parentFlowElements.push(...flowElementsToMove);

  if (__readonly_targetSubProcessId && targetSubProcess) {
    targetSubProcess.artifact ??= [];
    targetSubProcess.artifact.push(...artifactsToMove);
  } else if (__readonly_targetSubProcessId === null) {
    process.artifact ??= [];
    process.artifact.push(...artifactsToMove);
  } else {
    const parentSubProcess = findParentSubProcess(process.flowElement ?? [], __readonly_subProcessId ?? "");
    if (parentSubProcess) {
      parentSubProcess.artifact ??= [];
      parentSubProcess.artifact.push(...artifactsToMove);
    } else {
      process.artifact ??= [];
      process.artifact.push(...artifactsToMove);
    }
  }
}
