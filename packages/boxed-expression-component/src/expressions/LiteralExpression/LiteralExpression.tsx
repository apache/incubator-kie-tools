/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import { useCallback, useEffect, useMemo, useRef } from "react";
import * as ReactTable from "react-table";
import { BeeTableHeaderVisibility, LiteralExpressionDefinition } from "../../api";
import { useNestedExpressionContainer } from "../../resizing/NestedExpressionContainerContext";
import { LITERAL_EXPRESSION_EXTRA_WIDTH, LITERAL_EXPRESSION_MIN_WIDTH } from "../../resizing/WidthConstants";
import { BeeTable, BeeTableCellUpdate, BeeTableColumnUpdate, BeeTableRef } from "../../table/BeeTable";
import { usePublishedBeeTableResizableColumns } from "../../resizing/BeeTableResizableColumnsContextProvider";
import { useBeeTableCoordinates, useBeeTableSelectableCellRef } from "../../selection/BeeTableSelectionContext";
import { useBoxedExpressionEditorDispatch } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import "./LiteralExpression.css";
import { DEFAULT_EXPRESSION_NAME } from "../ExpressionDefinitionHeaderMenu";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";

type ROWTYPE = any;

export function LiteralExpression(literalExpression: LiteralExpressionDefinition & { isNested: boolean }) {
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const getValue = useCallback(() => {
    return literalExpression.content ?? "";
  }, [literalExpression.content]);

  const setValue = useCallback(
    (value: string) => {
      setExpression((prev) => ({ ...prev, content: value }));
    },
    [setExpression]
  );

  const { containerCellCoordinates } = useBeeTableCoordinates();
  useBeeTableSelectableCellRef(
    containerCellCoordinates?.rowIndex ?? 0,
    containerCellCoordinates?.columnIndex ?? 0,
    setValue,
    getValue
  );

  const onColumnUpdates = useCallback(
    ([{ name, dataType }]: BeeTableColumnUpdate<ROWTYPE>[]) => {
      setExpression((prev) => ({
        ...prev,
        name,
        dataType,
      }));
    },
    [setExpression]
  );

  const setWidth = useCallback(
    (newWidthAction: React.SetStateAction<number | undefined>) => {
      setExpression((prev: LiteralExpressionDefinition) => {
        const newWidth = typeof newWidthAction === "function" ? newWidthAction(prev.width) : newWidthAction;
        return { ...prev, width: newWidth };
      });
    },
    [setExpression]
  );

  const onCellUpdates = useCallback(
    (cellUpdates: BeeTableCellUpdate<ROWTYPE>[]) => {
      setValue(cellUpdates[0].value);
    },
    [setValue]
  );

  /// //////////////////////////////////////////////////////
  /// ///////////// RESIZING WIDTHS ////////////////////////
  /// //////////////////////////////////////////////////////

  const { onColumnResizingWidthChange, isPivoting } = usePublishedBeeTableResizableColumns(literalExpression.id, 1);
  const nestedExpressionContainer = useNestedExpressionContainer();

  const minWidth = useMemo(() => {
    return Math.max(
      nestedExpressionContainer.minWidth - LITERAL_EXPRESSION_EXTRA_WIDTH, //
      LITERAL_EXPRESSION_MIN_WIDTH
    );
  }, [nestedExpressionContainer]);

  const beeTableRef = useRef<BeeTableRef>(null);

  useEffect(() => {
    if (isPivoting || !literalExpression.isNested) {
      return;
    }

    const COLUMN_INDEX = 1;
    beeTableRef.current?.updateColumnResizingWidth(COLUMN_INDEX, (prev) => {
      return {
        value: Math.max(nestedExpressionContainer.resizingWidth.value - LITERAL_EXPRESSION_EXTRA_WIDTH, minWidth),
        isPivoting: prev?.isPivoting ?? false,
      };
    });
  }, [isPivoting, literalExpression.isNested, minWidth, nestedExpressionContainer.resizingWidth.value]);

  /// //////////////////////////////////////////////////////

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    return [
      {
        accessor: "literal-expression" as any, // FIXME: Tiago -> No bueno.
        label: literalExpression.name ?? DEFAULT_EXPRESSION_NAME,
        isRowIndexColumn: false,
        dataType: literalExpression.dataType,
        minWidth,
        width: literalExpression.width ?? LITERAL_EXPRESSION_MIN_WIDTH,
        setWidth,
      },
    ];
  }, [literalExpression.dataType, literalExpression.name, literalExpression.width, minWidth, setWidth]);

  const beeTableRows = useMemo<ROWTYPE[]>(() => {
    return [{ "literal-expression": literalExpression.content ?? "" }];
  }, [literalExpression]);

  const beeTableHeaderVisibility = useMemo(() => {
    return literalExpression.isNested ? BeeTableHeaderVisibility.None : BeeTableHeaderVisibility.AllLevels;
  }, [literalExpression.isNested]);

  const getColumnKey = useCallback((column: ReactTable.Column<ROWTYPE>) => {
    return column.label;
  }, []);

  const getRowKey = useCallback((row: ReactTable.Row<ROWTYPE>) => {
    return row.id;
  }, []);

  const beeTableOperationConfig = useMemo(() => {
    return [];
  }, []);

  return (
    <div className={`literal-expression`}>
      <div className={"literal-expression-body-container"}>
        <div className={"equals-sign"}>{`=`}</div>
        <BeeTable
          resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER}
          forwardRef={beeTableRef}
          getColumnKey={getColumnKey}
          getRowKey={getRowKey}
          columns={beeTableColumns}
          rows={beeTableRows}
          headerVisibility={beeTableHeaderVisibility}
          onColumnUpdates={onColumnUpdates}
          onCellUpdates={onCellUpdates}
          operationConfig={beeTableOperationConfig}
          onColumnResizingWidthChange={onColumnResizingWidthChange}
          shouldRenderRowIndexColumn={false}
          shouldShowRowsInlineControls={false}
          shouldShowColumnsInlineControls={false}
        ></BeeTable>
      </div>
    </div>
  );
}
