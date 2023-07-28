import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { DC__Bounds, DMN14__tDecision } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { TargetHandleId } from "../diagram/connections/NodeHandles";
import { EdgeType, NodeType } from "../diagram/connections/graphStructure";
import { _checkIsValidConnection } from "../diagram/connections/isValidConnection";
import { getBoundsCenterPoint, getPointForHandle } from "../diagram/maths/DmnMaths";
import { Dispatch } from "../store/Store";
import { getRequirementsFromEdge } from "./addConnectedNode";

export function addEdge({
  sourceNode,
  targetNode,
  edge,
  dispatch: { dmn },
}: {
  dispatch: { dmn: Dispatch["dmn"] };
  sourceNode: { type: NodeType; id: string; bounds: DC__Bounds };
  targetNode: { type: NodeType; id: string; bounds: DC__Bounds; index: number };
  edge: { type: EdgeType; handle: TargetHandleId };
}) {
  if (!_checkIsValidConnection(sourceNode, targetNode, edge.type)) {
    throw new Error(`Invalid structure: (${sourceNode.type}) --${edge.type}--> (${targetNode.type}) `);
  }

  const newEdgeId = generateUuid();

  dmn.set((model) => {
    if (edge.type === "edge_association") {
      model.definitions.artifact ??= [];
      // Remove potentially existing edge
      const existingIndex = model.definitions.artifact.findIndex(
        (association) =>
          association.__$$element === "association" &&
          association.sourceRef["@_href"] === `#${sourceNode.id}` &&
          association.targetRef["@_href"] === `#${targetNode.id}`
      );
      model.definitions.artifact.splice(existingIndex, existingIndex >= 0 ? 1 : 0);

      // Replace with the new one.
      model.definitions.artifact?.push({
        "@_id": newEdgeId,
        __$$element: "association",
        sourceRef: { "@_href": `#${sourceNode.id}` },
        targetRef: { "@_href": `#${targetNode.id}` },
      });
    } else {
      const requirements = getRequirementsFromEdge(sourceNode, newEdgeId, edge.type);
      const drgElement = model.definitions.drgElement![targetNode.index] as DMN14__tDecision; // We cast to tDecision here because it has all three types of requirement.
      if (requirements?.informationRequirement) {
        // FIXME: Tiago -->  Remove potentially existing edge
        drgElement.informationRequirement ??= [];
        drgElement.informationRequirement?.push(...(requirements?.informationRequirement ?? []));
      } else if (requirements?.knowledgeRequirement) {
        // FIXME: Tiago -->  Remove potentially existing edge
        drgElement.knowledgeRequirement ??= [];
        drgElement.knowledgeRequirement?.push(...(requirements?.knowledgeRequirement ?? []));
      } else if (requirements?.authorityRequirement) {
        // FIXME: Tiago -->  Remove potentially existing edge
        drgElement.authorityRequirement ??= [];
        drgElement.authorityRequirement?.push(...(requirements?.authorityRequirement ?? []));
      }
    }

    // FIXME: Tiago -->  Remove potentially existing edge

    // Add the new edge shape
    model.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.[0]?.["dmndi:DMNDiagramElement"]?.push({
      __$$element: "dmndi:DMNEdge",
      "@_id": generateUuid(),
      "@_dmnElementRef": newEdgeId,
      "@_sourceElement": sourceNode.id,
      "@_targetElement": targetNode.id,
      "di:waypoint": [
        getBoundsCenterPoint(sourceNode.bounds),
        getPointForHandle({ bounds: targetNode.bounds, handle: edge.handle }),
      ],
    });
  });
}
