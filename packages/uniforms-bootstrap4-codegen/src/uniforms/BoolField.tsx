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
import { FormInput } from "../api";

import { useAddFormElementToBootstrapContext } from "./BootstrapCodeGenContext";
import { CHECKBOX, renderCodeGenElement } from "./templates/templates";

export type BoolFieldProps = HTMLFieldProps<
  boolean,
  HTMLDivElement,
  {
    name: string;
    label: string;
  }
>;

const Bool: React.FC<BoolFieldProps> = (props: BoolFieldProps) => {
  const properties = {
    id: props.name,
    name: props.name,
    label: props.label,
    disabled: props.disabled ?? false,
    checked: props.value ?? false,
  };

  const element: FormInput = renderCodeGenElement(CHECKBOX, properties);

  useAddFormElementToBootstrapContext(element);

  return <>{JSON.stringify(element)}</>;
};

export default connectField(Bool);
