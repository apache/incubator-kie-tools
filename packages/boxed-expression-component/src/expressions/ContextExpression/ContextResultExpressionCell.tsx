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
import { ContextEntryExpression } from "./ContextEntryExpression";
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
        <ContextEntryExpression expression={props.contextExpression.result} />
      </NestedExpressionDispatchContextProvider>
    </NestedExpressionContainerContext.Provider>
  );
}
