import { useCallback, useMemo } from "react";
import { BeeTableCellProps, ListExpressionDefinition, ExpressionDefinitionLogicType } from "../../api";
import {
  NestedExpressionContainerContextType,
  NestedExpressionContainerContext,
  useNestedExpressionContainer,
} from "../../resizing/NestedExpressionContainerContext";
import {
  useBoxedExpressionEditorDispatch,
  NestedExpressionDispatchContextProvider,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { ROWTYPE } from "../FunctionExpression";
import * as React from "react";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";
import { LIST_EXPRESSION_EXTRA_WIDTH } from "../../resizing/WidthValues";

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

  const nestedExpressionContainer = useNestedExpressionContainer();
  const nestedExpressionContainerValue = useMemo<NestedExpressionContainerContextType>(() => {
    return {
      actualWidth: nestedExpressionContainer.actualWidth - LIST_EXPRESSION_EXTRA_WIDTH,
      minWidthGlobal: nestedExpressionContainer.minWidthGlobal - LIST_EXPRESSION_EXTRA_WIDTH,
      minWidthLocal: nestedExpressionContainer.minWidthLocal - LIST_EXPRESSION_EXTRA_WIDTH,
      resizingWidth: {
        value: nestedExpressionContainer.resizingWidth.value - LIST_EXPRESSION_EXTRA_WIDTH,
        isPivoting: false,
      },
    };
  }, [nestedExpressionContainer]);

  return (
    <NestedExpressionContainerContext.Provider value={nestedExpressionContainerValue}>
      <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
        <ExpressionContainer expression={items[rowIndex]?.entryExpression} isResetSupported={true} isHeadless={true} />
      </NestedExpressionDispatchContextProvider>
    </NestedExpressionContainerContext.Provider>
  );
}
