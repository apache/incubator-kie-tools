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
import { renderField } from "./_render";
import { UnsupportedField } from "../src/uniforms";

const schema = {
  friends: { type: "foo" },
};

describe("<UnsupportedField> tests", () => {
  it("<UnsupportedField> - rendering", () => {
    const props = {
      id: "id",
      label: "Friends?",
      name: "friends",
    };

    const { container, formElement } = renderField(UnsupportedField, props, schema);

    expect(container).toMatchSnapshot();

    expect(formElement.html).toContain("Unsupported field type: ");
    expect(formElement.html).toContain(
      `Cannot find form control for property <code>${props.name}</code> with type <code></code>.</p>`
    );

    expect(formElement.setValueFromModelCode).toBeUndefined();
    expect(formElement.writeValueToModelCode).toBeUndefined();
  });
});
