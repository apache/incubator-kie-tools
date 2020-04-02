import React, { Component, Ref } from 'react';
import { connectField, filterDOMProps } from 'uniforms';

import wrapField from './wrapField';
import { TextInput, TextInputProps } from '@patternfly/react-core';

const noneIfNaN = x => (isNaN(x) ? undefined : x);

export type NumFieldProps = {
  decimal?: boolean;
  inputRef?: Ref<HTMLInputElement>;
  onChange: (value?: number) => void;
  disabled: boolean;
} & Omit<TextInputProps, 'isDisabled'>;

const Num = (props: NumFieldProps) => (
 
  wrapField(
    props,
    <TextInput 
      isDisabled={props.disabled}
      id={props.id}
      max={props.max}
      min={props.min}
      onChange={props.onChange}
      placeholder={props.placeholder}
      ref={props.inputRef}
      step={props.step || (props.decimal ? 0.01 : 1)}
      type="number"
      css= {CSS}
      {...filterDOMProps(props)}
    />
  )
);

export default connectField(Num);
