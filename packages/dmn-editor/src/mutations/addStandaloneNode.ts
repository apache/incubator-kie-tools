import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { DC__Bounds } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { NodeType } from "../diagram/connections/graphStructure";
import { Dispatch } from "../store/Store";
import { switchExpression } from "../switchExpression/switchExpression";
import { NodeNature, nodeNatures } from "./types";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";

export function addStandaloneNode({
  newNode,
  dispatch: { dmn },
}: {
  dispatch: { dmn: Dispatch["dmn"] };
  newNode: { type: NodeType; bounds: DC__Bounds };
}) {
  const newNodeId = generateUuid();
  const nature = nodeNatures[newNode.type];

  dmn.set(({ definitions }) => {
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
    definitions["dmndi:DMNDI"] ??= {};
    definitions["dmndi:DMNDI"]["dmndi:DMNDiagram"] ??= [];
    definitions["dmndi:DMNDI"]["dmndi:DMNDiagram"][0] ??= {};
    definitions["dmndi:DMNDI"]["dmndi:DMNDiagram"][0]["dmndi:DMNDiagramElement"] ??= [];
    definitions["dmndi:DMNDI"]["dmndi:DMNDiagram"][0]["dmndi:DMNDiagramElement"]?.push({
      __$$element: "dmndi:DMNShape",
      "@_id": generateUuid(),
      "@_dmnElementRef": newNodeId,
      "@_isCollapsed": false,
      "@_isListedInputData": false,
      "dc:Bounds": newNode.bounds,
    });
  });
}
