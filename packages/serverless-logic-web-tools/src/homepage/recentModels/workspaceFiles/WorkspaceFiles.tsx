/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { PromiseStateWrapper } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { useWorkspacePromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspaceHooks";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { WorkspaceKind } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceOrigin";
import { BreadcrumbItem, Breadcrumb } from "@patternfly/react-core/dist/js/components/Breadcrumb";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { Dropdown, DropdownToggle } from "@patternfly/react-core/deprecated";
import { ToolbarItem } from "@patternfly/react-core/dist/js/components/Toolbar";
import { Alert, AlertActionCloseButton } from "@patternfly/react-core/dist/js/components/Alert";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateHeader,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";

import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { CaretDownIcon, PlusIcon } from "@patternfly/react-icons/dist/js/icons";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useGlobalAlert } from "../../../alerts/GlobalAlertsContext";
import { NewFileDropdownMenu } from "../../../editor/NewFileDropdownMenu";
import { splitFiles } from "../../../extension";
import { routes } from "../../../navigation/Routes";
import { setPageTitle } from "../../../PageTitle";
import { ConfirmDeleteModal, defaultPerPageOptions, TablePagination, TableToolbar } from "../../../table";
import { WorkspaceFilesTable } from "./WorkspaceFilesTable";
import { ErrorPage } from "../../../error/ErrorPage";
import Fuse from "fuse.js";

export function WorkspaceFiles() {
  const { workspaceId } = useParams<{ workspaceId: string }>();

  const workspacePromise = useWorkspacePromise(workspaceId!);
  const [selectedWorkspaceFiles, setSelectedWorkspaceFiles] = useState<WorkspaceFile[]>([]);
  const [deletingWorkspaceFiles, setDeletingWorkspaceFiles] = useState<WorkspaceFile[]>([]);
  const [isConfirmDeleteModalOpen, setIsConfirmDeleteModalOpen] = useState(false);
  const [searchValue, setSearchValue] = React.useState("");
  const [page, setPage] = React.useState(1);
  const [perPage, setPerPage] = React.useState(5);
  const [isViewRoFilesChecked, setIsViewRoFilesChecked] = useState(false);
  const [isNewFileDropdownMenuOpen, setNewFileDropdownMenuOpen] = useState(false);
  const splittedFiles = useMemo(() => splitFiles(workspacePromise.data?.files || []), [workspacePromise]);
  const isViewRoFilesDisabled = useMemo(
    () => !splittedFiles.editableFiles.length || !splittedFiles.readonlyFiles.length,
    [splittedFiles]
  );
  const visibleFiles = useMemo(
    () => [...splittedFiles.editableFiles, ...(isViewRoFilesChecked ? splittedFiles.readonlyFiles : [])],
    [isViewRoFilesChecked, splittedFiles]
  );
  const [fuseSearch, setFuseSearch] = useState<Fuse<WorkspaceFile>>();
  const workspaces = useWorkspaces();
  const navigate = useNavigate();
  const isDeletingWorkspaceFilesPlural = useMemo(() => deletingWorkspaceFiles.length > 1, [deletingWorkspaceFiles]);
  const deletingElementTypesName = useMemo(
    () => (isDeletingWorkspaceFilesPlural ? "files" : "file"),
    [isDeletingWorkspaceFilesPlural]
  );

  const deleteModalMessage = useMemo(
    () => (
      <>
        Deleting {isDeletingWorkspaceFilesPlural ? "these" : "this"}{" "}
        <b>{isDeletingWorkspaceFilesPlural ? deletingWorkspaceFiles.length : deletingWorkspaceFiles[0]?.name}</b>{" "}
        {deletingElementTypesName}
      </>
    ),
    [isDeletingWorkspaceFilesPlural, deletingWorkspaceFiles, deletingElementTypesName]
  );

  const onSingleConfirmDeleteModalOpen = useCallback((workspaceFile: WorkspaceFile) => {
    setIsConfirmDeleteModalOpen(true);
    setDeletingWorkspaceFiles([workspaceFile]);
  }, []);

  const onBulkConfirmDeleteModalOpen = useCallback(() => {
    setIsConfirmDeleteModalOpen(true);
    setDeletingWorkspaceFiles(selectedWorkspaceFiles);
  }, [selectedWorkspaceFiles]);

  const onConfirmDeleteModalClose = useCallback(() => {
    setIsConfirmDeleteModalOpen(false);
    setDeletingWorkspaceFiles([]);
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

  const onConfirmDeleteModalDelete = useCallback(
    async (totalFilesCount: number) => {
      const elementsTypeName = deletingElementTypesName;
      setIsConfirmDeleteModalOpen(false);

      if (deletingWorkspaceFiles.length === totalFilesCount) {
        workspaces.deleteWorkspace({ workspaceId: workspaceId! });
        navigate({ pathname: routes.recentModels.path({}) });
        deleteSuccessAlert.show({ elementsTypeName });
        return;
      }

      Promise.all(deletingWorkspaceFiles.map((file) => workspaces.deleteFile({ file })))
        .then(() => {
          deleteSuccessAlert.show({ elementsTypeName });
        })
        .catch((e) => {
          console.error(e);
          deleteErrorAlert.show({ elementsTypeName });
        })
        .finally(() => {
          setSelectedWorkspaceFiles([]);
          setDeletingWorkspaceFiles([]);
          setPage(1);
        });
    },
    [
      deletingWorkspaceFiles,
      workspaces,
      navigate,
      workspaceId,
      deleteErrorAlert,
      deleteSuccessAlert,
      deletingElementTypesName,
    ]
  );

  const onFileToggle = useCallback((workspaceFile: WorkspaceFile, checked: boolean) => {
    setSelectedWorkspaceFiles((prevSelected) => {
      const otherSelectedFiles = [...prevSelected.filter((f) => f !== workspaceFile)];
      return checked ? [...otherSelectedFiles, workspaceFile] : otherSelectedFiles;
    });
  }, []);

  const onToggleAllElements = useCallback((checked: boolean, files: WorkspaceFile[]) => {
    setSelectedWorkspaceFiles(checked ? files : []);
  }, []);

  const onClearFilters = useCallback(() => {
    setSearchValue("");
    setPage(1);
  }, []);

  const handleViewRoCheckboxChange = useCallback((checked: boolean) => {
    setIsViewRoFilesChecked(checked);
    setPage(1);
  }, []);

  const filterFiles = useCallback(
    (searchValue: string) => {
      return !searchValue.trim() || !fuseSearch ? visibleFiles : fuseSearch.search(searchValue).map((r) => r.item);
    },
    [fuseSearch, visibleFiles]
  );

  useEffect(() => {
    setPage(1);
  }, [searchValue]);

  useEffect(() => {
    if (isViewRoFilesDisabled) {
      setIsViewRoFilesChecked(true);
    }
  }, [isViewRoFilesDisabled]);

  useEffect(() => {
    if (workspacePromise.data?.files) {
      setSelectedWorkspaceFiles((selectedFiles) =>
        selectedFiles.filter((sFile) =>
          workspacePromise.data.files.some((wFile) => wFile.relativePath === sFile.relativePath)
        )
      );
    }

    setFuseSearch(
      new Fuse(visibleFiles || [], {
        keys: ["nameWithoutExtension"],
        shouldSort: false,
        threshold: 0.3,
      })
    );
  }, [workspacePromise, visibleFiles]);

  return (
    <PromiseStateWrapper
      promise={workspacePromise}
      rejected={(e) => <ErrorPage kind="WorkspaceFiles" workspaceId={workspaceId!} errors={e} />}
      resolved={(workspace: ActiveWorkspace) => {
        const allFilesCount = workspace.files.length;
        const filteredFiles = filterFiles(searchValue);
        const filteredFilesCount = filteredFiles.length;

        setPageTitle([workspace.descriptor.name]);

        return (
          <>
            <Page
              breadcrumb={
                <Breadcrumb>
                  <BreadcrumbItem to={"#" + routes.recentModels.path({})}>Recent Models</BreadcrumbItem>
                  <BreadcrumbItem isActive>{workspace.descriptor.name}</BreadcrumbItem>
                </Breadcrumb>
              }
            >
              <PageSection variant={"light"}>
                <TextContent>
                  <Text component={TextVariants.h1}>Files in &lsquo;{workspace.descriptor.name}&rsquo;</Text>
                  <Text component={TextVariants.p}>
                    &apos;{workspace.descriptor?.name}&apos;
                    {workspace.descriptor?.origin.kind === WorkspaceKind.GIT && (
                      <>
                        {" "}
                        is linked to a Git Repository.{" "}
                        <a href={workspace.descriptor?.origin.url.toString()} target="_blank" rel="noopener noreferrer">
                          {workspace.descriptor?.origin.url.toString()}
                        </a>
                      </>
                    )}
                    {workspace.descriptor?.origin.kind === WorkspaceKind.GITHUB_GIST && (
                      <>
                        {" "}
                        is linked to a GitHub Gist.{" "}
                        <a href={workspace.descriptor?.origin.url.toString()} target="_blank" rel="noopener noreferrer">
                          {workspace.descriptor?.origin.url.toString()}
                        </a>
                      </>
                    )}
                    {workspace.descriptor?.origin.kind === WorkspaceKind.LOCAL && (
                      <> is saved directly in the browser. Incognito windows don&apos;t have access to it.</>
                    )}
                  </Text>
                </TextContent>
              </PageSection>

              <PageSection isFilled aria-label="workspaces-table-section">
                <PageSection variant={"light"} padding={{ default: "noPadding" }}>
                  {allFilesCount > 0 && (
                    <>
                      <TableToolbar
                        itemCount={filteredFilesCount}
                        onDeleteActionButtonClick={onBulkConfirmDeleteModalOpen}
                        onToggleAllElements={(checked) => onToggleAllElements(checked, filteredFiles)}
                        searchValue={searchValue}
                        selectedElementsCount={selectedWorkspaceFiles.length}
                        setSearchValue={setSearchValue}
                        page={page}
                        perPage={perPage}
                        perPageOptions={defaultPerPageOptions}
                        setPage={setPage}
                        setPerPage={setPerPage}
                        additionalComponents={
                          <>
                            <ToolbarItem>
                              <Dropdown
                                position={"right"}
                                isOpen={isNewFileDropdownMenuOpen}
                                toggle={
                                  <DropdownToggle
                                    onToggle={(_event, val) => setNewFileDropdownMenuOpen(val)}
                                    toggleIndicator={CaretDownIcon}
                                    toggleVariant="primary"
                                  >
                                    <PlusIcon />
                                    &nbsp;&nbsp;New file
                                  </DropdownToggle>
                                }
                              >
                                <NewFileDropdownMenu
                                  workspaceId={workspaceId}
                                  destinationDirPath={""}
                                  onAddFile={async (file) => {
                                    setNewFileDropdownMenuOpen(false);
                                    if (!file) {
                                      return;
                                    }

                                    navigate({
                                      pathname: routes.workspaceWithFilePath.path({
                                        workspaceId: file.workspaceId,
                                        fileRelativePath: file.relativePath,
                                      }),
                                    });
                                  }}
                                />
                              </Dropdown>
                            </ToolbarItem>
                            <ToolbarItem>
                              <Checkbox
                                id="viewRoFiles"
                                label="View readonly files"
                                isChecked={isViewRoFilesChecked}
                                isDisabled={isViewRoFilesDisabled}
                                onChange={(_event, value) => handleViewRoCheckboxChange(value)}
                              ></Checkbox>
                            </ToolbarItem>
                          </>
                        }
                      />

                      <WorkspaceFilesTable
                        page={page}
                        perPage={perPage}
                        onFileToggle={onFileToggle}
                        selectedWorkspaceFiles={selectedWorkspaceFiles}
                        workspaceFiles={filteredFiles}
                        onClearFilters={onClearFilters}
                        onDelete={onSingleConfirmDeleteModalOpen}
                      />

                      <TablePagination
                        itemCount={filteredFilesCount}
                        page={page}
                        perPage={perPage}
                        perPageOptions={defaultPerPageOptions}
                        setPage={setPage}
                        setPerPage={setPerPage}
                        variant="bottom"
                      />
                    </>
                  )}
                  {allFilesCount === 0 && (
                    <Bullseye>
                      <EmptyState>
                        <EmptyStateHeader
                          titleText={<>{`Nothing here`}</>}
                          icon={<EmptyStateIcon icon={CubesIcon} />}
                          headingLevel="h4"
                        />
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
              onDelete={() => onConfirmDeleteModalDelete(allFilesCount)}
              elementsTypeName={deletingElementTypesName}
              deleteMessage={deleteModalMessage}
            />
          </>
        );
      }}
    />
  );
}

const capitalizeString = (value: string) => value.charAt(0).toUpperCase() + value.slice(1);
