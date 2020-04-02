import React, { Ref } from 'react';
import { connectField } from 'uniforms';
import { TextInput, TextInputProps } from '@patternfly/react-core';
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
  inputRef?: Ref<HTMLInputElement>;
  onChange: (value: string) => void;
  value?: string;
  disabled: boolean;
} & Omit<TextInputProps, 'isDisabled'>;

const Date = (props: DateFieldProps) => (
  wrapField(
    props,
    <TextInput
      isDisabled={props.disabled}
      id={props.id}
      max={dateFormat(props.max)}
      min={dateFormat(props.min)}
      name={props.name}
      onChange={(value) => dateParse(value, props.onChange)}
      placeholder={props.placeholder}
      ref={props.inputRef}
      type="datetime-local"
      css=""
      value={dateFormat(props.value)}
    />
  )
);

export default connectField(Date);
