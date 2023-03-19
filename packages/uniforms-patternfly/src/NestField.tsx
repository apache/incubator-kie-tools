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
import { connectField, filterDOMProps, HTMLFieldProps } from "uniforms";
import { Card, CardBody } from "@patternfly/react-core/dist/js/components/Card";
import AutoField from "./AutoField";

export type NestFieldProps = HTMLFieldProps<object, HTMLDivElement, { helperText?: string; itemProps?: object }>;

function NestField({
  children,
  error,
  errorMessage,
  fields,
  itemProps,
  label,
  name,
  showInlineError,
  disabled,
  ...props
}: NestFieldProps) {
  return (
    <Card data-testid={"nest-field"} {...filterDOMProps(props)}>
      <CardBody className="pf-c-form">
        {label && (
          <label>
            <b>{label}</b>
          </label>
        )}
        {children || fields?.map((field) => <AutoField key={field} disabled={disabled} name={field} {...itemProps} />)}
      </CardBody>
    </Card>
  );
}

export default connectField(NestField);
