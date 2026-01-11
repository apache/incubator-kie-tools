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

import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { Normalized } from "../normalization/normalize";
import { BPMN20__tDefinitions, BPMN20__tTask } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { BpmnDiagramNodeData, BpmnNodeType, elementToNodeType, NODE_TYPES } from "../diagram/BpmnDiagramDomain";
import { DC__Bounds } from "@kie-tools/xyflow-react-kie-diagram/dist/maths/model";
import { NodeNature, nodeNatures } from "./_NodeNature";
import { addOrGetProcessAndDiagramElements } from "./addOrGetProcessAndDiagramElements";
import { assertUnreachable } from "../ts-ext/assertUnreachable";

export function getNewNodeDefaultName({
  type,
  element,
}: {
  type: BpmnNodeType;
  element: keyof typeof elementToNodeType;
}) {
  switch (type) {
    case NODE_TYPES.subProcess:
      if (element === "transaction") {
        return "New Transaction";
      } else {
        return "New Sub-process";
      }
    case NODE_TYPES.startEvent:
    case NODE_TYPES.intermediateCatchEvent:
    case NODE_TYPES.intermediateThrowEvent:
    case NODE_TYPES.endEvent:
    case NODE_TYPES.gateway:
    case NODE_TYPES.group:
    case NODE_TYPES.textAnnotation:
    case NODE_TYPES.unknown:
      return undefined;
    case NODE_TYPES.dataObject:
      return "New Data Object";
    case NODE_TYPES.lane:
      return "New Lane";
    case NODE_TYPES.task:
      switch (element) {
        case "callActivity":
          return "New Call Activity";
        case "task":
          return "New Task";
        case "userTask":
          return "New User Task";
        case "businessRuleTask":
          return "New Business Rule Task";
        case "scriptTask":
          return "New Script Task";
        case "serviceTask":
          return "New Service Task";
        default:
          return undefined;
      }
    default:
      assertUnreachable(type);
  }
}

export function addStandaloneNode({
  definitions,
  __readonly_element,
  __readonly_newNode,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  __readonly_element: keyof typeof elementToNodeType;
  __readonly_newNode: { type: BpmnNodeType; bounds: DC__Bounds; data: undefined | BpmnDiagramNodeData };
}) {
  const newBpmnElementId = generateUuid();
  const nature = nodeNatures[__readonly_newNode.type];

  const { process, diagramElements } = addOrGetProcessAndDiagramElements({ definitions });

  if (nature === NodeNature.PROCESS_FLOW_ELEMENT) {
    process.flowElement ??= [];
    process.flowElement?.push(
      switchExpression(
        __readonly_newNode.type as Exclude<
          BpmnNodeType,
          "node_group" | "node_textAnnotation" | "node_unknown" | "node_lane" | "node_subProcess"
        >,
        {
          [NODE_TYPES.task]:
            __readonly_element === "callActivity"
              ? {
                  "@_id": newBpmnElementId,
                  __$$element: "callActivity",
                  "@_name": getNewNodeDefaultName({ type: __readonly_newNode.type, element: __readonly_element }),
                }
              : {
                  ...__readonly_newNode.data?.bpmnElement,
                  "@_id": newBpmnElementId,
                  __$$element: "task",
                  "@_name":
                    (__readonly_newNode.data?.bpmnElement as Normalized<BPMN20__tTask>)["@_name"] ??
                    getNewNodeDefaultName({ type: __readonly_newNode.type, element: __readonly_element }),
                },
          [NODE_TYPES.startEvent]: {
            "@_id": newBpmnElementId,
            __$$element: "startEvent",
          },
          [NODE_TYPES.intermediateCatchEvent]: {
            "@_id": newBpmnElementId,
            __$$element: "intermediateCatchEvent",
            eventDefinition: [
              {
                "@_id": generateUuid(),
                __$$element: "timerEventDefinition",
              },
            ],
          },
          [NODE_TYPES.intermediateThrowEvent]: {
            "@_id": newBpmnElementId,
            __$$element: "intermediateThrowEvent",
            eventDefinition: [
              {
                "@_id": generateUuid(),
                __$$element: "signalEventDefinition",
              },
            ],
          },
          [NODE_TYPES.endEvent]: {
            "@_id": newBpmnElementId,
            __$$element: "endEvent",
          },
          [NODE_TYPES.gateway]: {
            "@_id": newBpmnElementId,
            __$$element: "exclusiveGateway",
          },
          [NODE_TYPES.dataObject]: {
            "@_id": newBpmnElementId,
            "@_name": getNewNodeDefaultName({ type: __readonly_newNode.type, element: __readonly_element }),
            __$$element: "dataObject",
          },
        }
      )
    );
  } else if (nature === NodeNature.ARTIFACT) {
    process.artifact ??= [];
    process.artifact?.push(
      ...switchExpression(__readonly_newNode.type as Extract<BpmnNodeType, "node_group" | "node_textAnnotation">, {
        [NODE_TYPES.textAnnotation]: [
          {
            "@_id": newBpmnElementId,
            __$$element: "textAnnotation" as const,
            text: { __$$text: "" },
          },
        ],
        [NODE_TYPES.group]: [
          {
            "@_id": newBpmnElementId,
            __$$element: "group" as const,
          },
        ],
      })
    );
  } else if (nature === NodeNature.CONTAINER) {
    process.flowElement ??= [];
    process.flowElement.push(
      __readonly_element === "transaction"
        ? {
            "@_id": newBpmnElementId,
            "@_name": getNewNodeDefaultName({ type: __readonly_newNode.type, element: __readonly_element }),
            __$$element: "transaction",
          }
        : {
            "@_id": newBpmnElementId,
            "@_name": getNewNodeDefaultName({ type: __readonly_newNode.type, element: __readonly_element }),
            __$$element: "subProcess",
          }
    );
  } else if (nature === NodeNature.LANE) {
    process.laneSet ??= [{ "@_id": generateUuid() }];
    process.laneSet[0].lane ??= [];
    process.laneSet[0].lane.push({
      "@_id": newBpmnElementId,
      "@_name": getNewNodeDefaultName({ type: __readonly_newNode.type, element: __readonly_element }),
    });
  }
  //
  else {
    throw new Error(`Unknown node nature '${nature}'.`);
  }

  // Add the new node shape
  const shapeId = generateUuid();
  diagramElements?.push({
    __$$element: "bpmndi:BPMNShape",
    "@_id": shapeId,
    "@_bpmnElement": newBpmnElementId,
    "dc:Bounds": __readonly_newNode.bounds,
  });

  return { id: newBpmnElementId, shapeId };
}
