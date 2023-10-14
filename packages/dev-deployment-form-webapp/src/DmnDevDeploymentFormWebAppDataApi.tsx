/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { ExtendedServicesDmnJsonSchema } from "@kie-tools/extended-services-api";
import { routes } from "./Routes";

export interface FormData {
  uri: string;
  modelName: string;
  schema: ExtendedServicesDmnJsonSchema;
}

export interface AppData {
  baseUrl: string;
  forms: FormData[];
}

export type DmnDefinitionsJson = FormData;

export async function fetchAppData(): Promise<AppData> {
  const response = await fetch(routes.dmnDefinitionsJson.path({}));
  const dmnDefinitionsJson = (await response.json()) as ExtendedServicesDmnJsonSchema;
  dmnDefinitionsJson["$ref"] = "#/definitions/InputSet";

  console.log({ response, dmnDefinitionsJson });

  const inputRef = dmnDefinitionsJson["$ref"]!.replace("#/definitions/", "");
  const schema = JSON.parse(JSON.stringify(dmnDefinitionsJson).replace(new RegExp(inputRef, "g"), "InputSet"));

  return {
    // ...appData,
    baseUrl: "..",
    // The input set property associated with the mainURI is InputSetX, where X is a number not always 1.
    // So replace all occurrences InputSetX -> InputSet to keep compatibility with the current DmnForm.
    forms: [
      {
        // ...dmnDefinitionsJson,
        schema: schema,
        uri: "/Sample.dmn",
        modelName: "loan_pre_qualification",
      },
    ],
  };
}
