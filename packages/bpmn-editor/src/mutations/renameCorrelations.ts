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
import { deleteUnusedItemDefinitions } from "./deleteItemDefinition";

export function updateCorrelationPropertyName({
  definitions,
  propertyId,
  newName,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  propertyId: string;
  newName: string;
}): void {
  const property = definitions.rootElement
    ?.filter((e) => e.__$$element === "correlationProperty")
    .find((p) => p["@_id"] === propertyId);

  if (property) {
    property["@_name"] = newName;
  }
}

export function updateCorrelationPropertyType({
  definitions,
  propertyId,
  newItemDefinitionRef,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  propertyId: string;
  newItemDefinitionRef: string | undefined;
}): void {
  const property = definitions.rootElement
    ?.filter((e) => e.__$$element === "correlationProperty")
    .find((p) => p["@_id"] === propertyId);

  if (property) {
    property["@_type"] = newItemDefinitionRef;
    property.correlationPropertyRetrievalExpression.forEach((cpre) => {
      if (cpre.messagePath) {
        cpre.messagePath["@_evaluatesToTypeRef"] = newItemDefinitionRef;
      }
    });

    const { process } = addOrGetProcessAndDiagramElements({ definitions });
    process.correlationSubscription?.forEach((subscription) => {
      subscription.correlationPropertyBinding
        ?.filter((binding) => binding["@_correlationPropertyRef"] === propertyId)
        .forEach((binding) => {
          if (binding.dataPath) {
            binding.dataPath["@_evaluatesToTypeRef"] = newItemDefinitionRef;
          }
        });
    });

    deleteUnusedItemDefinitions({ definitions });
  }
}

export function updateCorrelationKeyName({
  definitions,
  keyId,
  newName,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  keyId: string;
  newName: string;
}): void {
  const key = definitions.rootElement
    ?.find((e) => e.__$$element === "collaboration")
    ?.correlationKey?.find((k) => k["@_id"] === keyId);

  if (key) {
    key["@_name"] = newName;
  }
}

export function updateMessageBindingExpression({
  definitions,
  propertyId,
  bindingIndex,
  newExpression,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  propertyId: string;
  bindingIndex: number;
  newExpression: string;
}): void {
  const property = definitions.rootElement
    ?.filter((p) => p.__$$element === "correlationProperty")
    .find((p) => p["@_id"] === propertyId);

  if (property) {
    property.correlationPropertyRetrievalExpression ??= [];
    property.correlationPropertyRetrievalExpression[bindingIndex].messagePath.__$$text = newExpression;
  }
}

export function updateMessageBindingMessage({
  definitions,
  propertyId,
  bindingIndex,
  newMessageRef,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  propertyId: string;
  bindingIndex: number;
  newMessageRef: string;
}): void {
  const property = definitions.rootElement
    ?.filter((p) => p.__$$element === "correlationProperty")
    .find((p) => p["@_id"] === propertyId);
  if (property) {
    property.correlationPropertyRetrievalExpression[bindingIndex]["@_messageRef"] = newMessageRef || "";
  }
}

export function updateSubscriptionValue({
  definitions,
  subscriptionId,
  propertyBindingIndex,
  newValue,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  subscriptionId: string;
  propertyBindingIndex: number;
  newValue: string;
}): void {
  const { process } = addOrGetProcessAndDiagramElements({ definitions });
  process.correlationSubscription ??= [];
  process.correlationSubscription.find((subs) => subs["@_id"] === subscriptionId)!.correlationPropertyBinding![
    propertyBindingIndex
  ].dataPath.__$$text = newValue;
}
