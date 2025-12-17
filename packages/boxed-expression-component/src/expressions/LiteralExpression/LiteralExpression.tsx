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

import * as React from "react";
import { useCallback, useEffect, useMemo, useRef } from "react";
import * as ReactTable from "react-table";
import {
  Action,
  BeeTableContextMenuAllowedOperationsConditions,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BoxedLiteral,
  DmnBuiltInDataType,
  ExpressionChangedArgs,
  Normalized,
} from "../../api";
import { useNestedExpressionContainer } from "../../resizing/NestedExpressionContainerContext";
import {
  LITERAL_EXPRESSION_EXTRA_WIDTH,
  LITERAL_EXPRESSION_MIN_WIDTH,
  LITERAL_EXPRESSION_WIDTH_INDEX,
} from "../../resizing/WidthConstants";
import { BeeTable, BeeTableCellUpdate, BeeTableColumnUpdate, BeeTableRef } from "../../table/BeeTable";
import { usePublishedBeeTableResizableColumns } from "../../resizing/BeeTableResizableColumnsContext";
import { useBeeTableCoordinates, useBeeTableSelectableCellRef } from "../../selection/BeeTableSelectionContext";
import { useBoxedExpressionEditor, useBoxedExpressionEditorDispatch } from "../../BoxedExpressionEditorContext";
import { DEFAULT_EXPRESSION_VARIABLE_NAME } from "../../expressionVariable/ExpressionVariableMenu";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import "./LiteralExpression.css";

type ROWTYPE = any; // FIXME: https://github.com/kiegroup/kie-issues/issues/169

export function LiteralExpression({
  isNested,
  expression: literalExpression,
}: {
  expression: Normalized<BoxedLiteral>;
  isNested: boolean;
}) {
  const { setExpression, setWidthsById } = useBoxedExpressionEditorDispatch();
  const { expressionHolderId, widthsById, isReadOnly } = useBoxedExpressionEditor();

  const id = literalExpression["@_id"]!;

  const getValue = useCallback(() => {
    return literalExpression.text?.__$$text ?? "";
  }, [literalExpression.text]);

  const setValue = useCallback(
    (value: string) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedLiteral>) => {
          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedLiteral> = { ...literalExpression, text: { __$$text: value } };
          return ret;
        },
        expressionChangedArgs: {
          action: Action.LiteralTextExpressionChanged,
          from: literalExpression?.text?.__$$text ?? "",
          to: value,
        },
      });
    },
    [literalExpression, setExpression]
  );

  const width = useMemo(() => {
    return widthsById.get(id)?.[LITERAL_EXPRESSION_WIDTH_INDEX] ?? LITERAL_EXPRESSION_MIN_WIDTH;
  }, [id, widthsById]);

  const { containerCellCoordinates } = useBeeTableCoordinates();
  useBeeTableSelectableCellRef(
    containerCellCoordinates?.rowIndex ?? 0,
    containerCellCoordinates?.columnIndex ?? 0,
    setValue,
    getValue
  );

  const onColumnUpdates = useCallback(
    ([{ name, typeRef }]: BeeTableColumnUpdate<ROWTYPE>[]) => {
      const expressionChangedArgs: ExpressionChangedArgs = {
        action: Action.VariableChanged,
        variableUuid: expressionHolderId,
        typeChange:
          typeRef !== literalExpression["@_typeRef"]
            ? {
                from: literalExpression["@_typeRef"] ?? "",
                to: typeRef,
              }
            : undefined,
        nameChange:
          name !== literalExpression["@_label"]
            ? {
                from: literalExpression["@_label"] ?? "",
                to: name,
              }
            : undefined,
      };

      setExpression({
        setExpressionAction: (): Normalized<BoxedLiteral> => ({
          ...literalExpression,
          "@_label": name,
          "@_typeRef": typeRef,
        }),
        expressionChangedArgs,
      });
    },
    [expressionHolderId, literalExpression, setExpression]
  );

  const setLiteralExpressionWidth = useCallback(
    (newWidthAction: React.SetStateAction<number | undefined>) => {
      setWidthsById(({ newMap }) => {
        const prev = newMap.get(id) ?? [];
        const prevWidth = prev[LITERAL_EXPRESSION_WIDTH_INDEX];
        const newWidth = typeof newWidthAction === "function" ? newWidthAction(prevWidth) : newWidthAction;
        newMap.set(id, [newWidth ?? LITERAL_EXPRESSION_MIN_WIDTH]);
      });
    },
    [id, setWidthsById]
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

  const { onColumnResizingWidthChange, isPivoting } = usePublishedBeeTableResizableColumns(id, 1, false);

  const nestedExpressionContainer = useNestedExpressionContainer();

  const minWidth = useMemo(() => {
    return Math.max(
      nestedExpressionContainer.minWidth - LITERAL_EXPRESSION_EXTRA_WIDTH, //
      LITERAL_EXPRESSION_MIN_WIDTH
    );
  }, [nestedExpressionContainer]);

  const beeTableRef = useRef<BeeTableRef>(null);

  useEffect(() => {
    if (isPivoting || !isNested) {
      return;
    }

    const COLUMN_INDEX = 1;
    beeTableRef.current?.updateColumnResizingWidths(
      new Map([
        [
          COLUMN_INDEX,
          {
            value: Math.max(nestedExpressionContainer.resizingWidth.value - LITERAL_EXPRESSION_EXTRA_WIDTH, minWidth),
            isPivoting: false,
          },
        ],
      ])
    );
  }, [isPivoting, isNested, minWidth, nestedExpressionContainer.resizingWidth.value]);

  /// //////////////////////////////////////////////////////

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    return [
      {
        accessor: expressionHolderId as any, // FIXME: https://github.com/apache/incubator-kie-issues/issues/169
        label: literalExpression["@_label"] ?? DEFAULT_EXPRESSION_VARIABLE_NAME,
        isRowIndexColumn: false,
        dataType: literalExpression["@_typeRef"] ?? DmnBuiltInDataType.Undefined,
        minWidth,
        width,
        setWidth: setLiteralExpressionWidth,
      },
    ];
  }, [expressionHolderId, literalExpression, minWidth, setLiteralExpressionWidth, width]);

  const beeTableRows = useMemo<ROWTYPE[]>(() => {
    return [{ [expressionHolderId]: { content: literalExpression.text?.__$$text ?? "", id } }];
  }, [expressionHolderId, literalExpression.text, id]);

  const beeTableHeaderVisibility = useMemo(() => {
    return isNested ? BeeTableHeaderVisibility.None : BeeTableHeaderVisibility.AllLevels;
  }, [isNested]);

  const getRowKey = useCallback((row: ReactTable.Row<ROWTYPE>) => {
    return row.id;
  }, []);

  const { i18n } = useBoxedExpressionEditorI18n();

  const beeTableOperationConfig = useMemo(() => {
    return [
      {
        group: i18n.terms.selection.toUpperCase(),
        items: [
          { name: i18n.terms.copy, type: BeeTableOperation.SelectionCopy },
          { name: i18n.terms.cut, type: BeeTableOperation.SelectionCut },
          { name: i18n.terms.paste, type: BeeTableOperation.SelectionPaste },
          { name: i18n.terms.reset, type: BeeTableOperation.SelectionReset },
        ],
      },
    ];
  }, [i18n]);

  const allowedOperations = useCallback((conditions: BeeTableContextMenuAllowedOperationsConditions) => {
    if (!conditions.selection.selectionStart || !conditions.selection.selectionEnd) {
      return [];
    }

    return [
      BeeTableOperation.SelectionCopy,
      ...(conditions.selection.selectionStart.rowIndex === 0
        ? [BeeTableOperation.SelectionCut, BeeTableOperation.SelectionPaste, BeeTableOperation.SelectionReset]
        : []),
    ];
  }, []);

  return (
    <div className={`literal-expression`} data-testid={`kie-tools--bee--literal-expression-${id}`}>
      <div className={"literal-expression-body-container"}>
        <div className={"equals-sign"} data-testid={"kie-tools--equals-sign"}>{`=`}</div>
        <BeeTable<ROWTYPE>
          isReadOnly={isReadOnly}
          isEditableHeader={!isReadOnly}
          resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER}
          forwardRef={beeTableRef}
          getRowKey={getRowKey}
          columns={beeTableColumns}
          rows={beeTableRows}
          headerVisibility={beeTableHeaderVisibility}
          onColumnUpdates={onColumnUpdates}
          onCellUpdates={onCellUpdates}
          operationConfig={beeTableOperationConfig}
          allowedOperations={allowedOperations}
          onColumnResizingWidthChange={onColumnResizingWidthChange}
          shouldRenderRowIndexColumn={false}
          shouldShowRowsInlineControls={false}
          shouldShowColumnsInlineControls={false}
        />
      </div>
    </div>
  );
}
