/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useCallback, useRef } from "react";
import "./ExpressionDefinitionRoot.css";
import {
  ContextExpressionDefinition,
  DmnBuiltInDataType,
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  FunctionExpressionDefinition,
  FunctionExpressionDefinitionKind,
  generateUuid,
} from "../../api";
import { ExpressionDefinitionLogicTypeSelector } from "../ExpressionDefinitionLogicTypeSelector";
import { useBoxedExpressionEditorDispatch } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";

export interface ExpressionDefinitionRootProps {
  decisionNodeId: string;
  expression: ExpressionDefinition;
}

export function getDefaultExpressionDefinitionByLogicType(
  logicType: ExpressionDefinitionLogicType,
  prev: ExpressionDefinition
) {
  if (logicType === ExpressionDefinitionLogicType.Function) {
    const functionExpression: FunctionExpressionDefinition = {
      ...prev,
      logicType,
      functionKind: FunctionExpressionDefinitionKind.Feel,
      formalParameters: [],
      expression: {
        logicType: ExpressionDefinitionLogicType.LiteralExpression,
        isHeadless: true,
      },
    };
    return functionExpression;
  } else if (logicType === ExpressionDefinitionLogicType.Context) {
    const contextExpression: ContextExpressionDefinition = {
      ...prev,
      logicType,
      contextEntries: [
        {
          entryInfo: {
            id: generateUuid(),
            name: "ContextEntry-1",
            dataType: DmnBuiltInDataType.Undefined,
          },
          entryExpression: {
            name: "ContextEntry-1",
            dataType: DmnBuiltInDataType.Undefined,
            logicType: ExpressionDefinitionLogicType.Undefined,
          },
          nameAndDataTypeSynchronized: true,
        },
      ],
    };
    return contextExpression;
  } else {
    return prev;
  }
}

export function ExpressionDefinitionRoot({ decisionNodeId, expression }: ExpressionDefinitionRootProps) {
  const expressionContainerRef = useRef<HTMLDivElement>(null);

  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onLogicTypeSelected = useCallback(
    (logicType) => {
      return setExpression((prev) => {
        {
          return {
            ...getDefaultExpressionDefinitionByLogicType(logicType, prev),
            logicType,
            isHeadless: false,
            id: prev.id ?? generateUuid(),
          };
        }
      });
    },
    [setExpression]
  );

  const onLogicTypeReset = useCallback(() => {
    setExpression((prev) => ({
      id: prev.id,
      name: prev.name,
      dataType: prev.dataType,
      logicType: ExpressionDefinitionLogicType.Undefined,
    }));
  }, [setExpression]);

  const getLogicTypeSelectorRef = useCallback(() => expressionContainerRef.current!, []);

  return (
    <div className="expression-container">
      <div className="expression-name-and-logic-type">
        <span className="expression-title">{expression.name}</span>
        <span className="expression-type">({expression.logicType || ExpressionDefinitionLogicType.Undefined})</span>
      </div>

      <div
        className={`expression-container-box ${decisionNodeId}`}
        ref={expressionContainerRef}
        data-ouia-component-id="expression-container"
      >
        <ExpressionDefinitionLogicTypeSelector
          expression={expression}
          onLogicTypeSelected={onLogicTypeSelected}
          onLogicTypeReset={onLogicTypeReset}
          getPlacementRef={getLogicTypeSelectorRef}
        />
      </div>
    </div>
  );
}
