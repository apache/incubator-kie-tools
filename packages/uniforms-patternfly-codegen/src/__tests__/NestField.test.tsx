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

import SimpleSchema from "simpl-schema";
import { renderField } from "./_render";
import { NestField } from "../uniforms";
import { InputsContainer } from "../api";

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
    const { container, formElement } = renderField(
      NestField,
      {
        id: "id",
        label: "Candidate",
        name: "candidate",
        disabled: false,
      },
      schema
    );

    expect(container).toMatchSnapshot();

    const inputContainer = formElement as InputsContainer;
    expect(inputContainer.pfImports).toStrictEqual([
      "Card",
      "CardBody",
      "TextInput",
      "FormGroup",
      "SelectOption",
      "SelectOptionObject",
      "Select",
      "SelectVariant",
    ]);
    expect(inputContainer.childRefs).toHaveLength(3);

    expect(inputContainer.childRefs[0].binding).toEqual("candidate.name");
    expect(inputContainer.childRefs[0].stateName).toEqual("candidate__name");
    expect(inputContainer.childRefs[0].stateSetter).toEqual("set__candidate__name");

    expect(inputContainer.childRefs[1].binding).toEqual("candidate.age");
    expect(inputContainer.childRefs[1].stateName).toEqual("candidate__age");
    expect(inputContainer.childRefs[1].stateSetter).toEqual("set__candidate__age");

    expect(inputContainer.childRefs[2].binding).toEqual("candidate.role");
    expect(inputContainer.childRefs[2].stateName).toEqual("candidate__role");
    expect(inputContainer.childRefs[2].stateSetter).toEqual("set__candidate__role");
  });
});
