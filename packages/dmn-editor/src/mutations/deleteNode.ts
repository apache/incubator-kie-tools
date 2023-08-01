import { DMN14__tDecision, DMN14__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { NodeType } from "../diagram/connections/graphStructure";
import { NodeNature, nodeNatures } from "./NodeNature";
import { DmnEditorDiagramEdgeData } from "../diagram/edges/Edges";
import * as RF from "reactflow";
import { switchExpression } from "../switchExpression/switchExpression";

export function deleteNode({
  definitions,
  node,
  sourceEdgeIndexes,
  targetEdgeIndexes,
}: {
  definitions: DMN14__tDefinitions;
  node: { type: NodeType; id: string; index: number; shapeIndex: number };
  sourceEdgeIndexes: RF.Edge<DmnEditorDiagramEdgeData>[];
  targetEdgeIndexes: RF.Edge<DmnEditorDiagramEdgeData>[];
}) {
  const diagramElements = definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.[0]?.["dmndi:DMNDiagramElement"];

  for (const edge of [...sourceEdgeIndexes, ...targetEdgeIndexes]) {
    const dmnObjects =
      switchExpression(edge.data?.dmnObject.type, {
        association: definitions.artifact,
        default: definitions.drgElement,
      }) ?? [];

    const requirements =
      switchExpression(edge.data?.dmnObject.requirementType, {
        // Casting to DMN14__tDefinitions because if has all types of requirement
        ir: (dmnObjects[edge.data!.dmnObject.index] as DMN14__tDecision).informationRequirement,
        kr: (dmnObjects[edge.data!.dmnObject.index] as DMN14__tDecision).knowledgeRequirement,
        ar: (dmnObjects[edge.data!.dmnObject.index] as DMN14__tDecision).authorityRequirement,
        a: dmnObjects,
      }) ?? [];

    // Deleting the requirement
    requirements.splice(edge.data!.dmnObject.requirementIndex, 1);

    // Deleting the DMNEdge's
    diagramElements?.splice(edge.data!.dmnEdge!.index, 1);
  }

  // FIXME: Tiago --> nodes need to be deleted in reverse order, otherwise we end up deleting the wrong stuff, as indexes shift.

  // FIXME: Tiago --> delete extension elements when deleting nodes that contain expressions.

  // delete the DMNShape
  diagramElements?.splice(node.shapeIndex, 1);

  // delete the dmnObject itself
  if (nodeNatures[node.type] === NodeNature.ARTIFACT) {
    definitions.artifact?.splice(node.index, 1);
  } else if (nodeNatures[node.type] === NodeNature.DRG_ELEMENT) {
    definitions.drgElement?.splice(node.index, 1);
  } else {
    throw new Error(`Unknown node nature '${nodeNatures[node.type]}'.`);
  }
}
