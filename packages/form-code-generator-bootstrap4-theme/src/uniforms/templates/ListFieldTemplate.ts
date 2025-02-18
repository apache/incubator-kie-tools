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
import setValueFromModel from "!!raw-loader!../../resources/templates/listField.setModelData.template";
import writeValueToModel from "!!raw-loader!../../resources/templates/listField.writeModelData.template";
import addListItem from "!!raw-loader!../../resources/staticCode/addListItem.txt";
import delListItem from "!!raw-loader!../../resources/staticCode/delListItem.txt";
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
  }: ListFieldTemplateProps): FormInputContainer {
    const ref: InputReference[] = children.ref;

    const setValueFromModelRequiredCode: string[] = children.setValueFromModelCode.requiredCode;
    const writeValueToModelRequiredCode: string[] = children.writeValueToModelCode.requiredCode;

    const getDefaultItemValue = () => {
      return "{}";
      // const typeName = listItem?.ref.dataType.name;
      // if (typeName?.endsWith("[]")) {
      //   return listItem?.ref.dataType.defaultValue ?? [];
      // }
      // switch (typeName) {
      //   case "string":
      //     ref.dataType = DEFAULT_DATA_TYPE_STRING_ARRAY;
      //     return listItem?.ref.dataType.defaultValue ?? "";
      //   case "number":
      //     ref.dataType = DEFAULT_DATA_TYPE_NUMBER_ARRAY;
      //     return listItem?.ref.dataType.defaultValue ?? null;
      //   case "boolean":
      //     ref.dataType = DEFAULT_DATA_TYPE_BOOLEAN_ARRAY;
      //     return listItem?.ref.dataType.defaultValue ?? false;
      //   case "object":
      //     ref.dataType = DEFAULT_DATA_TYPE_OBJECT_ARRAY;
      //     return listItem?.ref.dataType.defaultValue ?? {};
      //   default: // any
      //     ref.dataType = DEFAULT_DATA_TYPE_ANY_ARRAY;
      //     return listItem?.ref.dataType.defaultValue;
      // }
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
      setValueFromModelCode: {
        code: this.listFieldSetValueFromModelTemplate({
          defaultValue: getDefaultItemValue(),
          disabled,
          minCount,
          maxCount,
          childrenHtml: `${children.html}`,
          name,
          path: fieldNameToOptionalChain(name),
        }),
        requiredCode: [delListItem, addListItem],
      },
      writeValueToModelCode:
        disabled === true
          ? undefined
          : {
              code: this.listFieldWriteValueToModelTemplate(),
              requiredCode: [],
            },
    };
  }
}
