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

import { DMN_LATEST__tDecisionService, DMN_LATEST__tDefinitions } from "@kie-tools/dmn-marshaller";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { buildXmlHref, parseXmlHref } from "@kie-tools/dmn-marshaller/dist/xml/xmlHrefs";
import { ExternalModelsIndex } from "../DmnEditor";

export function repopulateInputDataAndDecisionsOnAllDecisionServices({
  definitions,
  externalModelsByNamespace,
}: {
  definitions: Normalized<DMN_LATEST__tDefinitions>;
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
  definitions: Normalized<DMN_LATEST__tDefinitions>;
  decisionService: Normalized<DMN_LATEST__tDecisionService>;
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

  /** Map all DS Input Data and Decision requirements to their href */
  const requirements = new Map<string, "decisionIr" | "inputDataIr">();

  for (const decisionHrefString of hrefsToDecisionsInsideDecisionService) {
    const decisionHref = parseXmlHref(decisionHrefString);

    // local decision
    if (!decisionHref.namespace || decisionHref.namespace === definitions["@_namespace"]) {
      const localDecision = definitions.drgElement?.find((drgElement) => drgElement["@_id"] === decisionHref.id);
      if (localDecision?.__$$element !== "decision") {
        throw new Error(`DMN MUTATION: Node inside Decision Service is not a Decision. ID: ${localDecision?.["@_id"]}`);
      }

      (localDecision.informationRequirement ?? []).forEach((ir) => {
        if (ir.requiredDecision) {
          requirements.set(ir.requiredDecision["@_href"], "decisionIr");
        } else if (ir.requiredInput) {
          requirements.set(ir.requiredInput["@_href"], "inputDataIr");
        }
      });
    }
    // external decision
    else {
      const externalModel = externalModelsByNamespace?.[decisionHref.namespace];
      if (externalModel?.type !== "dmn") {
        throw new Error(`DMN MUTATION: External model with namespace ${decisionHref.namespace} is not a DMN.`);
      }

      const externalDecision = externalModel.model.definitions.drgElement?.find(
        (drgElement) => drgElement["@_id"] === decisionHref.id
      );
      if (externalDecision?.__$$element !== "decision") {
        throw new Error(
          `DMN MUTATION: Node inside Decision Service is not a Decision. ID: ${externalDecision?.["@_id"]}`
        );
      }

      (externalDecision.informationRequirement ?? []).forEach((ir) => {
        if (ir.requiredDecision) {
          const requirementHref = parseXmlHref(ir.requiredDecision["@_href"]);
          // If the requiredDecision has namespace, it means that it is pointing to a node in a 3rd model,
          // not this one (the local model) neither the model in the `href.namespace`.
          if (requirementHref.namespace) {
            requirements.set(ir.requiredDecision["@_href"], "decisionIr");
          } else {
            requirements.set(
              buildXmlHref({ namespace: externalModel.model.definitions["@_namespace"], id: requirementHref.id }),
              "decisionIr"
            );
          }
        } else if (ir.requiredInput) {
          // If the requiredInput has namespace, it means that it is pointing to a node in a 3rd model,
          // not this one (the local model) neither the model in the `href.namespace`.
          const requirementHref = parseXmlHref(ir.requiredInput["@_href"]);
          if (requirementHref.namespace) {
            requirements.set(ir.requiredInput["@_href"], "inputDataIr");
          } else {
            requirements.set(
              buildXmlHref({ namespace: externalModel.model.definitions["@_namespace"], id: requirementHref.id }),
              "inputDataIr"
            );
          }
        } else {
          throw new Error(
            `DMN MUTATION: Invalid information requirement referenced by external DecisionService: '${externalDecision["@_id"]}'`
          );
        }
      });
    }
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
