import React from 'react';
import { Checkbox, CheckboxProps } from '@patternfly/react-core';

import { connectField } from './uniforms';
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
  wrapField(
    props,
    <Checkbox
      isChecked={!!value}
      isDisabled={disabled}
      id={id}
      name={name}
      onChange={() => disabled || onChange(!value)}
      ref={inputRef}
      label={label}
    />
  )
);

export default connectField(Bool);
