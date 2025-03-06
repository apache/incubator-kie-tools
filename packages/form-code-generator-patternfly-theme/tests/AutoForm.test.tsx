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
import { render } from "@testing-library/react";
import { unescape } from "lodash";
import AutoForm, { AutoFormProps } from "../src/uniforms/AutoForm";
import SimpleSchema from "simpl-schema";
import createSchema, { createJsonSchema } from "./_createSchema";

const schema = {
  personalData: { type: Object },
  "personalData.name": { type: String },
  "personalData.email": { type: String },
  "personalData.child": { type: Object },
  "personalData.child.name": { type: String },
  address: { type: Object },
  "address.street": { type: String },
  "address.num": { type: SimpleSchema.Integer },
  "address.cp": { type: String },
  "address.city": { type: String },
  interview: { type: Object },
  "interview.position": {
    type: String,
    allowedValues: ["Developer", "HR", "UX"],
  },
  "interview.otherPositions": {
    type: Array,
    allowedValues: ["Developer", "HR", "UX"],
  },
  "interview.otherPositions.$": String,
  "interview.skills": {
    type: Array,
    allowedValues: ["Java", "React", "TypeScript", "Quarkus"],
    uniforms: { checkboxes: true },
  },
  "interview.skills.$": String,
  "interview.age": {
    type: SimpleSchema.Integer,
    min: 18,
    max: 99,
  },
  "interview.salary": {
    type: Number,
    min: 0,
    max: 1000.5,
  },
  "interview.rating": {
    type: Number,
    allowedValues: [1, 2, 3, 4, 5],
    uniforms: { checkboxes: true },
  },
  "interview.hire": Boolean,
  "interview.hiringDate": Date,
  friends: { type: Array },
  "friends.$": Object,
  "friends.$.name": { type: String },
  "friends.$.age": { type: Number },
  "friends.$.know": { type: Array },
  "friends.$.know.$": { type: String },
};

const props: AutoFormProps = {
  id: "HRInterview",
  idWithoutInvalidTsVarChars: "HRInterview",
  schema: createSchema(schema),
  disabled: false,
  placeholder: true,
};

const propsSchema: AutoFormProps = {
  id: "HRInterview",
  idWithoutInvalidTsVarChars: "HRInterview",
  schema: createJsonSchema({
    $schema: "https://json-schema.org/draft/2019-09/schema",
    $defs: {
      CandidateData: {
        type: "object",
        properties: {
          email: { type: "string" },
          experience: { type: "integer" },
          lastName: { type: "string" },
          name: { type: "string" },
          skills: { type: "array", items: { type: "string" } },
          tests: { type: "array", items: { $ref: "#/$defs/Test" } },
        },
      },
      Level: { type: "string", enum: ["LOW", "MEDIUM", "HIGH"] },
      Test: {
        type: "object",
        properties: {
          date: { type: "string", format: "date-time" },
          level: { $ref: "#/$defs/Level" },
          levels: { type: "array", items: { $ref: "#/$defs/Level" } },
          name: { type: "string" },
          value: { type: "integer" },
        },
      },
    },
    type: "object",
    properties: { candidateData: { $ref: "#/$defs/CandidateData" } },
  }),
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

  it("<AutoForm> - Full rendering - JSON Schema", () => {
    const { container } = render(<AutoForm {...propsSchema} />);

    expect(container).toMatchSnapshot();
    const formSource = unescape(container.innerHTML);
    expect(formSource).not.toBeUndefined();
  });
});
