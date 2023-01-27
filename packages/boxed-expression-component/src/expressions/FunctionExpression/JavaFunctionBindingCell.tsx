import * as React from "react";
import { useCallback, useMemo, useRef } from "react";
import {
  BeeTableCellProps,
  BeeTableHeaderVisibility,
  BeeTableProps,
  JavaFunctionExpressionDefinition,
} from "../../api";
import { usePublishedBeeTableResizableColumns } from "../../resizing/BeeTableResizableColumnsContextProvider";
import { useApportionedColumnWidthsIfNestedTable } from "../../resizing/Hooks";
import { useNestedExpressionContainer } from "../../resizing/NestedExpressionContainerContext";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";
import {
  JAVA_FUNCTION_EXPRESSION_EXTRA_WIDTH,
  JAVA_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH,
  JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
} from "../../resizing/WidthConstants";
import { useBeeTableSelectableCellRef } from "../../selection/BeeTableSelectionContext";
import { BeeTable, BeeTableCellUpdate, BeeTableRef } from "../../table/BeeTable";
import { useBoxedExpressionEditorDispatch } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { ROWTYPE } from "./FunctionExpression";
import "./JavaFunctionBindingCell.css";

export type JAVA_ROWTYPE = {
  value: string;
  label: string;
  javaFunctionExpression: JavaFunctionExpressionDefinition;
};

export function JavaFunctionBindingCell({ data, rowIndex }: BeeTableCellProps<ROWTYPE>) {
  const functionExpression = data[rowIndex].functionExpression as JavaFunctionExpressionDefinition & {
    isNested: boolean;
  };

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

  const setClassAndMethodNamesWidth = useCallback(
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

  const nestedExpressionContainer = useNestedExpressionContainer();
  const classAndMethodNamesMinWidth = useMemo(() => {
    return Math.max(
      nestedExpressionContainer.minWidth -
        JAVA_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH -
        JAVA_FUNCTION_EXPRESSION_EXTRA_WIDTH,
      JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH
    );
  }, [nestedExpressionContainer]);

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
        setWidth: setClassAndMethodNamesWidth,
        minWidth: classAndMethodNamesMinWidth,
      },
    ],
    [functionExpression.classAndMethodNamesWidth, classAndMethodNamesMinWidth, setClassAndMethodNamesWidth]
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

  const cellComponentByColumnAccessor: BeeTableProps<JAVA_ROWTYPE>["cellComponentByColumnAccessor"] = useMemo(
    () => ({
      label: (props) => <JavaFunctionExpressionLabelCell {...props} />,
    }),
    []
  );

  /// //////////////////////////////////////////////////////
  /// ///////////// RESIZING WIDTHS ////////////////////////
  /// //////////////////////////////////////////////////////

  const columns = useMemo(
    () => [
      { width: JAVA_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH, isFrozen: true },
      { width: functionExpression.classAndMethodNamesWidth },
    ],
    [functionExpression.classAndMethodNamesWidth]
  );

  const { onColumnResizingWidthChange, isPivoting } = usePublishedBeeTableResizableColumns(
    functionExpression.id,
    columns.length,
    false
  );

  const beeTableRef = useRef<BeeTableRef>(null);

  useApportionedColumnWidthsIfNestedTable(
    beeTableRef,
    isPivoting,
    functionExpression.isNested,
    JAVA_FUNCTION_EXPRESSION_EXTRA_WIDTH,
    columns,
    useMemo(() => [], []) // rows
  );

  /// //////////////////////////////////////////////////////

  return (
    <BeeTable<JAVA_ROWTYPE>
      forwardRef={beeTableRef}
      onColumnResizingWidthChange={onColumnResizingWidthChange}
      resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER}
      columns={beeTableColumns}
      rows={beeTableRows}
      onCellUpdates={onCellUpdates}
      getRowKey={(r) => r.id}
      getColumnKey={(c) => c.id}
      operationConfig={[]}
      cellComponentByColumnAccessor={cellComponentByColumnAccessor}
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

  useBeeTableSelectableCellRef(
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
