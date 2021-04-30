import React, { Ref, useCallback } from 'react';
import { DatePicker, TextInput, TextInputProps } from '@patternfly/react-core';
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
  errorMessage?: string;
  field?: { format: string };
} & Omit<TextInputProps, 'isDisabled'>;

const Text = (props: TextFieldProps) => {
  const validateDate = useCallback(
    (date) => {
      if (props.min && date < props.min) {
        return props.errorMessage ?? `Should be bigger than ${props.min}`;
      } else if (props.max && date > props.max) {
        return props.errorMessage ?? `Should be smaller than ${props.max}`;
      }
      return '';
    },
    [props.max, props.min]
  );

  const onDateChange = useCallback(
    (value: string) => {
      props.onChange(value);
    },
    [props.disabled, props.onChange]
  );

  return wrapField(
    props,
    props.type === 'date' || props.field?.format === 'date' ? (
      <DatePicker
        name={props.name}
        isDisabled={props.disabled}
        validators={[validateDate]}
        onChange={onDateChange}
        value={props.value ?? ''}
        {...filterDOMProps(props)}
      />
    ) : (
      <TextInput
        name={props.name}
        isDisabled={props.disabled}
        validated={props.error ? 'error' : 'default'}
        onChange={(value, event) => props.onChange((event.target as any).value)}
        placeholder={props.placeholder}
        ref={props.inputRef}
        type={props.type ?? 'text'}
        value={props.value ?? ''}
        {...filterDOMProps(props)}
      />
    )
  );
};

export default connectField(Text);
