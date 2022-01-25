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

import "./TableHandlerMenu.css";
import * as React from "react";
import { Menu, MenuGroup, MenuItem, MenuList } from "@patternfly/react-core";
import { AllowedOperations, GroupOperations, TableOperation } from "../../api";
import * as _ from "lodash";

export interface TableHandlerMenuProps {
  /** Handler menu groups and items */
  handlerConfiguration: GroupOperations[];
  /** Dynamic Allowed operations */
  allowedOperations: AllowedOperations;
  /** Function to be executed when an operation gets selected */
  onOperation: (operation: TableOperation) => void;
}

export const TableHandlerMenu: React.FunctionComponent<TableHandlerMenuProps> = ({
  handlerConfiguration,
  allowedOperations,
  onOperation,
}) => {
  return (
    <Menu
      ouiaId="expression-table-handler-menu"
      className="table-handler-menu"
      onSelect={(event, itemId) => onOperation(itemId as TableOperation)}
    >
      {handlerConfiguration.map((groupOperation) => (
        <MenuGroup
          key={groupOperation.group}
          label={groupOperation.group}
          className={
            _.every(groupOperation.items, (operation) => !_.includes(allowedOperations, operation.type))
              ? "no-allowed-actions-in-group"
              : ""
          }
        >
          <MenuList>
            {groupOperation.items.map((operation) => (
              <MenuItem
                data-ouia-component-id={"expression-table-handler-menu-" + operation.name}
                key={operation.type}
                itemId={operation.type}
                isDisabled={!_.includes(allowedOperations, operation.type)}
              >
                {operation.name}
              </MenuItem>
            ))}
          </MenuList>
        </MenuGroup>
      ))}
    </Menu>
  );
};
