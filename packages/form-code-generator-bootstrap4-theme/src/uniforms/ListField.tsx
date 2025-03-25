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

import React, { useContext } from "react";
import { connectField, context, HTMLFieldProps } from "uniforms/cjs";
import { FormInputContainer } from "../api";
import { renderListItemFragmentWithContext } from "./rendering/RenderingUtils";
import { useBootstrapCodegenContext } from "./BootstrapCodeGenContext";
import { LIST, renderCodeGenElement } from "./templates/templates";
import { ListItemProps } from "./rendering/ListFieldInput";
import { getNextIndexVariableName } from "./templates/ListFieldTemplate";

export type ListFieldProps = HTMLFieldProps<
  unknown[],
  HTMLDivElement,
  {
    itemProps: ListItemProps;
    maxCount?: number;
    minCount?: number;
  }
>;

const List: React.FunctionComponent<ListFieldProps> = ({
  id,
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
}: ListFieldProps) => {
  const uniformsContext = useContext(context);
  const codegenCtx = useBootstrapCodegenContext();

  const element: FormInputContainer = renderCodeGenElement(LIST, {
    id: name,
    name: name,
    label: label,
    disabled: disabled,
    itemProps: itemProps,
    children: renderListItemFragmentWithContext(
      uniformsContext,
      "$",
      {
        isListItem: true,
        indexVariableName: getNextIndexVariableName(itemProps),
        listName: name,
      },
      disabled
    ),
  });

  codegenCtx?.rendered.push(element);
  return <>{JSON.stringify(element)}</>;
};

export default connectField(List);
