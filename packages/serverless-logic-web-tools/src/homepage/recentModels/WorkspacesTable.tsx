/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import {
  Dropdown,
  DropdownItem,
  DropdownToggle,
  DropdownToggleCheckbox,
  Pagination,
  SearchInput,
  Toolbar,
  ToolbarContent,
  ToolbarItem,
} from "@patternfly/react-core/dist/js";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import "@patternfly/react-core/dist/styles/base.css";
import { TrashIcon } from "@patternfly/react-icons/dist/js/icons";
import { TableComposable, Tbody, Th, Thead, Tr } from "@patternfly/react-table";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { KebabDropdown } from "../../editor/EditorToolbar";
import { ErrorBoundary } from "../../reactExt/ErrorBoundary";
import { WorkspacesTableRow, WorkspacesTableRowEmptyState, WorkspacesTableRowError } from "./WorkspacesTableRow";

export const columnNames = {
  name: "Name",
  type: "Type",
  created: "Created",
  lastUpdated: "Last updated",
  editableFiles: "Editable files",
  totalFiles: "Total files",
};

export type WorkspacesTableProps = {
  workspaceDescriptors: WorkspaceDescriptor[];
  selectedWorkspaceIds: WorkspaceDescriptor["workspaceId"][];
  setSelectedWorkspaceIds: React.Dispatch<React.SetStateAction<WorkspaceDescriptor["workspaceId"][]>>;
  setIsConfirmDeleteModalOpen: React.Dispatch<React.SetStateAction<boolean>>;
  onWsToggle: (workspaceId: WorkspaceDescriptor["workspaceId"], checked: boolean) => void;
};

export function WorkspacesTable(props: WorkspacesTableProps) {
  const modelsListPadding = "10px";
  const { workspaceDescriptors, selectedWorkspaceIds, setSelectedWorkspaceIds, setIsConfirmDeleteModalOpen } = props;
  const [isBulkDropDownOpen, setIsBulkDropDownOpen] = useState(false);
  const [searchValue, setSearchValue] = React.useState("");
  const [isLargeKebabOpen, setLargeKebabOpen] = useState(false);

  const tableData = useMemo(() => {
    if (!searchValue) {
      return workspaceDescriptors;
    }
    const regexp = new RegExp(searchValue, "i");
    console.log(
      "### workspaceDescriptors",
      workspaceDescriptors.map((w) => w.name)
    );
    return workspaceDescriptors.filter((w) => w.name.search(regexp) >= 0);
  }, [searchValue, workspaceDescriptors]);

  const onBulkDropDownSelect = useCallback(() => setIsBulkDropDownOpen(false), []);

  const onBulkDropDownToggle = useCallback((isOpen: boolean) => setIsBulkDropDownOpen(isOpen), []);

  const isWsCheckboxChecked = useCallback(
    (workspaceId: WorkspaceDescriptor["workspaceId"]) => selectedWorkspaceIds.includes(workspaceId),
    [selectedWorkspaceIds]
  );

  const onSelectAllWorkspace = useCallback(
    (checked: boolean, workspaceDescriptors: WorkspaceDescriptor[]) => {
      setSelectedWorkspaceIds(checked ? workspaceDescriptors.map((e) => e.workspaceId) : []);
    },
    [setSelectedWorkspaceIds]
  );

  const isBulkCheckBoxChecked = useMemo(() => {
    if (workspaceDescriptors.length && selectedWorkspaceIds.length) {
      return selectedWorkspaceIds.length === workspaceDescriptors.length ? true : null;
    }
    return false;
  }, [workspaceDescriptors, selectedWorkspaceIds]);

  const onBulkDeleteButtonClick = useCallback(() => setIsConfirmDeleteModalOpen(true), [setIsConfirmDeleteModalOpen]);

  const deleteFileDropdownItem = useMemo(() => {
    return (
      <DropdownItem
        key={"delete-dropdown-item"}
        isDisabled={!selectedWorkspaceIds.length}
        onClick={onBulkDeleteButtonClick}
        ouiaId={"delete-file-button"}
        aria-label="Open confirm delete modal"
      >
        <Flex flexWrap={{ default: "nowrap" }}>
          <FlexItem>
            <TrashIcon />
            &nbsp;&nbsp;Delete <b>selected {selectedWorkspaceIds.length > 1 ? "models" : "model"}</b>
          </FlexItem>
        </Flex>
      </DropdownItem>
    );
  }, [selectedWorkspaceIds, onBulkDeleteButtonClick]);

  const bulkDropDownItems = useCallback(
    (workspaceDescriptors: WorkspaceDescriptor[]) => [
      <DropdownItem onClick={() => setSelectedWorkspaceIds([])} key="none" aria-label="Select none">
        Select none (0)
      </DropdownItem>,
      <DropdownItem
        onClick={() => setSelectedWorkspaceIds(workspaceDescriptors.map((e) => e.workspaceId))}
        key="all"
        aria-label="Select All"
      >
        Select all({workspaceDescriptors.length})
      </DropdownItem>,
    ],
    [setSelectedWorkspaceIds]
  );

  const onSearchChange = useCallback((value: string) => {
    setSearchValue(value);
  }, []);

  /* TODO: WorkspacesTable: sort by lastUpdated */
  return (
    <>
      <Toolbar>
        <ToolbarContent style={{ paddingLeft: modelsListPadding, paddingRight: modelsListPadding }}>
          <ToolbarItem alignment={{ default: "alignLeft" }}>
            <Dropdown
              onSelect={onBulkDropDownSelect}
              toggle={
                <DropdownToggle
                  splitButtonItems={[
                    <DropdownToggleCheckbox
                      onChange={(checked) => onSelectAllWorkspace(checked, workspaceDescriptors)}
                      isChecked={isBulkCheckBoxChecked}
                      id="split-button-text-checkbox"
                      key="bulk-check-box"
                      aria-label="Select all"
                    >
                      {selectedWorkspaceIds.length ? `${selectedWorkspaceIds.length} selected` : ""}
                    </DropdownToggleCheckbox>,
                  ]}
                  onToggle={onBulkDropDownToggle}
                  id="toggle-split-button-text"
                />
              }
              isOpen={isBulkDropDownOpen}
              dropdownItems={bulkDropDownItems(workspaceDescriptors)}
              aria-label="Bulk selection dropdown"
            />
          </ToolbarItem>
          <ToolbarItem variant="search-filter">
            <SearchInput
              placeholder="Filter by server name"
              value={searchValue}
              onChange={(_event, value) => onSearchChange(value)}
              onClear={() => onSearchChange("")}
            />
          </ToolbarItem>
          <ToolbarItem>
            <KebabDropdown
              id={"kebab-lg"}
              state={[isLargeKebabOpen, setLargeKebabOpen]}
              items={[deleteFileDropdownItem]}
              menuAppendTo="parent"
            />
          </ToolbarItem>
          <ToolbarItem variant="pagination">
            <Pagination
              titles={{ paginationTitle: "Search filter pagination" }}
              perPageComponent="button"
              itemCount={workspaceDescriptors.length}
              perPage={10}
              page={1}
              widgetId="search-input-mock-pagination"
              isCompact
            />
          </ToolbarItem>
        </ToolbarContent>
      </Toolbar>

      <TableComposable aria-label="Selectable table">
        <Thead>
          <Tr>
            <Th>&nbsp;</Th>
            <Th>{columnNames.name}</Th>
            <Th>{columnNames.type}</Th>
            <Th>{columnNames.created}</Th>
            <Th>{columnNames.lastUpdated}</Th>
            <Th>{columnNames.editableFiles}</Th>
            <Th>{columnNames.totalFiles}</Th>
            <Th></Th>
          </Tr>
        </Thead>
        <Tbody>
          {tableData.map((workspace, rowIndex) => (
            <ErrorBoundary
              key={workspace.workspaceId}
              error={<WorkspacesTableRowError workspaceDescriptor={workspace} />}
            >
              <WorkspacesTableRow
                workspaceDescriptor={workspace}
                rowIndex={rowIndex}
                isSelected={isWsCheckboxChecked(workspace.workspaceId)}
                onToggle={(checked) => props.onWsToggle(workspace.workspaceId, checked)}
              />
            </ErrorBoundary>
          )) || <WorkspacesTableRowEmptyState />}
        </Tbody>
      </TableComposable>
    </>
  );
}
