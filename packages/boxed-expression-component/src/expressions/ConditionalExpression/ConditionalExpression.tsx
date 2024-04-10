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
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  BeeTableProps,
  BoxedConditional,
  BoxedContext,
  BoxedList,
  DmnBuiltInDataType,
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
import { getExpressionTotalMinWidth } from "../../resizing/WidthMaths";

export type ROWTYPE = ConditionalClause;

export type ConditionalClause = {
  part: DMN15__tChildExpression;
  label: string;
};

export function ConditionalExpression(
  conditionalExpression: BoxedConditional & {
    isNested: boolean;
    parentElementId: string;
  }
) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { expressionHolderId, widthsById } = useBoxedExpressionEditor();
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
        return <ConditionalExpressionCell {...props} parentElementId={conditionalExpression.parentElementId} />;
      },
    };
  }, [conditionalExpression.parentElementId]);
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
    return conditionalExpression.isNested ? BeeTableHeaderVisibility.None : BeeTableHeaderVisibility.SecondToLastLevel;
  }, [conditionalExpression.isNested]);

  const { nestedExpressionContainerValue, onColumnResizingWidthChange: onColumnResizingWidthChange } =
    useNestedExpressionContainerWithNestedExpressions(
      useMemo(() => {
        const nestedExpressions = [
          conditionalExpression.if.expression,
          conditionalExpression.then.expression,
          conditionalExpression.else.expression,
        ];

        const entriesWidths = nestedExpressions.map((e) => getExpressionTotalMinWidth(0, e, widthsById));

        const maxNestedExpressionMinWidth = Math.max(...entriesWidths, CONDITIONAL_EXPRESSION_CLAUSE_COLUMN_MIN_WIDTH);

        return {
          nestedExpressions: nestedExpressions,
          fixedColumnActualWidth: CONDITIONAL_EXPRESSION_LABEL_COLUMN_WIDTH,
          fixedColumnResizingWidth: { value: CONDITIONAL_EXPRESSION_LABEL_COLUMN_WIDTH, isPivoting: false },
          fixedColumnMinWidth: CONDITIONAL_EXPRESSION_LABEL_COLUMN_WIDTH,
          nestedExpressionMinWidth: maxNestedExpressionMinWidth,
          extraWidth: CONDITIONAL_EXPRESSION_EXTRA_WIDTH,
          expression: conditionalExpression,
          flexibleColumnIndex: 2,
          widthsById: widthsById,
        };
      }, [conditionalExpression, widthsById])
    );

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
      setExpression((prev: BoxedConditional) => {
        if (args.rowIndex === 0) {
          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: BoxedConditional = {
            ...prev,
            if: { expression: undefined! }, // SPEC DISCREPANCY
          };
          return ret;
        } else if (args.rowIndex === 1) {
          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: BoxedConditional = {
            ...prev,
            then: { expression: undefined! }, // SPEC DISCREPANCY
          };
          return ret;
        } else if (args.rowIndex === 2) {
          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: BoxedConditional = {
            ...prev,
            else: { expression: undefined! }, // SPEC DISCREPANCY
          };
          return ret;
        } else {
          throw new Error("ConditionalExpression shouldn't have more than 3 rows.");
        }
      });
    },
    [setExpression]
  );

  const onColumnUpdates = useCallback(
    ([{ name, typeRef }]: BeeTableColumnUpdate<ROWTYPE>[]) => {
      setExpression((prev: BoxedConditional) => {
        // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
        const ret: BoxedConditional = {
          ...prev,
          "@_label": name,
          "@_typeRef": typeRef,
        };

        return ret;
      });
    },
    [setExpression]
  );

  return (
    <NestedExpressionContainerContext.Provider value={nestedExpressionContainerValue}>
      <div>
        <BeeTable<ROWTYPE>
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
