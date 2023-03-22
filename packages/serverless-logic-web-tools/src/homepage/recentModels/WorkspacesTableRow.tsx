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
import * as React from "react";
import { useMemo } from "react";
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { Bullseye, Button, EmptyState, EmptyStateBody, EmptyStateIcon, Title } from "@patternfly/react-core/dist/js";
import { Skeleton } from "@patternfly/react-core/dist/js/components/Skeleton";
import "@patternfly/react-core/dist/styles/base.css";
import { ExclamationTriangleIcon, SearchIcon } from "@patternfly/react-icons/dist/js/icons";
import { FolderIcon } from "@patternfly/react-icons/dist/js/icons/folder-icon";
import { TaskIcon } from "@patternfly/react-icons/dist/js/icons/task-icon";
import { ActionsColumn, Td, Tr } from "@patternfly/react-table/dist/esm";
import { TdSelectType } from "@patternfly/react-table/dist/esm/components/Table/base";
import { Link } from "react-router-dom";
import { RelativeDate } from "../../dates/RelativeDate";
import { routes } from "../../navigation/Routes";
import { FileLabel } from "../../workspace/components/FileLabel";
import { WorkspaceLabel } from "../../workspace/components/WorkspaceLabel";
import { columnNames, WorkspacesTableRowData } from "./WorkspacesTable";

export type WorkspacesTableRowProps = {
  rowIndex: TdSelectType["rowIndex"];
  rowData: WorkspacesTableRowData;
  isSelected: boolean;
  /**
   * event fired when the Checkbox is toggled
   */
  onToggle: (selected: boolean) => void;
};

export function WorkspacesTableRow(props: WorkspacesTableRowProps) {
  const { isSelected, rowIndex } = props;
  const { descriptor, editableFiles, totalFiles, name, isWsFolder, workspaceId, createdDateISO, lastUpdatedDateISO } =
    props.rowData;
  const workspaces = useWorkspaces();

  const linkTo = useMemo(
    () =>
      routes.workspaceWithFilePath.path({
        workspaceId: editableFiles[0].workspaceId,
        fileRelativePath: editableFiles[0].relativePathWithoutExtension,
        extension: editableFiles[0].extension,
      }),
    [editableFiles]
  );

  return (
    <Tr key={name}>
      <Td
        select={{
          rowIndex,
          onSelect: (_event, checked) => props.onToggle(checked),
          isSelected,
        }}
      />
      <Td dataLabel={columnNames.name}>
        {isWsFolder ? (
          <>
            <FolderIcon />
            &nbsp;&nbsp;&nbsp;{name}
          </>
        ) : (
          <>
            <TaskIcon />
            &nbsp;&nbsp;&nbsp;<Link to={linkTo}>{name}</Link>
          </>
        )}
      </Td>
      <Td dataLabel={columnNames.type}>
        {isWsFolder ? <WorkspaceLabel descriptor={descriptor} /> : <FileLabel extension={editableFiles[0].extension} />}
      </Td>
      <Td dataLabel={columnNames.created}>
        <RelativeDate date={new Date(createdDateISO ?? "")} />
      </Td>
      <Td dataLabel={columnNames.lastUpdated}>
        <RelativeDate date={new Date(lastUpdatedDateISO ?? "")} />
      </Td>
      <Td dataLabel={columnNames.editableFiles}>{editableFiles.length}</Td>
      <Td dataLabel={columnNames.totalFiles}>{totalFiles}</Td>
      <Td isActionCell>
        <ActionsColumn
          items={[
            {
              title: "Delete",
              onClick: () => workspaces.deleteWorkspace({ workspaceId }),
            },
          ]}
        />
      </Td>
    </Tr>
  );
}

export function WorkspacesTableRowError(props: { rowData: WorkspacesTableRowData }) {
  const { rowData } = props;
  const workspaces = useWorkspaces();

  return (
    <>
      <Td></Td>
      <Td>
        <ExclamationTriangleIcon />
        &nbsp;&nbsp;
        {`There was an error obtaining information for '${rowData.workspaceId}'`}
      </Td>
      <Td></Td>
      <Td></Td>
      <Td></Td>
      <Td></Td>
      <Td></Td>
      <Td isActionCell>
        <ActionsColumn
          items={[
            {
              title: "Delete",
              onClick: () => workspaces.deleteWorkspace({ workspaceId: rowData.workspaceId }),
            },
          ]}
        />
      </Td>
    </>
  );
}

export function WorkspacesTableRowEmptyState(props: { onClearFilters: () => void }) {
  /* TODO: WorkspacesTableRow: component to be tested */
  return (
    <Tr>
      <Td colSpan={Object.keys(columnNames).length + 2}>
        <Bullseye>
          <EmptyState variant="small">
            <EmptyStateIcon icon={SearchIcon} />
            <Title headingLevel="h2" size="lg">
              No matching modules found
            </Title>
            <EmptyStateBody>This filter criteria matches no groups. Try changing your filter settings.</EmptyStateBody>
            <Button variant="link" onClick={props.onClearFilters}>
              Clear all filters
            </Button>
          </EmptyState>
        </Bullseye>
      </Td>
    </Tr>
  );
}

export function WorkspacesTableRowLoading() {
  return (
    <tr>
      <Td>
        <Skeleton />
      </Td>
      <Td>
        <Skeleton />
      </Td>
      <Td>
        <Skeleton />
      </Td>
      <Td>
        <Skeleton />
      </Td>
      <Td>
        <Skeleton />
      </Td>
      <Td>
        <Skeleton />
      </Td>
      <Td>
        <Skeleton />
      </Td>
      <Td>
        <Skeleton />
      </Td>
    </tr>
  );
}
