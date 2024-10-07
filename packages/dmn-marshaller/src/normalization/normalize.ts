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

import { DmnLatestModel } from "..";
import { getNewDmnIdRandomizer } from "../idRandomizer/dmnIdRandomizer";
import { DMN15__tDefinitions } from "../schemas/dmn-1_5/ts-gen/types";

export type Normalized<T> = WithRequiredDeep<T, "@_id">;

type WithRequiredDeep<T, K extends keyof any> = T extends undefined
  ? T
  : T extends Array<infer U>
    ? Array<WithRequiredDeep<U, K>>
    : { [P in keyof T]: WithRequiredDeep<T[P], K> } & (K extends keyof T
        ? { [P in K]-?: NonNullable<WithRequiredDeep<T[P], K>> }
        : T);

export function normalize(model: DmnLatestModel): Normalized<DmnLatestModel> {
  getNewDmnIdRandomizer()
    .ack({
      json: model.definitions.drgElement,
      type: "DMN15__tDefinitions",
      attr: "drgElement",
    })
    .ack({
      json: model.definitions.artifact,
      type: "DMN15__tDefinitions",
      attr: "artifact",
    })
    .ack({
      json: model.definitions["dmndi:DMNDI"],
      type: "DMN15__tDefinitions",
      attr: "dmndi:DMNDI",
    })
    .ack({
      json: model.definitions.import,
      type: "DMN15__tDefinitions",
      attr: "import",
    })
    .ack({
      json: model.definitions.itemDefinition,
      type: "DMN15__tDefinitions",
      attr: "itemDefinition",
    })
    .randomize({ skipAlreadyAttributedIds: true });

  const normalizedModel = model as Normalized<DmnLatestModel>;

  addMissingImportNamespaces(normalizedModel.definitions);

  return normalizedModel;
}

function addMissingImportNamespaces(definitions: Normalized<DMN15__tDefinitions>) {
  if (definitions.import === undefined) {
    return;
  }

  // Collect all declared namespaces
  const definedNamespaces = new Set(
    Object.keys(definitions)
      .filter((keys: keyof DMN15__tDefinitions) => String(keys).startsWith("@_xmlns:"))
      .map((xmlnsKey: keyof DMN15__tDefinitions) => definitions[xmlnsKey])
  );

  // Add missing import namespace declarations as `xmlns:included*`
  let includedIndex = 0;
  for (let index = 0; index < definitions.import.length; index++) {
    const importedModelNamespace = definitions.import[index]["@_namespace"];

    // Check if namespace is already declared
    if (definedNamespaces.has(importedModelNamespace)) {
      // Ignore namespaces that are already declared
      continue;
    }

    // Get next available `included*` namespace declaration name
    while (definitions[`@_xmlns:included${includedIndex}`]) {
      includedIndex++;
    }

    definitions[`@_xmlns:included${includedIndex}`] = importedModelNamespace;
  }
}
