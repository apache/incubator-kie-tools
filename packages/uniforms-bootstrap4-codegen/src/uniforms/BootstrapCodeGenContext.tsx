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

import { Bridge, context, Context, randomIds } from "uniforms/cjs";

import { FormElement } from "../api";

export interface BootstrapCodeGenContext {
  rendered: FormElement<any>[];
}

export const bootstrapCodeGenContext = createContext<BootstrapCodeGenContext | null>(null);

export const useBootstrapCodegenContext = (): BootstrapCodeGenContext | null => {
  return useContext<BootstrapCodeGenContext | null>(bootstrapCodeGenContext);
};

export const useAddFormElementToBootstrapContext = (formElement: FormElement<any>): void => {
  const ctx = useBootstrapCodegenContext();
  if (!ctx) {
    throw new Error(`'useAddFormElementToBootstrapContext' should be called within a 'bootstrapCodeGenContext'`);
  }
  ctx.rendered.push(formElement);
};

export interface ProviderProps {
  schema: Bridge;
  codegenCtx: BootstrapCodeGenContext;
  uniformsCtx?: Context<any>;
  children: JSX.Element;
}

export const CodeGenContextProvider: React.FC<ProviderProps> = (props) => {
  const ctx: Context<any> = props.uniformsCtx || {
    changed: false,
    changedMap: {},
    error: false,
    model: {},
    name: [],
    onChange: (key, value) => {},
    onSubmit: (event) => {},
    randomId: randomIds(),
    schema: props.schema,
    state: {
      readOnly: false,
      disabled: false,
      label: true,
      placeholder: true,
      showInlineError: true,
    },
    submitted: false,
    submitting: false,
    validating: false,
  };

  return (
    <bootstrapCodeGenContext.Provider value={props.codegenCtx}>
      <context.Provider value={ctx}>{props.children}</context.Provider>
    </bootstrapCodeGenContext.Provider>
  );
};
