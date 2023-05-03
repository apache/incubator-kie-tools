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
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import "@patternfly/react-core/dist/styles/base.css";
import { BanIcon, CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons";
import { TaskIcon } from "@patternfly/react-icons/dist/js/icons/task-icon";
import { ActionsColumn, Td, Tr } from "@patternfly/react-table/dist/esm";
import { TdSelectType } from "@patternfly/react-table/dist/esm/components/Table/base";
import { useCallback, useMemo } from "react";
import { Link, useHistory } from "react-router-dom";
import { routes } from "../../../navigation/Routes";
import "../../../table/Table.css";
import { FileLabel } from "../../../workspace/components/FileLabel";
import { columnNames, WorkspaceFilesTableRowData } from "./WorkspaceFilesTable";

export const workspacesTableRowErrorContent = "Error obtaining workspace information";

export type WorkspaceFilesTableRowProps = {
  /**
   * total files count
   */
  filesCount: number;
  isSelected: boolean;
  /**
   * event fired when the Checkbox is toggled
   */
  onToggle: (selected: boolean) => void;
  rowIndex: TdSelectType["rowIndex"];
  rowData: WorkspaceFilesTableRowData;
};

export function WorkspaceFilesTableRow(props: WorkspaceFilesTableRowProps) {
  const { isSelected, rowIndex, filesCount } = props;
  const { name, extension, isEditable, fileDescriptor } = props.rowData;
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
    if (filesCount > 1) {
      return await workspaces.deleteFile({ file: fileDescriptor });
    }
    workspaces.deleteWorkspace({ workspaceId: fileDescriptor.workspaceId });
    history.push({ pathname: routes.recentModels.path({}) });
  }, [fileDescriptor, history, workspaces, filesCount]);

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
        <TaskIcon />
        &nbsp;&nbsp;&nbsp;<Link to={linkTo}>{name}</Link>
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
          ]}
        />
      </Td>
    </Tr>
  );
}
