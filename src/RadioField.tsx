import React from 'react';
import { Radio as RadioField, RadioProps } from '@patternfly/react-core';

import { connectField } from './uniforms';
import { default as wrapField } from './wrapField';

export type RadioFieldProps = {
  transform?: (string?: string) => string;
  allowedValues: string[];
  onChange: (string) => void;
  value?: string;
  disabled: boolean;
} & Omit<RadioProps, 'isDisabled'>;

const Radio = (props: RadioFieldProps) => (
  wrapField(
    props,
    props.allowedValues.map(item => (
      <RadioField
        key={item}
        isChecked={item === props.value}
        isDisabled={props.disabled}
        id={`${props.id}-${escape(item)}`}
        name={name}
        label={props.label}
        onChange={() => props.onChange(item)}
      />
    ))
  )
);

export default connectField(Radio);
