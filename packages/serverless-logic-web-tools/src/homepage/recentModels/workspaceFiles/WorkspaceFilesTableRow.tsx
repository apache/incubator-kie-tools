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
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { BanIcon, CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons";
import { TaskIcon } from "@patternfly/react-icons/dist/js/icons/task-icon";
import { ActionsColumn, Td, Tr } from "@patternfly/react-table/dist/esm";
import { TdSelectType } from "@patternfly/react-table/dist/esm/components/Table/base";
import { useCallback, useMemo, useRef } from "react";
import { Link, useHistory } from "react-router-dom";
import { routes } from "../../../navigation/Routes";
import "../../../table/Table.css";
import { FileLabel } from "../../../workspace/components/FileLabel";
import { columnNames, WorkspaceFilesTableRowData } from "./WorkspaceFilesTable";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";

export const workspacesTableRowErrorContent = "Error obtaining workspace information";

export type WorkspaceFilesTableRowProps = {
  /**
   * total files count
   */
  totalFilesCount: number;
  isSelected: boolean;
  /**
   * event fired when the Checkbox is toggled
   */
  onToggle: (selected: boolean) => void;
  rowIndex: TdSelectType["rowIndex"];
  rowData: WorkspaceFilesTableRowData;

  /**
   * event fired when an element is deleted
   */
  onDelete: (file: WorkspaceFile) => void;
};

export function WorkspaceFilesTableRow(props: WorkspaceFilesTableRowProps) {
  const { isSelected, rowIndex, totalFilesCount } = props;
  const { name, extension, isEditable, fileDescriptor } = props.rowData;
  const downloadRef = useRef<HTMLAnchorElement>(null);
  const workspaces = useWorkspaces();
  const history = useHistory();

  const linkTo = useMemo(
    () =>
      routes.workspaceWithFilePath.path({
        workspaceId: fileDescriptor.workspaceId,
        fileRelativePath: fileDescriptor.relativePathWithoutExtension,
        extension: fileDescriptor.extension,
      }),
    [fileDescriptor]
  );

  const onDelete = useCallback(async () => {
    if (totalFilesCount > 1) {
      await workspaces.deleteFile({ file: fileDescriptor });
    } else {
      await workspaces.deleteWorkspace({ workspaceId: fileDescriptor.workspaceId });
      history.push({ pathname: routes.recentModels.path({}) });
    }
    props.onDelete(fileDescriptor);
  }, [fileDescriptor, history, workspaces, totalFilesCount, props]);

  const onDownload = useCallback(async () => {
    if (!downloadRef.current) {
      return;
    }
    const content = await fileDescriptor.getFileContentsAsString();
    const fileBlob = new Blob([content], { type: "text/plain" });
    downloadRef.current.download = `${fileDescriptor.name}`;
    downloadRef.current.href = URL.createObjectURL(fileBlob);
    downloadRef.current.click();
  }, [fileDescriptor]);

  return (
    <>
      <Tr key={fileDescriptor.relativePath}>
        <Td
          select={{
            rowIndex,
            onSelect: (_event, checked) => props.onToggle(checked),
            isSelected,
          }}
        />
        <Td dataLabel={columnNames.name}>
          <TaskIcon />
          &nbsp;&nbsp;&nbsp;
          <Tooltip content={fileDescriptor.relativePath}>
            <Link to={linkTo}>{name}</Link>
          </Tooltip>
        </Td>
        <Td dataLabel={columnNames.type}>
          <FileLabel extension={extension} />
        </Td>
        <Td dataLabel={columnNames.isEditable}>
          {isEditable ? <CheckCircleIcon className="success-icon"></CheckCircleIcon> : <BanIcon></BanIcon>}
        </Td>
        <Td isActionCell>
          <ActionsColumn
            items={[
              {
                title: "Delete",
                onClick: onDelete,
              },
              {
                title: "Download",
                onClick: onDownload,
              },
            ]}
          />
        </Td>
      </Tr>
      <a ref={downloadRef} />
    </>
  );
}
