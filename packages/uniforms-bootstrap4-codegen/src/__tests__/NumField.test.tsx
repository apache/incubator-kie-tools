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
import { renderField } from "./_render";
import { NumField } from "../uniforms";

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
    };

    const { container, formElement } = renderField(NumField, props, schema);

    expect(container).toMatchSnapshot();

    expect(formElement.html).toContain(`<label for="${props.name}">${props.label}</label>`);
    expect(formElement.html).toContain(`id="${props.name}"`);
    expect(formElement.html).toContain('type="number"');
    expect(formElement.html).toContain(`name="${props.name}"`);
    expect(formElement.html).toContain(`min="${props.min}"`);
    expect(formElement.html).toContain(`max="${props.max}"`);
    expect(formElement.html).toContain(`step="1"`);
    expect(formElement.html).not.toContain("autoComplete");
    expect(formElement.html).not.toContain("disabled");
    expect(formElement.html).not.toContain("placeholder");

    expect(formElement.ref.binding).toBe(props.name);

    expect(formElement.setValueFromModelCode).not.toBeUndefined();
    expect(formElement.writeValueToModelCode).not.toBeUndefined();
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

    expect(formElement.html).toContain(`<label for="${props.name}">${props.label}</label>`);
    expect(formElement.html).toContain(`id="${props.name}"`);
    expect(formElement.html).toContain('type="number"');
    expect(formElement.html).toContain(`name="${props.name}"`);
    expect(formElement.html).not.toContain("min=");
    expect(formElement.html).not.toContain("max=");
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
    };

    const { container, formElement } = renderField(NumField, props, schema);

    expect(container).toMatchSnapshot();

    expect(formElement.html).toContain(`<label for="${props.name}">${props.label}</label>`);
    expect(formElement.html).toContain(`id="${props.name}"`);
    expect(formElement.html).toContain('type="number"');
    expect(formElement.html).toContain(`name="${props.name}"`);
    expect(formElement.html).toContain("disabled");

    expect(formElement.setValueFromModelCode).not.toBeUndefined();
    expect(formElement.writeValueToModelCode).toBeUndefined();
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
    };

    const { container, formElement } = renderField(NumField, props, schema);

    expect(container).toMatchSnapshot();

    expect(formElement.html).toContain(`<label for="${props.name}">${props.label}</label>`);
    expect(formElement.html).toContain(`id="${props.name}"`);
    expect(formElement.html).toContain('type="number"');
    expect(formElement.html).toContain(`name="${props.name}"`);
    expect(formElement.html).toContain(`min="${props.min}"`);
    expect(formElement.html).toContain(`max="${props.max}"`);
    expect(formElement.html).toContain(`step="0.01"`);
    expect(formElement.html).not.toContain('autoComplete"');
    expect(formElement.html).not.toContain('disabled"');
    expect(formElement.html).not.toContain("placeholder");
  });
});
