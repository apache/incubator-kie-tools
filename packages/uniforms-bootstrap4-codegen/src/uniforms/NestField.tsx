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

import React, { useContext } from "react";
import { connectField, context, HTMLFieldProps } from "uniforms/cjs";
import { renderNestedInputFragmentWithContext } from "./rendering/RenderingUtils";
import { FormElement, FormInput, FormInputContainer } from "../api";
import { useBootstrapCodegenContext } from "./BootstrapCodeGenContext";
import { NESTED, renderCodeGenElement } from "./templates/templates";

export type NestFieldProps = HTMLFieldProps<object, HTMLDivElement, { itemProps?: object }>;

const Nest: React.FunctionComponent<NestFieldProps> = ({
  id,
  children,
  error,
  errorMessage,
  fields,
  itemProps,
  label,
  name,
  showInlineError,
  disabled,
  ...props
}: NestFieldProps) => {
  const uniformsContext = useContext(context);
  const codegenCtx = useBootstrapCodegenContext();

  const nestedFields: FormElement<any>[] = [];
  if (fields) {
    fields.forEach((field) => {
      const nestedElement = renderNestedInputFragmentWithContext(uniformsContext, field, itemProps, disabled);
      if (nestedElement) {
        nestedFields.push(nestedElement);
      } else {
        console.log(`Cannot render form field for: '${field}'`);
      }
    });
  }

  const properties = {
    id: name,
    name: name,
    label: label,
    disabled: disabled,
    children: nestedFields,
  };

  const element: FormInputContainer = renderCodeGenElement(NESTED, properties);
  codegenCtx?.rendered.push(element);
  return <>{JSON.stringify(element)}</>;
};

export default connectField(Nest);
