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

import { FormCodeGeneratorTheme, FormAsset } from "@kie-tools/form-code-generator/dist/types";
import { renderForm } from "@kie-tools/form-code-generator-bootstrap4-theme/dist";
import unescape from "lodash/unescape";
import JSONSchemaBridge from "uniforms-bridge-json-schema";
import { getUniformsSchema } from "./getUniformsSchema";
import { inputSanitizationUtil } from "./inputSanitizationUtil";
import { JbpmFormAssetBase } from "./types";

export const BOOTSTRAP4_CSS_URL = "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css";
export const BOOTSTRAP4_JS_URL = "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.bundle.min.js";
export const JQUERY_URL = "https://code.jquery.com/jquery-3.2.1.slim.min.js";

const BOOTSTRAP4_STYLE = "bootstrap";
const BOOTSTRAP4_FILE_EXT = "html";

export type Bootstrap4Style = typeof BOOTSTRAP4_STYLE;
export type Bootstrap4FileExt = typeof BOOTSTRAP4_FILE_EXT;

export interface Bootstrap4FormAsset extends FormAsset<Bootstrap4FileExt>, JbpmFormAssetBase {}

export const jbpmBootstrap4FormCodeGeneratorTheme: FormCodeGeneratorTheme<
  Bootstrap4FileExt,
  Bootstrap4Style,
  Bootstrap4FormAsset
> = {
  theme: BOOTSTRAP4_STYLE,
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
      assetName: `${formSchema.name}.${BOOTSTRAP4_FILE_EXT}`,
      sanitizedAssetName: `${inputSanitizationUtil(formSchema.name)}.${BOOTSTRAP4_FILE_EXT}`,
      type: BOOTSTRAP4_FILE_EXT,
      content: unescape(form),
      config: {
        schema: JSON.stringify(formSchema.schema),
        resources: {
          styles: {
            "bootstrap.min.css": BOOTSTRAP4_CSS_URL,
          },
          scripts: {
            "jquery.js": JQUERY_URL,
            "bootstrap.bundle.min.js": BOOTSTRAP4_JS_URL,
          },
        },
      },
    };
  },
};
