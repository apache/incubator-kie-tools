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
import { LITERAL_EXPRESSION_EXTRA_WIDTH, LITERAL_EXPRESSION_MIN_WIDTH } from "../../resizing/WidthValues";
import { BeeTable, BeeTableCellUpdate, BeeTableColumnUpdate, BeeTableRef } from "../../table/BeeTable";
import { usePublishedBeeTableColumnResizingWidths } from "../../table/BeeTable/BeeTableColumnResizingWidthsContextProvider";
import { useBeeTableCoordinates, useBeeTableCell } from "../../table/BeeTable/BeeTableSelectionContext";
import { useBoxedExpressionEditorDispatch } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import "./LiteralExpression.css";

type ROWTYPE = any;

export function LiteralExpression(literalExpression: LiteralExpressionDefinition & { isHeadless: boolean }) {
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
  useBeeTableCell(
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
    (width) => {
      setExpression((prev) => ({ ...prev, width }));
    },
    [setExpression]
  );

  const onCellUpdates = useCallback(
    (cellUpdates: BeeTableCellUpdate<ROWTYPE>[]) => {
      setValue(cellUpdates[0].value);
    },
    [setValue]
  );

  //// RESIZING WIDTH

  const { onColumnResizingWidthChange, isPivoting } = usePublishedBeeTableColumnResizingWidths(literalExpression.id!);
  const nestedExpressionContainer = useNestedExpressionContainer();

  const minWidth = useMemo(() => {
    return Math.max(
      nestedExpressionContainer.minWidth - LITERAL_EXPRESSION_EXTRA_WIDTH, //
      LITERAL_EXPRESSION_MIN_WIDTH
    );
  }, [nestedExpressionContainer]);

  const beeTableRef = useRef<BeeTableRef>(null);

  useEffect(() => {
    if (isPivoting) {
      return;
    }

    const COLUMN_INDEX = 1;
    beeTableRef.current?.updateResizingWidth(COLUMN_INDEX, (prev) => {
      return {
        value: Math.max(nestedExpressionContainer.resizingWidth.value - LITERAL_EXPRESSION_EXTRA_WIDTH, minWidth),
        isPivoting: prev?.isPivoting ?? false,
      };
    });
  }, [isPivoting, minWidth, nestedExpressionContainer.resizingWidth.value]);

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    return [
      {
        accessor: "literal-expression" as any, // FIXME: Tiago -> No bueno.
        label: literalExpression.name ?? "Expression Name",
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
    return literalExpression.isHeadless ? BeeTableHeaderVisibility.None : BeeTableHeaderVisibility.AllLevels;
  }, [literalExpression.isHeadless]);

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
