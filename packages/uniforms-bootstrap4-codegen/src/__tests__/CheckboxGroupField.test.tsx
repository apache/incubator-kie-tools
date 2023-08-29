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
import { renderField } from "./_render";
import { CheckBoxGroupField } from "../uniforms";

const schema = {
  roles: {
    type: Array,
    allowedValues: ["Developer", "HR", "UX"],
    uniforms: {
      checkboxes: true,
    },
  },
  "roles.$": String,
};

describe("<CheckBoxGroupField> tests", () => {
  it("<CheckBoxGroupField> - rendering", () => {
    const props = {
      id: "id",
      label: "Roles",
      name: "roles",
      disabled: false,
      allowedValues: ["Developer", "HR", "UX"],
    };

    const { container, formElement } = renderField(CheckBoxGroupField, props, schema);

    expect(container).toMatchSnapshot();

    expect(formElement.html).toContain(`<label for="${props.name}">${props.label}</label>`);

    const options = formElement.html.match(new RegExp('type="checkbox"', "g")) || [];
    expect(options).toHaveLength(3);
    const names = formElement.html.match(new RegExp('name="roles"', "g")) || [];
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

  it("<CheckBoxGroupField> - rendering - disabled", () => {
    const props = {
      id: "id",
      label: "Roles",
      name: "roles",
      disabled: true,
      allowedValues: ["Developer", "HR", "UX"],
    };

    const { container, formElement } = renderField(CheckBoxGroupField, props, schema);

    expect(container).toMatchSnapshot();

    const options = formElement.html.match(new RegExp('type="checkbox"', "g")) || [];
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
