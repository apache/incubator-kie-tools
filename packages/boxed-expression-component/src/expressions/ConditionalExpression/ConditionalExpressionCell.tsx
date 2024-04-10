import { BeeTableCellProps, BoxedConditional } from "../../api";

import {
  NestedExpressionDispatchContextProvider,
  OnSetExpression,
  useBoxedExpressionEditorDispatch,
} from "../../BoxedExpressionEditorContext";
import * as React from "react";
import { useCallback } from "react";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";
import { ConditionalClause, ROWTYPE } from "./ConditionalExpression";

export interface ConditionalExpressionCellProps {
  conditionalClause: ConditionalClause;
  rowIndex: number;
  columnIndex: number;
  columnId: string;
}

export function ConditionalExpressionCell({
  data,
  rowIndex,
  columnIndex,
  parentElementId,
}: BeeTableCellProps<ROWTYPE> & { parentElementId: string }) {
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onSetExpression = useCallback<OnSetExpression>(
    ({ getNewExpression }) => {
      setExpression((prev: BoxedConditional) => {
        if (rowIndex === 0) {
          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: BoxedConditional = {
            ...prev,
            if: { ...prev.if, expression: getNewExpression(prev.if.expression)! },
          };
          return ret;
        } else if (rowIndex === 1) {
          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: BoxedConditional = {
            ...prev,
            then: { ...prev.then, expression: getNewExpression(prev.then.expression)! },
          };
          return ret;
        } else if (rowIndex === 2) {
          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: BoxedConditional = {
            ...prev,
            else: { ...prev.else, expression: getNewExpression(prev.else.expression)! },
          };
          return ret;
        } else {
          throw new Error("ConditionalExpression shouldn't have more than 3 rows.");
        }
      });
    },
    [rowIndex, setExpression]
  );

  return (
    <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
      <ExpressionContainer
        expression={data[rowIndex].part.expression}
        isResetSupported={true}
        isNested={true}
        rowIndex={rowIndex}
        columnIndex={columnIndex}
        parentElementId={parentElementId}
        parentElementTypeRef={undefined}
      />
    </NestedExpressionDispatchContextProvider>
  );
}
