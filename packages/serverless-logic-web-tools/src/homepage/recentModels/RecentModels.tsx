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
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { useWorkspaceDescriptorsPromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspacesHooks";
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
import { useCallback, useState, useEffect, useMemo } from "react";
import { ConfirmDeleteModal } from "../../table/ConfirmDeleteModal";
import { TableToolbar } from "../../table/TableToolbar";
import { WorkspacesTable } from "./WorkspacesTable";
import { TablePagination } from "../../table/TablePagination";
import { splitFiles } from "../../extension";

const perPageOptions: PerPageOptions[] = [5, 10, 20, 50, 100].map((n) => ({
  title: n.toString(),
  value: n,
}));

export function RecentModels() {
  const workspaceDescriptorsPromise = useWorkspaceDescriptorsPromise();
  const [selectedWorkspaceIds, setSelectedWorkspaceIds] = useState<WorkspaceDescriptor["workspaceId"][]>([]);
  const [isConfirmDeleteModalOpen, setIsConfirmDeleteModalOpen] = useState(false);
  const [alerts, setAlerts] = useState<Partial<AlertProps>[]>([]);
  const [searchValue, setSearchValue] = React.useState("");
  const [page, setPage] = React.useState(1);
  const [perPage, setPerPage] = React.useState(5);
  const workspaces = useWorkspaces();
  const [selectedFoldersCount, setSelectedFoldersCount] = useState(0);
  const [firstSelectedWorkspaceName, setFirstSelectedWorkspaceName] = useState("");
  const [deleteModalDataLoaded, setDeleteModalDataLoaded] = useState(false);
  const [deleteModalFetchError, setDeleteModalFetchError] = useState(false);
  const isSelectedWorkspacePlural = useMemo(() => selectedWorkspaceIds.length > 1, [selectedWorkspaceIds]);

  const selectedElementTypesName = useMemo(() => {
    if (selectedWorkspaceIds.length > 1) {
      return selectedFoldersCount ? "workspaces" : "models";
    }
    return selectedFoldersCount ? "workspace" : "model";
  }, [selectedFoldersCount, selectedWorkspaceIds]);

  const deleteModalMessage = useMemo(
    () => (
      <>
        Deleting {isSelectedWorkspacePlural ? "these" : "this"}{" "}
        <b>{isSelectedWorkspacePlural ? selectedWorkspaceIds.length : firstSelectedWorkspaceName}</b>{" "}
        {selectedElementTypesName}
        {selectedFoldersCount ? ` removes the ${selectedElementTypesName} and all the models inside.` : "."}
      </>
    ),
    [
      isSelectedWorkspacePlural,
      selectedWorkspaceIds,
      firstSelectedWorkspaceName,
      selectedElementTypesName,
      selectedFoldersCount,
    ]
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

  const onConfirmDeleteModalDelete = useCallback(
    async (workspaceDescriptors: WorkspaceDescriptor[]) => {
      const modelsWord = selectedWorkspaceIds.length > 1 ? "Models" : "Model";
      setIsConfirmDeleteModalOpen(false);

      Promise.all(
        workspaceDescriptors
          .filter((w) => selectedWorkspaceIds.includes(w.workspaceId))
          .map((w) => workspaces.deleteWorkspace(w))
      )
        .then(() => {
          addAlert(`${modelsWord} deleted successfully`, "success");
        })
        .catch((e) => {
          console.error(e);
          addAlert(
            `Oops, something went wrong while trying to delete the selected ${modelsWord}. Please refresh the page and try again. If the problem persists, you can try deleting site data for this application in your browser's settings.`,
            "danger"
          );
        })
        .finally(() => {
          setSelectedWorkspaceIds([]);
        });
    },
    [selectedWorkspaceIds, addAlert, workspaces]
  );

  const onWsToggle = useCallback((workspaceId: WorkspaceDescriptor["workspaceId"], checked: boolean) => {
    setSelectedWorkspaceIds((prevSelected) => {
      const otherSelectedIds = prevSelected.filter((r) => r !== workspaceId);
      return checked ? [...otherSelectedIds, workspaceId] : otherSelectedIds;
    });
  }, []);

  const onToggleAllElements = useCallback((checked: boolean, workspaceDescriptors: WorkspaceDescriptor[]) => {
    setSelectedWorkspaceIds(checked ? workspaceDescriptors.map((e) => e.workspaceId) : []);
  }, []);

  const onClearFilters = useCallback(() => {
    setSearchValue("");
  }, []);

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

  useEffect(() => {
    Promise.all([
      Promise.all(selectedWorkspaceIds.map(isWsFolder)).then((results) => {
        const foldersCount = results.filter((r) => r).length;
        setSelectedFoldersCount(foldersCount);
      }),
      getWorkspaceName(selectedWorkspaceIds[0]).then(setFirstSelectedWorkspaceName),
    ])
      .then(() => setDeleteModalDataLoaded(true))
      .catch(() => setDeleteModalFetchError(true));
  }, [getWorkspaceName, selectedWorkspaceIds, isWsFolder]);

  return (
    <PromiseStateWrapper
      promise={workspaceDescriptorsPromise}
      rejected={(e) => <>Error fetching workspaces: {e + ""}</>}
      resolved={(workspaceDescriptors: WorkspaceDescriptor[]) => {
        const itemCount = workspaceDescriptors.length;

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
                  {itemCount > 0 && (
                    <>
                      <TableToolbar
                        itemCount={itemCount}
                        onDeleteActionButtonClick={() => setIsConfirmDeleteModalOpen(true)}
                        onToggleAllElements={(checked) => onToggleAllElements(checked, workspaceDescriptors)}
                        searchValue={searchValue}
                        selectedElementsCount={selectedWorkspaceIds.length}
                        setSearchValue={setSearchValue}
                        page={page}
                        perPage={perPage}
                        perPageOptions={perPageOptions}
                        setPage={setPage}
                        setPerPage={setPerPage}
                      />
                      <WorkspacesTable
                        page={page}
                        perPage={perPage}
                        onClearFilters={onClearFilters}
                        onWsToggle={onWsToggle}
                        searchValue={searchValue}
                        selectedWorkspaceIds={selectedWorkspaceIds}
                        workspaceDescriptors={workspaceDescriptors}
                      />
                      <TablePagination
                        itemCount={itemCount}
                        page={page}
                        perPage={perPage}
                        perPageOptions={perPageOptions}
                        setPage={setPage}
                        setPerPage={setPerPage}
                        variant="bottom"
                      />
                    </>
                  )}
                  {workspaceDescriptors.length === 0 && (
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
              onDelete={() => onConfirmDeleteModalDelete(workspaceDescriptors)}
              elementsTypeName={selectedElementTypesName}
              deleteMessage={deleteModalMessage}
              dataLoaded={deleteModalDataLoaded}
              fetchError={deleteModalFetchError}
            />
          </>
        );
      }}
    />
  );
}
