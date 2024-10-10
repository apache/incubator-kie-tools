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

import setRequiredCode from "!!raw-loader!../../resources/staticCode/setSelectValue.txt";
import setMultipleRequiredCode from "!!raw-loader!../../resources/staticCode/setSelecMultipletValue.txt";
import getMultipleRequiredCode from "!!raw-loader!../../resources/staticCode/getMultipleSelectValue.txt";
import input from "!!raw-loader!../../resources/templates/select.template";
import setValueFromModel from "!!raw-loader!../../resources/templates/select.setModelData.template";
import writeValueToModel from "!!raw-loader!../../resources/templates/select.writeModelData.template";
import { FORM_GROUP_TEMPLATE, FormElementTemplate, FormElementTemplateProps } from "./types";
import { CompiledTemplate, template } from "underscore";
import { CodeFragment, FormInput } from "../../api";
import { fieldNameToOptionalChain } from "./utils";
import { getInputReference } from "../utils/Utils";

export interface Option {
  value: string;
  label: string;
  checked: boolean;
}

export interface SelectFieldProps extends FormElementTemplateProps<string> {
  placeHolder: string;
  multiple: boolean;
  options: Option[];
}

export class SelectFieldTemplate implements FormElementTemplate<FormInput, SelectFieldProps> {
  private readonly inputTemplate: CompiledTemplate;
  private readonly setValueFromModelTemplate: CompiledTemplate;
  private readonly writeValueToModelTemplate: CompiledTemplate;

  constructor() {
    this.inputTemplate = template(input);
    this.setValueFromModelTemplate = template(setValueFromModel);
    this.writeValueToModelTemplate = template(writeValueToModel);
  }

  render(props: SelectFieldProps): FormInput {
    const data = {
      props: props,
      input: this.inputTemplate({ props: props }),
    };

    return {
      ref: getInputReference(props),
      html: FORM_GROUP_TEMPLATE(data),
      disabled: props.disabled,
      setValueFromModelCode: this.buildSetValueFromModelCode(props),
      writeValueToModelCode: this.writeValueToModelCode(props),
    };
  }

  protected buildSetValueFromModelCode(props: SelectFieldProps): CodeFragment {
    const properties = {
      ...props,
      path: fieldNameToOptionalChain(props.name),
    };
    return {
      code: this.setValueFromModelTemplate(properties),
      requiredCode: props.multiple ? [setMultipleRequiredCode] : [setRequiredCode],
    };
  }

  protected writeValueToModelCode(props: SelectFieldProps): CodeFragment | undefined {
    if (props.disabled) {
      return undefined;
    }

    return {
      requiredCode: props.multiple ? [getMultipleRequiredCode] : undefined,
      code: this.writeValueToModelTemplate(props),
    };
  }
}
