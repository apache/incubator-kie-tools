import * as React from "react";
import { useCallback, useMemo } from "react";
import { ContextExpressionDefinition } from "../../api";
import {
  NestedExpressionContainerContextType,
  NestedExpressionContainerContext,
} from "../../resizing/NestedExpressionContainerContext";
import {
  useBoxedExpressionEditorDispatch,
  NestedExpressionDispatchContextProvider,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";
import { useContextExpressionContext } from "./ContextExpression";

export function ContextResultExpressionCell(props: { contextExpression: ContextExpressionDefinition }) {
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
        <ExpressionContainer expression={props.contextExpression.result} isClearSupported={true} isHeadless={true} />
      </NestedExpressionDispatchContextProvider>
    </NestedExpressionContainerContext.Provider>
  );
}
