import React, { FormEvent } from 'react';
import { connectField, FieldProps } from 'uniforms/es5';
import { TextInput, TextInputProps } from '@patternfly/react-core';

import wrapField from './wrapField';

const DateConstructor = (typeof global === 'object' ? global : window).Date;
const dateFormat = (value?: Date) => value && value.toISOString().slice(0, -8);
const dateParse = (timestamp: number, onChange: DateFieldProps['onChange']) => {
  const date = new DateConstructor(timestamp);
  if (date.getFullYear() < 10000) {
    onChange(date);
  } else if (isNaN(timestamp)) {
    onChange(undefined);
  }
};

export type DateFieldProps = FieldProps<
  Date,
  TextInputProps,
  {
    inputRef: React.RefObject<HTMLInputElement>;
    max?: Date;
    min?: Date;
  }
>;

function Date(props: DateFieldProps) {
  const onChange = (value: string, event: FormEvent<HTMLInputElement>) =>
    props.disabled ||
    dateParse((event.target as any).valueAsNumber, props.onChange);

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
    />
  );
}

export default connectField(Date);
