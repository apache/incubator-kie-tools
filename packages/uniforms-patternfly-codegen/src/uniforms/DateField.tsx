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

import React from "react";
import { connectField } from "uniforms";
import { TextInputProps } from "@patternfly/react-core";

import { FormInput, InputReference } from "../api";
import { buildDefaultInputElement, getInputReference, renderField } from "./utils/Utils";
import { useCodegenContext } from "./CodeGenContext";
import { DATE_FUNCTIONS, TIME_FUNCTIONS } from "./staticCode/staticCodeBlocks";

export type DateFieldProps = {
  id: string;
  label: string;
  value?: string;
  onChange: (value?: string) => void;
  disabled: boolean;
  field?: { format: string };
} & Omit<TextInputProps, "isDisabled">;

const Date: React.FC<DateFieldProps> = (props: DateFieldProps) => {
  const codegenContext = useCodegenContext();
  const ref: InputReference = getInputReference(props.name);

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
          defaultTime={parseTime(${ref.stateName})}
        />
      </InputGroup>
    </FlexItem>
  </Flex>`;

  const element: FormInput = buildDefaultInputElement({
    pfImports,
    inputJsxCode: jsxCode,
    ref,
    dataType: "Date",
    requiredCode: [DATE_FUNCTIONS, TIME_FUNCTIONS],
    wrapper: {
      id: props.id,
      label: props.label,
      required: props.required,
    },
  });

  codegenContext.rendered.push(element);

  return renderField(element);
};

export default connectField(Date);
