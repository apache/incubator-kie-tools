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
import { CheckBoxGroupField } from "../src/uniforms";

describe("<CheckBoxGroupField> tests", () => {
  it("<CheckBoxGroupField> - rendering", () => {
    const props = {
      id: "id",
      label: "Roles",
      name: "roles",
      disabled: false,
      allowedValues: ["Developer", "HR", "UX"],
      onChange: jest.fn(),
    };

    const { formElement } = renderField(CheckBoxGroupField, props, {
      roles: {
        type: Array,
        allowedValues: ["Developer", "HR", "UX"],
        uniforms: {
          checkboxes: true,
        },
      },
      "roles.$": String,
    });

    expect(formElement.jsxCode).toMatchSnapshot();

    expect(formElement.reactImports).toContain("useState");

    expect(formElement.pfImports).toContain("FormGroup");
    expect(formElement.pfImports).toContain("Checkbox");

    expect(formElement.ref.binding).toBe(props.name);
    expect(formElement.ref.stateName).toBe(props.name);
    expect(formElement.ref.stateSetter).toBe(`set__${props.name}`);

    expect(formElement.jsxCode).not.toBeNull();
    expect(formElement.jsxCode).toContain(`label={'${props.label}'}`);
    expect(formElement.jsxCode).toContain(`name={'${props.name}'}`);
    expect(formElement.jsxCode).toContain("isDisabled={false}");

    props.allowedValues.forEach((value) => {
      const checkbox = `<Checkbox
  key={'${props.id}-${value}'}
  id={'${props.id}-${value}'}
  name={'${props.name}'}
  aria-label={'${props.name}'}`;
      expect(formElement.jsxCode).toContain(checkbox);
      expect(formElement.jsxCode).toContain(`label={'${value}'}`);
      expect(formElement.jsxCode).toContain(`isChecked={${formElement.ref.stateName}.indexOf('${value}') !== -1}`);
      expect(formElement.jsxCode).toContain(`value={'${value}'}`);
    });
    expect(formElement.stateCode).not.toBeNull();
  });
});
