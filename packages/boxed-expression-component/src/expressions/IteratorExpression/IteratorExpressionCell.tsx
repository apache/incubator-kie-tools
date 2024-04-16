import { BoxedIterator } from "../../api";

import {
  NestedExpressionDispatchContextProvider,
  useBoxedExpressionEditorDispatch,
} from "../../BoxedExpressionEditorContext";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";
import { IteratorClause } from "./IteratorExpressionComponent";

export interface IteratorExpressionCellExpressionCellProps {
  iteratorClause: IteratorClause;
  rowIndex: number;
  columnIndex: number;
  columnId: string;
}

export function IteratorExpressionCell({
  rowIndex,
  columnIndex,
  parentElementId,
  iteratorClause,
}: IteratorExpressionCellExpressionCellProps & { parentElementId: string }) {
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onSetExpression = useCallback(
    ({ getNewExpression }) => {
      setExpression((prev: BoxedIterator) => {
        switch (rowIndex) {
          case 1:
            return {
              ...prev,
              in: {
                expression: getNewExpression(prev.in.expression),
              },
            };
          case 2:
          default:
            if (prev.__$$element === "for") {
              return {
                ...prev,
                return: {
                  expression: getNewExpression(),
                },
              };
            } else {
              return {
                ...prev,
                satisfies: {
                  expression: getNewExpression(),
                },
              };
            }
        }
      });
    },
    [rowIndex, setExpression]
  );

  const currentExpression = useMemo(() => {
    if (typeof iteratorClause.child !== "string") {
      return iteratorClause.child?.expression as any;
    }
  }, [iteratorClause.child]);

  return (
    <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
      <ExpressionContainer
        expression={currentExpression}
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
