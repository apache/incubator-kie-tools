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
  BoxedConditional,
  DmnBuiltInDataType,
  ExpressionChangedArgs,
  generateUuid,
  Normalized,
} from "../../api";
import { BeeTable, BeeTableColumnUpdate } from "../../table/BeeTable";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";
import React, { useCallback, useMemo } from "react";
import { DMN15__tChildExpression } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import * as ReactTable from "react-table";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { BeeTableReadOnlyCell } from "../../table/BeeTable/BeeTableReadOnlyCell";
import { ConditionalExpressionCell } from "./ConditionalExpressionCell";
import { DEFAULT_EXPRESSION_VARIABLE_NAME } from "../../expressionVariable/ExpressionVariableMenu";
import { useBoxedExpressionEditor, useBoxedExpressionEditorDispatch } from "../../BoxedExpressionEditorContext";
import { NestedExpressionContainerContext } from "../../resizing/NestedExpressionContainerContext";
import { useNestedExpressionContainerWithNestedExpressions } from "../../resizing/Hooks";
import {
  CONDITIONAL_EXPRESSION_CLAUSE_COLUMN_MIN_WIDTH,
  CONDITIONAL_EXPRESSION_EXTRA_WIDTH,
  CONDITIONAL_EXPRESSION_LABEL_COLUMN_WIDTH,
} from "../../resizing/WidthConstants";

export type ROWTYPE = Normalized<ConditionalClause>;

export type ConditionalClause = {
  part: DMN15__tChildExpression;
  label: string;
};

export function ConditionalExpression({
  isNested,
  parentElementId,
  expression: conditionalExpression,
}: {
  expression: Normalized<BoxedConditional>;
  isNested: boolean;
  parentElementId: string;
}) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { expressionHolderId, widthsById, isReadOnly } = useBoxedExpressionEditor();
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const tableRows = useMemo<ROWTYPE[]>(() => {
    return [
      { label: "if", part: conditionalExpression.if },
      { label: "then", part: conditionalExpression.then },
      { label: "else", part: conditionalExpression.else },
    ];
  }, [conditionalExpression.else, conditionalExpression.if, conditionalExpression.then]);

  const cellComponentByColumnAccessor: BeeTableProps<ROWTYPE>["cellComponentByColumnAccessor"] = useMemo(() => {
    return {
      label: (props) => {
        return <BeeTableReadOnlyCell value={props.data[props.rowIndex].label} />;
      },
      part: (props) => {
        return <ConditionalExpressionCell {...props} parentElementId={parentElementId} />;
      },
    };
  }, [parentElementId]);
  const id = conditionalExpression["@_id"]!;

  const tableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    return [
      {
        accessor: expressionHolderId as any, // FIXME: https://github.com/kiegroup/kie-issues/issues/169
        label: conditionalExpression["@_label"] ?? DEFAULT_EXPRESSION_VARIABLE_NAME,
        isRowIndexColumn: false,
        dataType: conditionalExpression["@_typeRef"] ?? DmnBuiltInDataType.Undefined,
        width: undefined,
        columns: [
          {
            accessor: "label",
            label: "label",
            width: CONDITIONAL_EXPRESSION_LABEL_COLUMN_WIDTH,
            minWidth: CONDITIONAL_EXPRESSION_LABEL_COLUMN_WIDTH,
            isInlineEditable: false,
            isRowIndexColumn: false,
            isWidthPinned: true,
            isWidthConstant: true,
            dataType: undefined as any,
          },
          {
            accessor: "part",
            label: "part",
            width: undefined,
            minWidth: CONDITIONAL_EXPRESSION_CLAUSE_COLUMN_MIN_WIDTH,
            isInlineEditable: false,
            isRowIndexColumn: false,
            dataType: undefined as any,
          },
        ],
      },
    ];
  }, [conditionalExpression, expressionHolderId]);

  const headerVisibility = useMemo(() => {
    return isNested ? BeeTableHeaderVisibility.None : BeeTableHeaderVisibility.SecondToLastLevel;
  }, [isNested]);

  /// //////////////////////////////////////////////////////
  /// ///////////// RESIZING WIDTHS ////////////////////////
  /// //////////////////////////////////////////////////////

  const { nestedExpressionContainerValue, onColumnResizingWidthChange: onColumnResizingWidthChange } =
    useNestedExpressionContainerWithNestedExpressions(
      useMemo(() => {
        const nestedExpressions = [
          conditionalExpression.if.expression,
          conditionalExpression.then.expression,
          conditionalExpression.else.expression,
        ];

        return {
          nestedExpressions: nestedExpressions,
          fixedColumnActualWidth: CONDITIONAL_EXPRESSION_LABEL_COLUMN_WIDTH,
          fixedColumnResizingWidth: { value: CONDITIONAL_EXPRESSION_LABEL_COLUMN_WIDTH, isPivoting: false },
          fixedColumnMinWidth: CONDITIONAL_EXPRESSION_LABEL_COLUMN_WIDTH,
          nestedExpressionMinWidth: CONDITIONAL_EXPRESSION_CLAUSE_COLUMN_MIN_WIDTH,
          extraWidth: CONDITIONAL_EXPRESSION_EXTRA_WIDTH,
          expression: conditionalExpression,
          flexibleColumnIndex: 2,
          widthsById: widthsById,
        };
      }, [conditionalExpression, widthsById])
    );

  /// //////////////////////////////////////////////////////

  const allowedOperations = useCallback(() => {
    return [BeeTableOperation.SelectionCopy, BeeTableOperation.RowReset];
  }, []);

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

  const onRowReset = useCallback(
    (args: { rowIndex: number }) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedConditional>) => {
          if (args.rowIndex === 0) {
            // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
            const ret: Normalized<BoxedConditional> = {
              ...prev,
              if: {
                "@_id": generateUuid(),
                expression: undefined!,
              }, // SPEC DISCREPANCY
            };
            return ret;
          } else if (args.rowIndex === 1) {
            // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
            const ret: Normalized<BoxedConditional> = {
              ...prev,
              then: {
                "@_id": generateUuid(),
                expression: undefined!,
              }, // SPEC DISCREPANCY
            };
            return ret;
          } else if (args.rowIndex === 2) {
            // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
            const ret: Normalized<BoxedConditional> = {
              ...prev,
              else: {
                "@_id": generateUuid(),
                expression: undefined!,
              }, // SPEC DISCREPANCY
            };
            return ret;
          } else {
            throw new Error("ConditionalExpression shouldn't have more than 3 rows.");
          }
        },
        expressionChangedArgs: { action: Action.RowReset, rowIndex: args.rowIndex },
      });
    },
    [setExpression]
  );

  const onColumnUpdates = useCallback(
    ([{ name, typeRef }]: BeeTableColumnUpdate<ROWTYPE>[]) => {
      const expressionChangedArgs: ExpressionChangedArgs = {
        action: Action.VariableChanged,
        variableUuid: expressionHolderId,
        typeChange:
          typeRef !== conditionalExpression["@_typeRef"]
            ? {
                from: conditionalExpression["@_typeRef"] ?? "",
                to: typeRef,
              }
            : undefined,
        nameChange:
          name !== conditionalExpression["@_label"]
            ? {
                from: conditionalExpression["@_label"] ?? "",
                to: name,
              }
            : undefined,
      };

      setExpression({
        setExpressionAction: (prev: Normalized<BoxedConditional>) => {
          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedConditional> = {
            ...prev,
            "@_label": name,
            "@_typeRef": typeRef,
          };

          return ret;
        },
        expressionChangedArgs,
      });
    },
    [conditionalExpression, expressionHolderId, setExpression]
  );

  const getRowKey = useCallback((row: ReactTable.Row<ROWTYPE>) => {
    return row.original.part["@_id"];
  }, []);

  const supportsEvaluationHitsCount = useCallback((row: ReactTable.Row<ROWTYPE>) => {
    return row.original.label !== "if";
  }, []);

  return (
    <NestedExpressionContainerContext.Provider value={nestedExpressionContainerValue}>
      <div className={"conditional-expression"} data-testid={"kie-tools--boxed-expression-component---conditional"}>
        <BeeTable<ROWTYPE>
          isReadOnly={isReadOnly}
          isEditableHeader={!isReadOnly}
          resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER}
          tableId={id}
          getRowKey={getRowKey}
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
          supportsEvaluationHitsCount={supportsEvaluationHitsCount}
        />
      </div>
    </NestedExpressionContainerContext.Provider>
  );
}
