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

import { FormAsset, FormCodeGeneratorTheme } from "@kie-tools/form-code-generator/dist/types";
import JSONSchemaBridge from "uniforms-bridge-json-schema";
import unescape from "lodash/unescape";
import { renderForm } from ".";

export const PATTERNFLY_FILE_EXT = "tsx";

export type PatternflyFileExt = typeof PATTERNFLY_FILE_EXT;
export interface PatternflyFormAsset extends FormAsset<PatternflyFileExt> {}

export const patternflyFormCodeGeneratorTheme: FormCodeGeneratorTheme<PatternflyFileExt, PatternflyFormAsset> = {
  generate: (formSchema) => {
    const form = renderForm({
      id: formSchema.name,
      sanitizedId: formSchema.name,
      schema: new JSONSchemaBridge(formSchema.schema, () => true),
      disabled: false,
      placeholder: true,
    });
    return {
      id: formSchema.name,
      assetName: `${formSchema.name}.${PATTERNFLY_FILE_EXT}`,
      type: PATTERNFLY_FILE_EXT,
      content: unescape(form),
      config: {
        schema: JSON.stringify(formSchema.schema),
        resources: {
          styles: {},
          scripts: {},
        },
      },
    };
  },
};
