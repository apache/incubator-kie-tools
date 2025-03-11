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
import SimpleSchema from "simpl-schema";
import { InputReference } from "../src/api";
import NumField from "../src/uniforms/NumField";
import { renderField } from "./_render";

describe("<NumField> tests", () => {
  it("<NumField> - integer rendering", () => {
    const schema = {
      age: { type: SimpleSchema.Integer },
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

    const { formElement } = renderField(NumField, props, schema);

    expect(formElement.jsxCode).toMatchSnapshot();

    expect(formElement.reactImports).toContain("useState");
    expect(formElement.pfImports).toContain("FormGroup");
    expect(formElement.pfImports).toContain("TextInput");

    expect(formElement.ref.binding).toBe(props.name);
    expect(formElement.ref.stateName).toBe(props.name);
    expect(formElement.ref.stateSetter).toBe(`set__${props.name}`);

    expect(formElement.jsxCode).not.toBeNull();
    expect(formElement.jsxCode).toContain("label={'age'}");
    expect(formElement.jsxCode).toContain("name={'age'}");
    expect(formElement.jsxCode).toContain("isDisabled={false}");

    expect(formElement.jsxCode).toContain(`step={1}`);
    expect(formElement.jsxCode).toContain(`min={${props.min}}`);
    expect(formElement.jsxCode).toContain(`max={${props.max}}`);
    expect(formElement.stateCode).not.toBeNull();
  });

  it("<NumField> - integer rendering - no min/max", () => {
    const schema = {
      age: { type: SimpleSchema.Integer },
    };

    const props = {
      id: "id",
      label: "age",
      name: "age",
      onChange: jest.fn(),
    };

    const { formElement } = renderField(NumField, props, schema);

    expect(formElement.reactImports).toContain("useState");
    expect(formElement.pfImports).toContain("FormGroup");
    expect(formElement.pfImports).toContain("TextInput");

    expect(formElement.jsxCode).not.toContain(`min=`);
    expect(formElement.jsxCode).not.toContain(`max=`);
  });

  it("<NumField> - integer rendering - disabled", () => {
    const schema = {
      age: { type: SimpleSchema.Integer },
    };

    const props = {
      id: "id",
      label: "age",
      name: "age",
      min: 10,
      max: 15,
      disabled: true,
      onChange: jest.fn(),
    };

    const { formElement } = renderField(NumField, props, schema);

    expect(formElement.jsxCode).toMatchSnapshot();

    expect(formElement.reactImports).toContain("useState");
    expect(formElement.pfImports).toContain("FormGroup");
    expect(formElement.pfImports).toContain("TextInput");

    expect(formElement.ref.binding).toBe(props.name);
    expect(formElement.ref.stateName).toBe(props.name);
    expect(formElement.ref.stateSetter).toBe(`set__${props.name}`);

    expect(formElement.jsxCode).not.toBeNull();
    expect(formElement.jsxCode).toContain("label={'age'}");
    expect(formElement.jsxCode).toContain("name={'age'}");
    expect(formElement.jsxCode).toContain("isDisabled={true}");
  });

  it("<NumField> - decimal rendering", () => {
    const schema = {
      salary: { type: Number },
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

    const { formElement } = renderField(NumField, props, schema);

    expect(formElement.jsxCode).toMatchSnapshot();

    expect(formElement.reactImports).toContain("useState");
    expect(formElement.pfImports).toContain("FormGroup");
    expect(formElement.pfImports).toContain("TextInput");
    const ref = formElement.ref as InputReference;
    expect(ref.binding).toBe(props.name);
    expect(ref.stateName).toBe(props.name);
    expect(ref.stateSetter).toBe(`set__${props.name}`);

    expect(formElement.jsxCode).not.toBeNull();
    expect(formElement.jsxCode).toContain(`step={0.01}`);
    expect(formElement.jsxCode).toContain(`min={${props.min}}`);
    expect(formElement.jsxCode).toContain(`max={${props.max}}`);
  });
});
