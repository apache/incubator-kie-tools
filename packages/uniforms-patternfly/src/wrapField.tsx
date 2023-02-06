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
import { FormGroup, FormGroupProps } from "@patternfly/react-core/dist/js/components/Form";
import { filterDOMProps } from "uniforms";

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
  "menuAppendTo"
);

type WrapperProps = {
  id: string;
  error?: boolean;
  errorMessage?: string;
  help?: string;
  showInlineError?: boolean;
} & Omit<FormGroupProps, "onChange" | "fieldId">;

export default function wrapField(
  { id, label, type, disabled, error, errorMessage, showInlineError, help, required, ...props }: WrapperProps,
  children: React.ReactNode
) {
  return (
    <FormGroup
      data-testid={"wrapper-field"}
      fieldId={id}
      label={label}
      isRequired={required}
      validated={error ? "error" : "default"}
      type={type}
      helperText={help}
      helperTextInvalid={errorMessage}
      {...filterDOMProps(props)}
    >
      {children}
    </FormGroup>
  );
}
