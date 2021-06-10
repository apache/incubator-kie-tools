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
import SimpleSchema from "simpl-schema";
import { TestCodeGenContextProvider } from "./TestCodegenContextProvider";
import { CodeGenContext } from "../uniforms/CodeGenContext";
import { InputReference } from "../api";
import NumField from "../uniforms/NumField";

describe("NumField test", () => {
  it("NumField - integer rendering", () => {
    const schema = {
      age: { type: SimpleSchema.Integer },
    };

    const codegenContext: CodeGenContext = {
      rendered: [],
    };

    const props = {
      id: "id",
      label: "age",
      name: "age",
      min: 10,
      max: 15,
      disabled: false,
      onChange: jest.fn(),
    };

    render(
      <TestCodeGenContextProvider ctx={codegenContext} schema={schema}>
        <NumField {...props} />
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
    expect(field.jsxCode).toContain(`step={1}`);
    expect(field.jsxCode).toContain(`min={${props.min}}`);
    expect(field.jsxCode).toContain(`max={${props.max}}`);
    expect(field.stateCode).not.toBeNull();
  });

  it("NumField - decimal rendering", () => {
    const schema = {
      salary: { type: Number },
    };

    const codegenContext: CodeGenContext = {
      rendered: [],
    };

    const props = {
      id: "id",
      label: "salary",
      name: "salary",
      min: 400.5,
      max: 900.5,
      disabled: false,
      onChange: jest.fn(),
    };

    render(
      <TestCodeGenContextProvider ctx={codegenContext} schema={schema}>
        <NumField {...props} />
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
    expect(field.jsxCode).toContain(`step={0.01}`);
    expect(field.jsxCode).toContain(`min={${props.min}}`);
    expect(field.jsxCode).toContain(`max={${props.max}}`);
    expect(field.stateCode).not.toBeNull();
  });
});
