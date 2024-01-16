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
  InsertRowColumnsDirection,
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
import { useBeeTableCoordinates, useBeeTableSelectableCellRef } from "../../selection/BeeTableSelectionContext";
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
import { getExpressionTotalMinWidth } from "../../resizing/WidthMaths";

const CONTEXT_ENTRY_DEFAULT_DATA_TYPE = DmnBuiltInDataType.Undefined;

type ROWTYPE = ContextExpressionDefinitionEntry;

export function ContextExpression(
  contextExpression: ContextExpressionDefinition & {
    isNested: boolean;
    parentElementId: string;
  }
) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { decisionNodeId } = useBoxedExpressionEditor();
  const { setExpression } = useBoxedExpressionEditorDispatch();
  const { variables } = useBoxedExpressionEditor();

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
        const entriesWidths = contextExpression.contextEntries.map((e) =>
          getExpressionTotalMinWidth(0, e.entryExpression)
        );
        const resultWidth = getExpressionTotalMinWidth(0, contextExpression.result);

        const maxNestedExpressionMinWidth = Math.max(...entriesWidths, resultWidth, CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH);

        return {
          nestedExpressions: [
            ...contextExpression.contextEntries.map((e) => e.entryExpression),
            contextExpression.result,
          ],
          fixedColumnActualWidth: entryInfoWidth,
          fixedColumnResizingWidth: entryInfoResizingWidth,
          fixedColumnMinWidth: CONTEXT_ENTRY_INFO_MIN_WIDTH,
          nestedExpressionMinWidth: maxNestedExpressionMinWidth,
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
        accessor: decisionNodeId as any, // FIXME: https://github.com/kiegroup/kie-issues/issues/169
        label: contextExpression.name ?? DEFAULT_EXPRESSION_NAME,
        isRowIndexColumn: false,
        dataType: contextExpression.dataType,
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
    return contextExpression.isNested ? BeeTableHeaderVisibility.None : BeeTableHeaderVisibility.SecondToLastLevel;
  }, [contextExpression.isNested]);

  const updateEntry = useCallback(
    (rowIndex: number, newEntry: ContextExpressionDefinitionEntry) => {
      setExpression((prev: ContextExpressionDefinition) => {
        const contextEntries = [...prev.contextEntries];

        variables?.repository.updateVariableType(newEntry.entryInfo.id, newEntry.entryInfo.dataType);
        variables?.repository.renameVariable(newEntry.entryInfo.id, newEntry.entryInfo.name);
        contextEntries[rowIndex] = newEntry;
        return { ...prev, contextEntries };
      });
    },
    [setExpression, variables?.repository]
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
          { name: i18n.insert, type: BeeTableOperation.RowInsertN },
          { name: i18n.rowOperations.delete, type: BeeTableOperation.RowDelete },
        ],
      },
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
          dataType: CONTEXT_ENTRY_DEFAULT_DATA_TYPE,
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
          dataType: CONTEXT_ENTRY_DEFAULT_DATA_TYPE,
        },
      };
    },
    [contextExpression]
  );

  const addVariable = useCallback(
    (
      args: {
        beforeIndex: number;
      },
      newContextEntries: ContextExpressionDefinitionEntry[],
      prev: ContextExpressionDefinition,
      newVariable: ContextExpressionDefinitionEntry
    ) => {
      const parentIndex = args.beforeIndex - 1;
      let parentId = contextExpression.parentElementId;
      if (parentIndex >= 0 && parentIndex < newContextEntries.length) {
        parentId = newContextEntries[parentIndex].entryInfo.id;
      }

      let childId: undefined | string;
      if (args.beforeIndex < newContextEntries.length) {
        childId = newContextEntries[args.beforeIndex].entryInfo.id;
      } else {
        childId = prev.result.id;
      }

      variables?.repository.addVariableToContext(
        newVariable.entryInfo.id,
        newVariable.entryInfo.name,
        parentId,
        childId
      );
    },
    [contextExpression.parentElementId, variables?.repository]
  );

  const onRowAdded = useCallback(
    (args: { beforeIndex: number; rowsCount: number; insertDirection: InsertRowColumnsDirection }) => {
      setExpression((prev: ContextExpressionDefinition) => {
        const newContextEntries = [...(prev.contextEntries ?? [])];

        const newEntries = [];
        const names = contextExpression.contextEntries.map((e) => e.entryInfo.name);
        for (let i = 0; i < args.rowsCount; i++) {
          const name = getNextAvailablePrefixedName(names, "ContextEntry");
          names.push(name);

          const defaultContextEntry = getDefaultContextEntry(name);
          addVariable(args, newContextEntries, prev, defaultContextEntry);
          newEntries.push(defaultContextEntry);
        }

        for (const newEntry of newEntries) {
          let index = args.beforeIndex;
          newContextEntries.splice(index, 0, newEntry);
          if (args.insertDirection === InsertRowColumnsDirection.AboveOrRight) {
            index++;
          }
        }

        return {
          ...prev,
          contextEntries: newContextEntries,
        };
      });
    },
    [addVariable, contextExpression.contextEntries, getDefaultContextEntry, setExpression]
  );

  const onRowDeleted = useCallback(
    (args: { rowIndex: number }) => {
      setExpression((prev: ContextExpressionDefinition) => {
        const newContextEntries = [...(prev.contextEntries ?? [])];

        variables?.repository.removeVariable(prev.contextEntries[args.rowIndex].entryInfo.id);

        newContextEntries.splice(args.rowIndex, 1);
        return {
          ...prev,
          contextEntries: newContextEntries,
        };
      });
    },
    [setExpression, variables?.repository]
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
              ...(rowIndex !== contextExpression.contextEntries.length ? [BeeTableOperation.RowInsertN] : []), // Because we can't insert multiple lines below <result>
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
          variables={variables}
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
      beeGwtService?.selectObject(""); // FIXME: Tiago --> This should actually be the id of the parent expression, as the <result> of a context follows its parent's data type.
    }
  }, [beeGwtService, isActive]);

  return <div className="context-result">{value}</div>;
}
