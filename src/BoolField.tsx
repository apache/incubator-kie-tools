import React from 'react';
import { connectField, filterDOMProps } from 'uniforms';
import { Checkbox, CheckboxProps } from '@patternfly/react-core';

export type BoolFieldProps = {
  appearance?: 'checkbox' | 'switch';
  label?: string;
  legend?: string;
  onChange?: (value: any) => void;
  transform?: (label?: string) => string;
  disabled: boolean;
} & Omit<CheckboxProps, 'isDisabled'>;

const Bool = ({
  disabled,
  id,
  inputRef,
  label,
  name,
  onChange,
  value,
  ...props
}) => (
  <Checkbox
    isChecked={!!value}
    isDisabled={disabled}
    id={id}
    name={name}
    onChange={() => disabled || onChange(!value)}
    ref={inputRef}
    label={label}
    {...filterDOMProps(props)}
  />
);

export default connectField(Bool);
