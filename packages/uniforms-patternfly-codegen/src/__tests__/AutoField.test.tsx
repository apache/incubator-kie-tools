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
import {
  CHECKBOX_GROUP_FUNCTIONS,
  DATE_FUNCTIONS,
  MULTIPLE_SELECT_FUNCTIONS,
  SELECT_FUNCTIONS,
  TIME_FUNCTIONS,
} from "../uniforms/staticCode/staticCodeBlocks";
import { SELECT_IMPORTS } from "../uniforms/SelectField";

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

    expect(formElement.pfImports).toContain("FormGroup");
    expect(formElement.pfImports).toContain("Checkbox");
  });

  it("<CheckBoxGroupField> - rendering", () => {
    const { formElement } = doRenderField("roles");

    expect(formElement.reactImports).toContain("useState");

    expect(formElement.pfImports).toContain("FormGroup");
    expect(formElement.pfImports).toContain("Checkbox");

    expect(formElement.requiredCode).not.toBeUndefined();
    expect(formElement.requiredCode).toHaveLength(1);
    expect(formElement.requiredCode).toContain(CHECKBOX_GROUP_FUNCTIONS);
  });

  it("<DateField> - rendering", () => {
    const { formElement } = doRenderField("birthday");

    expect(formElement.pfImports).toContain("FormGroup");
    expect(formElement.pfImports).toContain("DatePicker");
    expect(formElement.pfImports).toContain("TimePicker");
    expect(formElement.requiredCode).toHaveLength(2);
    expect(formElement.requiredCode).toContain(DATE_FUNCTIONS);
    expect(formElement.requiredCode).toContain(TIME_FUNCTIONS);
  });

  it("<NumField> - integer rendering", () => {
    const { formElement } = doRenderField("age");

    expect(formElement.pfImports).toContain("FormGroup");
    expect(formElement.pfImports).toContain("TextInput");

    expect(formElement.jsxCode).not.toBeNull();
    expect(formElement.jsxCode).toContain(`step={1}`);
  });

  it("<NumField> - decimal rendering", () => {
    const { formElement } = doRenderField("salary");

    expect(formElement.pfImports).toContain("FormGroup");
    expect(formElement.pfImports).toContain("TextInput");

    expect(formElement.jsxCode).not.toBeNull();
    expect(formElement.jsxCode).toContain(`step={0.01}`);
  });

  it("<RadioField> - rendering", () => {
    const { formElement } = doRenderField("role");

    expect(formElement.pfImports).toContain("FormGroup");
    expect(formElement.pfImports).toContain("Radio");
  });

  it("<SelectField> - single value rendering", () => {
    const { formElement } = doRenderField("selectRole");

    expect(formElement.pfImports).toHaveLength(SELECT_IMPORTS.length);
    SELECT_IMPORTS.forEach((pfImport) => expect(formElement.pfImports).toContain(pfImport));

    expect(formElement.requiredCode).not.toBeUndefined();
    expect(formElement.requiredCode).toHaveLength(1);
    expect(formElement.requiredCode).toContain(SELECT_FUNCTIONS);
  });

  it("<SelectField> - multiple value rendering", () => {
    const { formElement } = doRenderField("otherPositions");

    expect(formElement.reactImports).toContain("useState");
    expect(formElement.pfImports).toHaveLength(SELECT_IMPORTS.length);
    SELECT_IMPORTS.forEach((pfImport) => expect(formElement.pfImports).toContain(pfImport));

    expect(formElement.requiredCode).not.toBeUndefined();
    expect(formElement.requiredCode).toHaveLength(1);
    expect(formElement.requiredCode).toContain(MULTIPLE_SELECT_FUNCTIONS);
  });

  it("<TextField> - TextInput rendering", () => {
    const { formElement } = doRenderField("name");

    expect(formElement.reactImports).toContain("useState");
    expect(formElement.pfImports).toContain("FormGroup");
    expect(formElement.pfImports).toContain("TextInput");
  });

  it("<TextField> - DatePicker rendering", () => {
    const { formElement } = doRenderField("dateStr");

    expect(formElement.reactImports).toContain("useState");
    expect(formElement.pfImports).toContain("FormGroup");
    expect(formElement.pfImports).toContain("DatePicker");
    expect(formElement.requiredCode).not.toBeUndefined();
    expect(formElement.requiredCode).toHaveLength(1);
    expect(formElement.requiredCode).toContain(DATE_FUNCTIONS);
  });

  it("<UnsupportedField> - rendering", () => {
    const { formElement } = doRenderField("friends");

    expect(formElement.pfImports).toContain("FormGroup");
    expect(formElement.pfImports).toContain("Alert");
  });
});
