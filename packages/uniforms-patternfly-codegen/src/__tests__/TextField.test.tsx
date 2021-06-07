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
import { render } from "@testing-library/react";
import { TestCodeGenContextProvider } from "./TestCodegenContextProvider";
import { CodeGenContext } from "../uniforms/CodeGenContext";
import { InputReference } from "../api";
import TextField from "../uniforms/TextField";

const schema = {
  name: "test schema",
  type: "object",
  properties: {
    name: {
      type: "string",
    },
  },
};

describe("TextField test", () => {
  it("TextInput rendering", () => {
    const codegenContext: CodeGenContext = {
      rendered: [],
    };

    const props = {
      id: "id",
      label: "label",
      name: "name",
      disabled: false,
      onChange: jest.fn(),
    };

    const { container } = render(
      <TestCodeGenContextProvider ctx={codegenContext} schema={schema}>
        <TextField {...props} />
      </TestCodeGenContextProvider>
    );

    expect(codegenContext.rendered).toHaveLength(1);

    const field = codegenContext.rendered[0];

    expect(field.reactImports).toContain("useState");
    expect(field.pfImports).toContain("FormGroup");
    expect(field.pfImports).toContain("TextInput");
    const ref = field.ref as InputReference;
    expect(ref.binding).toBe(props.name);
    expect(ref.stateName).toBe(props.name);
    expect(ref.stateSetter).toBe(`set__${props.name}`);

    expect(field.jsxCode).not.toBeNull();
    expect(field.stateCode).not.toBeNull();
  });
});
