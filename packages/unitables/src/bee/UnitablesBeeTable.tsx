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
import { useUnitablesContext, useUnitablesRow } from "../UnitablesContext";
import { UnitablesRowApi } from "../UnitablesRow";
import moment from "moment";
import { X_DMN_TYPE } from "@kie-tools/extended-services-api";

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
  rowsRefs: Map<number, UnitablesRowApi>;
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
  rowsRefs,
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
              minWidth: insideProperty.width,
            };
          }),
        };
      } else {
        return {
          originalId: uuid + `field-${column.name}-parent`,
          label: "",
          accessor: getColumnAccessor(column) + "-parent",
          dataType: undefined as any,
          isRowIndexColumn: false,
          width: undefined,
          columns: [
            {
              originalId: uuid + `field-${column.name}`,
              label: column.name,
              accessor: getColumnAccessor(column),
              dataType: column.dataType,
              isRowIndexColumn: false,
              width: (getObjectValueByPath(configs, column.name) as UnitablesCellConfigs)?.width ?? column.width,
              setWidth: setColumnWidth(column.name),
              minWidth: column.width,
            },
          ],
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
}: BeeTableCellProps<ROWTYPE> & {
  joinedName: string;
  rowCount: number;
  columnCount: number;
}) {
  const [{ field, onChange: onFieldChange, name: fieldName }] = useField(joinedName, {});
  const cellRef = useRef<HTMLDivElement | null>(null);
  const [autoFieldKey, forceUpdate] = useReducer((x) => x + 1, 0);
  const { containerCellCoordinates } = useBeeTableCoordinates();
  const { isBeeTableChange } = useUnitablesContext();
  const { submitRow, rowInputs } = useUnitablesRow(containerCellCoordinates?.rowIndex ?? 0);
  const fieldInput = useMemo(() => getObjectValueByPath(rowInputs, fieldName), [rowInputs, fieldName]);
  const [isSelectFieldOpen, setIsSelectFieldOpen] = useState(false);
  const xDmnFieldType = useMemo(() => field?.["x-dmn-type"], [field]);
  const isEnumField = useMemo(() => !!field?.enum, [field]);
  const previousFieldInput = useRef(fieldInput);

  // keep previous updated;
  useEffect(() => {
    previousFieldInput.current = fieldInput;
  }, [fieldInput]);

  // FIXME: Decouple from DMN --> https://github.com/kiegroup/kie-issues/issues/166
  const setValue = useCallback(
    (newValue?: string) => {
      isBeeTableChange.current = true;
      const newValueWithoutSymbols = newValue?.replace(/\r/g, "") ?? "";

      if (field.enum) {
        if (field.enum.findIndex((value: unknown) => value === newValueWithoutSymbols) >= 0) {
          onFieldChange(newValueWithoutSymbols);
        } else {
          onFieldChange(field.placeholder);
        }
        // Changing the values using onChange will not re-render <select> nodes;
        // This ensure a re-render of the SelectField;
        forceUpdate();
      } else if (field.type === "string") {
        if (field.format === "time") {
          if (moment(newValueWithoutSymbols, [moment.HTML5_FMT.TIME, moment.HTML5_FMT.TIME_SECONDS], true).isValid()) {
            onFieldChange(newValueWithoutSymbols);
          } else {
            onFieldChange("");
          }
        } else if (field.format === "date") {
          if (moment(newValueWithoutSymbols, [moment.HTML5_FMT.DATE]).isValid()) {
            onFieldChange(newValueWithoutSymbols);
          } else {
            onFieldChange("");
          }
        } else if (field.format === "date-time") {
          const valueAsNumber = Date.parse(newValueWithoutSymbols);
          if (!isNaN(valueAsNumber)) {
            onFieldChange(newValueWithoutSymbols);
          } else {
            onFieldChange("");
          }
        } else {
          onFieldChange(newValueWithoutSymbols);
        }
      } else if (field.type === "number") {
        const numberValue = parseFloat(newValueWithoutSymbols);
        onFieldChange(isNaN(numberValue) ? undefined : numberValue);
      } else if (field.type === "boolean") {
        onFieldChange(newValueWithoutSymbols === "true");
      } else if (field.type === "array") {
        // FIXME: Support lists --> https://github.com/kiegroup/kie-issues/issues/167
      } else if (field.type === "object" && typeof newValue !== "object") {
        // objects are flattened in a single row - this case shouldn't happen;
      } else {
        onFieldChange(newValue);
      }
      submitRow();
    },
    [isBeeTableChange, field.enum, field.type, field.placeholder, field.format, submitRow, onFieldChange]
  );

  const { isActive, isEditing } = useBeeTableSelectableCellRef(
    containerCellCoordinates?.rowIndex ?? 0,
    containerCellCoordinates?.columnIndex ?? 0,
    setValue,
    useCallback(() => `${fieldInput ?? ""}`, [fieldInput])
  );
  const { mutateSelection } = useBeeTableSelectionDispatch();

  const navigateVertically = useCallback(
    (args: { isShiftPressed: boolean }) => {
      mutateSelection({
        part: SelectionPart.ActiveCell,
        columnCount: () => columnCount,
        rowCount,
        deltaColumns: 0,
        deltaRows: args.isShiftPressed ? -1 : 1,
        isEditingActiveCell: false,
        keepInsideSelection: true,
      });
    },
    [mutateSelection, rowCount, columnCount]
  );

  const setEditingCell = useCallback(
    (isEditing: boolean) => {
      mutateSelection({
        part: SelectionPart.ActiveCell,
        columnCount: () => columnCount,
        rowCount,
        deltaColumns: 0,
        deltaRows: 0,
        isEditingActiveCell: isEditing,
        keepInsideSelection: true,
      });
    },
    [mutateSelection, rowCount, columnCount]
  );

  const onKeyDown = useCallback(
    (e: React.KeyboardEvent<HTMLDivElement>) => {
      // TAB
      if (e.key.toLowerCase() === "tab") {
        submitRow();
        setEditingCell(false);
        if (isEnumField) {
          setIsSelectFieldOpen((prev) => {
            if (prev) {
              cellRef.current?.getElementsByTagName("button")?.[0]?.click();
            }
            return false;
          });
        }
        return;
      }

      // ESC
      if (e.key.toLowerCase() === "escape") {
        e.stopPropagation();
        onFieldChange(previousFieldInput.current);
        cellRef.current?.focus();
        setEditingCell(false);
        if (isEnumField) {
          setIsSelectFieldOpen((prev) => {
            if (prev) {
              cellRef.current?.getElementsByTagName("button")?.[0]?.click();
            }
            return false;
          });
        }
        return;
      }

      // ENTER
      if (e.key.toLowerCase() === "enter") {
        e.stopPropagation();
        if (isEnumField) {
          cellRef.current?.getElementsByTagName("button")?.[0]?.click();
          setIsSelectFieldOpen((prev) => {
            if (prev === true) {
              submitRow();
              setEditingCell(false);
            } else {
              setEditingCell(true);
            }
            return !prev;
          });
          return;
        }

        if (!isEditing) {
          const inputField = cellRef.current?.getElementsByTagName("input");
          if (inputField && inputField.length > 0) {
            inputField?.[0]?.focus();
            setEditingCell(true);
            return;
          }
        }
        submitRow();
        setEditingCell(false);
        navigateVertically({ isShiftPressed: e.shiftKey });
        return;
      }

      // Normal editing;
      if (isEditModeTriggeringKey(e)) {
        e.stopPropagation();

        // If the target is an input node it is already editing the cell;
        if (!isEditing && (e.target as HTMLInputElement).tagName.toLowerCase() !== "input") {
          // handle checkbox field;
          if (e.code.toLowerCase() === "space" && xDmnFieldType === X_DMN_TYPE.BOOLEAN) {
            cellRef.current?.getElementsByTagName("input")?.[0]?.click();
            submitRow();
            return;
          } else {
            cellRef.current?.getElementsByTagName("input")?.[0]?.select();
          }
        }

        setEditingCell(true);
      }

      if (isEditing) {
        e.stopPropagation();
      }
    },
    [xDmnFieldType, isEditing, isEnumField, navigateVertically, onFieldChange, setEditingCell, submitRow]
  );

  // if it's active should focus on cell;
  useEffect(() => {
    if (isActive) {
      if (isEnumField) {
        if (isSelectFieldOpen) {
          // if a SelectField is open, it takes a time to render the select options;
          // After the select options are rendered we focus in the selected option;
          setTimeout(() => {
            const selectOptions = document.getElementsByName(fieldName)?.[0]?.getElementsByTagName("button");
            Array.from(selectOptions ?? [])
              ?.filter((selectOption) => selectOption.innerText === cellRef.current?.innerText)?.[0]
              ?.focus();
          }, 0);
        } else {
          cellRef.current?.focus();
        }
      }
      if (!isEditing) {
        cellRef.current?.focus();
      }
    }
  }, [fieldName, isActive, isEditing, isEnumField, isSelectFieldOpen]);

  const onBlur = useCallback(
    (e: React.FocusEvent<HTMLDivElement>) => {
      if (e.target.tagName.toLowerCase() === "input") {
        submitRow();
      }
      if (
        e.target.tagName.toLowerCase() === "button" ||
        (e.relatedTarget as HTMLElement)?.tagName.toLowerCase() === "button"
      ) {
        // if the select field is open and it blurs to another cell, close it;
        const selectOptions = document.getElementsByName(fieldName)?.[0]?.getElementsByTagName("button");
        if ((selectOptions?.length ?? 0) > 0 && (e.relatedTarget as HTMLElement).tagName.toLowerCase() === "td") {
          e.target.click();
          setIsSelectFieldOpen(false);
        }
        submitRow();
      }
    },
    [fieldName, submitRow]
  );

  const onClick = useCallback((e: React.MouseEvent<HTMLDivElement>) => {
    if (e.isTrusted && (e.target as HTMLElement).tagName.toLowerCase() === "button") {
      setIsSelectFieldOpen((prev) => !prev);
    }
  }, []);

  return (
    <div
      style={{ outline: "none" }}
      tabIndex={-1}
      ref={cellRef}
      onKeyDown={onKeyDown}
      onBlur={onBlur}
      onClick={onClick}
    >
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
