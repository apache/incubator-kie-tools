import React, { Ref } from 'react';
import { connectField, filterDOMProps } from 'uniforms';
import { TextInput, TextInputProps } from '@patternfly/react-core';

import wrapField from './wrapField';

export type TextFieldProps = {
  inputRef?: Ref<HTMLInputElement>;
  onChange: (value: string) => void;
  value?: string;
  disabled: boolean;
} & Omit<TextInputProps, 'isDisabled'>;

const Text = (props: TextFieldProps) =>
  wrapField(
    props,
    <TextInput
      id={props.id}
      name={name}
      isDisabled={props.disabled}
      onChange={value => props.onChange(value)}
      placeholder={props.placeholder}
      ref={props.inputRef}
      type={props.type ?? 'text'}
      value={props.value ?? ''}
      {...filterDOMProps(props)}
    />,
  );

export default connectField(Text);
