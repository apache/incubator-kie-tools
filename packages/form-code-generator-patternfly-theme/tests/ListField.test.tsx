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
import { createJsonSchema } from "./_createSchema";
import AutoForm, { AutoFormProps } from "../src/uniforms/AutoForm";

describe("<ListField> tests", () => {
  it("<ListField>", () => {
    // const schema = {
    //   friends: { type: Array },
    //   "friends.$": Object,
    //   "friends.$.name": { type: String },
    //   "friends.$.age": { type: Number },
    //   "friends.$.country": { type: String, allowedValues: ["US", "Brazil"] },
    //   "friends.$.married": { type: Boolean },
    //   "friends.$.know": {
    //     type: Array,
    //     allowedValues: ["Java", "Node", "Docker"],
    //     uniforms: {
    //       checkboxes: true,
    //     },
    //   },
    //   "friends.$.know.$": String,
    //   "friends.$.areas": {
    //     type: String,
    //     allowedValues: ["Developer", "HR", "UX"],
    //   },
    //   "friends.$.birthday": { type: Date },
    // };

    const schema = {
      $schema: "https://json-schema.org/draft/2019-09/schema",
      $defs: {
        CandidateData: {
          type: "object",
          properties: {
            skills: { type: "array", items: { type: "string" } },
          },
        },
      },
      type: "object",
      properties: {
        candidate: { $ref: "#/$defs/CandidateData" },
      },
    };

    const props: AutoFormProps = {
      id: "hiring_ITInterview",
      schema: createJsonSchema(schema),
      disabled: false,
      placeholder: true,
    };

    const { container } = render(<AutoForm {...props} />);

    expect(container).toMatchSnapshot();
  });
});
