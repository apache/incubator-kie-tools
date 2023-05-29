/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Popover } from "@patternfly/react-core/dist/js/components/Popover/Popover";
import HelpIcon from "@patternfly/react-icons/dist/js/icons/help-icon";
import _ from "lodash";
import * as React from "react";
import { useCallback, useEffect, useMemo } from "react";
import * as ReactTable from "react-table";
import {
  BeeTableCellProps,
  BeeTableHeaderVisibility,
  BeeTableOperationConfig,
  BeeTableProps,
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
  FunctionExpressionDefinitionKind,
  generateUuid,
  JavaFunctionExpressionDefinition,
} from "../../api";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { usePublishedBeeTableResizableColumns } from "../../resizing/BeeTableResizableColumnsContext";
import { useApportionedColumnWidthsIfNestedTable } from "../../resizing/Hooks";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";
import {
  JAVA_FUNCTION_EXPRESSION_EXTRA_WIDTH,
  JAVA_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH,
  JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
} from "../../resizing/WidthConstants";
import { useBeeTableSelectableCellRef } from "../../selection/BeeTableSelectionContext";
import { BeeTable, BeeTableCellUpdate, BeeTableColumnUpdate, BeeTableRef } from "../../table/BeeTable";
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { DEFAULT_EXPRESSION_NAME } from "../ExpressionDefinitionHeaderMenu";
import { useFunctionExpressionControllerCell, useFunctionExpressionParametersColumnHeader } from "./FunctionExpression";
import "./JavaFunctionExpression.css";

export type JAVA_ROWTYPE = {
  value: string;
  label: string;
};

export function JavaFunctionExpression({
  functionExpression,
}: {
  functionExpression: JavaFunctionExpressionDefinition & { isNested: boolean };
}) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setExpression } = useBoxedExpressionEditorDispatch();

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

  const parametersColumnHeader = useFunctionExpressionParametersColumnHeader(functionExpression.formalParameters);

  const beeTableColumns = useMemo<ReactTable.Column<JAVA_ROWTYPE>[]>(() => {
    return [
      {
        label: functionExpression.name ?? DEFAULT_EXPRESSION_NAME,
        accessor: functionExpression.id as any, // FIXME: https://github.com/kiegroup/kie-issues/issues/169
        dataType: functionExpression.dataType ?? DmnBuiltInDataType.Undefined,
        isRowIndexColumn: false,
        width: undefined,
        columns: [
          {
            headerCellElement: parametersColumnHeader,
            accessor: "parameters" as any,
            label: "parameters",
            isRowIndexColumn: false,
            dataType: undefined as any,
            width: undefined,
            columns: [
              {
                label: "label",
                accessor: "label" as any,
                dataType: undefined as any,
                isRowIndexColumn: false,
                isWidthPinned: true,
                isWidthConstant: true,
                width: JAVA_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH,
                minWidth: JAVA_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH,
              },
              {
                label: "value",
                accessor: "value" as any,
                dataType: undefined as any,
                isRowIndexColumn: false,
                width: functionExpression.classAndMethodNamesWidth,
                setWidth: setClassAndMethodNamesWidth,
                minWidth: JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
              },
            ],
          },
        ],
      },
    ];
  }, [
    functionExpression.classAndMethodNamesWidth,
    functionExpression.dataType,
    functionExpression.id,
    functionExpression.name,
    parametersColumnHeader,
    setClassAndMethodNamesWidth,
  ]);

  const headerVisibility = useMemo(() => {
    return functionExpression.isNested
      ? BeeTableHeaderVisibility.SecondToLastLevel
      : BeeTableHeaderVisibility.AllLevels;
  }, [functionExpression.isNested]);

  const onColumnUpdates = useCallback(
    ([{ name, dataType }]: BeeTableColumnUpdate<JAVA_ROWTYPE>[]) => {
      setExpression((prev) => ({
        ...prev,
        name,
        dataType,
      }));
    },
    [setExpression]
  );

  const beeTableOperationConfig = useMemo<BeeTableOperationConfig>(() => {
    return [
      {
        group: _.upperCase(i18n.function),
        items: [],
      },
    ];
  }, [i18n]);

  const beeTableRows = useMemo<JAVA_ROWTYPE[]>(() => {
    return [
      { label: "Class name", value: functionExpression.className ?? "" },
      { label: "Method signature", value: functionExpression.methodName ?? "" },
    ];
  }, [functionExpression]);

  const controllerCell = useFunctionExpressionControllerCell(FunctionExpressionDefinitionKind.Java);

  const getRowKey = useCallback((r: ReactTable.Row<JAVA_ROWTYPE>) => {
    return r.id;
  }, []);

  const onRowReset = useCallback(() => {
    setExpression((prev) => {
      return {
        ...prev,
        expression: {
          id: generateUuid(),
          logicType: ExpressionDefinitionLogicType.Undefined,
          dataType: DmnBuiltInDataType.Undefined,
        },
      };
    });
  }, [setExpression]);

  /// //////////////////////////////////////////////////////
  /// ///////////// RESIZING WIDTHS ////////////////////////
  /// //////////////////////////////////////////////////////

  const columns = useMemo(
    () => [
      {
        minWidth: JAVA_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH,
        width: JAVA_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH,
        isFrozen: true,
      },
      {
        minWidth: JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
        width: functionExpression.classAndMethodNamesWidth,
      },
    ],
    [functionExpression.classAndMethodNamesWidth]
  );

  const { onColumnResizingWidthChange, isPivoting, columnResizingWidths } = usePublishedBeeTableResizableColumns(
    functionExpression.id,
    columns.length,
    true
  );

  const beeTableRef = React.useRef<BeeTableRef>(null);

  useApportionedColumnWidthsIfNestedTable(
    beeTableRef,
    isPivoting,
    functionExpression.isNested,
    JAVA_FUNCTION_EXPRESSION_EXTRA_WIDTH,
    columns,
    columnResizingWidths,
    useMemo(() => [], []) // rows
  );

  useEffect(() => {
    beeTableRef.current?.updateColumnResizingWidths(
      new Map([[1, { isPivoting: false, value: JAVA_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH }]])
    );
  }, []);

  /// //////////////////////////////////////////////////////

  const cellComponentByColumnAccessor: BeeTableProps<JAVA_ROWTYPE>["cellComponentByColumnAccessor"] = useMemo(
    () => ({
      label: (props) => <JavaFunctionExpressionLabelCell {...props} />,
    }),
    []
  );

  const onCellUpdates = useCallback(
    (cellUpdates: BeeTableCellUpdate<JAVA_ROWTYPE>[]) => {
      for (const u of cellUpdates) {
        // Class
        if (u.rowIndex === 0) {
          setExpression((prev: JavaFunctionExpressionDefinition) => ({
            ...prev,
            className: u.value,
            classFieldId: prev.classFieldId ?? generateUuid(),
          }));
        }

        // Method
        else if (u.rowIndex === 1) {
          setExpression((prev: JavaFunctionExpressionDefinition) => ({
            ...prev,
            methodName: u.value,
            methodFieldId: prev.methodFieldId ?? generateUuid(),
          }));
        }
      }
    },
    [setExpression]
  );

  return (
    <div className={`function-expression ${functionExpression.id}`}>
      <BeeTable<JAVA_ROWTYPE>
        forwardRef={beeTableRef}
        onColumnResizingWidthChange={onColumnResizingWidthChange}
        resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER}
        operationConfig={beeTableOperationConfig}
        onColumnUpdates={onColumnUpdates}
        getRowKey={getRowKey}
        onRowReset={onRowReset}
        onCellUpdates={onCellUpdates}
        columns={beeTableColumns}
        rows={beeTableRows}
        headerLevelCountForAppendingRowIndexColumn={2}
        skipLastHeaderGroup={true}
        cellComponentByColumnAccessor={cellComponentByColumnAccessor}
        headerVisibility={headerVisibility}
        controllerCell={controllerCell}
        shouldRenderRowIndexColumn={true}
        shouldShowRowsInlineControls={false}
        shouldShowColumnsInlineControls={false}
      />
    </div>
  );
}

function JavaFunctionExpressionLabelCell(props: React.PropsWithChildren<BeeTableCellProps<JAVA_ROWTYPE>>) {
  const label = useMemo(() => {
    return props.data[props.rowIndex].label;
  }, [props.data, props.rowIndex]);

  const { isActive } = useBeeTableSelectableCellRef(
    props.rowIndex,
    props.columnIndex,
    undefined,
    useCallback(() => label, [label])
  );

  const { beeGwtService } = useBoxedExpressionEditor();

  useEffect(() => {
    if (isActive) {
      beeGwtService?.selectObject("");
    }
  }, [beeGwtService, isActive]);

  const getParameterLabelHelp = useCallback((): React.ReactNode => {
    if (props.rowIndex === 0) {
      return <code>org.kie.kogito.MyClass</code>;
    } else {
      return <code>doSomething(java.lang.Integer, double)</code>;
    }
  }, [props.rowIndex]);

  const [isCellHovered, setIsCellHovered] = React.useState<boolean>();

  return (
    <div
      className={"java-function-expression-label"}
      onMouseEnter={() => setIsCellHovered(true)}
      onMouseLeave={() => setIsCellHovered(false)}
    >
      <div className={"name"}>{label}</div>
      <div className={"data-type"}>
        {`(string)`}
        {isCellHovered && (
          <Popover
            className="java-function-parameter-help-popover"
            headerContent={label + " example"}
            bodyContent={getParameterLabelHelp}
          >
            <HelpIcon size="sm" className="java-function-parameter-help-icon" />
          </Popover>
        )}
      </div>
    </div>
  );
}
