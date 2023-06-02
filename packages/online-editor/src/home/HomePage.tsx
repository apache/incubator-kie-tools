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
import {
  SupportedFileExtensions,
  useEditorEnvelopeLocator,
} from "../envelopeLocator/hooks/EditorEnvelopeLocatorContext";
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
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Stack, StackItem } from "@patternfly/react-core/dist/js/layouts/Stack";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { OnlineEditorPage } from "../pageTemplate/OnlineEditorPage";
import { useWorkspaceDescriptorsPromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspacesHooks";
import { useWorkspacePromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspaceHooks";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { FileLabel } from "../filesList/FileLabel";
import { PromiseStateWrapper } from "@kie-tools-core/react-hooks/dist/PromiseState";
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
import { UploadCard } from "./UploadCard";
import { ImportFromUrlCard } from "../importFromUrl/ImportFromUrlHomePageCard";
import { WorkspaceKind } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceOrigin";
import { PlusIcon } from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { NewFileDropdownMenu } from "../editor/Toolbar/NewFileDropdownMenu";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { useRoutes } from "../navigation/Hooks";
import { ErrorBoundary } from "../reactExt/ErrorBoundary";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { VariableSizeList } from "react-window";
import AutoSizer from "react-virtualized-auto-sizer";
import {
  FileDataList,
  FileLink,
  FileListItemDisplayMode,
  getFileDataListHeight,
  SingleFileWorkspaceListItem,
} from "../filesList/FileDataList";
import { WorkspaceListItem } from "../workspace/components/WorkspaceListItem";
import { WorkspaceLoadingCard } from "../workspace/components/WorkspaceLoadingCard";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { ResponsiveDropdown } from "../ResponsiveDropdown/ResponsiveDropdown";
import { ResponsiveDropdownToggle } from "../ResponsiveDropdown/ResponsiveDropdownToggle";
import { listDeletedFiles } from "../workspace/components/WorkspaceStatusIndicator";
import { useEditorsConfig } from "../envelopeLocator/hooks/EditorEnvelopeLocatorContext";
import { useEnv } from "../env/hooks/EnvContext";

export function HomePage() {
  const routes = useRoutes();
  const history = useHistory();
  const workspaceDescriptorsPromise = useWorkspaceDescriptorsPromise();
  const expandedWorkspaceId = useQueryParam(QueryParams.EXPAND);
  const queryParams = useQueryParams();
  const editorsConfig = useEditorsConfig();
  const { env } = useEnv();

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
    document.title = `${env.KIE_SANDBOX_APP_NAME} :: Home`;
  }, [env.KIE_SANDBOX_APP_NAME]);

  return (
    <OnlineEditorPage>
      <PageSection
        isFilled={false}
        sticky={"top"}
        hasOverflowScroll={false}
        style={{ overflowX: "scroll" }}
        aria-label="New Models Section"
      >
        <Grid hasGutter style={{ minWidth: "1280px", gridGap: "var(--pf-c-page__main-section--PaddingTop)" }}>
          <GridItem span={6}>
            <PageSection variant={"light"} isFilled={true} style={{ height: "100%" }}>
              <TextContent>
                <Text component={TextVariants.h1}>Create</Text>
              </TextContent>
              <br />
              <Divider inset={{ default: "insetXl" }} />
              <Gallery
                hasGutter={true}
                // var(--pf-c-page__main-section--PaddingTop) is the "Gutter" width.
                minWidths={{
                  default: "calc(" + 100 / editorsConfig.length + "% - var(--pf-c-page__main-section--PaddingTop))",
                }}
                style={{ height: "calc(100% - 32px)", gridAutoFlow: "column" }}
              >
                {editorsConfig.map((config, index) => {
                  return (
                    <NewModelCard
                      key={index}
                      title={config.card.title}
                      extension={config.extension}
                      description={config.card.description}
                    />
                  );
                })}
              </Gallery>
            </PageSection>
          </GridItem>
          <GridItem span={6}>
            <PageSection variant={"light"} isFilled={true} style={{ height: "100%" }}>
              <TextContent>
                <Text component={TextVariants.h1}>Import</Text>
              </TextContent>
              <br />
              <Divider inset={{ default: "insetXl" }} />
              <Gallery
                hasGutter={true}
                // var(--pf-c-page__main-section--PaddingTop) is the "Gutter" width.
                minWidths={{ default: "calc(50% - var(--pf-c-page__main-section--PaddingTop))" }}
                style={{ height: "calc(100% - 32px)", gridAutoFlow: "column" }}
              >
                <ImportFromUrlCard />
                <UploadCard expandWorkspace={expandWorkspace} />
              </Gallery>
            </PageSection>
          </GridItem>
        </Grid>
      </PageSection>
      <PageSection isFilled={true} variant={"light"} hasOverflowScroll={true} aria-label="Workspace Section">
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
                        <br /> {/* Do not let bottom shadow be cut at the bottom*/}
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
            );
          }}
        />
      </PageSection>
    </OnlineEditorPage>
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
        <CardActions
          onClick={(e) => e.stopPropagation()} // Prevent bug when clicking at the backdrop of ResponsiveDropdown
        >
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
  const editorEnvelopeLocator = useEditorEnvelopeLocator();
  const routes = useRoutes();
  const history = useHistory();
  const workspaces = useWorkspaces();
  const [isHovered, setHovered] = useState(false);
  const workspacePromise = useWorkspacePromise(props.workspaceId);

  const editableFiles = useMemo(() => {
    return workspacePromise.data?.files.filter((file) => editorEnvelopeLocator.hasMappingFor(file.relativePath)) ?? [];
  }, [editorEnvelopeLocator, workspacePromise.data?.files]);

  return (
    <PromiseStateWrapper
      promise={workspacePromise}
      pending={<WorkspaceLoadingCard isBig={true} />}
      rejected={() => <>ERROR</>}
      resolved={(workspace) => (
        <>
          {(editableFiles.length === 1 && workspace.descriptor.origin.kind === WorkspaceKind.LOCAL && (
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
                <FileLink file={editableFiles[0]} style={{ width: "100%", minWidth: 0 }}>
                  <CardHeaderMain style={{ width: "100%" }}>
                    <SingleFileWorkspaceListItem
                      isBig={true}
                      file={editableFiles[0]}
                      workspaceDescriptor={workspace.descriptor}
                    />
                  </CardHeaderMain>
                </FileLink>
                <CardActions
                  style={{ visibility: isHovered ? "visible" : "hidden" }}
                  onClick={(e) => e.stopPropagation()} // Prevent bug when clicking at the backdrop of ResponsiveDropdown
                >
                  <DeleteDropdownWithConfirmation
                    key={`${workspace.descriptor.workspaceId}-${isHovered}`}
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
                </CardActions>
              </CardHeader>
            </Card>
          )) || (
            <Card
              isExpanded={false}
              isSelected={props.isSelected}
              isSelectable={true}
              onMouseOver={() => setHovered(true)}
              onMouseLeave={() => setHovered(false)}
              isHoverable={true}
              isCompact={true}
              style={{ cursor: "pointer" }}
              onClick={props.onSelect}
            >
              <CardHeader isToggleRightAligned={true} onExpand={props.onSelect}>
                <CardHeaderMain style={{ width: "100%", minWidth: 0 }}>
                  <WorkspaceListItem
                    isBig={true}
                    workspaceDescriptor={workspace.descriptor}
                    allFiles={workspace.files}
                    editableFiles={editableFiles}
                  />
                </CardHeaderMain>
                <CardActions
                  style={{ visibility: isHovered ? "visible" : "hidden" }}
                  onClick={(e) => e.stopPropagation()} // Prevent bug when clicking at the backdrop of ResponsiveDropdown
                >
                  <DeleteDropdownWithConfirmation
                    key={`${workspace.descriptor.workspaceId}-${isHovered}`}
                    onDelete={() => {
                      workspaces.deleteWorkspace({ workspaceId: props.workspaceId });
                    }}
                    item={
                      <Flex
                        flexWrap={{ default: "nowrap" }}
                        justifyContent={{ default: "justifyContentFlexStart" }}
                        style={{ width: "100%" }}
                        spaceItems={{ default: "spaceItemsNone" }}
                      >
                        <FlexItem>{"Delete "}</FlexItem>
                        <FlexItem style={{ minWidth: 0 }}>
                          <Tooltip distance={5} position={"top-start"} content={workspace.descriptor.name}>
                            <TextContent>
                              <Text
                                component={TextVariants.p}
                                style={{
                                  whiteSpace: "nowrap",
                                  overflow: "hidden",
                                  textOverflow: "ellipsis",
                                }}
                              >
                                <b>{`"${workspace.descriptor.name}"`}</b>
                              </Text>
                            </TextContent>
                          </Tooltip>
                        </FlexItem>
                      </Flex>
                    }
                  />
                </CardActions>
              </CardHeader>
            </Card>
          )}
        </>
      )}
    />
  );
}

export function NewModelCard(props: { title: string; extension: string; description: string }) {
  const routes = useRoutes();

  return (
    <Card isFullHeight={true} isPlain={true} isLarge={true}>
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
          <Link
            to={{
              pathname: routes.import.path({}),
              search: routes.import.queryString({
                url: `${window.location.origin}${window.location.pathname}${routes.static.sample.path({
                  type: props.extension,
                })}`,
              }),
            }}
          >
            <Button
              variant={ButtonVariant.link}
              style={{ paddingLeft: "2px" }}
              ouiaId={`try-${props.extension}-sample-button`}
            >
              Try sample
            </Button>
          </Link>
        </Grid>
      </CardFooter>
    </Card>
  );
}

export function WorkspacesListDrawerPanelContent(props: { workspaceId: string | undefined; onClose: () => void }) {
  const editorEnvelopeLocator = useEditorEnvelopeLocator();
  const workspacePromise = useWorkspacePromise(props.workspaceId);

  const otherFiles = useMemo(
    () =>
      (workspacePromise.data?.files ?? [])
        .sort((a, b) => a.relativePath.localeCompare(b.relativePath))
        .filter((file) => !editorEnvelopeLocator.hasMappingFor(file.relativePath)),
    [editorEnvelopeLocator, workspacePromise.data?.files]
  );

  const models = useMemo(
    () =>
      (workspacePromise.data?.files ?? [])
        .sort((a, b) => a.relativePath.localeCompare(b.relativePath))
        .filter((file) => editorEnvelopeLocator.hasMappingFor(file.relativePath)),
    [editorEnvelopeLocator, workspacePromise.data?.files]
  );

  const arrayWithModelsThenOtherFiles = useMemo(() => {
    return [...models, ...otherFiles];
  }, [models, otherFiles]);

  const [isNewFileDropdownMenuOpen, setNewFileDropdownMenuOpen] = useState(false);

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
          <DrawerHead>
            <Flex>
              <FlexItem>
                <TextContent>
                  <Text component={TextVariants.h3}>{`Models in '${workspace.descriptor.name}'`}</Text>
                </TextContent>
              </FlexItem>
              <FlexItem>
                <ResponsiveDropdown
                  isPlain={true}
                  position={"left"}
                  isOpen={isNewFileDropdownMenuOpen}
                  onClose={() => setNewFileDropdownMenuOpen(false)}
                  title={"Add file"}
                  toggle={
                    <ResponsiveDropdownToggle
                      className={"kie-tools--masthead-hoverable"}
                      toggleIndicator={null}
                      onToggle={() => setNewFileDropdownMenuOpen((prev) => !prev)}
                    >
                      <PlusIcon />
                    </ResponsiveDropdownToggle>
                  }
                >
                  <NewFileDropdownMenu
                    workspaceDescriptor={workspace.descriptor}
                    destinationDirPath={""}
                    onAddFile={async () => setNewFileDropdownMenuOpen(false)}
                  />
                </ResponsiveDropdown>
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
                      displayMode={
                        index < models.length ? FileListItemDisplayMode.enabled : FileListItemDisplayMode.readonly
                      }
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
