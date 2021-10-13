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

export interface FormElement {
  reactImports: string[];
  pfImports: string[];

  requiredCode?: string[];

  ref: InputReference;

  stateCode: string;
  jsxCode: string;
  isReadonly: boolean;
}

abstract class AbstractFormElement implements FormElement {
  jsxCode: string;
  pfImports: string[];
  reactImports: string[];
  requiredCode?: string[];
  ref: InputReference;
  stateCode: string;
  isReadonly: boolean;
}

export class FormInput extends AbstractFormElement {}

export class InputsContainer extends AbstractFormElement {
  childRefs: InputReference[];
}

export interface InputReference {
  binding: string;
  dataType: DataType;

  stateName: string;
  stateSetter: string;
}

export interface DataType {
  name: string;
  defaultValue?: string;
}
