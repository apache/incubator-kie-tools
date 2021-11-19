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
import { INPUT, renderCodeGenElement } from "./templates/templates";
import { useAddFormElementToBootstrapContext } from "./BootstrapCodeGenContext";
import { FormInput } from "../api";

export type TextFieldProps = HTMLFieldProps<
  string,
  HTMLInputElement,
  {
    label: string;
    required: boolean;
  }
>;

const Text: React.FC<TextFieldProps> = (props: TextFieldProps) => {
  const properties = {
    id: props.name,
    name: props.name,
    label: props.label,
    type: props.type ?? "text",
    disabled: props.disabled ?? false,
    placeholder: props.placeholder,
    autoComplete: props.autoComplete ?? false,
    value: props.value,
  };

  const element: FormInput = renderCodeGenElement(INPUT, properties);
  useAddFormElementToBootstrapContext(element);
  return <>{JSON.stringify(element)}</>;
};

export default connectField(Text, { kind: "leaf" });
