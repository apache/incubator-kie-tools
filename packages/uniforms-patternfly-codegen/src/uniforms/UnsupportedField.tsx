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

import * as React from "react";
import { FormInput, InputReference } from "../api";
import { buildDefaultInputElement, getInputReference, renderField } from "./utils/Utils";
import { connectField, HTMLFieldProps } from "uniforms/cjs";
import { useAddFormElementToContext } from "./CodeGenContext";
import { ANY_ARRAY, OBJECT } from "./utils/dataTypes";

export type UnsupportedFieldProps = HTMLFieldProps<
  any,
  HTMLDivElement,
  {
    label: string;
    required: boolean;
  }
>;

const Unsupported: React.FC<UnsupportedFieldProps> = (props: UnsupportedFieldProps) => {
  const isArray: boolean = props.fieldType === Array;
  const ref: InputReference = getInputReference(props.name, isArray ? ANY_ARRAY : OBJECT);

  const jsxCode = `<Alert variant='warning' title='Unsupported field type: ${props.fieldType.name}'> 
        Cannot find form control for property <code>${props.name}</code> with type <code>${props.fieldType.name}</code>:<br/> 
        Some complex property types, such as <code>Array&lt;object&gt;</code> aren't yet supported, however, you can still write your 
        own component into the form and use the already existing states <code>const [ ${ref.stateName}, ${ref.stateSetter} ]</code>.
    </Alert>`;

  const element: FormInput = buildDefaultInputElement({
    pfImports: ["Alert"],
    inputJsxCode: jsxCode,
    ref,
    wrapper: {
      id: props.id,
      label: props.label,
      required: props.required,
    },
    disabled: props.disabled,
  });

  useAddFormElementToContext(element);
  return renderField(element);
};

export default connectField(Unsupported);
