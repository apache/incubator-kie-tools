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

import { FormGroup, FormGroupProps } from "@patternfly/react-core/dist/js/components/Form";
import * as React from "react";
import { FilterDOMPropsKeys, filterDOMProps } from "uniforms";
import FielDetailsPopover from "./FieldDetailsPopover";

declare module "uniforms" {
  interface FilterDOMProps {
    decimal: never;
    minCount: never;
    autoValue: never;
    isDisabled: never;
    checkboxes: never;
    exclusiveMaximum: never;
    exclusiveMinimum: never;
    menuAppendTo: never;
  }
}

filterDOMProps.register(
  "decimal",
  "minCount",
  "autoValue",
  "isDisabled",
  "exclusiveMaximum",
  "exclusiveMinimum",
  "menuAppendTo",
  "checkboxes",
  "helperText" as FilterDOMPropsKeys,
  "initialCount" as FilterDOMPropsKeys,
  "deprecated" as FilterDOMPropsKeys,
  "transform" as FilterDOMPropsKeys,
  "placeholder" as FilterDOMPropsKeys
);

export type WrapFieldProps = {
  id: string;
  error?: any;
  errorMessage?: string;
  help?: string;
  showInlineError?: boolean;
  description?: React.ReactNode;
  deprecated?: boolean;
  field?: unknown;
} & Omit<FormGroupProps, "onChange" | "fieldId">;

export default function wrapField(
  {
    id,
    label,
    type,
    disabled,
    error,
    errorMessage,
    showInlineError,
    help,
    required,
    description,
    deprecated,
    ...props
  }: WrapFieldProps,
  children: React.ReactNode
) {
  let defaultValue;
  if (typeof props.field === "object" && props.field !== null && "default" in props.field) {
    defaultValue = props.field.default;
  }
  return (
    <FormGroup
      data-testid="wrapper-field"
      data-fieldname={props.name}
      fieldId={id}
      label={label}
      isRequired={required}
      type={type}
      labelIcon={<FielDetailsPopover default={defaultValue} description={description} deprecated={deprecated} />}
      {...filterDOMProps(props)}
    >
      {children}
    </FormGroup>
  );
}
