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

import formGroupTemplate from "!!raw-loader!../../resources/templates/formGroup.template";
import { CodeFragment, CodeGenElement, FormElement, FormInput } from "../../api";
import { CompiledTemplate, template } from "underscore";
import { getInputReference } from "../utils/Utils";
import { fieldNameToOptionalChain, flatFieldName } from "./utils";

export interface CodeGenTemplate<Element extends CodeGenElement, Properties> {
  render: (props: Properties) => Element;
}

export interface FormElementTemplateProps<Type> {
  id: string;
  name: string;
  label: string;
  disabled: boolean;
  value: Type;
}

export interface FormElementTemplate<
  Element extends FormElement<any>,
  Properties extends FormElementTemplateProps<any>
> {
  render: (props: Properties) => Element;
}

export const FORM_GROUP_TEMPLATE: CompiledTemplate = template(formGroupTemplate);

export abstract class AbstractFormGroupInputTemplate<Properties extends FormElementTemplateProps<any>>
  implements FormElementTemplate<FormInput, Properties>
{
  protected constructor(
    readonly inputTemplate: CompiledTemplate,
    readonly setValueFromModelTemplate: CompiledTemplate,
    readonly writeValueToModelTemplate: CompiledTemplate
  ) {}

  render(props: Properties): FormInput {
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

  protected buildSetValueFromModelCode(props: Properties): CodeFragment {
    const properties = {
      ...props,
      path: fieldNameToOptionalChain(props.name),
      flatFieldName: flatFieldName(props.name),
    };

    return {
      code: this.setValueFromModelTemplate(properties),
    };
  }

  protected writeValueToModelCode(props: Properties): CodeFragment | undefined {
    if (props.disabled || !this.writeValueToModelTemplate) {
      return undefined;
    }

    const properties = {
      ...props,
      flatFieldName: flatFieldName(props.name),
    };

    return {
      code: this.writeValueToModelTemplate(properties),
    };
  }
}
