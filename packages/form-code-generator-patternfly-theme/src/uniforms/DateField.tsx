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
import { DATE_FUNCTIONS } from "./staticCode/staticCodeBlocks";
import { DEFAULT_DATA_TYPE_DATE } from "./utils/dataTypes";
import {
  getItemNameAndWithIsNested,
  getListItemName,
  getListItemValue,
  ListItemProps,
} from "./rendering/ListItemField";

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

  function getOnDateChange() {
    if (props.itemProps?.isListItem) {
      const { itemName, isNested } = getItemNameAndWithIsNested(props.name);
      const propertyPath = props.itemProps?.listStateName.split(".").splice(1).join(".");
      const path = `${propertyPath}[${props.itemProps?.indexVariableName}]${isNested ? `.${itemName}` : ""}`;
      return `newDate => {
        ${props.itemProps?.listStateSetter}(prev => {
          if (newDate) {
            const newState = [...prev];
            const currentDate = newState${path}
            const newDate = new Date(newDate);
            const time = parseTime(currentDate);
            if (time !== '') {
                newDate.setHours(parseInt(time && time.split(':')[0]));
                newDate.setMinutes(parseInt(time && time.split(':')[1].split(' ')[0]));
            }
            newState${path} = newDate.toISOString();
            return newState;
          }
          return prev;
        })
      }`;
    }
    return `newDate => {
        ${ref.stateSetter}(prev => {
          if (newDate) {
            const newDate = new Date(newDate);
            const time = parseTime(prev);
            if (time !== '') {
                newDate.setHours(parseInt(time && time.split(':')[0]));
                newDate.setMinutes(parseInt(time && time.split(':')[1].split(' ')[0]));
            }
            return newDate.toISOString();
          }
          return prev;
        })
      }
      `;
  }

  function getOnTimeChange() {
    if (props.itemProps?.isListItem) {
      const { itemName, isNested } = getItemNameAndWithIsNested(props.name);
      const propertyPath = props.itemProps?.listStateName.split(".").splice(1).join(".");
      const path = `${propertyPath}[${props.itemProps?.indexVariableName}]${isNested ? `.${itemName}` : ""}`;
      return `(time, hours?, minutes?) => ${props.itemProps?.listStateSetter}(prev => {
        const newState = [...prev];
        const currentDate = newState${path}
        if (currentDate) {
          const newDate = new Date(Date.parse(currentDate));
          if (hours && minutes) {
            newDate.setHours(hours);
            newDate.setMinutes(minutes);
          } else if (time !== '') {
            const localeHours = parseInt(time && time.split(':')[0]);
            const localeMinutes = parseInt(time && time.split(':')[1].split(' ')[0]);
            if (!isNaN(localeHours) && !isNaN(localeMinutes)) {
                newDate.setHours(localeHours);
                newDate.setMinutes(localeMinutes);
            }
          }
          newState${path} = newDate.toISOString();
          return newState;
        }
        return prev;
      })`;
    }
    return `(time, hours?, minutes?) => {
      ${ref.stateSetter}(prev => {
        if (prev) {
          const newDate = new Date(Date.parse(prev));
          if (hours && minutes) {
            newDate.setHours(hours);
            newDate.setMinutes(minutes);
          } else if (time !== '') {
            const localeHours = parseInt(time && time.split(':')[0]);
            const localeMinutes = parseInt(time && time.split(':')[1].split(' ')[0]);
            if (!isNaN(localeHours) && !isNaN(localeMinutes)) {
                newDate.setHours(localeHours);
                newDate.setMinutes(localeMinutes);
            }
          }
          return newDate.toISOString();
        }
        return prev;
      })
    }
    `;
  }

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
          onChange={${getOnDateChange()}}
          value={${props.itemProps?.isListItem ? getListItemValue({ itemProps: props.itemProps, name: props.name, callback: (value: string) => `parseDate(${value})` }) : `parseDate(${ref.stateName})`}}
        />
        <TimePicker
          id={'time-picker-${props.id}'}
          isDisabled={${props.disabled || false}}
          name={${props.itemProps?.isListItem ? getListItemName({ itemProps: props.itemProps, name: props.name }) : `'${props.name}'`}}
          onChange={${getOnTimeChange()}}
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
    requiredCode: [DATE_FUNCTIONS],
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
