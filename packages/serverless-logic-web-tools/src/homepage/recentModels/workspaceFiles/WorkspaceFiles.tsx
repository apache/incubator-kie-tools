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
import { useController } from "@kie-tools-core/react-hooks/dist/useController";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { useWorkspacePromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspaceHooks";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { Breadcrumb } from "@patternfly/react-core/components/Breadcrumb";
import { BreadcrumbItem, Checkbox, Dropdown, DropdownToggle, ToolbarItem } from "@patternfly/react-core/dist/js";
import { Alert, AlertActionCloseButton } from "@patternfly/react-core/dist/js/components/Alert";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { PerPageOptions } from "@patternfly/react-core/dist/js/components/Pagination";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { CaretDownIcon, PlusIcon } from "@patternfly/react-icons/dist/js/icons";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { useHistory } from "react-router";
import { Alerts, AlertsController, useAlert } from "../../../alerts/Alerts";
import { NewFileDropdownMenu } from "../../../editor/NewFileDropdownMenu";
import { isEditable } from "../../../extension";
import { routes } from "../../../navigation/Routes";
import { ConfirmDeleteModal } from "../../../table/ConfirmDeleteModal";
import { TablePagination } from "../../../table/TablePagination";
import { TableToolbar } from "../../../table/TableToolbar";
import { WorkspaceFilesTable } from "./WorkspaceFilesTable";

export interface Props {
  workspaceId: string;
}

const perPageOptions: PerPageOptions[] = [5, 10, 20, 50, 100].map((n) => ({
  title: n.toString(),
  value: n,
}));

export function WorkspaceFiles(props: Props) {
  const { workspaceId } = props;
  const workspacePromise = useWorkspacePromise(workspaceId);
  const [selectedWorkspaceFiles, setSelectedWorkspaceFiles] = useState<WorkspaceFile[]>([]);
  const [isConfirmDeleteModalOpen, setIsConfirmDeleteModalOpen] = useState(false);
  const [searchValue, setSearchValue] = React.useState("");
  const [page, setPage] = React.useState(1);
  const [perPage, setPerPage] = React.useState(5);
  const [isViewRoFilesChecked, setIsViewRoFilesChecked] = useState(false);
  const [isNewFileDropdownMenuOpen, setNewFileDropdownMenuOpen] = useState(false);
  const workspaces = useWorkspaces();
  const history = useHistory();
  const [alerts, alertsRef] = useController<AlertsController>();
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

  const deleteSuccessAlert = useAlert<{ selectedElementTypesName: string }>(
    alerts,
    useCallback(({ close }, { selectedElementTypesName }) => {
      return <Alert variant="success" title={`${capitalizeString(selectedElementTypesName)} deleted successfully`} />;
    }, []),
    { durationInSeconds: 2 }
  );

  const deleteErrorAlert = useAlert<{ selectedElementTypesName: string }>(
    alerts,
    useCallback(({ close }, { selectedElementTypesName }) => {
      return (
        <Alert
          variant="danger"
          title={`Oops, something went wrong while trying to delete the selected ${selectedElementTypesName}. Please refresh the page and try again. If the problem persists, you can try deleting site data for this application in your browser's settings.`}
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      );
    }, [])
  );

  const onConfirmDeleteModalDelete = useCallback(
    async (filesCount: number) => {
      setIsConfirmDeleteModalOpen(false);

      if (selectedWorkspaceFiles.length === filesCount) {
        workspaces.deleteWorkspace({ workspaceId });
        history.push({ pathname: routes.recentModels.path({}) });
        deleteSuccessAlert.show({ selectedElementTypesName });
        return;
      }

      Promise.all(selectedWorkspaceFiles.map((file) => workspaces.deleteFile({ file })))
        .then(() => {
          deleteSuccessAlert.show({ selectedElementTypesName });
        })
        .catch((e) => {
          console.error(e);
          deleteErrorAlert.show({ selectedElementTypesName });
        })
        .finally(() => {
          setSelectedWorkspaceFiles([]);
        });
    },
    [
      selectedWorkspaceFiles,
      workspaces,
      history,
      workspaceId,
      deleteErrorAlert,
      deleteSuccessAlert,
      selectedElementTypesName,
    ]
  );

  const onFileToggle = useCallback((workspaceFile: WorkspaceFile, checked: boolean) => {
    setSelectedWorkspaceFiles((prevSelected) => {
      const otherSelectedFiles = [...prevSelected.filter((f) => f !== workspaceFile)];
      return checked ? [...otherSelectedFiles, workspaceFile] : otherSelectedFiles;
    });
  }, []);

  const onToggleAllElements = useCallback((checked: boolean, workspace: ActiveWorkspace) => {
    setSelectedWorkspaceFiles(checked ? workspace.files : []);
  }, []);

  const handleViewRoCheckboxChange = useCallback((checked: boolean) => {
    setIsViewRoFilesChecked(checked);
  }, []);

  useEffect(() => {
    setSelectedWorkspaceFiles([]);
  }, [workspacePromise]);

  return (
    <PromiseStateWrapper
      promise={workspacePromise}
      rejected={(e) => <>Error fetching workspaces: {e + ""}</>}
      resolved={(workspace: ActiveWorkspace) => {
        const files = workspace.files.filter((f: WorkspaceFile) => isViewRoFilesChecked || isEditable(f.relativePath));
        const filesCount = files.length;

        return (
          <>
            <Alerts ref={alertsRef} width={"500px"} />
            <Page
              breadcrumb={
                <Breadcrumb>
                  <BreadcrumbItem to={"#" + routes.recentModels.path({})}>Recent Models</BreadcrumbItem>
                  <BreadcrumbItem to="#" isActive>
                    {workspace.descriptor.name}
                  </BreadcrumbItem>
                </Breadcrumb>
              }
            >
              <PageSection variant={"light"}>
                <TextContent>
                  <Text component={TextVariants.h1}>Files in &lsquo;{workspace.descriptor.name}&rsquo;</Text>
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
                        additionalComponents={
                          <>
                            <ToolbarItem>
                              <Dropdown
                                position={"right"}
                                isOpen={isNewFileDropdownMenuOpen}
                                toggle={
                                  <DropdownToggle
                                    onToggle={setNewFileDropdownMenuOpen}
                                    toggleIndicator={CaretDownIcon}
                                    toggleVariant="primary"
                                  >
                                    <PlusIcon />
                                    &nbsp;&nbsp;New file
                                  </DropdownToggle>
                                }
                              >
                                <NewFileDropdownMenu
                                  alerts={alerts}
                                  workspaceId={workspaceId}
                                  destinationDirPath={""}
                                  onAddFile={async (file) => {
                                    setNewFileDropdownMenuOpen(false);
                                    if (!file) {
                                      return;
                                    }

                                    history.push({
                                      pathname: routes.workspaceWithFilePath.path({
                                        workspaceId: file.workspaceId,
                                        fileRelativePath: file.relativePathWithoutExtension,
                                        extension: file.extension,
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
                                onChange={handleViewRoCheckboxChange}
                              ></Checkbox>
                            </ToolbarItem>
                          </>
                        }
                      />

                      <WorkspaceFilesTable
                        page={page}
                        perPage={perPage}
                        onFileToggle={onFileToggle}
                        searchValue={searchValue}
                        selectedWorkspaceFiles={selectedWorkspaceFiles}
                        workspaceFiles={files}
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
              onDelete={() => onConfirmDeleteModalDelete(filesCount)}
              elementsTypeName={selectedElementTypesName}
              deleteMessage={deleteModalMessage}
            />
          </>
        );
      }}
    />
  );
}

const capitalizeString = (value: string) => value.charAt(0).toUpperCase() + value.slice(1);
