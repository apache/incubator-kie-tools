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

import {
  DrilldownMenu,
  Menu,
  MenuContent,
  MenuGroup,
  MenuItem,
  MenuList,
} from "@patternfly/react-core/dist/js/components/Menu";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { PlusIcon } from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { TrashIcon } from "@patternfly/react-icons/dist/js/icons/trash-icon";
import { BlueprintIcon } from "@patternfly/react-icons/dist/js/icons/blueprint-icon";
import { CompressIcon } from "@patternfly/react-icons/dist/js/icons/compress-icon";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import {
  BeeTableContextMenuAllowedOperationsConditions,
  BeeTableOperation,
  BeeTableOperationConfig,
  BeeTableOperationGroup,
  InsertRowColumnsDirection,
} from "../../api";
import { ContextMenu, ContextMenuRef } from "../../contextMenu";
import { useBoxedExpressionEditor } from "../../BoxedExpressionEditorContext";
import { assertUnreachable } from "../../expressions/ExpressionDefinitionRoot/ExpressionDefinitionLogicTypeSelector";
import {
  BeeTableSelection,
  useBeeTableSelection,
  useBeeTableSelectionDispatch,
} from "../../selection/BeeTableSelectionContext";
import * as ReactTable from "react-table";
import * as _ from "lodash";
import { CutIcon } from "@patternfly/react-icons/dist/js/icons/cut-icon";
import { CopyIcon } from "@patternfly/react-icons/dist/js/icons/copy-icon";
import { PasteIcon } from "@patternfly/react-icons/dist/js/icons/paste-icon";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { NumberInput, Radio } from "@patternfly/react-core/dist/js/";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import "./BeeTableContextMenuHandler.css";

export interface BeeTableContextMenuHandlerProps {
  tableRef: React.RefObject<HTMLDivElement | null>;
  operationConfig: BeeTableOperationConfig | undefined;
  allowedOperations: (conditions: BeeTableContextMenuAllowedOperationsConditions) => BeeTableOperation[];
  reactTableInstance: ReactTable.TableInstance<any>;
  //
  onRowAdded?: (args: { beforeIndex: number; rowsCount: number; insertDirection: InsertRowColumnsDirection }) => void;
  onRowDuplicated?: (args: { rowIndex: number }) => void;
  onRowReset?: (args: { rowIndex: number }) => void;
  onRowDeleted?: (args: { rowIndex: number }) => void;
  onColumnAdded?: (args: {
    beforeIndex: number;
    currentIndex: number;
    groupType: string | undefined;
    columnsCount: number;
    insertDirection: InsertRowColumnsDirection;
  }) => void;
  onColumnDeleted?: (args: { columnIndex: number; groupType: string | undefined }) => void;
  isReadOnly: boolean;
}

/** The maximum numbers of rows or columns that can be inserted from the Insert menu. */
const MAXIMUM_ROWS_COLUMNS_PER_INSERTION = 500;

/** The default value for insert multiple rows/columns. */
const DEFAULT_MULTIPLE_ROWS_COLUMNS_INSERTION = 2;

export function BeeTableContextMenuHandler({
  tableRef,
  operationConfig,
  allowedOperations,
  reactTableInstance,
  onRowAdded,
  onRowDuplicated,
  onRowDeleted,
  onRowReset,
  onColumnAdded,
  onColumnDeleted,
  isReadOnly,
}: BeeTableContextMenuHandlerProps) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setCurrentlyOpenContextMenu } = useBoxedExpressionEditor();
  const [menuDrilledIn, setMenuDrilledIn] = useState<string[]>([]);
  const [drillDownPath, setDrillDownPath] = useState<string[]>([]);
  const [menuHeights, setMenuHeights] = useState<{ [key: string]: number }>({});

  const [direction, setDirection] = useState(InsertRowColumnsDirection.AboveOrRight);
  const [insertMultipleRowColumnsValue, setInsertMultipleRowColumnsValue] = React.useState<number>(
    DEFAULT_MULTIPLE_ROWS_COLUMNS_INSERTION
  );

  const { activeCell, selectionStart, selectionEnd } = useBeeTableSelection();

  const menuId = "menu-" + activeCell?.columnIndex + "-" + activeCell?.rowIndex;
  const [lastActiveMenu, setLastActiveMenu] = useState("");
  const [lastRootMenuId, setLastRootMenuId] = useState(menuId);

  const activeMenuId = useMemo(() => {
    if (menuId !== lastRootMenuId) {
      return menuId;
    } else {
      return lastActiveMenu;
    }
  }, [lastActiveMenu, lastRootMenuId, menuId]);

  const rootMenuId = useMemo(() => {
    if (menuId !== lastRootMenuId) {
      return menuId;
    }
    return lastRootMenuId;
  }, [lastRootMenuId, menuId]);

  useEffect(() => {
    setLastRootMenuId(menuId);
  }, [menuId]);

  useEffect(() => {
    // If menuId changes it means that user clicked in another cell, so we have to close the currently open
    // context menu in order to force it to be reopened with the correct options for the new cell
    setCurrentlyOpenContextMenu(undefined);
  }, [menuId, setCurrentlyOpenContextMenu]);

  const drillIn = useCallback((_event, fromMenuId, toMenuId, pathId) => {
    setMenuDrilledIn((prev) => [...prev, fromMenuId]);
    setDrillDownPath((prev) => [...prev, pathId]);
    setLastActiveMenu(toMenuId);
  }, []);

  const drillOut = useCallback((_event, toMenuId) => {
    setMenuDrilledIn((prev) => prev.slice(0, prev.length - 1));
    setDrillDownPath((prev) => prev.slice(0, prev.length - 1));
    setLastActiveMenu(toMenuId);
  }, []);

  const setMenuHeight = useCallback(
    (menuId: string, height: number) => {
      setMenuHeights((prev) => {
        if (prev[menuId] === undefined || (menuId !== rootMenuId && prev[menuId] !== height)) {
          return { ...prev, [menuId]: height };
        }
        return prev;
      });
    },
    [rootMenuId]
  );

  const selection: BeeTableSelection = useMemo(() => {
    return {
      active: activeCell,
      selectionStart: selectionStart,
      selectionEnd: selectionEnd,
    };
  }, [activeCell, selectionStart, selectionEnd]);
  const { copy, cut, paste, erase } = useBeeTableSelectionDispatch();

  const columns = useMemo(() => {
    if (!activeCell) {
      return undefined;
    }
    const rowIndex = activeCell.rowIndex;

    return rowIndex < 0 // Header cells to be read from headerGroups
      ? _.nth(reactTableInstance.headerGroups, rowIndex)?.headers
      : reactTableInstance.allColumns;
  }, [activeCell, reactTableInstance.allColumns, reactTableInstance.headerGroups]);

  const column = useMemo(() => {
    if (!activeCell) {
      return undefined;
    }

    const columnIndex = activeCell.columnIndex;
    const rowIndex = activeCell.rowIndex;
    if (rowIndex < 0) {
      // column index for rows with index < -1 is equal to count of cells on given row
      // so for the example below, 'output' column index is 1
      // +-----+--------+--------+------------------------+
      // |     |        |        |        output          |    <- rowIndex: -2
      // |  #  |  in-1  |  in-2  +----------+-------------+
      // |     |        |        |   out-1  |   out-2     |    <- rowIndex: -1
      // +-----+--------+--------+----------+-------------+
      //
      // See the same principle in: src/table/BeeTable/BeeTable.tsx#getColumnCount
      const nonPlaceholderColumns = columns?.filter((col) => !col?.placeholderOf);
      if (nonPlaceholderColumns) {
        return nonPlaceholderColumns[columnIndex];
      } else {
        console.error(`No column found at [${rowIndex}, ${columnIndex}]`);
      }
    } else {
      return columns?.[columnIndex];
    }
  }, [activeCell, columns]);

  const operationLabel = useCallback(
    (operation: BeeTableOperation) => {
      switch (operation) {
        case BeeTableOperation.ColumnInsertLeft:
          return i18n.columnOperations.insertLeft;
        case BeeTableOperation.ColumnInsertRight:
          return i18n.columnOperations.insertRight;
        case BeeTableOperation.ColumnInsertN:
          return i18n.insert;
        case BeeTableOperation.ColumnDelete:
          return i18n.columnOperations.delete;
        case BeeTableOperation.RowInsertAbove:
          return i18n.rowOperations.insertAbove;
        case BeeTableOperation.RowInsertBelow:
          return i18n.rowOperations.insertBelow;
        case BeeTableOperation.RowInsertN:
          return i18n.insert;
        case BeeTableOperation.RowDelete:
          return i18n.rowOperations.delete;
        case BeeTableOperation.RowReset:
          return i18n.rowOperations.reset;
        case BeeTableOperation.RowDuplicate:
          return i18n.rowOperations.duplicate;
        case BeeTableOperation.SelectionCopy:
          return i18n.terms.copy;
        case BeeTableOperation.SelectionCut:
          return i18n.terms.cut;
        case BeeTableOperation.SelectionPaste:
          return i18n.terms.paste;
        case BeeTableOperation.SelectionReset:
          return i18n.terms.reset;
        default:
          assertUnreachable(operation);
      }
    },
    [i18n]
  );

  const operationGroups = useMemo(() => {
    if (!activeCell) {
      return [];
    }
    if (isReadOnly) {
      const operationGroup: BeeTableOperationGroup = {
        group: "",
        items: [
          {
            name: operationLabel(BeeTableOperation.SelectionCopy),
            type: BeeTableOperation.SelectionCopy,
          },
        ],
      };

      return [operationGroup];
    }
    if (_.isArray(operationConfig)) {
      return operationConfig;
    }
    return (operationConfig ?? {})[column?.groupType || ""];
  }, [activeCell, column?.groupType, isReadOnly, operationConfig, operationLabel]);

  const allOperations = useMemo(() => {
    return operationGroups.flatMap(({ items }) => items);
  }, [operationGroups]);

  const operationIcon = useCallback((operation: BeeTableOperation) => {
    switch (operation) {
      case BeeTableOperation.ColumnInsertLeft:
        return <PlusIcon />;
      case BeeTableOperation.ColumnInsertRight:
        return <PlusIcon />;
      case BeeTableOperation.ColumnInsertN:
        return <PlusIcon />;
      case BeeTableOperation.ColumnDelete:
        return <TrashIcon />;
      case BeeTableOperation.RowInsertAbove:
        return <PlusIcon />;
      case BeeTableOperation.RowInsertBelow:
        return <PlusIcon />;
      case BeeTableOperation.RowInsertN:
        return <PlusIcon />;
      case BeeTableOperation.RowDelete:
        return <TrashIcon />;
      case BeeTableOperation.RowReset:
        return <CompressIcon />;
      case BeeTableOperation.RowDuplicate:
        return <BlueprintIcon />;
      case BeeTableOperation.SelectionCopy:
        return <CopyIcon />;
      case BeeTableOperation.SelectionCut:
        return <CutIcon />;
      case BeeTableOperation.SelectionPaste:
        return <PasteIcon />;
      case BeeTableOperation.SelectionReset:
        return <CompressIcon />;
      default:
        assertUnreachable(operation);
    }
  }, []);

  const handleOperation = useCallback(
    (operation: BeeTableOperation | undefined | null) => {
      if (operation === undefined || operation === null) {
        return;
      }

      if (!activeCell) {
        return [];
      }

      const rowIndex = activeCell.rowIndex;
      const columnIndex = activeCell.columnIndex;
      switch (operation) {
        case BeeTableOperation.ColumnInsertLeft:
          onColumnAdded?.({
            beforeIndex: columnIndex - 1,
            currentIndex: columnIndex,
            groupType: column?.groupType,
            columnsCount: 1,
            insertDirection: InsertRowColumnsDirection.BelowOrLeft,
          });
          console.debug(`Insert column left to ${columnIndex}`);
          break;
        case BeeTableOperation.ColumnInsertRight:
          onColumnAdded?.({
            beforeIndex: columnIndex,
            currentIndex: columnIndex,
            groupType: column?.groupType,
            columnsCount: 1,
            insertDirection: InsertRowColumnsDirection.AboveOrRight,
          });
          console.debug(`Insert column right to ${columnIndex}`);
          break;
        case BeeTableOperation.ColumnInsertN:
          if (direction === InsertRowColumnsDirection.AboveOrRight) {
            onColumnAdded?.({
              beforeIndex: columnIndex,
              currentIndex: columnIndex,
              groupType: column?.groupType,
              columnsCount: insertMultipleRowColumnsValue,
              insertDirection: InsertRowColumnsDirection.AboveOrRight,
            });
          } else {
            onColumnAdded?.({
              beforeIndex: columnIndex - 1,
              currentIndex: columnIndex,
              groupType: column?.groupType,
              columnsCount: insertMultipleRowColumnsValue,
              insertDirection: InsertRowColumnsDirection.BelowOrLeft,
            });
          }
          console.debug(`Insert n columns to ${columnIndex}`);
          break;
        case BeeTableOperation.ColumnDelete:
          onColumnDeleted?.({
            columnIndex: columnIndex - 1,
            groupType: column?.groupType,
          });
          console.debug(`Delete column ${columnIndex}`);
          break;
        case BeeTableOperation.RowInsertAbove:
          onRowAdded?.({
            beforeIndex: rowIndex,
            rowsCount: 1,
            insertDirection: InsertRowColumnsDirection.AboveOrRight,
          });
          console.debug(`Insert row above to ${rowIndex}`);
          break;
        case BeeTableOperation.RowInsertBelow:
          onRowAdded?.({
            beforeIndex: rowIndex + 1,
            rowsCount: 1,
            insertDirection: InsertRowColumnsDirection.BelowOrLeft,
          });
          console.debug(`Insert row below to ${rowIndex}`);
          break;
        case BeeTableOperation.RowInsertN:
          if (direction === InsertRowColumnsDirection.AboveOrRight) {
            onRowAdded?.({
              beforeIndex: rowIndex,
              rowsCount: insertMultipleRowColumnsValue,
              insertDirection: InsertRowColumnsDirection.AboveOrRight,
            });
          } else {
            onRowAdded?.({
              beforeIndex: rowIndex + 1,
              rowsCount: insertMultipleRowColumnsValue,
              insertDirection: InsertRowColumnsDirection.BelowOrLeft,
            });
          }
          console.debug(`Insert n rows to ${columnIndex}`);
          break;
        case BeeTableOperation.RowDelete:
          onRowDeleted?.({ rowIndex: rowIndex });
          console.debug(`Delete row ${rowIndex}`);
          break;
        case BeeTableOperation.RowReset:
          onRowReset?.({ rowIndex: rowIndex });
          console.debug(`Reset row ${rowIndex}`);
          break;
        case BeeTableOperation.RowDuplicate:
          onRowDuplicated?.({ rowIndex: rowIndex });
          console.debug(`Duplicate row ${rowIndex}`);
          break;
        case BeeTableOperation.SelectionCopy:
          copy();
          console.debug(
            `Copying area from: [${selectionStart?.rowIndex}, ${selectionStart?.columnIndex}] to [${selectionEnd?.rowIndex}, ${selectionEnd?.columnIndex}]`
          );
          break;
        case BeeTableOperation.SelectionCut:
          cut();
          console.debug(
            `Cuting area from: [${selectionStart?.rowIndex}, ${selectionStart?.columnIndex}] to [${selectionEnd?.rowIndex}, ${selectionEnd?.columnIndex}]`
          );
          break;
        case BeeTableOperation.SelectionPaste:
          paste();
          console.debug(`Pasting into: [${selectionStart?.rowIndex}, ${selectionStart?.columnIndex}]`);
          break;
        case BeeTableOperation.SelectionReset:
          erase();
          console.debug(
            `Reseting area from: [${selectionStart?.rowIndex}, ${selectionStart?.columnIndex}] to [${selectionEnd?.rowIndex}, ${selectionEnd?.columnIndex}]`
          );
          break;
        default:
          assertUnreachable(operation);
      }

      setCurrentlyOpenContextMenu(undefined);
    },
    [
      activeCell,
      selectionStart,
      selectionEnd,
      setCurrentlyOpenContextMenu,
      onColumnAdded,
      column?.groupType,
      onColumnDeleted,
      onRowAdded,
      onRowDeleted,
      onRowReset,
      onRowDuplicated,
      copy,
      cut,
      paste,
      erase,
      direction,
      insertMultipleRowColumnsValue,
    ]
  );

  const onMinus = useCallback(() => {
    const newValue = (insertMultipleRowColumnsValue || 0) - 1;
    setInsertMultipleRowColumnsValue(newValue);
  }, [insertMultipleRowColumnsValue]);

  const onChange = useCallback((event: React.FormEvent<HTMLInputElement>) => {
    const value = (event.target as HTMLInputElement).value;
    const intValue = Math.abs(parseInt(value));
    setInsertMultipleRowColumnsValue(intValue === 0 ? 1 : Math.min(intValue, MAXIMUM_ROWS_COLUMNS_PER_INSERTION));
  }, []);

  const onPlus = useCallback(() => {
    const newValue = (insertMultipleRowColumnsValue || 0) + 1;
    setInsertMultipleRowColumnsValue(newValue);
  }, [insertMultipleRowColumnsValue]);

  const insertRowColumnsNumberInput = useMemo(() => {
    return (
      <NumberInput
        value={insertMultipleRowColumnsValue}
        onMinus={onMinus}
        onChange={onChange}
        onPlus={onPlus}
        inputName="input"
        inputAriaLabel="number input"
        minusBtnAriaLabel="minus"
        plusBtnAriaLabel="plus"
        // allowEmptyInput={false}
        min={1}
        max={MAXIMUM_ROWS_COLUMNS_PER_INSERTION}
        style={{ textAlign: "center" }}
        onDoubleClick={(e) => e.stopPropagation()}
      />
    );
  }, [insertMultipleRowColumnsValue, onMinus, onChange, onPlus]);

  const resetDrillDownMenu = useCallback(() => {
    setMenuDrilledIn([]);
    setDrillDownPath([]);
    setMenuHeights({});
    setLastActiveMenu(rootMenuId);
    setInsertMultipleRowColumnsValue(DEFAULT_MULTIPLE_ROWS_COLUMNS_INSERTION);
  }, [rootMenuId]);

  useEffect(() => {
    resetDrillDownMenu();
  }, [resetDrillDownMenu]);

  const allowedOperationsForSelection = useMemo(() => {
    return allowedOperations({
      selection,
      column,
      columns,
    });
  }, [allowedOperations, selection, column, columns]);

  const hasAllowedOperations = useMemo(() => {
    return allOperations.some((operation) => allowedOperationsForSelection.includes(operation.type));
  }, [allOperations, allowedOperationsForSelection]);

  let countGroupsWithAllowedOperations = 0;

  function toggleInsertDirection() {
    setDirection(
      direction === InsertRowColumnsDirection.AboveOrRight
        ? InsertRowColumnsDirection.BelowOrLeft
        : InsertRowColumnsDirection.AboveOrRight
    );
  }

  function createDrillDownMenu(group: string, operation: BeeTableOperation) {
    return (
      <DrilldownMenu id={"insertNColumnsMenu" + operation.toString()}>
        <MenuItem direction="up" onClick={(e) => e.stopPropagation()}>
          Back
        </MenuItem>
        <Divider />
        <MenuGroup label={group}>
          <Flex direction={{ default: "column" }} style={{ padding: "16px" }}>
            <Flex direction={{ default: "column" }} width={"300px"}>
              <FlexItem onClick={(event) => event.stopPropagation()}>{insertRowColumnsNumberInput}</FlexItem>
            </Flex>
            <Flex direction={{ default: "row" }}>
              <FlexItem onClick={(event) => event.stopPropagation()}>
                <br />
                <Radio
                  id={"insertRightAbove" + operation}
                  name={"insertRightAbove" + operation}
                  label={
                    operation === BeeTableOperation.ColumnInsertN
                      ? i18n.insertDirections.toTheRight
                      : i18n.insertDirections.above
                  }
                  onChange={toggleInsertDirection}
                  isChecked={direction === InsertRowColumnsDirection.AboveOrRight}
                />
                <Radio
                  id={"insertLeftBelow" + operation}
                  name={"insertLeftBelow" + operation}
                  label={
                    operation === BeeTableOperation.ColumnInsertN
                      ? i18n.insertDirections.toTheLeft
                      : i18n.insertDirections.below
                  }
                  onChange={toggleInsertDirection}
                  isChecked={direction === InsertRowColumnsDirection.BelowOrLeft}
                />
              </FlexItem>
            </Flex>
            <FlexItem align={{ default: "alignLeft" }}>
              <br />
              <Button onClick={() => handleOperation(operation)}>Insert</Button>
            </FlexItem>
          </Flex>
        </MenuGroup>
      </DrilldownMenu>
    );
  }

  function buildMenuList(items: { name: string; type: BeeTableOperation }[], group: string) {
    return (
      <MenuList>
        {items.map((operation) =>
          operation.type === BeeTableOperation.ColumnInsertN || operation.type === BeeTableOperation.RowInsertN ? (
            <MenuItem
              icon={operationIcon(operation.type)}
              key={operation.type + group}
              itemId={operation.type}
              isDisabled={!allowedOperationsForSelection.includes(operation.type)}
              direction={"down"}
              onClick={(e) => e.stopPropagation()}
              drilldownMenu={createDrillDownMenu(group, operation.type)}
            >
              {operation.name ? operation.name : operationLabel(operation.type)}
            </MenuItem>
          ) : (
            <MenuItem
              icon={operationIcon(operation.type)}
              key={operation.type + group}
              itemId={operation.type}
              onClick={() => handleOperation(operation.type)}
              isDisabled={!allowedOperationsForSelection.includes(operation.type)}
            >
              {operation.name ? operation.name : operationLabel(operation.type)}
            </MenuItem>
          )
        )}
      </MenuList>
    );
  }

  function createMenuGroup(items: { name: string; type: BeeTableOperation }[], group: string, element: JSX.Element) {
    return (
      <>
        {items.some((operation) => allowedOperationsForSelection.includes(operation.type)) &&
          ++countGroupsWithAllowedOperations &&
          countGroupsWithAllowedOperations > 1 && <Divider key={"divider-" + group} style={{ padding: "16px" }} />}
        <MenuGroup
          label={group}
          className={
            items.every((operation) => !allowedOperationsForSelection.includes(operation.type))
              ? "no-allowed-actions-in-group"
              : ""
          }
        >
          {element}
        </MenuGroup>
      </>
    );
  }

  const contextMenuRef = useRef<ContextMenuRef>(null);
  useEffect(() => {
    if (activeMenuId === rootMenuId) {
      contextMenuRef.current?.recalculateNiceHeight(menuHeights[activeMenuId]);
      return;
    }

    const t = setTimeout(() => {
      contextMenuRef.current?.recalculateNiceHeight(menuHeights[activeMenuId]);
    }, 250 /* this is how long it takes for the drill down menu animation to be done (inMs) */);
    return () => {
      clearTimeout(t);
    };
  }, [activeMenuId, menuHeights, rootMenuId]);

  return (
    <>
      <ContextMenu domEventTargetRef={tableRef as any} triggerOn={"contextmenu"} forwardRef={contextMenuRef}>
        <Menu
          id={rootMenuId}
          containsDrilldown={true}
          onDrillIn={drillIn}
          onDrillOut={drillOut}
          activeMenu={activeMenuId}
          onGetMenuHeight={setMenuHeight}
          drilldownItemPath={drillDownPath}
          drilledInMenus={menuDrilledIn}
        >
          <MenuContent menuHeight={`${menuHeights[activeMenuId]}px`}>
            {hasAllowedOperations &&
              operationGroups.map(({ group, items }) => (
                <React.Fragment key={group}>
                  {activeMenuId === rootMenuId
                    ? createMenuGroup(items, group, buildMenuList(items, group))
                    : buildMenuList(items, group)}
                </React.Fragment>
              ))}
          </MenuContent>
        </Menu>
      </ContextMenu>
    </>
  );
}
