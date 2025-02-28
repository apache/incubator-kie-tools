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
import formGroupTemplate from "!!raw-loader!../../resources/templates/formGroup.template";
import { FormElementTemplate, FormElementTemplateProps } from "./AbstractFormGroupTemplate";
import { CompiledTemplate, template } from "underscore";
import { FormInput } from "../../api";
import { fieldNameToOptionalChain, getItemValeuPath } from "./utils";
import { getInputReference } from "../utils/Utils";
import { DEFAULT_LIST_INDEX_NAME, getCurrentItemSetModelData, getNormalizedListIdOrName } from "./ListFieldTemplate";

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
  private readonly formGroupTemplate: CompiledTemplate;

  constructor() {
    this.inputTemplate = template(input);
    this.setValueFromModelTemplate = template(setValueFromModel);
    this.writeValueToModelTemplate = template(writeValueToModel);
    this.formGroupTemplate = template(formGroupTemplate);
  }

  render(props: RadioGroupFieldProps): FormInput {
    return {
      ref: getInputReference(props),
      html: this.formGroupTemplate({
        id: props.itemProps?.isListItem ? getNormalizedListIdOrName(props.id) : props.id,
        label: props.label,
        input: this.inputTemplate({
          id: props.itemProps?.isListItem ? getNormalizedListIdOrName(props.id) : props.id,
          name: props.itemProps?.isListItem ? getNormalizedListIdOrName(props.name) : props.name,
          options: props.options,
          disabled: props.disabled,
        }),
        isListItem: props.itemProps?.isListItem ?? false,
      }),
      disabled: props.disabled,
      globalFunctions: undefined,
      setValueFromModelCode: {
        requiredCode: [setRequiredCode],
        code: this.setValueFromModelTemplate({
          ...props,
          name: props.itemProps?.isListItem
            ? getCurrentItemSetModelData(props.name, props.itemProps?.indexVariableName ?? DEFAULT_LIST_INDEX_NAME)
            : props.name,
          path: fieldNameToOptionalChain(props.name),
          valuePath: props.itemProps?.isListItem ? getItemValeuPath(props.name) : "",
          isListItem: props.itemProps?.isListItem ?? false,
        }),
      },
      writeValueToModelCode: props.disabled
        ? undefined
        : {
            requiredCode: [getRequiredCode],
            code: this.writeValueToModelTemplate(props),
          },
    };
  }
}
