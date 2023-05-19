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
import {
  AUTO_ROW_ID,
  DEFAULT_COLUMN_MIN_WIDTH,
  UnitablesJsonSchemaBridge,
} from "../uniforms/UnitablesJsonSchemaBridge";
import getObjectValueByPath from "lodash/get";
import { useUnitablesContext, useUnitablesRow } from "../UnitablesContext";
import moment from "moment";
import { X_DMN_TYPE } from "@kie-tools/extended-services-api";

export type ROWTYPE = Record<string, any>;

const LIST_ADD_WIDTH = 63;
const LIST_DEL_WIDTH = 61;
const LIST_IDX_WIDTH = 61;

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
  bridge: UnitablesJsonSchemaBridge;
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
  bridge,
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

  const deepSomething = useCallback(
    (columnName: string, row: Record<string, any>): number => {
      const field = bridge.getField(columnName);
      const listInput = getObjectValueByPath(row, columnName) as [] | undefined;
      if (listInput && Array.isArray(listInput)) {
        if (listInput.length === 0) {
          return DEFAULT_COLUMN_MIN_WIDTH;
        }
        return listInput.reduce((length, _, index) => {
          return (
            LIST_IDX_WIDTH +
            Object.entries(field.items.properties).reduce(
              (length, [fieldKey, fieldProperty]: [string, Record<string, any>]) => {
                if (fieldProperty.type === "array") {
                  return length + LIST_ADD_WIDTH + deepSomething(`${columnName}.${index}.${fieldKey}`, row);
                }
                return length + bridge.getFieldDataType(fieldProperty).width;
              },
              length
            ) +
            LIST_DEL_WIDTH
          );
        }, 0);
      }
      if (listInput === undefined) {
        return DEFAULT_COLUMN_MIN_WIDTH;
      }
      return bridge.getFieldDataType(field).width + LIST_DEL_WIDTH;
    },
    [bridge]
  );

  const calculateArrayFieldLength = useCallback(
    (columnName: string) => {
      return rows.reduce((width, row) => {
        const rowWidth = LIST_ADD_WIDTH + deepSomething(columnName, row);
        if (rowWidth > width) {
          return rowWidth;
        }
        return width;
      }, 0);
    },
    [deepSomething, rows]
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
          columns: column.insideProperties.flatMap((insideProperty) => {
            let minWidth = insideProperty.minWidth ?? insideProperty.width;
            if (insideProperty.type === "array") {
              minWidth = calculateArrayFieldLength(insideProperty.joinedName);
              // minWidth =
              //   length > 0
              //     ? 63 + length * (insideProperty.minWidth ?? insideProperty.width)
              //     : insideProperty.minWidth ?? insideProperty.width;
            }
            return {
              originalId: uuid + `field-${insideProperty.joinedName}`,
              label: insideProperty.name,
              accessor: getColumnAccessor(insideProperty),
              dataType: insideProperty.dataType,
              isRowIndexColumn: false,
              width:
                (getObjectValueByPath(configs, insideProperty.joinedName) as UnitablesCellConfigs)?.width ??
                DEFAULT_COLUMN_MIN_WIDTH,
              setWidth: setColumnWidth(insideProperty.joinedName),
              minWidth,
            };
          }),
        };
      } else {
        let minWidth = column.width;
        if (column.type === "array") {
          minWidth = calculateArrayFieldLength(column.joinedName);
        }
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
              minWidth,
            },
          ],
        };
      }
    });
  }, [columns, uuid, configs, setColumnWidth, calculateArrayFieldLength]);

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
  const isListField = useMemo(() => field?.type === "array", [field?.type]);
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
        if (isListField) {
          // Get all uniforms components in the cell;
          const uniformsComponents = cellRef.current?.querySelectorAll('[id^="uniforms-"]');
          const uniformComponentTargetIndex = Array.from(uniformsComponents ?? []).findIndex(
            (component) => component.id === (e.target as HTMLElement).id
          );
          if (uniformsComponents === undefined) {
            console.log("false");
            setEditingCell(false);
            return;
          }

          // Event from ListField;
          if (uniformComponentTargetIndex < 0) {
            (uniformsComponents?.item(1) as HTMLElement).parentElement?.focus();
            console.log("true");
            setEditingCell(true);
            e.stopPropagation();
            return;
          }

          const nextUniformsComponent = e.shiftKey
            ? uniformsComponents[uniformComponentTargetIndex - 1]
            : uniformsComponents[uniformComponentTargetIndex + 1];

          if (nextUniformsComponent === undefined) {
            // Should leave ListField
            console.log("false");
            setEditingCell(false);
            return;
          }

          // TextField, BoolField, DateTimeField, NumField, ListAddField, ListDelField
          if (
            nextUniformsComponent.tagName.toLowerCase() === "input" ||
            nextUniformsComponent.tagName.toLowerCase() === "button"
          ) {
            (nextUniformsComponent as HTMLButtonElement | HTMLInputElement).parentElement?.focus();
            setEditingCell(true);
            e.stopPropagation();
            return;
          }

          if (nextUniformsComponent.tagName.toLowerCase() === "div") {
            setEditingCell(true);
            const buttons = Array.from(nextUniformsComponent?.getElementsByTagName("button"));
            if (buttons.length === 1) {
              // Check if it's a ListField
              if ((nextUniformsComponent as HTMLElement).attributes.getNamedItem("items")) {
                nextUniformsComponent?.getElementsByTagName("button")?.[0]?.parentElement?.focus();
              } else {
                nextUniformsComponent?.getElementsByTagName("button")?.[0]?.click();
                setIsSelectFieldOpen(true);
              }
            } else {
              (nextUniformsComponent as HTMLElement).focus();
            }
            e.stopPropagation();
          }
          return;
        }

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
        if (isListField) {
          const uniformsComponents = cellRef.current?.querySelectorAll('[id^="uniforms-"]');
          if (!uniformsComponents) {
            return;
          }

          const targetIsPresent = Array.from(uniformsComponents).find((component) => component === e.target);
          // From top level, what happens with lower levels?
          if (!targetIsPresent) {
            if (uniformsComponents[1].tagName.toLowerCase() === "button") {
              (uniformsComponents[1] as HTMLButtonElement)?.focus();
            }
          }

          return;
        }

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
    [isEditing, submitRow, isListField, setEditingCell, isEnumField, onFieldChange, navigateVertically, xDmnFieldType]
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
      if (isListField) {
        console.log("here2");
        if (isSelectFieldOpen) {
          // if a SelectField is open, it takes a time to render the select options;
          // After the select options are rendered we focus in the selected option;
          setTimeout(() => {
            const selectOptions = document.getElementsByName(fieldName)?.[0]?.getElementsByTagName("button");
            Array.from(selectOptions ?? [])
              ?.filter((selectOption) => selectOption.innerText === cellRef.current?.innerText)?.[0]
              ?.focus();
          }, 0);
        }
      }
      if (!isEditing) {
        cellRef.current?.focus();
      }
    }
  }, [fieldName, isActive, isEditing, isListField, isEnumField, isSelectFieldOpen]);

  const onBlur = useCallback(
    (e: React.FocusEvent<HTMLDivElement>) => {
      if (isListField) {
        console.log("here3", isEditing, e);
        return;
      }
      if (e.target.tagName.toLowerCase() === "div") {
        if ((e.target.getElementsByTagName("input")?.length ?? 0) > 0) {
          submitRow();
        }
      }

      if (e.target.tagName.toLowerCase() === "input") {
        submitRow();
      }
      if (
        e.target.tagName.toLowerCase() === "button" &&
        (e.relatedTarget as HTMLElement)?.tagName.toLowerCase() === "button"
      ) {
        // array field;
      } else if (
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
    [fieldName, isEditing, isListField, submitRow]
  );

  const onClick = useCallback(
    (e: React.MouseEvent<HTMLDivElement>) => {
      if (e.isTrusted && (e.target as HTMLElement).tagName.toLowerCase() === "button") {
        if (field.type === "array") {
          submitRow();
        } else {
          setIsSelectFieldOpen((prev) => !prev);
        }
      } else if (
        e.isTrusted &&
        field.type === "array" &&
        (e.target as HTMLElement)?.tagName.toLowerCase() !== "div" &&
        (e.target as HTMLElement)?.tagName.toLowerCase() !== "input" &&
        (e.currentTarget as HTMLElement)?.tagName.toLowerCase() === "div"
      ) {
        submitRow();
      }
    },
    [submitRow, field.type]
  );

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
