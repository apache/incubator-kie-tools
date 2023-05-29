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
import { useCallback, useLayoutEffect, useMemo } from "react";
import { BeeTableOperation, BeeTableOperationConfig } from "../../api";
import { useCustomContextMenuHandler } from "../../contextMenu/Hooks";
import { useBoxedExpressionEditor } from "../../expressions/BoxedExpressionEditor/BoxedExpressionEditorContext";
import { assertUnreachable } from "../../expressions/ExpressionDefinitionRoot/ExpressionDefinitionLogicTypeSelector";
import "./BeeTableContextMenuHandler.css";
import { useBeeTableSelection, useBeeTableSelectionDispatch } from "../../selection/BeeTableSelectionContext";
import * as ReactTable from "react-table";
import * as _ from "lodash";
import CutIcon from "@patternfly/react-icons/dist/js/icons/cut-icon";
import CopyIcon from "@patternfly/react-icons/dist/js/icons/copy-icon";
import PasteIcon from "@patternfly/react-icons/dist/js/icons/paste-icon";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { useBoxedExpressionEditorI18n } from "../../i18n";

export interface BeeTableContextMenuHandlerProps {
  tableRef: React.RefObject<HTMLDivElement | null>;
  operationConfig: BeeTableOperationConfig | undefined;
  reactTableInstance: ReactTable.TableInstance<any>;
  //
  onRowAdded?: (args: { beforeIndex: number }) => void;
  onRowDuplicated?: (args: { rowIndex: number }) => void;
  onRowReset?: (args: { rowIndex: number }) => void;
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
  onRowReset,
  onColumnAdded,
  onColumnDeleted,
}: BeeTableContextMenuHandlerProps) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setCurrentlyOpenContextMenu } = useBoxedExpressionEditor();

  const { activeCell } = useBeeTableSelection();
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
    return columns?.[columnIndex];
  }, [activeCell, columns]);

  const columnOperations = useMemo(() => {
    if (!activeCell) {
      return [];
    }

    const columnIndex = activeCell.columnIndex;

    const atLeastTwoColumnsOfTheSameGroupType = column?.groupType
      ? _.groupBy(columns, (column) => column?.groupType)[column.groupType].length > 1
      : true;

    const columnCanBeDeleted =
      columnIndex > 0 &&
      atLeastTwoColumnsOfTheSameGroupType &&
      (columns?.length ?? 0) > 2 && // That's a regular column and the rowIndex column
      (column?.columns?.length ?? 0) <= 0;

    return columnIndex === 0 // This is the rowIndex column
      ? []
      : [
          BeeTableOperation.ColumnInsertLeft,
          BeeTableOperation.ColumnInsertRight,
          ...(columnCanBeDeleted ? [BeeTableOperation.ColumnDelete] : []),
        ];
  }, [activeCell, column, columns]);

  const operationGroups = useMemo(() => {
    if (!activeCell) {
      return [];
    }
    if (_.isArray(operationConfig)) {
      return operationConfig;
    }
    return (operationConfig ?? {})[column?.groupType || ""];
  }, [activeCell, column?.groupType, operationConfig]);

  const allowedOperations = useMemo(() => {
    if (!activeCell) {
      return [];
    }

    return [
      ...columnOperations,
      ...(activeCell.rowIndex >= 0
        ? [
            BeeTableOperation.RowInsertAbove,
            BeeTableOperation.RowInsertBelow,
            ...(reactTableInstance.rows.length > 1 ? [BeeTableOperation.RowDelete] : []),
            BeeTableOperation.RowReset,
            BeeTableOperation.RowDuplicate,
          ]
        : []),
    ];
  }, [activeCell, columnOperations, reactTableInstance.rows.length]);

  const operationLabel = useCallback(
    (operation: BeeTableOperation) => {
      switch (operation) {
        case BeeTableOperation.ColumnInsertLeft:
          return i18n.columnOperations.insertLeft;
        case BeeTableOperation.ColumnInsertRight:
          return i18n.columnOperations.insertRight;
        case BeeTableOperation.ColumnDelete:
          return i18n.columnOperations.delete;
        case BeeTableOperation.RowInsertAbove:
          return i18n.rowOperations.insertAbove;
        case BeeTableOperation.RowInsertBelow:
          return i18n.rowOperations.insertBelow;
        case BeeTableOperation.RowDelete:
          return i18n.rowOperations.delete;
        case BeeTableOperation.RowReset:
          return i18n.rowOperations.reset;
        case BeeTableOperation.RowDuplicate:
          return i18n.rowOperations.duplicate;
        default:
          assertUnreachable(operation);
      }
    },
    [i18n]
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
      case BeeTableOperation.RowReset:
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

      if (!activeCell) {
        return [];
      }

      const rowIndex = activeCell.rowIndex;
      const columnIndex = activeCell.columnIndex;
      switch (operation) {
        case BeeTableOperation.ColumnInsertLeft:
          onColumnAdded?.({
            beforeIndex: columnIndex - 1,
            groupType: column?.groupType,
          });
          console.info(`Insert column left to ${columnIndex}`);
          break;
        case BeeTableOperation.ColumnInsertRight:
          onColumnAdded?.({
            beforeIndex: columnIndex,
            groupType: column?.groupType,
          });
          console.info(`Insert column right to ${columnIndex}`);
          break;
        case BeeTableOperation.ColumnDelete:
          onColumnDeleted?.({
            columnIndex: columnIndex - 1,
            groupType: column?.groupType,
          });
          console.info(`Delete column ${columnIndex}`);
          break;
        case BeeTableOperation.RowInsertAbove:
          onRowAdded?.({ beforeIndex: rowIndex });
          console.info(`Insert row above to ${rowIndex}`);
          break;
        case BeeTableOperation.RowInsertBelow:
          onRowAdded?.({ beforeIndex: rowIndex + 1 });
          console.info(`Insert row below to ${rowIndex}`);
          break;
        case BeeTableOperation.RowDelete:
          onRowDeleted?.({ rowIndex: rowIndex });
          console.info(`Delete row ${rowIndex}`);
          break;
        case BeeTableOperation.RowReset:
          onRowReset?.({ rowIndex: rowIndex });
          console.info(`Reset row ${rowIndex}`);
          break;
        case BeeTableOperation.RowDuplicate:
          onRowDuplicated?.({ rowIndex: rowIndex });
          console.info(`Duplicate row ${rowIndex}`);
          break;
        default:
          assertUnreachable(operation);
      }

      setCurrentlyOpenContextMenu(undefined);
    },
    [
      activeCell,
      setCurrentlyOpenContextMenu,
      onColumnAdded,
      column?.groupType,
      onColumnDeleted,
      onRowAdded,
      onRowDeleted,
      onRowReset,
      onRowDuplicated,
    ]
  );

  const contextMenuContainerDiv = React.createRef<HTMLDivElement>();

  const { xPos, yPos, isOpen } = useCustomContextMenuHandler(tableRef);

  const style = useMemo(() => {
    return {
      top: yPos + "px",
      left: xPos + "px",
    };
  }, [xPos, yPos]);

  useLayoutEffect(() => {
    if (contextMenuContainerDiv.current) {
      const bounds = contextMenuContainerDiv.current.getBoundingClientRect();
      const contextMenuHeight = bounds.height;
      const availableHeight = document.documentElement.clientHeight;
      if (contextMenuHeight <= availableHeight && contextMenuHeight + yPos > availableHeight) {
        const offset = contextMenuHeight + yPos - availableHeight;
        contextMenuContainerDiv.current.style.top = yPos - offset + "px";
        contextMenuContainerDiv.current.style.left = xPos + 2 + "px";
      }

      const contextMenuWidth = bounds.width;
      const availableWidth = document.documentElement.clientWidth;
      if (contextMenuWidth <= availableWidth && contextMenuWidth + xPos > availableWidth) {
        const offset = contextMenuWidth + xPos - availableWidth;
        contextMenuContainerDiv.current.style.left = xPos - offset - 2 + "px";
      }
    }
  });

  return (
    <>
      {isOpen && (
        <div
          className="context-menu-container"
          style={style}
          onMouseDown={(e) => e.stopPropagation()}
          ref={contextMenuContainerDiv}
        >
          <Menu
            ouiaId="expression-table-context-menu"
            className="table-context-menu"
            onSelect={(e, itemId) => handleOperation(itemId as BeeTableOperation)}
          >
            {operationGroups.map(({ group, items }) => (
              <React.Fragment key={group}>
                <MenuGroup
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
                        key={operation.type + group}
                        itemId={operation.type}
                        isDisabled={!allowedOperations.includes(operation.type)}
                      >
                        {operationLabel(operation.type)}
                      </MenuItem>
                    ))}
                  </MenuList>
                </MenuGroup>
                {items.some((operation) => allowedOperations.includes(operation.type)) && (
                  <Divider key={"divider-" + group} style={{ padding: "16px" }} />
                )}
              </React.Fragment>
            ))}

            <MenuGroup label={"SELECTION"}>
              <MenuList>
                {/* FIXME: Depends on some cells registering setValue (https://github.com/kiegroup/kie-issues/issues/168) */}
                <MenuItem onClick={erase} icon={<CompressIcon />}>
                  {i18n.terms.reset}
                </MenuItem>
                {/* FIXME: Depends on some cells registering getValue (https://github.com/kiegroup/kie-issues/issues/168) */}
                <MenuItem onClick={copy} icon={<CopyIcon />}>
                  {i18n.terms.copy}
                </MenuItem>
                {/* FIXME: Depends on some cells registering getValue AND setValue (https://github.com/kiegroup/kie-issues/issues/168) */}
                <MenuItem onClick={cut} icon={<CutIcon />}>
                  {i18n.terms.cut}
                </MenuItem>
                {/* FIXME: Depends on some cells registering setValue (https://github.com/kiegroup/kie-issues/issues/168)*/}
                <MenuItem onClick={paste} icon={<PasteIcon />}>
                  {i18n.terms.paste}
                </MenuItem>
              </MenuList>
            </MenuGroup>
          </Menu>
        </div>
      )}
    </>
  );
}
