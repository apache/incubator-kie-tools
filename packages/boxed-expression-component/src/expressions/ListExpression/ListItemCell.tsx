import { useCallback, useMemo } from "react";
import { BeeTableCellProps, ListExpressionDefinition, ExpressionDefinitionLogicType } from "../../api";
import {
  NestedExpressionContainerContextType,
  NestedExpressionContainerContext,
} from "../../resizing/NestedExpressionContainerContext";
import {
  useBoxedExpressionEditorDispatch,
  NestedExpressionDispatchContextProvider,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { useContextExpressionContext } from "../ContextExpression";
import { ROWTYPE } from "../FunctionExpression";
import * as React from "react";
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
        <ExpressionContainer expression={items[rowIndex]?.entryExpression} isResetSupported={true} isHeadless={true} />
      </NestedExpressionDispatchContextProvider>
    </NestedExpressionContainerContext.Provider>
  );
}
