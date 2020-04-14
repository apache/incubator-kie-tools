import React from 'react';
import { Checkbox, CheckboxProps } from '@patternfly/react-core';
import { connectField, filterDOMProps } from 'uniforms';

import wrapField from './wrapField';

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
  <div {...filterDOMProps(props)}>
    <Checkbox
      isChecked={!!value}
      isDisabled={disabled}
      id={id}
      name={name}
      onChange={() => disabled || onChange(!value)}
      ref={inputRef}
      label={label}
    />
  </div>
);

export default connectField(Bool);
