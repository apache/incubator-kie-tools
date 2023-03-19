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
import { render } from "@testing-library/react";
import { context, Context } from "uniforms/cjs";
import createContext from "./_createContext";
import { codeGenContext, CodeGenContext } from "../uniforms/CodeGenContext";
import { FormElement } from "../api";

const TestCodeGenContextProvider: React.FC<any> = (props: any) => {
  const ctx: Context<any> = createContext(props.schema);
  return (
    <codeGenContext.Provider value={props.ctx}>
      <context.Provider value={ctx}>{props.children}</context.Provider>
    </codeGenContext.Provider>
  );
};

export type RenderedField<Element extends FormElement, Container extends Element | DocumentFragment> = {
  formElement: Element;
  container: Container;
};

export const renderField = (Field: React.FC, props: any, schema?: any) => {
  const codegenContext: CodeGenContext = {
    rendered: [],
  };

  const { container } = render(
    <TestCodeGenContextProvider ctx={codegenContext} schema={schema}>
      <Field {...props} />
    </TestCodeGenContextProvider>
  );

  expect(codegenContext.rendered).toHaveLength(1);

  return {
    container,
    formElement: codegenContext.rendered[0],
  };
};

export const renderFields = (Field: React.FC, props: any, schema?: any) => {
  const codegenContext: CodeGenContext = {
    rendered: [],
  };

  const { container } = render(
    <TestCodeGenContextProvider ctx={codegenContext} schema={schema}>
      <Field {...props} />
    </TestCodeGenContextProvider>
  );

  expect(codegenContext.rendered).not.toHaveLength(0);

  return codegenContext.rendered;
};
