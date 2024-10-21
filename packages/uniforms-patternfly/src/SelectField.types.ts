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

import { SelectDirection, SelectProps } from "@patternfly/react-core/deprecated";
import { CheckboxProps } from "@patternfly/react-core/dist/js/components/Checkbox";
import { RadioProps } from "@patternfly/react-core/dist/js/components/Radio";
import { FieldProps } from "uniforms";

export type SelectCheckboxProps = FieldProps<
  string | string[],
  CheckboxProps | RadioProps,
  {
    onChange: (value?: string | string[]) => void;
    transform?: TransformFn;
    options: string[];
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
    options?: Array<string | number | SelectOptionObject>;
    disabled?: boolean;
    error?: boolean;
    transform?: TransformFn;
    direction?: SelectDirection;
    menuAppendTo?: HTMLElement;
  }
>;

export type SelectOptionObject = { value: string | number };

export type TransformFn = (value?: string | number | SelectOptionObject) => { label: string | number };

export function isSelectOptionObject(
  toBeDetermined: string | number | { value: string | number }
): toBeDetermined is SelectOptionObject {
  return typeof toBeDetermined === "object" && !Array.isArray(toBeDetermined) && toBeDetermined !== null;
}
