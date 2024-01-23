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
import { DescriptionField, LabelField } from "./Fields";
import { ExpressionPath } from "../../boxedExpressions/getBeeMap";

/**
 * This component implements a form to change an object with the { "@_label"?: "string", "description": { "__$$text": string } } type
 * It's used for: ContextExpressionRoot, InvocationExpressionRootCell, RelationExpressionRootCell, ListExpressionCells, ForExpressionCells,
 * EveryExpressionCells, SomeExpressionCells, ConditionalExpressionCells, FilterExpressionCells
 */
export function ExpressionRootCell(props: {
  label: string;
  description: string;
  isReadonly: boolean;
  expressionPath: ExpressionPath[];
  onChangeLabel: (newLabel: string) => void;
  onChangeDescription: (newDescription: string) => void;
}) {
  return (
    <>
      <LabelField isReadonly={props.isReadonly} label={props.label} onChange={props.onChangeLabel} />
      <DescriptionField
        isReadonly={props.isReadonly}
        initialValue={props.description}
        expressionPath={props.expressionPath}
        onChange={props.onChangeDescription}
      />
    </>
  );
}
