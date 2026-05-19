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
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { addOrGetItemDefinitions, DEFAULT_DATA_TYPES } from "./addOrGetItemDefinitions";

// Reserved ID for the shared ItemDefinition used by all messages and linked by the message @_itemRef.
export const RESERVED_ITEM_DEFINITION_ID_FOR_MESSAGES = "__messageItemDefinition";

export function addOrGetMessages({
  definitions,
  messageName,
  dataType,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  messageName: string;
  dataType?: string;
}): {
  messageRef: string;
} {
  definitions.rootElement ??= [];
  const messages = definitions.rootElement.filter((s) => s.__$$element === "message");

  let itemDefinitionId: string;

  if (dataType) {
    const { itemDefinition } = addOrGetItemDefinitions({
      definitions: definitions,
      dataType: dataType,
    });
    itemDefinitionId = itemDefinition["@_id"];
  } else {
    const itemDefinitions = definitions.rootElement.filter((s) => s.__$$element === "itemDefinition");
    const itemDefinitionForMessages = itemDefinitions.find(
      (s) => s["@_id"] === RESERVED_ITEM_DEFINITION_ID_FOR_MESSAGES
    );

    if (!itemDefinitionForMessages) {
      addOrGetItemDefinitions({
        definitions: definitions,
        dataType: DEFAULT_DATA_TYPES.OBJECT,
        id: RESERVED_ITEM_DEFINITION_ID_FOR_MESSAGES,
      });
    }
    itemDefinitionId = RESERVED_ITEM_DEFINITION_ID_FOR_MESSAGES;
  }

  const existingMessage = messages.find((s) => s["@_name"] === messageName);

  if (existingMessage) {
    if (existingMessage["@_itemRef"] !== itemDefinitionId) {
      existingMessage["@_itemRef"] = itemDefinitionId;
    }
    return { messageRef: existingMessage["@_id"] };
  }

  const newMessage: ElementFilter<Unpacked<Normalized<BPMN20__tDefinitions["rootElement"]>>, "message"> = {
    __$$element: "message",
    "@_id": generateUuid(),
    "@_itemRef": itemDefinitionId,
    "@_name": messageName,
  };

  definitions.rootElement.push(newMessage);
  return { messageRef: newMessage["@_id"] };
}
