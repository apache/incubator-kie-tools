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
import { ElementFilter } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import { Normalized } from "../normalization/normalize";
import { State } from "../store/Store";
import { addOrGetProcessAndDiagramElements } from "./addOrGetProcessAndDiagramElements";
import { FoundElement, visitFlowElementsAndArtifacts } from "./_elementVisitor";

export function detachBoundaryEvent({
  definitions,
  __readonly_eventId,
}: {
  definitions: State["bpmn"]["model"]["definitions"];
  __readonly_eventId: string | undefined;
}) {
  const { process } = addOrGetProcessAndDiagramElements({ definitions });

  if (__readonly_eventId === undefined) {
    throw new Error("Event needs to have an ID.");
  }

  let foundBoundaryEvent:
    | undefined
    | FoundElement<Normalized<ElementFilter<Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>, "boundaryEvent">>>;

  let foundActivity:
    | undefined
    | FoundElement<
        Normalized<
          ElementFilter<
            Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>,
            | "task"
            | "businessRuleTask"
            | "userTask"
            | "scriptTask"
            | "serviceTask"
            | "callActivity"
            | "subProcess"
            | "adHocSubProcess"
            | "transaction"
          >
        >
      >;

  visitFlowElementsAndArtifacts(process, ({ element, ...args }) => {
    if (element["@_id"] === __readonly_eventId) {
      if (element.__$$element === "boundaryEvent") {
        foundBoundaryEvent = { element, ...args };
        return false; // Will stop visiting.
      } else {
        throw new Error("Provided id is not associated with a Boundary Event.");
      }
    }
  });

  if (!foundBoundaryEvent) {
    throw new Error("Boundary Event not found. Aborting.");
  }

  visitFlowElementsAndArtifacts(process, ({ element, ...args }) => {
    if (element["@_id"] === foundBoundaryEvent?.element["@_attachedToRef"]) {
      if (
        element.__$$element === "task" ||
        element.__$$element === "businessRuleTask" ||
        element.__$$element === "userTask" ||
        element.__$$element === "scriptTask" ||
        element.__$$element === "serviceTask" ||
        element.__$$element === "subProcess" ||
        element.__$$element === "callActivity" ||
        element.__$$element === "adHocSubProcess" ||
        element.__$$element === "transaction"
      ) {
        foundActivity = { element, ...args };
        return false; // Will stop visiting.
      } else {
        throw new Error("'attachedToRef' is not associated with an Activity.");
      }
    }
  });

  if (!foundActivity) {
    throw new Error("Target Activity not found. Aborting.");
  }

  foundBoundaryEvent.owner.flowElement?.splice(foundBoundaryEvent.index, 1);

  process.flowElement?.push({
    "@_id": foundBoundaryEvent.element["@_id"],
    __$$element: "intermediateCatchEvent",
    eventDefinition: foundBoundaryEvent.element.eventDefinition,
  });
}
