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

import { PromiseStateWrapper } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { useWorkspacePromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspaceHooks";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { PerPageOptions } from "@patternfly/react-core/dist/js/components/Pagination";
import { Alert, AlertActionCloseButton, AlertProps } from "@patternfly/react-core/dist/js/components/Alert";
import { AlertGroup } from "@patternfly/react-core/dist/js/components/AlertGroup";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import * as React from "react";
import { useCallback, useState, useMemo } from "react";
import { ConfirmDeleteModal } from "./ConfirmDeleteModal";
import { TableToolbar } from "../../table/TableToolbar";
import { WorkspacesTable } from "./WorkspacesTable";
import { TablePagination } from "../../table/TablePagination";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { remove } from "jszip";

export interface Props {
  workspaceId: string;
}

const perPageOptions: PerPageOptions[] = [5, 10, 20, 50, 100].map((n) => ({
  title: n.toString(),
  value: n,
}));

export function WorkspaceFiles(props: Props) {
  const workspacePromise = useWorkspacePromise(props.workspaceId);
  const [selectedWorkspaceFiles, setSelectedWorkspaceFiles] = useState<WorkspaceFile[]>([]);
  const [isConfirmDeleteModalOpen, setIsConfirmDeleteModalOpen] = useState(false);
  const [alerts, setAlerts] = useState<Partial<AlertProps>[]>([]);
  const [searchValue, setSearchValue] = React.useState("");
  const [page, setPage] = React.useState(1);
  const [perPage, setPerPage] = React.useState(5);
  const workspaces = useWorkspaces();
  const isSelectedWorkspaceFilesPlural = useMemo(() => selectedWorkspaceFiles.length > 1, [selectedWorkspaceFiles]);
  const selectedElementTypesName = useMemo(
    () => (isSelectedWorkspaceFilesPlural ? "files" : "file"),
    [isSelectedWorkspaceFilesPlural]
  );

  const deleteModalMessage = useMemo(
    () => (
      <>
        Deleting {isSelectedWorkspaceFilesPlural ? "these" : "this"}{" "}
        <b>{isSelectedWorkspaceFilesPlural ? selectedWorkspaceFiles.length : selectedWorkspaceFiles[0]?.name}</b>{" "}
        {selectedElementTypesName}
      </>
    ),
    [isSelectedWorkspaceFilesPlural, selectedWorkspaceFiles, selectedElementTypesName]
  );

  const onConfirmDeleteModalClose = useCallback(() => setIsConfirmDeleteModalOpen(false), []);

  const addAlert = useCallback(
    (title: string, variant: AlertProps["variant"], key: React.Key = new Date().getTime()) => {
      setAlerts((prevAlerts) => [...prevAlerts, { title, variant, key }]);
    },
    []
  );

  const removeAlert = useCallback((key: React.Key) => {
    setAlerts((prevAlerts) => [...prevAlerts.filter((alert) => alert.key !== key)]);
  }, []);

  const onConfirmDeleteModalDelete = useCallback(async () => {
    const filesWord = selectedWorkspaceFiles.length > 1 ? "Files" : "File";
    setIsConfirmDeleteModalOpen(false);

    Promise.all(selectedWorkspaceFiles.map((file) => workspaces.deleteFile({ file })))
      .then(() => {
        addAlert(`${filesWord} deleted successfully`, "success");
      })
      .catch((e) => {
        console.error(e);
        addAlert(
          `Oops, something went wrong while trying to delete the selected ${filesWord}. Please refresh the page and try again. If the problem persists, you can try deleting site data for this application in your browser's settings.`,
          "danger"
        );
      })
      .finally(() => {
        setSelectedWorkspaceFiles([]);
      });
  }, [selectedWorkspaceFiles, addAlert, workspaces]);

  const onFileToggle = useCallback((workspaceFile: WorkspaceFile, checked: boolean) => {
    setSelectedWorkspaceFiles((prevSelected) => [...prevSelected, workspaceFile]);
  }, []);

  const onToggleAllElements = useCallback((checked: boolean, workspace: ActiveWorkspace) => {
    setSelectedWorkspaceFiles(checked ? workspace.files : []);
  }, []);

  const onClearFilters = useCallback(() => {
    setSearchValue("");
  }, []);

  return (
    <PromiseStateWrapper
      promise={workspacePromise}
      rejected={(e) => <>Error fetching workspaces: {e + ""}</>}
      resolved={(workspace: ActiveWorkspace) => {
        const files = workspace.files;
        const filesCount = files.length;
        /* TODO: WorkspaceFiles: remove me */
        console.log("### workspace.files", workspace.files);

        return (
          <>
            <AlertGroup isToast isLiveRegion>
              {alerts.map(
                ({ key, variant, title }) =>
                  key && (
                    <Alert
                      variant={variant}
                      title={title}
                      timeout
                      onTimeout={() => removeAlert(key)}
                      actionClose={
                        <AlertActionCloseButton
                          title={title as string}
                          variantLabel={`${variant} alert`}
                          onClose={() => removeAlert(key)}
                        />
                      }
                      key={key}
                    />
                  )
              )}
            </AlertGroup>
            <Page>
              <PageSection variant={"light"}>
                <TextContent>
                  <Text component={TextVariants.h1}>Recent models</Text>
                  <Text component={TextVariants.p}>
                    Use your recent models from GitHub Repository, a GitHub Gist or saved in your browser.
                  </Text>
                </TextContent>
              </PageSection>

              <PageSection isFilled aria-label="workspaces-table-section">
                <PageSection variant={"light"} padding={{ default: "noPadding" }}>
                  {filesCount > 0 && (
                    <>
                      <TableToolbar
                        itemCount={filesCount}
                        onDeleteActionButtonClick={() => setIsConfirmDeleteModalOpen(true)}
                        onToggleAllElements={(checked) => onToggleAllElements(checked, workspace)}
                        searchValue={searchValue}
                        selectedElementsCount={selectedWorkspaceFiles.length}
                        setSearchValue={setSearchValue}
                        page={page}
                        perPage={perPage}
                        perPageOptions={perPageOptions}
                        setPage={setPage}
                        setPerPage={setPerPage}
                      />

                      <TablePagination
                        itemCount={filesCount}
                        page={page}
                        perPage={perPage}
                        perPageOptions={perPageOptions}
                        setPage={setPage}
                        setPerPage={setPerPage}
                        variant="bottom"
                      />
                    </>
                  )}
                  {files.length === 0 && (
                    <Bullseye>
                      <EmptyState>
                        <EmptyStateIcon icon={CubesIcon} />
                        <Title headingLevel="h4" size="lg">
                          {`Nothing here`}
                        </Title>
                        <EmptyStateBody>{`Start by adding a new model`}</EmptyStateBody>
                      </EmptyState>
                    </Bullseye>
                  )}
                </PageSection>
              </PageSection>
            </Page>
            <ConfirmDeleteModal
              isOpen={isConfirmDeleteModalOpen}
              onClose={onConfirmDeleteModalClose}
              onDelete={() => onConfirmDeleteModalDelete()}
              elementsTypeName={selectedElementTypesName}
              deleteMessage={deleteModalMessage}
            />
          </>
        );
      }}
    />
  );
}
