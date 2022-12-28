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

import { Menu, MenuGroup, MenuItem, MenuList } from "@patternfly/react-core/dist/js/components/Menu";
import PlusIcon from "@patternfly/react-icons/dist/js/icons/plus-icon";
import TrashIcon from "@patternfly/react-icons/dist/js/icons/trash-icon";
import BlueprintIcon from "@patternfly/react-icons/dist/js/icons/blueprint-icon";
import CompressIcon from "@patternfly/react-icons/dist/js/icons/compress-icon";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { BeeTableOperation, BeeTableOperationConfig, BeeTableOperationGroup } from "../../api";
import { useCustomContextMenuHandler } from "../../contextMenu/Hooks";
import { useBoxedExpressionEditor } from "../../expressions/BoxedExpressionEditor/BoxedExpressionEditorContext";
import { assertUnreachable } from "../../expressions/ExpressionDefinitionLogicTypeSelector";
import "./BeeTableContextMenuHandler.css";
import { useBeeTableSelection, useBeeTableSelectionDispatch } from "./BeeTableSelectionContext";
import * as ReactTable from "react-table";
import * as _ from "lodash";
import CutIcon from "@patternfly/react-icons/dist/js/icons/cut-icon";
import CopyIcon from "@patternfly/react-icons/dist/js/icons/copy-icon";
import PasteIcon from "@patternfly/react-icons/dist/js/icons/paste-icon";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";

export interface BeeTableContextMenuHandlerProps {
  tableRef: React.RefObject<HTMLDivElement | null>;
  operationConfig: BeeTableOperationConfig | undefined;
  reactTableInstance: ReactTable.TableInstance<any>;
  //
  onRowAdded?: (args: { beforeIndex: number }) => void;
  onRowDuplicated?: (args: { rowIndex: number }) => void;
  onRowDeleted?: (args: { rowIndex: number }) => void;
  onColumnAdded?: (args: { beforeIndex: number; groupType: string | undefined }) => void;
  onColumnDeleted?: (args: { columnIndex: number; groupType: string | undefined }) => void;
}

export function BeeTableContextMenuHandler({
  tableRef,
  operationConfig,
  reactTableInstance,
  onRowAdded,
  onRowDuplicated,
  onRowDeleted,
  onColumnAdded,
  onColumnDeleted,
}: BeeTableContextMenuHandlerProps) {
  const { setCurrentlyOpenContextMenu } = useBoxedExpressionEditor();

  const { activeCell, selectionStart, selectionEnd } = useBeeTableSelection();
  const { copy, cut, paste, erase } = useBeeTableSelectionDispatch();

  const getColumnOperations = useCallback(
    (columnIndex: number) => {
      const groupTypeForCurrentColumn = reactTableInstance.allColumns[columnIndex]?.groupType;
      const columnsByGroupType = _.groupBy(reactTableInstance.allColumns, (column) => column.groupType);
      const atLeastTwoColumnsOfTheSameGroupType = groupTypeForCurrentColumn
        ? columnsByGroupType[groupTypeForCurrentColumn].length > 1
        : // FIXME: Tiago -> : colmnsWidthAddedRowIndex.length > 2; // The total number of columns is counting also the # of rows column
          reactTableInstance.allColumns.length > 2; // The total number of columns is counting also the # of rows column

      const columnCanBeDeleted = columnIndex > 0 && atLeastTwoColumnsOfTheSameGroupType;

      return columnIndex === 0 // This is the "row index" column
        ? []
        : [
            BeeTableOperation.ColumnInsertLeft,
            BeeTableOperation.ColumnInsertRight,
            ...(columnCanBeDeleted ? [BeeTableOperation.ColumnDelete] : []),
          ];
    },
    [reactTableInstance.allColumns]
  );

  const operationGroups = useMemo(() => {
    if (!activeCell) {
      return [];
    }
    if (_.isArray(operationConfig)) {
      return operationConfig;
    }
    const column = reactTableInstance.allColumns[activeCell.columnIndex];
    return (operationConfig ?? {})[column?.groupType || ""];
  }, [activeCell, operationConfig, reactTableInstance.allColumns]);

  const allowedOperations = useMemo(() => {
    if (!activeCell) {
      return [];
    }

    return [
      ...getColumnOperations(activeCell.columnIndex),
      ...(activeCell.rowIndex >= 0
        ? [
            BeeTableOperation.RowInsertAbove,
            BeeTableOperation.RowInsertBelow,
            ...(reactTableInstance.rows.length > 1 ? [BeeTableOperation.RowDelete] : []),
            BeeTableOperation.RowClear,
            BeeTableOperation.RowDuplicate,
          ]
        : []),
    ];
  }, [activeCell, getColumnOperations, reactTableInstance.rows.length]);

  const quantities = useMemo(() => {
    return {
      newRows: 1 + Math.abs((selectionEnd?.rowIndex ?? 0) - (selectionStart?.rowIndex ?? 0)),
      newColumns: 1 + Math.abs((selectionEnd?.columnIndex ?? 0) - (selectionStart?.columnIndex ?? 0)),
    };
  }, [selectionStart?.columnIndex, selectionStart?.rowIndex, selectionEnd?.columnIndex, selectionEnd?.rowIndex]);

  const operationLabel = useCallback(
    (operation: BeeTableOperation) => {
      switch (operation) {
        case BeeTableOperation.ColumnInsertLeft:
          return `Insert ${quantities.newColumns} left`;
        case BeeTableOperation.ColumnInsertRight:
          return `Insert ${quantities.newColumns} right`;
        case BeeTableOperation.ColumnDelete:
          return `Delete ${quantities.newColumns}`;
        case BeeTableOperation.RowInsertAbove:
          return `Insert ${quantities.newRows} above`;
        case BeeTableOperation.RowInsertBelow:
          return `Insert ${quantities.newRows} below`;
        case BeeTableOperation.RowDelete:
          return `Delete ${quantities.newRows}`;
        case BeeTableOperation.RowClear:
          return `Clear`;
        case BeeTableOperation.RowDuplicate:
          return `Duplicate`;
        default:
          assertUnreachable(operation);
      }
    },
    [quantities.newColumns, quantities.newRows]
  );

  const operationIcon = useCallback((operation: BeeTableOperation) => {
    switch (operation) {
      case BeeTableOperation.ColumnInsertLeft:
        return <PlusIcon />;
      case BeeTableOperation.ColumnInsertRight:
        return <PlusIcon />;
      case BeeTableOperation.ColumnDelete:
        return <TrashIcon />;
      case BeeTableOperation.RowInsertAbove:
        return <PlusIcon />;
      case BeeTableOperation.RowInsertBelow:
        return <PlusIcon />;
      case BeeTableOperation.RowDelete:
        return <TrashIcon />;
      case BeeTableOperation.RowClear:
        return <CompressIcon />;
      case BeeTableOperation.RowDuplicate:
        return <BlueprintIcon />;
      default:
        assertUnreachable(operation);
    }
  }, []);

  const handleOperation = useCallback(
    (operation: BeeTableOperation | undefined | null) => {
      if (operation === undefined || operation === null) {
        return;
      }

      switch (operation) {
        case BeeTableOperation.ColumnInsertLeft:
          onColumnAdded?.({
            beforeIndex: selectionStart!.columnIndex - 1,
            groupType: reactTableInstance.allColumns[selectionStart!.columnIndex].groupType,
          });
          console.info(`Insert column left to ${selectionStart!.columnIndex}`);
          break;
        case BeeTableOperation.ColumnInsertRight:
          onColumnAdded?.({
            beforeIndex: selectionStart!.columnIndex,
            groupType: reactTableInstance.allColumns[selectionStart!.columnIndex].groupType,
          });
          console.info(`Insert column right to ${selectionStart!.columnIndex}`);
          break;
        case BeeTableOperation.ColumnDelete:
          onColumnDeleted?.({
            columnIndex: selectionStart!.columnIndex - 1,
            groupType: reactTableInstance.allColumns[selectionStart!.columnIndex].groupType,
          });
          console.info(`Delete column ${selectionStart!.columnIndex}`);
          break;
        case BeeTableOperation.RowInsertAbove:
          onRowAdded?.({ beforeIndex: selectionStart!.rowIndex });
          console.info(`Insert row above to ${selectionStart!.rowIndex}`);
          break;
        case BeeTableOperation.RowInsertBelow:
          onRowAdded?.({ beforeIndex: selectionStart!.rowIndex + 1 });
          console.info(`Insert row below to ${selectionStart!.rowIndex}`);
          break;
        case BeeTableOperation.RowDelete:
          onRowDeleted?.({ rowIndex: selectionStart!.rowIndex });
          console.info(`Delete row ${selectionStart!.rowIndex}`);
          break;
        case BeeTableOperation.RowClear:
          console.info(`Clear row ${selectionStart!.rowIndex}`);
          break;
        case BeeTableOperation.RowDuplicate:
          onRowDuplicated?.({ rowIndex: selectionStart!.rowIndex });
          console.info(`Duplicate row ${selectionStart!.rowIndex}`);
          break;
        default:
          assertUnreachable(operation);
      }

      setCurrentlyOpenContextMenu(undefined);
    },
    [
      setCurrentlyOpenContextMenu,
      onColumnAdded,
      selectionStart,
      reactTableInstance.allColumns,
      onColumnDeleted,
      onRowAdded,
      onRowDeleted,
      onRowDuplicated,
    ]
  );

  const { xPos, yPos, isOpen } = useCustomContextMenuHandler(tableRef);

  const style = useMemo(() => {
    return {
      top: yPos,
      left: xPos,
    };
  }, [xPos, yPos]);

  return (
    <>
      {isOpen && (
        <div className="context-menu-container" style={style}>
          <Menu
            ouiaId="expression-table-context-menu"
            className="table-context-menu"
            onSelect={(e, itemId) => handleOperation(itemId as BeeTableOperation)}
          >
            {operationGroups.map(({ group, items }) => (
              <MenuGroup
                key={group}
                label={group}
                className={
                  items.every((operation) => !allowedOperations.includes(operation.type))
                    ? "no-allowed-actions-in-group"
                    : ""
                }
              >
                <MenuList>
                  {items.map((operation) => (
                    <MenuItem
                      icon={operationIcon(operation.type)}
                      data-ouia-component-id={"expression-table-context-menu-" + operation.name}
                      key={operation.type}
                      itemId={operation.type}
                      isDisabled={!allowedOperations.includes(operation.type)}
                    >
                      {operationLabel(operation.type)}
                    </MenuItem>
                  ))}
                </MenuList>
              </MenuGroup>
            ))}

            <Divider style={{ padding: "16px" }} />
            <MenuGroup>
              <MenuList>
                <MenuItem onClick={erase} icon={<CompressIcon />}>
                  {"Reset"}
                </MenuItem>
                <MenuItem onClick={copy} icon={<CopyIcon />}>
                  {"Copy"}
                </MenuItem>
                <MenuItem onClick={cut} icon={<CutIcon />}>
                  {"Cut"}
                </MenuItem>
                <MenuItem onClick={paste} icon={<PasteIcon />}>
                  {"Paste"}
                </MenuItem>
              </MenuList>
            </MenuGroup>
          </Menu>
        </div>
      )}
    </>
  );
}
