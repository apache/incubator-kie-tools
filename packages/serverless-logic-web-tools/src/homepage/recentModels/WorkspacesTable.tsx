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
import React from "react";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import {
  TableComposable,
  Tbody,
  Th,
  Thead,
  ThProps,
  Tr,
} from "@patternfly/react-table/dist/js/components/TableComposable";
import { useCallback, useMemo, useState } from "react";
import { ErrorBoundary } from "../../reactExt/ErrorBoundary";
import { TablePaginationProps, TableRowEmptyState } from "../../table";
import { WorkspacesTableRow, WorkspacesTableRowError } from "./WorkspacesTableRow";

export const columnNames = {
  name: "Name",
  type: "Type",
  created: "Created",
  lastUpdated: "Last updated",
  editableFiles: "Editable files",
  totalFiles: "Total files",
};

export type WorkspacesTableProps = Pick<TablePaginationProps, "page" | "perPage"> & {
  onClearFilters: () => void;
  onWsToggle: (workspaceId: WorkspaceDescriptor["workspaceId"], checked: boolean) => void;
  selectedWorkspaceIds: WorkspaceDescriptor["workspaceId"][];

  /**
   * event fired when an element is deleted
   */
  onDelete: (workspaceId: WorkspaceDescriptor["workspaceId"]) => void;
  tableData: WorkspacesTableRowData[];
};

export type WorkspacesTableRowData = {
  descriptor: WorkspaceDescriptor;
  editableFiles: WorkspaceFile[];
  hasErrors: boolean;
  isWsFolder: boolean;
  totalFiles: number;
};

export function WorkspacesTable(props: WorkspacesTableProps) {
  const { selectedWorkspaceIds, onClearFilters, page, perPage, tableData } = props;
  const [activeSortIndex, setActiveSortIndex] = useState<number>(3);
  const [activeSortDirection, setActiveSortDirection] = useState<"asc" | "desc">("desc");

  const sortedTableData = useMemo<WorkspacesTableRowData[]>(
    () =>
      // slice() here is needed to create a copy of tableData and sort the data
      tableData.slice().sort((a, b) => {
        const aValue = getSortableRowValues(a)[activeSortIndex];
        const bValue = getSortableRowValues(b)[activeSortIndex];
        // put items with errors at the top
        if (a.hasErrors) {
          return -1;
        }
        if (typeof aValue === "number") {
          return activeSortDirection === "asc"
            ? (aValue as number) - (bValue as number)
            : (bValue as number) - (aValue as number);
        } else {
          return activeSortDirection === "asc"
            ? (aValue as string).localeCompare(bValue as string)
            : (bValue as string).localeCompare(aValue as string);
        }
      }),
    [tableData, activeSortIndex, activeSortDirection]
  );

  const visibleTableData = useMemo<WorkspacesTableRowData[]>(
    () => sortedTableData.slice((page - 1) * perPage, page * perPage),
    [sortedTableData, page, perPage]
  );

  const getSortParams = useCallback(
    (columnIndex: number): ThProps["sort"] => ({
      sortBy: {
        index: activeSortIndex,
        direction: activeSortDirection,
        defaultDirection: "asc",
      },
      onSort: (_event, index, direction) => {
        setActiveSortIndex(index);
        setActiveSortDirection(direction);
      },
      columnIndex,
    }),
    [activeSortIndex, activeSortDirection]
  );

  const isWsCheckboxChecked = useCallback(
    (workspaceId: WorkspaceDescriptor["workspaceId"]) => selectedWorkspaceIds.includes(workspaceId),
    [selectedWorkspaceIds]
  );

  return (
    <>
      <TableComposable aria-label="Selectable table">
        <Thead>
          <Tr>
            <Th>&nbsp;</Th>
            <Th sort={getSortParams(0)}>{columnNames.name}</Th>
            <Th sort={getSortParams(1)}>{columnNames.type}</Th>
            <Th sort={getSortParams(2)}>{columnNames.created}</Th>
            <Th sort={getSortParams(3)}>{columnNames.lastUpdated}</Th>
            <Th sort={getSortParams(4)}>{columnNames.editableFiles}</Th>
            <Th sort={getSortParams(5)}>{columnNames.totalFiles}</Th>
            <Th></Th>
          </Tr>
        </Thead>
        <Tbody>
          {!visibleTableData.length ? (
            <TableRowEmptyState
              colSpan={Object.keys(columnNames).length + 2}
              elementsName="modules"
              onClearFilters={onClearFilters}
            />
          ) : (
            visibleTableData.map((rowData, rowIndex) => (
              <ErrorBoundary key={rowData.descriptor.workspaceId} error={<WorkspacesTableRowError rowData={rowData} />}>
                {rowData.hasErrors ? (
                  <WorkspacesTableRowError rowData={rowData} />
                ) : (
                  <WorkspacesTableRow
                    rowData={rowData}
                    rowIndex={rowIndex}
                    isSelected={isWsCheckboxChecked(rowData.descriptor.workspaceId)}
                    onToggle={(checked) => props.onWsToggle(rowData.descriptor.workspaceId, checked)}
                    onDelete={props.onDelete}
                  />
                )}
              </ErrorBoundary>
            ))
          )}
        </Tbody>
      </TableComposable>
    </>
  );
}

function getSortableRowValues(tableData: WorkspacesTableRowData): (string | number | boolean)[] {
  const { isWsFolder, editableFiles, totalFiles, descriptor } = tableData;
  const workspaceType = !editableFiles.length
    ? ""
    : isWsFolder
    ? "d_" + descriptor.origin.toString()
    : "f_" + editableFiles[0].extension;
  return [
    descriptor.name,
    workspaceType,
    descriptor.createdDateISO,
    descriptor.lastUpdatedDateISO,
    editableFiles.length,
    totalFiles,
  ];
}
