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
  DMN15__tDecision,
  DMN15__tDecisionService,
  DMN15__tDefinitions,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Normalized } from "../normalization/normalize";
import { ExternalModelsIndex } from "../DmnEditor";
import { parseXmlHref } from "../xml/xmlHrefs";
import { DmnLatestModel } from "@kie-tools/dmn-marshaller/dist";

export function repopulateInputDataAndDecisionsOnAllDecisionServices({
  definitions,
  externalModelsByNamespace,
}: {
  definitions: Normalized<DMN15__tDefinitions>;
  externalModelsByNamespace: ExternalModelsIndex | undefined;
}) {
  for (let i = 0; i < (definitions.drgElement ?? []).length; i++) {
    const drgElement = definitions.drgElement![i];
    if (drgElement.__$$element === "decisionService") {
      repopulateInputDataAndDecisionsOnDecisionService({
        definitions,
        decisionService: drgElement,
        externalModelsByNamespace,
      });
    }
  }
}

export function repopulateInputDataAndDecisionsOnDecisionService({
  definitions,
  decisionService,
  externalModelsByNamespace,
}: {
  definitions: Normalized<DMN15__tDefinitions>;
  decisionService: Normalized<DMN15__tDecisionService>;
  externalModelsByNamespace: ExternalModelsIndex | undefined;
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

  for (const hrefString of hrefsToDecisionsInsideDecisionService) {
    const href = parseXmlHref(hrefString);
    const externalModel = externalModelsByNamespace?.[href.namespace ?? ""];
    if (!externalModel) {
      continue;
    }

    const externalDecision = (externalModel?.model as Normalized<DmnLatestModel>).definitions.drgElement?.find(
      (drgElement) => drgElement["@_id"] === href.id
    ) as Normalized<DMN15__tDecision>;
    if (!externalDecision) {
      continue;
    }

    (externalDecision.informationRequirement ?? []).flatMap((ir) => {
      if (ir.requiredDecision) {
        const externalHref = parseXmlHref(ir.requiredDecision["@_href"]);
        // If the requiredDecision has namespace, it means that it is pointing to a node in a 3rd model,
        // not this one (the local model) neither the model in the `href.namespace`.
        if (externalHref.namespace) {
          requirements.set(`${ir.requiredDecision["@_href"]}`, "decisionIr");
        } else {
          requirements.set(`${href.namespace}${ir.requiredDecision["@_href"]}`, "decisionIr");
        }
      } else if (ir.requiredInput) {
        // If the requiredInput has namespace, it means that it is pointing to a node in a 3rd model,
        // not this one (the local model) neither the model in the `href.namespace`.
        const externalHref = parseXmlHref(ir.requiredInput["@_href"]);
        if (externalHref.namespace) {
          requirements.set(`${ir.requiredInput["@_href"]}`, "inputDataIr");
        } else {
          requirements.set(`${href.namespace}${ir.requiredInput["@_href"]}`, "inputDataIr");
        }
      } else {
        throw new Error(
          `DMN MUTATION: Invalid information requirement referenced by external DecisionService: '${externalDecision["@_id"]}'`
        );
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
