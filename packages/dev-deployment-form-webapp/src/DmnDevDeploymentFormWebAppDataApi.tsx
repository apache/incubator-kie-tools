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

import { ExtendedServicesDmnJsonSchema } from "@kie-tools/extended-services-api";
import { routes } from "./Routes";

export interface FormData {
  modelName: string;
  schema: ExtendedServicesDmnJsonSchema;
}

export interface AppData {
  baseUrl: string;
  forms: FormData[];
}

export type DmnDefinitionsJson = FormData;

function fromiAsciiSpacing(value: string) {
  return value.replace(new RegExp("_32", "g"), " ").replace(new RegExp("__", "g"), "_");
}

export async function fetchAppData(): Promise<AppData> {
  const response = await fetch(routes.dmnDefinitionsJson.path({}));
  const dmnDefinitionsJson = (await response.json()) as ExtendedServicesDmnJsonSchema;

  if (!dmnDefinitionsJson.definitions) {
    throw new Error("No DMN definitions available.");
  }

  const forms = Object.keys(dmnDefinitionsJson.definitions)
    .filter((key: string) => key.startsWith("InputSet"))
    .map((asciiSpacedInputSetRef) => {
      const modelName = fromiAsciiSpacing(asciiSpacedInputSetRef.replace("InputSet", ""));
      const fullDmnDefinitions = {
        $ref: `#/definitions/${asciiSpacedInputSetRef}`,
        ...dmnDefinitionsJson,
      };

      // The input set property associated with a model is InputSetX, where X is the model name (with some character substitutions).
      // So replace all occurrences of InputSetX -> InputSet to keep compatibility with the current DmnForm.
      const inputRef = fullDmnDefinitions["$ref"]!.replace("#/definitions/", "");
      const schema = JSON.parse(JSON.stringify(fullDmnDefinitions).replace(new RegExp(inputRef, "g"), "InputSet"));
      return {
        modelName,
        schema,
      };
    });

  return {
    baseUrl: "..",
    forms,
  };
}
