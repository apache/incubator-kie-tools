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
  path: string;
  type: string;
  overwrite: boolean;
}

export function generateForms({ path, type, overwrite }: Args) {
  console.log("\nStarting Form generation:");

  try {
    const tool: FormGenerationTool = lookupFormGenerationTool(type);

    const forms: FormSchema[] = loadProjectSchemas(path);

    if (forms.length === 0) {
      console.log(`\nCouldn't find any form schema in "${path}", check if your project is already built.`);
      return;
    }

    console.log(`\nFound ${forms.length} schemas`);

    forms.forEach((form) => {
      try {
        console.log(`\nGenerating form "${form.name}"`);
        const output: FormAsset = tool.generate(form);
        storeFormAsset(output, path, overwrite);
        console.log(`Successfully generated form "${output.assetName}"`);
      } catch (err) {
        console.log(`Cannot generate form "${form.name}": `, err.message);
      }
    });
  } catch (err) {
    console.log("Error during form generation:");
    console.log(err.message);
  }
}
