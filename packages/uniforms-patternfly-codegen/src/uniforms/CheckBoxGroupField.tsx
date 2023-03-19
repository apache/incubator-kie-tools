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
import { connectField, HTMLFieldProps } from "uniforms/cjs";
import { buildDefaultInputElement, getInputReference, renderField } from "./utils/Utils";
import { FormInput, InputReference } from "../api";
import { useAddFormElementToContext } from "./CodeGenContext";
import { CHECKBOX_GROUP_FUNCTIONS } from "./staticCode/staticCodeBlocks";
import { ARRAY } from "./utils/dataTypes";

export type CheckBoxGroupProps = HTMLFieldProps<
  string[],
  HTMLDivElement,
  {
    name: string;
    label: string;
    allowedValues?: string[];
    required: boolean;
    transform?(value: string): string;
  }
>;

const CheckBoxGroup: React.FC<CheckBoxGroupProps> = (props: CheckBoxGroupProps) => {
  const ref: InputReference = getInputReference(props.name, ARRAY);

  const jsxCode = props.allowedValues
    ?.map((value) => {
      return `<Checkbox key={'${props.id}-${value}'} id={'${props.id}-${value}'} name={'${props.name}'} aria-label={'${
        props.name
      }'}
               label={'${props.transform ? props.transform(value) : value}'} 
               isDisabled={${props.disabled || false}} 
               isChecked={${ref.stateName}.indexOf('${value}') != -1}
               onChange={() => handleCheckboxGroupChange('${value}', ${ref.stateName}, ${ref.stateSetter})}
               value={'${value}'}/>`;
    })
    .join("\n");

  const element: FormInput = buildDefaultInputElement({
    pfImports: ["Checkbox"],
    inputJsxCode: jsxCode || "",
    ref: ref,
    requiredCode: [CHECKBOX_GROUP_FUNCTIONS],
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

export default connectField(CheckBoxGroup);
