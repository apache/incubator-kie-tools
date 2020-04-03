import React from 'react';
import { Radio as RadioField, RadioProps } from '@patternfly/react-core';

import { connectField, filterDOMProps } from './uniforms';
import { default as wrapField } from './wrapField';

export type RadioFieldProps = {
  transform?: (string?: string) => string;
  allowedValues: string[];
  onChange: (string) => void;
  value?: string;
  disabled: boolean;
} & Omit<RadioProps, 'isDisabled'>;

const Radio = (props: RadioFieldProps) => {
  return <div {...filterDOMProps(props)} >
    {props.label && <label>{props.label}</label>}
    {props.allowedValues.map(item => (
      <React.Fragment key={item}>
        <label htmlFor={props.id}>{props.transform ? props.transform(item) : item}</label>
        <RadioField
          isChecked={item === props.value}
          isDisabled={props.disabled}
          id={`${props.id}`}
          name={props.name}
          aria-label={props.name}
          onChange={() => props.onChange(item)}
        />
      </React.Fragment>
    ))}
  </div>
}

export default connectField(Radio);
