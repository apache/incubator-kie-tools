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

import "./ContextEntryExpressionCell.css";
import * as React from "react";
import {
  ContextExpressionDefinition,
  ContextExpressionDefinitionEntry,
  ExpressionDefinitionLogicType,
} from "../../api";
import * as _ from "lodash";
import {
  NestedExpressionDispatchContextProvider,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { useCallback, useMemo } from "react";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";

export interface ContextEntryExpressionCellProps {
  // This name ('data') can't change, as this is used on "cellComponentByColumnAccessor".
  data: readonly ContextExpressionDefinitionEntry[];
  rowIndex: number;
}

export const ContextEntryExpressionCell: React.FunctionComponent<ContextEntryExpressionCellProps> = ({
  data: contextEntries,
  rowIndex,
}) => {
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onSetExpression = useCallback(
    ({ getNewExpression }) => {
      setExpression((prev: ContextExpressionDefinition) => {
        const contextEntries = [...(prev.contextEntries ?? [])];
        contextEntries[rowIndex].entryExpression = getNewExpression(
          contextEntries[rowIndex]?.entryExpression ?? { logicType: ExpressionDefinitionLogicType.Undefined }
        );
        return { ...prev, contextEntries };
      });
    },
    [rowIndex, setExpression]
  );

  return (
    <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
      <ExpressionContainer
        expression={contextEntries[rowIndex]?.entryExpression}
        isResetSupported={true}
        isNested={true}
      />
    </NestedExpressionDispatchContextProvider>
  );
};
