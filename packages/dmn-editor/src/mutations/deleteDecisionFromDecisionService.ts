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
    throw new Error(`DMN MUTATION: DRG Element with id '${decisionId}' is either not a Decision or doesn't exist.`);
  }

  const decisionService = definitions.drgElement?.find((s) => s["@_id"] === decisionServiceId);
  if (decisionService?.__$$element !== "decisionService") {
    throw new Error(
      `DMN MUTATION: DRG Element with id '${decisionServiceId}' is either not a Decision Service or doesn't exist.`
    );
  }

  decisionService.outputDecision = (decisionService.outputDecision ?? []).filter(
    (s) => s["@_href"] !== `#${decisionId}`
  );
  decisionService.encapsulatedDecision = (decisionService.encapsulatedDecision ?? []).filter(
    (s) => s["@_href"] !== `#${decisionId}`
  );

  repopulateInputDataAndDecisionsOnDecisionService({ definitions, decisionService });
}
