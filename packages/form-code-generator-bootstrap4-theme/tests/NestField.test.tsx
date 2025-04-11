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

import SimpleSchema from "simpl-schema";
import { renderField } from "./_render";
import { NestField } from "../src/uniforms";
import { FormInputContainer } from "../src/api";

const schema = {
  candidate: { type: Object },
  "candidate.name": { type: String },
  "candidate.age": { type: SimpleSchema.Integer },
  "candidate.role": {
    type: String,
    allowedValues: ["Developer", "HR", "UX"],
  },
};

describe("<NestField> tests", () => {
  it("<NestField> - rendering", () => {
    const props = {
      id: "id",
      label: "Candidate",
      name: "candidate",
      disabled: false,
    };

    const { container, formElement } = renderField(NestField, props, schema);

    expect(container).toMatchSnapshot();

    expect(formElement.html).toContain("<fieldset>");
    expect(formElement.html).toContain(`<legend>${props.label}</legend>`);

    const inputContainer = formElement as FormInputContainer;

    expect(inputContainer.ref).toHaveLength(3);

    expect(inputContainer.ref[0].binding).toEqual("candidate.name");
    expect(inputContainer.ref[1].binding).toEqual("candidate.age");
    expect(inputContainer.ref[2].binding).toEqual("candidate.role");

    expect(formElement.setValueFromModelCode).not.toBeUndefined();
    expect(formElement.writeValueToModelCode).not.toBeUndefined();
  });

  it("<NestField> - rendering disabled", () => {
    const props = {
      id: "id",
      label: "Candidate",
      name: "candidate",
      disabled: true,
    };

    const { container, formElement } = renderField(NestField, props, schema);

    expect(container).toMatchSnapshot();

    expect(formElement.html).toContain("<fieldset disabled>");
    expect(formElement.html).toContain(`<legend>${props.label}</legend>`);

    const inputContainer = formElement as FormInputContainer;

    expect(inputContainer.ref).toHaveLength(3);

    expect(inputContainer.ref[0].binding).toEqual("candidate.name");
    expect(inputContainer.ref[1].binding).toEqual("candidate.age");
    expect(inputContainer.ref[2].binding).toEqual("candidate.role");

    expect(formElement.setValueFromModelCode).not.toBeUndefined();
    expect(formElement.writeValueToModelCode).not.toBeUndefined();
  });
});
