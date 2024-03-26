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

import { DmnBuiltInDataType, generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import {
  DC__Bounds,
  DMN15__tAuthorityRequirement,
  DMN15__tDecision,
  DMN15__tDefinitions,
  DMN15__tInformationRequirement,
  DMN15__tKnowledgeRequirement,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { EdgeType, NodeType } from "../diagram/connections/graphStructure";
import { AutoPositionedEdgeMarker } from "../diagram/edges/AutoPositionedEdgeMarker";
import { EDGE_TYPES } from "../diagram/edges/EdgeTypes";
import { getDmnBoundsCenterPoint } from "../diagram/maths/DmnMaths";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { NodeNature, nodeNatures } from "./NodeNature";
import { addOrGetDrd } from "./addOrGetDrd";
import { getCentralizedDecisionServiceDividerLine } from "./updateDecisionServiceDividerLine";
import { repopulateInputDataAndDecisionsOnAllDecisionServices } from "./repopulateInputDataAndDecisionsOnDecisionService";
import { buildXmlHref } from "../xml/xmlHrefs";

export function addConnectedNode({
  definitions,
  drdIndex,
  sourceNode,
  newNode,
  edgeType,
}: {
  definitions: DMN15__tDefinitions;
  drdIndex: number;
  sourceNode: { type: NodeType; href: string; bounds: DC__Bounds; shapeId: string | undefined };
  newNode: { type: NodeType; bounds: DC__Bounds };
  edgeType: EdgeType;
}) {
  const newDmnObjectId = generateUuid();
  const newDmnObjectHref = buildXmlHref({ id: newDmnObjectId });
  const newEdgeId = generateUuid();
  const nature = nodeNatures[newNode.type];

  if (nature === NodeNature.DRG_ELEMENT) {
    const requirements = getRequirementsFromEdge(sourceNode, newEdgeId, edgeType);

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
          "@_id": newDmnObjectId,
          ...requirements,
          variable: {
            ...variableBase,
            "@_name": "New BKM",
          },
        },
        [NODE_TYPES.decision]: {
          __$$element: "decision",
          "@_name": "New Decision",
          "@_id": newDmnObjectId,
          ...requirements,
          variable: {
            ...variableBase,
            "@_name": "New Decision",
          },
        },
        [NODE_TYPES.decisionService]: {
          __$$element: "decisionService",
          "@_name": "New Decision Service",
          "@_id": newDmnObjectId,
          ...requirements,
          variable: {
            ...variableBase,
            "@_name": "New Decision Service",
          },
        },
        [NODE_TYPES.inputData]: {
          __$$element: "inputData",
          "@_name": "New Input Data",
          "@_id": newDmnObjectId,
          ...requirements,
          variable: {
            ...variableBase,
            "@_name": "New Input Data",
          },
        },
        [NODE_TYPES.knowledgeSource]: {
          __$$element: "knowledgeSource",
          "@_name": "New Knowledge Source",
          "@_id": newDmnObjectId,
          ...requirements,
        },
      })
    );
  } else if (nature === NodeNature.ARTIFACT) {
    definitions.artifact ??= [];
    definitions.artifact?.push(
      ...switchExpression(newNode.type as Extract<NodeType, "node_group" | "node_textAnnotation">, {
        [NODE_TYPES.textAnnotation]: [
          {
            "@_id": newDmnObjectId,
            __$$element: "textAnnotation" as const,
            text: { __$$text: "New text annotation" },
          },
          {
            "@_id": newEdgeId,
            __$$element: "association" as const,
            "@_associationDirection": "Both" as const,
            sourceRef: { "@_href": `${sourceNode.href}` },
            targetRef: { "@_href": `${newDmnObjectHref}` },
          },
        ],
        [NODE_TYPES.group]: [
          {
            "@_id": newDmnObjectId,
            __$$element: "group" as const,
            "@_name": "New group",
          },
        ],
      })
    );
  } else {
    throw new Error(`DMN MUTATION: Unknown node usage '${nature}'.`);
  }

  const newShapeId = generateUuid();
  const { diagramElements } = addOrGetDrd({ definitions, drdIndex });
  // Add the new node shape
  diagramElements?.push({
    __$$element: "dmndi:DMNShape",
    "@_id": newShapeId,
    "@_dmnElementRef": newDmnObjectId,
    "@_isCollapsed": false,
    "@_isListedInputData": false,
    "dc:Bounds": newNode.bounds,
    ...(newNode.type === NODE_TYPES.decisionService
      ? { "dmndi:DMNDecisionServiceDividerLine": getCentralizedDecisionServiceDividerLine(newNode.bounds) }
      : {}),
  });

  // Add the new edge shape
  diagramElements?.push({
    __$$element: "dmndi:DMNEdge",
    "@_id": generateUuid() + AutoPositionedEdgeMarker.TARGET,
    "@_dmnElementRef": newEdgeId,
    "@_sourceElement": sourceNode.shapeId,
    "@_targetElement": newShapeId,
    "di:waypoint": [getDmnBoundsCenterPoint(sourceNode.bounds), getDmnBoundsCenterPoint(newNode.bounds)],
  });

  repopulateInputDataAndDecisionsOnAllDecisionServices({ definitions });

  return { id: newDmnObjectId, href: newDmnObjectHref };
}

export function getRequirementsFromEdge(
  sourceNode: { type: NodeType; href: string },
  newEdgeId: string,
  edge: EdgeType
) {
  const ir:
    | undefined //
    | Required<Pick<DMN15__tInformationRequirement, "requiredInput" | "@_id">>
    | Required<Pick<DMN15__tInformationRequirement, "requiredDecision" | "@_id">> = switchExpression(sourceNode.type, {
    [NODE_TYPES.inputData]: { "@_id": newEdgeId, requiredInput: { "@_href": `${sourceNode.href}` } },
    [NODE_TYPES.decision]: { "@_id": newEdgeId, requiredDecision: { "@_href": `${sourceNode.href}` } },
    default: undefined,
  });

  const kr:
    | undefined //
    | Required<Pick<DMN15__tKnowledgeRequirement, "requiredKnowledge" | "@_id">> = switchExpression(sourceNode.type, {
    [NODE_TYPES.bkm]: { "@_id": newEdgeId, requiredKnowledge: { "@_href": `${sourceNode.href}` } },
    [NODE_TYPES.decisionService]: { "@_id": newEdgeId, requiredKnowledge: { "@_href": `${sourceNode.href}` } },
    default: undefined,
  });

  const ar:
    | undefined //
    | Required<Pick<DMN15__tAuthorityRequirement, "requiredInput" | "@_id">>
    | Required<Pick<DMN15__tAuthorityRequirement, "requiredDecision" | "@_id">>
    | Required<Pick<DMN15__tAuthorityRequirement, "requiredAuthority" | "@_id">> = switchExpression(sourceNode.type, {
    [NODE_TYPES.inputData]: { "@_id": newEdgeId, requiredInput: { "@_href": `${sourceNode.href}` } },
    [NODE_TYPES.decision]: { "@_id": newEdgeId, requiredDecision: { "@_href": `${sourceNode.href}` } },
    [NODE_TYPES.knowledgeSource]: { "@_id": newEdgeId, requiredAuthority: { "@_href": `${sourceNode.href}` } },
    default: undefined,
  });

  // We can use tDecision to type here, because it contains all requirement types.
  const requirements:
    | (Pick<DMN15__tDecision, "informationRequirement"> &
        Pick<DMN15__tDecision, "knowledgeRequirement"> &
        Pick<DMN15__tDecision, "authorityRequirement">)
    | undefined = switchExpression(edge, {
    [EDGE_TYPES.informationRequirement]: ir ? { informationRequirement: [ir] } : undefined,
    [EDGE_TYPES.knowledgeRequirement]: kr ? { knowledgeRequirement: [kr] } : undefined,
    [EDGE_TYPES.authorityRequirement]: ar ? { authorityRequirement: [ar] } : undefined,
    default: undefined,
  });

  return requirements;
}
