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
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Skeleton } from "@patternfly/react-core/dist/js/components/Skeleton";
import { ExclamationTriangleIcon, OutlinedQuestionCircleIcon, SearchIcon } from "@patternfly/react-icons/dist/js/icons";
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
import "../../table/Table.css";

export const workspacesTableRowErrorContent = "Error obtaining workspace information";

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
      !isWsFolder
        ? routes.workspaceWithFilePath.path({
            workspaceId: editableFiles[0].workspaceId,
            fileRelativePath: editableFiles[0].relativePathWithoutExtension,
            extension: editableFiles[0].extension,
          })
        : routes.workspaceWithFiles.path({ workspaceId }),
    [editableFiles, isWsFolder]
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
            &nbsp;&nbsp;&nbsp;<Link to={linkTo}>{name}</Link>
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
      <Tr>
        <Td>&nbsp;</Td>
        <Td colSpan={Object.keys(columnNames).length}>
          <ExclamationTriangleIcon />
          &nbsp;&nbsp;
          {workspacesTableRowErrorContent}&nbsp;
          <Popover
            maxWidth="30%"
            bodyContent={
              <>
                Error obtaining information for the following element:
                <br />
                workspace name: <b>{rowData.descriptor.name}</b>
                <br />
                workspace id: <b>{rowData.workspaceId}</b>
                <br />
                <br />
                To solve the issue, try deleting the workspace and creating it again.
              </>
            }
          >
            <OutlinedQuestionCircleIcon className="pf-c-question-circle-icon" />
          </Popover>
        </Td>
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
      </Tr>
    </>
  );
}

export function WorkspacesTableRowEmptyState(props: { onClearFilters: () => void }) {
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
