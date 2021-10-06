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
import { BoolField } from "../uniforms";

const schema = {
  hire: { type: Boolean },
};

describe("<BoolField> tests", () => {
  it("<BoolField> - rendering", () => {
    const props = {
      id: "id",
      label: "Hire?",
      name: "hire",
      disabled: false,
      value: true,
    };

    const { container, formElement } = renderField(BoolField, props, schema);

    expect(container).toMatchSnapshot();

    expect(formElement.html).toContain(`<label class="form-check-label" for="${props.id}">${props.label}</label>`);
    expect(formElement.html).toContain('id="id"');
    expect(formElement.html).toContain('type="checkbox"');
    expect(formElement.html).toContain('name="hire"');
    expect(formElement.html).toContain("checked");
    expect(formElement.html).not.toContain('disabled"');
  });

  it("<BoolField> - rendering - disabled", () => {
    const props = {
      id: "id",
      label: "Hire?",
      name: "hire",
      disabled: true,
    };

    const { container, formElement } = renderField(BoolField, props, schema);

    expect(container).toMatchSnapshot();

    expect(formElement.html).toContain(`<label class="form-check-label" for="${props.id}">${props.label}</label>`);
    expect(formElement.html).toContain('id="id"');
    expect(formElement.html).toContain('type="checkbox"');
    expect(formElement.html).toContain('name="hire"');
    expect(formElement.html).toContain("disabled");
  });
});
