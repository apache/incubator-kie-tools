import React, { Ref } from 'react';
import { TextInput, TextInputProps } from '@patternfly/react-core';
import { connectField } from 'uniforms/es5';

import wrapField from './wrapField';

const DateConstructor = (typeof global === 'object' ? global : window).Date;
const dateFormat = value => value && value.toISOString().slice(0, -8);
const dateParse = (timestamp, onChange) => {
  const date = new DateConstructor(timestamp);
  if (date.getFullYear() < 10000) {
    onChange(date);
  } else if (isNaN(timestamp)) {
    onChange(undefined);
  }
};

export type DateFieldProps = {
  id: string;
  inputRef?: Ref<HTMLInputElement>;
  onChange: (value?: string) => void;
  value?: string;
  disabled: boolean;
  error?: boolean;
} & Omit<TextInputProps, 'isDisabled'>;

function Date(props: DateFieldProps) {

  const onChange = (value, event) => {
    props.disabled || dateParse(event.target.valueAsNumber, props.onChange)
  }

  return wrapField(
    props,
    <TextInput
      isDisabled={props.disabled}
      id={props.id}
      max={dateFormat(props.max)}
      min={dateFormat(props.min)}
      name={props.name}
      onChange={onChange}
      placeholder={props.placeholder}
      ref={props.inputRef}
      type="datetime-local"
      value={dateFormat(props.value) ?? ''}
    />,
  );
}

export default connectField(Date);
