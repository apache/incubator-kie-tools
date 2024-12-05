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
  BeeTableContextMenuAllowedOperationsConditions,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  BeeTableProps,
  BoxedFilter,
  DmnBuiltInDataType,
  ExpressionChangedArgs,
  Normalized,
} from "../../api";
import { BeeTable, BeeTableColumnUpdate } from "../../table/BeeTable";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";
import React, { useCallback, useMemo } from "react";
import { DMN15__tChildExpression } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import * as ReactTable from "react-table";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { DEFAULT_EXPRESSION_VARIABLE_NAME } from "../../expressionVariable/ExpressionVariableMenu";
import { useBoxedExpressionEditor, useBoxedExpressionEditorDispatch } from "../../BoxedExpressionEditorContext";
import { NestedExpressionContainerContext } from "../../resizing/NestedExpressionContainerContext";
import { useNestedExpressionContainerWithNestedExpressions } from "../../resizing/Hooks";
import {
  FILTER_EXPRESSION_EXTRA_WIDTH,
  FILTER_EXPRESSION_MATCH_ROW_EXTRA_WIDTH,
  FILTER_EXPRESSION_MIN_WIDTH,
} from "../../resizing/WidthConstants";
import { FilterExpressionCollectionCell } from "./FilterExpressionCollectionCell";
import { FilterExpressionMatchCell } from "./FilterExpressionMatchCell";
import "./FilterExpression.css";

export type ROWTYPE = Normalized<DMN15__tChildExpression>;

export function FilterExpressionComponent({
  isNested,
  parentElementId,
  expression: filterExpression,
}: {
  expression: Normalized<BoxedFilter>;
  isNested: boolean;
  parentElementId: string;
}) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { expressionHolderId, widthsById, isReadOnly } = useBoxedExpressionEditor();
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    return [
      {
        accessor: expressionHolderId as any, // FIXME: https://github.com/kiegroup/kie-issues/issues/169
        label: filterExpression["@_label"] ?? DEFAULT_EXPRESSION_VARIABLE_NAME,
        dataType: filterExpression["@_typeRef"] ?? DmnBuiltInDataType.Undefined,
        isRowIndexColumn: false,
        minWidth: FILTER_EXPRESSION_MIN_WIDTH,
        width: undefined,
      },
    ];
  }, [filterExpression, expressionHolderId]);

  const headerVisibility = useMemo(() => {
    return isNested ? BeeTableHeaderVisibility.None : BeeTableHeaderVisibility.AllLevels;
  }, [isNested]);

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

  const tableRows = useMemo(() => {
    return [filterExpression.in];
  }, [filterExpression.in]);

  const allowedOperations = useCallback((conditions: BeeTableContextMenuAllowedOperationsConditions) => {
    if (!conditions.selection.selectionStart || !conditions.selection.selectionEnd) {
      return [];
    }

    return [
      BeeTableOperation.SelectionCopy,
      ...(conditions.selection.selectionStart.columnIndex > 1
        ? [BeeTableOperation.SelectionCut, BeeTableOperation.SelectionPaste, BeeTableOperation.SelectionReset]
        : []),
    ];
  }, []);

  const { nestedExpressionContainerValue, onColumnResizingWidthChange } =
    useNestedExpressionContainerWithNestedExpressions(
      useMemo(() => {
        return {
          nestedExpressions: [filterExpression.in.expression, filterExpression.match.expression],
          fixedColumnActualWidth: 0,
          fixedColumnResizingWidth: { value: 0, isPivoting: false },
          fixedColumnMinWidth: 0,
          nestedExpressionMinWidth: FILTER_EXPRESSION_MIN_WIDTH,
          extraWidth: FILTER_EXPRESSION_EXTRA_WIDTH,
          expression: filterExpression,
          flexibleColumnIndex: 1,
          widthsById: widthsById,
          nestedExpressionsExtraWidths: new Map([
            [filterExpression.match.expression?.["@_id"] ?? "", FILTER_EXPRESSION_MATCH_ROW_EXTRA_WIDTH],
          ]),
        };
      }, [filterExpression, widthsById])
    );

  const cellComponentByColumnAccessor: BeeTableProps<ROWTYPE>["cellComponentByColumnAccessor"] = useMemo(
    () => ({
      [expressionHolderId]: (props) => <FilterExpressionCollectionCell {...props} parentElementId={parentElementId} />,
    }),
    [expressionHolderId, parentElementId]
  );

  const beeTableAdditionalRow = useMemo(() => {
    return [
      <FilterExpressionMatchCell
        key={"filter-expression-match-cell"}
        rowIndex={1}
        columnIndex={1}
        parentElementId={parentElementId}
        data={[filterExpression.match]}
        columnId={"filterExpressionColumn"}
      />,
    ];
  }, [filterExpression.match, parentElementId]);

  const onColumnUpdates = useCallback(
    ([{ name, typeRef }]: BeeTableColumnUpdate<ROWTYPE>[]) => {
      const expressionChangedArgs: ExpressionChangedArgs = {
        action: Action.VariableChanged,
        variableUuid: expressionHolderId,
        typeChange:
          typeRef !== filterExpression["@_typeRef"]
            ? {
                from: filterExpression["@_typeRef"] ?? "",
                to: typeRef,
              }
            : undefined,
        nameChange:
          name !== filterExpression["@_label"]
            ? {
                from: filterExpression["@_label"] ?? "",
                to: name,
              }
            : undefined,
      };

      setExpression({
        setExpressionAction: (prev: Normalized<BoxedFilter>) => {
          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedFilter> = {
            ...prev,
            "@_label": name,
            "@_typeRef": typeRef,
          };

          return ret;
        },
        expressionChangedArgs,
      });
    },
    [expressionHolderId, filterExpression, setExpression]
  );

  return (
    <NestedExpressionContainerContext.Provider value={nestedExpressionContainerValue}>
      <BeeTable<ROWTYPE>
        isReadOnly={isReadOnly}
        isEditableHeader={!isReadOnly}
        onColumnResizingWidthChange={onColumnResizingWidthChange}
        resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER}
        tableId={filterExpression["@_id"]}
        headerVisibility={headerVisibility}
        cellComponentByColumnAccessor={cellComponentByColumnAccessor}
        columns={beeTableColumns}
        rows={tableRows}
        operationConfig={beeTableOperationConfig}
        allowedOperations={allowedOperations}
        onColumnUpdates={onColumnUpdates}
        shouldRenderRowIndexColumn={false}
        shouldShowRowsInlineControls={false}
        shouldShowColumnsInlineControls={false}
        additionalRow={beeTableAdditionalRow}
      />
    </NestedExpressionContainerContext.Provider>
  );
}
