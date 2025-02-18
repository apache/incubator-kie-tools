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

import listField from "!!raw-loader!../../resources/templates/listField.template";
import globalFunctions from "!!raw-loader!../../resources/templates/listField.globalFunctions.template";
import modifyListSize from "!!raw-loader!../../resources/staticCode/modifyListSize.txt";
import { FormElementTemplate, FormElementTemplateProps } from "./types";
import { FormInputContainer, InputReference } from "../../api";
import { CompiledTemplate, template } from "underscore";
import { fieldNameToOptionalChain } from "./utils";

interface ListFieldTemplateProps extends FormElementTemplateProps<any> {
  children: {
    ref: { id: string; binding: string }[];
    html: string;
    disabled: boolean;
    setValueFromModelCode: { code: string; requiredCode: string[] };
    writeValueToModelCode: { code: string; requiredCode: string[] };
  };
  maxCount: number;
  minCount: number;
}

export class ListFieldTemplate implements FormElementTemplate<FormInputContainer, ListFieldTemplateProps> {
  private readonly listFieldTemplate: CompiledTemplate = template(listField);
  private readonly listFieldGlobalFunctionsTemplate: CompiledTemplate = template(globalFunctions);
  // private readonly listFieldSetValueFromModelTemplate: CompiledTemplate = template(setValueFromModel);
  // private readonly listFieldWriteValueToModelTemplate: CompiledTemplate = template(writeValueToModel);

  render({
    id,
    disabled,
    label,
    children,
    maxCount,
    minCount,
    value,
    name,
  }: ListFieldTemplateProps): FormInputContainer {
    const ref: InputReference[] = children.ref;

    const setValueFromModelRequiredCode: string[] = children.setValueFromModelCode.requiredCode;
    const writeValueToModelRequiredCode: string[] = children.writeValueToModelCode.requiredCode;

    const getDefaultItemValue = () => {
      return "{}";
    };

    return {
      ref,
      html: this.listFieldTemplate({
        id,
        disabled,
        label,
        name,
        value,
        childrenHtml: children.html,
      }),
      disabled: disabled,
      globalFunctions: {
        code: this.listFieldGlobalFunctionsTemplate({
          defaultValue: getDefaultItemValue(),
          disabled,
          minCount,
          maxCount,
          childrenHtml: `${children.html}`,
          name,
          path: fieldNameToOptionalChain(name),
        }),
        requiredCode: [modifyListSize],
      },
    };
  }
}
