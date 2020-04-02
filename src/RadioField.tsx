import React from 'react';
import { connectField } from 'uniforms';
import { Radio as RadioField, RadioProps } from '@patternfly/react-core'

export type RadioFieldProps = {
  transform?: (string?: string) => string;
  allowedValues: string[];
  onChange: (string) => void;
  value?: string;
} & RadioProps;

const Radio = (props: RadioFieldProps) => (
  <React.Fragment>
    {props.allowedValues.map(item => (
      < RadioField
        key={item}
        isChecked={item === props.value}
        isDisabled={props.isDisabled}
        id={`${props.id}-${escape(item)}`}
        name={name}
        label={props.label}
        onChange={() => props.onChange(item)}
      />
    ))}
  </React.Fragment>
);

export default connectField(Radio);
