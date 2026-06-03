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
import { visitFlowElementsAndArtifacts } from "./_elementVisitor";
import { addOrGetProcessAndDiagramElements } from "./addOrGetProcessAndDiagramElements";
import { createReservedItemDefinitionForMessages, RESERVED_ITEM_DEFINITION_ID_FOR_MESSAGES } from "./addOrGetMessages";

export function renameMessage({
  definitions,
  id,
  newMessageName,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  id: string;
  newMessageName: string;
}):
  | {
      message: ElementFilter<Unpacked<Normalized<BPMN20__tDefinitions["rootElement"]>>, "message">;
    }
  | undefined {
  if (definitions.rootElement === undefined) {
    throw new Error(`BPMN MUTATION: Model without root element`);
  }
  const existingMessageIndex = definitions.rootElement?.findIndex((s) => s["@_id"] === id);
  if (existingMessageIndex === undefined || existingMessageIndex < 0) {
    throw new Error(`BPMN MUTATION: Message with id ${id} is not in the model`);
  }

  // Rename message name
  const message = definitions.rootElement[existingMessageIndex] as ElementFilter<
    Unpacked<Normalized<BPMN20__tDefinitions["rootElement"]>>,
    "message"
  >;
  message["@_name"] = newMessageName;

  const { process } = addOrGetProcessAndDiagramElements({ definitions });

  // Rename on all flow elements
  visitFlowElementsAndArtifacts(process, ({ array, index, owner, element }) => {
    if (array != owner.flowElement) {
      throw new Error(`BPMN MUTATION: Element with id ${id} is not a flowElement, but rather a ${element.__$$element}`);
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
          eventDefinition["@_messageRef"] === id
        ) {
          eventDefinition["@_drools:msgref"] = newMessageName;
        }
      }
    }
  });

  return { message };
}

export function updateMessageItemDefinition({
  definitions,
  messageId,
  newItemDefinitionRef,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  messageId: string;
  newItemDefinitionRef: string | undefined;
}) {
  const message = definitions.rootElement?.find((e) => e.__$$element === "message" && e["@_id"] === messageId);

  if (!message || message.__$$element !== "message") {
    return;
  }

  if (!newItemDefinitionRef) {
    createReservedItemDefinitionForMessages({ definitions });
  }

  message["@_itemRef"] = newItemDefinitionRef || RESERVED_ITEM_DEFINITION_ID_FOR_MESSAGES;

  const newItemDef = newItemDefinitionRef
    ? definitions.rootElement?.find((e) => e.__$$element === "itemDefinition" && e["@_id"] === newItemDefinitionRef)
    : undefined;
  const newDataType =
    newItemDef && newItemDef.__$$element === "itemDefinition" ? newItemDef["@_structureRef"] || "" : "";

  const process = definitions.rootElement?.find((e) => e.__$$element === "process");
  if (process && process.__$$element === "process") {
    process.flowElement?.forEach((flowElement) => {
      if ("eventDefinition" in flowElement) {
        const messageEventDef = flowElement.eventDefinition?.find(
          (ed) => ed.__$$element === "messageEventDefinition" && ed["@_messageRef"] === messageId
        );

        if (messageEventDef) {
          if ("dataInput" in flowElement && flowElement.dataInput) {
            flowElement.dataInput.forEach((dataInput: any) => {
              dataInput["@_drools:dtype"] = newDataType;
            });
          }

          if ("dataOutput" in flowElement && flowElement.dataOutput) {
            flowElement.dataOutput.forEach((dataOutput: any) => {
              dataOutput["@_drools:dtype"] = newDataType;
            });
          }
        }
      }
    });
  }

  const hasMessagesUsingReservedItemDef = definitions.rootElement?.some(
    (e) => e.__$$element === "message" && e["@_itemRef"] === RESERVED_ITEM_DEFINITION_ID_FOR_MESSAGES
  );
  if (!hasMessagesUsingReservedItemDef) {
    definitions.rootElement = definitions.rootElement?.filter(
      (e) => !(e.__$$element === "itemDefinition" && e["@_id"] === RESERVED_ITEM_DEFINITION_ID_FOR_MESSAGES)
    );
  }
}
