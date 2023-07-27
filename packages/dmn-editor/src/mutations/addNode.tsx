import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import {
  DC__Bounds,
  DMN14__tAuthorityRequirement,
  DMN14__tDecision,
  DMN14__tInformationRequirement,
  DMN14__tKnowledgeRequirement,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { Dispatch } from "../store/Store";
import { switchExpression } from "../switchExpression/switchExpression";
import { EdgeType, NodeType } from "../diagram/connections/graphStructure";
import { AutoMarker } from "../diagram/edges/AutoMarker";
import { getBoundsCenterPoint } from "../diagram/maths/DmnMaths";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";

enum NodeNature {
  DRG_ELEMENT = "DRG_ELEMENT",
  ARTIFACT = "ARTIFACT",
}
const nodeNatures: Record<NodeType, NodeNature> = {
  [NODE_TYPES.inputData]: NodeNature.DRG_ELEMENT,
  [NODE_TYPES.decision]: NodeNature.DRG_ELEMENT,
  [NODE_TYPES.bkm]: NodeNature.DRG_ELEMENT,
  [NODE_TYPES.knowledgeSource]: NodeNature.DRG_ELEMENT,
  [NODE_TYPES.decisionService]: NodeNature.DRG_ELEMENT,
  [NODE_TYPES.textAnnotation]: NodeNature.ARTIFACT,
  [NODE_TYPES.group]: NodeNature.ARTIFACT,
};

export function addNode({
  sourceNode,
  newNode,
  edge,
  dispatch,
}: {
  sourceNode: { type: NodeType; id: string; bounds: DC__Bounds };
  newNode: { type: NodeType; bounds: DC__Bounds };
  edge: EdgeType;
  dispatch: { dmn: Dispatch["dmn"] };
}) {
  const newNodeId = generateUuid();
  const newEdgeId = generateUuid();
  const nature = nodeNatures[newNode.type];

  dispatch.dmn.set(({ definitions }) => {
    if (nature === NodeNature.DRG_ELEMENT) {
      const ir:
        | undefined //
        | Required<Pick<DMN14__tInformationRequirement, "requiredInput" | "@_id">>
        | Required<Pick<DMN14__tInformationRequirement, "requiredDecision" | "@_id">> = switchExpression(
        sourceNode.type,
        {
          node_inputData: { "@_id": newEdgeId, requiredInput: { "@_href": `#${sourceNode.id}` } },
          node_decision: { "@_id": newEdgeId, requiredDecision: { "@_href": `#${sourceNode.id}` } },
          default: undefined,
        }
      );

      const kr:
        | undefined //
        | Required<Pick<DMN14__tKnowledgeRequirement, "requiredKnowledge" | "@_id">> = switchExpression(
        sourceNode.type,
        {
          node_bkm: { "@_id": newEdgeId, requiredKnowledge: { "@_href": `#${sourceNode.id}` } },
          default: undefined,
        }
      );

      const ar:
        | undefined //
        | Required<Pick<DMN14__tAuthorityRequirement, "requiredInput" | "@_id">>
        | Required<Pick<DMN14__tAuthorityRequirement, "requiredDecision" | "@_id">>
        | Required<Pick<DMN14__tAuthorityRequirement, "requiredAuthority" | "@_id">> = switchExpression(
        sourceNode.type,
        {
          node_inputData: { "@_id": newEdgeId, requiredInput: { "@_href": `#${sourceNode.id}` } },
          node_decision: { "@_id": newEdgeId, requiredDecision: { "@_href": `#${sourceNode.id}` } },
          node_knowledgeSource: { "@_id": newEdgeId, requiredAuthority: { "@_href": `#${sourceNode.id}` } },
          default: undefined,
        }
      );

      // We can use tDecision to type here, because it contains all requirement types.
      const requirements:
        | Pick<DMN14__tDecision, "informationRequirement">
        | Pick<DMN14__tDecision, "knowledgeRequirement">
        | Pick<DMN14__tDecision, "authorityRequirement">
        | undefined = switchExpression(edge, {
        edge_informationRequirement: ir ? { informationRequirement: [ir] } : undefined,
        edge_knowledgeRequirement: kr ? { knowledgeRequirement: [kr] } : undefined,
        edge_authorityRequirement: ar ? { authorityRequirement: [ar] } : undefined,
        default: undefined,
      });

      definitions.drgElement ??= [];
      definitions.drgElement?.push(
        switchExpression(newNode.type as Exclude<NodeType, "node_group" | "node_textAnnotation">, {
          node_bkm: {
            __$$element: "businessKnowledgeModel",
            "@_name": "New BKM",
            "@_id": newNodeId,
            ...requirements,
          },
          node_decision: {
            __$$element: "decision",
            "@_name": "New Decision",
            "@_id": newNodeId,
            ...requirements,
          },
          node_decisionService: {
            __$$element: "decisionService",
            "@_name": "New Decision Service",
            "@_id": newNodeId,
            ...requirements,
          },
          node_inputData: {
            __$$element: "inputData",
            "@_name": "New Input Data",
            "@_id": newNodeId,
            ...requirements,
          },
          node_knowledgeSource: {
            __$$element: "knowledgeSource",
            "@_name": "New Knowledge Source",
            "@_id": newNodeId,
            ...requirements,
          },
        })
      );
    } else if (nature === NodeNature.ARTIFACT) {
      definitions.artifact ??= [];
      definitions.artifact?.push(
        ...switchExpression(newNode.type as Extract<NodeType, "node_group" | "node_textAnnotation">, {
          node_textAnnotation: [
            {
              "@_id": newNodeId,
              __$$element: "textAnnotation" as const,
              text: "New text annotation",
            },
            {
              "@_id": newEdgeId,
              __$$element: "association" as const,
              sourceRef: { "@_href": `#${sourceNode.id}` },
              targetRef: { "@_href": `#${newNodeId}` },
            },
          ],
          node_group: [
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
    definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.[0]?.["dmndi:DMNDiagramElement"]?.push({
      __$$element: "dmndi:DMNShape",
      "@_id": generateUuid(),
      "@_dmnElementRef": newNodeId,
      "@_isCollapsed": false,
      "@_isListedInputData": false,
      "dc:Bounds": newNode.bounds,
    });

    // Add the new edge shape
    definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.[0]?.["dmndi:DMNDiagramElement"]?.push({
      __$$element: "dmndi:DMNEdge",
      "@_id": generateUuid() + AutoMarker.TARGET,
      "@_dmnElementRef": newEdgeId,
      "@_sourceElement": sourceNode.id,
      "@_targetElement": newNodeId,
      "di:waypoint": [getBoundsCenterPoint(sourceNode.bounds), getBoundsCenterPoint(newNode.bounds)],
    });
  });
}
