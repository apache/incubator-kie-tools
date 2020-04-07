import React from 'react';
import { Radio as RadioField, RadioProps } from '@patternfly/react-core';

import { connectField, filterDOMProps } from './uniforms';
export type RadioFieldProps = {
  transform?: (string?: string) => string;
  allowedValues: string[];
  onChange: (string) => void;
  value?: string;
  disabled: boolean;
} & Omit<RadioProps, 'isDisabled'>;

const Radio = (props: RadioFieldProps) => {
  filterDOMProps.register('checkboxes', 'decimal');
  return <div {...filterDOMProps(props)} >
    {props.label && <div><label>{props.label}</label></div>}
    {props.allowedValues.map(item => (
      <React.Fragment key={item}>
        <RadioField
          isChecked={item === props.value}
          isDisabled={props.disabled}
          id={`${props.id}`}
          name={props.name}
          label={props.transform ? props.transform(item) : item}
          aria-label={props.name}
          onChange={() => props.onChange(item)}
        />
      </React.Fragment>
    ))}
  </div>
}

export default connectField(Radio);
