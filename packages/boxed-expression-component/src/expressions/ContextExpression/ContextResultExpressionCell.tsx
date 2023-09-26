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

  // It is not possible to have a ContextExpression without any entry (props.contextExpression.contextEntries.length === 0)
  const lastEntry = props.contextExpression.contextEntries[props.contextExpression.contextEntries.length - 1];

  return (
    <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
      <ExpressionContainer
        expression={props.contextExpression.result}
        isResetSupported={true}
        isNested={true}
        rowIndex={props.rowIndex}
        columnIndex={props.columnIndex}
        parentElementId={lastEntry.entryInfo.id}
      />
    </NestedExpressionDispatchContextProvider>
  );
}
