/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Popover } from "@patternfly/react-core/dist/js/components/Popover/Popover";
import HelpIcon from "@patternfly/react-icons/dist/js/icons/help-icon";
import * as React from "react";
import { useCallback, useEffect, useMemo } from "react";
import * as ReactTable from "react-table";
import {
  BeeTableCellProps,
  BeeTableContextMenuAllowedOperationsConditions,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  BeeTableProps,
  DmnBuiltInDataType,
  BoxedFunctionKind,
  generateUuid,
  BoxedFunction,
} from "../../api";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { usePublishedBeeTableResizableColumns } from "../../resizing/BeeTableResizableColumnsContext";
import { useApportionedColumnWidthsIfNestedTable } from "../../resizing/Hooks";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";
import {
  JAVA_FUNCTION_EXPRESSION_EXTRA_WIDTH,
  JAVA_FUNCTION_EXPRESSION_LABEL_MIN_WIDTH,
  JAVA_FUNCTION_EXPRESSION_VALUES_COLUMN_WIDTH_INDEX,
  JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
} from "../../resizing/WidthConstants";
import { BeeTable, BeeTableCellUpdate, BeeTableColumnUpdate, BeeTableRef } from "../../table/BeeTable";
import { useBoxedExpressionEditor, useBoxedExpressionEditorDispatch } from "../../BoxedExpressionEditorContext";
import { DEFAULT_EXPRESSION_VARIABLE_NAME } from "../../expressionVariable/ExpressionVariableMenu";
import { useFunctionExpressionControllerCell, useFunctionExpressionParametersColumnHeader } from "./FunctionExpression";
import {
  DMN15__tContext,
  DMN15__tFunctionDefinition,
  DMN15__tLiteralExpression,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import "./JavaFunctionExpression.css";
import { useBeeTableSelectableCellRef } from "../../selection/BeeTableSelectionContext";

export type JAVA_ROWTYPE = {
  value: string;
  label: string;
};

export type BoxedFunctionJava = DMN15__tFunctionDefinition & {
  "@_kind"?: "Java";
  __$$element: "functionDefinition";
};

export function JavaFunctionExpression({
  functionExpression,
  isNested,
  parentElementId,
}: {
  functionExpression: BoxedFunctionJava;
  isNested: boolean;
  parentElementId: string;
}) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { expressionHolderId, widthsById } = useBoxedExpressionEditor();
  const { setExpression, setWidthsById } = useBoxedExpressionEditorDispatch();

  const getClassContextEntry = useCallback((c: DMN15__tContext) => {
    return c.contextEntry?.find(({ variable }) => variable?.["@_name"] === "class");
  }, []);

  const getVariableContextEntry = useCallback((c: DMN15__tContext) => {
    return c.contextEntry?.find(({ variable }) => variable?.["@_name"] === "method signature");
  }, []);

  const id = functionExpression["@_id"]!;

  const widths = useMemo(() => widthsById.get(id) ?? [], [id, widthsById]);

  const getClassAndMethodNamesWidth = useCallback((widths: number[]) => {
    return widths[JAVA_FUNCTION_EXPRESSION_VALUES_COLUMN_WIDTH_INDEX] ?? JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH;
  }, []);

  const classAndMethodNamesWidth = useMemo(
    () => getClassAndMethodNamesWidth(widths),
    [getClassAndMethodNamesWidth, widths]
  );

  const setClassAndMethodNamesWidth = useCallback(
    (newWidthAction: React.SetStateAction<number | undefined>) => {
      setWidthsById(({ newMap }) => {
        const prev = newMap.get(id) ?? [];
        const newWidth =
          typeof newWidthAction === "function" ? newWidthAction(getClassAndMethodNamesWidth(prev)) : newWidthAction;

        if (newWidth) {
          const minSize = JAVA_FUNCTION_EXPRESSION_VALUES_COLUMN_WIDTH_INDEX + 1;
          const newValues = [...prev];
          newValues.push(...Array(Math.max(0, minSize - newValues.length)));
          newValues.splice(JAVA_FUNCTION_EXPRESSION_VALUES_COLUMN_WIDTH_INDEX, 1, newWidth);
          newMap.set(id, newValues);
        }
      });
    },
    [getClassAndMethodNamesWidth, id, setWidthsById]
  );

  const parametersColumnHeader = useFunctionExpressionParametersColumnHeader(functionExpression.formalParameter);
  const parametersId = useMemo(
    () => (functionExpression["@_id"] ? `${functionExpression["@_id"]}-parameters` : "parameters"),
    [functionExpression]
  );

  const beeTableColumns = useMemo<ReactTable.Column<JAVA_ROWTYPE>[]>(() => {
    return [
      {
        accessor: expressionHolderId as any, // FIXME: https://github.com/apache/incubator-kie-issues/issues/169
        label: functionExpression["@_label"] ?? DEFAULT_EXPRESSION_VARIABLE_NAME,
        dataType: functionExpression["@_typeRef"] ?? DmnBuiltInDataType.Undefined,
        isRowIndexColumn: false,
        width: undefined,
        columns: [
          {
            headerCellElement: parametersColumnHeader,
            accessor: parametersId as any,
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
                width: classAndMethodNamesWidth,
                setWidth: setClassAndMethodNamesWidth,
                minWidth: JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
              },
            ],
          },
        ],
      },
    ];
  }, [
    expressionHolderId,
    functionExpression,
    classAndMethodNamesWidth,
    parametersColumnHeader,
    setClassAndMethodNamesWidth,
    parametersId,
  ]);

  const headerVisibility = useMemo(() => {
    return isNested ? BeeTableHeaderVisibility.SecondToLastLevel : BeeTableHeaderVisibility.AllLevels;
  }, [isNested]);

  const onColumnUpdates = useCallback(
    ([{ name, typeRef }]: BeeTableColumnUpdate<JAVA_ROWTYPE>[]) => {
      setExpression((prev: BoxedFunctionJava) => {
        // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
        const ret: BoxedFunctionJava = {
          ...prev,
          "@_label": name,
          "@_typeRef": typeRef,
        };
        return ret;
      });
    },
    [setExpression]
  );

  // It is always a Context
  const context = functionExpression.expression! as DMN15__tContext;
  const clazz = getClassContextEntry(context);
  const method = getVariableContextEntry(context);

  const beeTableOperationConfig = useMemo<BeeTableOperationConfig>(() => {
    return [
      {
        group: i18n.terms.selection.toUpperCase(),
        items: [{ name: i18n.terms.copy, type: BeeTableOperation.SelectionCopy }],
      },
      {
        group: i18n.function.toUpperCase(),
        items: [{ name: i18n.rowOperations.reset, type: BeeTableOperation.RowReset }],
      },
    ];
  }, [i18n]);

  const beeTableRows = useMemo<JAVA_ROWTYPE[]>(() => {
    return [
      {
        label: "Class name",
        value: (clazz?.expression as DMN15__tLiteralExpression | undefined)?.text?.__$$text ?? "",
      },
      {
        label: "Method signature",
        value: (method?.expression as DMN15__tLiteralExpression | undefined)?.text?.__$$text ?? "",
      },
    ];
  }, [clazz?.expression, method?.expression]);

  const controllerCell = useFunctionExpressionControllerCell(BoxedFunctionKind.Java);

  const getRowKey = useCallback((r: ReactTable.Row<JAVA_ROWTYPE>) => {
    return r.id;
  }, []);

  const onRowReset = useCallback(() => {
    setExpression((prev: BoxedFunctionJava) => {
      // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
      const ret: BoxedFunctionJava = {
        ...prev,
        expression: undefined!,
      };

      return ret;
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
        width: classAndMethodNamesWidth,
      },
    ],
    [classAndMethodNamesWidth]
  );

  const { onColumnResizingWidthChange, isPivoting, columnResizingWidths } = usePublishedBeeTableResizableColumns(
    functionExpression["@_id"]!,
    columns.length,
    true
  );

  const beeTableRef = React.useRef<BeeTableRef>(null);

  useApportionedColumnWidthsIfNestedTable(
    beeTableRef,
    isPivoting,
    isNested,
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
        const context: DMN15__tContext = functionExpression.expression!;

        const clazz = getClassContextEntry(context) ?? {
          expression: {
            __$$element: "literalExpression",
            "@_id": generateUuid(),
            text: { __$$text: "" },
          },
          variable: {
            "@_name": "class",
          },
        };
        const method = getVariableContextEntry(context) ?? {
          expression: {
            __$$element: "literalExpression",
            "@_id": generateUuid(),
            text: { __$$text: "" },
          },
          variable: {
            "@_name": "method signature",
          },
        };

        // Class
        if (u.rowIndex === 0) {
          setExpression((prev: BoxedFunctionJava) => {
            clazz.expression = {
              ...clazz.expression,
              __$$element: "literalExpression",
              text: {
                __$$text: u.value,
              },
            };

            // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
            const ret: BoxedFunction = {
              ...prev,
              expression: {
                __$$element: "context",
                ...context,
                contextEntry: [clazz, method],
              },
            };

            return ret;
          });
        }
        // Method
        else if (u.rowIndex === 1) {
          setExpression((prev: BoxedFunctionJava) => {
            method.expression = {
              ...method.expression,
              __$$element: "literalExpression",
              "@_id": method.expression["@_id"] ?? generateUuid(),
              text: {
                __$$text: u.value,
              },
            };

            // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
            const ret: BoxedFunction = {
              ...prev,
              expression: {
                __$$element: "context",
                ...context,
                contextEntry: [clazz, method],
              },
            };
            return ret;
          });
        }
      }
    },
    [functionExpression.expression, getClassContextEntry, getVariableContextEntry, setExpression]
  );

  const allowedOperations = useCallback((conditions: BeeTableContextMenuAllowedOperationsConditions) => {
    return [BeeTableOperation.SelectionCopy];
  }, []);

  return (
    <div className={`function-expression ${functionExpression["@_id"]}`}>
      <BeeTable<JAVA_ROWTYPE>
        forwardRef={beeTableRef}
        onColumnResizingWidthChange={onColumnResizingWidthChange}
        resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER}
        operationConfig={beeTableOperationConfig}
        allowedOperations={allowedOperations}
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
