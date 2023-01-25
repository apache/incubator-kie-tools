import * as React from "react";
import { useCallback, useMemo } from "react";
import {
  BeeTableProps,
  DmnBuiltInDataType,
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  generateUuid,
} from "../../api";
import { BoxedExpressionEditorContextProvider } from "../../expressions/BoxedExpressionEditor/BoxedExpressionEditorContext";
import { ResizingWidthsContextProvider } from "../../resizing/ResizingWidthsContext";
import { BeeTable } from "./BeeTable";

export function StandaloneBeeTable<R extends object>(props: BeeTableProps<R>) {
  const dataTypes = useMemo(() => {
    return [];
  }, []);

  const setExpression = useCallback(() => {
    // Empty on purpose.
  }, []);

  const expression = useMemo<ExpressionDefinition>(() => {
    return {
      id: generateUuid(),
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.Undefined,
    };
  }, []);

  return (
    <BoxedExpressionEditorContextProvider
      dataTypes={dataTypes}
      decisionNodeId={""}
      expressionDefinition={expression}
      setExpressionDefinition={setExpression}
    >
      <ResizingWidthsContextProvider>
        <BeeTable {...props} />
      </ResizingWidthsContextProvider>
    </BoxedExpressionEditorContextProvider>
  );
}
