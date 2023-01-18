import * as React from "react";
import { useCallback, useMemo } from "react";
import {
  BeeTableCellProps,
  BeeTableHeaderVisibility,
  BeeTableProps,
  JavaFunctionExpressionDefinition,
} from "../../api";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";
import {
  JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
  JAVA_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH,
} from "../../resizing/WidthConstants";
import { useBeeTableCell } from "../../selection/BeeTableSelectionContext";
import { BeeTable, BeeTableCellUpdate } from "../../table/BeeTable";
import { useBoxedExpressionEditorDispatch } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { ROWTYPE } from "./FunctionExpression";
import "./JavaFunctionBindingCell.css";

export type JAVA_ROWTYPE = {
  value: string;
  label: string;
  javaFunctionExpression: JavaFunctionExpressionDefinition;
};

export function JavaFunctionBindingCell({ data, rowIndex }: BeeTableCellProps<ROWTYPE>) {
  const functionExpression = data[rowIndex].functionExpression as JavaFunctionExpressionDefinition;

  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onCellUpdates = useCallback(
    (cellUpdates: BeeTableCellUpdate<JAVA_ROWTYPE>[]) => {
      for (const u of cellUpdates) {
        // Class
        if (u.rowIndex === 0) {
          setExpression((prev) => ({
            ...prev,
            className: u.value,
            classFieldId: u.value,
          }));
        }

        // Method
        else if (u.rowIndex === 1) {
          setExpression((prev) => ({
            ...prev,
            methodName: u.value,
            methodFieldId: u.value,
          }));
        }
      }
    },
    [setExpression]
  );

  const setJavaBindingsWidth = useCallback(
    (newWidthAction: React.SetStateAction<number | undefined>) => {
      setExpression((prev: JavaFunctionExpressionDefinition) => {
        const newWidth =
          typeof newWidthAction === "function" ? newWidthAction(prev.classAndMethodNamesWidth) : newWidthAction;
        return {
          ...prev,
          classAndMethodNamesWidth: newWidth,
        };
      });
    },
    [setExpression]
  );

  const beeTableColumns = useMemo(
    () => [
      {
        label: "label",
        accessor: "label" as any,
        dataType: undefined as any,
        isRowIndexColumn: false,
        width: undefined,
        minWidth: JAVA_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH,
      },
      {
        label: "value",
        accessor: "value" as any,
        dataType: undefined as any,
        isRowIndexColumn: false,
        width: functionExpression.classAndMethodNamesWidth,
        setWidth: setJavaBindingsWidth,
        minWidth: JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
      },
    ],
    [functionExpression.classAndMethodNamesWidth, setJavaBindingsWidth]
  );

  const beeTableRows = useMemo(() => {
    return [
      {
        label: "Class name",
        value: functionExpression.className ?? "",
        javaFunctionExpression: functionExpression,
      },
      {
        label: "Method signature",
        value: functionExpression.methodName ?? "",
        javaFunctionExpression: functionExpression,
      },
    ];
  }, [functionExpression]);

  const cellComponentByColumnId: BeeTableProps<JAVA_ROWTYPE>["cellComponentByColumnId"] = useMemo(
    () => ({
      label: (props) => <JavaFunctionExpressionLabelCell {...props} />,
    }),
    []
  );

  return (
    <BeeTable<JAVA_ROWTYPE>
      resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER}
      columns={beeTableColumns}
      rows={beeTableRows}
      onCellUpdates={onCellUpdates}
      getRowKey={(r) => r.id}
      getColumnKey={(c) => c.id}
      operationConfig={[]}
      cellComponentByColumnId={cellComponentByColumnId}
      headerVisibility={BeeTableHeaderVisibility.None}
      shouldRenderRowIndexColumn={false}
      shouldShowRowsInlineControls={false}
      shouldShowColumnsInlineControls={false}
    />
  );
}

function JavaFunctionExpressionLabelCell(props: React.PropsWithChildren<BeeTableCellProps<JAVA_ROWTYPE>>) {
  const label = React.useMemo(() => {
    return props.data[props.rowIndex].label;
  }, [props.data, props.rowIndex]);

  useBeeTableCell(
    props.rowIndex,
    props.columnIndex,
    undefined,
    React.useCallback(() => label, [label])
  );

  return (
    <div className={"java-function-expression-label"}>
      <div className={"name"}>{label}</div>
      <div className={"data-type"}>{`(string)`}</div>
    </div>
  );
}
