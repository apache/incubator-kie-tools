import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { DMN15__tDecision, DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { addOrGetDefaultDiagram } from "./addOrGetDefaultDiagram";
import { DmnEditorDiagramEdgeData } from "../diagram/edges/Edges";

export function deleteEdge({
  definitions,
  edge,
}: {
  definitions: DMN15__tDefinitions;
  edge: { id: string; dmnObject: DmnEditorDiagramEdgeData["dmnObject"] };
}) {
  const { diagramElements } = addOrGetDefaultDiagram({ definitions });

  const dmnObjects: DMN15__tDefinitions["artifact"] | DMN15__tDefinitions["drgElement"] =
    switchExpression(edge?.dmnObject.type, {
      association: definitions.artifact,
      default: definitions.drgElement,
    }) ?? [];

  const dmnObjectIndex = dmnObjects.findIndex((d) => d["@_id"] === edge.dmnObject.id);
  if (dmnObjectIndex < 0) {
    throw new Error(`Can't find DMN element with ID ${edge.dmnObject.id}`);
  }

  const requirements =
    switchExpression(edge?.dmnObject.requirementType, {
      // Casting to DMN15__tDecision because if has all types of requirement, but not necessarily that's true.
      informationRequirement: (dmnObjects[dmnObjectIndex] as DMN15__tDecision).informationRequirement,
      knowledgeRequirement: (dmnObjects[dmnObjectIndex] as DMN15__tDecision).knowledgeRequirement,
      authorityRequirement: (dmnObjects[dmnObjectIndex] as DMN15__tDecision).authorityRequirement,
      association: dmnObjects,
    }) ?? [];

  // Deleting the requirement
  const requirementIndex = (requirements ?? []).findIndex((d) => d["@_id"] === edge.id);
  if (requirementIndex >= 0) {
    requirements?.splice(requirementIndex, 1);
  }

  // Deleting the DMNEdge's
  const dmnEdgeIndex = (diagramElements ?? []).findIndex((d) => d["@_dmnElementRef"] === edge.id);
  if (dmnEdgeIndex >= 0) {
    diagramElements?.splice(dmnEdgeIndex, 1);
  }
}
