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
      allowedValues: ["Developer", "HR", "UX"],
      value: "Developer",
    };
    const { container, formElement } = renderField(RadioField, props, schema);

    expect(container).toMatchSnapshot();

    expect(formElement.html).toContain(`<label for="${props.id}">${props.label}</label>`);

    const options = formElement.html.match(new RegExp('type="radio"', "g")) || [];
    expect(options).toHaveLength(3);
    const names = formElement.html.match(new RegExp('name="role"', "g")) || [];
    expect(names).toHaveLength(3);
    props.allowedValues.forEach((value) => {
      expect(formElement.html).toContain(`id="${props.id}__${value}"`);
      expect(formElement.html).toContain(`value="${value}"`);
      expect(formElement.html).toContain(`for="${props.id}__${value}">${value}</label>`);
    });
    expect(formElement.ref.binding).toBe(props.name);
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
      expect(formElement.html).toContain(`id="${props.id}__${value}"`);
      expect(formElement.html).toContain(`value="${value}"`);
      expect(formElement.html).toContain(`for="${props.id}__${value}">${value}</label>`);
    });
    expect(formElement.ref.binding).toBe(props.name);
  });
});
