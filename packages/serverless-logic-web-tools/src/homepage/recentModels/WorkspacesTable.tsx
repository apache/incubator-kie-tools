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
import "@patternfly/react-core/dist/styles/base.css";
import { TableComposable, Tbody, Th, Thead, Tr } from "@patternfly/react-table";
import * as React from "react";
import { useCallback } from "react";
import { ErrorBoundary } from "../../reactExt/ErrorBoundary";
import { WorkspacesTableRow, WorkspacesTableRowError } from "./WorkspacesTableRow";

export const columnNames = {
  name: "Name",
  type: "Type",
  created: "Created",
  lastUpdated: "Last updated",
  editableFiles: "Editable files",
  totalFiles: "Total files",
  singleAction: "Action",
};

export type WorkspacesTableProps = {
  workspaceDescriptors: WorkspaceDescriptor[];
  selectedWorkspaceIds: WorkspaceDescriptor["workspaceId"][];
  setSelectedWorkspaceIds: React.Dispatch<React.SetStateAction<WorkspaceDescriptor["workspaceId"][]>>;
  onWsToggle: (workspaceId: WorkspaceDescriptor["workspaceId"], checked: boolean) => void;
};

export function WorkspacesTable(props: WorkspacesTableProps) {
  const { workspaceDescriptors, selectedWorkspaceIds } = props;

  const isWsCheckboxChecked = useCallback(
    (workspaceId: WorkspaceDescriptor["workspaceId"]) => selectedWorkspaceIds.includes(workspaceId),
    [selectedWorkspaceIds]
  );

  return (
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
        {workspaceDescriptors.map((workspace, rowIndex) => (
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
        ))}
      </Tbody>
    </TableComposable>
  );
}
