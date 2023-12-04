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
import { SelectDirection, SelectProps } from "@patternfly/react-core/dist/js/components/Select";
import { connectField, FieldProps } from "uniforms";
import SelectCheckboxField from "./SelectCheckboxField";
import SelectInputsField from "./SelectInputsField";
import { CheckboxProps } from "@patternfly/react-core/dist/js/components/Checkbox";
import { RadioProps } from "@patternfly/react-core/dist/js/components/Radio";

export type SelectCheckboxProps = FieldProps<
  string | string[],
  CheckboxProps | RadioProps,
  {
    onChange: (value?: string | string[]) => void;
    transform?: (value?: string) => string;
    allowedValues: string[];
    id?: string;
    fieldType?: typeof Array;
    disabled?: boolean;
  }
>;

export type SelectInputProps = FieldProps<
  string | string[],
  SelectProps,
  {
    checkboxes?: boolean;
    required?: boolean;
    fieldType?: typeof Array;
    onChange: (value?: string | string[] | number | number[]) => void;
    placeholder?: string;
    allowedValues?: (string | number)[];
    disabled?: boolean;
    error?: boolean;
    transform?: (value?: string | number) => string | number;
    direction?: SelectDirection;
    menuAppendTo?: HTMLElement;
  }
>;

type SelectFieldProps = SelectCheckboxProps | SelectInputProps;

function isSelectCheckboxProps(toBeDetermined: SelectFieldProps): toBeDetermined is SelectCheckboxProps {
  return (toBeDetermined as SelectInputProps).checkboxes === true;
}

function SelectField(props: SelectFieldProps) {
  if (isSelectCheckboxProps(props)) {
    return <SelectCheckboxField {...props} />;
  }
  return <SelectInputsField {...props} />;
}

export default connectField<SelectFieldProps>(SelectField);
