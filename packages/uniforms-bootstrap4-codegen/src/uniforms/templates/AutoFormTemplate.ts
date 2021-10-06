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

import form from "!!raw-loader!../../resources/templates/form.template";
import * as prettier from "prettier";
import trim from "lodash/trim";
import { CodeGenTemplate, FormElementTemplate, FormElementTemplateProps } from "./types";
import { CompiledTemplate, template } from "underscore";
import { CodeGenElement, FormElement, FormInput } from "../../api";

export interface AutoFormProps {
  elements: FormElement<any>[];
}

export class AutoFormTemplate implements CodeGenTemplate<CodeGenElement, AutoFormProps> {
  private readonly formTemplate: CompiledTemplate;

  constructor() {
    this.formTemplate = template(form);
  }

  render(props: AutoFormProps): CodeGenElement {
    const data = {
      props: props,
    };

    const rawTemplate = trim(this.formTemplate(data));

    const formattedFormTemplate = prettier.format(rawTemplate, {
      parser: "html",
      printWidth: 140,
      tabWidth: 2,
      useTabs: false,
    });

    return {
      html: formattedFormTemplate,
    };
  }
}
