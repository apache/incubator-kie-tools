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
import { BeeTableContextMenuAllowedOperationsConditions, BeeTableOperation, BeeTableOperationConfig } from "../../api";
import { useCustomContextMenuHandler } from "../../contextMenu/Hooks";
import { useBoxedExpressionEditor } from "../../expressions/BoxedExpressionEditor/BoxedExpressionEditorContext";
import { assertUnreachable } from "../../expressions/ExpressionDefinitionRoot/ExpressionDefinitionLogicTypeSelector";
import "./BeeTableContextMenuHandler.css";
import {
  BeeTableSelection,
  useBeeTableSelection,
  useBeeTableSelectionDispatch,
} from "../../selection/BeeTableSelectionContext";
import * as ReactTable from "react-table";
import * as _ from "lodash";
import CutIcon from "@patternfly/react-icons/dist/js/icons/cut-icon";
import CopyIcon from "@patternfly/react-icons/dist/js/icons/copy-icon";
import PasteIcon from "@patternfly/react-icons/dist/js/icons/paste-icon";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { EmptyState } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Title } from "@patternfly/react-core/dist/js/components/Title";

export interface BeeTableContextMenuHandlerProps {
  tableRef: React.RefObject<HTMLDivElement | null>;
  operationConfig: BeeTableOperationConfig | undefined;
  allowedOperations: (conditions: BeeTableContextMenuAllowedOperationsConditions) => BeeTableOperation[];
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
  allowedOperations,
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

  const { activeCell, selectionStart, selectionEnd } = useBeeTableSelection();
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
    return columns?.[columnIndex];
  }, [activeCell, columns]);

  const operationGroups = useMemo(() => {
    if (!activeCell) {
      return [];
    }
    if (_.isArray(operationConfig)) {
      return operationConfig;
    }
    return (operationConfig ?? {})[column?.groupType || ""];
  }, [activeCell, column?.groupType, operationConfig]);

  const allOperations = useMemo(() => {
    return operationGroups.flatMap(({ group, items }) => items);
  }, [operationGroups]);

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

  const allowedOperationsForSelection = useMemo(() => {
    return allowedOperations({
      selection,
      column,
      columns,
    } as BeeTableContextMenuAllowedOperationsConditions);
  }, [allowedOperations, selection, column, columns]);
  const hasAllowedOperations = useMemo(() => {
    return allOperations.some((operation) => allowedOperationsForSelection.includes(operation.type));
  }, [allOperations, allowedOperationsForSelection]);

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
            {hasAllowedOperations &&
              operationGroups.map(({ group, items }, operationGroupIndex) => (
                <React.Fragment key={group}>
                  {items.some((operation) => allowedOperationsForSelection.includes(operation.type)) &&
                    operationGroupIndex > 0 && <Divider key={"divider-" + group} style={{ padding: "16px" }} />}
                  <MenuGroup
                    label={group}
                    className={
                      items.every((operation) => !allowedOperationsForSelection.includes(operation.type))
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
                          isDisabled={!allowedOperationsForSelection.includes(operation.type)}
                        >
                          {operationLabel(operation.type)}
                        </MenuItem>
                      ))}
                    </MenuList>
                  </MenuGroup>
                </React.Fragment>
              ))}
            {!hasAllowedOperations && (
              <EmptyState>
                <Title headingLevel="h6">{i18n.noOperationsAvailable}</Title>
              </EmptyState>
            )}
          </Menu>
        </div>
      )}
    </>
  );
}
