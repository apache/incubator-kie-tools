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
  BeeTableContextMenuAllowedOperationsConditions,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  BeeTableProps,
  ContextExpressionDefinition,
  ContextExpressionDefinitionEntry,
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
  generateUuid,
  getNextAvailablePrefixedName,
} from "../../api";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { useNestedExpressionContainerWithNestedExpressions } from "../../resizing/Hooks";
import { NestedExpressionContainerContext } from "../../resizing/NestedExpressionContainerContext";
import { ResizerStopBehavior, ResizingWidth } from "../../resizing/ResizingWidthsContext";
import {
  CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
  CONTEXT_ENTRY_INFO_MIN_WIDTH,
  CONTEXT_EXPRESSION_EXTRA_WIDTH,
} from "../../resizing/WidthConstants";
import { useBeeTableSelectableCellRef, useBeeTableCoordinates } from "../../selection/BeeTableSelectionContext";
import { BeeTable, BeeTableColumnUpdate } from "../../table/BeeTable";
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { DEFAULT_EXPRESSION_NAME } from "../ExpressionDefinitionHeaderMenu";
import { ContextEntryExpressionCell } from "./ContextEntryExpressionCell";
import { ContextEntryInfoCell } from "./ContextEntryInfoCell";
import "./ContextExpression.css";
import { ContextResultExpressionCell } from "./ContextResultExpressionCell";
import _ from "lodash";

const CONTEXT_ENTRY_DEFAULT_DATA_TYPE = DmnBuiltInDataType.Undefined;

type ROWTYPE = ContextExpressionDefinitionEntry;

export function ContextExpression(contextExpression: ContextExpressionDefinition & { isNested: boolean }) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const entryInfoWidth = useMemo(
    () => contextExpression.entryInfoWidth ?? CONTEXT_ENTRY_INFO_MIN_WIDTH,
    [contextExpression.entryInfoWidth]
  );

  const setEntryInfoWidth = useCallback(
    (newWidthAction: React.SetStateAction<number | undefined>) => {
      setExpression((prev: ContextExpressionDefinition) => {
        const newWidth = typeof newWidthAction === "function" ? newWidthAction(prev.entryInfoWidth) : newWidthAction;
        return { ...prev, entryInfoWidth: newWidth };
      });
    },
    [setExpression]
  );

  const [entryInfoResizingWidth, setEntryInfoResizingWidth] = useState<ResizingWidth>({
    value: entryInfoWidth,
    isPivoting: false,
  });

  const onColumnResizingWidthChange1 = useCallback((args: Map<number, ResizingWidth | undefined>) => {
    const newResizingWidth = args.get(1);
    if (newResizingWidth) {
      setEntryInfoResizingWidth(newResizingWidth);
    }
  }, []);

  /// //////////////////////////////////////////////////////
  /// ///////////// RESIZING WIDTHS ////////////////////////
  /// //////////////////////////////////////////////////////

  const { nestedExpressionContainerValue, onColumnResizingWidthChange: onColumnResizingWidthChange2 } =
    useNestedExpressionContainerWithNestedExpressions(
      useMemo(() => {
        return {
          nestedExpressions: [
            ...contextExpression.contextEntries.map((e) => e.entryExpression),
            contextExpression.result,
          ],
          fixedColumnActualWidth: entryInfoWidth,
          fixedColumnResizingWidth: entryInfoResizingWidth,
          fixedColumnMinWidth: CONTEXT_ENTRY_INFO_MIN_WIDTH,
          nestedExpressionMinWidth: CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
          extraWidth: CONTEXT_EXPRESSION_EXTRA_WIDTH,
          expression: contextExpression,
          flexibleColumnIndex: 2,
        };
      }, [contextExpression, entryInfoResizingWidth, entryInfoWidth])
    );

  /// //////////////////////////////////////////////////////

  const onColumnResizingWidthChange = useCallback(
    (args: Map<number, ResizingWidth | undefined>) => {
      onColumnResizingWidthChange2?.(args);
      onColumnResizingWidthChange1(args);
    },
    [onColumnResizingWidthChange1, onColumnResizingWidthChange2]
  );

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    return [
      {
        accessor: contextExpression.id as any, // FIXME: https://github.com/kiegroup/kie-issues/issues/169
        label: contextExpression.name ?? DEFAULT_EXPRESSION_NAME,
        isRowIndexColumn: false,
        dataType: contextExpression.dataType ?? CONTEXT_ENTRY_DEFAULT_DATA_TYPE,
        width: undefined,
        columns: [
          {
            accessor: "entryInfo",
            label: "entryInfo",
            isRowIndexColumn: false,
            dataType: DmnBuiltInDataType.Undefined,
            isWidthPinned: true,
            minWidth: CONTEXT_ENTRY_INFO_MIN_WIDTH,
            width: entryInfoWidth,
            setWidth: setEntryInfoWidth,
          },
          {
            accessor: "entryExpression",
            label: "entryExpression",
            dataType: DmnBuiltInDataType.Undefined,
            isRowIndexColumn: false,
            minWidth: CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
            width: undefined,
          },
        ],
      },
    ];
  }, [contextExpression.name, contextExpression.dataType, entryInfoWidth, setEntryInfoWidth]);

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
    return contextExpression.isNested ? BeeTableHeaderVisibility.None : BeeTableHeaderVisibility.SecondToLastLevel;
  }, [contextExpression.isNested]);

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

  const cellComponentByColumnAccessor: BeeTableProps<ROWTYPE>["cellComponentByColumnAccessor"] = useMemo(() => {
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
      {
        group: _.upperCase(i18n.terms.selection),
        items: [
          { name: i18n.terms.copy, type: BeeTableOperation.SelectionCopy },
          { name: i18n.terms.cut, type: BeeTableOperation.SelectionCut },
          { name: i18n.terms.paste, type: BeeTableOperation.SelectionPaste },
          { name: i18n.terms.reset, type: BeeTableOperation.SelectionReset },
        ],
      },
    ];
  }, [i18n]);

  const getRowKey = useCallback((row: ReactTable.Row<ROWTYPE>) => {
    return row.original.entryInfo.id;
  }, []);

  const beeTableAdditionalRow = useMemo(() => {
    return [
      <ContextResultInfoCell key={"context-result-info"} />,
      <ContextResultExpressionCell
        key={"context-result-expression"}
        contextExpression={contextExpression}
        rowIndex={contextExpression.contextEntries.length}
        columnIndex={2}
      />,
    ];
  }, [contextExpression]);

  const getDefaultContextEntry = useCallback(
    (name?: string): ContextExpressionDefinitionEntry => {
      return {
        entryInfo: {
          id: generateUuid(),
          dataType: DmnBuiltInDataType.Undefined,
          name:
            name ||
            getNextAvailablePrefixedName(
              contextExpression.contextEntries.map((e) => e.entryInfo.name),
              "ContextEntry"
            ),
        },
        entryExpression: {
          id: generateUuid(),
          logicType: ExpressionDefinitionLogicType.Undefined,
          dataType: DmnBuiltInDataType.Undefined,
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

  const allowedOperations = useCallback(
    (conditions: BeeTableContextMenuAllowedOperationsConditions) => {
      if (!conditions.selection.selectionStart || !conditions.selection.selectionEnd) {
        return [];
      }

      const columnIndex = conditions.selection.selectionStart.columnIndex;
      const rowIndex = conditions.selection.selectionStart.rowIndex;

      return [
        BeeTableOperation.SelectionCopy,
        ...(columnIndex > 1
          ? [BeeTableOperation.SelectionCut, BeeTableOperation.SelectionPaste, BeeTableOperation.SelectionReset]
          : []),
        ...(conditions.selection.selectionStart.rowIndex >= 0
          ? [
              BeeTableOperation.RowInsertAbove,
              ...(rowIndex !== contextExpression.contextEntries.length ? [BeeTableOperation.RowInsertBelow] : []), // do not insert below <result>
              ...(contextExpression.contextEntries.length > 1 && rowIndex !== contextExpression.contextEntries.length
                ? [BeeTableOperation.RowDelete]
                : []), // do not delete <result>
              BeeTableOperation.RowReset,
            ]
          : []),
      ];
    },
    [contextExpression.contextEntries.length]
  );

  return (
    <NestedExpressionContainerContext.Provider value={nestedExpressionContainerValue}>
      <div className={`context-expression ${contextExpression.id}`}>
        <BeeTable
          resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER}
          tableId={contextExpression.id}
          headerLevelCountForAppendingRowIndexColumn={1}
          headerVisibility={headerVisibility}
          cellComponentByColumnAccessor={cellComponentByColumnAccessor}
          columns={beeTableColumns}
          rows={contextExpression.contextEntries}
          onColumnUpdates={onColumnUpdates}
          operationConfig={beeTableOperationConfig}
          allowedOperations={allowedOperations}
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

  const { isActive } = useBeeTableSelectableCellRef(
    containerCellCoordinates?.rowIndex ?? 0,
    containerCellCoordinates?.columnIndex ?? 0,
    undefined,
    getValue
  );

  const { beeGwtService } = useBoxedExpressionEditor();

  useEffect(() => {
    if (isActive) {
      beeGwtService?.selectObject("");
    }
  }, [beeGwtService, isActive]);

  return <div className="context-result">{value}</div>;
}
