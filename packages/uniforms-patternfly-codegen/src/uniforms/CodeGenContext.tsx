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

import React, { createContext, useContext } from "react";

import { Bridge, context, Context, randomIds } from "uniforms";

import { FormElement } from "../api";

export interface CodeGenContext {
  rendered: FormElement<any>[];
}

export const codeGenContext = createContext<CodeGenContext>(null);

export const useCodegenContext = (): CodeGenContext => {
  return useContext<CodeGenContext>(codeGenContext);
};

export const useAddFormElementToContext = (formElement: FormElement<any>): void => {
  const ctx = useCodegenContext();
  if (!ctx) {
    throw new Error(`'useAddFormElementToContext' should be called within a 'codegenContext'`);
  }
  ctx.rendered.push(formElement);
};

export interface ProviderProps {
  schema?: Bridge;
  codegenCtx: CodeGenContext;
  uniformsCtx?: Context<any>;
  children: JSX.Element;
}

export const CodeGenContextProvider: React.FC<ProviderProps> = (props) => {
  const ctx: Context<any> = props.uniformsCtx || {
    changed: false,
    changedMap: undefined,
    error: false,
    model: undefined,
    name: [],
    onChange: undefined,
    onSubmit: undefined,
    randomId: randomIds(),
    schema: props.schema,
    state: {
      disabled: false,
      label: true,
      placeholder: true,
      showInlineError: true,
    },
    submitting: false,
    validating: false,
  };

  return (
    <codeGenContext.Provider value={props.codegenCtx}>
      <context.Provider value={ctx}>{props.children}</context.Provider>
    </codeGenContext.Provider>
  );
};
