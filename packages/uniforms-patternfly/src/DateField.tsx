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
import { useMemo } from "react";
import { connectField, FieldProps } from "uniforms";
import { TextInput, TextInputProps } from "@patternfly/react-core/dist/js/components/TextInput";
import wrapField from "./wrapField";

export type DateFieldProps = FieldProps<
  Date,
  TextInputProps,
  {
    inputRef?: React.RefObject<HTMLInputElement>;
    labelProps?: object;
    max?: Date;
    min?: Date;
    type?: "date" | "datetime-local";
  }
>;

type DateFieldType = "date" | "datetime-local";

const DateConstructor = (typeof global === "object" ? global : window).Date;

const dateFormat = (value?: Date | string, type: DateFieldType = "datetime-local") => {
  if (typeof value === "string") {
    return value?.slice(0, type === "datetime-local" ? -8 : -14);
  }
  return value?.toISOString().slice(0, type === "datetime-local" ? -8 : -14);
};

const dateParse = (value: string, onChange: DateFieldProps["onChange"]) => {
  const valueAsNumber = DateConstructor.parse(value);
  if (isNaN(valueAsNumber)) {
    // Checking if year is too big
    const splitedValue = value.split("-");
    if (splitedValue.length > 1) {
      // A year can't be bigger than 9999;
      splitedValue[0] = parseInt(splitedValue[0]) > 9999 ? "9999" : splitedValue[0];
      onChange(new DateConstructor(`${splitedValue.join("-")}Z`));
      return;
    }
    onChange(undefined);
  } else {
    const date = new DateConstructor(`${value}Z`);
    if (date.getFullYear() < 10000) {
      onChange(date);
    } else {
      onChange(date);
    }
  }
};

function DateField({ onChange, ...props }: DateFieldProps) {
  const isInvalid = useMemo(() => {
    if (!props.value) {
      return false;
    }

    if (props.min) {
      const minDate = new Date(props.min);
      if (minDate.toString() === "Invalid Date") {
        return false;
      } else if (props.value < minDate) {
        return `Should be after ${minDate.toISOString()}`;
      }
    }
    if (props.max) {
      const maxDate = new Date(props.max);
      if (maxDate.toString() === "Invalid Date") {
        return false;
      } else if (props.value > maxDate) {
        return `Should be before ${maxDate.toISOString()}`;
      }
    }

    return false;
  }, [props.value, props.min, props.max]);

  return wrapField(
    props as any,
    <>
      <TextInput
        id={props.id}
        aria-label={"uniforms date field"}
        data-testid={"date-field"}
        isDisabled={props.disabled}
        name={props.name}
        placeholder={props.placeholder}
        ref={props.inputRef}
        type="datetime-local"
        onChange={(value) => {
          props.disabled || dateParse(value, onChange);
        }}
        value={dateFormat(props.value, props.type) ?? ""}
      />
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
    </>
  );
}

export default connectField(DateField);
