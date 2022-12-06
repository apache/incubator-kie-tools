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
import {
  DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  generateUuid,
} from "../../api";
import { useBoxedExpressionEditorDispatch } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { getDefaultExpressionDefinitionByLogicType } from "../ContextExpression";
import { ExpressionDefinitionLogicTypeSelector } from "../ExpressionDefinitionLogicTypeSelector";
import "./ExpressionDefinitionRoot.css";

export interface ExpressionDefinitionRootProps {
  decisionNodeId: string;
  expression: ExpressionDefinition;
}

export function ExpressionDefinitionRoot({ decisionNodeId, expression }: ExpressionDefinitionRootProps) {
  const expressionContainerRef = useRef<HTMLDivElement>(null);

  const { setExpression } = useBoxedExpressionEditorDispatch();
  const expressionContainerWidth = DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH; // FIXME: Tiago -> What's the default for all?

  const onLogicTypeSelected = useCallback(
    (logicType) => {
      return setExpression((prev) => {
        {
          return {
            ...getDefaultExpressionDefinitionByLogicType(logicType, expressionContainerWidth, prev),
            logicType,
            isHeadless: false,
            id: prev.id ?? generateUuid(),
          };
        }
      });
    },
    [expressionContainerWidth, setExpression]
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
