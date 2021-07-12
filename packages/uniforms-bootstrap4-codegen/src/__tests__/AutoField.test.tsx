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
import SimpleSchema from "simpl-schema";
import { renderField } from "./_render";
import { AutoField } from "../uniforms";

const schema = {
  name: { type: String },
  hire: { type: Boolean },
  birthday: { type: Date },
  age: { type: SimpleSchema.Integer },
  salary: { type: Number },
  role: {
    type: String,
    allowedValues: ["Developer", "HR", "UX"],
    uniforms: {
      checkboxes: true,
    },
  },
  roles: {
    type: Array,
    allowedValues: ["Developer", "HR", "UX"],
    uniforms: {
      checkboxes: true,
    },
  },
  "roles.$": String,
  selectRole: {
    type: String,
    allowedValues: ["Developer", "HR", "UX"],
  },
  otherPositions: {
    type: Array,
    allowedValues: ["Developer", "HR", "UX"],
  },
  "otherPositions.$": String,
  dateStr: {
    type: String,
    uniforms: {
      type: "date",
    },
  },
  friends: { type: Array },
  "friends.$": Object,
  "friends.$.name": { type: String },
  "friends.$.age": { type: Number },
};

const doRenderField = (fieldName: string) => {
  const props = {
    name: fieldName,
  };

  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  return renderField(AutoField, props, schema);
};

describe("<AutoField> tests", () => {
  it("<BoolField> - rendering", () => {
    const { formElement } = doRenderField("hire");

    expect(formElement.html).toContain('<input type="checkbox"');
    expect(formElement.html).toContain('name="hire"');
  });

  it("<CheckBoxGroupField> - rendering", () => {
    const { formElement } = doRenderField("roles");

    const options = formElement.html.match(new RegExp('type="checkbox"', "g")) || [];
    expect(options).toHaveLength(3);
    const names = formElement.html.match(new RegExp('name="roles"', "g")) || [];
    expect(names).toHaveLength(3);
  });

  it("<DateField> - rendering", () => {
    const { formElement } = doRenderField("birthday");

    expect(formElement.html).toContain('type="datetime-local"');
    expect(formElement.html).toContain('name="birthday"');
  });

  it("<NumField> - integer rendering", () => {
    const { formElement } = doRenderField("age");

    expect(formElement.html).toContain('type="number"');
    expect(formElement.html).toContain('name="age"');
  });

  it("<RadioField> - rendering", () => {
    const { formElement } = doRenderField("role");

    const options = formElement.html.match(new RegExp('type="radio"', "g")) || [];
    expect(options).toHaveLength(3);
    const names = formElement.html.match(new RegExp('name="role"', "g")) || [];
    expect(names).toHaveLength(3);
  });

  it("<SelectField> - single value rendering", () => {
    const { formElement } = doRenderField("selectRole");

    const options = formElement.html.match(new RegExp("<option", "g")) || [];
    expect(options).toHaveLength(3);
  });

  it("<SelectField> - multiple value rendering", () => {
    const { formElement } = doRenderField("otherPositions");

    const options = formElement.html.match(new RegExp("<option", "g")) || [];
    expect(options).toHaveLength(3);
  });

  it("<TextField> - TextInput rendering", () => {
    const { formElement } = doRenderField("name");

    expect(formElement.html).toContain('type="text"');
    expect(formElement.html).toContain('name="name"');
  });

  it("<UnsupportedField> - rendering", () => {
    const { formElement } = doRenderField("friends");

    expect(formElement.html).toContain("Unsupported field type: Array");
    expect(formElement.html).toContain(
      `Cannot find form control for property <code>friends</code> with type <code>Array</code>.</p>`
    );
  });
});
