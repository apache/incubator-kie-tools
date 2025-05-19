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
import { FormGroup, FormGroupProps, FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { HelpIcon } from "@patternfly/react-icons/dist/js/icons/help-icon";
import { filterDOMProps } from "uniforms";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";

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
  description?: React.ReactNode;
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
    ...props
  }: WrapperProps,
  children: React.ReactNode
) {
  return (
    <FormGroup
      data-testid={"wrapper-field"}
      fieldId={id}
      label={label}
      labelIcon={
        description ? (
          <Popover bodyContent={description}>
            <button
              type="button"
              aria-label="field description"
              onClick={(e) => e.preventDefault()}
              className="pf-v5-c-form__group-label-help"
            >
              <Icon isInline>
                <HelpIcon />
              </Icon>
            </button>
          </Popover>
        ) : undefined
      }
      isRequired={required}
      type={type}
      {...filterDOMProps(props)}
    >
      {children}
      {error === true ? (
        <FormHelperText>
          <HelperText>
            <HelperTextItem variant="error">{errorMessage}</HelperTextItem>
          </HelperText>
        </FormHelperText>
      ) : (
        <FormHelperText>
          <HelperText>
            <HelperTextItem variant="default">{help}</HelperTextItem>
          </HelperText>
        </FormHelperText>
      )}
    </FormGroup>
  );
}
