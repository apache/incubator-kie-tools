import {
  DMN15__tDecisionService,
  DMN15__tDefinitions,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

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

  const section = "encapsulated"; // FIXME: Tiago --> Determine this automatically based on the Decision's position.

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

export function repopulateInputDataAndDecisionsOnDecisionService({
  definitions,
  decisionService,
}: {
  definitions: DMN15__tDefinitions;
  decisionService: DMN15__tDecisionService;
}) {
  decisionService.inputData = [];
  decisionService.inputDecision = [];

  const decisionsInsideDecisionService = new Set([
    ...(decisionService.outputDecision ?? []).map((s) => s["@_href"]),
    ...(decisionService.encapsulatedDecision ?? []).map((s) => s["@_href"]),
  ]);

  for (let i = 0; i < definitions.drgElement!.length; i++) {
    const drgElement = definitions.drgElement![i];
    if (!decisionsInsideDecisionService.has(`#${drgElement["@_id"]}`)) {
      continue;
    }

    if (drgElement.__$$element === "inputData") {
      decisionService.inputData ??= [];
      decisionService.inputData.push({ "@_href": `#${drgElement["@_id"]}` });
    } else if (drgElement.__$$element === "decision") {
      decisionService.inputDecision ??= [];
      decisionService.inputDecision.push({ "@_href": `#${drgElement["@_id"]}` });
    } else {
      throw new Error(`Invalid type of element to be referenced by DecisionService: '${drgElement.__$$element}'`);
    }
  }
}
