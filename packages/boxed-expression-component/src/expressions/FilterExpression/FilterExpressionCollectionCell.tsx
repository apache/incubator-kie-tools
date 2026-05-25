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
import { useCallback } from "react";
import { BeeTableCellProps, BoxedFilter, Normalized } from "../../api";
import {
  NestedExpressionDispatchContextProvider,
  useBoxedExpressionEditorDispatch,
} from "../../BoxedExpressionEditorContext";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";
import { ROWTYPE } from "./FilterExpressionComponent";
import "./FilterExpression.css";

export function FilterExpressionCollectionCell({
  rowIndex,
  data: items,
  columnIndex,
  parentElementId,
}: BeeTableCellProps<ROWTYPE> & { parentElementId: string }) {
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onSetExpression = useCallback(
    ({ getNewExpression, expressionChangedArgs }) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedFilter>) => {
          return {
            ...prev,
            in: {
              ...prev.in,
              expression: getNewExpression(prev.in.expression),
            },
          };
        },
        expressionChangedArgs,
      });
    },
    [setExpression]
  );

  return (
    <div className="filter-expression" data-testid={"kie-tools--bee--filter-collection-in"}>
      <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
        <ExpressionContainer
          expression={items[rowIndex]?.expression}
          isResetSupported={true}
          isNested={true}
          rowIndex={rowIndex}
          columnIndex={columnIndex}
          parentElementId={parentElementId}
          parentElementTypeRef={undefined}
        />
      </NestedExpressionDispatchContextProvider>
    </div>
  );
}
