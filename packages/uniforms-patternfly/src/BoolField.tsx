import React from 'react';
import {
  Checkbox,
  CheckboxProps,
  Switch,
  SwitchProps,
} from '@patternfly/react-core';
import { connectField, FieldProps } from 'uniforms';

import wrapField from './wrapField';

enum ComponentType {
  checkbox = 'checkbox',
  switch = 'switch',
}

export type BoolFieldProps = FieldProps<
  boolean,
  CheckboxProps & SwitchProps,
  {
    appearance?: ComponentType;
    inputRef: React.RefObject<Switch | Checkbox> &
      React.RefObject<HTMLInputElement>;
  }
>;

function Bool({
  appearance,
  disabled,
  id,
  inputRef,
  label,
  name,
  onChange,
  value,
  ...props
}: BoolFieldProps) {
  const Component = appearance === ComponentType.switch ? Switch : Checkbox;
  return wrapField(
    { id, ...props },
    <Component
      isChecked={value || false}
      isDisabled={disabled}
      id={id}
      name={name}
      onChange={() => disabled || onChange(!value)}
      ref={inputRef}
      label={label}
    />
  );
}

Bool.defaultProps = { appearance: ComponentType.checkbox };

export default connectField(Bool);
