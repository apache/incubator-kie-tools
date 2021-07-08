import React, { useCallback, useMemo } from 'react';
import { connectField, FieldProps } from 'uniforms/es5';
import {
  DatePicker,
  Flex,
  FlexItem,
  InputGroup,
  TextInputProps,
  TimePicker,
} from '@patternfly/react-core';

import wrapField from './wrapField';

export type DateFieldProps = FieldProps<
  Date,
  TextInputProps,
  {
    inputRef: React.RefObject<HTMLInputElement>;
    max?: string;
    min?: string;
  }
>;

const DateConstructor = (typeof global === 'object' ? global : window).Date;

function DateField(props: DateFieldProps) {
  const parseDate = useCallback(() => {
    if (!props.value) {
      return '';
    }
    return props.value.toISOString().slice(0, -14);
  }, [props.value]);

  const parseTime = useCallback(() => {
    if (!props.value) {
      return '';
    }
    return `${props.value.getUTCHours()}:${props.value.getUTCMinutes()}`;
  }, [props.value]);

  const onDateChange = useCallback(
    (value: string, date?: Date) => {
      if (!date) {
        props.onChange(date);
      } else {
        const newDate = new DateConstructor(date);
        const time = parseTime();
        if (time !== '') {
          newDate.setUTCHours(parseInt(time?.split(':')[0]));
          newDate.setUTCMinutes(parseInt(time?.split(':')[1]?.split(' ')[0]));
        } else {
          newDate.setUTCHours(0);
          newDate.setUTCMinutes(0);
        }
        props.onChange(newDate);
      }
    },
    [props.onChange, parseTime]
  );

  const isInvalid = useMemo(() => {
    if (props.value) {
      if (props.min) {
        const minDate = new Date(props.min);
        if (minDate.toString() === 'Invalid Date') {
          return false;
        } else if (props.value < minDate) {
          return `Should be after ${minDate.toISOString()}`;
        }
      }
      if (props.max) {
        const maxDate = new Date(props.max);
        if (maxDate.toString() === 'Invalid Date') {
          return false;
        } else if (props.value > maxDate) {
          return `Should be before ${maxDate.toISOString()}`;
        }
      }
    }
    return false;
  }, [props.value]);

  const onTimeChange = useCallback(
    (time: string, hours?: number, minutes?: number) => {
      if (props.value) {
        const newDate = new DateConstructor(props.value);
        if (hours && minutes) {
          newDate.setUTCHours(hours);
          newDate.setUTCMinutes(minutes);
        } else if (time !== '') {
          const localeHours = parseInt(time?.split(':')[0]);
          const localeMinutes = parseInt(time?.split(':')[1]?.split(' ')[0]);
          if (!isNaN(localeHours) && !isNaN(localeMinutes)) {
            newDate.setUTCHours(localeHours);
            newDate.setUTCMinutes(localeMinutes);
          }
        } else {
          newDate.setUTCHours(0);
          newDate.setUTCMinutes(0);
        }
        props.onChange(newDate);
      }
    },
    [props.onChange, props.value]
  );

  return wrapField(
    props,
    <Flex
      style={{ margin: 0 }}
      direction={{ default: 'column' }}
      id={props.id}
      name={props.name}
      ref={props.inputRef}
    >
      <FlexItem style={{ margin: 0 }}>
        <InputGroup style={{ background: 'transparent' }}>
          <DatePicker
            id={`date-picker-${props.id}`}
            data-testid={`date-picker`}
            isDisabled={props.disabled}
            name={props.name}
            onChange={onDateChange}
            value={parseDate() ?? ''}
          />
          <TimePicker
            id={`time-picker-${props.id}`}
            data-testid={`time-picker`}
            isDisabled={props.disabled || !props.value}
            name={props.name}
            onChange={onTimeChange}
            style={{ width: '120px' }}
            value={parseTime() ?? ''}
            is24Hour
          />
        </InputGroup>
      </FlexItem>
      {isInvalid && (
        <div
          id={`${props.id}-invalid-date-time`}
          style={{
            fontSize: '0.875rem',
            color: '#c9190b',
            marginTop: '0.25rem',
          }}
        >
          {isInvalid}
        </div>
      )}
    </Flex>
  );
}

export default connectField(DateField);
