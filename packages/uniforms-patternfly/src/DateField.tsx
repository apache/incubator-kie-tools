/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useCallback, useMemo } from "react";
import { connectField, FieldProps } from "uniforms";
import { DatePicker } from "@patternfly/react-core/dist/js/components/DatePicker";
import { InputGroup } from "@patternfly/react-core/dist/js/components/InputGroup";
import { TimePicker } from "@patternfly/react-core/dist/js/components/TimePicker";
import { TextInputProps } from "@patternfly/react-core/dist/js/components/TextInput";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import wrapField from "./wrapField";

export type DateFieldProps = FieldProps<
  string,
  TextInputProps,
  {
    inputRef?: React.RefObject<HTMLInputElement>;
    max?: string;
    min?: string;
  }
>;

const DateConstructor = (typeof global === "object" ? global : window).Date;

function DateField({ onChange, ...props }: DateFieldProps) {
  const dateValue = useCallback(
    (newDate?: Date) => {
      if (newDate) {
        return new DateConstructor(newDate);
      }
      if (!props.value) {
        return undefined;
      }
      return new DateConstructor(props.value);
    },
    [props.value]
  );

  const parseDate = useMemo(() => {
    const date = dateValue();
    if (!date) {
      return "";
    }
    return date.toISOString().slice(0, -14);
  }, [dateValue]);

  const parseTime = useMemo(() => {
    const date = dateValue();
    if (!date) {
      return "";
    }
    return `${date.getUTCHours()}:${date.getUTCMinutes()}`;
  }, [dateValue]);

  const onDateChange = useCallback(
    (event: React.FormEvent<HTMLInputElement>, value: string, date?: Date) => {
      if (!date) {
        onChange(date);
      } else {
        const newDate = dateValue(date);
        const time = parseTime;
        if (time !== "") {
          newDate?.setUTCHours(parseInt(time?.split(":")[0]));
          newDate?.setUTCMinutes(parseInt(time?.split(":")[1]?.split(" ")[0]));
        } else {
          newDate?.setUTCHours(0);
          newDate?.setUTCMinutes(0);
        }
        onChange(newDate?.toISOString());
      }
    },
    [onChange, parseTime, dateValue]
  );

  const isInvalid = useMemo(() => {
    const date = dateValue();
    if (date) {
      if (props.min) {
        const minDate = new Date(props.min);
        if (minDate.toString() === "Invalid Date") {
          return false;
        } else if (date < minDate) {
          return `Should be after ${minDate.toISOString()}`;
        }
      }
      if (props.max) {
        const maxDate = new Date(props.max);
        if (maxDate.toString() === "Invalid Date") {
          return false;
        } else if (date > maxDate) {
          return `Should be before ${maxDate.toISOString()}`;
        }
      }
    }
    return false;
  }, [dateValue, props.min, props.max]);

  const onTimeChange = useCallback(
    (event: React.FormEvent<HTMLInputElement>, time: string, hours?: number, minutes?: number) => {
      const newDate = dateValue();
      if (newDate) {
        if (hours && minutes) {
          newDate.setUTCHours(hours);
          newDate.setUTCMinutes(minutes);
        } else if (time !== "") {
          const localeHours = parseInt(time?.split(":")[0]);
          const localeMinutes = parseInt(time?.split(":")[1]?.split(" ")[0]);
          if (!isNaN(localeHours) && !isNaN(localeMinutes)) {
            newDate.setUTCHours(localeHours);
            newDate.setUTCMinutes(localeMinutes);
          }
        } else {
          newDate.setUTCHours(0);
          newDate.setUTCMinutes(0);
        }
        onChange(newDate.toISOString());
      }
    },
    [onChange, dateValue]
  );

  return wrapField(
    props as any,
    <Flex
      data-testid={"date-field"}
      style={{ margin: 0 }}
      direction={{ default: "column" }}
      id={props.id}
      name={props.name}
      ref={props.inputRef}
    >
      <FlexItem style={{ margin: 0 }}>
        <InputGroup style={{ background: "transparent" }}>
          <DatePicker
            id={`date-picker-${props.id}`}
            data-testid={`date-picker`}
            isDisabled={props.disabled}
            name={props.name}
            onChange={onDateChange}
            value={parseDate}
          />
          <TimePicker
            id={`time-picker-${props.id}`}
            data-testid={`time-picker`}
            isDisabled={props.disabled || !props.value}
            name={props.name}
            onChange={onTimeChange}
            style={{ width: "120px" }}
            value={parseTime}
            is24Hour
          />
        </InputGroup>
      </FlexItem>
      {isInvalid && (
        <div
          id={`${props.id}-invalid-date-time`}
          style={{
            fontSize: "0.875rem",
            color: "#c9190b",
            marginTop: "0.25rem",
          }}
        >
          {isInvalid}
        </div>
      )}
    </Flex>
  );
}

export default connectField(DateField);
