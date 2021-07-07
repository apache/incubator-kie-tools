/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import nestFieldTemplate from "!!raw-loader!../../resources/templates/nestField.template";
import { FormElementTemplateProps, FormElementTemplate } from "./types";
import { FormElement, FormInputContainer, InputReference } from "../../api";
import { CompiledTemplate, template } from "underscore";
import { union } from "lodash";

interface NestFieldTemplateProps extends FormElementTemplateProps<any> {
  children: FormElement<any>[];
}

export class NestFieldTemplate implements FormElementTemplate<FormInputContainer, NestFieldTemplateProps> {
  private readonly nestFieldTemplate: CompiledTemplate = template(nestFieldTemplate);

  render(props: NestFieldTemplateProps): FormInputContainer {
    const ref: InputReference[] = [];
    let requiredCode: string[] = [];

    props.children.forEach((child: FormElement<any>) => {
      if (Array.isArray(child.ref)) {
        child.ref.forEach((childRef) => ref.push(childRef));
      } else {
        ref.push(child.ref);
      }
      if (child.requiredCode) {
        requiredCode = union(requiredCode, child.requiredCode);
      }
    });

    return {
      ref,
      html: this.nestFieldTemplate({ props: props }),
      requiredCode,
    };
  }
}
