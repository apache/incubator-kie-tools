/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {
  BeeTableCellProps,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  BeeTableProps,
  generateUuid,
} from "@kie-tools/boxed-expression-component/dist/api";
import { BoxedExpressionEditorI18n } from "@kie-tools/boxed-expression-component/dist/i18n";
import { StandaloneBeeTable } from "@kie-tools/boxed-expression-component/dist/table/BeeTable/StandaloneBeeTable";
import {
  SelectionPart,
  useBeeTableCoordinates,
  useBeeTableSelectableCellRef,
  useBeeTableSelectionDispatch,
} from "@kie-tools/boxed-expression-component/dist/selection/BeeTableSelectionContext";
import * as React from "react";
import { useCallback, useMemo, useReducer, useEffect, useRef, useState } from "react";
import * as ReactTable from "react-table";
import { UnitablesColumnType, UnitablesInputsConfigs, UnitablesCellConfigs } from "../UnitablesTypes";
import "@kie-tools/boxed-expression-component/dist/@types/react-table";
import { ResizerStopBehavior } from "@kie-tools/boxed-expression-component/dist/resizing/ResizingWidthsContext";
import { AutoField } from "@kie-tools/uniforms-patternfly/dist/esm";
import { useField } from "uniforms";
import { AUTO_ROW_ID } from "../uniforms/UnitablesJsonSchemaBridge";
import getObjectValueByPath from "lodash/get";
import { useUnitablesContext } from "../UnitablesContext";
import { getOperatingSystem, OperatingSystem } from "@kie-tools-core/operating-system";

export const UNITABLES_COLUMN_MIN_WIDTH = 150;

export type ROWTYPE = Record<string, any>;

export interface UnitablesBeeTable {
  id: string;
  i18n: BoxedExpressionEditorI18n;
  rows: object[];
  columns: UnitablesColumnType[];
  scrollableParentRef: React.RefObject<HTMLElement>;
  rowWrapper?: React.FunctionComponent<React.PropsWithChildren<{ row: object; rowIndex: number }>>;
  onRowAdded: (args: { beforeIndex: number }) => void;
  onRowDuplicated: (args: { rowIndex: number }) => void;
  onRowReset: (args: { rowIndex: number }) => void;
  onRowDeleted: (args: { rowIndex: number }) => void;
  configs: UnitablesInputsConfigs;
  setWidth: (newWidth: number, fieldName: string) => void;
}

export function UnitablesBeeTable({
  id,
  i18n,
  columns,
  rows,
  scrollableParentRef,
  rowWrapper,
  onRowAdded,
  onRowDuplicated,
  onRowReset,
  onRowDeleted,
  configs,
  setWidth,
}: UnitablesBeeTable) {
  const beeTableOperationConfig = useMemo<BeeTableOperationConfig>(
    () => [
      {
        group: i18n.rows,
        items: [
          { name: i18n.rowOperations.insertAbove, type: BeeTableOperation.RowInsertAbove },
          { name: i18n.rowOperations.insertBelow, type: BeeTableOperation.RowInsertBelow },
          { name: i18n.rowOperations.duplicate, type: BeeTableOperation.RowDuplicate },
          { name: i18n.rowOperations.reset, type: BeeTableOperation.RowReset },
          { name: i18n.rowOperations.delete, type: BeeTableOperation.RowDelete },
        ],
      },
    ],
    [i18n]
  );

  const uuid = useMemo(() => {
    return generateUuid();
  }, []);

  // starts with 1 due to "index" column
  const columnsCount = useMemo(
    () => columns.reduce((acc, column) => acc + (column.insideProperties ? column.insideProperties.length : 1), 1),
    [columns]
  );

  const cellComponentByColumnAccessor: BeeTableProps<ROWTYPE>["cellComponentByColumnAccessor"] = React.useMemo(() => {
    return columns.reduce((acc, column) => {
      if (column.insideProperties) {
        for (const insideProperty of column.insideProperties) {
          acc[getColumnAccessor(insideProperty)] = (props) => (
            <UnitablesBeeTableCell
              {...props}
              joinedName={insideProperty.joinedName}
              rowCount={rows.length}
              columnCount={columnsCount}
            />
          );
        }
      } else {
        acc[getColumnAccessor(column)] = (props) => (
          <UnitablesBeeTableCell
            {...props}
            joinedName={column.joinedName}
            rowCount={rows.length}
            columnCount={columnsCount}
          />
        );
      }
      return acc;
    }, {} as NonNullable<BeeTableProps<ROWTYPE>["cellComponentByColumnAccessor"]>);
  }, [columns, rows.length, columnsCount]);

  const setColumnWidth = useCallback(
    (fieldName: string) => (newWidthAction: React.SetStateAction<number | undefined>) => {
      const newWidth = typeof newWidthAction === "function" ? newWidthAction(0) : newWidthAction;
      setWidth(newWidth ?? 0, fieldName);
      return newWidth;
    },
    [setWidth]
  );

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    return columns.map((column) => {
      if (column.insideProperties) {
        return {
          originalId: uuid + `field-${column.name}`,
          label: column.name,
          accessor: getColumnAccessor(column),
          dataType: column.dataType,
          isRowIndexColumn: false,
          width: undefined,
          minWidth: UNITABLES_COLUMN_MIN_WIDTH,
          columns: column.insideProperties.map((insideProperty) => {
            return {
              originalId: uuid + `field-${insideProperty.joinedName}`,
              label: insideProperty.name,
              accessor: getColumnAccessor(insideProperty),
              dataType: insideProperty.dataType,
              isRowIndexColumn: false,
              width:
                (getObjectValueByPath(configs, insideProperty.joinedName) as UnitablesCellConfigs)?.width ??
                insideProperty.width,
              setWidth: setColumnWidth(insideProperty.joinedName),
              minWidth: UNITABLES_COLUMN_MIN_WIDTH,
            };
          }),
        };
      } else {
        return {
          originalId: uuid + `field-${column.name}`,
          label: column.name,
          accessor: getColumnAccessor(column),
          dataType: column.dataType,
          isRowIndexColumn: false,
          width: (getObjectValueByPath(configs, column.name) as UnitablesCellConfigs)?.width ?? column.width,
          setWidth: setColumnWidth(column.name),
          minWidth: UNITABLES_COLUMN_MIN_WIDTH,
        };
      }
    });
  }, [setColumnWidth, configs, columns, uuid]);

  const getColumnKey = useCallback((column: ReactTable.ColumnInstance<ROWTYPE>) => {
    return column.originalId ?? column.id;
  }, []);

  const getRowKey = useCallback((row: ReactTable.Row<ROWTYPE>) => {
    return row.original.id;
  }, []);

  return (
    <StandaloneBeeTable
      cellComponentByColumnAccessor={cellComponentByColumnAccessor}
      scrollableParentRef={scrollableParentRef}
      getColumnKey={getColumnKey}
      getRowKey={getRowKey}
      tableId={id}
      isEditableHeader={false}
      headerLevelCountForAppendingRowIndexColumn={1}
      headerVisibility={BeeTableHeaderVisibility.AllLevels}
      operationConfig={beeTableOperationConfig}
      columns={beeTableColumns}
      rows={rows}
      enableKeyboardNavigation={true}
      shouldRenderRowIndexColumn={true}
      shouldShowRowsInlineControls={true}
      shouldShowColumnsInlineControls={false}
      onRowAdded={onRowAdded}
      onRowDuplicated={onRowDuplicated}
      onRowReset={onRowReset}
      onRowDeleted={onRowDeleted}
      rowWrapper={rowWrapper}
      resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_ALWAYS}
    />
  );
}

function getColumnAccessor(c: UnitablesColumnType) {
  return `field-${c.joinedName}`;
}

function UnitablesBeeTableCell({
  joinedName,
  rowCount,
  columnCount,
}: BeeTableCellProps<ROWTYPE> & { joinedName: string; rowCount: number; columnCount: number }) {
  const [{ field, value, onChange }] = useField(joinedName, {});

  const cellRef = useRef<HTMLDivElement | null>(null);

  const [autoFieldKey, forceUpdate] = useReducer((x) => x + 1, 0);

  const { containerCellCoordinates } = useBeeTableCoordinates();
  const { internalChange } = useUnitablesContext();

  // FIXME: Luiz - shouldn't have any reference to DMN!
  // TODO: Luiz - Fix: x-dmn-type from field property: Any, Undefined, string, number, ...;
  // this is useful in case we have a "time", "date", "date and time" fields;
  const setValue = useCallback(
    (newValue: string) => {
      internalChange.current = true;
      const newValueWithoutSymbols = newValue.replace(/\r/g, "");

      if (field.enum) {
        onChange(field.placeholder);
        // Changing the values using onChange will not re-render <select> nodes;
        // This ensure a re-render of the SelectField;
        forceUpdate();
      } else if (field.type === "string") {
        onChange(newValueWithoutSymbols);
      } else if (field.type === "number") {
        const numberValue = parseFloat(newValueWithoutSymbols);
        onChange(isNaN(numberValue) ? undefined : numberValue);
      } else if (field.type === "boolean") {
        onChange(newValueWithoutSymbols === "true");
      } else if (field.type === "array") {
        // FIXME: Luiz - array fields are still not supported by DMN Runner Table;
      } else if (field.type === "object" && typeof newValue !== "object") {
        // objects are flattened in a single row - this case shouldn't happen;
      } else {
        onChange(newValue);
      }
    },
    [internalChange, field, onChange]
  );

  const { isActive, isEditing } = useBeeTableSelectableCellRef(
    containerCellCoordinates?.rowIndex ?? 0,
    containerCellCoordinates?.columnIndex ?? 0,
    setValue,
    useCallback(() => `${value ?? ""}`, [value])
  );
  const { mutateSelection } = useBeeTableSelectionDispatch();

  const columnCountCallback = useCallback(() => columnCount, [columnCount]);

  const navigateVertically = useCallback(
    (args: { isShiftPressed: boolean }) => {
      mutateSelection({
        part: SelectionPart.ActiveCell,
        columnCount: columnCountCallback,
        rowCount,
        deltaColumns: 0,
        deltaRows: args.isShiftPressed ? -1 : 1,
        isEditingActiveCell: false,
        keepInsideSelection: true,
      });
    },
    [mutateSelection, rowCount, columnCountCallback]
  );

  const setEditing = useCallback(
    (isEditing: boolean) => {
      console.log("setEditing", isEditing);
      mutateSelection({
        part: SelectionPart.ActiveCell,
        columnCount: columnCountCallback,
        rowCount,
        deltaColumns: 0,
        deltaRows: 0,
        isEditingActiveCell: isEditing,
        keepInsideSelection: true,
      });
    },
    [mutateSelection, rowCount, columnCountCallback]
  );

  // This useEffect forces the focus into the selected cell;
  useEffect(() => {
    if (!isActive) {
      return;
    }

    const keyDownListener = (e: KeyboardEvent) => {
      console.log("KEYDOWN", e);
      if (isEditModeTriggeringKey(e as any)) {
        // setEditing(true);
        cellRef.current?.getElementsByTagName("input")?.[0]?.select();
      }
    };

    document?.addEventListener("keydown", keyDownListener);
    return () => {
      document?.removeEventListener("keydown", keyDownListener);
    };
  }, [isActive, setEditing]);

  useEffect(() => {
    if (!isActive) {
      return;
    }

    const keyUpListener = (e: KeyboardEvent) => {
      console.log("KEYUP", e);
      if (e.key.toLowerCase() === "enter") {
        // setEditing(true);
        const inputField = cellRef.current?.getElementsByTagName("input");
        if (inputField && inputField.length > 0) {
          inputField?.[0]?.focus();
          return;
        }
        // const buttonField = cellRef.current?.getElementsByTagName("button");
        // if (buttonField && buttonField.length > 0) {
        //   buttonField?.[0]?.click();
        //   (document.querySelectorAll(".pf-c-select__menu-item")?.[0] as any)?.focus();
        //   return;
        // }
      }
    };

    document?.addEventListener("keyup", keyUpListener);
    return () => {
      // const selectOptions = (document.querySelectorAll(".pf-c-select__menu-item")?.[0] as any);
      // if (selectOptions) {
      //   const buttonField = saveCellRef?.getElementsByTagName("button");
      //   if (buttonField && buttonField.length > 0) {
      //     buttonField?.[0]?.click();
      //     return;
      //   }
      // }

      document?.removeEventListener("keyup", keyUpListener);
    };
  }, [isActive, setEditing]);

  const onKeyDown = useCallback(
    (e: React.KeyboardEvent<HTMLDivElement>) => {
      // console.log("DIV KEYDOWN", e);
      if (e.key.toLowerCase() === "tab") {
        // setEditing(false);
        return;
      }

      if (e.key.toLowerCase() === "escape") {
        // setEditing(false);
        return;
        // setValue(`${previousValue.current ?? ""}`);
      }

      if (e.key.toLowerCase() === "enter") {
        // setEditing(false);
        navigateVertically({ isShiftPressed: e.shiftKey });
        return;
      }

      // if (isEditModeTriggeringKey(e)) {
      //   setEditing(true);
      //   return;
      // }
      e.stopPropagation();
    },
    [navigateVertically]
  );

  useEffect(() => {
    console.log(
      "coordinates x,y=",
      containerCellCoordinates?.rowIndex,
      containerCellCoordinates?.columnIndex,
      " IS ACTIVE=",
      isActive,
      ", IS EDITING=",
      isEditing
    );
  }, [containerCellCoordinates, isActive, isEditing]);

  return (
    <div ref={cellRef} onKeyDown={onKeyDown}>
      <AutoField
        key={joinedName + autoFieldKey}
        name={joinedName}
        form={`${AUTO_ROW_ID}-${containerCellCoordinates?.rowIndex ?? 0}`}
        style={{ height: "60px" }}
      />
    </div>
  );
}

function isEditModeTriggeringKey(e: React.KeyboardEvent) {
  if (e.altKey || e.ctrlKey || e.metaKey) {
    return false;
  }

  return /^[\d\w ()[\]{},.\-_'"/?<>+\\|]$/.test(e.key);
}
