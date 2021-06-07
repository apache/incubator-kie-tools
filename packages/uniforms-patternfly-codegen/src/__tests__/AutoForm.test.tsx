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

import { render } from "@testing-library/react";
import * as React from "react";
import AutoForm, { AutoFormProps } from "../uniforms/AutoForm";
import JSONSchemaBridge from "uniforms-bridge-json-schema";

const schema = {
  name: "Candidate",
  type: "object",
  properties: {
    name: {
      type: "string",
    },
    age: {
      type: "integer",
      uniforms: {
        min: 18,
        max: 99,
      },
    },
    salary: {
      type: "number",
      uniforms: {
        min: 0,
        max: 100.98,
      },
    },
    hire: {
      type: "boolean",
    },
  },
};

const props: AutoFormProps = {
  id: "HRInterview",
  schema: new JSONSchemaBridge(schema, () => true),
  disabled: false,
  placeholder: true,
};

describe("AutoForm tests", () => {
  it("Full rendering", () => {
    const { container } = render(<AutoForm {...props} />);
  });
});
