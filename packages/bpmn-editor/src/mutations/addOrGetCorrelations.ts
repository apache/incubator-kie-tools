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
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { Normalized } from "../normalization/normalize";
import { addOrGetProcessAndDiagramElements } from "./addOrGetProcessAndDiagramElements";

export function addCorrelationProperty({
  definitions,
  propertyId,
  propertyName,
  propertyType,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  propertyId: string;
  propertyName: string;
  propertyType?: string;
}): void {
  definitions.rootElement ??= [];
  definitions.rootElement.push({
    __$$element: "correlationProperty",
    "@_id": propertyId,
    "@_name": propertyName,
    "@_type": propertyType,
    correlationPropertyRetrievalExpression: [],
  });
}

export function addCorrelationKey({
  definitions,
  keyId,
  keyName,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  keyId: string;
  keyName: string;
}): void {
  const { process } = addOrGetProcessAndDiagramElements({ definitions });

  definitions.rootElement ??= [];
  let collaboration = definitions.rootElement.find((e) => e.__$$element === "collaboration");
  if (!collaboration) {
    collaboration = {
      "@_id": generateUuid(),
      __$$element: "collaboration",
      participant: [
        {
          "@_id": generateUuid(),
          "@_name": "Pool Participant",
          "@_processRef": process["@_id"],
        },
      ],
    };
    definitions.rootElement.push(collaboration);
  }

  collaboration.correlationKey ??= [];
  collaboration.correlationKey.push({
    "@_id": keyId,
    "@_name": keyName,
  });
}

export function addMessageBindingToProperty({
  definitions,
  propertyId,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  propertyId: string;
}): void {
  const property = definitions.rootElement
    ?.filter((e) => e.__$$element === "correlationProperty")
    .find((p) => p["@_id"] === propertyId);

  if (property) {
    property.correlationPropertyRetrievalExpression ??= [];
    property.correlationPropertyRetrievalExpression.push({
      "@_id": generateUuid(),
      "@_messageRef": undefined as any,
      messagePath: {
        "@_id": generateUuid(),
        "@_evaluatesToTypeRef": property["@_type"],
        __$$text: "",
      },
    });
  }
}

export function addPropertyToCorrelationKey({
  definitions,
  keyId,
  propertyId,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  keyId: string;
  propertyId: string;
}): void {
  const key = definitions.rootElement
    ?.find((e) => e.__$$element === "collaboration")
    ?.correlationKey?.find((k) => k["@_id"] === keyId);

  if (key) {
    key.correlationPropertyRef ??= [];
    key.correlationPropertyRef.push({ __$$text: propertyId });

    const { process } = addOrGetProcessAndDiagramElements({ definitions });
    for (const subs of process.correlationSubscription ?? []) {
      if (subs["@_correlationKeyRef"] === keyId) {
        subs.correlationPropertyBinding ??= [];
        subs.correlationPropertyBinding?.push({
          "@_id": generateUuid(),
          "@_correlationPropertyRef": propertyId,
          dataPath: {
            "@_id": generateUuid(),
            __$$text: "",
          },
        });
      }
    }
  }
}

export function addSubscription({
  definitions,
  keyId,
  selectedKey,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  keyId: string;
  selectedKey: any;
}): void {
  const { process } = addOrGetProcessAndDiagramElements({ definitions });
  process.correlationSubscription ??= [];
  const propertiesById = new Map(
    (definitions.rootElement ?? []).filter((e) => e.__$$element === "correlationProperty").map((p) => [p["@_id"], p])
  );
  process.correlationSubscription?.push({
    "@_id": generateUuid(),
    "@_correlationKeyRef": keyId,
    correlationPropertyBinding: (selectedKey!.correlationPropertyRef ?? []).map((propRef: any) => {
      const property = propertiesById.get(propRef.__$$text);
      const propertyType = property && property.__$$element === "correlationProperty" ? property["@_type"] : undefined;
      return {
        "@_id": generateUuid(),
        "@_correlationPropertyRef": propRef.__$$text,
        dataPath: {
          "@_id": generateUuid(),
          "@_language": "java",
          "@_evaluatesToTypeRef": propertyType,
          __$$text: "",
        },
      };
    }),
  });
}
