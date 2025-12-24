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
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { visitFlowElementsAndArtifacts } from "../../../mutations/_elementVisitor";
import { addOrGetProcessAndDiagramElements } from "../../../mutations/addOrGetProcessAndDiagramElements";
import { Normalized } from "../../../normalization/normalize";
import { useBpmnEditorStoreApi } from "../../../store/StoreContext";
import { EndEventIcon, EventDefinitionIcon, IntermediateThrowEventIcon, StartEventIcon } from "../NodeIcons";
import { BPMN20__tProcess } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { ElementFilter } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { NODE_COLORS } from "../NodeSvgs";

export type Event = Normalized<
  ElementFilter<
    Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>,
    "startEvent" | "endEvent" | "boundaryEvent" | "intermediateCatchEvent" | "intermediateThrowEvent"
  >
>;

export function useEventNodeMorphingActions(event: Event) {
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  const foregroundColor = NODE_COLORS[event.__$$element].foreground;
  const backgroundColor = NODE_COLORS[event.__$$element].background;

  const filled = event.__$$element === "intermediateThrowEvent" || event.__$$element === "endEvent";

  const morphEvent = useCallback(
    (eventDefinitionElement: undefined | Unpacked<Event["eventDefinition"]>["__$$element"]) => {
      // 1 - None
      // 2 - Message
      // 3 - Timer
      // 4 - Error
      // 5 - Escalation
      // 6 - Compensation
      // 7 - Conditional
      // 8 - Link
      // 9 - Signal
      // 0 - Terminate
      bpmnEditorStoreApi.setState((s) => {
        const { process } = addOrGetProcessAndDiagramElements({
          definitions: s.bpmn.model.definitions,
        });
        visitFlowElementsAndArtifacts(process, ({ array, index, owner, element }) => {
          if (element["@_id"] === event["@_id"] && element.__$$element === event.__$$element) {
            if (eventDefinitionElement === undefined) {
              element.eventDefinition = undefined;
              return false;
            }

            element.eventDefinition ??= [];
            switch (eventDefinitionElement) {
              case "compensateEventDefinition":
                element.eventDefinition[0] = {
                  "@_id": generateUuid(),
                  __$$element: "compensateEventDefinition",
                };
                break;
              case "conditionalEventDefinition":
                element.eventDefinition[0] = {
                  "@_id": generateUuid(),
                  __$$element: "conditionalEventDefinition",
                  condition: {
                    "@_id": generateUuid(),
                  },
                };
                break;
              case "errorEventDefinition":
                element.eventDefinition[0] = {
                  "@_id": generateUuid(),
                  __$$element: "errorEventDefinition",
                };
                break;
              case "escalationEventDefinition":
                element.eventDefinition[0] = {
                  "@_id": generateUuid(),
                  __$$element: "escalationEventDefinition",
                };
                break;
              case "linkEventDefinition":
                element.eventDefinition[0] = {
                  "@_id": generateUuid(),
                  __$$element: "linkEventDefinition",
                  "@_name": "",
                };
                break;
              case "messageEventDefinition":
                element.eventDefinition[0] = {
                  "@_id": generateUuid(),
                  __$$element: "messageEventDefinition",
                };
                break;
              case "signalEventDefinition":
                element.eventDefinition[0] = {
                  "@_id": generateUuid(),
                  __$$element: "signalEventDefinition",
                };
                break;
              case "terminateEventDefinition":
                element.eventDefinition[0] = {
                  "@_id": generateUuid(),
                  __$$element: "terminateEventDefinition",
                };
                break;
              case "timerEventDefinition":
                element.eventDefinition[0] = {
                  "@_id": generateUuid(),
                  __$$element: "timerEventDefinition",
                };
                break;
            }
            element.eventDefinition[0] = {
              "@_id": generateUuid(),
              __$$element: eventDefinitionElement as any,
            };
            return false; // Will stop visiting.
          }
        });
      });
    },
    [bpmnEditorStoreApi, event]
  );

  const morphingActions = useMemo(() => {
    return [
      {
        icon:
          event.__$$element === "startEvent" ? (
            <StartEventIcon variant={undefined} />
          ) : event.__$$element === "intermediateThrowEvent" ? (
            <IntermediateThrowEventIcon variant={undefined} />
          ) : event.__$$element === "endEvent" ? (
            <EndEventIcon variant={undefined} />
          ) : (
            <></>
          ),
        key: "1",
        title: "None",
        id: "none",
        action: () => morphEvent(undefined),
      } as const, // none
      {
        icon: (
          <EventDefinitionIcon
            stroke={foregroundColor}
            filled={filled}
            fill={backgroundColor}
            variant={"messageEventDefinition"}
          />
        ),
        key: "2",
        title: "Message",
        id: "messageEventDefinition",
        action: () => morphEvent("messageEventDefinition"),
      } as const,
      {
        icon: <EventDefinitionIcon stroke={foregroundColor} filled={filled} variant={"timerEventDefinition"} />,
        key: "3",
        title: "Timer",
        id: "timerEventDefinition",
        action: () => morphEvent("timerEventDefinition"),
      } as const,
      {
        icon: <EventDefinitionIcon stroke={foregroundColor} filled={filled} variant={"errorEventDefinition"} />,
        key: "4",
        title: "Error",
        id: "errorEventDefinition",
        action: () => morphEvent("errorEventDefinition"),
      } as const,
      {
        icon: <EventDefinitionIcon stroke={foregroundColor} filled={filled} variant={"escalationEventDefinition"} />,
        key: "5",
        title: "Escalation",
        id: "escalationEventDefinition",
        action: () => morphEvent("escalationEventDefinition"),
      } as const,
      {
        icon: <EventDefinitionIcon stroke={foregroundColor} filled={filled} variant={"compensateEventDefinition"} />,
        key: "6",
        title: "Compensation",
        id: "compensateEventDefinition",
        action: () => morphEvent("compensateEventDefinition"),
      } as const,
      {
        icon: <EventDefinitionIcon stroke={foregroundColor} filled={filled} variant={"conditionalEventDefinition"} />,
        key: "7",
        title: "Conditional",
        id: "conditionalEventDefinition",
        action: () => morphEvent("conditionalEventDefinition"),
      } as const,
      {
        icon: <EventDefinitionIcon stroke={foregroundColor} filled={filled} variant={"linkEventDefinition"} />,
        key: "8",
        title: "Link",
        id: "linkEventDefinition",
        action: () => morphEvent("linkEventDefinition"),
      } as const,
      {
        icon: <EventDefinitionIcon stroke={foregroundColor} filled={filled} variant={"signalEventDefinition"} />,
        key: "9",
        title: "Signal",
        id: "signalEventDefinition",
        action: () => morphEvent("signalEventDefinition"),
      } as const,
      {
        icon: <EndEventIcon variant={"terminateEventDefinition"} />,
        key: "0",
        title: "Terminate",
        id: "terminateEventDefinition",
        action: () => morphEvent("terminateEventDefinition"),
      } as const,
    ];
  }, [event.__$$element, foregroundColor, filled, backgroundColor, morphEvent]);

  return morphingActions;
}
