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

export function deleteInterfaceAndOperation({
  definitions,
  operationRef,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  operationRef: string;
}) {
  if (!definitions.rootElement) {
    return;
  }

  const serviceTaskInterface = definitions.rootElement.find(
    (s) => s.__$$element === "interface" && s.operation?.some((op) => op["@_id"] === operationRef)
  );

  if (!serviceTaskInterface || serviceTaskInterface.__$$element !== "interface") {
    return;
  }

  serviceTaskInterface.operation ??= [];

  const operationIndex = serviceTaskInterface.operation.findIndex((op) => op["@_id"] === operationRef);
  if (operationIndex < 0) {
    return;
  }

  const operation = serviceTaskInterface.operation[operationIndex];

  const inMessageId = operation.inMessageRef.__$$text;
  const outMessageId = operation.outMessageRef?.__$$text;

  const existingInMessageIndex = definitions.rootElement.findIndex(
    (s) => s.__$$element === "message" && s["@_id"] === inMessageId
  );
  const existingOutMessageIndex = outMessageId
    ? definitions.rootElement.findIndex((s) => s.__$$element === "message" && s["@_id"] === outMessageId)
    : -1;

  [existingInMessageIndex, existingOutMessageIndex]
    .filter((index): index is number => index >= 0)
    .sort((a, b) => b - a)
    .forEach((index) => {
      definitions.rootElement?.splice(index, 1);
    });

  serviceTaskInterface.operation.splice(operationIndex, 1);

  if (serviceTaskInterface.operation.length === 0) {
    const interfaceIndex = definitions.rootElement.findIndex(
      (s) => s.__$$element === "interface" && s["@_id"] === serviceTaskInterface["@_id"]
    );
    if (interfaceIndex >= 0) {
      definitions.rootElement.splice(interfaceIndex, 1);
    }
  }
}
