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

import { BeeTableCellProps, BoxedFilter, Normalized } from "../../api";
import {
  NestedExpressionDispatchContextProvider,
  OnSetExpression,
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../../BoxedExpressionEditorContext";
import React, { useCallback, useEffect, useMemo } from "react";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";
import { ROWTYPE } from "./FilterExpressionComponent";
import "./FilterExpression.css";
import {
  NestedExpressionContainerContext,
  NestedExpressionContainerContextType,
  useNestedExpressionContainer,
} from "../../resizing/NestedExpressionContainerContext";
import { FILTER_EXPRESSION_MATCH_ROW_EXTRA_WIDTH } from "../../resizing/WidthConstants";
import { useBeeTableSelectableCellRef } from "../../selection/BeeTableSelectionContext";

export function FilterExpressionMatchCell({
  rowIndex,
  data: items,
  columnIndex,
  parentElementId,
}: BeeTableCellProps<ROWTYPE> & {
  parentElementId: string;
}) {
  const { setExpression } = useBoxedExpressionEditorDispatch();
  const { expression } = items[0];
  const { isActive } = useBeeTableSelectableCellRef(rowIndex, columnIndex, undefined);
  const { beeGwtService } = useBoxedExpressionEditor();

  useEffect(() => {
    if (isActive) {
      beeGwtService?.selectObject(expression?.["@_id"]);
    }
  }, [beeGwtService, expression, isActive]);

  const onSetExpression = useCallback<OnSetExpression>(
    ({ getNewExpression, expressionChangedArgs }) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedFilter>) => {
          const newExpression = getNewExpression(prev.match.expression);

          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedFilter> = {
            ...prev,
            match: {
              ...prev.match,
              expression: newExpression!, // SPEC DISCREPANCY
            },
          };

          return ret;
        },
        expressionChangedArgs,
      });
    },
    [setExpression]
  );

  const nestedExpressionContainer = useNestedExpressionContainer();

  const nestedExpressionContainerValue = useMemo<NestedExpressionContainerContextType>(() => {
    return {
      minWidth: nestedExpressionContainer.minWidth - FILTER_EXPRESSION_MATCH_ROW_EXTRA_WIDTH,
      actualWidth: nestedExpressionContainer.actualWidth - FILTER_EXPRESSION_MATCH_ROW_EXTRA_WIDTH,
      resizingWidth: {
        value: nestedExpressionContainer.resizingWidth.value - FILTER_EXPRESSION_MATCH_ROW_EXTRA_WIDTH,
        isPivoting: nestedExpressionContainer.resizingWidth.isPivoting,
      },
    };
  }, [
    nestedExpressionContainer.actualWidth,
    nestedExpressionContainer.minWidth,
    nestedExpressionContainer.resizingWidth.isPivoting,
    nestedExpressionContainer.resizingWidth.value,
  ]);

  return (
    <div className={"filter-expression-cell"}>
      <div
        className={"bracket-sign-container"}
        style={{ borderRight: "1px solid var(--pf-v5-global--palette--black-300)" }}
      >
        <div className={"bracket-sign"}>[</div>
      </div>
      <div data-testid={"kie-tools--bee--filter-collection-match"}>
        <NestedExpressionContainerContext.Provider value={nestedExpressionContainerValue}>
          <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
            <ExpressionContainer
              expression={items[0].expression}
              isResetSupported={true}
              isNested={true}
              rowIndex={rowIndex}
              columnIndex={columnIndex}
              parentElementId={parentElementId}
              parentElementTypeRef={undefined}
              parentElementName={undefined}
            />
          </NestedExpressionDispatchContextProvider>
        </NestedExpressionContainerContext.Provider>
      </div>
      <div
        className={"bracket-sign-container"}
        style={{ borderLeft: "1px solid var(--pf-v5-global--palette--black-300)" }}
      >
        <div className={"bracket-sign"}>]</div>
      </div>
    </div>
  );
}
