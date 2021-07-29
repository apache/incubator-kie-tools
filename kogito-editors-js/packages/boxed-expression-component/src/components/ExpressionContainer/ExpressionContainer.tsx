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
import { useCallback, useRef, useState } from "react";
import "./ExpressionContainer.css";
import { ExpressionProps, LogicType } from "../../api";
import { LogicTypeSelector } from "../LogicTypeSelector";

export interface ExpressionContainerProps {
  /** Expression properties */
  selectedExpression: ExpressionProps;
}

export const ExpressionContainer: ({ selectedExpression }: ExpressionContainerProps) => JSX.Element = (
  props: ExpressionContainerProps
) => {
  const expressionContainerRef = useRef<HTMLDivElement>(null);

  const [selectedExpression, setSelectedExpression] = useState(props.selectedExpression);

  const updateExpressionNameAndDataType = useCallback((updatedName, updatedDataType) => {
    setSelectedExpression((previousSelectedExpression: ExpressionProps) => ({
      ...previousSelectedExpression,
      name: updatedName,
      dataType: updatedDataType,
    }));
  }, []);

  const onLogicTypeUpdating = useCallback((logicType) => {
    setSelectedExpression((previousSelectedExpression: ExpressionProps) => ({
      ...previousSelectedExpression,
      logicType: logicType,
    }));
  }, []);

  const onLogicTypeResetting = useCallback(() => {
    setSelectedExpression((previousSelectedExpression: ExpressionProps) => {
      const updatedExpression = {
        uid: previousSelectedExpression.uid,
        name: previousSelectedExpression.name,
        dataType: previousSelectedExpression.dataType,
        logicType: LogicType.Undefined,
      };
      window.beeApi?.resetExpressionDefinition?.(updatedExpression);
      return updatedExpression;
    });
  }, []);

  const getLogicTypeSelectorRef = useCallback(() => {
    return expressionContainerRef.current!;
  }, []);

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
          getPlacementRef={getLogicTypeSelectorRef}
        />
      </div>
    </div>
  );
};
