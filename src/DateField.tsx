import React, { useCallback, useState } from 'react';
import { connectField, FieldProps, filterDOMProps } from 'uniforms/es5';
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
    max?: Date;
    min?: Date;
  }
>;

const DateConstructor = (typeof global === 'object' ? global : window).Date;

function DateField(props: DateFieldProps) {
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

    let isAm = true;
    let hours = props.value.getHours();
    if (hours > 12) {
      hours %= 12;
      isAm = false;
    }
    const minutes = props.value.getMinutes();
    return `${hours}:${minutes} ${isAm ? 'AM' : 'PM'}`;
  }, [props.value]);

  const onDateChange = useCallback(
    (value: string, date?: Date) => {
      if (!date) {
        props.onChange(date);
      } else {
        const newDate = new DateConstructor(date);
        const time = parseTime();
        if (time !== '') {
          newDate.setHours(parseInt(time?.split(':')[0]));
          newDate.setMinutes(parseInt(time?.split(':')[1]?.split(' ')[0]));
        }
        props.onChange(newDate);
      }
    },
    [props.onChange, parseTime]
  );

  const onTimeChange = useCallback(
    (time: string, hours?: number, minutes?: number) => {
      if (props.value) {
        const newDate = new DateConstructor(props.value);
        if (hours && minutes) {
          newDate.setHours(hours);
          newDate.setMinutes(minutes);
        } else if (time !== '') {
          const localeHours = parseInt(time?.split(':')[0]);
          const localeMinutes = parseInt(time?.split(':')[1]?.split(' ')[0]);
          if (!isNaN(localeHours) && !isNaN(localeMinutes)) {
            newDate.setHours(localeHours);
            newDate.setMinutes(localeMinutes);
          }
        }
        props.onChange(newDate);
      }
    },
    [props.onChange, props.value]
  );

  return wrapField(
    props,
    <Flex
      direction={{ default: 'column' }}
      id={props.id}
      name={props.name}
      ref={props.inputRef}
    >
      <FlexItem>
        <InputGroup style={{ background: 'transparent' }}>
          <DatePicker
            id={`date-picker-${props.id}`}
            data-testid={`date-picker`}
            isDisabled={props.disabled}
            validators={[validateDate]}
            name={props.name}
            onChange={onDateChange}
            value={parseDate() ?? ''}
          />
          <TimePicker
            id={`time-picker-${props.id}`}
            data-testid={`time-picker`}
            isDisabled={props.disabled}
            name={props.name}
            onChange={onTimeChange}
            style={{ width: '120px' }}
            value={parseTime() ?? ''}
          />
        </InputGroup>
      </FlexItem>
    </Flex>
  );
}

export default connectField(DateField);
