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
import { useCallback, useEffect, useRef } from "react";
import { useBoxedExpressionEditor, useBoxedExpressionEditorDispatch } from "../../BoxedExpressionEditorContext";
import { BoxedContext, BoxedExpression, DmnBuiltInDataType, generateUuid } from "../../api";
import { findAllIdsDeep } from "../../ids/ids";
import { DEFAULT_EXPRESSION_VARIABLE_NAME } from "../../expressionVariable/ExpressionVariableMenu";
import { useBeeTableSelectableCellRef } from "../../selection/BeeTableSelectionContext";
import { ExpressionDefinitionLogicTypeSelector } from "./ExpressionDefinitionLogicTypeSelector";

export interface ExpressionContainerProps {
  expression?: BoxedExpression;
  isNested: boolean;
  isResetSupported: boolean;
  rowIndex: number;
  columnIndex: number;
  parentElementId: string | undefined;
  parentElementTypeRef: string | undefined;
  parentElementName?: string;
}

export const ExpressionContainer: React.FunctionComponent<ExpressionContainerProps> = ({
  expression,
  isNested,
  isResetSupported,
  rowIndex,
  columnIndex,
  parentElementId,
  parentElementTypeRef,
  parentElementName,
}) => {
  const containerRef = useRef<HTMLDivElement>(null);

  const { beeGwtService, expressionHolderId, hideDmn14BoxedExpressions } = useBoxedExpressionEditor();
  const { setExpression, setWidthsById } = useBoxedExpressionEditorDispatch();
  const { isActive } = useBeeTableSelectableCellRef(rowIndex, columnIndex, undefined);

  useEffect(() => {
    if (isActive) {
      beeGwtService?.selectObject("");
    }
  }, [beeGwtService, isActive]);

  const expressionTypeRef = expression?.["@_typeRef"];

  const onLogicTypeSelected = useCallback(
    (logicType: BoxedExpression["__$$element"] | undefined) => {
      const { expression: defaultExpression, widthsById: defaultWidthsById } =
        beeGwtService!.getDefaultExpressionDefinition(
          logicType,
          parentElementTypeRef ?? expressionTypeRef ?? DmnBuiltInDataType.Undefined,
          !isNested
        );

      setExpression((prev: BoxedExpression) => {
        // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
        const ret: BoxedExpression = {
          ...defaultExpression,
          "@_id": defaultExpression["@_id"] ?? generateUuid(),
          "@_label": defaultExpression["@_label"] ?? parentElementName ?? DEFAULT_EXPRESSION_VARIABLE_NAME,
        };

        return ret;
      });

      setWidthsById(({ newMap }) => {
        defaultWidthsById.forEach((value, id) => {
          newMap.set(id, value);
        });
      });
    },
    [beeGwtService, expressionTypeRef, parentElementName, isNested, parentElementTypeRef, setExpression, setWidthsById]
  );

  const onLogicTypeReset = useCallback(() => {
    setWidthsById(({ newMap }) => {
      for (const id of findAllIdsDeep(expression)) {
        newMap.delete(id);
      }
    });

    setExpression(undefined!); // SPEC DISCREPANCY: Undefined expressions gives users the ability to select the expression type.
  }, [expression, setExpression, setWidthsById]);

  const getPlacementRef = useCallback(() => containerRef.current!, []);

  return (
    <div ref={containerRef} className={"expression-container-box"} data-testid="expression-container">
      <ExpressionDefinitionLogicTypeSelector
        expression={expression}
        onLogicTypeSelected={onLogicTypeSelected}
        onLogicTypeReset={onLogicTypeReset}
        getPlacementRef={getPlacementRef}
        isResetSupported={isResetSupported}
        isNested={isNested}
        parentElementId={parentElementId ?? expressionHolderId}
        hideDmn14BoxedExpressions={hideDmn14BoxedExpressions}
      />
    </div>
  );
};
