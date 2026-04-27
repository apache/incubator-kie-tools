/*
 * IBM Confidential
 * PID 5900-AR4
 * Copyright IBM Corp. 2026
 */
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

import { BPMN20__tDefinitions } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { Normalized } from "../normalization/normalize";
import { addOrGetProcessAndDiagramElements } from "./addOrGetProcessAndDiagramElements";

export function deleteOrphanedCorrelationSubscriptions({
  definitions,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
}) {
  const { process } = addOrGetProcessAndDiagramElements({ definitions });

  if (!process.correlationSubscription || process.correlationSubscription.length === 0) {
    return;
  }

  const validCorrelationKeyIds = new Set<string>();
  definitions.rootElement
    ?.filter((e) => e.__$$element === "collaboration")
    .forEach((collaboration) => {
      if (collaboration.__$$element === "collaboration") {
        collaboration.correlationKey?.forEach((key) => {
          if (key["@_id"]) {
            validCorrelationKeyIds.add(key["@_id"]);
          }
        });
      }
    });

  const validCorrelationPropertyIds = new Set<string>();
  definitions.rootElement
    ?.filter((e) => e.__$$element === "correlationProperty")
    .forEach((property) => {
      if (property.__$$element === "correlationProperty" && property["@_id"]) {
        validCorrelationPropertyIds.add(property["@_id"]);
      }
    });

  const cleanedSubscriptions = [];

  for (const subscription of process.correlationSubscription) {
    const correlationKeyRef = subscription["@_correlationKeyRef"];

    if (!correlationKeyRef || !validCorrelationKeyIds.has(correlationKeyRef)) {
      continue;
    }

    if (subscription.correlationPropertyBinding) {
      subscription.correlationPropertyBinding = subscription.correlationPropertyBinding.filter((binding) => {
        const propertyRef = binding["@_correlationPropertyRef"];
        return propertyRef && validCorrelationPropertyIds.has(propertyRef);
      });
    }

    if (subscription.correlationPropertyBinding && subscription.correlationPropertyBinding.length > 0) {
      cleanedSubscriptions.push(subscription);
    }
  }

  process.correlationSubscription = cleanedSubscriptions;
}
