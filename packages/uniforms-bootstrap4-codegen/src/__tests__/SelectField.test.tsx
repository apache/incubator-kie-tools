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
import { SelectField } from "../uniforms";

const schema = {
  role: {
    type: String,
    allowedValues: ["Developer", "HR", "UX"],
  },
  otherPositions: {
    type: Array,
    allowedValues: ["Developer", "HR", "UX"],
  },
  "otherPositions.$": String,
};

describe("<SelectField> tests", () => {
  it("<SelectField> - rendering", () => {
    const props = {
      id: "id",
      label: "Role",
      name: "role",
      disabled: false,
      placeHolder: "--- Choose a Role ---",
      allowedValues: ["Developer", "HR", "UX"],
      value: "Developer",
    };

    const { container, formElement } = renderField(SelectField, props, schema);

    expect(container).toMatchSnapshot();

    expect(formElement.html).toContain(`<select class="form-control" id="${props.id}" name="${props.name}">`);
    const options = formElement.html.match(new RegExp("<option", "g")) || [];
    expect(options).toHaveLength(4);
    props.allowedValues.forEach((value) => {
      expect(formElement.html).toContain(
        `<option value="${value}"${value === "Developer" ? " selected" : ""}>${value}</option>`
      );
    });
    expect(formElement.ref.binding).toBe(props.name);
  });

  it("<SelectField> - disabled rendering", () => {
    const props = {
      id: "id",
      label: "Role",
      name: "role",
      disabled: true,
      allowedValues: ["Developer", "HR", "UX"],
      value: "Developer",
    };

    const { container, formElement } = renderField(SelectField, props, schema);

    expect(container).toMatchSnapshot();
    expect(formElement.html).toContain(`<select class="form-control" id="${props.id}" name="${props.name}" disabled>`);
    const options = formElement.html.match(new RegExp("<option", "g")) || [];
    expect(options).toHaveLength(3);
    props.allowedValues.forEach((value) => {
      expect(formElement.html).toContain(
        `<option value="${value}"${value === "Developer" ? " selected" : ""}>${value}</option>`
      );
    });
    expect(formElement.ref.binding).toBe(props.name);
  });

  it("<SelectField> - rendering multiple", () => {
    const props = {
      id: "id",
      label: "Other Positions",
      name: "otherPositions",
      disabled: false,
      allowedValues: ["Developer", "HR", "UX"],
    };
    const { container, formElement } = renderField(SelectField, props, schema);

    expect(container).toMatchSnapshot();

    expect(formElement.html).toContain(`<select class="form-control" id="${props.id}" name="${props.name}" multiple>`);
    const options = formElement.html.match(new RegExp("<option", "g")) || [];
    expect(options).toHaveLength(3);
    props.allowedValues.forEach((value) => {
      expect(formElement.html).toContain(`<option value="${value}">${value}</option>`);
    });
    expect(formElement.ref.binding).toBe(props.name);
  });

  it("<SelectField> - rendering multiple disabled", () => {
    const props = {
      id: "id",
      label: "Other Positions",
      name: "otherPositions",
      disabled: true,
      allowedValues: ["Developer", "HR", "UX"],
    };

    const { container, formElement } = renderField(SelectField, props, schema);

    expect(container).toMatchSnapshot();
    expect(formElement.html).toContain(
      `<select class="form-control" id="${props.id}" name="${props.name}" disabled multiple>`
    );
    const options = formElement.html.match(new RegExp("<option", "g")) || [];
    expect(options).toHaveLength(3);
    props.allowedValues.forEach((value) => {
      expect(formElement.html).toContain(`<option value="${value}">${value}</option>`);
    });
    expect(formElement.ref.binding).toBe(props.name);
  });
});
