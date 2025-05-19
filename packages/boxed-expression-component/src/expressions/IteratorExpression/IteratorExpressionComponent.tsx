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

import {
  Action,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  BeeTableProps,
  BoxedFor,
  BoxedIterator,
  DmnBuiltInDataType,
  ExpressionChangedArgs,
  generateUuid,
  Normalized,
} from "../../api";
import { BeeTable, BeeTableColumnUpdate, BeeTableRef } from "../../table/BeeTable";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";
import React, { useCallback, useMemo, useRef } from "react";
import {
  DMN15__tChildExpression,
  DMN15__tTypedChildExpression,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import * as ReactTable from "react-table";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { BeeTableReadOnlyCell } from "../../table/BeeTable/BeeTableReadOnlyCell";
import { IteratorExpressionCell } from "./IteratorExpressionCell";

import { useBoxedExpressionEditor, useBoxedExpressionEditorDispatch } from "../../BoxedExpressionEditorContext";
import { NestedExpressionContainerContext } from "../../resizing/NestedExpressionContainerContext";
import { useNestedExpressionContainerWithNestedExpressions } from "../../resizing/Hooks";
import {
  ITERATOR_EXPRESSION_CLAUSE_COLUMN_MIN_WIDTH,
  ITERATOR_EXPRESSION_EXTRA_WIDTH,
  ITERATOR_EXPRESSION_LABEL_COLUMN_WIDTH,
} from "../../resizing/WidthConstants";
import { DEFAULT_EXPRESSION_VARIABLE_NAME } from "../../expressionVariable/ExpressionVariableMenu";
import { IteratorExpressionVariableCell } from "./IteratorExpressionVariableCell";

type ROWTYPE = Normalized<IteratorClause>;

export type IteratorClause = {
  child: DMN15__tTypedChildExpression | DMN15__tChildExpression | string | undefined;
  label: string;
};

export function IteratorExpressionComponent({
  isNested,
  parentElementId,
  expression: expression,
}: {
  expression: Normalized<BoxedIterator>;
  isNested: boolean;
  parentElementId: string;
}) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { expressionHolderId, widthsById, isReadOnly } = useBoxedExpressionEditor();
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const id = expression["@_id"]!;

  const tableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    return [
      {
        accessor: expressionHolderId as any, // FIXME: https://github.com/kiegroup/kie-issues/issues/169
        label: expression["@_label"] ?? DEFAULT_EXPRESSION_VARIABLE_NAME,
        isRowIndexColumn: false,
        dataType: expression["@_typeRef"] ?? DmnBuiltInDataType.Undefined,
        width: undefined,
        columns: [
          {
            accessor: "label",
            label: "label",
            width: ITERATOR_EXPRESSION_LABEL_COLUMN_WIDTH,
            minWidth: ITERATOR_EXPRESSION_LABEL_COLUMN_WIDTH,
            isInlineEditable: false,
            isRowIndexColumn: false,
            isWidthPinned: true,
            isWidthConstant: true,
            dataType: undefined as any,
          },
          {
            accessor: "child",
            label: "child",
            width: undefined,
            minWidth: ITERATOR_EXPRESSION_CLAUSE_COLUMN_MIN_WIDTH,
            isInlineEditable: false,
            isRowIndexColumn: false,
            dataType: undefined as any,
          },
        ],
      },
    ];
  }, [expression, expressionHolderId]);

  const headerVisibility = useMemo(() => {
    return isNested ? BeeTableHeaderVisibility.None : BeeTableHeaderVisibility.SecondToLastLevel;
  }, [isNested]);

  const beeTableOperationConfig = useMemo<BeeTableOperationConfig>(() => {
    return [
      {
        group: i18n.contextEntry,
        items: [{ name: i18n.rowOperations.reset, type: BeeTableOperation.RowReset }],
      },
      {
        group: i18n.terms.selection.toUpperCase(),
        items: [{ name: i18n.terms.copy, type: BeeTableOperation.SelectionCopy }],
      },
    ];
  }, [i18n]);

  const getIterableRowLabel = useCallback(
    (rowNumber: number) => {
      if (rowNumber === 0) {
        if (expression.__$$element === "for") {
          return "for";
        } else if (expression.__$$element === "some") {
          return "some";
        } else if (expression.__$$element === "every") {
          return "every";
        } else {
          throw new Error("Unknown IteratorExpression element");
        }
      } else if (rowNumber === 1) {
        return "in";
      } else if (rowNumber === 2) {
        if (expression.__$$element === "for") {
          return "return";
        } else if (expression.__$$element === "some" || expression.__$$element === "every") {
          return "satisfies";
        } else {
          throw new Error("Unknown IteratorExpression element");
        }
      } else {
        throw new Error("IteratorExpression can't have more than 3 rows.");
      }
    },
    [expression.__$$element]
  );

  const getIterableRowElement = useCallback(
    (rowNumber: number) => {
      if (rowNumber === 0) {
        return expression["@_iteratorVariable"] ?? "";
      } else if (rowNumber === 1) {
        return expression.in;
      } else {
        switch (expression.__$$element) {
          case "for":
            return expression.return;
          case "every":
          case "some":
            return expression.satisfies;
        }
      }
    },
    [expression]
  );

  const tableRows = useMemo(() => {
    return [
      { label: getIterableRowLabel(0), child: getIterableRowElement(0) },
      { label: getIterableRowLabel(1), child: getIterableRowElement(1) },
      { label: getIterableRowLabel(2), child: getIterableRowElement(2) },
    ];
  }, [getIterableRowElement, getIterableRowLabel]);

  const allowedOperations = useCallback(() => {
    return [BeeTableOperation.SelectionCopy, BeeTableOperation.RowReset];
  }, []);

  const beeTableRef = useRef<BeeTableRef>(null);

  const cellComponentByColumnAccessor: BeeTableProps<ROWTYPE>["cellComponentByColumnAccessor"] = useMemo(() => {
    return {
      label: (props) => {
        return <BeeTableReadOnlyCell value={props.data[props.rowIndex].label} />;
      },
      child: (props) => {
        if (props.rowIndex === 0) {
          return (
            <IteratorExpressionVariableCell
              data={props.data}
              rowIndex={props.rowIndex}
              columnIndex={props.columnIndex}
              currentElementId={id}
              beeTableRef={beeTableRef}
            />
          );
        } else if (props.rowIndex === 1 || props.rowIndex === 2) {
          return (
            <IteratorExpressionCell
              iteratorClause={props.data[props.rowIndex]}
              {...props}
              parentElementId={parentElementId}
            />
          );
        } else {
          throw new Error("IteratorExpression can't have more than 3 rows.");
        }
      },
    };
  }, [id, parentElementId]);

  const { nestedExpressionContainerValue, onColumnResizingWidthChange } =
    useNestedExpressionContainerWithNestedExpressions(
      useMemo(() => {
        const nestedExpressions = [
          expression.in.expression,
          expression.__$$element === "for" ? expression.return.expression : expression.satisfies.expression,
        ];

        return {
          nestedExpressions: nestedExpressions,
          fixedColumnActualWidth: ITERATOR_EXPRESSION_LABEL_COLUMN_WIDTH,
          fixedColumnResizingWidth: { value: ITERATOR_EXPRESSION_LABEL_COLUMN_WIDTH, isPivoting: false },
          fixedColumnMinWidth: ITERATOR_EXPRESSION_LABEL_COLUMN_WIDTH,
          nestedExpressionMinWidth: ITERATOR_EXPRESSION_CLAUSE_COLUMN_MIN_WIDTH,
          extraWidth: ITERATOR_EXPRESSION_EXTRA_WIDTH,
          expression: expression,
          flexibleColumnIndex: 2,
          widthsById: widthsById,
        };
      }, [expression, widthsById])
    );

  const onColumnUpdates = useCallback(
    ([{ name, typeRef }]: BeeTableColumnUpdate<ROWTYPE>[]) => {
      const expressionChangedArgs: ExpressionChangedArgs = {
        action: Action.VariableChanged,
        variableUuid: expressionHolderId,
        typeChange:
          typeRef !== expression["@_typeRef"]
            ? {
                from: expression["@_typeRef"] ?? "",
                to: typeRef,
              }
            : undefined,
        nameChange:
          name !== expression["@_label"]
            ? {
                from: expression["@_label"] ?? "",
                to: name,
              }
            : undefined,
      };

      setExpression({
        setExpressionAction: (prev: Normalized<BoxedIterator>) => {
          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedIterator> = {
            ...prev,
            "@_label": name,
            "@_typeRef": typeRef,
          };

          return ret;
        },
        expressionChangedArgs,
      });
    },
    [expression, expressionHolderId, setExpression]
  );
  const onRowReset = useCallback(
    (args: { rowIndex: number }) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedIterator>) => {
          if (args.rowIndex === 0) {
            // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
            const ret: Normalized<BoxedIterator> = {
              ...prev,
              "@_iteratorVariable": undefined,
            };
            return ret;
          } else if (args.rowIndex === 1) {
            // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
            const ret: Normalized<BoxedIterator> = {
              ...prev,
              in: {
                "@_id": generateUuid(),
                expression: undefined!,
              }, // SPEC DISCREPANCY
            };
            return ret;
          } else if (args.rowIndex === 2) {
            if (prev.__$$element === "for") {
              // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
              const ret: Normalized<BoxedFor> = {
                ...prev,
                return: {
                  "@_id": generateUuid(),
                  expression: undefined!,
                }, // SPEC DISCREPANCY
              };
              return ret;
            } else if (prev.__$$element === "some" || prev.__$$element === "every") {
              // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
              const iterator: Normalized<BoxedIterator> = {
                ...prev,
                satisfies: {
                  "@_id": generateUuid(),
                  expression: undefined!,
                }, // SPEC DISCREPANCY
              };
              return iterator;
            } else {
              throw new Error("Nested expression type not supported in IteratorExpression.");
            }
          } else {
            throw new Error("IteratorExpression shouldn't have more than 3 rows.");
          }
        },
        expressionChangedArgs: { action: Action.RowReset, rowIndex: args.rowIndex },
      });
    },
    [setExpression]
  );

  return (
    <NestedExpressionContainerContext.Provider value={nestedExpressionContainerValue}>
      <div>
        <BeeTable<ROWTYPE>
          isReadOnly={isReadOnly}
          isEditableHeader={!isReadOnly}
          forwardRef={beeTableRef}
          resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER}
          tableId={id}
          headerLevelCountForAppendingRowIndexColumn={1}
          headerVisibility={headerVisibility}
          cellComponentByColumnAccessor={cellComponentByColumnAccessor}
          columns={tableColumns}
          rows={tableRows}
          operationConfig={beeTableOperationConfig}
          allowedOperations={allowedOperations}
          onColumnUpdates={onColumnUpdates}
          onRowReset={onRowReset}
          onColumnResizingWidthChange={onColumnResizingWidthChange}
          shouldRenderRowIndexColumn={false}
          shouldShowRowsInlineControls={false}
          shouldShowColumnsInlineControls={false}
        />
      </div>
    </NestedExpressionContainerContext.Provider>
  );
}
