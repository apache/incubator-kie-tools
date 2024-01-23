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
import { ExpressionPath } from "../../boxedExpressions/getBeeMap";
import { DescriptionField, LabelField } from "./Fields";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { LiteralExpressionContentCell } from "./LiteralExpressionContentCell";
import { UnaryTestCell } from "./UnaryTestCell";

/**
 * Pick<DMN15__tOutputClause, "@_label" | "@_name" | "@_typeRef" | "description"> & {
    outputValues: Pick<
      DMN15__tUnaryTests,
      "@_expressionLanguage" | "@_kie:constraintType" | "@_label" | "description" | "text"
    >;
    defaultOutputEntry: Pick<DMN15__tLiteralExpression, "@_expressionLanguage" | "@_label" | "description" | "text">;
  };
 */
export function DecisionTableOutputHeaderCell(props: {
  label: string;
  description: string;
  isReadonly: boolean;
  expressionPath: ExpressionPath[];
  onChangeLabel: (newLabel: string) => void;
  onChangeDescription: (newDescription: string) => void;
  defaultOutputEntry: {
    text: string;
    expressionLanguage: string;
    label: string;
    description: string;
    onChangeText: (newText: string) => void;
    onChangeExpressionLanguage: (newExpressionLanguage: string) => void;
    onChangeLabel: (newLabel: string) => void;
    onChangeDescription: (newDescription: string) => void;
  };
  outputValues: {
    text: string;
    expressionLanguage: string;
    label: string;
    description: string;
    onChangeText: (newText: string) => void;
    onChangeExpressionLanguage: (newExpressionLanguage: string) => void;
    onChangeLabel: (newLabel: string) => void;
    onChangeDescription: (newDescription: string) => void;
  };
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
      <FormGroup label={"Default Output Entry"}>
        <LiteralExpressionContentCell
          isReadonly={props.isReadonly}
          expressionPath={props.expressionPath}
          description={props.defaultOutputEntry.description}
          onChangeDescription={props.defaultOutputEntry.onChangeDescription}
          label={props.defaultOutputEntry.label}
          onChangeLabel={props.defaultOutputEntry.onChangeLabel}
          expressionLanguage={props.defaultOutputEntry.expressionLanguage}
          onChangeExpressionLanguage={props.defaultOutputEntry.onChangeExpressionLanguage}
          text={props.defaultOutputEntry.text}
          onChangeText={props.defaultOutputEntry.onChangeText}
        />
      </FormGroup>
      <FormGroup label={"Output Values"}>
        <UnaryTestCell
          isReadonly={props.isReadonly}
          expressionPath={props.expressionPath}
          description={props.outputValues.description}
          onChangeDescription={props.outputValues.onChangeDescription}
          label={props.outputValues.label}
          onChangeLabel={props.outputValues.onChangeLabel}
          expressionLanguage={props.outputValues.expressionLanguage}
          onChangeExpressionLanguage={props.outputValues.onChangeExpressionLanguage}
          text={props.outputValues.text}
          onChangeText={props.outputValues.onChangeText}
        />
      </FormGroup>
    </>
  );
}
