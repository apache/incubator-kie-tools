/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import {
  DMN15__tDecisionService,
  DMN15__tDefinitions,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

export function repopulateInputDataAndDecisionsOnAllDecisionServices({
  definitions,
}: {
  definitions: DMN15__tDefinitions;
}) {
  for (let i = 0; i < (definitions.drgElement ?? []).length; i++) {
    const drgElement = definitions.drgElement![i];
    if (drgElement.__$$element === "decisionService") {
      repopulateInputDataAndDecisionsOnDecisionService({
        definitions,
        decisionService: drgElement,
      });
    }
  }
}

export function repopulateInputDataAndDecisionsOnDecisionService({
  definitions,
  decisionService,
}: {
  definitions: DMN15__tDefinitions;
  decisionService: DMN15__tDecisionService;
}) {
  // Save previous values to preserve order
  const inputDatas = new Set<string>([...(decisionService.inputData ?? [])].map((e) => e["@_href"])); // Using Set for uniqueness
  const inputDecisions = new Set<string>([...(decisionService.inputDecision ?? [])].map((e) => e["@_href"])); // Using Set for uniqueness

  // Reset the inputData and inputDecision entries
  decisionService.inputData = [];
  decisionService.inputDecision = [];

  const hrefsToDecisionsInsideDecisionService = new Set([
    ...(decisionService.outputDecision ?? []).map((s) => s["@_href"]),
    ...(decisionService.encapsulatedDecision ?? []).map((s) => s["@_href"]),
  ]);

  // Map all DS Input Data and Decision requirements to their href
  const requirements = new Map<string, "decisionIr" | "inputDataIr">();
  for (let i = 0; i < definitions.drgElement!.length; i++) {
    const drgElement = definitions.drgElement![i];
    if (!hrefsToDecisionsInsideDecisionService.has(`#${drgElement["@_id"]}`) || drgElement.__$$element !== "decision") {
      continue;
    }

    (drgElement.informationRequirement ?? []).flatMap((ir) => {
      if (ir.requiredDecision) {
        requirements.set(ir.requiredDecision["@_href"], "decisionIr");
      } else if (ir.requiredInput) {
        requirements.set(ir.requiredInput["@_href"], "inputDataIr");
      }
    });
  }

  // START - Remove outdated requirements
  [...inputDatas].forEach((inputData) => {
    if (!requirements.has(inputData)) {
      inputDatas.delete(inputData);
    }
  });

  [...inputDecisions].forEach((inputDecision) => {
    if (!requirements.has(inputDecision)) {
      inputDecisions.delete(inputDecision);
    }
  });
  // END

  // Update inputDecisions and inputDatas requirements with possible new hrefs
  requirements.forEach((type, href) => {
    if (type === "decisionIr") {
      inputDecisions.add(href);
    } else if (type === "inputDataIr") {
      inputDatas.add(href);
    } else {
      throw new Error(`DMN MUTATION: Invalid type of element to be referenced by DecisionService: '${type}'`);
    }
  });

  decisionService.inputData = [...inputDatas].map((iHref) => ({ "@_href": iHref }));
  decisionService.inputDecision = [...inputDecisions].flatMap(
    (dHref) => (hrefsToDecisionsInsideDecisionService.has(dHref) ? [] : { "@_href": dHref }) // Makes sure output and encapsulated Decisions are not listed as inputDecisions
  );
}
