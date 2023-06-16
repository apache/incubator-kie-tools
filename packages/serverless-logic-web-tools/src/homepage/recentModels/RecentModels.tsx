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

import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { Alert, AlertActionCloseButton } from "@patternfly/react-core/dist/js/components/Alert";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { useGlobalAlert } from "../../alerts/GlobalAlertsContext";
import { splitFiles } from "../../extension";
import { setPageTitle } from "../../PageTitle";
import { ConfirmDeleteModal, defaultPerPageOptions, TablePagination, TableToolbar } from "../../table";
import { WorkspacesTable, WorkspacesTableRowData } from "./WorkspacesTable";
import { Link } from "react-router-dom";
import { routes } from "../../navigation/Routes";
import { escapeRegExp } from "../../regex";
import { useWorkspacesWithFilesPromise } from "./hooks/useWorkspacesWithFilesPromise";
import { WorkspaceKind } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceOrigin";
import { GIT_DEFAULT_BRANCH } from "@kie-tools-core/workspaces-git-fs/dist/constants/GitConstants";

const PAGE_TITLE = "Recent models";

export function RecentModels() {
  const [selectedWorkspaceIds, setSelectedWorkspaceIds] = useState<WorkspaceDescriptor["workspaceId"][]>([]);
  const [deletingWorkspaceIds, setDeletingWorkspaceIds] = useState<WorkspaceDescriptor["workspaceId"][]>([]);
  const [isConfirmDeleteModalOpen, setIsConfirmDeleteModalOpen] = useState(false);
  const [searchValue, setSearchValue] = useState("");
  const [page, setPage] = useState(1);
  const [perPage, setPerPage] = useState(5);
  const [workspaceIds, setWorkspaceIds] = useState<WorkspaceDescriptor["workspaceId"][]>();
  const workspaces = useWorkspaces();
  const [deletingFoldersCount, setDeletingFoldersCount] = useState(0);
  const [firstDeletingWorkspaceName, setFirstDeletingWorkspaceName] = useState("");
  const [deleteModalDataLoaded, setDeleteModalDataLoaded] = useState(false);
  const [deleteModalFetchError, setDeleteModalFetchError] = useState(false);
  const isDeletingWorkspacePlural = useMemo(() => deletingWorkspaceIds.length > 1, [deletingWorkspaceIds]);
  const workspacesWithFilesPromise = useWorkspacesWithFilesPromise(workspaceIds);

  const deletingElementTypesName = useMemo(() => {
    if (deletingWorkspaceIds.length > 1) {
      return deletingFoldersCount ? "workspaces" : "models";
    }
    return deletingFoldersCount ? "workspace" : "model";
  }, [deletingFoldersCount, deletingWorkspaceIds]);

  const deleteModalMessage = useMemo(
    () => (
      <>
        Deleting {isDeletingWorkspacePlural ? "these" : "this"}{" "}
        <b>{isDeletingWorkspacePlural ? deletingWorkspaceIds.length : firstDeletingWorkspaceName}</b>{" "}
        {deletingElementTypesName}
        {deletingFoldersCount ? ` removes the ${deletingElementTypesName} and all the files inside.` : "."}
      </>
    ),
    [
      isDeletingWorkspacePlural,
      deletingWorkspaceIds,
      firstDeletingWorkspaceName,
      deletingElementTypesName,
      deletingFoldersCount,
    ]
  );

  const onSingleConfirmDeleteModalOpen = useCallback((workspaceId: WorkspaceDescriptor["workspaceId"]) => {
    setIsConfirmDeleteModalOpen(true);
    setDeletingWorkspaceIds([workspaceId]);
  }, []);

  const onBulkConfirmDeleteModalOpen = useCallback(() => {
    setIsConfirmDeleteModalOpen(true);
    setDeletingWorkspaceIds(selectedWorkspaceIds);
  }, [selectedWorkspaceIds]);

  const onConfirmDeleteModalClose = useCallback(() => {
    setIsConfirmDeleteModalOpen(false);
    setDeletingWorkspaceIds([]);
  }, []);

  const deleteSuccessAlert = useGlobalAlert<{ elementsTypeName: string }>(
    useCallback(({ close }, { elementsTypeName }) => {
      return <Alert variant="success" title={`${capitalizeString(elementsTypeName)} deleted successfully`} />;
    }, []),
    { durationInSeconds: 2 }
  );

  const deleteErrorAlert = useGlobalAlert<{ elementsTypeName: string }>(
    useCallback(({ close }, { elementsTypeName }) => {
      return (
        <Alert
          variant="danger"
          title={`Oops, something went wrong while trying to delete the selected ${elementsTypeName}. Please refresh the page and try again. If the problem persists, you can try deleting site data for this application in your browser's settings.`}
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      );
    }, [])
  );

  const onWsToggle = useCallback((workspaceId: WorkspaceDescriptor["workspaceId"], checked: boolean) => {
    setSelectedWorkspaceIds((prevSelected) => {
      const otherSelectedIds = prevSelected.filter((r) => r !== workspaceId);
      return checked ? [...otherSelectedIds, workspaceId] : otherSelectedIds;
    });
  }, []);

  const onClearFilters = useCallback(() => {
    setSearchValue("");
    setPage(1);
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
      if (deletingWorkspaceIds.length !== 1) {
        return "";
      }
      const workspaceData = await workspaces.getWorkspace({ workspaceId });
      return (await isWsFolder(workspaceId))
        ? workspaceData.name
        : (await workspaces.getFiles({ workspaceId }))[0].nameWithoutExtension;
    },
    [isWsFolder, deletingWorkspaceIds, workspaces]
  );

  useEffect(() => {
    setPage(1);
  }, [searchValue]);

  useEffect(() => {
    Promise.all([
      Promise.all(deletingWorkspaceIds.map(isWsFolder)).then((results) => {
        const foldersCount = results.filter((r) => r).length;
        setDeletingFoldersCount(foldersCount);
      }),
      getWorkspaceName(deletingWorkspaceIds[0]).then(setFirstDeletingWorkspaceName),
    ])
      .then(() => setDeleteModalDataLoaded(true))
      .catch(() => setDeleteModalFetchError(true));
  }, [getWorkspaceName, deletingWorkspaceIds, isWsFolder]);

  useEffect(() => {
    setPageTitle([PAGE_TITLE]);
  }, []);

  const tableData = useMemo<WorkspacesTableRowData[]>(
    () =>
      workspacesWithFilesPromise.data?.map((item) => {
        if (!item.success) {
          return {
            descriptor: {
              workspaceId: item.workspaceId,
              name: item.name ?? "Unknown workspace",
              origin: item.origin ?? { kind: WorkspaceKind.LOCAL, branch: GIT_DEFAULT_BRANCH },
              createdDateISO: item.createdDateISO ?? new Date().toISOString(),
              lastUpdatedDateISO: item.lastUpdatedDateISO ?? new Date().toISOString(),
              gitAuthSessionId: undefined,
            },
            editableFiles: [],
            hasErrors: true,
            isWsFolder: true,
            totalFiles: 0,
          };
        }
        const { editableFiles, readonlyFiles } = splitFiles(item.files ?? []);
        const isWsFolder =
          editableFiles.length > 1 || readonlyFiles.length > 0 || item.descriptor.origin.kind !== WorkspaceKind.LOCAL;
        const name = !isWsFolder && editableFiles.length ? editableFiles[0].nameWithoutExtension : item.descriptor.name;

        return {
          descriptor: { ...item.descriptor, name },
          editableFiles: editableFiles,
          hasErrors: false,
          isWsFolder,
          totalFiles: item.files.length,
        };
      }) ?? [],
    [workspacesWithFilesPromise]
  );

  const filteredTableData = useMemo(() => {
    const searchRegex = new RegExp(escapeRegExp(searchValue), "i");
    return searchValue ? tableData.filter((e) => e.descriptor.name.search(searchRegex) >= 0) : tableData;
  }, [tableData, searchValue]);

  const onToggleAllElements = useCallback(
    (checked: boolean) => {
      setSelectedWorkspaceIds(checked ? filteredTableData.map((e) => e.descriptor.workspaceId) : []);
    },
    [filteredTableData]
  );

  const updateTableData = useCallback(async () => {
    const allworkspaces = await workspaces.listAllWorkspaces();
    setWorkspaceIds(allworkspaces.map((w) => w.workspaceId));
  }, [workspaces]);

  const onConfirmDeleteModalDelete = useCallback(async () => {
    const elementsTypeName = deletingWorkspaceIds.length > 1 ? "Models" : "Model";
    setIsConfirmDeleteModalOpen(false);

    Promise.all(
      tableData
        .filter((w) => deletingWorkspaceIds.includes(w.descriptor.workspaceId))
        .map((w) => workspaces.deleteWorkspace(w.descriptor))
    )
      .then(() => {
        deleteSuccessAlert.show({ elementsTypeName });
      })
      .catch((e) => {
        console.error(e);
        deleteErrorAlert.show({ elementsTypeName });
      })
      .finally(async () => {
        await updateTableData();
        setSelectedWorkspaceIds([]);
        setDeletingWorkspaceIds([]);
        setPage(1);
      });
  }, [updateTableData, deletingWorkspaceIds, workspaces, deleteErrorAlert, deleteSuccessAlert, tableData]);

  return (
    <>
      <Page>
        <PageSection variant={"light"}>
          <TextContent>
            <Text component={TextVariants.h1}>{PAGE_TITLE}</Text>
            <Text component={TextVariants.p}>
              Use your recent models from GitHub Repository, a GitHub Gist or saved in your browser.
            </Text>
          </TextContent>
        </PageSection>

        <PageSection isFilled aria-label="workspaces-table-section">
          <PageSection variant={"light"} padding={{ default: "noPadding" }}>
            {tableData.length > 0 && (
              <>
                <TableToolbar
                  itemCount={filteredTableData.length}
                  onDeleteActionButtonClick={onBulkConfirmDeleteModalOpen}
                  onToggleAllElements={(checked) => onToggleAllElements(checked)}
                  searchValue={searchValue}
                  selectedElementsCount={selectedWorkspaceIds.length}
                  setSearchValue={setSearchValue}
                  page={page}
                  perPage={perPage}
                  perPageOptions={defaultPerPageOptions}
                  setPage={setPage}
                  setPerPage={setPerPage}
                />
                <WorkspacesTable
                  page={page}
                  perPage={perPage}
                  onClearFilters={onClearFilters}
                  onWsToggle={onWsToggle}
                  selectedWorkspaceIds={selectedWorkspaceIds}
                  tableData={filteredTableData}
                  onDelete={onSingleConfirmDeleteModalOpen}
                />
                <TablePagination
                  itemCount={filteredTableData.length}
                  page={page}
                  perPage={perPage}
                  perPageOptions={defaultPerPageOptions}
                  setPage={setPage}
                  setPerPage={setPerPage}
                  variant="bottom"
                />
              </>
            )}
            {tableData.length === 0 && (
              <Bullseye>
                <EmptyState>
                  <EmptyStateIcon icon={CubesIcon} />
                  <Title headingLevel="h4" size="lg">
                    {`Nothing here`}
                  </Title>
                  <EmptyStateBody>
                    <TextContent>
                      <Text>
                        Start by adding a <Link to={routes.home.path({})}>new model</Link> or{" "}
                        <Link to={routes.sampleCatalog.path({})}>try a sample</Link>
                      </Text>
                    </TextContent>
                  </EmptyStateBody>
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
        elementsTypeName={deletingElementTypesName}
        deleteMessage={deleteModalMessage}
        dataLoaded={deleteModalDataLoaded}
        fetchError={deleteModalFetchError}
      />
    </>
  );
}

const capitalizeString = (value: string) => value.charAt(0).toUpperCase() + value.slice(1);
