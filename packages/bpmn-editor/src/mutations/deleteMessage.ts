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
import { visitFlowElementsAndArtifacts } from "./_elementVisitor";
import { addOrGetProcessAndDiagramElements } from "./addOrGetProcessAndDiagramElements";
import { RESERVED_ITEM_DEFINITION_ID_FOR_MESSAGES } from "./addOrGetMessages";

export function deleteMessage({
  definitions,
  message,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  message: string;
}) {
  const existingMessageIndex = definitions.rootElement?.findIndex(
    (s) => s.__$$element === "message" && s["@_name"] === message
  );

  if (existingMessageIndex === undefined || existingMessageIndex < 0) {
    throw new Error(`BPMN MUTATION: Message ${message} is not in the model`);
  }
  const messageId = definitions.rootElement?.[existingMessageIndex]?.["@_id"];

  // Delete from root element
  definitions.rootElement?.splice(existingMessageIndex, 1);

  const remainingMessages = definitions.rootElement?.filter((s) => s.__$$element === "message") ?? [];

  const hasMessagesUsingReservedItemDef = remainingMessages.some(
    (msg) => msg.__$$element === "message" && msg["@_itemRef"] === RESERVED_ITEM_DEFINITION_ID_FOR_MESSAGES
  );
  if (!hasMessagesUsingReservedItemDef) {
    const itemDefinitionIndex = definitions.rootElement?.findIndex(
      (s) => s.__$$element === "itemDefinition" && s["@_id"] === RESERVED_ITEM_DEFINITION_ID_FOR_MESSAGES
    );
    if (itemDefinitionIndex !== undefined && itemDefinitionIndex >= 0) {
      definitions.rootElement?.splice(itemDefinitionIndex, 1);
    }
  }

  definitions.rootElement
    ?.filter((e) => e.__$$element === "correlationProperty")
    .forEach((property) => {
      if (property.__$$element === "correlationProperty") {
        property.correlationPropertyRetrievalExpression =
          property.correlationPropertyRetrievalExpression?.filter((cpre) => cpre["@_messageRef"] !== messageId) ?? [];
      }
    });

  const { process } = addOrGetProcessAndDiagramElements({ definitions });

  // Delete from all flow elements
  visitFlowElementsAndArtifacts(process, ({ array, index, owner, element }) => {
    if (array != owner.flowElement) {
      throw new Error(
        `BPMN MUTATION: Element with id ${messageId} is not a flowElement, but rather a ${element.__$$element}`
      );
    }

    if (
      array[index].__$$element === "endEvent" ||
      array[index].__$$element === "intermediateThrowEvent" ||
      array[index].__$$element === "startEvent" ||
      array[index].__$$element === "intermediateCatchEvent"
    ) {
      for (const eventDefinition of array[index]?.eventDefinition ?? []) {
        if (
          eventDefinition &&
          eventDefinition.__$$element === "messageEventDefinition" &&
          eventDefinition["@_messageRef"] === messageId
        ) {
          delete eventDefinition["@_drools:msgref"];
          delete eventDefinition["@_messageRef"];
        }
      }
    }
  });
}
