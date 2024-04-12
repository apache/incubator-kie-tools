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
import { DmnBuiltInDataType, generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { DC__Bounds, DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { NodeType } from "../diagram/connections/graphStructure";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import { NodeNature, nodeNatures } from "./NodeNature";
import { addOrGetDrd as getDefaultDiagram } from "./addOrGetDrd";
import { getCentralizedDecisionServiceDividerLine } from "./updateDecisionServiceDividerLine";
import { repopulateInputDataAndDecisionsOnAllDecisionServices } from "./repopulateInputDataAndDecisionsOnDecisionService";
import { buildXmlHref } from "../xml/xmlHrefs";

export function addStandaloneNode({
  definitions,
  drdIndex,
  newNode,
}: {
  definitions: DMN15__tDefinitions;
  drdIndex: number;
  newNode: { type: NodeType; bounds: DC__Bounds };
}) {
  const newNodeId = generateUuid();
  const nature = nodeNatures[newNode.type];

  if (nature === NodeNature.DRG_ELEMENT) {
    definitions.drgElement ??= [];
    const variableBase = {
      "@_id": generateUuid(),
      "@_typeRef": DmnBuiltInDataType.Undefined,
    };
    definitions.drgElement?.push(
      switchExpression(newNode.type as Exclude<NodeType, "node_group" | "node_textAnnotation" | "node_unknown">, {
        [NODE_TYPES.bkm]: {
          __$$element: "businessKnowledgeModel",
          "@_name": "New BKM",
          "@_id": newNodeId,
          variable: {
            "@_name": "New BKM",
            ...variableBase,
          },
        },
        [NODE_TYPES.decision]: {
          __$$element: "decision",
          "@_name": "New Decision",
          "@_id": newNodeId,
          variable: {
            "@_name": "New Decision",
            ...variableBase,
          },
        },
        [NODE_TYPES.decisionService]: {
          __$$element: "decisionService",
          "@_name": "New Decision Service",
          "@_id": newNodeId,
          variable: {
            "@_name": "New Decision Service",
            ...variableBase,
          },
        },
        [NODE_TYPES.inputData]: {
          __$$element: "inputData",
          "@_name": "New Input Data",
          "@_id": newNodeId,
          variable: {
            "@_name": "New Input Data",
            ...variableBase,
          },
        },
        [NODE_TYPES.knowledgeSource]: {
          __$$element: "knowledgeSource",
          "@_name": "New Knowledge Source",
          "@_id": newNodeId,
        },
      })
    );
  } else if (nature === NodeNature.ARTIFACT) {
    definitions.artifact ??= [];
    definitions.artifact?.push(
      ...switchExpression(newNode.type as Extract<NodeType, "node_group" | "node_textAnnotation">, {
        [NODE_TYPES.textAnnotation]: [
          {
            "@_id": newNodeId,
            __$$element: "textAnnotation" as const,
            text: { __$$text: "New text annotation" },
          },
        ],
        [NODE_TYPES.group]: [
          {
            "@_id": newNodeId,
            __$$element: "group" as const,
            "@_name": "New group",
          },
        ],
      })
    );
  } else {
    throw new Error(`Unknown node usage '${nature}'.`);
  }

  // Add the new node shape
  const { diagramElements } = getDefaultDiagram({ definitions, drdIndex });
  const shapeId = generateUuid();
  diagramElements?.push({
    __$$element: "dmndi:DMNShape",
    "@_id": shapeId,
    "@_dmnElementRef": newNodeId,
    "@_isCollapsed": false,
    "@_isListedInputData": false,
    "dc:Bounds": newNode.bounds,
    ...(newNode.type === NODE_TYPES.decisionService
      ? { "dmndi:DMNDecisionServiceDividerLine": getCentralizedDecisionServiceDividerLine(newNode.bounds) }
      : {}),
  });

  repopulateInputDataAndDecisionsOnAllDecisionServices({ definitions });

  return { id: newNodeId, href: buildXmlHref({ id: newNodeId }), shapeId };
}
