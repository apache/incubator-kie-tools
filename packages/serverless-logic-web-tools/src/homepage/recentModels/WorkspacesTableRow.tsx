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
import React, { useRef } from "react";
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { Skeleton } from "@patternfly/react-core/dist/js/components/Skeleton";
import { ExclamationTriangleIcon, OutlinedQuestionCircleIcon } from "@patternfly/react-icons/dist/js/icons";
import { FolderIcon } from "@patternfly/react-icons/dist/js/icons/folder-icon";
import { TaskIcon } from "@patternfly/react-icons/dist/js/icons/task-icon";
import { ActionsColumn, Td, Tr } from "@patternfly/react-table/dist/esm";
import { TdSelectType } from "@patternfly/react-table/dist/esm/components/Table/base";
import { useCallback, useMemo } from "react";
import { Link } from "react-router-dom";
import { RelativeDate } from "../../dates/RelativeDate";
import { routes } from "../../navigation/Routes";
import "../../table/Table.css";
import { FileLabel } from "../../workspace/components/FileLabel";
import { WorkspaceLabel } from "../../workspace/components/WorkspaceLabel";
import { columnNames, WorkspacesTableRowData } from "./WorkspacesTable";

export const workspacesTableRowErrorContent = "Error obtaining workspace information";

export type WorkspacesTableRowProps = {
  rowIndex: TdSelectType["rowIndex"];
  rowData: WorkspacesTableRowData;
  isSelected: boolean;
  /**
   * event fired when the Checkbox is toggled
   */
  onToggle: (selected: boolean) => void;

  /**
   * event fired when an element is deleted
   */
  onDelete: (workspaceId: WorkspaceDescriptor["workspaceId"]) => void;
};

export function WorkspacesTableRow(props: WorkspacesTableRowProps) {
  const downloadRef = useRef<HTMLAnchorElement>(null);
  const downloadAllRef = useRef<HTMLAnchorElement>(null);
  const { isSelected, rowIndex, onDelete } = props;
  const { descriptor, editableFiles, totalFiles, name, isWsFolder, workspaceId, createdDateISO, lastUpdatedDateISO } =
    props.rowData;
  const workspaces = useWorkspaces();

  const linkTo = useMemo(
    () =>
      !isWsFolder
        ? routes.workspaceWithFilePath.path({
            workspaceId,
            fileRelativePath: editableFiles[0].relativePathWithoutExtension,
            extension: editableFiles[0].extension,
          })
        : routes.workspaceWithFiles.path({ workspaceId }),
    [editableFiles, isWsFolder, workspaceId]
  );

  const onDeleteWorkspace = useCallback(async () => {
    await workspaces.deleteWorkspace({ workspaceId });
    onDelete(workspaceId);
  }, [workspaceId, onDelete, workspaces]);

  const onDownloadWorkspace = useCallback(async () => {
    if (totalFiles === 0) {
      return;
    }

    if (totalFiles === 1) {
      if (!downloadRef.current) {
        return;
      }
      const file = await workspaces.getFile({
        workspaceId: workspaceId,
        relativePath: editableFiles[0].relativePath,
      });
      if (!file) {
        return;
      }
      const content = await file.getFileContentsAsString();
      const fileBlob = new Blob([content], { type: "text/plain" });
      downloadRef.current.download = `${file.name}`;
      downloadRef.current.href = URL.createObjectURL(fileBlob);
      downloadRef.current.click();
    } else {
      if (!downloadAllRef.current) {
        return;
      }

      const zipBlob = await workspaces.prepareZip({ workspaceId });
      downloadAllRef.current.download = `${name}.zip`;
      downloadAllRef.current.href = URL.createObjectURL(zipBlob);
      downloadAllRef.current.click();
    }
  }, [editableFiles, name, totalFiles, workspaceId, workspaces]);

  return (
    <>
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
          {isWsFolder ? (
            <WorkspaceLabel descriptor={descriptor} />
          ) : (
            <FileLabel extension={editableFiles[0].extension} />
          )}
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
                onClick: onDeleteWorkspace,
              },
              {
                title: "Download",
                onClick: onDownloadWorkspace,
              },
            ]}
          />
        </Td>
      </Tr>
      <a ref={downloadRef} />
      <a ref={downloadAllRef} />
    </>
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
