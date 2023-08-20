import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { DC__Bounds, DMN14__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { NodeType } from "../diagram/connections/graphStructure";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import { NodeNature, nodeNatures } from "./NodeNature";
import { addOrGetDefaultDiagram as getDefaultDiagram } from "./addOrGetDefaultDiagram";

export function addStandaloneNode({
  definitions,
  newNode,
}: {
  definitions: DMN14__tDefinitions;
  newNode: { type: NodeType; bounds: DC__Bounds };
}) {
  const newNodeId = generateUuid();
  const nature = nodeNatures[newNode.type];

  if (nature === NodeNature.DRG_ELEMENT) {
    definitions.drgElement ??= [];
    definitions.drgElement?.push(
      switchExpression(newNode.type as Exclude<NodeType, "node_group" | "node_textAnnotation">, {
        [NODE_TYPES.bkm]: {
          __$$element: "businessKnowledgeModel",
          "@_name": "New BKM",
          "@_id": newNodeId,
        },
        [NODE_TYPES.decision]: {
          __$$element: "decision",
          "@_name": "New Decision",
          "@_id": newNodeId,
        },
        [NODE_TYPES.decisionService]: {
          __$$element: "decisionService",
          "@_name": "New Decision Service",
          "@_id": newNodeId,
        },
        [NODE_TYPES.inputData]: {
          __$$element: "inputData",
          "@_name": "New Input Data",
          "@_id": newNodeId,
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
            text: "New text annotation",
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
  const { diagramElements } = getDefaultDiagram({ definitions });
  diagramElements?.push({
    __$$element: "dmndi:DMNShape",
    "@_id": generateUuid(),
    "@_dmnElementRef": newNodeId,
    "@_isCollapsed": false,
    "@_isListedInputData": false,
    "dc:Bounds": newNode.bounds,
  });

  return newNodeId;
}
