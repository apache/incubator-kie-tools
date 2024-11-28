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
import { FormElementTemplate, FormElementTemplateProps } from "./types";
import { CodeFragment, FormInput } from "../../api";
import { CompiledTemplate, template } from "underscore";
import { getInputReference } from "../utils/Utils";
import { fieldNameToOptionalChain } from "./utils";

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
    const data = {
      props: props,
    };
    return {
      ref: getInputReference(props),
      html: this.checkboxTemplate(data),
      disabled: props.disabled,
      setValueFromModelCode: this.buildSetValueFromModelCode(props),
      writeValueToModelCode: this.buildWriteModelDataCode(props),
    };
  }

  protected buildSetValueFromModelCode(props: BoolFieldProps): CodeFragment {
    const properties = {
      id: props.id,
      path: fieldNameToOptionalChain(props.name),
    };
    return {
      code: this.checkboxSetValueFromModelTemplate(properties),
    };
  }

  protected buildWriteModelDataCode(props: BoolFieldProps): CodeFragment | undefined {
    if (props.disabled) {
      return undefined;
    }
    const properties = {
      id: props.id,
      name: props.name,
    };
    return {
      code: this.checkboxWriteModelTemplate(properties),
    };
  }
}
