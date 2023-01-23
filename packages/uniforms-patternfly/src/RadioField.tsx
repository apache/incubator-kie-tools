import React from 'react';
import { Radio as RadioField, RadioProps } from '@patternfly/react-core';
import { connectField, filterDOMProps, HTMLFieldProps } from 'uniforms';

import wrapField from './wrapField';

export type RadioFieldProps = HTMLFieldProps<
  string,
  HTMLDivElement,
  {
    transform?: (string?: string) => string;
    allowedValues: string[];
    onChange: (value: string) => void;
    value?: string;
    disabled: boolean;
  }
>;

const Radio = (props: RadioFieldProps) => {
  filterDOMProps.register('checkboxes', 'decimal');
  return wrapField(
    props,
    <div {...filterDOMProps(props)}>
      {props.allowedValues?.map((item) => (
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
  );
};

export default connectField(Radio);
