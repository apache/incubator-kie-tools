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

import React from "react";
import { CheckboxProps } from "@patternfly/react-core";
import { connectField } from "uniforms/es5";
import { useAddFormElementToContext } from "./CodeGenContext";
import { FormInput, InputReference } from "../api";

import { getInputReference, getStateCodeFromRef, renderField } from "./utils/Utils";

export type BoolFieldProps = {
  appearance?: "checkbox" | "switch";
  label: string;
  disabled: boolean;
  required: boolean;
  onChange: (value?: string) => void;
} & Omit<CheckboxProps, "isDisabled">;

const Bool: React.FC<BoolFieldProps> = (props: BoolFieldProps) => {
  const ref: InputReference = getInputReference(props.name);

  const stateCode = getStateCodeFromRef(ref, "boolean");

  const jsxCode = `<FormGroup fieldId='${props.id}'>
    <Checkbox
      isChecked={${ref.stateName}}
      isDisabled={${props.disabled || "false"}}
      id={'${props.id}'}
      name={'${props.name}'}
      label={'${props.label}'}
      onChange={${ref.stateSetter}}
    />
  </FormGroup>`;

  const element: FormInput = {
    ref,
    pfImports: ["Checkbox", "FormGroup"],
    reactImports: ["useState"],
    jsxCode,
    stateCode,
  };

  useAddFormElementToContext(element);

  return renderField(element);
};

export default connectField(Bool);
