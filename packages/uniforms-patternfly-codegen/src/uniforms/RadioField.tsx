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
import { connectField } from "uniforms";
import { useCodegenContext } from "./CodeGenContext";
import { FormInput, InputReference } from "../api";
import { buildDefaultInputElement, getInputReference, renderField } from "./utils/Utils";

export type RadioFieldProps = {
  id: string;
  name: string;
  label: string;
  allowedValues: string[];
  onChange: (value: string) => void;
  disabled: boolean;
  required: boolean;
  transform?: (value?: string) => string;
};

const Radio = (props: RadioFieldProps) => {
  const codegenContext = useCodegenContext();
  const ref: InputReference = getInputReference(props.name);

  const radios: string[] = [];

  props.allowedValues?.forEach((item) => {
    const radio = `<Radio
      key={'${item}'}
      id={'${props.id}-${item}'}
      name={'${props.name}'}
      isChecked={'${item}' === ${ref.stateName}}
      isDisabled={${props.disabled || false}}
      label={'${props.transform ? props.transform(item) : item}'}
      aria-label={'${props.name}'}
      onChange={() => ${ref.stateSetter}('${item}')}
    />`;
    radios.push(radio);
  });

  const jsxCode = `<div>${radios.join("\n")}</div>`;

  const element: FormInput = buildDefaultInputElement({
    pfImports: ["Radio"],
    inputJsxCode: jsxCode,
    ref,
    dataType: "string",
    wrapper: {
      id: props.id,
      label: props.label,
      required: props.required,
    },
  });

  codegenContext.rendered.push(element);

  return renderField(element);
};

export default connectField(Radio);
