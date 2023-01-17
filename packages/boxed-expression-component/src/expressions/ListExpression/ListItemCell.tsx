import * as React from "react";
import { useCallback } from "react";
import { BeeTableCellProps, ListExpressionDefinition, ExpressionDefinitionLogicType } from "../../api";
import {
  useBoxedExpressionEditorDispatch,
  NestedExpressionDispatchContextProvider,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";
import { ROWTYPE } from "./ListExpression";

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
      <ExpressionContainer expression={items[rowIndex]?.entryExpression} isResetSupported={true} isNested={true} />
    </NestedExpressionDispatchContextProvider>
  );
}
