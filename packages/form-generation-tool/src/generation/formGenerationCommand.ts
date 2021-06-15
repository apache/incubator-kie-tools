/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { lookupFormGenerationTool } from "./tools";
import { FormGenerationTool, FormSchema, FormAsset } from "./types";
import { loadProjectSchemas, storeFormAsset } from "./fs";

export interface Args {
  source: string;
  type: string;
  overwrite: boolean;
}

export function generateForms({ source, type, overwrite }: Args) {
  console.log("Starting Form generation:");
  console.log(`Kogito Project path: '${source}'`);
  console.log(`Form type: '${type}'`);
  console.log(`Overwrite existing forms: '${overwrite}'`);

  try {
    const tool: FormGenerationTool = lookupFormGenerationTool(type);

    const forms: FormSchema[] = loadProjectSchemas(source);

    if (forms.length === 0) {
      console.log(`Couldn't find any form in "${source}", check if your project is already built.`);
      return;
    }

    console.log(`Found ${forms.length} schemas`);

    forms.forEach((form) => {
      try {
        console.log(`Generating form "${form.name}"`);
        const output: FormAsset = tool.generate(form);
        storeFormAsset(output, source, overwrite);
        console.log(`Successfully generated form "${form.name}"`);
      } catch (err) {
        console.log(`Error generating form "${form.name}": `, err.message);
      }
    });
  } catch (err) {
    console.log(`Cannot generate forms in '${source}':`);
    console.log(err.message);
  }
}
