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
import * as React from "react";
import { useCallback, useMemo } from "react";
import { BeeTableOperation, BeeTableOperationGroup } from "../../api";
import { useCustomContextMenuHandler } from "../../contextMenu/Hooks";
import { useBoxedExpressionEditor } from "../../expressions/BoxedExpressionEditor/BoxedExpressionEditorContext";
import { assertUnreachable } from "../../expressions/ExpressionDefinitionLogicTypeSelector";
import "./BeeTableContextMenuHandler.css";

export interface BeeTableContextMenuHandlerProps {
  lastSelectedColumnIndex: number;
  lastSelectedColumnGroupType: string;
  lastSelectedRowIndex: number;
  operationGroups: BeeTableOperationGroup[];
  allowedOperations: BeeTableOperation[];
  tableRef: React.RefObject<HTMLDivElement | null>;
  //
  onRowAdded?: (args: { beforeIndex: number }) => void;
  onRowDuplicated?: (args: { rowIndex: number }) => void;
  onRowDeleted?: (args: { rowIndex: number }) => void;
  onColumnAdded?: (args: { beforeIndex: number; groupType: string | undefined }) => void;
  onColumnDeleted?: (args: { columnIndex: number; groupType: string | undefined }) => void;
}

export function BeeTableContextMenuHandler({
  tableRef,
  lastSelectedColumnIndex,
  lastSelectedColumnGroupType,
  lastSelectedRowIndex,
  operationGroups,
  allowedOperations,
  onRowAdded,
  onRowDuplicated,
  onRowDeleted,
  onColumnAdded,
  onColumnDeleted,
}: BeeTableContextMenuHandlerProps) {
  const { setCurrentlyOpenContextMenu } = useBoxedExpressionEditor();

  const operationLabel = useCallback((operation: BeeTableOperation) => {
    switch (operation) {
      case BeeTableOperation.ColumnInsertLeft:
        return `Insert left`;
      case BeeTableOperation.ColumnInsertRight:
        return `Insert right`;
      case BeeTableOperation.ColumnDelete:
        return `Delete`;
      case BeeTableOperation.RowInsertAbove:
        return `Insert above`;
      case BeeTableOperation.RowInsertBelow:
        return `Insert below`;
      case BeeTableOperation.RowDelete:
        return `Delete`;
      case BeeTableOperation.RowClear:
        return `Clear`;
      case BeeTableOperation.RowDuplicate:
        return `Duplicate`;
      default:
        assertUnreachable(operation);
    }
  }, []);

  const handleOperation = useCallback(
    (operation: BeeTableOperation) => {
      switch (operation) {
        case BeeTableOperation.ColumnInsertLeft:
          onColumnAdded?.({ beforeIndex: lastSelectedColumnIndex - 1, groupType: lastSelectedColumnGroupType });
          console.info(`Insert column left to ${lastSelectedColumnIndex}`);
          break;
        case BeeTableOperation.ColumnInsertRight:
          onColumnAdded?.({ beforeIndex: lastSelectedColumnIndex, groupType: lastSelectedColumnGroupType });
          console.info(`Insert column right to ${lastSelectedColumnIndex}`);
          break;
        case BeeTableOperation.ColumnDelete:
          onColumnDeleted?.({ columnIndex: lastSelectedColumnIndex - 1, groupType: lastSelectedColumnGroupType });
          console.info(`Delete column ${lastSelectedColumnIndex}`);
          break;
        case BeeTableOperation.RowInsertAbove:
          onRowAdded?.({ beforeIndex: lastSelectedRowIndex });
          console.info(`Insert row above to ${lastSelectedRowIndex}`);
          break;
        case BeeTableOperation.RowInsertBelow:
          onRowAdded?.({ beforeIndex: lastSelectedRowIndex + 1 });
          console.info(`Insert row below to ${lastSelectedRowIndex}`);
          break;
        case BeeTableOperation.RowDelete:
          onRowDeleted?.({ rowIndex: lastSelectedRowIndex });
          console.info(`Delete row ${lastSelectedRowIndex}`);
          break;
        case BeeTableOperation.RowClear:
          console.info(`Clear row ${lastSelectedRowIndex}`);
          break;
        case BeeTableOperation.RowDuplicate:
          onRowDuplicated?.({ rowIndex: lastSelectedRowIndex });
          console.info(`Duplicate row ${lastSelectedRowIndex}`);
          break;
        default:
          assertUnreachable(operation);
      }
      setCurrentlyOpenContextMenu(undefined);
    },
    [
      setCurrentlyOpenContextMenu,
      onColumnAdded,
      lastSelectedColumnIndex,
      lastSelectedColumnGroupType,
      onColumnDeleted,
      onRowAdded,
      lastSelectedRowIndex,
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
          </Menu>
        </div>
      )}
    </>
  );
}
