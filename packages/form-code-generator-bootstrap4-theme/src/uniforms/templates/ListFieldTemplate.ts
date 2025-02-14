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
import { FormElementTemplate, FormElementTemplateProps } from "./types";
import { CodeFragment, FormElement, FormInputContainer, InputReference } from "../../api";
import { CompiledTemplate, template } from "underscore";
import { union } from "lodash";

interface ListFieldTemplateProps extends FormElementTemplateProps<any> {
  children: {
    ref: { id: string; binding: string }[];
    html: string;
    disabled: boolean;
    setValueFromModelCode: { code: string; requiredCode: string[] };
    writeValueToModelCode: { code: string; requiredCode: string[] };
  };
}

export class ListFieldTemplate implements FormElementTemplate<FormInputContainer, ListFieldTemplateProps> {
  private readonly listFieldTemplate: CompiledTemplate = template(listField);
  private readonly listFieldSetValueFromModelTemplate: CompiledTemplate = template(setValueFromModel);
  private readonly listFieldWriteValueToModelTemplate: CompiledTemplate = template(writeValueToModel);

  render(props: ListFieldTemplateProps): FormInputContainer {
    const ref: InputReference[] = props.children.ref;

    const setValueFromModelRequiredCode: string[] = props.children.setValueFromModelCode.requiredCode;
    const writeValueToModelRequiredCode: string[] = props.children.writeValueToModelCode.requiredCode;

    return {
      ref,
      html: this.listFieldTemplate({ props: props }),
      disabled: props.disabled,
      setValueFromModelCode: {
        code: this.listFieldSetValueFromModelTemplate({ props: props }),
        requiredCode: setValueFromModelRequiredCode,
      },
      writeValueToModelCode:
        props.disabled === true
          ? undefined
          : {
              code: this.listFieldWriteValueToModelTemplate({ props: props }),
              requiredCode: writeValueToModelRequiredCode,
            },
    };
  }
}
