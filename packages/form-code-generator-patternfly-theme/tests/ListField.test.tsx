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
import SimpleSchema from "simpl-schema";
import { InputReference } from "../src/api";
import ListField from "../src/uniforms/ListField";
import { render } from "@testing-library/react";
import { renderField } from "./_render";
import { TextField } from "../src/uniforms";
import createSchema from "./_createSchema";
import AutoForm, { AutoFormProps } from "../src/uniforms/AutoForm";

describe("<ListField> tests", () => {
  it("<ListField>", () => {
    const schema = {
      friends: { type: Array },
      "friends.$": Object,
      "friends.$.name": { type: String },
    };

    const props: AutoFormProps = {
      id: "id",
      schema: createSchema(schema),
      disabled: false,
      placeholder: true,
    };

    // const props = {
    //   id: "id",
    //   label: "friends",
    //   name: "friends",
    //   disabled: false,
    //   onChange: jest.fn(),
    //   maxCount: 10,
    // };

    const { container } = render(<AutoForm {...props} />);

    // const { container, formElement } = renderField(ListField, props, schema);

    expect(container).toMatchSnapshot();

    // expect(formElement.reactImports).toContain("useState");
    // expect(formElement.pfImports).toContain("FormGroup");
    // expect(formElement.pfImports).toContain("TextInput");

    // expect(formElement.ref.binding).toBe(props.name);
    // expect(formElement.ref.stateName).toBe(props.name);
    // expect(formElement.ref.stateSetter).toBe(`set__${props.name}`);

    // expect(formElement.jsxCode).not.toBeNull();
    // expect(formElement.jsxCode).toContain("label={'age'}");
    // expect(formElement.jsxCode).toContain("name={'age'}");
    // expect(formElement.jsxCode).toContain("isDisabled={false}");

    // expect(formElement.jsxCode).toContain(`step={1}`);
    // expect(formElement.jsxCode).toContain(`min={${props.min}}`);
    // expect(formElement.jsxCode).toContain(`max={${props.max}}`);
    // expect(formElement.stateCode).not.toBeNull();
  });
});
