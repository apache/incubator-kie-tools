import React, { Ref } from 'react';
import { TextInput, TextInputProps } from '@patternfly/react-core';

import { connectField } from './uniforms';
import wrapField from './wrapField';

const noneIfNaN = x => (isNaN(x) ? undefined : x);

export type NumFieldProps = {
  decimal?: boolean;
  inputRef?: Ref<HTMLInputElement>;
  onChange: (value: number, event: React.FormEvent<HTMLInputElement>) => void;
  disabled: boolean;
  value?: number;
} & Omit<TextInputProps, 'isDisabled'>;

const Num = (props: NumFieldProps) => {

  const onChange = (value, event) => {
    const parse = props.decimal ? parseFloat : parseInt;
    const v = parse(event.target.value);
    // @ts-ignore
    props.onChange(isNaN(v) ? undefined : v);
  }

  return wrapField(
    props,
    <TextInput
      name={props.name}
      isDisabled={props.disabled}
      id={props.id}
      max={props.max}
      min={props.min}
      onChange={onChange}
      placeholder={props.placeholder}
      ref={props.inputRef}
      step={props.decimal ? 0.01 : 1}
      type="number"
      value={props.value ?? ''}
    />
  );
}

export default connectField(Num);
