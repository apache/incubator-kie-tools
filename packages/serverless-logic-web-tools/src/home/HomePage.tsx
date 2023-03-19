/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { useCallback, useEffect, useMemo, useState } from "react";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { useHistory } from "react-router";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import {
  Card,
  CardActions,
  CardBody,
  CardFooter,
  CardHeader,
  CardHeaderMain,
  CardTitle,
} from "@patternfly/react-core/dist/js/components/Card";
import { ExpandableSection } from "@patternfly/react-core/dist/js/components/ExpandableSection";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Stack, StackItem } from "@patternfly/react-core/dist/js/layouts/Stack";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { OnlineEditorPage } from "../pageTemplate/OnlineEditorPage";
import { useWorkspaceDescriptorsPromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspacesHooks";
import { useWorkspacePromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspaceHooks";
import { FolderIcon } from "@patternfly/react-icons/dist/js/icons/folder-icon";
import { TaskIcon } from "@patternfly/react-icons/dist/js/icons/task-icon";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { FileLabel } from "../workspace/components/FileLabel";
import { PromiseStateWrapper } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { Skeleton } from "@patternfly/react-core/dist/js/components/Skeleton";
import { Gallery } from "@patternfly/react-core/dist/js/layouts/Gallery";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import {
  Drawer,
  DrawerActions,
  DrawerCloseButton,
  DrawerContent,
  DrawerContentBody,
  DrawerHead,
  DrawerPanelBody,
  DrawerPanelContent,
  DrawerSection,
} from "@patternfly/react-core/dist/js/components/Drawer";
import { Link } from "react-router-dom";
import { DeleteDropdownWithConfirmation } from "../editor/DeleteDropdownWithConfirmation";
import { useQueryParam, useQueryParams } from "../queryParams/QueryParamsContext";
import { QueryParams } from "../navigation/Routes";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { RelativeDate } from "../dates/RelativeDate";
import {
  DataList,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
} from "@patternfly/react-core/dist/js/components/DataList";
import { WorkspaceLabel } from "../workspace/components/WorkspaceLabel";
import { UploadCard } from "./UploadCard";
import { ImportFromUrlCard } from "./ImportFromUrlCard";
import { WorkspaceKind } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceOrigin";
import { Dropdown, DropdownToggle } from "@patternfly/react-core/dist/js/components/Dropdown";
import { PlusIcon } from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { NewFileDropdownMenu } from "../editor/NewFileDropdownMenu";
import { Alerts, AlertsController } from "../alerts/Alerts";
import { useController } from "@kie-tools-core/react-hooks/dist/useController";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { useRoutes } from "../navigation/Hooks";
import { ErrorBoundary } from "../reactExt/ErrorBoundary";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { Showcase } from "./Showcase";
import { FileTypes, splitFiles, SupportedFileExtensions } from "../extension";
import { APP_NAME } from "../AppConstants";

export function HomePage() {
  const routes = useRoutes();
  const history = useHistory();
  const workspaceDescriptorsPromise = useWorkspaceDescriptorsPromise();
  const expandedWorkspaceId = useQueryParam(QueryParams.EXPAND);
  const queryParams = useQueryParams();

  const closeExpandedWorkspace = useCallback(() => {
    history.replace({
      pathname: routes.home.path({}),
      search: queryParams.without(QueryParams.EXPAND).toString(),
    });
  }, [history, routes, queryParams]);

  const expandWorkspace = useCallback(
    (workspaceId: string) => {
      const expand = workspaceId !== expandedWorkspaceId ? workspaceId : undefined;
      if (!expand) {
        closeExpandedWorkspace();
        return;
      }

      history.replace({
        pathname: routes.home.path({}),
        search: routes.home.queryString({ expand }),
      });
    },
    [closeExpandedWorkspace, history, routes, expandedWorkspaceId]
  );

  useEffect(() => {
    document.title = `${APP_NAME} :: Home`;
  }, []);

  useEffect(() => {
    if (
      workspaceDescriptorsPromise.data &&
      !workspaceDescriptorsPromise.data.map((f) => f.workspaceId).includes(expandedWorkspaceId!)
    ) {
      closeExpandedWorkspace();
    }
  }, [workspaceDescriptorsPromise, closeExpandedWorkspace, expandedWorkspaceId]);

  const buildInfo = useMemo(() => {
    return process.env["WEBPACK_REPLACE__buildInfo"];
  }, []);

  return (
    <OnlineEditorPage>
      <PageSection isFilled={false}>
        <Grid hasGutter>
          <GridItem sm={12} xl={6}>
            <PageSection variant={"light"} isFilled={true} style={{ height: "100%" }}>
              <TextContent>
                <Text component={TextVariants.h1}>Create</Text>
              </TextContent>
              <br />
              <Divider inset={{ default: "insetXl" }} />
              <Gallery
                hasGutter={true}
                // 16px is the "Gutter" width.
                minWidths={{ sm: "calc(33% - 16px)", default: "100%" }}
                style={{ height: "calc(100% - 32px)" }}
              >
                <NewServerlessModelCard
                  title={"Workflow"}
                  jsonExtension={FileTypes.SW_JSON}
                  yamlExtension={FileTypes.SW_YAML}
                  description={"Serverless Workflow files are used to define orchestration logic for services."}
                />
                <NewServerlessModelCard
                  title={"Decision"}
                  jsonExtension={FileTypes.YARD_JSON}
                  yamlExtension={FileTypes.YARD_YAML}
                  description={"Serverless Decision files are used to define decision logic for services."}
                />
                <NewModelCard
                  title={"Dashboard"}
                  extension={FileTypes.DASH_YAML}
                  description={
                    "Dashboard files are used to define data visualization from data extracted from applications."
                  }
                />
              </Gallery>
            </PageSection>
          </GridItem>
          <GridItem sm={12} xl={6}>
            <PageSection variant={"light"} isFilled={true} style={{ height: "100%" }}>
              <TextContent>
                <Text component={TextVariants.h1}>Import</Text>
              </TextContent>
              <br />
              <Divider inset={{ default: "insetXl" }} />
              <Gallery
                hasGutter={true}
                // 16px is the "Gutter" width.
                minWidths={{ sm: "calc(50% - 16px)", default: "100%" }}
                style={{ height: "calc(100% - 32px)" }}
              >
                <ImportFromUrlCard />
                <UploadCard expandWorkspace={expandWorkspace} />
              </Gallery>
            </PageSection>
          </GridItem>
        </Grid>
      </PageSection>
      <PageSection isFilled={false} style={{ paddingRight: 0 }}>
        <Showcase />
      </PageSection>
      <PageSection
        isFilled={true}
        variant={"light"}
        hasOverflowScroll={false}
        data-ouia-component-id={"recent-models-section"}
      >
        <PromiseStateWrapper
          promise={workspaceDescriptorsPromise}
          rejected={(e) => <>Error fetching workspaces: {e + ""}</>}
          resolved={(workspaceDescriptors) => {
            return (
              <Drawer isExpanded={!!expandedWorkspaceId} isInline={true}>
                <DrawerSection>
                  <TextContent>
                    <Text component={TextVariants.h1}>Recent models</Text>
                  </TextContent>
                  <br />
                </DrawerSection>
                <DrawerContent
                  panelContent={
                    <WorkspacesListDrawerPanelContent
                      workspaceId={expandedWorkspaceId}
                      onClose={closeExpandedWorkspace}
                    />
                  }
                >
                  <DrawerContentBody data-ouia-component-id={"recent-models-section-body"}>
                    {workspaceDescriptors.length > 0 && (
                      <Stack hasGutter={true} style={{ padding: "10px" }}>
                        {workspaceDescriptors
                          .sort((a, b) => (new Date(a.lastUpdatedDateISO) < new Date(b.lastUpdatedDateISO) ? 1 : -1))
                          .map((workspace) => (
                            <StackItem key={workspace.workspaceId}>
                              <ErrorBoundary error={<WorkspaceCardError workspace={workspace} />}>
                                <WorkspaceCard
                                  workspaceId={workspace.workspaceId}
                                  onSelect={() => expandWorkspace(workspace.workspaceId)}
                                  isSelected={workspace.workspaceId === expandedWorkspaceId}
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
                          <Title headingLevel="h4" size="lg" ouiaId={"empty-recent-models-title"}>
                            {`Nothing here`}
                          </Title>
                          <EmptyStateBody>{`Start by adding a new model`}</EmptyStateBody>
                        </EmptyState>
                      </Bullseye>
                    )}
                  </DrawerContentBody>
                </DrawerContent>
              </Drawer>
            );
          }}
        />
      </PageSection>
      {buildInfo && (
        <div className={"kie-tools--build-info"}>
          <Label>{buildInfo}</Label>
        </div>
      )}
    </OnlineEditorPage>
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

export function WorkspaceCard(props: { workspaceId: string; isSelected: boolean; onSelect: () => void }) {
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

export function NewModelCard(props: { title: string; extension: SupportedFileExtensions; description: string }) {
  const routes = useRoutes();

  return (
    <Card isFullHeight={true} isPlain={true} isLarge={true} ouiaId={`${props.title}-card`}>
      <CardTitle>
        <FileLabel style={{ fontSize: "0.8em" }} extension={props.extension} />
      </CardTitle>
      <CardBody>
        <TextContent>
          <Text component={TextVariants.p}>{props.description}</Text>
        </TextContent>
      </CardBody>
      <CardFooter>
        <Grid>
          <Link to={{ pathname: routes.newModel.path({ extension: props.extension }) }}>
            <Button variant={ButtonVariant.secondary} ouiaId={`new-${props.extension}-button`}>
              New {props.title}
            </Button>
          </Link>
        </Grid>
      </CardFooter>
    </Card>
  );
}

export function NewServerlessModelCard(props: {
  title: string;
  jsonExtension: SupportedFileExtensions;
  yamlExtension: SupportedFileExtensions;
  description: string;
}) {
  const routes = useRoutes();

  return (
    <Card isFullHeight={true} isPlain={true} isLarge={true} ouiaId={`${props.title}-card`}>
      <CardTitle>
        <FileLabel style={{ fontSize: "0.8em" }} extension={props.jsonExtension} />
      </CardTitle>
      <CardBody>
        <TextContent>
          <Text component={TextVariants.p}>{props.description}</Text>
        </TextContent>
      </CardBody>
      <CardFooter>
        <Grid hasGutter>
          <GridItem span={12}>New {props.title}</GridItem>
          <GridItem span={6}>
            <Link to={{ pathname: routes.newModel.path({ extension: props.jsonExtension }) }}>
              <Button variant={ButtonVariant.secondary} ouiaId={`new-${props.jsonExtension}-button`}>
                JSON
              </Button>
            </Link>
          </GridItem>
          <GridItem span={6}>
            <Link to={{ pathname: routes.newModel.path({ extension: props.yamlExtension }) }}>
              <Button variant={ButtonVariant.secondary} ouiaId={`new-${props.yamlExtension}-button`}>
                YAML
              </Button>
            </Link>
          </GridItem>
        </Grid>
      </CardFooter>
    </Card>
  );
}

export function WorkspacesListDrawerPanelContent(props: { workspaceId: string | undefined; onClose: () => void }) {
  const routes = useRoutes();
  const workspacePromise = useWorkspacePromise(props.workspaceId);

  const { editableFiles, readonlyFiles } = useMemo(() => {
    const allFiles = (workspacePromise.data?.files ?? []).sort((a, b) => a.relativePath.localeCompare(b.relativePath));
    return splitFiles(allFiles);
  }, [workspacePromise.data?.files]);

  const [isNewFileDropdownMenuOpen, setNewFileDropdownMenuOpen] = useState(false);
  const [alerts, alertsRef] = useController<AlertsController>();

  return (
    <DrawerPanelContent isResizable={true} minSize={"40%"} maxSize={"80%"}>
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
              <DataList aria-label="models-data-list">
                {editableFiles.map((file) => (
                  <Link
                    key={file.relativePath}
                    to={routes.workspaceWithFilePath.path({
                      workspaceId: workspace.descriptor.workspaceId ?? "",
                      fileRelativePath: file.relativePathWithoutExtension,
                      extension: file.extension,
                    })}
                  >
                    <FileDataListItem file={file} />
                  </Link>
                ))}
              </DataList>
              <br />
              {readonlyFiles.length > 0 && (
                <ExpandableSection
                  toggleTextCollapsed="View readonly files"
                  toggleTextExpanded="Hide readonly files"
                  className={"plain"}
                >
                  <DataList aria-label="readonly-files-data-list">
                    {readonlyFiles.map((file) => (
                      <Link
                        key={file.relativePath}
                        to={routes.workspaceWithFilePath.path({
                          workspaceId: workspace.descriptor.workspaceId ?? "",
                          fileRelativePath: file.relativePathWithoutExtension,
                          extension: file.extension,
                        })}
                      >
                        <FileDataListItem key={file.relativePath} file={file} />
                      </Link>
                    ))}
                  </DataList>
                </ExpandableSection>
              )}
            </DrawerPanelBody>
          </>
        )}
      />
    </DrawerPanelContent>
  );
}

export function FileDataListItem(props: { file: WorkspaceFile }) {
  return (
    <DataListItem>
      <DataListItemRow>
        <DataListItemCells
          dataListCells={[
            <DataListCell key="link" isFilled={false}>
              <Flex flexWrap={{ default: "nowrap" }}>
                <FlexItem>{props.file.nameWithoutExtension}</FlexItem>
                <FlexItem>
                  <FileLabel extension={props.file.extension} />
                </FlexItem>
              </Flex>
              <TextContent>
                <Text component={TextVariants.small}>{props.file.relativeDirPath.split("/").join(" > ")}</Text>
              </TextContent>
            </DataListCell>,
          ]}
        />
      </DataListItemRow>
    </DataListItem>
  );
}
