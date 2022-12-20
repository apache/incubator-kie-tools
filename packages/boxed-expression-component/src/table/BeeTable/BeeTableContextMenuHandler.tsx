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

import { Menu, MenuGroup, MenuItem, MenuList } from "@patternfly/react-core";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { BeeTableOperation, BeeTableOperationGroup } from "../../api";
import { useCustomContextMenuHandler } from "../../contextMenu/Hooks";
import { useBoxedExpressionEditor } from "../../expressions/BoxedExpressionEditor/BoxedExpressionEditorContext";
import { assertUnreachable } from "../../expressions/ExpressionDefinitionLogicTypeSelector";
import "./BeeTableContextMenuHandler.css";

export interface BeeTableContextMenuHandlerProps {
  lastSelectedColumnIndex: number;
  lastSelectedRowIndex: number;
  operationGroups: BeeTableOperationGroup[];
  allowedOperations: BeeTableOperation[];
  tableRef: React.RefObject<HTMLDivElement | null>;
}

export function BeeTableContextMenuHandler({
  tableRef,
  lastSelectedColumnIndex,
  lastSelectedRowIndex,
  operationGroups,
  allowedOperations,
}: BeeTableContextMenuHandlerProps) {
  const { setCurrentlyOpenContextMenu } = useBoxedExpressionEditor();

  const operationLabel = useCallback(
    (operation: BeeTableOperation) => {
      switch (operation) {
        case BeeTableOperation.ColumnInsertLeft:
          return `Insert column left to ${lastSelectedColumnIndex}`;
        case BeeTableOperation.ColumnInsertRight:
          return `Insert column right to ${lastSelectedColumnIndex}`;
        case BeeTableOperation.ColumnDelete:
          return `Delete column ${lastSelectedColumnIndex}`;
        case BeeTableOperation.RowInsertAbove:
          return `Insert row above to ${lastSelectedRowIndex + 1}`;
        case BeeTableOperation.RowInsertBelow:
          return `Insert row below to ${lastSelectedRowIndex + 1}`;
        case BeeTableOperation.RowDelete:
          return `Delete row ${lastSelectedRowIndex + 1}`;
        case BeeTableOperation.RowClear:
          return `Clear row ${lastSelectedRowIndex + 1}`;
        case BeeTableOperation.RowDuplicate:
          return `Duplicate row ${lastSelectedRowIndex + 1}`;
        default:
          assertUnreachable(operation);
      }
    },
    [lastSelectedColumnIndex, lastSelectedRowIndex]
  );

  const handleOperation = useCallback(
    (operation: BeeTableOperation) => {
      switch (operation) {
        case BeeTableOperation.ColumnInsertLeft:
          console.info(`Insert column left to ${lastSelectedColumnIndex}`);
          break;
        case BeeTableOperation.ColumnInsertRight:
          console.info(`Insert column right to ${lastSelectedColumnIndex}`);
          break;
        case BeeTableOperation.ColumnDelete:
          console.info(`Delete column ${lastSelectedColumnIndex}`);
          break;
        case BeeTableOperation.RowInsertAbove:
          console.info(`Insert row above to ${lastSelectedRowIndex}`);
          break;
        case BeeTableOperation.RowInsertBelow:
          console.info(`Insert row below to ${lastSelectedRowIndex}`);
          break;
        case BeeTableOperation.RowDelete:
          console.info(`Delete row ${lastSelectedRowIndex}`);
          break;
        case BeeTableOperation.RowClear:
          console.info(`Clear row ${lastSelectedRowIndex}`);
          break;
        case BeeTableOperation.RowDuplicate:
          console.info(`Duplicate row ${lastSelectedRowIndex}`);
          break;
        default:
          assertUnreachable(operation);
      }
      setCurrentlyOpenContextMenu(undefined);
    },
    [setCurrentlyOpenContextMenu, lastSelectedColumnIndex, lastSelectedRowIndex]
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
            ouiaId="expression-table-handler-menu"
            className="table-handler-menu"
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
                      data-ouia-component-id={"expression-table-handler-menu-" + operation.name}
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
