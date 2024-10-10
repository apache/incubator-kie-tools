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
import { renderForm } from "@kie-tools/form-code-generator-patternfly-theme/dist";
import { PATTERNFLY_FILE_EXT, PatternflyFileExt } from "@kie-tools/form-code-generator-patternfly-theme/dist/theme";
import unescape from "lodash/unescape";
import JSONSchemaBridge from "uniforms-bridge-json-schema";
import { getUniformsSchema } from "./getUniformsSchema";
import { inputSanitizationUtil } from "./inputSanitizationUtil";
import { JbpmFormAssetBase } from "./types";

export interface PatternflyFormAsset extends FormAsset<PatternflyFileExt>, JbpmFormAssetBase {}

export const jbpmPatternflyFormCodeGeneratorTheme: FormCodeGeneratorTheme<PatternflyFileExt, PatternflyFormAsset> = {
  generate: (formSchema) => {
    const uniformsSchema = getUniformsSchema(formSchema.schema);
    const form = renderForm({
      id: formSchema.name,
      sanitizedId: inputSanitizationUtil(formSchema.name),
      schema: new JSONSchemaBridge(uniformsSchema, () => true),
      disabled: false,
      placeholder: true,
    });
    return {
      id: formSchema.name,
      sanitizedId: inputSanitizationUtil(formSchema.name),
      assetName: `${formSchema.name}.${PATTERNFLY_FILE_EXT}`,
      sanitizedAssetName: `${inputSanitizationUtil(formSchema.name)}.${PATTERNFLY_FILE_EXT}`,
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
