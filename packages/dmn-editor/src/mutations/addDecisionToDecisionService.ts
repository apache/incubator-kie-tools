import { DMN15__tDefinitions, DMNDI15__DMNShape } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { getContainmentRelationship, getDecisionServiceDividerLineLocalY } from "../diagram/maths/DmnMaths";
import { addOrGetDefaultDiagram } from "./addOrGetDefaultDiagram";
import { repopulateInputDataAndDecisionsOnDecisionService } from "./repopulateInputDataAndDecisionsOnDecisionService";

export function addDecisionToDecisionService({
  definitions,
  decisionId,
  decisionServiceId,
}: {
  definitions: DMN15__tDefinitions;
  decisionId: string;
  decisionServiceId: string;
}) {
  console.debug(`DMN MUTATION: Adding Decision '${decisionId}' to Decision Service '${decisionServiceId}'`);

  const decision = definitions.drgElement?.find((s) => s["@_id"] === decisionId);
  if (decision?.__$$element !== "decision") {
    throw new Error(`DRG Element with id '${decision}' either is not a decision or doesn't exist.`);
  }

  const decisionService = definitions.drgElement?.find((s) => s["@_id"] === decisionServiceId);
  if (decisionService?.__$$element !== "decisionService") {
    throw new Error(`DRG Element with id '${decision}' either is not a decision or doesn't exist.`);
  }

  const diagram = addOrGetDefaultDiagram({ definitions });
  const decisionShape = diagram.diagramElements.find(
    (s) => s["@_dmnElementRef"] === decisionId && s.__$$element === "dmndi:DMNShape"
  ) as DMNDI15__DMNShape;

  const decisionServiceShape = diagram.diagramElements.find(
    (s) => s["@_dmnElementRef"] === decisionServiceId && s.__$$element === "dmndi:DMNShape"
  ) as DMNDI15__DMNShape;

  const section = getSectionForDecisionInsideDecisionService({ decisionShape, decisionServiceShape });
  if (section === "encapsulated") {
    decisionService.encapsulatedDecision ??= [];
    decisionService.encapsulatedDecision.push({ "@_href": `#${decisionId}` });
  } else if (section === "output") {
    decisionService.outputDecision ??= [];
    decisionService.outputDecision.push({ "@_href": `#${decisionId}` });
  } else {
    throw new Error(`Invalid section to add decision to: '${section}' `);
  }

  repopulateInputDataAndDecisionsOnDecisionService({ definitions, decisionService });
}

export function getSectionForDecisionInsideDecisionService({
  decisionShape,
  decisionServiceShape,
}: {
  decisionShape: DMNDI15__DMNShape;
  decisionServiceShape: DMNDI15__DMNShape;
}): "output" | "encapsulated" {
  if (!decisionShape?.["dc:Bounds"] || !decisionServiceShape?.["dc:Bounds"]) {
    throw new Error(
      `Can't determine Decision Service section for Decision '${decisionShape["@_dmnElementRef"]}' because it doens't have a DMNShape.`
    );
  }

  const contaimentRelationship = getContainmentRelationship({
    bounds: decisionShape["dc:Bounds"],
    container: decisionServiceShape["dc:Bounds"],
    divingLineLocalY: getDecisionServiceDividerLineLocalY(decisionServiceShape),
  });

  if (!contaimentRelationship.isInside) {
    throw new Error(
      `Decision '${decisionShape["@_dmnElementRef"]}' can't be added to Decision Service '${decisionServiceShape["@_dmnElementRef"]}' because its shape is not visually contained by the Decision Service's shape.`
    );
  }

  return contaimentRelationship.section === "upper" ? "output" : "encapsulated";
}
