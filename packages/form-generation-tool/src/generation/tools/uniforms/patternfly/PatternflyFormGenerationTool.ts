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

import unescape from "lodash/unescape";
import { FormAssetType, FormAsset, FormStyle, FormConfig, FormGenerationTool, FormSchema } from "../../../types";

import { renderForm } from "@kie-tools/uniforms-patternfly-codegen/dist";
import JSONSchemaBridge from "uniforms-bridge-json-schema";
import { getUniformsSchema } from "../utils/UniformsSchemaUtils";
import { inputSanitizationUtil } from "../utils/InputSanitizationUtil";

export class PatternflyFormConfig implements FormConfig {
  public readonly schema: string;

  constructor(formSchema: any) {
    this.schema = JSON.stringify(formSchema);
  }

  public resources = {
    styles: {},
    scripts: {},
  };
}

export class PatternflyFormGenerationTool implements FormGenerationTool {
  type: string = FormStyle.PATTERNFLY;

  generate(inputSchema: FormSchema): FormAsset {
    const uniformsSchema = getUniformsSchema(inputSchema.schema);
    const form = renderForm({
      id: inputSchema.name,
      sanitizedId: inputSanitizationUtil(inputSchema.name),
      schema: new JSONSchemaBridge(uniformsSchema, () => true),
      disabled: false,
      placeholder: true,
    });
    return {
      id: inputSchema.name,
      sanitizedId: inputSanitizationUtil(inputSchema.name),
      assetName: `${inputSchema.name}.${FormAssetType.TSX}`,
      sanitizedAssetName: `${inputSanitizationUtil(inputSchema.name)}.${FormAssetType.TSX}`,
      type: FormAssetType.TSX,
      content: unescape(form),
      config: new PatternflyFormConfig(inputSchema.schema),
    };
  }
}
