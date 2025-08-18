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

import { DMN_LATEST__tDefinitions } from "@kie-tools/dmn-marshaller";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { parseXmlHref } from "@kie-tools/dmn-marshaller/dist/xml/xmlHrefs";
import { repopulateInputDataAndDecisionsOnDecisionService } from "./repopulateInputDataAndDecisionsOnDecisionService";
import { ExternalModelsIndex } from "../DmnEditor";

export function deleteDecisionFromDecisionService({
  definitions,
  decisionHref,
  decisionServiceId,
  externalModelsByNamespace,
}: {
  definitions: Normalized<DMN_LATEST__tDefinitions>;
  decisionHref: string;
  decisionServiceId: string;
  externalModelsByNamespace: ExternalModelsIndex | undefined;
}) {
  console.debug(`DMN MUTATION: Deleting Decision '${decisionHref}' from Decision Service '${decisionServiceId}'`);

  const href = parseXmlHref(decisionHref);
  if (href.namespace) {
    const externalModel = externalModelsByNamespace?.[href.namespace];
    if (!externalModel) {
      throw new Error(`DMN MUTATION: Namespace '${href.namespace}' not found.`);
    }

    if (externalModel?.type !== "dmn") {
      throw new Error(`DMN MUTATION: External model with namespace ${href.namespace} is not a DMN.`);
    }

    const externalDrgs = externalModel.model.definitions.drgElement;
    const decision = externalDrgs?.find((drgElement) => drgElement["@_id"] === href.id);
    if (decision?.__$$element !== "decision") {
      throw new Error(
        `DMN MUTATION: DRG Element with id '${href.id}' is either not a Decision or doesn't exist in the external model '${href.namespace}'`
      );
    }
  } else {
    const decision = definitions.drgElement?.find((s) => s["@_id"] === href.id);
    if (decision?.__$$element !== "decision") {
      throw new Error(`DMN MUTATION: DRG Element with id '${href.id}' is either not a Decision or doesn't exist.`);
    }
  }

  const decisionService = definitions.drgElement?.find((s) => s["@_id"] === decisionServiceId);
  if (decisionService?.__$$element !== "decisionService") {
    throw new Error(
      `DMN MUTATION: DRG Element with id '${decisionServiceId}' is either not a Decision Service or doesn't exist.`
    );
  }

  decisionService.outputDecision = (decisionService.outputDecision ?? []).filter((od) => od["@_href"] !== decisionHref);
  decisionService.encapsulatedDecision = (decisionService.encapsulatedDecision ?? []).filter(
    (ed) => ed["@_href"] !== decisionHref
  );

  repopulateInputDataAndDecisionsOnDecisionService({ definitions, decisionService, externalModelsByNamespace });
}
