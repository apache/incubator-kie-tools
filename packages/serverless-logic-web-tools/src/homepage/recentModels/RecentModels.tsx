/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import { PromiseStateWrapper } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useController } from "@kie-tools-core/react-hooks/dist/useController";
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { useWorkspacePromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspaceHooks";
import { useWorkspaceDescriptorsPromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspacesHooks";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { WorkspaceKind } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceOrigin";
import {
  Button,
  Checkbox,
  Page,
  PageSection,
  Toolbar,
  ToolbarContent,
  ToolbarItem,
} from "@patternfly/react-core/dist/js";
import {
  Card,
  CardActions,
  CardBody,
  CardHeader,
  CardHeaderMain,
  CardTitle,
} from "@patternfly/react-core/dist/js/components/Card";
import {
  Drawer,
  DrawerActions,
  DrawerCloseButton,
  DrawerContent,
  DrawerContentBody,
  DrawerHead,
  DrawerPanelBody,
  DrawerPanelContent,
} from "@patternfly/react-core/dist/js/components/Drawer";
import {
  Dropdown,
  DropdownItem,
  DropdownToggle,
  DropdownToggleCheckbox,
} from "@patternfly/react-core/dist/js/components/Dropdown";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Skeleton } from "@patternfly/react-core/dist/js/components/Skeleton";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Stack, StackItem } from "@patternfly/react-core/dist/js/layouts/Stack";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { FolderIcon } from "@patternfly/react-icons/dist/js/icons/folder-icon";
import { PlusIcon } from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { TaskIcon } from "@patternfly/react-icons/dist/js/icons/task-icon";
import { useCallback, useEffect, useMemo, useState } from "react";
import { useHistory } from "react-router";
import { Link } from "react-router-dom";
import AutoSizer from "react-virtualized-auto-sizer";
import { VariableSizeList } from "react-window";
import { Alerts, AlertsController } from "../../alerts/Alerts";
import { RelativeDate } from "../../dates/RelativeDate";
import { DeleteDropdownWithConfirmation } from "../../editor/DeleteDropdownWithConfirmation";
import { NewFileDropdownMenu } from "../../editor/NewFileDropdownMenu";
import { isEditable, splitFiles } from "../../extension";
import { FileDataList, getFileDataListHeight } from "../../fileList/FileDataList";
import { useRoutes } from "../../navigation/Hooks";
import { QueryParams } from "../../navigation/Routes";
import { useQueryParam, useQueryParams } from "../../queryParams/QueryParamsContext";
import { ErrorBoundary } from "../../reactExt/ErrorBoundary";
import { FileLabel } from "../../workspace/components/FileLabel";
import { WorkspaceLabel } from "../../workspace/components/WorkspaceLabel";
import { KebabDropdown } from "../../editor/EditorToolbar";
import { TrashIcon } from "@patternfly/react-icons/dist/js/icons";

export function RecentModels() {
  const routes = useRoutes();
  const history = useHistory();
  const workspaceDescriptorsPromise = useWorkspaceDescriptorsPromise();
  const expandedWorkspaceId = useQueryParam(QueryParams.EXPAND);
  const queryParams = useQueryParams();
  const [isLargeKebabOpen, setLargeKebabOpen] = useState(false);
  const [isBulkDropDownOpen, setIsBulkDropDownOpen] = useState(false);
  const [selectedWorkspaceIds, setSelectedWorkspaceIds] = useState<WorkspaceDescriptor["workspaceId"][]>([]);
  const modelsListPadding = "10px";

  const isBulkCheckBoxChecked = useMemo(() => {
    if (workspaceDescriptorsPromise.data && selectedWorkspaceIds.length) {
      return selectedWorkspaceIds.length === workspaceDescriptorsPromise.data.length ? true : null;
    }
    return false;
  }, [workspaceDescriptorsPromise, selectedWorkspaceIds]);

  const closeExpandedWorkspace = useCallback(() => {
    history.replace({
      pathname: "/RecentModels",
      search: queryParams.without(QueryParams.EXPAND).toString(),
    });
  }, [history, queryParams]);

  const expandWorkspace = useCallback(
    (workspaceId: string) => {
      const expand = workspaceId !== expandedWorkspaceId ? workspaceId : undefined;
      if (!expand) {
        closeExpandedWorkspace();
        return;
      }

      history.replace({
        pathname: "/RecentModels",
        search: routes.home.queryString({ expand }),
      });
    },
    [closeExpandedWorkspace, history, routes, expandedWorkspaceId]
  );

  useEffect(() => {
    if (
      workspaceDescriptorsPromise.data &&
      !workspaceDescriptorsPromise.data.map((f) => f.workspaceId).includes(expandedWorkspaceId!)
    ) {
      closeExpandedWorkspace();
    }
  }, [workspaceDescriptorsPromise, closeExpandedWorkspace, expandedWorkspaceId]);

  const bulkDeleteWorkspaceFiles = useCallback(async (workspaceDescriptors: WorkspaceDescriptor[]) => {
    console.log("## delete", workspaceDescriptors);
  }, []);

  const deleteFileDropdownItem = useCallback(
    (workspaceDescriptors: WorkspaceDescriptor[]) => {
      return (
        <DropdownItem
          key={"delete-dropdown-item"}
          isDisabled={selectedWorkspaceIds.length === 0}
          onClick={() => bulkDeleteWorkspaceFiles(workspaceDescriptors)}
          ouiaId={"delete-file-button"}
        >
          <Flex flexWrap={{ default: "nowrap" }}>
            <FlexItem>
              <TrashIcon />
              &nbsp;&nbsp;Delete <b>selected models</b>
            </FlexItem>
          </Flex>
        </DropdownItem>
      );
    },
    [bulkDeleteWorkspaceFiles, selectedWorkspaceIds]
  );

  const onWorkspaceCardCheckBoxSelect = useCallback(
    (checked: boolean, workspaceId: string) => {
      setSelectedWorkspaceIds((prevState) =>
        checked ? [...prevState, workspaceId] : prevState.filter((id) => id !== workspaceId)
      );
    },
    [setSelectedWorkspaceIds]
  );

  const onBulkDropDownToggle = (isOpen: boolean) => {
    setIsBulkDropDownOpen(isOpen);
  };

  const onBulkDropDownSelect = () => {
    setIsBulkDropDownOpen(false);
  };

  const bulkDropDownItems = useCallback(
    (workspaceDescriptors: WorkspaceDescriptor[]) => [
      <DropdownItem onClick={() => setSelectedWorkspaceIds([])} key="none">
        Select none (0)
      </DropdownItem>,
      <DropdownItem onClick={() => setSelectedWorkspaceIds(workspaceDescriptors.map((e) => e.workspaceId))} key="all">
        Select all({workspaceDescriptors.length})
      </DropdownItem>,
    ],
    []
  );

  const onSelectAllWorkspace = useCallback((checked: boolean, workspaceDescriptors: WorkspaceDescriptor[]) => {
    setSelectedWorkspaceIds(checked ? workspaceDescriptors.map((e) => e.workspaceId) : []);
  }, []);

  return (
    <PromiseStateWrapper
      promise={workspaceDescriptorsPromise}
      rejected={(e) => <>Error fetching workspaces: {e + ""}</>}
      resolved={(workspaceDescriptors) => {
        return (
          <>
            <Page>
              <PageSection variant={"light"}>
                <TextContent>
                  <Text component={TextVariants.h1}>Recent models</Text>
                  <Text component={TextVariants.p}>
                    Use your recent models from GitHub Repository, a GitHub Gist or saved in your browser.
                  </Text>
                </TextContent>
              </PageSection>

              <PageSection hasOverflowScroll isFilled>
                <PageSection variant={"light"} isFilled style={{ height: "100%" }}>
                  <Toolbar>
                    <ToolbarContent style={{ paddingLeft: modelsListPadding, paddingRight: modelsListPadding }}>
                      <ToolbarItem alignment={{ default: "alignLeft" }}>
                        <Dropdown
                          onSelect={onBulkDropDownSelect}
                          toggle={
                            <DropdownToggle
                              splitButtonItems={[
                                <DropdownToggleCheckbox
                                  onChange={(checked) => onSelectAllWorkspace(checked, workspaceDescriptors)}
                                  isChecked={isBulkCheckBoxChecked}
                                  id="split-button-text-checkbox"
                                  key="bulk-check-box"
                                  aria-label="Select all"
                                >
                                  {selectedWorkspaceIds.length ? `${selectedWorkspaceIds.length} selected` : ""}
                                </DropdownToggleCheckbox>,
                              ]}
                              onToggle={onBulkDropDownToggle}
                              id="toggle-split-button-text"
                            />
                          }
                          isOpen={isBulkDropDownOpen}
                          dropdownItems={bulkDropDownItems(workspaceDescriptors)}
                        />
                      </ToolbarItem>
                      <ToolbarItem alignment={{ default: "alignRight" }}>
                        <KebabDropdown
                          id={"kebab-lg"}
                          state={[isLargeKebabOpen, setLargeKebabOpen]}
                          items={[deleteFileDropdownItem(workspaceDescriptors)]}
                          menuAppendTo="parent"
                        />
                      </ToolbarItem>
                    </ToolbarContent>
                  </Toolbar>

                  <Drawer isExpanded={!!expandedWorkspaceId} isInline={true}>
                    <DrawerContent
                      panelContent={
                        <DrawerPanelContent isResizable={true} minSize={"40%"} maxSize={"80%"}>
                          <WorkspacesListDrawerPanelContent
                            key={expandedWorkspaceId}
                            workspaceId={expandedWorkspaceId}
                            onClose={closeExpandedWorkspace}
                          />
                        </DrawerPanelContent>
                      }
                    >
                      <DrawerContentBody>
                        {workspaceDescriptors.length > 0 && (
                          <Stack hasGutter={true} style={{ padding: modelsListPadding }}>
                            {workspaceDescriptors
                              .sort((a, b) =>
                                new Date(a.lastUpdatedDateISO) < new Date(b.lastUpdatedDateISO) ? 1 : -1
                              )
                              .map((workspace) => (
                                <StackItem key={workspace.workspaceId}>
                                  <ErrorBoundary error={<WorkspaceCardError workspace={workspace} />}>
                                    <WorkspaceCard
                                      workspaceId={workspace.workspaceId}
                                      onSelect={() => expandWorkspace(workspace.workspaceId)}
                                      isSelected={workspace.workspaceId === expandedWorkspaceId}
                                      selectedWorkspaceIds={selectedWorkspaceIds}
                                      setSelectedWorkspaceIds={setSelectedWorkspaceIds}
                                    />
                                  </ErrorBoundary>
                                </StackItem>
                              ))}
                          </Stack>
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
                      </DrawerContentBody>
                    </DrawerContent>
                  </Drawer>
                </PageSection>
              </PageSection>
            </Page>
          </>
        );
      }}
    />
  );
}

export function WorkspacesListDrawerPanelContent(props: { workspaceId: string | undefined; onClose: () => void }) {
  const workspacePromise = useWorkspacePromise(props.workspaceId);

  const readonlyFiles = useMemo(
    () =>
      (workspacePromise.data?.files ?? [])
        .sort((a, b) => a.relativePath.localeCompare(b.relativePath))
        .filter((file) => !isEditable(file.relativePath)),
    [workspacePromise.data?.files]
  );

  const editableFiles = useMemo(
    () =>
      (workspacePromise.data?.files ?? [])
        .sort((a, b) => a.relativePath.localeCompare(b.relativePath))
        .filter((file) => isEditable(file.relativePath)),
    [workspacePromise.data?.files]
  );

  const arrayWithModelsThenOtherFiles = useMemo(() => {
    return [...editableFiles, ...readonlyFiles];
  }, [editableFiles, readonlyFiles]);

  const [isNewFileDropdownMenuOpen, setNewFileDropdownMenuOpen] = useState(false);
  const [alerts, alertsRef] = useController<AlertsController>();

  return (
    <PromiseStateWrapper
      promise={workspacePromise}
      pending={
        <DrawerPanelBody>
          <Bullseye>
            <Spinner />
          </Bullseye>
        </DrawerPanelBody>
      }
      resolved={(workspace) => (
        <>
          <Alerts width={"100%"} ref={alertsRef} />
          <DrawerHead>
            <Flex>
              <FlexItem>
                <TextContent>
                  <Text
                    component={TextVariants.h3}
                  >{`Editable files in '${workspacePromise.data?.descriptor.name}'`}</Text>
                </TextContent>
              </FlexItem>
              <FlexItem>
                <Dropdown
                  isPlain={true}
                  position={"left"}
                  isOpen={isNewFileDropdownMenuOpen}
                  toggle={
                    <DropdownToggle
                      className={"kie-tools--masthead-hoverable"}
                      toggleIndicator={null}
                      onToggle={setNewFileDropdownMenuOpen}
                    >
                      <PlusIcon />
                    </DropdownToggle>
                  }
                >
                  <NewFileDropdownMenu
                    alerts={alerts}
                    workspaceId={workspace.descriptor.workspaceId}
                    destinationDirPath={""}
                    onAddFile={async () => setNewFileDropdownMenuOpen(false)}
                  />
                </Dropdown>
              </FlexItem>
            </Flex>
            {(workspace.descriptor.origin.kind === WorkspaceKind.GITHUB_GIST ||
              workspace.descriptor.origin.kind === WorkspaceKind.GIT) && (
              <TextContent>
                <Text component={TextVariants.small}>
                  <i>{workspace.descriptor.origin.url.toString()}</i>
                </Text>
              </TextContent>
            )}
            <DrawerActions>
              <DrawerCloseButton onClick={props.onClose} />
            </DrawerActions>
          </DrawerHead>
          <DrawerPanelBody>
            <AutoSizer>
              {({ height, width }) => (
                <VariableSizeList
                  height={height}
                  itemCount={arrayWithModelsThenOtherFiles.length}
                  itemSize={(index) => getFileDataListHeight(arrayWithModelsThenOtherFiles[index])}
                  width={width}
                >
                  {({ index, style }) => (
                    <FileDataList
                      file={arrayWithModelsThenOtherFiles[index]}
                      isEditable={index < editableFiles.length}
                      style={style}
                    />
                  )}
                </VariableSizeList>
              )}
            </AutoSizer>
          </DrawerPanelBody>
        </>
      )}
    />
  );
}

export function WorkspaceLoadingCard() {
  return (
    <Card>
      <CardBody>
        <Skeleton fontSize={"sm"} width={"40%"} />
        <br />
        <Skeleton fontSize={"sm"} width={"70%"} />
      </CardBody>
    </Card>
  );
}

export function WorkspaceCardError(props: { workspace: WorkspaceDescriptor }) {
  const workspaces = useWorkspaces();
  return (
    <Card isSelected={false} isSelectable={true} isHoverable={true} isCompact={true}>
      <CardHeader>
        <CardHeaderMain>
          <Flex>
            <FlexItem>
              <CardTitle>
                <TextContent>
                  <Text component={TextVariants.h3}>
                    <ExclamationTriangleIcon />
                    &nbsp;&nbsp;
                    {`There was an error obtaining information for '${props.workspace.workspaceId}'`}
                  </Text>
                </TextContent>
              </CardTitle>
            </FlexItem>
          </Flex>
        </CardHeaderMain>
        <CardActions>
          <DeleteDropdownWithConfirmation
            onDelete={() => {
              workspaces.deleteWorkspace({ workspaceId: props.workspace.workspaceId });
            }}
            item={
              <>
                Delete <b>{`"${props.workspace.name}"`}</b>
              </>
            }
          />
        </CardActions>
      </CardHeader>
    </Card>
  );
}

export function WorkspaceCard(props: {
  workspaceId: string;
  isSelected: boolean;
  onSelect: () => void;
  selectedWorkspaceIds: WorkspaceDescriptor["workspaceId"][];
  setSelectedWorkspaceIds: React.Dispatch<React.SetStateAction<WorkspaceDescriptor["workspaceId"][]>>;
}) {
  const { setSelectedWorkspaceIds, workspaceId } = props;
  const routes = useRoutes();
  const history = useHistory();
  const workspaces = useWorkspaces();
  const [isHovered, setHovered] = useState(false);
  const workspacePromise = useWorkspacePromise(props.workspaceId);

  const { editableFiles, readonlyFiles } = useMemo(
    () => splitFiles(workspacePromise.data?.files ?? []),
    [workspacePromise.data?.files]
  );

  const workspaceName = useMemo(() => {
    return workspacePromise.data ? workspacePromise.data.descriptor.name : null;
  }, [workspacePromise.data]);

  const isWsCheckboxChecked = useMemo(
    () => props.selectedWorkspaceIds.includes(props.workspaceId),
    [props.selectedWorkspaceIds, props.workspaceId]
  );

  const onWsCheckboxChange = useCallback(
    (event: React.MouseEvent<HTMLButtonElement>) => {
      event.stopPropagation();
      setSelectedWorkspaceIds((prevState) =>
        !isWsCheckboxChecked ? [...prevState, workspaceId] : prevState.filter((id) => id !== workspaceId)
      );
    },
    [isWsCheckboxChecked, workspaceId, setSelectedWorkspaceIds]
  );

  return (
    <PromiseStateWrapper
      promise={workspacePromise}
      pending={<WorkspaceLoadingCard />}
      rejected={() => <>ERROR</>}
      resolved={(workspace) => (
        <>
          {(editableFiles.length === 1 &&
            readonlyFiles.length === 0 &&
            workspace.descriptor.origin.kind === WorkspaceKind.LOCAL && (
              <Card
                isSelected={props.isSelected}
                isSelectable={true}
                onMouseOver={() => setHovered(true)}
                onMouseLeave={() => setHovered(false)}
                isHoverable={true}
                isCompact={true}
                style={{ cursor: "pointer" }}
                onClick={() => {
                  history.push({
                    pathname: routes.workspaceWithFilePath.path({
                      workspaceId: editableFiles[0].workspaceId,
                      fileRelativePath: editableFiles[0].relativePathWithoutExtension,
                      extension: editableFiles[0].extension,
                    }),
                  });
                }}
              >
                <CardHeader>
                  <Button
                    variant="plain"
                    aria-label="Select"
                    onClick={onWsCheckboxChange}
                    style={{ padding: "0px 4px" }}
                  >
                    <Checkbox
                      id={"checkbox-" + workspace.descriptor.workspaceId}
                      isChecked={isWsCheckboxChecked}
                      onChange={() => {}}
                    />
                  </Button>
                  &nbsp; &nbsp;
                  <Link
                    to={routes.workspaceWithFilePath.path({
                      workspaceId: editableFiles[0].workspaceId,
                      fileRelativePath: editableFiles[0].relativePathWithoutExtension,
                      extension: editableFiles[0].extension,
                    })}
                  >
                    <CardHeaderMain style={{ width: "100%" }}>
                      <Flex>
                        <FlexItem>
                          <CardTitle>
                            <TextContent>
                              <Text
                                component={TextVariants.h3}
                                style={{ textOverflow: "ellipsis", overflow: "hidden" }}
                              >
                                <TaskIcon />
                                &nbsp;&nbsp;
                                {editableFiles[0].nameWithoutExtension}
                              </Text>
                            </TextContent>
                          </CardTitle>
                        </FlexItem>
                        <FlexItem>
                          <b>
                            <FileLabel extension={editableFiles[0].extension} />
                          </b>
                        </FlexItem>
                      </Flex>
                    </CardHeaderMain>
                  </Link>
                  <CardActions>
                    {isHovered && (
                      <DeleteDropdownWithConfirmation
                        onDelete={() => {
                          workspaces.deleteWorkspace({ workspaceId: props.workspaceId });
                        }}
                        item={
                          <Flex flexWrap={{ default: "nowrap" }}>
                            <FlexItem>
                              Delete <b>{`"${editableFiles[0].nameWithoutExtension}"`}</b>
                            </FlexItem>
                            <FlexItem>
                              <b>
                                <FileLabel extension={editableFiles[0].extension} />
                              </b>
                            </FlexItem>
                          </Flex>
                        }
                      />
                    )}
                  </CardActions>
                </CardHeader>
                <CardBody>
                  <TextContent>
                    <Text component={TextVariants.p}>
                      <b>{`Created: `}</b>
                      <RelativeDate date={new Date(workspacePromise.data?.descriptor.createdDateISO ?? "")} />
                      <b>{`, Last updated: `}</b>
                      <RelativeDate date={new Date(workspacePromise.data?.descriptor.lastUpdatedDateISO ?? "")} />
                    </Text>
                  </TextContent>
                </CardBody>
              </Card>
            )) || (
            <Card
              isSelected={props.isSelected}
              isSelectable={true}
              onMouseOver={() => setHovered(true)}
              onMouseLeave={() => setHovered(false)}
              isHoverable={true}
              isCompact={true}
              style={{ cursor: "pointer" }}
              onClick={props.onSelect}
            >
              <CardHeader>
                <Button variant="plain" aria-label="Select" onClick={onWsCheckboxChange} style={{ padding: "0px 4px" }}>
                  <Checkbox
                    id={"checkbox-" + workspace.descriptor.workspaceId}
                    isChecked={isWsCheckboxChecked}
                    onChange={() => {}}
                  />
                </Button>
                &nbsp; &nbsp;
                <CardHeaderMain style={{ width: "100%" }}>
                  <Flex>
                    <FlexItem>
                      <CardTitle>
                        <TextContent>
                          <Text component={TextVariants.h3} style={{ textOverflow: "ellipsis", overflow: "hidden" }}>
                            <FolderIcon />
                            &nbsp;&nbsp;
                            {workspaceName}
                            &nbsp;&nbsp;
                            <WorkspaceLabel descriptor={workspacePromise.data?.descriptor} />
                          </Text>
                        </TextContent>
                      </CardTitle>
                    </FlexItem>
                    <FlexItem>
                      <Text component={TextVariants.p}>
                        {`${editableFiles?.length} editable files(s) in ${workspace.files.length} file(s)`}
                      </Text>
                    </FlexItem>
                  </Flex>
                </CardHeaderMain>
                <CardActions>
                  {isHovered && (
                    <DeleteDropdownWithConfirmation
                      onDelete={() => {
                        workspaces.deleteWorkspace({ workspaceId: props.workspaceId });
                      }}
                      item={
                        <>
                          Delete <b>{`"${workspacePromise.data?.descriptor.name}"`}</b>
                        </>
                      }
                    />
                  )}
                </CardActions>
              </CardHeader>
              <CardBody>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <b>{`Created: `}</b>
                    <RelativeDate date={new Date(workspacePromise.data?.descriptor.createdDateISO ?? "")} />
                    <b>{`, Last updated: `}</b>
                    <RelativeDate date={new Date(workspacePromise.data?.descriptor.lastUpdatedDateISO ?? "")} />
                  </Text>
                </TextContent>
              </CardBody>
            </Card>
          )}
        </>
      )}
    />
  );
}
