import React, { Ref, FormEvent } from 'react';
import { TextInput, TextInputProps } from '@patternfly/react-core';

import { connectField, filterDOMProps } from './uniforms';
import wrapField from './wrapField';

export type TextFieldProps = {
  decimal?: boolean;
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
      name={props.name}
      isDisabled={props.disabled}
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
