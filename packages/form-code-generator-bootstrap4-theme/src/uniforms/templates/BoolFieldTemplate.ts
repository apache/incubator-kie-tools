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

import checkbox from "!!raw-loader!../../resources/templates/checkbox.template";
import checkboxSetValueFromModel from "!!raw-loader!../../resources/templates/checkbox.setModelData.template";
import checkboxWriteModelData from "!!raw-loader!../../resources/templates/checkbox.writeModelData.template";
import { FormElementTemplate, FormElementTemplateProps } from "./AbstractFormGroupTemplate";
import { FormInput } from "../../api";
import { CompiledTemplate, template } from "underscore";
import { getInputReference } from "../utils/Utils";
import { fieldNameToOptionalChain, getItemValeuPath } from "./utils";
import { DEFAULT_LIST_INDEX_NAME, getCurrentItemSetModelData, getNormalizedListIdOrName } from "./ListFieldTemplate";

interface BoolFieldProps extends FormElementTemplateProps<boolean> {
  checked: boolean;
}

export class BoolFieldTemplate implements FormElementTemplate<FormInput, BoolFieldProps> {
  private readonly checkboxTemplate: CompiledTemplate;
  private readonly checkboxSetValueFromModelTemplate: CompiledTemplate;
  private readonly checkboxWriteModelTemplate: CompiledTemplate;

  constructor() {
    this.checkboxTemplate = template(checkbox);
    this.checkboxSetValueFromModelTemplate = template(checkboxSetValueFromModel);
    this.checkboxWriteModelTemplate = template(checkboxWriteModelData);
  }

  render(props: BoolFieldProps): FormInput {
    return {
      ref: getInputReference(props),
      html: this.checkboxTemplate({
        id: props.itemProps?.isListItem ? getNormalizedListIdOrName(props.id) : props.id,
        name: props.itemProps?.isListItem ? getNormalizedListIdOrName(props.name) : props.name,
        disabled: props.disabled,
        checked: props.checked,
        label: props.label,
      }),
      disabled: props.disabled,
      globalFunctions: undefined,
      setValueFromModelCode: {
        code: this.checkboxSetValueFromModelTemplate({
          id: props.itemProps?.isListItem
            ? getCurrentItemSetModelData(props.id, props.itemProps?.indexVariableName ?? DEFAULT_LIST_INDEX_NAME)
            : props.id,
          path: fieldNameToOptionalChain(props.name),
          valuePath: props.itemProps?.isListItem ? getItemValeuPath(props.name) : "",
          isListItem: props.itemProps?.isListItem ?? false,
        }),
      },
      writeValueToModelCode: props.disabled
        ? undefined
        : {
            code: this.checkboxWriteModelTemplate({
              id: props.id,
              name: props.name,
            }),
          },
    };
  }
}
