import * as React from "react";
import { useCallback } from "react";
import { BeeTableCellProps, ListExpressionDefinition, ExpressionDefinitionLogicType } from "../../api";
import {
  useBoxedExpressionEditorDispatch,
  NestedExpressionDispatchContextProvider,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { ROWTYPE } from "../FunctionExpression";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";

export function ListItemCell({ rowIndex, data: items }: BeeTableCellProps<ROWTYPE>) {
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onSetExpression = useCallback(
    ({ getNewExpression }) => {
      setExpression((prev: ListExpressionDefinition) => {
        const newItems = [...(prev.items ?? [])];
        newItems[rowIndex] = getNewExpression(
          newItems[rowIndex] ?? { logicType: ExpressionDefinitionLogicType.Undefined }
        );
        return { ...prev, items: newItems };
      });
    },
    [rowIndex, setExpression]
  );

  return (
    <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
      <ExpressionContainer expression={items[rowIndex]?.entryExpression} isResetSupported={true} isHeadless={true} />
    </NestedExpressionDispatchContextProvider>
  );
}
