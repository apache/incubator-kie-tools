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

import { renderField, renderFields } from "./_render";
import { AutoFields } from "../uniforms";
import SimpleSchema from "simpl-schema";
import { FormElement } from "../api";

describe("<AutoFields> - tests", () => {
  it("<AutoFields> - works", () => {
    const { formElement } = renderField(AutoFields, {}, { x: { type: String } });

    expect(formElement).not.toBeUndefined();
    expect(formElement.html).toContain('type="text"');
  });

  it("<AutoFields> - render all fields by default", () => {
    const fields: FormElement<any>[] = renderFields(
      AutoFields,
      {},
      {
        x: { type: String },
        y: { type: Date },
        z: { type: SimpleSchema.Integer },
      }
    );

    expect(fields).toHaveLength(3);

    expect(fields[0].html).toContain('type="text"');
    expect(fields[1].html).toContain('type="datetime-local"');
    expect(fields[2].html).toContain('type="number"');
  });

  it("<AutoFields> - renders only specified fields", () => {
    const fields = renderFields(
      AutoFields,
      {
        fields: ["x", "y"],
      },
      {
        x: { type: String },
        y: { type: Date },
        z: { type: SimpleSchema.Integer },
      }
    );

    expect(fields).toHaveLength(2);
  });

  test("<AutoFields> - does not render ommited fields", () => {
    const fields = renderFields(
      AutoFields,
      {
        omitFields: ["x", "y"],
      },
      {
        x: { type: String },
        y: { type: Date },
        z: { type: SimpleSchema.Integer },
      }
    );

    expect(fields).toHaveLength(1);
  });
});
