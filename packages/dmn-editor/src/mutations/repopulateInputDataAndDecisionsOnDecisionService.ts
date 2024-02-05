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
  decisionService.inputData = [];
  decisionService.inputDecision = [];

  const hrefsToDecisionsInsideDecisionService = new Set([
    ...(decisionService.outputDecision ?? []).map((s) => s["@_href"]),
    ...(decisionService.encapsulatedDecision ?? []).map((s) => s["@_href"]),
  ]);

  const requirements = new Array<{ href: string; type: "decisionIr" | "inputDataIr" }>();
  for (let i = 0; i < definitions.drgElement!.length; i++) {
    const drgElement = definitions.drgElement![i];
    if (!hrefsToDecisionsInsideDecisionService.has(`#${drgElement["@_id"]}`) || drgElement.__$$element !== "decision") {
      continue;
    }

    (drgElement.informationRequirement ?? []).flatMap((ir) => {
      if (ir.requiredDecision) {
        requirements.push({ href: ir.requiredDecision["@_href"], type: "decisionIr" });
      } else if (ir.requiredInput) {
        requirements.push({ href: ir.requiredInput["@_href"], type: "inputDataIr" });
      }
    });
  }

  const inputDatas = new Set<string>(); // Using Set for uniqueness
  const inputDecisions = new Set<string>(); // Using Set for uniqueness

  const requirementsArray = [...requirements];
  for (let i = 0; i < requirementsArray.length; i++) {
    const r = requirementsArray[i];
    if (r.type === "inputDataIr") {
      inputDatas.add(r.href);
    } else if (r.type === "decisionIr") {
      inputDecisions.add(r.href);
    } else {
      throw new Error(`DMN MUTATION: Invalid type of element to be referenced by DecisionService: '${r.type}'`);
    }
  }

  decisionService.inputData = [...inputDatas].map((iHref) => ({ "@_href": iHref }));
  decisionService.inputDecision = [...inputDecisions].flatMap(
    (dHref) => (hrefsToDecisionsInsideDecisionService.has(dHref) ? [] : { "@_href": dHref }) // Makes sure output and encapsulated Decisions are not listed as inputDecisions
  );
}
