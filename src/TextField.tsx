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

const timeRgx = /^([0-1]?[0-9]|2[0-3]):([0-5][0-9])$/;
// simplified date regex
const dateRgx = /^(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\d\d$/;

const Text = (props: TextFieldProps) => {
  const validateDate = useCallback(
    (date) => {
      if (
        typeof props.min === 'string' &&
        dateRgx.exec(props.min) &&
        date.toISOString() < new Date(props.min).toISOString()
      ) {
        return props.errorMessage && props.errorMessage.trim().length > 0
          ? props.errorMessage
          : `Should be after than ${props.min}`;
      } else if (
        typeof props.max === 'string' &&
        dateRgx.exec(props.max) &&
        date.toISOString() > new Date(props.max).toISOString()
      ) {
        return props.errorMessage && props.errorMessage.trim().length > 0
          ? props.errorMessage
          : `Should be before than ${props.max}`;
      }
      return '';
    },
    [props.max, props.min]
  );

  const parseTime = useCallback((time: string) => {
    const parsedTime = timeRgx.exec(time);
    const date = new Date();
    // @ts-ignore
    date.setUTCHours(parsedTime[1], parsedTime[2]);
    return date;
  }, []);

  const validateTime = useCallback(
    (time: string) => {
      const parsedTime = parseTime(time);
      if (typeof props.min === 'string' && timeRgx.exec(props.min)) {
        const parsedMin = parseTime(props.min);
        if (parsedTime < parsedMin) {
          return false;
        }
      }
      if (typeof props.max === 'string' && timeRgx.exec(props.max)) {
        const parsedMax = parseTime(props.max);
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
