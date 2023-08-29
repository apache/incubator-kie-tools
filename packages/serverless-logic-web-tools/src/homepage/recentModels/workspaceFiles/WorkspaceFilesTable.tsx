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
import * as React from "react";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import {
  TableComposable,
  Tbody,
  Th,
  Thead,
  ThProps,
  Tr,
} from "@patternfly/react-table/dist/js/components/TableComposable";
import { useCallback, useMemo, useState } from "react";
import { isEditable } from "../../../extension";
import { WorkspaceFilesTableRow } from "./WorkspaceFilesTableRow";
import { TablePaginationProps, TableRowEmptyState } from "../../../table";

export const columnNames = {
  name: "Name",
  type: "Type",
  isEditable: "Editable",
};

export type WorkspaceFilesTableProps = Pick<TablePaginationProps, "page" | "perPage"> & {
  onClearFilters: () => void;
  onFileToggle: (workspaceFile: WorkspaceFile, checked: boolean) => void;
  selectedWorkspaceFiles: WorkspaceFile[];
  workspaceFiles: WorkspaceFile[];

  /**
   * event fired when an element is deleted
   */
  onDelete: (file: WorkspaceFile) => void;
};

export type WorkspaceFilesTableRowData = Pick<WorkspaceFile, "extension"> & {
  fileDescriptor: WorkspaceFile;
  isEditable: boolean;
  name: string;
};

export function WorkspaceFilesTable(props: WorkspaceFilesTableProps) {
  const { workspaceFiles, selectedWorkspaceFiles, page, perPage, onClearFilters } = props;
  const [activeSortIndex, setActiveSortIndex] = useState<number>(0);
  const [activeSortDirection, setActiveSortDirection] = useState<"asc" | "desc">("desc");

  const tableData = useMemo<WorkspaceFilesTableRowData[]>(
    () =>
      workspaceFiles.map((f) => ({
        extension: f.extension,
        fileDescriptor: f,
        isEditable: isEditable(f.relativePath),
        name: f.nameWithoutExtension.trim().length ? f.nameWithoutExtension : f.name,
        relativePath: f.relativePath,
        workspaceId: f.workspaceId,
      })),
    [workspaceFiles]
  );

  const sortedTableData = useMemo<WorkspaceFilesTableRowData[]>(
    () =>
      // slice() here is needed to create a copy of tableData and sort the data
      tableData.slice().sort((a, b) => {
        const aValue = getSortableRowValues(a)[activeSortIndex];
        const bValue = getSortableRowValues(b)[activeSortIndex];
        if (typeof aValue === "number" || typeof aValue === "boolean") {
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

  const visibleTableData = useMemo<WorkspaceFilesTableRowData[]>(
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

  const isFileCheckboxChecked = useCallback(
    (rowData: WorkspaceFilesTableRowData) =>
      selectedWorkspaceFiles.some(
        (f) =>
          f.workspaceId === rowData.fileDescriptor.workspaceId && f.relativePath === rowData.fileDescriptor.relativePath
      ),
    [selectedWorkspaceFiles]
  );

  return (
    <>
      <TableComposable aria-label="Selectable table">
        <Thead>
          <Tr>
            <Th>&nbsp;</Th>
            <Th sort={getSortParams(0)}>{columnNames.name}</Th>
            <Th sort={getSortParams(1)}>{columnNames.type}</Th>
            <Th sort={getSortParams(2)}>{columnNames.isEditable}</Th>
            <Th></Th>
          </Tr>
        </Thead>
        <Tbody>
          {!visibleTableData.length ? (
            <TableRowEmptyState
              colSpan={Object.keys(columnNames).length + 2}
              elementsName="files"
              onClearFilters={onClearFilters}
            />
          ) : (
            visibleTableData.map((rowData, rowIndex) => (
              <WorkspaceFilesTableRow
                isSelected={isFileCheckboxChecked(rowData)}
                key={rowIndex}
                onToggle={(checked) => props.onFileToggle(rowData.fileDescriptor, checked)}
                rowData={rowData}
                rowIndex={rowIndex}
                onDelete={props.onDelete}
              />
            ))
          )}
        </Tbody>
      </TableComposable>
    </>
  );
}

function getSortableRowValues(tableData: WorkspaceFilesTableRowData): (string | number | boolean)[] {
  const { name, extension, isEditable } = tableData;
  return [name, extension, isEditable];
}
