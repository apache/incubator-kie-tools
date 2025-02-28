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

import unsupported from "!!raw-loader!../../resources/templates/unsupported.template";
import { FormElementTemplate, FormElementTemplateProps } from "./AbstractFormGroupTemplate";
import { CompiledTemplate, template } from "underscore";
import { FormInput } from "../../api";
import { getInputReference } from "../utils/Utils";

export interface UnsupportedFieldProps extends FormElementTemplateProps<any> {
  fieldType: string;
}

export class UnsupportedFieldTemplate implements FormElementTemplate<FormInput, UnsupportedFieldProps> {
  private readonly unsupportedTemplate: CompiledTemplate;

  constructor() {
    this.unsupportedTemplate = template(unsupported);
  }

  render(props: UnsupportedFieldProps): FormInput {
    return {
      ref: getInputReference(props),
      html: this.unsupportedTemplate({ props }),
      globalFunctions: undefined,
      setValueFromModelCode: undefined,
      writeValueToModelCode: undefined,
    };
  }
}
