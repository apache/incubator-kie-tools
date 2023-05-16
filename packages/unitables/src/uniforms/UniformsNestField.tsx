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
import { ReactNode, useMemo } from "react";
import { HTMLFieldProps } from "uniforms";
import UniformsListItemField from "./UniformsListItemField";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { OutlinedQuestionCircleIcon } from "@patternfly/react-icons/dist/js/icons/outlined-question-circle-icon";
import { connectField, filterDOMProps, useField } from "uniforms/esm";
import wrapField from "@kie-tools/uniforms-patternfly/dist/esm/wrapField";
import { AutoField, ListAddField } from "@kie-tools/uniforms-patternfly/dist/esm";
import { Card, CardBody } from "@patternfly/react-core/dist/js/components/Card";

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
    <div style={{ display: "flex" }}>
      {children ||
        fields?.map((field) => (
          <div key={field} style={{ width: "100%", borderRight: "1px solid var(--pf-global--palette--black-300)" }}>
            <AutoField disabled={disabled} name={field} {...itemProps} />
          </div>
        ))}
    </div>
  );
}

export default connectField(NestField);
