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
import { ElementFilter } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import { Normalized } from "../normalization/normalize";
import { addOrGetInterfaces } from "./addOrGetInterfaces";
import { addOrGetMessages } from "./addOrGetMessages";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";

export function addOrGetOperations({
  definitions,
  interfaceName,
  operationRef,
  operationName,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  interfaceName: string;
  operationRef?: string;
  operationName: string;
}): {
  interface: ElementFilter<Unpacked<Normalized<BPMN20__tDefinitions["rootElement"]>>, "interface">;
  operation: ElementFilter<Unpacked<Normalized<BPMN20__tDefinitions["rootElement"]>>, "interface">["operation"][number];
} {
  const { interface: serviceTaskInterface } = addOrGetInterfaces({
    definitions,
    interfaceName,
  });

  serviceTaskInterface.operation ??= [];

  let operation:
    | ElementFilter<Unpacked<Normalized<BPMN20__tDefinitions["rootElement"]>>, "interface">["operation"][number]
    | undefined;

  if (operationRef) {
    const interfaces = definitions.rootElement?.filter((s) => s.__$$element === "interface") ?? [];

    for (const iface of interfaces) {
      if (iface.__$$element === "interface") {
        iface.operation ??= [];
        const operationIndex = iface.operation.findIndex((op) => op["@_id"] === operationRef);

        if (operationIndex >= 0) {
          operation = iface.operation[operationIndex];

          if (iface["@_id"] !== serviceTaskInterface["@_id"]) {
            iface.operation.splice(operationIndex, 1);
            serviceTaskInterface.operation.push(operation);

            if (iface.operation.length === 0) {
              const interfaceIndex = definitions.rootElement?.findIndex(
                (s) => s.__$$element === "interface" && s["@_id"] === iface["@_id"]
              );
              if (interfaceIndex !== undefined && interfaceIndex >= 0) {
                definitions.rootElement?.splice(interfaceIndex, 1);
              }
            }
          }
          break;
        }
      }
    }
  }

  if (!operation) {
    const { messageRef: inMessageId } = addOrGetMessages({
      definitions,
      messageName: "",
    });
    const { messageRef: outMessageId } = addOrGetMessages({
      definitions,
      messageName: "",
    });

    operation = {
      "@_id": generateUuid(),
      "@_name": operationName,
      inMessageRef: { __$$text: inMessageId },
      outMessageRef: { __$$text: outMessageId },
    };
    serviceTaskInterface.operation.push(operation);
  } else if (operation["@_name"] !== operationName) {
    operation["@_name"] = operationName;
  }

  return { interface: serviceTaskInterface, operation: operation };
}
