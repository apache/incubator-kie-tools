import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { repopulateInputDataAndDecisionsOnDecisionService } from "./addDecisionToDecisionService";

export function deleteDecisionFromDecisionService({
  definitions,
  decisionId,
  decisionServiceId,
}: {
  definitions: DMN15__tDefinitions;
  decisionId: string;
  decisionServiceId: string;
}) {
  console.debug(`DMN MUTATION: Deleting Decision '${decisionId}' from Decision Service '${decisionServiceId}'`);

  const decision = definitions.drgElement?.find((s) => s["@_id"] === decisionId);
  if (decision?.__$$element !== "decision") {
    throw new Error(`DRG Element with id '${decision}' either is not a decision or doesn't exist.`);
  }

  const decisionService = definitions.drgElement?.find((s) => s["@_id"] === decisionServiceId);
  if (decisionService?.__$$element !== "decisionService") {
    throw new Error(`DRG Element with id '${decision}' either is not a decision or doesn't exist.`);
  }

  decisionService.outputDecision = (decisionService.outputDecision ?? []).filter(
    (s) => s["@_href"] !== `#${decisionId}`
  );
  decisionService.encapsulatedDecision = (decisionService.encapsulatedDecision ?? []).filter(
    (s) => s["@_href"] !== `#${decisionId}`
  );

  repopulateInputDataAndDecisionsOnDecisionService({ definitions, decisionService });
}
