import * as React from "react";
import { useCallback } from "react";
import { ContextExpressionDefinition } from "../../api";
import {
  useBoxedExpressionEditorDispatch,
  NestedExpressionDispatchContextProvider,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";

export function ContextResultExpressionCell(props: {
  contextExpression: ContextExpressionDefinition;
  rowIndex: number;
  columnIndex: number;
}) {
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onSetExpression = useCallback(
    ({ getNewExpression }) => {
      setExpression((prev: ContextExpressionDefinition) => ({
        ...prev,
        result: getNewExpression(prev.result),
      }));
    },
    [setExpression]
  );

  return (
    <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
      <ExpressionContainer
        expression={props.contextExpression.result}
        isResetSupported={true}
        isNested={true}
        rowIndex={props.rowIndex}
        columnIndex={props.columnIndex}
      />
    </NestedExpressionDispatchContextProvider>
  );
}
