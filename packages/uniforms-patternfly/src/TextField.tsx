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
import { Ref, useCallback, useMemo } from "react";
import { DatePicker } from "@patternfly/react-core/dist/js/components/DatePicker";
import { TextInput, TextInputProps } from "@patternfly/react-core/dist/js/components/TextInput";
import { TimePicker } from "@patternfly/react-core/dist/js/components/TimePicker";
import { connectField, filterDOMProps } from "uniforms";
import wrapField from "./wrapField";

export type TextFieldProps = {
  id: string;
  decimal?: boolean;
  inputRef?: Ref<HTMLInputElement>;
  onChange: (value?: string) => void;
  value?: string;
  disabled?: boolean;
  error?: boolean;
  errorMessage?: string;
  helperText?: string;
  field?: { format: string };
} & Omit<TextInputProps, "isDisabled">;

const timeRgx = /^([0-1]?[0-9]|2[0-3]):([0-5][0-9])(:[0-5][0-9])?/;

function TextField({ onChange, ...props }: TextFieldProps) {
  const isDateInvalid = useMemo(() => {
    if (typeof props.value === "string" && (props.type === "date" || props.field?.format === "date")) {
      const date = new Date(props.value);
      if (typeof props.min === "string") {
        const minDate = new Date(props.min);
        if (minDate.toString() === "Invalid Date") {
          return false;
        } else if (date.toISOString() < minDate.toISOString()) {
          return props.errorMessage && props.errorMessage.trim().length > 0
            ? props.errorMessage
            : `Should be after ${props.min}`;
        }
      }
      if (typeof props.max === "string") {
        const maxDate = new Date(props.max);
        if (maxDate.toString() === "Invalid Date") {
          return false;
        } else if (date.toISOString() > maxDate.toISOString()) {
          return props.errorMessage && props.errorMessage.trim().length > 0
            ? props.errorMessage
            : `Should be before ${props.max}`;
        }
      }
    }
    return false;
  }, [props.value, props.max, props.min, props.errorMessage, props.type, props.field]);

  const parseTime = useCallback((time: string) => {
    const parsedTime = timeRgx.exec(time);
    const date = new Date();
    if (!parsedTime) {
      return undefined;
    }
    date.setUTCHours(Number(parsedTime[1]), Number(parsedTime[2]!));
    return date;
  }, []);

  const isTimeInvalid = useMemo(() => {
    if (typeof props.value === "string" && (props.type === "time" || props.field?.format === "time")) {
      const parsedTime = parseTime(props.value);
      if (parsedTime && typeof props.min === "string" && timeRgx.exec(props.min)) {
        const parsedMin = parseTime(props.min)!;
        if (parsedTime < parsedMin) {
          if (parsedMin.getUTCMinutes() < 10) {
            return `Should be after ${parsedMin.getUTCHours()}:0${parsedMin.getUTCMinutes()}`;
          }
          return `Should be after ${parsedMin.getUTCHours()}:${parsedMin.getUTCMinutes()}`;
        }
      }
      if (parsedTime && typeof props.max === "string" && timeRgx.exec(props.max)) {
        const parsedMax = parseTime(props.max)!;
        if (parsedTime > parsedMax) {
          if (parsedMax.getUTCMinutes() < 10) {
            return `Should be before ${parsedMax.getUTCHours()}:0${parsedMax.getUTCMinutes()}`;
          }
          return `Should be before ${parsedMax.getUTCHours()}:${parsedMax.getUTCMinutes()}`;
        }
      }
    }
    return false;
  }, [props.type, props.field, props.value, props.max, props.min, parseTime]);

  const onDateChange = useCallback(
    (event: React.FormEvent<HTMLInputElement>, value: string) => {
      onChange(value);
    },
    [onChange]
  );

  const onTimeChange = useCallback(
    (event: React.FormEvent<HTMLInputElement>, time: string) => {
      const parsedTime = time.split(":");
      if (parsedTime.length === 2) {
        onChange([...parsedTime, "00"].join(":"));
      } else {
        onChange(time);
      }
    },
    [onChange]
  );

  const { helperText, ...withoutHelperTextProps } = props;

  return wrapField(
    props,
    props.type === "date" || props.field?.format === "date" ? (
      <>
        <DatePicker
          data-testid={"text-field-date-picker"}
          name={props.name}
          isDisabled={props.disabled}
          onChange={onDateChange}
          value={props.value ?? ""}
          {...filterDOMProps(props)}
        />
        {isDateInvalid && (
          <div
            style={{
              fontSize: "0.875rem",
              color: "#c9190b",
              marginTop: "0.25rem",
            }}
          >
            {isDateInvalid}
          </div>
        )}
      </>
    ) : props.type === "time" || props.field?.format === "time" ? (
      <>
        <TimePicker
          data-testid={"text-field-time-picker"}
          name={props.name}
          isDisabled={props.disabled}
          onChange={onTimeChange}
          is24Hour
          value={props.value ?? ""}
        />
        {isTimeInvalid && (
          <div
            style={{
              fontSize: "0.875rem",
              color: "#c9190b",
              marginTop: "0.25rem",
            }}
          >
            {isTimeInvalid}
          </div>
        )}
      </>
    ) : (
      <TextInput
        data-testid={"text-field"}
        name={props.name}
        isDisabled={props.disabled}
        validated={props.error ? "error" : "default"}
        onChange={(value, event) => onChange((event.target as any)?.value)}
        placeholder={props.placeholder}
        ref={props.inputRef}
        type={props.type ?? "text"}
        value={props.value ?? ""}
        {...filterDOMProps(withoutHelperTextProps)}
      />
    )
  );
}

export default connectField(TextField);
