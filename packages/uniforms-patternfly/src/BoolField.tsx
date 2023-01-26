/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { Checkbox, CheckboxProps } from "@patternfly/react-core/dist/js/components/Checkbox";
import { Switch, SwitchProps } from "@patternfly/react-core/dist/js/components/Switch";
import { connectField, FieldProps } from "uniforms";
import wrapField from "./wrapField";

enum ComponentType {
  checkbox = "checkbox",
  switch = "switch",
}

export type BoolFieldProps = FieldProps<
  boolean,
  CheckboxProps & SwitchProps,
  {
    appearance?: ComponentType;
    inputRef?: React.RefObject<Switch | Checkbox> & React.RefObject<HTMLInputElement>;
  }
>;

function BoolField({ appearance, disabled, id, inputRef, label, name, onChange, value, ...props }: BoolFieldProps) {
  const Component = appearance === ComponentType.switch ? Switch : Checkbox;
  return wrapField(
    { id, ...props },
    <Component
      data-testid={"bool-field"}
      isChecked={value ?? false}
      isDisabled={disabled}
      id={id}
      name={name}
      onChange={() => disabled || onChange(!value)}
      ref={inputRef}
      label={label}
    />
  );
}

BoolField.defaultProps = { appearance: ComponentType.checkbox };

export default connectField(BoolField);
