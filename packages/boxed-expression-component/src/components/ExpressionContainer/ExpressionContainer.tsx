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
import "./ExpressionContainer.css";
import { ExpressionProps, LogicType } from "../../api";
import { LogicTypeSelector } from "../LogicTypeSelector";

export interface ExpressionContainerProps {
  /** Expression properties */
  selectedExpression: ExpressionProps;
  /** Callback triggered when expression gets changed */
  onExpressionChange?: (updatedExpression: ExpressionProps) => void;
}

export const ExpressionContainer: (props: ExpressionContainerProps) => JSX.Element = ({
  selectedExpression,
  onExpressionChange,
}: ExpressionContainerProps) => {
  const expressionContainerRef = useRef<HTMLDivElement>(null);

  const updateExpressionNameAndDataType = useCallback(
    (updatedName, updatedDataType) => {
      if (selectedExpression.name === updatedName && selectedExpression.dataType === updatedDataType) {
        return;
      }
      onExpressionChange?.({
        ...selectedExpression,
        name: updatedName,
        dataType: updatedDataType,
      });
    },
    [onExpressionChange, selectedExpression]
  );

  const onLogicTypeUpdating = useCallback(
    (logicType) => {
      onExpressionChange?.({
        ...selectedExpression,
        logicType: logicType,
      });
    },
    [onExpressionChange, selectedExpression]
  );

  const onLogicTypeResetting = useCallback(() => {
    const updatedExpression = {
      uid: selectedExpression.uid,
      name: selectedExpression.name,
      dataType: selectedExpression.dataType,
      logicType: LogicType.Undefined,
    };
    window.beeApi?.resetExpressionDefinition?.(updatedExpression);
    onExpressionChange?.(updatedExpression);
  }, [onExpressionChange, selectedExpression.dataType, selectedExpression.name, selectedExpression.uid]);

  return (
    <div className="expression-container">
      <div className="expression-name-and-logic-type">
        <span className="expression-title">{selectedExpression.name}</span>
        <span className="expression-type">({selectedExpression.logicType || LogicType.Undefined})</span>
      </div>

      <div
        className="expression-container-box"
        ref={expressionContainerRef}
        data-ouia-component-id="expression-container"
      >
        <LogicTypeSelector
          selectedExpression={selectedExpression}
          onLogicTypeUpdating={onLogicTypeUpdating}
          onLogicTypeResetting={onLogicTypeResetting}
          onUpdatingNameAndDataType={updateExpressionNameAndDataType}
          getPlacementRef={useCallback(() => expressionContainerRef.current!, [])}
        />
      </div>
    </div>
  );
};
