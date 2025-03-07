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
import { SelectField } from "../src/uniforms";
import { NS_SEPARATOR } from "../src/uniforms/utils/Utils";
import { SELECT_IMPORTS } from "../src/uniforms/SelectField";

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

    const { formElement } = renderField(SelectField, props, {
      role: {
        type: String,
        allowedValues: ["Developer", "HR", "UX"],
      },
    });

    expect(formElement.jsxCode).toMatchSnapshot();

    expect(formElement.reactImports).toContain("useState");
    expect(formElement.pfImports).toHaveLength(SELECT_IMPORTS.length);
    SELECT_IMPORTS.forEach((pfImport) => expect(formElement.pfImports).toContain(pfImport));

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

    const { formElement } = renderField(SelectField, props, {
      otherPositions: {
        type: Array,
        allowedValues: ["Developer", "HR", "UX"],
      },
      "otherPositions.$": String,
    });

    expect(formElement.jsxCode).toMatchSnapshot();

    expect(formElement.reactImports).toContain("useState");
    expect(formElement.pfImports).toHaveLength(SELECT_IMPORTS.length);
    SELECT_IMPORTS.forEach((pfImport) => expect(formElement.pfImports).toContain(pfImport));

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
