import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import {
  DMN15__tDecision,
  DMN15__tDefinitions,
  DMNDI15__DMNEdge,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { addOrGetDrd } from "./addOrGetDrd";
import { DmnDiagramEdgeData } from "../diagram/edges/Edges";
import {
  repopulateInputDataAndDecisionsOnAllDecisionServices,
  repopulateInputDataAndDecisionsOnDecisionService,
} from "./repopulateInputDataAndDecisionsOnDecisionService";

export function deleteEdge({
  definitions,
  edge,
}: {
  definitions: DMN15__tDefinitions;
  edge: { id: string; dmnObject: DmnDiagramEdgeData["dmnObject"] };
}) {
  const { diagramElements } = addOrGetDrd({ definitions });

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
  let dmnEdge: DMNDI15__DMNEdge | undefined;
  const dmnEdgeIndex = (diagramElements ?? []).findIndex((d) => d["@_dmnElementRef"] === edge.id);
  if (dmnEdgeIndex >= 0) {
    dmnEdge = diagramElements[dmnEdgeIndex];
    diagramElements?.splice(dmnEdgeIndex, 1);
  }

  repopulateInputDataAndDecisionsOnAllDecisionServices({ definitions });

  return { dmnEdge };
}
