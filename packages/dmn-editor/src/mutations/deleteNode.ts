import { DMN14__tDecision, DMN14__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { NodeType } from "../diagram/connections/graphStructure";
import { NodeNature, nodeNatures } from "./NodeNature";
import { DmnEditorDiagramEdgeData } from "../diagram/edges/Edges";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";

export function deleteNode({
  definitions,
  node,
  targetEdges,
}: {
  definitions: DMN14__tDefinitions;
  node: { type: NodeType; id: string };
  targetEdges: { id: string; data: DmnEditorDiagramEdgeData }[];
}) {
  const diagramElements = definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.[0]?.["dmndi:DMNDiagramElement"];

  const uniqueTargetEdgeIds = new Set<string>();

  for (const edge of targetEdges) {
    if (uniqueTargetEdgeIds.has(edge.id)) {
      continue;
    } else {
      uniqueTargetEdgeIds.add(edge.id);
    }

    const dmnObjects: DMN14__tDefinitions["artifact"] | DMN14__tDefinitions["drgElement"] =
      switchExpression(edge.data?.dmnObject.type, {
        association: definitions.artifact,
        default: definitions.drgElement,
      }) ?? [];

    const dmnObjectIndex = dmnObjects.findIndex((d) => d["@_id"] === edge.data.dmnObject.id);
    if (dmnObjectIndex < 0) {
      throw new Error("STOP!");
    }

    const requirements =
      switchExpression(edge.data?.dmnObject.requirementType, {
        // Casting to DMN14__tDefinitions because if has all types of requirement
        informationRequirement: (dmnObjects[dmnObjectIndex] as DMN14__tDecision).informationRequirement,
        knowledgeRequirement: (dmnObjects[dmnObjectIndex] as DMN14__tDecision).knowledgeRequirement,
        authorityRequirement: (dmnObjects[dmnObjectIndex] as DMN14__tDecision).authorityRequirement,
        association: dmnObjects,
      }) ?? [];

    // Deleting the requirement
    requirements?.splice(
      (requirements ?? []).findIndex((d) => d["@_id"] === edge.id),
      1
    );

    // Deleting the DMNEdge's
    diagramElements?.splice(
      (diagramElements ?? []).findIndex((d) => d["@_dmnElementRef"] === edge.id),
      1
    );
  }

  // FIXME: Tiago --> Delete extension elements when deleting nodes that contain expressions. What else needs to be clened up?

  // delete the DMNShape
  diagramElements?.splice(
    (diagramElements ?? []).findIndex((d) => d["@_dmnElementRef"] === node.id),
    1
  );

  // delete the dmnObject itself
  if (nodeNatures[node.type] === NodeNature.ARTIFACT) {
    definitions.artifact?.splice(
      (definitions.artifact ?? []).findIndex((a) => a["@_id"] === node.id),
      1
    );
  } else if (nodeNatures[node.type] === NodeNature.DRG_ELEMENT) {
    definitions.drgElement?.splice(
      (definitions.drgElement ?? []).findIndex((d) => d["@_id"] === node.id),
      1
    );
  } else {
    throw new Error(`Unknown node nature '${nodeNatures[node.type]}'.`);
  }
}
