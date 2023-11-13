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

import setRequiredCode from "!!raw-loader!../../resources/staticCode/setRadioGroupValue.txt";
import getRequiredCode from "!!raw-loader!../../resources/staticCode/getRadioGroupValue.txt";
import input from "!!raw-loader!../../resources/templates/radioGroup.template";
import setValueFromModel from "!!raw-loader!../../resources/templates/radioGroup.setModelData.template";
import writeValueToModel from "!!raw-loader!../../resources/templates/radioGroup.writeModelData.template";
import {
  AbstractFormGroupInputTemplate,
  FORM_GROUP_TEMPLATE,
  FormElementTemplate,
  FormElementTemplateProps,
} from "./types";
import { CompiledTemplate, template } from "underscore";
import { CodeFragment, FormInput } from "../../api";
import { fieldNameToOptionalChain, flatFieldName } from "./utils";
import { getInputReference } from "../utils/Utils";

export interface Option {
  value: string;
  label: string;
  checked: boolean;
}

export interface RadioGroupFieldProps extends FormElementTemplateProps<string> {
  options: Option[];
}

export class RadioGroupFieldTemplate implements FormElementTemplate<FormInput, RadioGroupFieldProps> {
  private readonly inputTemplate: CompiledTemplate;
  private readonly setValueFromModelTemplate: CompiledTemplate;
  private readonly writeValueToModelTemplate: CompiledTemplate;

  constructor() {
    this.inputTemplate = template(input);
    this.setValueFromModelTemplate = template(setValueFromModel);
    this.writeValueToModelTemplate = template(writeValueToModel);
  }

  render(props: RadioGroupFieldProps): FormInput {
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

  protected buildSetValueFromModelCode(props: RadioGroupFieldProps): CodeFragment {
    const properties = {
      ...props,
      path: fieldNameToOptionalChain(props.name),
    };

    return {
      requiredCode: [setRequiredCode],
      code: this.setValueFromModelTemplate(properties),
    };
  }

  protected writeValueToModelCode(props: RadioGroupFieldProps): CodeFragment | undefined {
    if (props.disabled) {
      return undefined;
    }

    return {
      requiredCode: [getRequiredCode],
      code: this.writeValueToModelTemplate(props),
    };
  }
}
