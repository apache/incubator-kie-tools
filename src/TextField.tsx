import React, { Ref } from 'react';
import { TextInput, TextInputProps } from '@patternfly/react-core';
import { connectField, filterDOMProps } from 'uniforms/es5';

import wrapField from './wrapField';

export type TextFieldProps = {
  id: string;
  decimal?: boolean;
  inputRef?: Ref<HTMLInputElement>;
  onChange: (value?: string) => void;
  value?: string;
  disabled: boolean;
  error?: boolean;
} & Omit<TextInputProps, 'isDisabled'>;

const Text = (props: TextFieldProps) =>
  wrapField(
    props,
    <TextInput
      id={props.id}
      name={props.name}
      isDisabled={props.disabled}
      isValid={!props.error}
      // @ts-ignore
      onChange={(value, event) => props.onChange(event.target.value)}
      placeholder={props.placeholder}
      ref={props.inputRef}
      type={props.type ?? 'text'}
      value={props.value ?? ''}
      {...filterDOMProps(props)}
    />,
  );

export default connectField(Text);
