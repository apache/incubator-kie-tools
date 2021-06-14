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
import { render } from "@testing-library/react";
import { unescape } from "lodash";
import AutoForm, { AutoFormProps } from "../uniforms/AutoForm";
import SimpleSchema from "simpl-schema";
import createSchema from "./_createSchema";

const schema = {
  name: String,
  position: {
    type: String,
    allowedValues: ["Developer", "HR", "UX"],
  },
  otherPositions: {
    type: Array,
    allowedValues: ["Developer", "HR", "UX"],
  },
  "otherPositions.$": String,
  skills: {
    type: Array,
    allowedValues: ["Java", "React", "TypeScript", "Quarkus"],
    uniforms: { checkboxes: true },
  },
  "skills.$": String,
  age: {
    type: SimpleSchema.Integer,
    min: 18,
    max: 99,
  },
  salary: {
    type: Number,
    min: 0,
    max: 1000.5,
  },
  rating: {
    type: Number,
    allowedValues: [1, 2, 3, 4, 5],
    uniforms: { checkboxes: true },
  },
  hire: Boolean,
  hidingDate: Date,
  friends: { type: Array },
  "friends.$": Object,
  "friends.$.name": { type: String },
  "friends.$.age": { type: Number },
};

const props: AutoFormProps = {
  id: "HRInterview",
  schema: createSchema(schema),
  disabled: false,
  placeholder: true,
};

describe("<AutoForm> tests", () => {
  it("<AutoForm> - Full rendering", () => {
    const { container } = render(<AutoForm {...props} />);

    expect(container).toMatchSnapshot();
    const formSource = unescape(container.innerHTML);
    expect(formSource).not.toBeUndefined();
  });
});
