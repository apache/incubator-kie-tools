import React, { Ref, useCallback } from 'react';
import {
  DatePicker,
  TextInput,
  TextInputProps,
  TimePicker,
} from '@patternfly/react-core';
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

  const parseTime = useCallback((time: string) => {
    const parsedTime = time.split(':').map((e) => parseInt(e, 10));
    const date = new Date();
    // @ts-ignore
    date.setUTCHours(...parsedTime);
    return date;
  }, []);

  const validateTime = useCallback(
    (time: string) => {
      if (typeof props.min === 'string' && typeof props.max === 'string') {
        const parsedTime = parseTime(time);
        const parsedMin = parseTime(props.min);
        const parsedMax = parseTime(props.max);
        if (parsedTime < parsedMin) {
          return false;
        }
        if (parsedTime > parsedMax) {
          return false;
        }
      }
      return true;
    },
    [props.max, props.min]
  );

  const onDateChange = useCallback(
    (value: string) => {
      props.onChange(value);
    },
    [props.disabled, props.onChange]
  );

  const onTimeChange = useCallback(
    (time: string) => {
      const parsedTime = time.split(':');
      if (parsedTime.length === 2) {
        props.onChange([...parsedTime, '00'].join(':'));
      } else {
        props.onChange(time);
      }
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
    ) : props.type === 'time' || props.field?.format === 'time' ? (
      <TimePicker
        name={props.name}
        isDisabled={props.disabled}
        validateTime={validateTime}
        onChange={onTimeChange}
        is24Hour
        value={props.value ?? ''}
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
