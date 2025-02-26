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
import setValueFromModel from "!!raw-loader!../../resources/templates/listField.setModelData.template";
import writeValueToModel from "!!raw-loader!../../resources/templates/listField.writeModelData.template";
import modifyList from "!!raw-loader!../../resources/staticCode/modifyList.txt";
import { FormElementTemplate, FormElementTemplateProps } from "./AbstractFormGroupTemplate";
import { FormElement, FormInputContainer, InputReference } from "../../api";
import { CompiledTemplate, template } from "underscore";
import { fieldNameToOptionalChain } from "./utils";
import { LIST_INDEX_VARIABLE_NAME } from "../ListField";

interface ListFieldTemplateProps extends FormElementTemplateProps<any> {
  children: FormElement<any>;
  maxCount: number;
  minCount: number;
}

export class ListFieldTemplate implements FormElementTemplate<FormInputContainer, ListFieldTemplateProps> {
  private readonly listFieldTemplate: CompiledTemplate = template(listField);
  private readonly listFieldGlobalFunctionsTemplate: CompiledTemplate = template(globalFunctions);
  private readonly listFieldSetValueFromModelTemplate: CompiledTemplate = template(setValueFromModel);
  private readonly listFieldWriteValueToModelTemplate: CompiledTemplate = template(writeValueToModel);

  render({
    id,
    disabled,
    label,
    children,
    maxCount,
    minCount,
    value,
    name,
    itemProps,
  }: ListFieldTemplateProps): FormInputContainer {
    const ref: InputReference[] = children.ref;

    const getDefaultItemValue = () => {
      return "undefined";
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
        functionName: getFunctionName(name),
      }),
      disabled: disabled,
      globalFunctions: {
        code:
          this.listFieldGlobalFunctionsTemplate({
            defaultValue: getDefaultItemValue(),
            disabled,
            minCount,
            maxCount,
            childrenHtml: `${children.html}`,
            name,
            functionName: getFunctionName(name),
          }) + (children?.globalFunctions?.code !== undefined ? children?.globalFunctions?.code : ""),
        requiredCode: [modifyList],
      },
      setValueFromModelCode: {
        code: this.listFieldSetValueFromModelTemplate({
          id,
          path: getSetItemPath(name),
          indexVariableName: LIST_INDEX_VARIABLE_NAME,
          itemsSetValueFromModel: children.setValueFromModelCode,
          name: getCurrentItemSetModelData(name),
        }),
        requiredCode: [],
      },
      writeValueToModelCode: {
        code: this.listFieldWriteValueToModelTemplate({
          name,
        }),
        requiredCode: [],
      },
    };
  }
}

// "value" is the name used in the listField.setModelData.template
function getSetItemPath(name: string) {
  return fieldNameToOptionalChain(name).includes(`$`) ? "value" : `data?.${fieldNameToOptionalChain(name)}`;
}

// "currentItem" is the name used in the listField.setModelData.template
export function getCurrentItemSetModelData(name: string) {
  const splittedName = name.split(`.$`);
  // Is nested
  if (splittedName.length > 1) {
    // ${currentItem}. with last part of the field name
    return "${currentItem}" + (splittedName?.[splittedName.length - 1] ?? "");
  }
  return name;
}

function getFunctionName(name: string) {
  return name
    .split(".")
    .map((word) => `${word?.[0]?.toUpperCase()}${word?.slice(1)}`)
    .join("");
}
