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

import select from "!!raw-loader!../../resources/templates/select.template";
import { AbstractFormGroupInputTemplate, FormElementTemplateProps } from "./types";
import { template } from "underscore";

export interface Option {
  value: string;
  label: string;
  checked: boolean;
}

export interface SelectFieldProps extends FormElementTemplateProps<string> {
  placeHolder: string;
  multiple: boolean;
  options: Option[];
}

export class SelectFieldTemplate extends AbstractFormGroupInputTemplate<SelectFieldProps> {
  constructor() {
    super(template(select));
  }
}
