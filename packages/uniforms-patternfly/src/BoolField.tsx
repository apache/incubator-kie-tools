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
import { Checkbox, CheckboxProps } from "@patternfly/react-core/dist/js/components/Checkbox";
import { Switch, SwitchProps } from "@patternfly/react-core/dist/js/components/Switch";
import { FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { connectField, FieldProps } from "uniforms";
import FieldDetailsPopover from "./FieldDetailsPopover";
import wrapField, { WrapFieldProps } from "./wrapField";

enum ComponentType {
  checkbox = "checkbox",
  switch = "switch",
}

export type BoolFieldProps = FieldProps<
  boolean,
  CheckboxProps & SwitchProps & WrapFieldProps,
  {
    appearance?: ComponentType;
    inputRef?: React.RefObject<Switch | Checkbox> & React.RefObject<HTMLInputElement>;
  }
>;

function BoolField({
  appearance = ComponentType.checkbox,
  disabled,
  id,
  inputRef,
  label,
  name,
  onChange,
  value,
  deprecated,
  ...props
}: BoolFieldProps) {
  const Component = appearance === ComponentType.switch ? Switch : Checkbox;
  let defaultValue;

  if (
    typeof props.field === "object" &&
    props.field !== null &&
    "default" in props.field &&
    typeof props.field.default === "boolean"
  ) {
    defaultValue = props.field.default.toString();
  }

  return wrapField(
    { id, ...props },
    <div style={{ display: "flex", alignItems: "center" }}>
      {" "}
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
      <FieldDetailsPopover default={defaultValue} description={props.description} deprecated={deprecated} />
      <FormHelperText>
        <HelperText>
          <HelperTextItem
            icon={props.error === false && <ExclamationCircleIcon />}
            variant={props.error ? "error" : "default"}
          >
            {!props.error ? "" : props.errorMessage}
          </HelperTextItem>
        </HelperText>
      </FormHelperText>
    </div>
  );
}

export default connectField(BoolField);
