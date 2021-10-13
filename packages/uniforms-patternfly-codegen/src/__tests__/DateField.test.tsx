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
import { renderField } from "./_render";
import { DATE_FUNCTIONS, TIME_FUNCTIONS } from "../uniforms/staticCode/staticCodeBlocks";
import { DateField } from "../uniforms";

const schema = {
  birthday: { type: Date },
};

describe("<DateField> tests", () => {
  it("<DateField> - rendering", () => {
    const props = {
      id: "id",
      label: "Birthday",
      name: "birthday",
      disabled: false,
      onChange: jest.fn(),
    };

    const { container, formElement } = renderField(DateField, props, schema);

    expect(container).toMatchSnapshot();

    expect(formElement.reactImports).toContain("useState");
    expect(formElement.pfImports).toContain("FormGroup");
    expect(formElement.pfImports).toContain("DatePicker");
    expect(formElement.pfImports).toContain("TimePicker");
    expect(formElement.requiredCode).not.toBeUndefined();
    expect(formElement.requiredCode).toHaveLength(2);
    expect(formElement.requiredCode).toContain(DATE_FUNCTIONS);
    expect(formElement.requiredCode).toContain(TIME_FUNCTIONS);
    expect(formElement.ref.binding).toBe(props.name);
    expect(formElement.ref.stateName).toBe(props.name);
    expect(formElement.ref.stateSetter).toBe(`set__${props.name}`);

    expect(formElement.jsxCode).not.toBeNull();
    expect(formElement.jsxCode).toContain(`label={'${props.label}'}`);
    expect(formElement.jsxCode).toContain(`name={'${props.name}'}`);
    expect(formElement.jsxCode).toContain("isDisabled={false}");
    expect(formElement.jsxCode).toContain(
      `onChange={newDate => onDateChange(newDate, ${formElement.ref.stateSetter},  ${formElement.ref.stateName})}`
    );
    expect(formElement.jsxCode).toContain(`value={parseDate(${formElement.ref.stateName})}`);
    expect(formElement.jsxCode).toContain(`time={parseTime(${formElement.ref.stateName})}`);
    expect(formElement.jsxCode).toContain(
      `onChange={(time, hours?, minutes?) => onTimeChange(time, ${formElement.ref.stateSetter}, ${formElement.ref.stateName}, hours, minutes)}`
    );
    expect(formElement.stateCode).not.toBeNull();
  });

  it("<DateField> - rendering - disabled", () => {
    const props = {
      id: "id",
      label: "Birthday",
      name: "birthday",
      disabled: true,
      onChange: jest.fn(),
    };

    const { container, formElement } = renderField(DateField, props, schema);

    expect(container).toMatchSnapshot();

    expect(formElement.jsxCode).not.toBeNull();
    expect(formElement.jsxCode).toContain(`label={'${props.label}'}`);
    expect(formElement.jsxCode).toContain(`name={'${props.name}'}`);
    expect(formElement.jsxCode).toContain("isDisabled={true}");
  });
});
