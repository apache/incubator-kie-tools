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

import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import * as ReactTable from "react-table";
import {
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  BeeTableProps,
  ContextExpressionDefinition,
  ContextExpressionDefinitionEntry,
  DmnBuiltInDataType,
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  generateUuid,
  getNextAvailablePrefixedName,
} from "../../api";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import {
  NestedExpressionContainerContext,
  NestedExpressionContainerContextType,
  useNestedExpressionContainer,
  usePivotAwareNestedExpressionContainer,
} from "../../resizing/NestedExpressionContainerContext";
import { ResizingWidth, useResizingWidths, useResizingWidthsDispatch } from "../../resizing/ResizingWidthsContext";
import { getExpressionMinWidth, getExpressionResizingWidth } from "../../resizing/Widths";
import {
  CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
  CONTEXT_ENTRY_EXTRA_WIDTH,
  CONTEXT_ENTRY_INFO_MIN_WIDTH,
} from "../../resizing/WidthValues";
import { BeeTable, BeeTableColumnUpdate } from "../../table/BeeTable";
import { useBeeTableCell, useBeeTableCoordinates } from "../../table/BeeTable/BeeTableSelectionContext";
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { ContextEntryExpressionCell } from "./ContextEntryExpressionCell";
import { ContextEntryInfoCell } from "./ContextEntryInfoCell";
import "./ContextExpression.css";
import { ContextResultExpressionCell } from "./ContextResultExpressionCell";

const CONTEXT_ENTRY_DEFAULT_DATA_TYPE = DmnBuiltInDataType.Undefined;

type ROWTYPE = ContextExpressionDefinitionEntry;

export function ContextExpression(contextExpression: ContextExpressionDefinition & { isHeadless: boolean }) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { decisionNodeId } = useBoxedExpressionEditor();
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const nestedExpressions = useMemo<ExpressionDefinition[]>(
    () => [...contextExpression.contextEntries.map((e) => e.entryExpression), contextExpression.result],
    [contextExpression.contextEntries, contextExpression.result]
  );

  const entryInfoWidth = useMemo(
    () => contextExpression.entryInfoWidth ?? CONTEXT_ENTRY_INFO_MIN_WIDTH,
    [contextExpression.entryInfoWidth]
  );

  const setEntryInfoWidth = useCallback(
    (newEntryInfoWidth: number) => {
      setExpression((prev) => ({ ...prev, entryInfoWidth: newEntryInfoWidth }));
    },
    [setExpression]
  );

  /// //////////////////////////////////////////////////////
  /// //////////////////////////////////////////////////////
  /// //////////////////////////////////////////////////////
  /// //////// ORIGINAL AT ContextExpression.tsx ///////////
  /// //////////////////////////////////////////////////////
  /// //////////////////////////////////////////////////////
  /// //////////////////////////////////////////////////////
  /// //////////////////////////////////////////////////////

  //// RESIZING WIDTHS (begin)

  const [entryInfoResizingWidth, setEntryInfoResizingWidth] = useState<ResizingWidth>({
    value: entryInfoWidth,
    isPivoting: false,
  });

  const onColumnResizingWidthChange = useCallback((args: { columnIndex: number; newResizingWidth: ResizingWidth }) => {
    if (args.columnIndex === 1) {
      setEntryInfoResizingWidth(args.newResizingWidth);
    }
  }, []);

  const { resizingWidths } = useResizingWidths();
  const isPivoting = useMemo<boolean>(() => {
    return entryInfoResizingWidth.isPivoting || nestedExpressions.some(({ id }) => resizingWidths.get(id!)?.isPivoting);
  }, [entryInfoResizingWidth.isPivoting, nestedExpressions, resizingWidths]);

  const entryExpressionsResizingWidthValue = useNestedExpressionResizingWidth(
    isPivoting,
    nestedExpressions,
    entryInfoWidth,
    entryInfoResizingWidth,
    CONTEXT_ENTRY_INFO_MIN_WIDTH,
    CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
    CONTEXT_ENTRY_EXTRA_WIDTH
  );

  const entryExpressionsMinWidth = useNestedExpressionMinWidth(
    nestedExpressions,
    entryInfoResizingWidth,
    CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
    CONTEXT_ENTRY_EXTRA_WIDTH
  );

  const entryExpressionsActualWidth = useNestedExpressionActualWidth(
    nestedExpressions,
    entryInfoWidth,
    CONTEXT_ENTRY_EXTRA_WIDTH
  );

  const nestedExpressionContainerValue = useMemo<NestedExpressionContainerContextType>(() => {
    return {
      minWidth: entryExpressionsMinWidth,
      actualWidth: entryExpressionsActualWidth,
      resizingWidth: {
        value: entryExpressionsResizingWidthValue,
        isPivoting,
      },
    };
  }, [entryExpressionsMinWidth, entryExpressionsActualWidth, entryExpressionsResizingWidthValue, isPivoting]);

  const { updateResizingWidth } = useResizingWidthsDispatch();

  useEffect(() => {
    updateResizingWidth(contextExpression.id!, (prev) => ({
      value: entryInfoResizingWidth.value + entryExpressionsResizingWidthValue + CONTEXT_ENTRY_EXTRA_WIDTH,
      isPivoting,
    }));
  }, [
    contextExpression.id,
    entryExpressionsResizingWidthValue,
    entryInfoResizingWidth.value,
    isPivoting,
    updateResizingWidth,
  ]);

  /// //////////////////////////////////////////////////////
  /// //////////////////////////////////////////////////////
  /// //////////////////////////////////////////////////////
  /// //////////////////////////////////////////////////////
  /// //////////////////////////////////////////////////////
  /// //////////////////////////////////////////////////////
  /// //////////////////////////////////////////////////////
  /// //////////////////////////////////////////////////////

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    return [
      {
        accessor: decisionNodeId as any,
        label: contextExpression.name ?? "Expression Name",
        isRowIndexColumn: false,
        dataType: contextExpression.dataType ?? CONTEXT_ENTRY_DEFAULT_DATA_TYPE,
        width: undefined,
        columns: [
          {
            accessor: "entryInfo",
            label: "entryInfo",
            isRowIndexColumn: false,
            dataType: DmnBuiltInDataType.Undefined,
            minWidth: CONTEXT_ENTRY_INFO_MIN_WIDTH,
            width: entryInfoWidth,
            setWidth: setEntryInfoWidth,
          },
          {
            accessor: "entryExpression",
            label: "entryExpression",
            isRowIndexColumn: false,
            dataType: DmnBuiltInDataType.Undefined,
            minWidth: CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
            width: undefined,
          },
        ],
      },
    ];
  }, [decisionNodeId, contextExpression.name, contextExpression.dataType, entryInfoWidth, setEntryInfoWidth]);

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

  const headerVisibility = useMemo(() => {
    return contextExpression.isHeadless ? BeeTableHeaderVisibility.None : BeeTableHeaderVisibility.SecondToLastLevel;
  }, [contextExpression.isHeadless]);

  const updateEntry = useCallback(
    (rowIndex: number, newEntry: ContextExpressionDefinitionEntry) => {
      setExpression((prev: ContextExpressionDefinition) => {
        const contextEntries = [...prev.contextEntries];
        contextEntries[rowIndex] = newEntry;
        return { ...prev, contextEntries };
      });
    },
    [setExpression]
  );

  const cellComponentByColumnId: BeeTableProps<ROWTYPE>["cellComponentByColumnId"] = useMemo(() => {
    return {
      entryInfo: (props) => {
        return <ContextEntryInfoCell {...props} onEntryUpdate={updateEntry} />;
      },
      entryExpression: (props) => {
        return <ContextEntryExpressionCell {...props} />;
      },
    };
  }, [updateEntry]);

  const beeTableOperationConfig = useMemo<BeeTableOperationConfig>(() => {
    return [
      {
        group: i18n.contextEntry,
        items: [
          { name: i18n.rowOperations.reset, type: BeeTableOperation.RowReset },
          { name: i18n.rowOperations.insertAbove, type: BeeTableOperation.RowInsertAbove },
          { name: i18n.rowOperations.insertBelow, type: BeeTableOperation.RowInsertBelow },
          { name: i18n.rowOperations.delete, type: BeeTableOperation.RowDelete },
        ],
      },
    ];
  }, [i18n]);

  const getRowKey = useCallback((row: ReactTable.Row<ROWTYPE>) => {
    return row.original.entryInfo.id;
  }, []);

  const beeTableAdditionalRow = useMemo(() => {
    return contextExpression.renderResult ?? true
      ? [
          <ContextResultInfoCell key={"context-result-info"} />,
          <ContextResultExpressionCell key={"context-result-expression"} contextExpression={contextExpression} />,
        ]
      : undefined;
  }, [contextExpression]);

  const getDefaultContextEntry = useCallback(
    (name?: string): ContextExpressionDefinitionEntry => {
      return {
        nameAndDataTypeSynchronized: true,
        entryExpression: {
          logicType: ExpressionDefinitionLogicType.Undefined,
          dataType: DmnBuiltInDataType.Undefined,
          id: generateUuid(),
        },
        entryInfo: {
          dataType: DmnBuiltInDataType.Undefined,
          id: generateUuid(),
          name:
            name ||
            getNextAvailablePrefixedName(
              contextExpression.contextEntries.map((e) => e.entryInfo.name),
              "ContextEntry"
            ),
        },
      };
    },
    [contextExpression]
  );

  const onRowAdded = useCallback(
    (args: { beforeIndex: number }) => {
      setExpression((prev: ContextExpressionDefinition) => {
        const newContextEntries = [...(prev.contextEntries ?? [])];
        newContextEntries.splice(args.beforeIndex, 0, getDefaultContextEntry());

        return {
          ...prev,
          contextEntries: newContextEntries,
        };
      });
    },
    [getDefaultContextEntry, setExpression]
  );

  const onRowDeleted = useCallback(
    (args: { rowIndex: number }) => {
      setExpression((prev: ContextExpressionDefinition) => {
        const newContextEntries = [...(prev.contextEntries ?? [])];
        newContextEntries.splice(args.rowIndex, 1);
        return {
          ...prev,
          contextEntries: newContextEntries,
        };
      });
    },
    [setExpression]
  );

  const onRowReset = useCallback(
    (args: { rowIndex: number }) => {
      setExpression((prev: ContextExpressionDefinition) => {
        // That's the additionalRow, meaning the contextExpression result.
        if (args.rowIndex === prev.contextEntries.length) {
          return {
            ...prev,
            result: {
              ...getDefaultContextEntry().entryExpression,
            },
          };
        }

        // That's a normal context entry
        else {
          const newContextEntries = [...(prev.contextEntries ?? [])];
          newContextEntries.splice(args.rowIndex, 1, {
            ...getDefaultContextEntry(newContextEntries[args.rowIndex].entryInfo.name),
          });
          return {
            ...prev,
            contextEntries: newContextEntries,
          };
        }
      });
    },
    [getDefaultContextEntry, setExpression]
  );

  return (
    <NestedExpressionContainerContext.Provider value={nestedExpressionContainerValue}>
      <div className={`context-expression ${contextExpression.id}`}>
        <BeeTable
          tableId={contextExpression.id}
          headerLevelCount={1}
          headerVisibility={headerVisibility}
          cellComponentByColumnId={cellComponentByColumnId}
          columns={beeTableColumns}
          rows={contextExpression.contextEntries}
          onColumnUpdates={onColumnUpdates}
          operationConfig={beeTableOperationConfig}
          getRowKey={getRowKey}
          additionalRow={beeTableAdditionalRow}
          onRowAdded={onRowAdded}
          onRowReset={onRowReset}
          onRowDeleted={onRowDeleted}
          onColumnResizingWidthChange={onColumnResizingWidthChange}
          shouldRenderRowIndexColumn={false}
          shouldShowRowsInlineControls={true}
          shouldShowColumnsInlineControls={false}
        />
      </div>
    </NestedExpressionContainerContext.Provider>
  );
}

export function ContextResultInfoCell() {
  const { containerCellCoordinates } = useBeeTableCoordinates();

  const value = useMemo(() => {
    return `<result>`;
  }, []);

  const getValue = useCallback(() => {
    return value;
  }, [value]);

  useBeeTableCell(
    containerCellCoordinates?.rowIndex ?? 0,
    containerCellCoordinates?.columnIndex ?? 0,
    undefined,
    getValue
  );

  return <div className="context-result">{value}</div>;
}

export function useNestedExpressionResizingWidth(
  isPivoting: boolean,
  nestedExpressions: ExpressionDefinition[],
  entryInfoWidth: number,
  entryInfoResizingWidth: ResizingWidth,
  entryInfoMinWidth: number,
  entryExpressionMinWidth: number,
  extraWidth: number
) {
  const { resizingWidths } = useResizingWidths();
  const nestedExpressionContainer = useNestedExpressionContainer();
  const pivotAwareNestedExpressionContainer = usePivotAwareNestedExpressionContainer(isPivoting);

  const entryExpressionsResizingWidthValue = useMemo<number>(() => {
    const nestedPivotingExpression: ExpressionDefinition | undefined = nestedExpressions.filter(
      ({ id }) => resizingWidths.get(id!)?.isPivoting ?? false
    )[0];

    if (nestedPivotingExpression) {
      return Math.max(getExpressionResizingWidth(nestedPivotingExpression, resizingWidths), entryInfoMinWidth);
    }

    const asdf =
      entryInfoResizingWidth.value >= entryInfoWidth
        ? pivotAwareNestedExpressionContainer.resizingWidth.value
        : nestedExpressionContainer.actualWidth;

    return Math.max(
      asdf - entryInfoResizingWidth.value - extraWidth,
      ...nestedExpressions.map((e) => getExpressionResizingWidth(e, new Map())),
      entryExpressionMinWidth
    );
  }, [
    entryExpressionMinWidth,
    entryInfoMinWidth,
    entryInfoResizingWidth.value,
    entryInfoWidth,
    extraWidth,
    nestedExpressionContainer.actualWidth,
    nestedExpressions,
    pivotAwareNestedExpressionContainer.resizingWidth.value,
    resizingWidths,
  ]);

  return entryExpressionsResizingWidthValue;
}

export function useNestedExpressionMinWidth(
  nestedExpressions: ExpressionDefinition[],
  entryInfoResizingWidth: ResizingWidth,
  entryExpressionMinWidth: number,
  extraWidth: number
) {
  const nestedExpressionContainer = useNestedExpressionContainer();
  return useMemo(() => {
    return Math.max(
      nestedExpressionContainer.minWidth - entryInfoResizingWidth.value - extraWidth,
      ...nestedExpressions.map((e) => getExpressionMinWidth(e)),
      entryExpressionMinWidth
    );
  }, [
    entryExpressionMinWidth,
    entryInfoResizingWidth.value,
    extraWidth,
    nestedExpressionContainer.minWidth,
    nestedExpressions,
  ]);
}

export function useNestedExpressionActualWidth(
  nestedExpressions: ExpressionDefinition[],
  entryInfoWidth: number,
  extraWidth: number
) {
  const nestedExpressionContainer = useNestedExpressionContainer();
  const { resizingWidths } = useResizingWidths();

  return useMemo<number>(() => {
    return Math.max(
      nestedExpressionContainer.actualWidth - entryInfoWidth - extraWidth,
      ...nestedExpressions
        .filter(({ id }) => !(resizingWidths.get(id!)?.isPivoting ?? false))
        .map((expression) => getExpressionResizingWidth(expression, new Map()))
    );
  }, [entryInfoWidth, extraWidth, nestedExpressionContainer.actualWidth, nestedExpressions, resizingWidths]);
}
