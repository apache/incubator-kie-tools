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
import { DescriptionField, ExpressionLanguageField, KieConstraintTypeField, LabelField, TextField } from "./Fields";
import { ExpressionPath } from "../../boxedExpressions/getBeeMap";

/**
 * Pick<
    DMN15__tUnaryTests,
    "@_expressionLanguage" | "@_kie:constraintType" | "@_label" | "description" | "text"
  >;
 * This component implements a form to change an object with the DMN15__tUnaryTests type
 * It's used for: DecisionTableInputRuleCell
 */
export function UnaryTestCell(props: {
  text: string;
  expressionLanguage: string;
  label: string;
  description: string;
  isReadonly: boolean;
  expressionPath: ExpressionPath[];
  onChangeText: (newText: string) => void;
  onChangeExpressionLanguage: (newExpressionLanguage: string) => void;
  onChangeLabel: (newLabel: string) => void;
  onChangeDescription: (newDescription: string) => void;
}) {
  return (
    <>
      <ExpressionLanguageField
        isReadonly={props.isReadonly}
        expressionLanguage={props.expressionLanguage}
        onChange={props.onChangeExpressionLanguage}
      />
      <TextField
        isReadonly={props.isReadonly}
        initialValue={props.text}
        expressionPath={props.expressionPath}
        onChange={props.onChangeText}
      />
      <LabelField isReadonly={props.isReadonly} label={props.label} onChange={props.onChangeLabel} />
      <DescriptionField
        isReadonly={props.isReadonly}
        initialValue={props.description}
        expressionPath={props.expressionPath}
        onChange={props.onChangeDescription}
      />
      <KieConstraintTypeField />
    </>
  );
}
