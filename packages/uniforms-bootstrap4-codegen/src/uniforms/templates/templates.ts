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

import { CodeGenElement, FormElement } from "../../api";
import { TextFieldTemplate } from "./TextFieldTemplate";
import { NumFieldTemplate } from "./NumFieldTemplate";
import { CodeGenTemplate, FormElementTemplate } from "./types";
import { BoolFieldTemplate } from "./BoolFieldTemplate";
import { DateFieldTemplate } from "./DateFieldTemplate";
import { RadioGroupFieldTemplate } from "./RadioGroupFieldTemplate";
import { CheckBoxGroupFieldTemplate } from "./CheckboxGroupFieldTemplate";
import { SelectFieldTemplate } from "./SelectFieldTemplate";
import { NestFieldTemplate } from "./NestFieldTemplate";
import { AutoFormTemplate } from "./AutoFormTemplate";
import { UnsupportedFieldTemplate } from "./UnsupportedTemplate";

export const FORM: string = "form";
export const CHECKBOX: string = "checkbox";
export const CHECKBOXGROUP: string = "checkboxGroup";
export const DATE: string = "date";
export const INPUT: string = "input";
export const NESTED: string = "nestField";
export const NUMBER: string = "number";
export const RADIOGROUP: string = "radioGroup";
export const SELECT: string = "select";
export const UNSUPPORTED: string = "unsupported";

const _templates: Map<string, CodeGenTemplate<any, any>> = new Map<string, CodeGenTemplate<any, any>>();
try {
  _templates.set(FORM, new AutoFormTemplate());
  _templates.set(INPUT, new TextFieldTemplate());
  _templates.set(CHECKBOX, new BoolFieldTemplate());
  _templates.set(CHECKBOXGROUP, new CheckBoxGroupFieldTemplate());
  _templates.set(DATE, new DateFieldTemplate());
  _templates.set(NESTED, new NestFieldTemplate());
  _templates.set(NUMBER, new NumFieldTemplate());
  _templates.set(RADIOGROUP, new RadioGroupFieldTemplate());
  _templates.set(SELECT, new SelectFieldTemplate());
  _templates.set(UNSUPPORTED, new UnsupportedFieldTemplate());
} catch (err) {
  console.log(err);
}

const getTemplate = (templateId: string): CodeGenTemplate<any, any> => {
  const inputTemplate = _templates.get(templateId);

  if (!inputTemplate) {
    throw new Error(`Cannot find template: ${templateId}`);
  }

  return inputTemplate;
};

export function renderCodeGenElement<Element extends CodeGenElement>(templateId: string, props: any): Element {
  return getTemplate(templateId).render(props);
}
