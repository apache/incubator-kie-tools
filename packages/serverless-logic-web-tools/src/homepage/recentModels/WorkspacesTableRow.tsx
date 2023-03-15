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
import { PromiseStateWrapper } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { useWorkspacePromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspaceHooks";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { Skeleton } from "@patternfly/react-core/dist/js/components/Skeleton";
import "@patternfly/react-core/dist/styles/base.css";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons";
import { FolderIcon } from "@patternfly/react-icons/dist/js/icons/folder-icon";
import { TaskIcon } from "@patternfly/react-icons/dist/js/icons/task-icon";
import { ActionsColumn, Td, Tr } from "@patternfly/react-table";
import { TdSelectType } from "@patternfly/react-table/dist/esm/components/Table/base";
import { useCallback, useMemo } from "react";
import { Link } from "react-router-dom";
import { RelativeDate } from "../../dates/RelativeDate";
import { splitFiles } from "../../extension";
import { routes } from "../../navigation/Routes";
import { FileLabel } from "../../workspace/components/FileLabel";
import { WorkspaceLabel } from "../../workspace/components/WorkspaceLabel";
import { columnNames } from "./WorkspacesTable";

export type WorkspacesTableRowProps = {
  rowIndex: TdSelectType["rowIndex"];
  workspaceDescriptor: WorkspaceDescriptor;
  isSelected: boolean;
  /**
   * event fired when the Checkbox is toggled
   */
  onToggle: (selected: boolean) => void;
};

export function WorkspacesTableRow(props: WorkspacesTableRowProps) {
  const { isSelected, rowIndex, workspaceDescriptor } = props;
  const workspacePromise = useWorkspacePromise(workspaceDescriptor.workspaceId);
  const workspaces = useWorkspaces();

  const { editableFiles, readonlyFiles } = useMemo(
    () => splitFiles(workspacePromise.data?.files ?? []),
    [workspacePromise.data?.files]
  );

  const isWsFolder = useMemo(
    () => editableFiles.length > 1 || readonlyFiles.length > 0,
    [editableFiles, readonlyFiles]
  );

  const workspaceName = useMemo(
    () => (!isWsFolder && editableFiles.length ? editableFiles[0].nameWithoutExtension : workspaceDescriptor.name),
    [isWsFolder, editableFiles, workspaceDescriptor.name]
  );

  const renderModel = useCallback(() => {
    const linkTo = routes.workspaceWithFilePath.path({
      workspaceId: editableFiles[0].workspaceId,
      fileRelativePath: editableFiles[0].relativePathWithoutExtension,
      extension: editableFiles[0].extension,
    });

    return (
      <>
        <Td dataLabel={columnNames.name}>
          <TaskIcon />
          &nbsp;&nbsp;&nbsp;
          <Link to={linkTo}>{workspaceName}</Link>
        </Td>
        <Td dataLabel={columnNames.type}>
          <FileLabel extension={editableFiles[0].extension} />
        </Td>
        <Td dataLabel={columnNames.created}>
          <RelativeDate date={new Date(workspaceDescriptor.createdDateISO ?? "")} />
        </Td>
        <Td dataLabel={columnNames.lastUpdated}>
          <RelativeDate date={new Date(workspaceDescriptor.lastUpdatedDateISO ?? "")} />
        </Td>
        <Td dataLabel={columnNames.editableFiles}></Td>
        <Td dataLabel={columnNames.totalFiles}></Td>
      </>
    );
  }, [editableFiles, workspaceDescriptor, workspaceName]);

  const renderFolder = useCallback(() => {
    return (
      <>
        <Td dataLabel={columnNames.name}>
          <FolderIcon />
          &nbsp;&nbsp;&nbsp;
          {workspaceName}
        </Td>
        <Td dataLabel={columnNames.type}>
          <WorkspaceLabel descriptor={workspaceDescriptor} />
        </Td>
        <Td dataLabel={columnNames.created}>
          <RelativeDate date={new Date(workspaceDescriptor.createdDateISO ?? "")} />
        </Td>
        <Td dataLabel={columnNames.lastUpdated}>
          <RelativeDate date={new Date(workspaceDescriptor.lastUpdatedDateISO ?? "")} />
        </Td>
        <Td dataLabel={columnNames.editableFiles}>{editableFiles.length}</Td>
        <Td dataLabel={columnNames.totalFiles}>{editableFiles.length + readonlyFiles.length}</Td>
      </>
    );
  }, [editableFiles, readonlyFiles, workspaceDescriptor, workspaceName]);

  return (
    <Tr key={workspaceDescriptor.name}>
      <PromiseStateWrapper
        promise={workspacePromise}
        pending={<WorkspacesTableRowLoading />}
        rejected={() => <>ERROR</>}
        resolved={() => {
          return (
            <>
              <Td
                select={{
                  rowIndex,
                  onSelect: (_event, checked) => props.onToggle(checked),
                  isSelected,
                }}
              />
              {isWsFolder ? renderFolder() : renderModel()}
              <Td isActionCell>
                <ActionsColumn
                  items={[
                    {
                      title: "Delete",
                      onClick: () => workspaces.deleteWorkspace({ workspaceId: workspaceDescriptor.workspaceId }),
                    },
                  ]}
                />
              </Td>
            </>
          );
        }}
      />
    </Tr>
  );
}

export function WorkspacesTableRowError(props: { workspaceDescriptor: WorkspaceDescriptor }) {
  return (
    <>
      <Td></Td>
      <Td>
        <ExclamationTriangleIcon />
        &nbsp;&nbsp;
        {`There was an error obtaining information for '${props.workspaceDescriptor.workspaceId}'`}
      </Td>
      <Td></Td>
      <Td></Td>
      <Td></Td>
      <Td></Td>
      <Td></Td>
      <Td></Td>
    </>
  );
}

function WorkspacesTableRowLoading() {
  return (
    <>
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
    </>
  );
}
