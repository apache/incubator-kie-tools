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

export async function fetchAppData(): Promise<AppData> {
  const response = await fetch(routes.dataJson.path({}));
  const appData = (await response.json()) as AppData;

  return {
    ...appData,
    // The input set property associated with the mainURI is InputSetX, where X is a number not always 1.
    // So replace all occurrences InputSetX -> InputSet to keep compatibility with the current DmnForm.
    forms: appData.forms.map((form: any) => {
      const inputRef = form.schema["$ref"].replace("#/definitions/", "");
      const schema = JSON.parse(JSON.stringify(form.schema).replace(new RegExp(inputRef, "g"), "InputSet"));
      return {
        ...form,
        schema: schema,
      };
    }),
  };
}
