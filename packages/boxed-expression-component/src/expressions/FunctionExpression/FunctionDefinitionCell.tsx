import { useCallback, useMemo } from "react";
import {
  BeeTableCellProps,
  ExpressionDefinitionLogicType,
  FunctionExpressionDefinitionKind,
  ContextExpressionDefinition,
  LiteralExpressionDefinition,
  PmmlLiteralExpressionDefinition,
} from "../../api";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import {
  NestedExpressionContainerContextType,
  NestedExpressionContainerContext,
} from "../../resizing/NestedExpressionContainerContext";
import {
  useBoxedExpressionEditorDispatch,
  NestedExpressionDispatchContextProvider,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { useContextExpressionContext } from "../ContextExpression";
import * as React from "react";
import { ROWTYPE } from "./FunctionExpression";
import { javaContextExpression } from "./JavaFunctionExpression";
import { pmmlContextExpression } from "./PmmlFunctionExpression";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";

export function FunctionDefinitionCell({ data, rowIndex }: BeeTableCellProps<ROWTYPE>) {
  const { i18n } = useBoxedExpressionEditorI18n();

  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onSetExpression = useCallback(
    ({ getNewExpression }) => {
      setExpression((prev) => {
        if (prev.logicType !== ExpressionDefinitionLogicType.Function) {
          return prev;
        }

        // FEEL
        if (prev.functionKind === FunctionExpressionDefinitionKind.Feel) {
          return { ...prev, expression: getNewExpression(prev.expression) };
        }

        // Java
        else if (prev.functionKind === FunctionExpressionDefinitionKind.Java) {
          const newExpression = getNewExpression(javaContextExpression(prev, i18n)) as ContextExpressionDefinition;
          return {
            ...prev,
            className: (newExpression.contextEntries![0].entryExpression as LiteralExpressionDefinition).content,
            classFieldId: (newExpression.contextEntries![0].entryExpression as LiteralExpressionDefinition).content,
            methodName: (newExpression.contextEntries![1].entryExpression as LiteralExpressionDefinition).content,
            methodFieldId: (newExpression.contextEntries![1].entryExpression as LiteralExpressionDefinition).content,
          };
        }

        // PMML
        else if (prev.functionKind === FunctionExpressionDefinitionKind.Pmml) {
          const newExpression = getNewExpression(pmmlContextExpression(prev, i18n)) as ContextExpressionDefinition;
          // FIXME: Tiago -> STATE GAP
          return {
            ...prev,
            document: (newExpression.contextEntries[0].entryExpression as PmmlLiteralExpressionDefinition).selected,
            documentFieldId: (newExpression.contextEntries[0].entryExpression as PmmlLiteralExpressionDefinition)
              .selected,
            model: (newExpression.contextEntries[1].entryExpression as PmmlLiteralExpressionDefinition).selected,
            modelFieldId: (newExpression.contextEntries[1].entryExpression as PmmlLiteralExpressionDefinition).selected,
          };
        }

        // default
        else {
          throw new Error("Shouldn't ever reach this point.");
        }
      });
    },
    [i18n, setExpression]
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
        <ExpressionContainer expression={data[rowIndex]?.entryExpression} isResetSupported={true} isHeadless={true} />
      </NestedExpressionDispatchContextProvider>
    </NestedExpressionContainerContext.Provider>
  );
}
