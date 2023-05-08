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
import { ReactNode } from "react";
import { connectField, HTMLFieldProps } from "uniforms";
import { ListField } from "@kie-tools/uniforms-patternfly/dist/esm";
import UniformsListItemField from "./UniformsListItemField";

export type ListFieldProps = HTMLFieldProps<
  unknown[],
  HTMLDivElement,
  {
    children?: ReactNode;
    info?: string;
    error?: boolean;
    initialCount?: number;
    itemProps?: object;
    showInlineError?: boolean;
  }
>;

function UniformsListField({ children = <UniformsListItemField name={"$"} />, ...props }: ListFieldProps) {
  return <ListField {...props}>{children}</ListField>;
}

export default connectField(UniformsListField);
