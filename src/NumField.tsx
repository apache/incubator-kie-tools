import React, { Component, Ref } from 'react';
import { connectField, filterDOMProps } from 'uniforms';
import { TextInput, TextInputProps } from '@patternfly/react-core';

import wrapField from './wrapField';

export type NumFieldProps = {
  decimal?: boolean;
  inputRef?: Ref<HTMLInputElement>;
  onChange: (value?: number) => void;
  disabled: boolean;
} & Omit<TextInputProps, 'isDisabled'>;

const Num = (props: NumFieldProps) =>
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
      {...filterDOMProps(props)}
    />,
  );

export default connectField(Num);
