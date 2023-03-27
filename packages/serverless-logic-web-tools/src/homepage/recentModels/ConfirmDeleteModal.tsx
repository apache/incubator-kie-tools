/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import * as React from "react";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { Button, Checkbox, Modal, ModalProps, Skeleton } from "@patternfly/react-core/dist/js";
import { useCallback, useEffect, useMemo, useState } from "react";
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { splitFiles } from "../../extension";

export function ConfirmDeleteModal(
  props: {
    onDelete: () => void;
    selectedWorkspaceIds: WorkspaceDescriptor["workspaceId"][];
  } & Pick<ModalProps, "isOpen" | "onClose">
) {
  const { selectedWorkspaceIds, isOpen, onClose, onDelete } = props;
  const [isDeleteCheck, setIsDeleteCheck] = useState(false);
  const [firstSelectedWorkspaceName, setFirstSelectedWorkspaceName] = useState("");
  const [selectedFoldersCount, setSelectedFoldersCount] = useState(0);
  const [elementsTypeName, setElementsTypeName] = useState("models");
  const [dataLoaded, setDataLoaded] = useState(false);
  const [fetchError, setFetchError] = useState(false);
  const workspaces = useWorkspaces();

  const isPlural = useMemo(() => selectedWorkspaceIds.length > 1, [selectedWorkspaceIds]);

  const isWsFolder = useCallback(
    async (workspaceId: WorkspaceDescriptor["workspaceId"]) => {
      const { editableFiles, readonlyFiles } = splitFiles(await workspaces.getFiles({ workspaceId }));
      return editableFiles.length > 1 || readonlyFiles.length > 0;
    },
    [workspaces]
  );

  const getWorkspaceName = useCallback(
    async (workspaceId: WorkspaceDescriptor["workspaceId"]) => {
      if (selectedWorkspaceIds.length !== 1) {
        return "";
      }
      const workspaceData = await workspaces.getWorkspace({ workspaceId });
      return (await isWsFolder(workspaceId))
        ? workspaceData.name
        : (await workspaces.getFiles({ workspaceId }))[0].nameWithoutExtension;
    },
    [isWsFolder, selectedWorkspaceIds, workspaces]
  );

  const onDeleteCheckChange = useCallback((checked: boolean) => {
    setIsDeleteCheck(checked);
  }, []);

  useEffect(() => {
    if (!isOpen) {
      return;
    }

    const allPromises: Promise<void>[] = [];

    setIsDeleteCheck(false);
    setDataLoaded(false);
    setFetchError(false);

    if (selectedWorkspaceIds.length === 1) {
      allPromises.push(getWorkspaceName(selectedWorkspaceIds[0]).then(setFirstSelectedWorkspaceName));
    }

    allPromises.push(
      Promise.all(selectedWorkspaceIds.map(isWsFolder)).then((results) => {
        const foldersCount = results.filter((r) => r).length;
        setSelectedFoldersCount(foldersCount);
        if (isPlural) {
          setElementsTypeName(foldersCount ? "workspaces" : "models");
        } else {
          setElementsTypeName(foldersCount ? "workspace" : "model");
        }
      })
    );

    Promise.all(allPromises)
      .then(() => setDataLoaded(true))
      .catch((error) => {
        console.error("Error retrieving workspace data:", error);
        setFetchError(true);
      });
  }, [selectedWorkspaceIds, isWsFolder, isOpen, getWorkspaceName, isPlural]);

  return (
    <>
      <Modal
        title={`Delete ${elementsTypeName}`}
        titleIconVariant={"warning"}
        isOpen={isOpen && !fetchError}
        onClose={onClose}
        aria-describedby="modal-custom-icon-description"
        actions={[
          dataLoaded ? (
            <Button key="confirm" variant="danger" onClick={onDelete} isDisabled={!isDeleteCheck} aria-label="Delete">
              Delete {elementsTypeName}
            </Button>
          ) : (
            <Skeleton width="100px" key="confirm-skeleton" />
          ),
          <Button key="cancel" variant="link" onClick={onClose} aria-label="Cancel">
            Cancel
          </Button>,
        ]}
        variant="small"
      >
        {dataLoaded ? (
          <span id="modal-custom-icon-description">
            Deleting {isPlural ? "these" : "this"}{" "}
            <b>{isPlural ? selectedWorkspaceIds.length : firstSelectedWorkspaceName}</b> {elementsTypeName}
            {selectedFoldersCount ? ` removes the ${elementsTypeName} and all the models inside.` : "."}
          </span>
        ) : (
          <Skeleton width="80%" />
        )}
        <br />
        <br />
        <Checkbox
          label="I understand that this action cannot be undone."
          id="delete-model-check"
          isChecked={isDeleteCheck}
          onChange={onDeleteCheckChange}
          aria-label="Confirm checkbox delete model"
        />
      </Modal>

      <Modal
        title={`Error retrieving data`}
        titleIconVariant={"danger"}
        isOpen={isOpen && fetchError}
        onClose={onClose}
        aria-describedby="modal-custom-icon-description"
        variant="small"
      >
        <span id="modal-custom-icon-description">An error occurred while loading the data!</span>
      </Modal>
    </>
  );
}
