/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { connectField, HTMLFieldProps } from "uniforms/cjs";

import { FormInput, InputReference } from "../api";
import { buildDefaultInputElement, getInputReference, renderField } from "./utils/Utils";
import { useAddFormElementToContext } from "./CodeGenContext";
import { DATE_FUNCTIONS, TIME_FUNCTIONS } from "./staticCode/staticCodeBlocks";
import { DEFAULT_DATA_TYPE_DATE } from "./utils/dataTypes";
import { getListItemName, getListItemOnChange, getListItemValue, ListItemProps } from "./rendering/ListItemField";

export type DateFieldProps = HTMLFieldProps<
  Date,
  HTMLDivElement,
  {
    name: string;
    label: string;
    required: boolean;
    max?: Date;
    min?: Date;
    itemProps: ListItemProps;
  }
>;

const Date: React.FC<DateFieldProps> = (props: DateFieldProps) => {
  const ref: InputReference = getInputReference(props.name, DEFAULT_DATA_TYPE_DATE);

  const pfImports = ["DatePicker", "Flex", "FlexItem", "InputGroup", "TimePicker"];

  const jsxCode = `<Flex
    direction={{ default: 'column' }}
    id={'${props.id}'}
  >
    <FlexItem>
      <InputGroup style={{ background: 'transparent' }}>
        <DatePicker
          id={'date-picker-${props.id}'}
          isDisabled={${props.disabled || false}}
          name={${props.itemProps?.isListItem ? getListItemName({ itemProps: props.itemProps, name: props.name }) : `'${props.name}'`}}
          onChange={${
            props.itemProps?.isListItem
              ? getListItemOnChange({
                  itemProps: props.itemProps,
                  name: props.name,
                  callback: (value) => `onDateChange(${value}, ${ref.stateSetter},  ${ref.stateName})`,
                })
              : `newDate => onDateChange(newDate, ${ref.stateSetter},  ${ref.stateName})`
          }}
          value={${props.itemProps?.isListItem ? getListItemValue({ itemProps: props.itemProps, name: props.name, callback: (value: string) => `parseDate(${value})` }) : `parseDate(${ref.stateName})`}}
        />
        <TimePicker
          id={'time-picker-${props.id}'}
          isDisabled={${props.disabled || false}}
          name={${props.itemProps?.isListItem ? getListItemName({ itemProps: props.itemProps, name: props.name }) : `'${props.name}'`}}
          onChange={${
            props.itemProps?.isListItem
              ? getListItemOnChange({
                  itemProps: props.itemProps,
                  name: props.name,
                  callback: (_) => `onTimeChange(time, ${ref.stateSetter}, ${ref.stateName}, hours, minutes)`,
                  overrideParam: "(time, hours?, minutes?)",
                })
              : `(time, hours?, minutes?) => onTimeChange(time, ${ref.stateSetter}, ${ref.stateName}, hours, minutes)`
          }}
          style={{ width: '120px' }}
          time={${props.itemProps?.isListItem ? getListItemValue({ itemProps: props.itemProps, name: props.name, callback: (value: string) => `parseTime(${value})` }) : `parseTime(${ref.stateName})`}}
        />
      </InputGroup>
    </FlexItem>
  </Flex>`;

  const element: FormInput = buildDefaultInputElement({
    pfImports,
    inputJsxCode: jsxCode,
    ref,
    requiredCode: [DATE_FUNCTIONS, TIME_FUNCTIONS],
    wrapper: {
      id: props.id,
      label: props.label,
      required: props.required,
    },
    disabled: props.disabled,
    itemProps: props.itemProps,
  });

  useAddFormElementToContext(element);

  return renderField(element);
};

export default connectField(Date);
