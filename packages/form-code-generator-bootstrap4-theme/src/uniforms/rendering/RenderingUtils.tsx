/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import * as ReactDOMServer from "react-dom/server";
import { Bridge } from "uniforms/cjs";
import { FormElement, FormInput } from "../../api";
import FormInputs from "./FormInputs";
import NestedFieldInput from "./NestedFieldInput";
import { BootstrapCodeGenContext } from "../BootstrapCodeGenContext";
import ListFieldInput, { ListItemProps } from "./ListFieldInput";

export const renderFormInputs = (schema: Bridge): FormElement<any>[] => {
  const codegenCtx: BootstrapCodeGenContext = {
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
  itemProps: ListItemProps,
  disabled?: boolean
): FormInput | undefined => {
  const codegenCtx: BootstrapCodeGenContext = {
    rendered: [],
  };

  ReactDOMServer.renderToString(
    React.createElement(NestedFieldInput, {
      codegenCtx,
      uniformsContext,
      field,
      itemProps,
      disabled,
    })
  );

  return codegenCtx.rendered.length === 1 ? codegenCtx.rendered[0] : undefined;
};

export const renderListItemFragmentWithContext = (
  uniformsContext: any,
  fieldName: string,
  itemProps: ListItemProps,
  disabled?: boolean
): FormInput | undefined => {
  const codegenCtx: BootstrapCodeGenContext = {
    rendered: [],
  };

  ReactDOMServer.renderToString(
    React.createElement(ListFieldInput, {
      codegenCtx,
      uniformsContext,
      fieldName,
      itemProps,
      disabled,
    })
  );

  return codegenCtx.rendered.length === 1 ? codegenCtx.rendered[0] : undefined;
};
