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

import input from "!!raw-loader!../../resources/templates/input.template";
import { template } from "underscore";
import { AbstractFormGroupInputTemplate, FormElementTemplateProps } from "./types";

interface TextFieldProps extends FormElementTemplateProps<string> {
  type: string;
  autoComplete: boolean;
  placeholder: string;
}

export class TextFieldTemplate extends AbstractFormGroupInputTemplate<TextFieldProps> {
  constructor() {
    super(template(input));
  }
}
