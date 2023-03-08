import * as React from "react";
import { useCallback, useMemo } from "react";
import { BeeTableCellProps, ExpressionDefinition, FeelFunctionExpressionDefinition } from "../../api";
import { useNestedExpressionContainerWithNestedExpressions } from "../../resizing/Hooks";
import { NestedExpressionContainerContext } from "../../resizing/NestedExpressionContainerContext";
import {
  CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
  FUNCTION_EXPRESSION_COMMON_EXTRA_WIDTH,
} from "../../resizing/WidthConstants";
import {
  NestedExpressionDispatchContextProvider,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";
import { ROWTYPE } from "./FunctionExpression";

export function FeelFunctionImplementationCell({ data, rowIndex }: BeeTableCellProps<ROWTYPE>) {
  const functionExpression = data[rowIndex].functionExpression as FeelFunctionExpressionDefinition;

  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onSetExpression = useCallback(
    ({ getNewExpression }: { getNewExpression: (prev: ExpressionDefinition) => ExpressionDefinition }) => {
      setExpression((prev: FeelFunctionExpressionDefinition) => ({
        ...prev,
        expression: getNewExpression(prev.expression),
      }));
    },
    [setExpression]
  );

  return (
    <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
      <ExpressionContainer expression={functionExpression.expression} isResetSupported={false} isNested={true} />
    </NestedExpressionDispatchContextProvider>
  );
}
