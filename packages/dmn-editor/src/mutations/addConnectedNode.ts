import { DmnBuiltInDataType, generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import {
  DC__Bounds,
  DMN14__tAuthorityRequirement,
  DMN14__tDecision,
  DMN14__tDefinitions,
  DMN14__tInformationRequirement,
  DMN14__tKnowledgeRequirement,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { EdgeType, NodeType } from "../diagram/connections/graphStructure";
import { AutoPositionedEdgeMarker } from "../diagram/edges/AutoPositionedEdgeMarker";
import { EDGE_TYPES } from "../diagram/edges/EdgeTypes";
import { getBoundsCenterPoint } from "../diagram/maths/DmnMaths";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { NodeNature, nodeNatures } from "./NodeNature";
import { addOrGetDefaultDiagram } from "./addOrGetDefaultDiagram";

export function addConnectedNode({
  definitions,
  sourceNode,
  newNode,
  edge,
}: {
  definitions: DMN14__tDefinitions;
  sourceNode: { type: NodeType; id: string; bounds: DC__Bounds; shapeId: string | undefined };
  newNode: { type: NodeType; bounds: DC__Bounds };
  edge: EdgeType;
}) {
  const newNodeId = generateUuid();
  const newEdgeId = generateUuid();
  const nature = nodeNatures[newNode.type];

  if (nature === NodeNature.DRG_ELEMENT) {
    const requirements = getRequirementsFromEdge(sourceNode, newEdgeId, edge);

    definitions.drgElement ??= [];
    const variableBase = {
      "@_id": generateUuid(),
      "@_typeRef": DmnBuiltInDataType.Undefined,
    };
    definitions.drgElement?.push(
      switchExpression(newNode.type as Exclude<NodeType, "node_group" | "node_textAnnotation">, {
        [NODE_TYPES.bkm]: {
          __$$element: "businessKnowledgeModel",
          "@_name": "New BKM",
          "@_id": newNodeId,
          ...requirements,
          variable: {
            ...variableBase,
            "@_name": "New BKM",
          },
        },
        [NODE_TYPES.decision]: {
          __$$element: "decision",
          "@_name": "New Decision",
          "@_id": newNodeId,
          ...requirements,
          variable: {
            ...variableBase,
            "@_name": "New Decision",
          },
        },
        [NODE_TYPES.decisionService]: {
          __$$element: "decisionService",
          "@_name": "New Decision Service",
          "@_id": newNodeId,
          ...requirements,
          variable: {
            ...variableBase,
            "@_name": "New Decision Service",
          },
        },
        [NODE_TYPES.inputData]: {
          __$$element: "inputData",
          "@_name": "New Input Data",
          "@_id": newNodeId,
          ...requirements,
          variable: {
            ...variableBase,
            "@_name": "New Input Data",
          },
        },
        [NODE_TYPES.knowledgeSource]: {
          __$$element: "knowledgeSource",
          "@_name": "New Knowledge Source",
          "@_id": newNodeId,
          ...requirements,
          variable: {
            ...variableBase,
            "@_name": "New Knowledge Source",
          },
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
            text: "New text annotation",
          },
          {
            "@_id": newEdgeId,
            __$$element: "association" as const,
            "@_associationDirection": "Both" as const,
            sourceRef: { "@_href": `#${sourceNode.id}` },
            targetRef: { "@_href": `#${newNodeId}` },
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

  const newShapeId = generateUuid();
  const { diagramElements } = addOrGetDefaultDiagram({ definitions });
  // Add the new node shape
  diagramElements?.push({
    __$$element: "dmndi:DMNShape",
    "@_id": newShapeId,
    "@_dmnElementRef": newNodeId,
    "@_isCollapsed": false,
    "@_isListedInputData": false,
    "dc:Bounds": newNode.bounds,
  });

  // Add the new edge shape
  diagramElements?.push({
    __$$element: "dmndi:DMNEdge",
    "@_id": generateUuid() + AutoPositionedEdgeMarker.TARGET,
    "@_dmnElementRef": newEdgeId,
    "@_sourceElement": sourceNode.shapeId,
    "@_targetElement": newShapeId,
    "di:waypoint": [getBoundsCenterPoint(sourceNode.bounds), getBoundsCenterPoint(newNode.bounds)],
  });

  return newNodeId;
}

export function getRequirementsFromEdge(sourceNode: { type: NodeType; id: string }, newEdgeId: string, edge: EdgeType) {
  const ir:
    | undefined //
    | Required<Pick<DMN14__tInformationRequirement, "requiredInput" | "@_id">>
    | Required<Pick<DMN14__tInformationRequirement, "requiredDecision" | "@_id">> = switchExpression(sourceNode.type, {
    [NODE_TYPES.inputData]: { "@_id": newEdgeId, requiredInput: { "@_href": `#${sourceNode.id}` } },
    [NODE_TYPES.decision]: { "@_id": newEdgeId, requiredDecision: { "@_href": `#${sourceNode.id}` } },
    default: undefined,
  });

  const kr:
    | undefined //
    | Required<Pick<DMN14__tKnowledgeRequirement, "requiredKnowledge" | "@_id">> = switchExpression(sourceNode.type, {
    [NODE_TYPES.bkm]: { "@_id": newEdgeId, requiredKnowledge: { "@_href": `#${sourceNode.id}` } },
    [NODE_TYPES.decisionService]: { "@_id": newEdgeId, requiredKnowledge: { "@_href": `#${sourceNode.id}` } },
    default: undefined,
  });

  const ar:
    | undefined //
    | Required<Pick<DMN14__tAuthorityRequirement, "requiredInput" | "@_id">>
    | Required<Pick<DMN14__tAuthorityRequirement, "requiredDecision" | "@_id">>
    | Required<Pick<DMN14__tAuthorityRequirement, "requiredAuthority" | "@_id">> = switchExpression(sourceNode.type, {
    [NODE_TYPES.inputData]: { "@_id": newEdgeId, requiredInput: { "@_href": `#${sourceNode.id}` } },
    [NODE_TYPES.decision]: { "@_id": newEdgeId, requiredDecision: { "@_href": `#${sourceNode.id}` } },
    [NODE_TYPES.knowledgeSource]: { "@_id": newEdgeId, requiredAuthority: { "@_href": `#${sourceNode.id}` } },
    default: undefined,
  });

  // We can use tDecision to type here, because it contains all requirement types.
  const requirements:
    | (Pick<DMN14__tDecision, "informationRequirement"> &
        Pick<DMN14__tDecision, "knowledgeRequirement"> &
        Pick<DMN14__tDecision, "authorityRequirement">)
    | undefined = switchExpression(edge, {
    [EDGE_TYPES.informationRequirement]: ir ? { informationRequirement: [ir] } : undefined,
    [EDGE_TYPES.knowledgeRequirement]: kr ? { knowledgeRequirement: [kr] } : undefined,
    [EDGE_TYPES.authorityRequirement]: ar ? { authorityRequirement: [ar] } : undefined,
    default: undefined,
  });

  return requirements;
}
