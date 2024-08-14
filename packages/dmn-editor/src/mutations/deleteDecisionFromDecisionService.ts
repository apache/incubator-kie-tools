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

import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { repopulateInputDataAndDecisionsOnDecisionService } from "./repopulateInputDataAndDecisionsOnDecisionService";
import { Normalized } from "../normalization/normalize";
import { buildXmlHref, parseXmlHref } from "../xml/xmlHrefs";
import { DmnLatestModel } from "@kie-tools/dmn-marshaller/dist";
import { ExternalModelsIndex } from "../DmnEditor";

export function deleteDecisionFromDecisionService({
  definitions,
  decisionHref,
  decisionServiceId,
  externalModelsByNamespace,
}: {
  definitions: Normalized<DMN15__tDefinitions>;
  decisionHref: string;
  decisionServiceId: string;
  externalModelsByNamespace: ExternalModelsIndex | undefined;
}) {
  console.debug(`DMN MUTATION: Deleting Decision '${decisionHref}' from Decision Service '${decisionServiceId}'`);

  const href = parseXmlHref(decisionHref);

  const externalModel = externalModelsByNamespace?.[href.namespace ?? ""];
  if (href.namespace && !externalModel) {
    throw new Error(`DMN MUTATION: Namespace '${href.namespace}' not found.`);
  }

  if (href.namespace) {
    const externalDrgs = (externalModel?.model as Normalized<DmnLatestModel>).definitions.drgElement;
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

  const xmlHref = buildXmlHref({ namespace: href.namespace, id: href.id });
  decisionService.outputDecision = (decisionService.outputDecision ?? []).filter((s) => s["@_href"] !== `${xmlHref}`);
  decisionService.encapsulatedDecision = (decisionService.encapsulatedDecision ?? []).filter(
    (s) => s["@_href"] !== `${xmlHref}`
  );

  repopulateInputDataAndDecisionsOnDecisionService({ definitions, decisionService, externalModelsByNamespace });
}
