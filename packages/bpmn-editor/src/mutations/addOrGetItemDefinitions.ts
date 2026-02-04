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

export const DEFAULT_DATA_TYPES = {
  STRING: "String",
  BOOLEAN: "Boolean",
  FLOAT: "Float",
  INTEGER: "Integer",
  OBJECT: "Object",
};

export function addOrGetItemDefinitions({
  definitions,
  dataType,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  dataType: string;
}): {
  itemDefinition: ElementFilter<Unpacked<Normalized<BPMN20__tDefinitions["rootElement"]>>, "itemDefinition">;
} {
  definitions.rootElement ??= [];
  const itemDefinitions = definitions.rootElement.filter((s) => s.__$$element === "itemDefinition");

  const existingItemDefinition = itemDefinitions.find((s) => s["@_structureRef"] === dataType);
  if (existingItemDefinition) {
    return { itemDefinition: existingItemDefinition };
  }

  const newItemDefinition: ElementFilter<
    Unpacked<Normalized<BPMN20__tDefinitions["rootElement"]>>,
    "itemDefinition"
  > = {
    __$$element: "itemDefinition",
    "@_id": generateUuid(),
    "@_structureRef": dataType,
  };

  definitions.rootElement.push(newItemDefinition);
  return { itemDefinition: newItemDefinition };
}
