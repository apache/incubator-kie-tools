import React, { Ref } from 'react';
import { TextInput, TextInputProps } from '@patternfly/react-core';

import { connectField } from './uniforms';
import wrapField from './wrapField';

const noneIfNaN = x => (isNaN(x) ? undefined : x);

export type NumFieldProps = {
  decimal?: boolean;
  inputRef?: Ref<HTMLInputElement>;
  onChange: (value?: number) => void;
  disabled: boolean;
  value?: number;
} & Omit<TextInputProps, 'isDisabled'>;

const Num = (props: NumFieldProps) =>
  wrapField(
    props,
    <TextInput
      isDisabled={props.disabled}
      id={props.id}
      max={props.max}
      min={props.min}
      onChange={(value) => props.onChange(noneIfNaN(value))}
      placeholder={props.placeholder}
      ref={props.inputRef}
      step={props.step || (props.decimal ? 0.01 : 1)}
      type="number"
    />,
  );

export default connectField(Num);
