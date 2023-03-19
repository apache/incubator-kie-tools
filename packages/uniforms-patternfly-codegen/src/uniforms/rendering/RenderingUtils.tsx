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
import * as ReactDOMServer from "react-dom/server";
import { Bridge } from "uniforms/cjs";
import { FormElement, FormInput } from "../../api";
import FormInputs from "./FormInputs";
import { CodeGenContext } from "../CodeGenContext";
import NestedFieldInput from "./NestedFieldInput";

export const renderFormInputs = (schema: Bridge): FormElement[] => {
  const codegenCtx: CodeGenContext = {
    rendered: [],
  };

  const inputsElement = React.createElement(FormInputs, {
    codegenCtx,
    schema,
  });

  ReactDOMServer.renderToString(inputsElement);

  return codegenCtx.rendered;
};

export const renderNestedInputFragmentWithContext = (
  uniformsContext: any,
  field: any,
  itempProps: any,
  disabled?: boolean
): FormInput | undefined => {
  const codegenCtx: CodeGenContext = {
    rendered: [],
  };

  ReactDOMServer.renderToString(
    React.createElement(NestedFieldInput, {
      codegenCtx,
      uniformsContext,
      field,
      itempProps,
      disabled,
    })
  );

  return codegenCtx.rendered.length == 1 ? codegenCtx.rendered[0] : undefined;
};
