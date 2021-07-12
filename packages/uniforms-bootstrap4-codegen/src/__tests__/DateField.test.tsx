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
import { renderField } from "./_render";
import { DateField } from "../uniforms";

const schema = {
  birthday: { type: Date },
};

const date = new Date(1592000000000); // UTC Fri Jun 12 2020 22:13:20

function formatDate(date?: Date) {
  return date?.toISOString().slice(0, -8);
}

describe("<DateField> tests", () => {
  it("<DateField> - rendering", () => {
    const props = {
      id: "id",
      label: "Birthday",
      name: "birthday",
      disabled: false,
      value: date,
      min: date,
      max: date,
    };

    const { container, formElement } = renderField(DateField, props, schema);

    expect(container).toMatchSnapshot();

    expect(formElement.html).toContain(`<label for="${props.id}">${props.label}</label>`);
    expect(formElement.html).toContain(`id="${props.id}"`);
    expect(formElement.html).toContain('type="datetime-local"');
    expect(formElement.html).toContain(`name="${props.name}"`);
    expect(formElement.html).toContain(`min="${formatDate(date)}"`);
    expect(formElement.html).toContain(`max="${formatDate(date)}"`);
    expect(formElement.html).toContain(`value="${formatDate(date)}"`);
    expect(formElement.html).not.toContain("autoComplete");
    expect(formElement.html).not.toContain("disabled");
    expect(formElement.html).not.toContain("placeholder");

    expect(formElement.ref.binding).toBe(props.name);
  });

  it("<DateField> - rendering - disabled", () => {
    const props = {
      id: "id",
      label: "Birthday",
      name: "birthday",
      disabled: true,
    };

    const { container, formElement } = renderField(DateField, props, schema);

    expect(container).toMatchSnapshot();

    expect(formElement.html).toContain(`<label for="${props.id}">${props.label}</label>`);
    expect(formElement.html).toContain(`id="${props.id}"`);
    expect(formElement.html).toContain('type="datetime-local"');
    expect(formElement.html).toContain(`name="${props.name}"`);
    expect(formElement.html).toContain("disabled");
  });
});
