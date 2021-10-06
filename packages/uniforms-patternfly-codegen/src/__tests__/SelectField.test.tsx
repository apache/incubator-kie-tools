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
import { MULTIPLE_SELECT_FUNCTIONS, SELECT_FUNCTIONS } from "../uniforms/staticCode/staticCodeBlocks";
import { SelectField } from "../uniforms";
import { NS_SEPARATOR } from "../uniforms/utils/Utils";
import { SELECT_IMPORTS } from "../uniforms/SelectField";

const schema = {
  role: {
    type: String,
    allowedValues: ["Developer", "HR", "UX"],
  },
  otherPositions: {
    type: Array,
    allowedValues: ["Developer", "HR", "UX"],
  },
  "otherPositions.$": String,
};

describe("<SelectField> tests", () => {
  it("<SelectField> - single value rendering", () => {
    const props = {
      id: "id",
      label: "Role",
      name: "role",
      disabled: false,
      allowedValues: ["Developer", "HR", "UX"],
      onChange: jest.fn(),
    };

    const { container, formElement } = renderField(SelectField, props, schema);

    expect(container).toMatchSnapshot();

    expect(formElement.reactImports).toContain("useState");
    expect(formElement.pfImports).toHaveLength(SELECT_IMPORTS.length);
    SELECT_IMPORTS.forEach((pfImport) => expect(formElement.pfImports).toContain(pfImport));

    expect(formElement.requiredCode).not.toBeUndefined();
    expect(formElement.requiredCode).toHaveLength(1);
    expect(formElement.requiredCode).toContain(SELECT_FUNCTIONS);
    expect(formElement.ref.binding).toBe(props.name);
    expect(formElement.ref.stateName).toBe(props.name);
    expect(formElement.ref.stateSetter).toBe(`set__${props.name}`);

    expect(formElement.jsxCode).not.toBeNull();
    expect(formElement.jsxCode).toContain(`label={'${props.label}'}`);
    expect(formElement.jsxCode).toContain(`name={'${props.name}'}`);
    expect(formElement.jsxCode).toContain("isDisabled={false}");
    expect(formElement.jsxCode).toContain("variant={SelectVariant.single}");

    const expandedStateName = `${formElement.ref.stateName}${NS_SEPARATOR}expanded`;
    const expandedStateNameSetter = `${formElement.ref.stateSetter}${NS_SEPARATOR}expanded`;
    expect(formElement.jsxCode).toContain(`isOpen={${expandedStateName}}`);
    expect(formElement.jsxCode).toContain(
      `handleSelect(value, isPlaceHolder, ${formElement.ref.stateName}, ${formElement.ref.stateSetter}, ${expandedStateNameSetter})`
    );
    expect(formElement.jsxCode).toContain(`selections={${formElement.ref.stateName}}`);
    expect(formElement.jsxCode).toContain(`onToggle={(isOpen) => ${expandedStateNameSetter}(isOpen)}`);

    props.allowedValues.forEach((value) => {
      const option = `<SelectOption key={'${value}'} value={'${value}'}>${value}</SelectOption>`;
      expect(formElement.jsxCode).toContain(option);
    });

    expect(formElement.jsxCode).toContain(`value={${formElement.ref.stateName}}`);
    expect(formElement.stateCode).not.toBeNull();
  });

  it("<SelectField> - multiple value rendering", () => {
    const props = {
      id: "id",
      label: "OtherPositions",
      name: "otherPositions",
      disabled: false,
      allowedValues: ["Developer", "HR", "UX"],
      onChange: jest.fn(),
    };

    const { container, formElement } = renderField(SelectField, props, schema);

    expect(container).toMatchSnapshot();

    expect(formElement.reactImports).toContain("useState");
    expect(formElement.pfImports).toHaveLength(SELECT_IMPORTS.length);
    SELECT_IMPORTS.forEach((pfImport) => expect(formElement.pfImports).toContain(pfImport));

    expect(formElement.requiredCode).not.toBeUndefined();
    expect(formElement.requiredCode).toHaveLength(1);
    expect(formElement.requiredCode).toContain(MULTIPLE_SELECT_FUNCTIONS);
    expect(formElement.ref.binding).toBe(props.name);
    expect(formElement.ref.stateName).toBe(props.name);
    expect(formElement.ref.stateSetter).toBe(`set__${props.name}`);

    expect(formElement.jsxCode).not.toBeNull();
    expect(formElement.jsxCode).toContain(`label={'${props.label}'}`);
    expect(formElement.jsxCode).toContain(`name={'${props.name}'}`);
    expect(formElement.jsxCode).toContain("isDisabled={false}");
    expect(formElement.jsxCode).toContain("variant={SelectVariant.typeaheadMulti}");

    const expandedStateName = `${formElement.ref.stateName}${NS_SEPARATOR}expanded`;
    const expandedStateNameSetter = `${formElement.ref.stateSetter}${NS_SEPARATOR}expanded`;
    expect(formElement.jsxCode).toContain(`isOpen={${expandedStateName}}`);
    expect(formElement.jsxCode).toContain(
      `handleMultipleSelect(value, isPlaceHolder, ${formElement.ref.stateName}, ${formElement.ref.stateSetter})`
    );
    expect(formElement.jsxCode).toContain(`selections={${formElement.ref.stateName}}`);
    expect(formElement.jsxCode).toContain(`onToggle={(isOpen) => ${expandedStateNameSetter}(isOpen)}`);

    props.allowedValues.forEach((value) => {
      const option = `<SelectOption key={'${value}'} value={'${value}'}>${value}</SelectOption>`;
      expect(formElement.jsxCode).toContain(option);
    });

    expect(formElement.jsxCode).toContain(`value={${formElement.ref.stateName}}`);
    expect(formElement.stateCode).not.toBeNull();
  });
});
