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
import { deleteOrphanedCorrelationSubscriptions } from "./deleteOrphanedCorrelationSubscriptions";
import { deleteUnusedItemDefinitions } from "./deleteItemDefinition";

export function deleteCorrelationProperty({
  definitions,
  propertyId,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  propertyId: string;
}): void {
  definitions.rootElement ??= [];
  definitions.rootElement = definitions.rootElement?.filter((e) => e["@_id"] !== propertyId);

  definitions.rootElement
    ?.filter((e) => e.__$$element === "collaboration")
    .forEach((collaboration) => {
      if (collaboration.__$$element === "collaboration") {
        collaboration.correlationKey?.forEach((key) => {
          if (key.correlationPropertyRef) {
            key.correlationPropertyRef = key.correlationPropertyRef.filter(
              (propRef) => propRef.__$$text !== propertyId
            );
          }
        });
        collaboration.correlationKey = collaboration.correlationKey?.filter(
          (key) => key.correlationPropertyRef && key.correlationPropertyRef.length > 0
        );
      }
    });

  definitions.rootElement = definitions.rootElement?.filter((e) => {
    if (e.__$$element === "collaboration") {
      return e.correlationKey && e.correlationKey.length > 0;
    }
    return true;
  });

  deleteOrphanedCorrelationSubscriptions({ definitions });
  deleteUnusedItemDefinitions({ definitions });
}

export function deleteCorrelationKey({
  definitions,
  keyId,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  keyId: string;
}): void {
  const collaboration = definitions.rootElement?.find((e) => e.__$$element === "collaboration");
  if (collaboration) {
    collaboration.correlationKey = collaboration.correlationKey?.filter((k) => k["@_id"] !== keyId);
  }

  definitions.rootElement = definitions.rootElement?.filter((e) => {
    if (e.__$$element === "collaboration") {
      return e.correlationKey && e.correlationKey.length > 0;
    }
    return true;
  });

  deleteOrphanedCorrelationSubscriptions({ definitions });
}

export function deletePropertyBinding({
  definitions,
  propertyId,
  bindingIndex,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  propertyId: string;
  bindingIndex: number;
}): void {
  const property = definitions.rootElement
    ?.filter((p) => p.__$$element === "correlationProperty")
    .find((p) => p["@_id"] === propertyId);

  if (property) {
    property.correlationPropertyRetrievalExpression.splice(bindingIndex, 1);
    deleteUnusedItemDefinitions({ definitions });
  }
}

export function deletePropertyFromKey({
  definitions,
  keyId,
  propertyIndex,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  keyId: string;
  propertyIndex: number;
}): void {
  const collaboration = definitions.rootElement?.find((e) => e.__$$element === "collaboration");
  const key =
    collaboration && collaboration.__$$element === "collaboration"
      ? collaboration.correlationKey?.find((k) => k["@_id"] === keyId)
      : undefined;

  if (key) {
    key.correlationPropertyRef ??= [];
    const removedCorrelationPropertyRef = key.correlationPropertyRef[propertyIndex];
    key.correlationPropertyRef.splice(propertyIndex, 1);

    const { process } = addOrGetProcessAndDiagramElements({ definitions });
    process.correlationSubscription
      ?.filter((subs) => subs["@_correlationKeyRef"] === keyId)
      .forEach((subs) => {
        subs.correlationPropertyBinding = subs.correlationPropertyBinding?.filter(
          (b) => b["@_correlationPropertyRef"] !== removedCorrelationPropertyRef.__$$text
        );
      });
  }
}

export function deleteSubscription({
  definitions,
  subscriptionId,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  subscriptionId: string;
}): void {
  const { process } = addOrGetProcessAndDiagramElements({ definitions });
  process.correlationSubscription ??= [];
  process.correlationSubscription = process.correlationSubscription.filter((subs) => subs["@_id"] !== subscriptionId);
}
