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

import "../ContextExpression/ContextEntryExpressionCell.css";
import * as React from "react";
import {
  ContextExpressionDefinitionEntry,
  ExpressionDefinitionLogicType,
  InvocationExpressionDefinition,
} from "../../api";
import {
  NestedExpressionDispatchContextProvider,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import {
  NestedExpressionContainerContext,
  NestedExpressionContainerContextType,
} from "../../resizing/NestedExpressionContainerContext";
import { useContextExpressionContext } from "../ContextExpression";
import { useCallback, useMemo } from "react";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";

export interface ArgumentEntryExpressionCellProps {
  // This name ('data') can't change, as this is used on "cellComponentByColumnId".
  data: readonly ContextExpressionDefinitionEntry[];
  rowIndex: number;
}

export const ArgumentEntryExpressionCell: React.FunctionComponent<ArgumentEntryExpressionCellProps> = ({
  data: argumentEntries,
  rowIndex,
}) => {
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onSetExpression = useCallback(
    ({ getNewExpression }) => {
      setExpression((prev: InvocationExpressionDefinition) => {
        const argumentEntries = [...(prev.bindingEntries ?? [])];
        argumentEntries[rowIndex].entryExpression = getNewExpression(
          argumentEntries[rowIndex]?.entryExpression ?? { logicType: ExpressionDefinitionLogicType.Undefined }
        );
        return { ...prev, bindingEntries: argumentEntries };
      });
    },
    [rowIndex, setExpression]
  );

  const contextExpression = useContextExpressionContext();
  const nestedExpressionContainer = useMemo<NestedExpressionContainerContextType>(() => {
    return {
      minWidthLocal: contextExpression.entryExpressionsMinWidthLocal,
      minWidthGlobal: contextExpression.entryExpressionsMinWidthGlobal,
      actualWidth: contextExpression.entryExpressionsActualWidth,
      resizingWidth: contextExpression.entryExpressionsResizingWidth,
    };
  }, [contextExpression]);

  return (
    <NestedExpressionContainerContext.Provider value={nestedExpressionContainer}>
      <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
        <ExpressionContainer
          expression={argumentEntries[rowIndex]?.entryExpression}
          isClearSupported={true}
          isHeadless={true}
        />
      </NestedExpressionDispatchContextProvider>
    </NestedExpressionContainerContext.Provider>
  );
};
