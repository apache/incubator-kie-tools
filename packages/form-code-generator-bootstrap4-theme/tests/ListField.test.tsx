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

import { renderField } from "./_render";
import { ListField } from "../src/uniforms";
import { FormInputContainer } from "../src/api";
import { render } from "@testing-library/react";
import AutoForm, { AutoFormProps } from "../src/uniforms/AutoForm";
import createSchema from "./_createSchema";

const schema = {
  friends: { type: Array },
  "friends.$": Object,
  "friends.$.know": {
    type: Array,
    uniforms: {
      checkboxes: true,
    },
  },
  "friends.$.know.$": Number,
};

describe("<ListField> tests", () => {
  it("<ListField> - full AutoForm rendering (TO DELETE)", () => {
    const props: AutoFormProps = {
      id: "id",
      schema: createSchema(schema),
      disabled: false,
      placeholder: true,
    };

    const { container } = render(<AutoForm {...props} />);

    expect(container).toMatchSnapshot();
  });

  it.skip("<ListField> - rendering enabled", () => {
    const props = {
      id: "id",
      label: "Friends",
      name: "friends",
      disabled: false,
    };

    const { container, formElement } = renderField(ListField, props, schema);

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

  it.skip("<ListField> - rendering disabled", () => {
    const props = {
      id: "id",
      label: "Friends",
      name: "friends",
      disabled: true,
    };

    const { container, formElement } = renderField(ListField, props, schema);

    expect(container).toMatchSnapshot();

    expect(formElement.html).toContain("<fieldset disabled>");
    expect(formElement.html).toContain(`<legend>${props.label}</legend>`);

    const inputContainer = formElement as FormInputContainer;

    expect(inputContainer.ref).toHaveLength(3);

    expect(inputContainer.ref[0].binding).toEqual("candidate.name");
    expect(inputContainer.ref[1].binding).toEqual("candidate.age");
    expect(inputContainer.ref[2].binding).toEqual("candidate.role");

    expect(formElement.setValueFromModelCode).not.toBeUndefined();
    expect(formElement.writeValueToModelCode).toBeUndefined();
  });
});
