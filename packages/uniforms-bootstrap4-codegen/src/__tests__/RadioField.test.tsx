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
      allowedValues: ["Developer", "HR", "UX"],
      value: "Developer",
    };
    const { container, formElement } = renderField(RadioField, props, schema);

    expect(container).toMatchSnapshot();

    expect(formElement.html).toContain(`<label for="${props.name}">${props.label}</label>`);

    const options = formElement.html.match(new RegExp('type="radio"', "g")) || [];
    expect(options).toHaveLength(3);
    const names = formElement.html.match(new RegExp('name="role"', "g")) || [];
    expect(names).toHaveLength(3);
    props.allowedValues.forEach((value) => {
      expect(formElement.html).toContain(`id="${props.name}__${value}"`);
      expect(formElement.html).toContain(`value="${value}"`);
      expect(formElement.html).toContain(`for="${props.name}__${value}">${value}</label>`);
    });
    expect(formElement.ref.binding).toBe(props.name);

    expect(formElement.setValueFromModelCode).not.toBeUndefined();
    expect(formElement?.setValueFromModelCode?.code).not.toBeUndefined();
    expect(formElement?.setValueFromModelCode?.requiredCode).toHaveLength(1);
    expect(formElement.writeValueToModelCode).not.toBeUndefined();
    expect(formElement?.writeValueToModelCode?.code).not.toBeUndefined();
    expect(formElement?.writeValueToModelCode?.requiredCode).toHaveLength(1);
  });

  it("<RadioField> - RadioInput rendering - disabled", () => {
    const props = {
      id: "id",
      label: "Role",
      name: "role",
      disabled: true,
      allowedValues: ["Developer", "HR", "UX"],
      value: "Developer",
    };

    const { container, formElement } = renderField(RadioField, props, schema);

    expect(container).toMatchSnapshot();

    const options = formElement.html.match(new RegExp('type="radio"', "g")) || [];
    expect(options).toHaveLength(3);
    const disabled = formElement.html.match(new RegExp("disabled", "g")) || [];
    expect(disabled).toHaveLength(3);
    props.allowedValues.forEach((value) => {
      expect(formElement.html).toContain(`id="${props.name}__${value}"`);
      expect(formElement.html).toContain(`value="${value}"`);
      expect(formElement.html).toContain(`for="${props.name}__${value}">${value}</label>`);
    });
    expect(formElement.ref.binding).toBe(props.name);

    expect(formElement.setValueFromModelCode).not.toBeUndefined();
    expect(formElement.writeValueToModelCode).toBeUndefined();
  });
});
