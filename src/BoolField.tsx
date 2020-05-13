import React from 'react';
import { Checkbox, CheckboxProps, Switch } from '@patternfly/react-core';
import { connectField, filterDOMProps } from 'uniforms/es5';

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
  toggle,
  ...props
}) => {
  const Component = (props.appearance === 'switch') ? Switch : Checkbox;
  return (
    <div {...filterDOMProps(props)}>
      <Component
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
};

Bool.defaultProps = {
  appearance: 'checkbox'
};

export default connectField(Bool);
