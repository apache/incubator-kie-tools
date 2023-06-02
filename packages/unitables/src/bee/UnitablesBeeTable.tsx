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
const NESTED_LIST_DEL_WIDTH = 63;
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

  const recursiveCalculateListFieldWidth = useCallback(
    (columnName: string, row: Record<string, any>): number => {
      const field = bridge.getField(columnName);
      const listInput = getObjectValueByPath(row, columnName) as [] | undefined;
      if (listInput && Array.isArray(listInput)) {
        if (listInput.length === 0) {
          return DEFAULT_COLUMN_MIN_WIDTH;
        }
        return listInput.reduce((length, _, index) => {
          if (field.items.properties) {
            return (
              LIST_IDX_WIDTH +
              Object.entries(field.items.properties).reduce(
                (length, [fieldKey, fieldProperty]: [string, Record<string, any>]) => {
                  if (fieldProperty.type === "array") {
                    return (
                      length +
                      LIST_ADD_WIDTH +
                      recursiveCalculateListFieldWidth(`${columnName}.${index}.${fieldKey}`, row) +
                      1 // border for each element;
                    );
                  }
                  return length + bridge.getFieldDataType(fieldProperty).width + 1;
                },
                length
              ) +
              NESTED_LIST_DEL_WIDTH
            );
          }
          return LIST_IDX_WIDTH + length + bridge.getFieldDataType(field.items).width + NESTED_LIST_DEL_WIDTH;
        }, 0);
      }
      if (listInput === undefined) {
        return DEFAULT_COLUMN_MIN_WIDTH;
      }
      return bridge.getFieldDataType(field).width + LIST_DEL_WIDTH;
    },
    [bridge]
  );

  const calculateListFieldWidth = useCallback(
    (columnName: string) => {
      return rows.reduce((width, row) => {
        const rowWidth = LIST_ADD_WIDTH + recursiveCalculateListFieldWidth(columnName, row);
        if (rowWidth > width) {
          return rowWidth;
        }
        return width;
      }, 0);
    },
    [recursiveCalculateListFieldWidth, rows]
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
            let width =
              (getObjectValueByPath(configs, insideProperty.joinedName) as UnitablesCellConfigs)?.width ??
              DEFAULT_COLUMN_MIN_WIDTH;
            if (insideProperty.type === "array") {
              width = calculateListFieldWidth(insideProperty.joinedName);
              minWidth = calculateListFieldWidth(insideProperty.joinedName);
            }
            return {
              originalId: uuid + `field-${insideProperty.joinedName}`,
              label: insideProperty.name,
              accessor: getColumnAccessor(insideProperty),
              dataType: insideProperty.dataType,
              isRowIndexColumn: false,
              width,
              setWidth: setColumnWidth(insideProperty.joinedName),
              minWidth,
            };
          }),
        };
      } else {
        let minWidth = column.width;
        let width = (getObjectValueByPath(configs, column.name) as UnitablesCellConfigs)?.width ?? column.width;
        if (column.type === "array") {
          width = calculateListFieldWidth(column.joinedName);
          minWidth = calculateListFieldWidth(column.joinedName);
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
              width,
              setWidth: setColumnWidth(column.name),
              minWidth,
            },
          ],
        };
      }
    });
  }, [columns, uuid, configs, setColumnWidth, calculateListFieldWidth]);

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
  const fieldCharacteristics = useMemo(() => {
    if (!field) {
      return;
    }
    return {
      xDmnType: field["x-dmn-type"] as X_DMN_TYPE,
      isEnum: !!field.enum,
      isList: field.type === "array",
    };
  }, [field]);
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
        // ListField - START
        if (fieldCharacteristics?.isList) {
          // Get all uniforms components inside the ListField;
          const uniformsComponents = cellRef.current?.querySelectorAll('[id^="uniforms-"]');
          if (uniformsComponents === undefined) {
            setEditingCell(false);
            return;
          }

          const uniformComponentTargetIndex = Array.from(uniformsComponents ?? []).findIndex(
            (component) => component.id === (e.target as HTMLElement).id
          );

          // If it wasn't possible to retrieve the index, it should focus on the first button;
          if (uniformComponentTargetIndex < 0) {
            (uniformsComponents?.item(1) as HTMLElement).parentElement?.focus();
            setEditingCell(true);
            e.stopPropagation();
            return;
          }

          const nextUniformsComponent = e.shiftKey
            ? uniformsComponents[uniformComponentTargetIndex - 1]
            : uniformsComponents[uniformComponentTargetIndex + 1];

          // Should leave ListField if nextUniformsComponent doesn't exist
          if (nextUniformsComponent === undefined) {
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
            submitRow();
            e.stopPropagation();
            return;
          }

          // Nested ListFields or SelectField
          if (nextUniformsComponent.tagName.toLowerCase() === "div") {
            (nextUniformsComponent as HTMLElement)?.focus();
            setEditingCell(true);
            submitRow();
            e.stopPropagation();
            return;
          }
        } // ListField - END

        submitRow();
        setEditingCell(false);
        if (fieldCharacteristics?.isEnum) {
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
        if (fieldCharacteristics?.isEnum) {
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
        // ListField - START
        if (fieldCharacteristics?.isList) {
          e.stopPropagation();
          e.preventDefault();
          const uniformsComponents = cellRef.current?.querySelectorAll('[id^="uniforms-"]');
          if (!uniformsComponents) {
            return;
          }

          // To search the uniforms components avoiding returning the top ListField
          // we search backwards;
          const reversedUniformsComponents = Array.from(uniformsComponents).reverse();
          const reversedUniformComponentTargetIndex = reversedUniformsComponents.findIndex((component) =>
            component.contains(e.target as HTMLElement)
          );
          const uniformsComponent = reversedUniformsComponents[reversedUniformComponentTargetIndex];

          // If field is selected, and the target is not present
          if (!uniformsComponent) {
            // check if it's a button from a SelectField
            const selectFieldUl = document.querySelectorAll(`ul[name^="${fieldName}."]`)?.item(0);
            if (selectFieldUl && selectFieldUl.contains(e.target as HTMLElement)) {
              setIsSelectFieldOpen(false);
              submitRow();
              (cellRef.current?.querySelector(`[id=${selectFieldUl.id}]`) as HTMLDivElement)
                ?.getElementsByTagName("button")
                ?.item(0)
                ?.focus();
            } else if (uniformsComponents[1].tagName.toLowerCase() === "button") {
              (uniformsComponents[1] as HTMLButtonElement)?.focus();
            }
          } else {
            // A button is the ListAddField or ListDelField
            if (uniformsComponent.tagName.toLowerCase() === "button") {
              (uniformsComponent as HTMLButtonElement)?.click();

              // The ListField ListDelField is the last element
              if (reversedUniformComponentTargetIndex === 0) {
                // focus on ListField parent element;
                if (uniformsComponents[1].tagName.toLowerCase() === "button") {
                  (uniformsComponents[1] as HTMLButtonElement)?.focus();
                }
              }
              submitRow();
            }

            // SelectField
            if (uniformsComponent.tagName.toLowerCase() === "div") {
              setIsSelectFieldOpen(true);
            }
          }
          return;
        } // ListField - END

        e.stopPropagation();
        if (fieldCharacteristics?.isEnum) {
          cellRef.current?.getElementsByTagName("button")?.[0]?.click();
          setIsSelectFieldOpen((prev) => {
            if (prev === true) {
              submitRow();
            }
            return !prev;
          });
          setEditingCell(!isEditing);
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
        setEditingCell(true);

        // If the target is an input node it is already editing the cell;
        if (
          !fieldCharacteristics?.isList &&
          !isEditing &&
          (e.target as HTMLInputElement).tagName.toLowerCase() !== "input"
        ) {
          // handle checkbox field;
          const inputField = cellRef.current?.getElementsByTagName("input")?.[0];
          if (e.code.toLowerCase() === "space" && fieldCharacteristics?.xDmnType === X_DMN_TYPE.BOOLEAN) {
            inputField?.click();
            submitRow();
            return;
          }
          inputField?.select();
        }

        if (fieldCharacteristics?.isList) {
          if (
            e.code.toLowerCase() === "space" &&
            (e.target as HTMLElement)?.tagName?.toLowerCase() === "input" &&
            (e.target as HTMLInputElement)?.type === "checkbox"
          ) {
            e.preventDefault();
            (e.target as HTMLInputElement).click();
            submitRow();
            return;
          }

          if (e.code.toLowerCase() === "space" && (e.target as HTMLElement)?.tagName?.toLowerCase() === "button") {
            e.preventDefault();
            if ((e.target as HTMLButtonElement).id.match(/^uniforms-/g)) {
              return;
            }
            setIsSelectFieldOpen(true);
            return;
          }
        }
      }

      if (isEditing) {
        e.stopPropagation();
      }
    },
    [isEditing, submitRow, setEditingCell, fieldCharacteristics, onFieldChange, navigateVertically, fieldName]
  );

  // if it's active should focus on cell;
  useEffect(() => {
    if (!isActive) {
      return;
    }

    if (fieldCharacteristics?.isList) {
      if (isSelectFieldOpen) {
        setTimeout(() => {
          document.querySelectorAll(`ul[name^="${fieldName}."]`)?.[0]?.getElementsByTagName("button")?.item(0)?.focus();
        }, 0);
      }
    } else if (fieldCharacteristics?.isEnum) {
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
  }, [fieldName, isActive, isEditing, fieldCharacteristics?.isList, fieldCharacteristics?.isEnum, isSelectFieldOpen]);

  const onBlur = useCallback(
    (e: React.FocusEvent<HTMLDivElement>) => {
      if (fieldCharacteristics?.isList) {
        if (
          e.target.tagName.toLowerCase() === "button" &&
          (e.relatedTarget as HTMLElement)?.tagName.toLowerCase() === "button"
        ) {
          // if the select field is open and it blurs to another cell, close it;
          const selectFieldUl = document.querySelectorAll(`ul[name^="${fieldName}."]`).item(0);
          // if relatedTarget aka button is not in the SelectField UL, it should close the SelectField
          if (selectFieldUl && !selectFieldUl?.contains(e.relatedTarget as HTMLButtonElement)) {
            (cellRef.current?.querySelector(`[id="${selectFieldUl?.id}"]`) as HTMLDivElement)?.click();
            setIsSelectFieldOpen(false);
          }
        }
        submitRow();
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
        e.target.tagName.toLowerCase() === "button" ||
        (e.relatedTarget as HTMLElement)?.tagName.toLowerCase() === "button"
      ) {
        // if the select field is open and it blurs to another cell, close it;
        const selectOptions = document.getElementsByName(fieldName)?.[0]?.getElementsByTagName("button");
        if ((selectOptions?.length ?? 0) > 0 && (e.relatedTarget as HTMLElement)?.tagName?.toLowerCase() === "td") {
          e.target.click();
          setIsSelectFieldOpen(false);
        }
        submitRow();
      }
    },
    [fieldName, fieldCharacteristics?.isList, submitRow]
  );

  const onClick = useCallback(
    (e: React.MouseEvent<HTMLDivElement>) => {
      // The "enter" key triggers the onClick if the button is inside a form
      if (e.detail === 0) {
        return;
      }
      // ListField
      if (
        e.isTrusted &&
        fieldCharacteristics?.isList &&
        ((e.target as HTMLElement).tagName.toLowerCase() === "path" ||
          (e.target as HTMLElement).tagName.toLowerCase() === "svg" ||
          (e.target as HTMLElement).tagName.toLowerCase() === "button")
      ) {
        // if the select field is open and it blurs to another cell, close it;
        const selectField = document.querySelectorAll(`ul[name^="${fieldName}."]`).item(0);
        if (selectField?.contains(e.target as HTMLButtonElement)) {
          setIsSelectFieldOpen((prev) => !prev);
        }
        submitRow();
        return;
      }

      // SelectField
      if (e.isTrusted && (e.target as HTMLElement).tagName.toLowerCase() === "button") {
        setIsSelectFieldOpen((prev) => {
          if (prev === true) {
            submitRow();
          }
          return !prev;
        });
        setEditingCell(!isEditing);
      }

      if (!isEditing && e.isTrusted && (e.target as HTMLElement).tagName.toLowerCase() === "input") {
        const inputField = cellRef.current?.getElementsByTagName("input");
        if (inputField && inputField.length > 0) {
          inputField?.[0]?.focus();
          setEditingCell(true);
          return;
        }
      }
    },
    [fieldName, isEditing, fieldCharacteristics?.isList, setEditingCell, submitRow]
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
