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
import { FormInput } from "../api";
import { connectField, HTMLFieldProps } from "uniforms/cjs";
import { renderCodeGenElement, UNSUPPORTED } from "./templates/templates";
import { useAddFormElementToBootstrapContext } from "./BootstrapCodeGenContext";

export type UnsupportedFieldProps = HTMLFieldProps<
  any,
  HTMLDivElement,
  {
    label: string;
    required: boolean;
  }
>;

const Unsupported: React.FC<UnsupportedFieldProps> = (props: UnsupportedFieldProps) => {
  const properties = {
    id: props.id,
    label: props.label,
    name: props.name,
    fieldType: props.fieldType.name,
  };

  const element: FormInput = renderCodeGenElement(UNSUPPORTED, properties);
  useAddFormElementToBootstrapContext(element);
  return <>{JSON.stringify(element)}</>;
};

export default connectField(Unsupported);
