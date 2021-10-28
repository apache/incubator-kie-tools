import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { SupportedFileExtensions, useGlobals } from "../common/GlobalContext";
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
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { LocalFile, useWorkspaces, WorkspaceFile } from "../workspace/WorkspacesContext";
import { BusinessAutomationStudioPage } from "./pageTemplate/BusinessAutomationStudioPage";
import { useWorkspaceDescriptorsPromise } from "../workspace/hooks/WorkspacesHooks";
import { useWorkspacePromise } from "../workspace/hooks/WorkspaceHooks";
import { FolderIcon } from "@patternfly/react-icons/dist/js/icons/folder-icon";
import { TaskIcon } from "@patternfly/react-icons/dist/js/icons/task-icon";
import { FileLabel } from "../workspace/pages/FileLabel";
import { PromiseStateWrapper } from "../workspace/hooks/PromiseState";
import { Skeleton } from "@patternfly/react-core/dist/js/components/Skeleton";
import { Gallery } from "@patternfly/react-core/dist/js/layouts/Gallery";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
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
import { useQueryParams } from "../queryParams/QueryParamsContext";
import { QueryParams } from "../common/Routes";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { RelativeDate } from "./RelativeDate";
import { WorkspaceKind } from "../workspace/model/WorkspaceOrigin";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import {
  DataList,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
} from "@patternfly/react-core/dist/js/components/DataList";
import { ExpandableSection } from "@patternfly/react-core/dist/js/components/ExpandableSection";

export function HomePage() {
  const globals = useGlobals();
  const history = useHistory();
  const workspaces = useWorkspaces();
  const workspaceDescriptorsPromise = useWorkspaceDescriptorsPromise();
  const [url, setUrl] = useState("");
  const [uploading, setUploading] = useState(false);
  const [filesToUpload, setFilesToUpload] = useState<LocalFile[]>([]);

  const onFolderUpload = useCallback((e) => {
    e.stopPropagation();
    e.preventDefault();

    setFilesToUpload(
      [...e.target.files].map((file: File) => {
        return {
          path: (file as any).webkitRelativePath,
          getFileContents: () =>
            new Promise((resolve) => {
              const reader = new FileReader();
              reader.onload = (event: any) => resolve(event.target.result);
              reader.readAsArrayBuffer(file);
            }),
        };
      })
    );
  }, []);

  const queryParams = useQueryParams();

  const expandedWorkspaceId = useMemo(() => {
    return queryParams.get(QueryParams.EXPAND);
  }, [queryParams]);

  const closeExpandedWorkspace = useCallback(() => {
    history.replace({ pathname: globals.routes.home.path({}) });
  }, [history, globals]);

  const expandWorkspace = useCallback(
    (workspaceId: string) => {
      const expand = workspaceId !== expandedWorkspaceId ? workspaceId : undefined;
      if (!expand) {
        closeExpandedWorkspace();
        return;
      }

      history.replace({
        pathname: globals.routes.home.path({}),
        search: globals.routes.home.queryString({ expand }),
      });
    },
    [closeExpandedWorkspace, history, globals, expandedWorkspaceId]
  );

  useEffect(() => {
    if (
      workspaceDescriptorsPromise.data &&
      !workspaceDescriptorsPromise.data.map((f) => f.workspaceId).includes(expandedWorkspaceId!)
    ) {
      closeExpandedWorkspace();
    }
  }, [workspaceDescriptorsPromise, closeExpandedWorkspace, expandedWorkspaceId]);

  const createWorkspaceFromUploadedFolder = useCallback(() => {
    if (filesToUpload.length === 0) {
      return;
    }

    const preferredName = filesToUpload[0].path.split("/")[0];

    const localFiles: LocalFile[] = filesToUpload.map(
      // Remove first portion of the path, which is the uploaded directory name.
      (file) => ({ ...file, path: file.path.substring(file.path.indexOf("/") + 1) })
    );

    setUploading(true);

    workspaces
      .createWorkspaceFromLocal({
        useInMemoryFs: true,
        localFiles,
        preferredName,
      })
      .then(({ workspace, suggestedFirstFile }) => {
        if (!suggestedFirstFile) {
          expandWorkspace(workspace.workspaceId);
          return;
        }
        history.push({
          pathname: globals.routes.workspaceWithFilePath.path({
            workspaceId: workspace.workspaceId,
            fileRelativePath: suggestedFirstFile.relativePathWithoutExtension,
            extension: suggestedFirstFile.extension,
          }),
        });
      })
      .finally(() => {
        setUploading(false);
      });
  }, [expandWorkspace, filesToUpload, workspaces, history, globals]);

  return (
    <BusinessAutomationStudioPage>
      <PageSection>
        <Split isWrappable={true} hasGutter={true}>
          <SplitItem isFilled={true}>
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
                <NewModelCard title={"Process"} extension={"bpmn"} />
                {/*<Divider isVertical={true} />*/}
                <NewModelCard title={"Decision"} extension={"dmn"} />
                {/*<Divider isVertical={true} />*/}
                <NewModelCard title={"Scorecard"} extension={"pmml"} />
              </Gallery>
            </PageSection>
          </SplitItem>
          <SplitItem isFilled={true}>
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
                <Card isFullHeight={true} isLarge={true} isPlain={true}>
                  <CardTitle>
                    <TextContent>
                      <Text component={TextVariants.h2}>Upload</Text>
                    </TextContent>
                  </CardTitle>
                  <CardBody>
                    <TextContent>
                      <Text component={TextVariants.p}>Import files from your computer.</Text>
                    </TextContent>
                    <br />
                    <input
                      type="file"
                      /* @ts-expect-error directory and webkitdirectory are not available but works*/
                      webkitdirectory=""
                      onChange={onFolderUpload}
                    />
                  </CardBody>
                  <CardFooter>
                    <Button
                      isLoading={uploading}
                      variant={filesToUpload.length > 0 ? ButtonVariant.primary : ButtonVariant.secondary}
                      onClick={createWorkspaceFromUploadedFolder}
                    >
                      Upload
                    </Button>
                  </CardFooter>
                </Card>
                {/*<Divider isVertical={true} />*/}
                <Card isFullHeight={true} isLarge={true} isPlain={true} isSelected={url.length > 0}>
                  <CardTitle>
                    <TextContent>
                      <Text component={TextVariants.h2}>From Gist</Text>
                    </TextContent>
                  </CardTitle>
                  <CardBody>
                    <TextContent>
                      <Text component={TextVariants.p}>Import files from a GitHub Gist.</Text>
                    </TextContent>
                    <br />
                    <TextInput
                      isRequired={true}
                      placeholder={"URL"}
                      value={url}
                      onChange={(v) => {
                        // TODO
                        // validate URL. if validated, change button to "primary"
                        setUrl(v);
                      }}
                    />
                  </CardBody>
                  <CardFooter>
                    <Button
                      variant={url.length > 0 ? ButtonVariant.primary : ButtonVariant.secondary}
                      onClick={() => {
                        // TODO
                        // enable `Enter` key to submit.
                        history.push({
                          pathname: globals.routes.importModel.path({}),
                          search: globals.routes.importModel.queryString({ url: url }),
                        });
                      }}
                    >
                      Import
                    </Button>
                  </CardFooter>
                </Card>
              </Gallery>
            </PageSection>
          </SplitItem>
        </Split>
      </PageSection>
      <PageSection variant={"light"} hasOverflowScroll={true}>
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
                  <DrawerContentBody>
                    {workspaceDescriptors.length > 0 && (
                      <Stack hasGutter={true} style={{ padding: "10px" }}>
                        {workspaceDescriptors
                          .sort((a, b) => (new Date(a.lastUpdatedDateISO) < new Date(b.lastUpdatedDateISO) ? 1 : -1))
                          .map((workspace) => (
                            <StackItem key={workspace.workspaceId}>
                              <WorkspaceCard
                                workspaceId={workspace.workspaceId}
                                onSelect={() => expandWorkspace(workspace.workspaceId)}
                                isSelected={workspace.workspaceId === expandedWorkspaceId}
                              />
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
            );
          }}
        />
      </PageSection>
    </BusinessAutomationStudioPage>
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

export function WorkspaceCard(props: { workspaceId: string; isSelected: boolean; onSelect: () => void }) {
  const globals = useGlobals();
  const history = useHistory();
  const workspaces = useWorkspaces();
  const [isHovered, setHovered] = useState(false);
  const workspacePromise = useWorkspacePromise(props.workspaceId);

  const editableFiles = useMemo(() => {
    return (
      workspacePromise.data?.files.filter((file) =>
        [...globals.editorEnvelopeLocator.mapping.keys()].includes(file.extension)
      ) ?? []
    );
  }, [workspacePromise]);

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
          {workspace.files.length === 1 && (
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
                  pathname: globals.routes.workspaceWithFilePath.path({
                    workspaceId: editableFiles[0].workspaceId,
                    fileRelativePath: editableFiles[0].relativePathWithoutExtension,
                    extension: editableFiles[0].extension,
                  }),
                });
              }}
            >
              <CardHeader>
                <Link
                  to={globals.routes.workspaceWithFilePath.path({
                    workspaceId: editableFiles[0].workspaceId,
                    fileRelativePath: editableFiles[0].relativePathWithoutExtension,
                    extension: editableFiles[0].extension,
                  })}
                >
                  <CardHeaderMain>
                    <Flex>
                      <FlexItem>
                        <CardTitle>
                          <TextContent>
                            <Text component={TextVariants.h3}>
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
                      onDelete={() => workspaces.deleteWorkspace({ workspaceId: props.workspaceId })}
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
          )}
          {(workspace.files.length > 1 || workspace.files.length < 1) && (
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
                <CardHeaderMain>
                  <Flex>
                    <FlexItem>
                      <CardTitle>
                        <TextContent>
                          <Text component={TextVariants.h3}>
                            <FolderIcon />
                            &nbsp;&nbsp;
                            {workspaceName}
                            &nbsp;&nbsp;
                            {workspace.descriptor.origin.kind === WorkspaceKind.GIST && <Label>Gist</Label>}
                          </Text>
                        </TextContent>
                      </CardTitle>
                    </FlexItem>
                    <FlexItem>
                      <Text component={TextVariants.p}>
                        {`${workspace.files.length} files, ${editableFiles?.length} models`}
                      </Text>
                    </FlexItem>
                  </Flex>
                </CardHeaderMain>

                <CardActions>
                  {isHovered && (
                    <DeleteDropdownWithConfirmation
                      onDelete={() => workspaces.deleteWorkspace({ workspaceId: props.workspaceId })}
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

export function NewModelCard(props: { title: string; extension: SupportedFileExtensions }) {
  const globals = useGlobals();

  return (
    <Card isFullHeight={true} isPlain={true} isLarge={true}>
      <CardTitle>
        <Flex flexWrap={{ default: "nowrap" }}>
          <FlexItem>
            <TextContent>
              <Text component={TextVariants.h2}>{props.title}</Text>
            </TextContent>
          </FlexItem>
          <FlexItem>
            <FileLabel extension={props.extension} />
          </FlexItem>
        </Flex>
      </CardTitle>
      <CardBody>
        <TextContent>
          <Text component={TextVariants.p}>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. ... Maecenas efficitur, elit quis
          </Text>
        </TextContent>
      </CardBody>
      <CardFooter>
        <Link
          to={{
            pathname: globals.routes.importModel.path({}),
            search: globals.routes.importModel.queryString({
              url: `${window.location.origin}${window.location.pathname}${globals.routes.static.sample.path({
                type: props.extension,
              })}`,
            }),
          }}
        >
          <Button variant={ButtonVariant.link} style={{ paddingLeft: "2px" }}>
            Open sample
          </Button>
        </Link>
        <br />
        <br />
        <Link to={{ pathname: globals.routes.newModel.path({ extension: props.extension }) }}>
          <Button isLarge={true} variant={ButtonVariant.secondary}>
            New {props.title}
          </Button>
        </Link>
      </CardFooter>
    </Card>
  );
}

export function WorkspacesListDrawerPanelContent(props: { workspaceId: string | undefined; onClose: () => void }) {
  const globals = useGlobals();
  const workspacePromise = useWorkspacePromise(props.workspaceId);

  const otherFiles = useMemo(
    () =>
      (workspacePromise.data?.files ?? [])
        .sort((a, b) => a.relativePath.localeCompare(b.relativePath))
        .filter((file) => ![...globals.editorEnvelopeLocator.mapping.keys()].includes(file.extension)),
    [workspacePromise.data]
  );

  const models = useMemo(
    () =>
      (workspacePromise.data?.files ?? [])
        .sort((a, b) => a.relativePath.localeCompare(b.relativePath))
        .filter((file) => [...globals.editorEnvelopeLocator.mapping.keys()].includes(file.extension)),
    [workspacePromise]
  );

  return (
    <DrawerPanelContent isResizable={true} minSize={"40%"} maxSize={"80%"}>
      <DrawerHead>
        <TextContent>
          <Text component={TextVariants.h3}>{`Models in '${workspacePromise.data?.descriptor.name}'`}</Text>
        </TextContent>
        <DrawerActions>
          <DrawerCloseButton onClick={props.onClose} />
        </DrawerActions>
      </DrawerHead>
      <DrawerPanelBody>
        <DataList aria-label="models-data-list">
          {models.map((file) => (
            <Link
              key={file.relativePath}
              to={globals.routes.workspaceWithFilePath.path({
                workspaceId: workspacePromise.data?.descriptor.workspaceId ?? "",
                fileRelativePath: file.relativePathWithoutExtension,
                extension: file.extension,
              })}
            >
              <FileDataListItem file={file} />
            </Link>
          ))}
        </DataList>
        <br />
        {otherFiles.length > 0 && (
          <ExpandableSection
            toggleTextCollapsed="View other files"
            toggleTextExpanded="Hide other files"
            className={"plain"}
          >
            <DataList aria-label="other-files-data-list">
              {otherFiles.map((file) => (
                <FileDataListItem key={file.relativePath} file={file} />
              ))}
            </DataList>
          </ExpandableSection>
        )}
      </DrawerPanelBody>
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
