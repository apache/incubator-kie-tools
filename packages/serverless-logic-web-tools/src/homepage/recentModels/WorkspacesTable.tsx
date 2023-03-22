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
import {
  PromiseStateStatus,
  PromiseStateWrapper,
  useLivePromiseState,
} from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import "@patternfly/react-core/dist/styles/base.css";
import {
  TableComposable,
  Tbody,
  Th,
  Thead,
  Tr,
  ThProps,
} from "@patternfly/react-table/dist/js/components/TableComposable";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { splitFiles } from "../../extension";
import { ErrorBoundary } from "../../reactExt/ErrorBoundary";
import {
  WorkspacesTableRow,
  WorkspacesTableRowEmptyState,
  WorkspacesTableRowError,
  WorkspacesTableRowLoading,
} from "./WorkspacesTableRow";

export const columnNames = {
  name: "Name",
  type: "Type",
  created: "Created",
  lastUpdated: "Last updated",
  editableFiles: "Editable files",
  totalFiles: "Total files",
};

export type WorkspacesTableProps = {
  onClearFilters: () => void;
  onWsToggle: (workspaceId: WorkspaceDescriptor["workspaceId"], checked: boolean) => void;
  searchValue: string;
  selectedWorkspaceIds: WorkspaceDescriptor["workspaceId"][];
  workspaceDescriptors: WorkspaceDescriptor[];
};

export type WorkspacesTableRowData = Pick<
  WorkspaceDescriptor,
  "workspaceId" | "origin" | "createdDateISO" | "lastUpdatedDateISO"
> & {
  descriptor: WorkspaceDescriptor;
  editableFiles: WorkspaceFile[];
  totalFiles: number;
  name: string;
  isWsFolder: boolean;
};

export function WorkspacesTable(props: WorkspacesTableProps) {
  const { workspaceDescriptors, selectedWorkspaceIds, onClearFilters, searchValue } = props;
  const [activeSortIndex, setActiveSortIndex] = useState<number>(3);
  const [activeSortDirection, setActiveSortDirection] = useState<"asc" | "desc">("desc");
  const workspaces = useWorkspaces();

  const [allWorkspacePromises] = useLivePromiseState<WorkspaceFile[][]>(
    useMemo(
      () => () => Promise.all(workspaceDescriptors.map((w) => workspaces.getFiles(w))),
      [workspaceDescriptors, workspaces]
    )
  );

  const tableData = useMemo<WorkspacesTableRowData[]>(
    () =>
      allWorkspacePromises.status !== PromiseStateStatus.RESOLVED
        ? []
        : workspaceDescriptors.map((workspace, index) => {
            const { editableFiles, readonlyFiles } = splitFiles(allWorkspacePromises.data[index] ?? []);
            const isWsFolder = editableFiles.length > 1 || readonlyFiles.length > 0;

            return {
              descriptor: workspace,
              workspaceId: workspace.workspaceId,
              name: !isWsFolder && editableFiles.length ? editableFiles[0].nameWithoutExtension : workspace.name,
              origin: workspace.origin,
              createdDateISO: workspace.createdDateISO,
              lastUpdatedDateISO: workspace.lastUpdatedDateISO,
              editableFiles: editableFiles,
              totalFiles: editableFiles.length + readonlyFiles.length,
              isWsFolder,
            };
          }),
    [workspaceDescriptors, allWorkspacePromises.data, allWorkspacePromises.status]
  );

  const filteredTableData = useMemo(() => {
    const searchRegex = new RegExp(searchValue, "i");
    return searchValue ? tableData.filter((e) => e.name.search(searchRegex) >= 0) : tableData;
  }, [searchValue, tableData]);

  const sortedTableData = useMemo<WorkspacesTableRowData[]>(
    () =>
      filteredTableData.sort((a, b) => {
        const aValue = getSortableRowValues(a)[activeSortIndex];
        const bValue = getSortableRowValues(b)[activeSortIndex];
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
    [filteredTableData, activeSortIndex, activeSortDirection]
  );

  const visibleTableData = useMemo(() => sortedTableData, [sortedTableData]);

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
          <PromiseStateWrapper
            promise={allWorkspacePromises}
            pending={<WorkspacesTableRowLoading />}
            rejected={() => <>ERROR</>}
            resolved={() =>
              !visibleTableData.length ? (
                <WorkspacesTableRowEmptyState onClearFilters={onClearFilters} />
              ) : (
                visibleTableData.map((rowData, rowIndex) => (
                  <ErrorBoundary key={rowData.workspaceId} error={<WorkspacesTableRowError rowData={rowData} />}>
                    <WorkspacesTableRow
                      rowData={rowData}
                      rowIndex={rowIndex}
                      isSelected={isWsCheckboxChecked(rowData.workspaceId)}
                      onToggle={(checked) => props.onWsToggle(rowData.workspaceId, checked)}
                    />
                  </ErrorBoundary>
                ))
              )
            }
          />
        </Tbody>
      </TableComposable>
    </>
  );
}

function getSortableRowValues(tableData: WorkspacesTableRowData): (string | number | boolean)[] {
  const { name, isWsFolder, createdDateISO, lastUpdatedDateISO, editableFiles, totalFiles, descriptor } = tableData;
  const workspaceType = !editableFiles.length
    ? ""
    : isWsFolder
    ? "d_" + descriptor.origin.toString()
    : "f_" + editableFiles[0].extension;
  return [name, workspaceType, createdDateISO, lastUpdatedDateISO, editableFiles.length, totalFiles];
}
