/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { ExpressionDefinition, ExpressionDefinitionLogicType, generateUuid } from "../../api";
import * as React from "react";
import { useCallback, useEffect, useRef } from "react";
import { ExpressionDefinitionLogicTypeSelector } from "./ExpressionDefinitionLogicTypeSelector";
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { DEFAULT_EXPRESSION_NAME } from "../ExpressionDefinitionHeaderMenu";
import { useBeeTableSelectableCellRef } from "../../selection/BeeTableSelectionContext";

export interface ExpressionContainerProps {
  expression: ExpressionDefinition;
  isNested: boolean;
  isResetSupported: boolean;
  rowIndex: number;
  columnIndex: number;
}

export const ExpressionContainer: React.FunctionComponent<ExpressionContainerProps> = ({
  expression,
  isNested,
  isResetSupported,
  rowIndex,
  columnIndex,
}) => {
  const containerRef = useRef<HTMLDivElement>(null);

  const { beeGwtService } = useBoxedExpressionEditor();
  const { setExpression } = useBoxedExpressionEditorDispatch();
  const { isActive } = useBeeTableSelectableCellRef(rowIndex, columnIndex, undefined);

  useEffect(() => {
    if (isActive) {
      beeGwtService?.selectObject("");
    }
  }, [beeGwtService, isActive]);

  const onLogicTypeSelected = useCallback(
    (logicType) => {
      setExpression((prev) => ({
        ...beeGwtService!.getDefaultExpressionDefinition(logicType, prev.dataType),
        logicType,
        isNested,
        id: prev.id ?? generateUuid(),
        name: prev.name ?? DEFAULT_EXPRESSION_NAME,
      }));
    },
    [beeGwtService, isNested, setExpression]
  );

  const onLogicTypeReset = useCallback(() => {
    setExpression((prev) => ({
      id: prev.id,
      name: prev.name,
      dataType: prev.dataType,
      logicType: ExpressionDefinitionLogicType.Undefined,
    }));
  }, [setExpression]);

  const getPlacementRef = useCallback(() => containerRef.current!, []);

  return (
    <div ref={containerRef} className={"expression-container-box"} data-ouia-component-id="expression-container">
      <ExpressionDefinitionLogicTypeSelector
        expression={expression}
        onLogicTypeSelected={onLogicTypeSelected}
        onLogicTypeReset={onLogicTypeReset}
        getPlacementRef={getPlacementRef}
        isResetSupported={isResetSupported}
        isNested={isNested}
      />
    </div>
  );
};
