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
import { RadioField } from "../uniforms";
import { renderField } from "./_render";

const schema = {
  role: {
    type: String,
    allowedValues: ["Developer", "HR", "UX"],
  },
};

describe("<RadioField> tests", () => {
  it("<RadioField> - rendering", () => {
    const props = {
      id: "id",
      label: "Role",
      name: "role",
      disabled: false,
      allowedValues: ["Developer", "HR", "UX"],
      onChange: jest.fn(),
    };
    const { container, formElement } = renderField(RadioField, props, schema);

    expect(container).toMatchSnapshot();

    expect(formElement.reactImports).toContain("useState");
    expect(formElement.pfImports).toContain("FormGroup");
    expect(formElement.pfImports).toContain("Radio");

    expect(formElement.ref.binding).toBe(props.name);
    expect(formElement.ref.stateName).toBe(props.name);
    expect(formElement.ref.stateSetter).toBe(`set__${props.name}`);

    expect(formElement.jsxCode).not.toBeNull();
    expect(formElement.jsxCode).toContain(`label={'${props.label}'}`);
    expect(formElement.jsxCode).toContain(`name={'${props.name}'}`);
    expect(formElement.jsxCode).toContain("isDisabled={false}");

    expect(formElement.stateCode).not.toBeNull();
  });

  it("<RadioField> - RadioInput rendering - disabled", () => {
    const props = {
      id: "id",
      label: "Role",
      name: "role",
      disabled: true,
      allowedValues: ["Developer", "HR", "UX"],
      onChange: jest.fn(),
    };

    const { container, formElement } = renderField(RadioField, props, schema);

    expect(container).toMatchSnapshot();

    expect(formElement.jsxCode).not.toBeNull();
    expect(formElement.jsxCode).toContain(`label={'${props.label}'}`);
    expect(formElement.jsxCode).toContain(`name={'${props.name}'}`);
    expect(formElement.jsxCode).toContain("isDisabled={true}");
  });
});
