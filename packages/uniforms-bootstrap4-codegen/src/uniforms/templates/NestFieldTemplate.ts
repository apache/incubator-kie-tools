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

import nestField from "!!raw-loader!../../resources/templates/nestField.template";
import setValueFromModel from "!!raw-loader!../../resources/templates/nestField.setModelData.template";
import writeValueToModel from "!!raw-loader!../../resources/templates/nestField.writeModelData.template";
import { FormElementTemplate, FormElementTemplateProps } from "./types";
import { CodeFragment, FormElement, FormInputContainer, InputReference } from "../../api";
import { CompiledTemplate, template } from "underscore";
import { union } from "lodash";

interface NestFieldTemplateProps extends FormElementTemplateProps<any> {
  children: FormElement<any>[];
}

export class NestFieldTemplate implements FormElementTemplate<FormInputContainer, NestFieldTemplateProps> {
  private readonly nestFieldTemplate: CompiledTemplate = template(nestField);
  private readonly nestFieldSetValueFromModelTemplate: CompiledTemplate = template(setValueFromModel);
  private readonly nestFieldWriteValueToModelTemplate: CompiledTemplate = template(writeValueToModel);

  render(props: NestFieldTemplateProps): FormInputContainer {
    const ref: InputReference[] = [];

    let setValueFromModelRequiredCode: string[] = [];
    let writeValueToModelRequiredCode: string[] = [];

    props.children.forEach((child: FormElement<any>) => {
      if (Array.isArray(child.ref)) {
        child.ref.forEach((childRef) => ref.push(childRef));
      } else {
        ref.push(child.ref);
      }

      if (child.setValueFromModelCode) {
        setValueFromModelRequiredCode = union(setValueFromModelRequiredCode, child.setValueFromModelCode.requiredCode);
      }

      if (child.writeValueToModelCode) {
        writeValueToModelRequiredCode = union(writeValueToModelRequiredCode, child.writeValueToModelCode.requiredCode);
      }
    });

    return {
      ref,
      html: this.nestFieldTemplate({ props: props }),
      disabled: props.disabled,
      setValueFromModelCode: this.buildSetValueFromModelCode(props, setValueFromModelRequiredCode),
      writeValueToModelCode: this.buildWriteValueFromModelCode(props, writeValueToModelRequiredCode),
    };
  }

  protected buildSetValueFromModelCode(
    props: NestFieldTemplateProps,
    setValueFromModelRequiredCode: string[]
  ): CodeFragment {
    return {
      code: this.nestFieldSetValueFromModelTemplate({ props: props }),
      requiredCode: setValueFromModelRequiredCode,
    };
  }

  protected buildWriteValueFromModelCode(
    props: NestFieldTemplateProps,
    writeValueToModelRequiredCode: string[]
  ): CodeFragment | undefined {
    if (props.disabled) {
      return undefined;
    }
    return {
      code: this.nestFieldWriteValueToModelTemplate({ props: props }),
      requiredCode: writeValueToModelRequiredCode,
    };
  }
}
