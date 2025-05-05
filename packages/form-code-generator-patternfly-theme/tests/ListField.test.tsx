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
import { InputsContainer } from "../src/api";
import { DATE_FUNCTIONS } from "../src/uniforms/staticCode/staticCodeBlocks";

describe("<ListField> tests", () => {
  it("<ListField>", () => {
    const { formElement } = renderField(
      ListField,
      {
        id: "id",
        label: "Friends",
        name: "friends",
        disabled: false,
      },
      {
        friends: { type: Array },
        "friends.$": Object,
        "friends.$.name": { type: String },
        "friends.$.age": { type: Number },
        "friends.$.country": { type: String, allowedValues: ["US", "Brazil"] },
        "friends.$.married": { type: Boolean },
        "friends.$.know": {
          type: Array,
          uniforms: {
            checkboxes: true,
          },
        },
        "friends.$.know.$": String,
        "friends.$.areas": {
          type: Array,
          allowedValues: ["Developer", "HR", "UX"],
        },
        "friends.$.areas.$": String,
        "friends.$.birthday": { type: Date },
        "friends.$.transport": {
          type: Array,
          allowedValues: ["Taxi", "Uber"],
          uniforms: {
            checkboxes: true,
          },
        },
        "friends.$.transport.$": String,
        "friends.$.children": {
          type: String,
          allowedValues: ["0", "1", "2+"],
        },
      }
    );
    expect(formElement.jsxCode).toMatchSnapshot();
    expect(formElement.stateCode).toMatchSnapshot();

    const inputContainer = formElement as InputsContainer;
    expect(inputContainer.pfImports).toStrictEqual([
      "Split",
      "SplitItem",
      "Button",
      "Card",
      "CardBody",
      "TextInput",
      "FormGroup",
      "Checkbox",
      "DatePicker",
      "Flex",
      "FlexItem",
      "InputGroup",
      "TimePicker",
    ]);
    expect(inputContainer.pfDeprecatedImports).toStrictEqual([
      "SelectOption",
      "SelectOptionObject",
      "Select",
      "SelectVariant",
    ]);
    expect(inputContainer.pfIconImports).toStrictEqual(["PlusCircleIcon", "MinusCircleIcon"]);

    expect(inputContainer.requiredCode).not.toBeUndefined();
    expect(inputContainer.requiredCode).toHaveLength(1);
    expect(inputContainer.requiredCode).toContain(DATE_FUNCTIONS);
  });
});
