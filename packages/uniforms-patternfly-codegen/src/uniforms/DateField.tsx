/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { connectField, HTMLFieldProps } from "uniforms/cjs";

import { FormInput, InputReference } from "../api";
import { buildDefaultInputElement, getInputReference, renderField } from "./utils/Utils";
import { useAddFormElementToContext } from "./CodeGenContext";
import { DATE_FUNCTIONS, TIME_FUNCTIONS } from "./staticCode/staticCodeBlocks";
import { DATE } from "./utils/dataTypes";

export type DateFieldProps = HTMLFieldProps<
  Date,
  HTMLDivElement,
  {
    name: string;
    label: string;
    required: boolean;
    max?: Date;
    min?: Date;
  }
>;

const Date: React.FC<DateFieldProps> = (props: DateFieldProps) => {
  const ref: InputReference = getInputReference(props.name, DATE);

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
          name={'${props.name}'}
          onChange={newDate => onDateChange(newDate, ${ref.stateSetter},  ${ref.stateName})}
          value={parseDate(${ref.stateName})}
        />
        <TimePicker
          id={'time-picker-${props.id}'}
          isDisabled={${props.disabled || false}}
          name={'${props.name}'}
          onChange={(time, hours?, minutes?) => onTimeChange(time, ${ref.stateSetter}, ${
    ref.stateName
  }, hours, minutes)}
          style={{ width: '120px' }}
          time={parseTime(${ref.stateName})}
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
  });

  useAddFormElementToContext(element);

  return renderField(element);
};

export default connectField(Date);
