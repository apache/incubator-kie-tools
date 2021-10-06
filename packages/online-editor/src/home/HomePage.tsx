import * as React from "react";
import { useCallback, useMemo, useState } from "react";
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
import { LocalFile, useWorkspaces } from "../workspace/WorkspacesContext";
import { OnlineEditorPage } from "./pageTemplate/OnlineEditorPage";
import { useWorkspaceDescriptorsPromise } from "../workspace/hooks/WorkspacesHooks";
import { useWorkspacePromise } from "../workspace/hooks/WorkspaceHooks";
import { SUPPORTED_FILES_EDITABLE } from "../workspace/SupportedFiles";
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
  DrawerPanelContent,
} from "@patternfly/react-core/dist/js/components/Drawer";
import { WorkspaceDescriptor } from "../workspace/model/WorkspaceDescriptor";
import {
  DataList,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
} from "@patternfly/react-core/dist/js/components/DataList";
import { Link } from "react-router-dom";
import { DeleteDropdownWithConfirmation } from "../editor/DeleteDropdownWithConfirmation";

export function HomePage() {
  const globals = useGlobals();
  const history = useHistory();
  const workspaces = useWorkspaces();
  const workspaceDescriptorsPromise = useWorkspaceDescriptorsPromise();
  const [url, setUrl] = useState("");
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
              reader.onload = (event: any) => resolve(event.target.result as string);
              reader.readAsText(file);
            }),
        };
      })
    );
  }, []);

  const createWorkspaceFromUploadedFolder = useCallback(() => {
    if (filesToUpload.length === 0) {
      return;
    }

    workspaces.createWorkspaceFromLocal(filesToUpload).then(({ descriptor, suggestedFirstFile }) => {
      if (!suggestedFirstFile) {
        return;
      }
      history.replace({
        pathname: globals.routes.workspaceWithFilePath.path({
          workspaceId: descriptor.workspaceId,
          filePath: suggestedFirstFile.pathRelativeToWorkspaceRootWithoutExtension,
          extension: suggestedFirstFile.extension,
        }),
      });
    });
  }, [filesToUpload, workspaces, history, globals]);

  const [expandedWorkspace, setExpandedWorkspace] = useState<WorkspaceDescriptor>();

  return (
    <OnlineEditorPage>
      <Stack hasGutter={true}>
        <StackItem key={"cards"}>
          <Split isWrappable={true}>
            <SplitItem isFilled={true}>
              {/*<PageSection isFilled={true} variant={"light"} style={{ height: "100%" }}>*/}
              <PageSection isFilled={true} style={{ height: "100%" }}>
                <TextContent>
                  <Text component={TextVariants.h1}>Create</Text>
                </TextContent>
                <br />
                {/*<Divider inset={{default: "insetXl"}}/>*/}
                <Gallery
                  hasGutter={true}
                  minWidths={{ sm: "calc(33% - 16px)", default: "100%" }}
                  style={{ height: "calc(100% - 32px)" }}
                >
                  <CreateNewCard title={"Process"} extension={"bpmn"} />
                  {/*<Divider isVertical={true}/>*/}
                  <CreateNewCard title={"Decision"} extension={"dmn"} />
                  {/*<Divider isVertical={true}/>*/}
                  <CreateNewCard title={"Scorecard"} extension={"pmml"} />
                </Gallery>
              </PageSection>
            </SplitItem>
            <SplitItem isFilled={true}>
              {/*<PageSection isFilled={true} variant={"light"} style={{ height: "100%" }}>*/}
              <PageSection isFilled={true} style={{ height: "100%" }}>
                <TextContent>
                  <Text component={TextVariants.h1}>Import</Text>
                </TextContent>
                <br />
                {/*<Divider inset={{default: "insetXl"}}/>*/}
                <Gallery
                  hasGutter={true}
                  minWidths={{ sm: "calc(50% - 16px)", default: "100%" }}
                  style={{ height: "calc(100% - 32px)" }}
                >
                  <Card
                    isFullHeight={true}
                    isLarge={true}
                    isPlain={false}
                    isHoverable={true}
                    isSelectable={true}
                    isSelected={filesToUpload.length > 0}
                  >
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
                        variant={filesToUpload.length > 0 ? ButtonVariant.primary : ButtonVariant.secondary}
                        onClick={createWorkspaceFromUploadedFolder}
                      >
                        Upload
                      </Button>
                    </CardFooter>
                  </Card>
                  {/*<Divider isVertical={true}/>*/}
                  <Card
                    isFullHeight={true}
                    isLarge={true}
                    isPlain={false}
                    isHoverable={true}
                    isSelectable={true}
                    isSelected={url.length > 0}
                  >
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
        </StackItem>
        <StackItem>
          <br />
          <Divider inset={{ default: "inset3xl" }} />
        </StackItem>
        <StackItem key={"workspaces"} isFilled={true}>
          <PageSection isFilled={true} style={{ height: "100%" }}>
            <PromiseStateWrapper
              promise={workspaceDescriptorsPromise}
              rejected={() => <></>}
              resolved={(workspaceDescriptors) => {
                return (
                  <Drawer isExpanded={!!expandedWorkspace} isInline={true}>
                    <DrawerContent
                      panelContent={
                        <>
                          {expandedWorkspace && (
                            <WorkspacesListDrawerPanelContent
                              workspaceDescriptor={expandedWorkspace}
                              onClose={() => setExpandedWorkspace(undefined)}
                            />
                          )}
                        </>
                      }
                    >
                      <DrawerContentBody>
                        {workspaceDescriptors.length > 0 && (
                          <PageSection variant={"light"}>
                            <Stack hasGutter={true}>
                              {workspaceDescriptors.map((workspace) => (
                                <StackItem key={workspace.workspaceId}>
                                  <WorkspaceCard
                                    workspaceId={workspace.workspaceId}
                                    onSelect={() => {
                                      setExpandedWorkspace((prev) => (prev === workspace ? undefined : workspace));
                                    }}
                                    isSelected={workspace === expandedWorkspace}
                                  />
                                </StackItem>
                              ))}
                            </Stack>
                          </PageSection>
                        )}
                        {workspaceDescriptors.length === 0 && (
                          <PageSection isFilled={true} style={{ height: "100%" }}>
                            <EmptyState>
                              <EmptyStateIcon icon={CubesIcon} />
                              <Title headingLevel="h4" size="lg">
                                {`Nothing here.`}
                              </Title>
                              <EmptyStateBody>{`Start by adding a new model`}</EmptyStateBody>
                            </EmptyState>
                          </PageSection>
                        )}
                      </DrawerContentBody>
                    </DrawerContent>
                  </Drawer>
                );
              }}
            />
          </PageSection>
        </StackItem>
      </Stack>
    </OnlineEditorPage>
  );
}

function WorkspaceLoadingCard() {
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

function WorkspaceCard(props: { workspaceId: string; isSelected: boolean; onSelect: () => void }) {
  const globals = useGlobals();
  const history = useHistory();
  const workspaces = useWorkspaces();
  const [isHovered, setHovered] = useState(false);
  const workspacePromise = useWorkspacePromise(props.workspaceId);

  const editableFiles = useMemo(() => {
    return workspacePromise.data?.files.filter((file) => SUPPORTED_FILES_EDITABLE.includes(file.extension)) ?? [];
  }, [workspacePromise]);

  const workspaceName = useMemo(() => {
    return workspacePromise.data ? workspacePromise.data.descriptor.name : null;
  }, [workspacePromise.data]);

  const createdDate = useMemo(() => {
    return workspacePromise.data ? new Date(workspacePromise.data.descriptor.createdDateISO).toLocaleString() : null;
  }, [workspacePromise.data]);

  const lastUpdatedDate = useMemo(() => {
    return workspacePromise.data
      ? new Date(workspacePromise.data.descriptor.lastUpdatedDateISO).toLocaleString()
      : null;
  }, [workspacePromise.data]);

  return (
    <PromiseStateWrapper
      promise={workspacePromise}
      pending={<WorkspaceLoadingCard />}
      rejected={() => <>ERROR</>}
      resolved={(workspace) => (
        <>
          {editableFiles.length === 1 && (
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
                    filePath: editableFiles[0].pathRelativeToWorkspaceRootWithoutExtension,
                    extension: editableFiles[0].extension,
                  }),
                });
              }}
            >
              <CardHeader>
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
                <CardActions>
                  {isHovered && (
                    <DeleteDropdownWithConfirmation
                      onDelete={() => {
                        if (workspacePromise.data) {
                          workspaces.workspaceService.delete(workspacePromise.data.descriptor, { broadcast: true });
                        }
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
                    {createdDate}
                    <b>{`, Last updated: `}</b>
                    {lastUpdatedDate}
                  </Text>
                </TextContent>
              </CardBody>
            </Card>
          )}
          {(editableFiles.length > 1 || editableFiles.length < 1) && (
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
                      onDelete={() => {
                        if (workspacePromise.data) {
                          workspaces.workspaceService.delete(workspacePromise.data.descriptor, { broadcast: true });
                        }
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
                    {createdDate}
                    <b>{`, Last updated: `}</b>
                    {lastUpdatedDate}
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

function CreateNewCard(props: { title: string; extension: SupportedFileExtensions }) {
  const globals = useGlobals();
  const history = useHistory();

  const newModel = useCallback(() => {
    history.push({ pathname: globals.routes.newModel.path({ extension: props.extension }) });
  }, [history, globals, props.extension]);

  const newSample = useCallback(() => {
    history.push({
      pathname: globals.routes.importModel.path({}),
      search: globals.routes.importModel.queryString({
        url: globals.routes.static.sample.path({ type: props.extension }),
      }),
    });
  }, [history, globals, props.extension]);

  return (
    <Card isFullHeight={true} isPlain={false} isLarge={true} isHoverable={true}>
      <CardTitle>
        <Flex>
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
        <Button variant={ButtonVariant.link} onClick={newSample} style={{ paddingLeft: "2px" }}>
          Open sample
        </Button>
        <br />
        <br />
        <Button isLarge={true} variant={ButtonVariant.secondary} onClick={newModel}>
          New {props.title}
        </Button>
      </CardFooter>
    </Card>
  );
}

export function WorkspacesListDrawerPanelContent(props: {
  workspaceDescriptor: WorkspaceDescriptor;
  onClose: () => void;
}) {
  const globals = useGlobals();
  const workspacePromise = useWorkspacePromise(props.workspaceDescriptor.workspaceId);
  return (
    <DrawerPanelContent isResizable={true} minSize={"40%"} maxSize={"80%"}>
      <DrawerHead>
        <TextContent>
          <Text component={TextVariants.h3}>{props.workspaceDescriptor.name}</Text>
        </TextContent>
        <DrawerActions>
          <DrawerCloseButton onClick={props.onClose} />
        </DrawerActions>
      </DrawerHead>
      {(workspacePromise.data?.files.length ?? 0) > 0 && (
        <>
          <DataList aria-label="draggable data list example" isCompact>
            {(workspacePromise.data?.files ?? []).map((file) => (
              <React.Fragment key={file.path}>
                <DataListItem aria-labelledby="simple-item1" id="data1" key="1">
                  <DataListItemRow>
                    <DataListItemCells
                      dataListCells={[
                        <DataListCell key={"label"} isIcon={true} style={{ minWidth: "60px" }}>
                          <Flex justifyContent={{ default: "justifyContentFlexEnd" }}>
                            <FileLabel extension={file.extension} />
                          </Flex>
                        </DataListCell>,
                        <DataListCell key="link" isFilled={false}>
                          {SUPPORTED_FILES_EDITABLE.includes(file.extension) ? (
                            <Link
                              to={globals.routes.workspaceWithFilePath.path({
                                workspaceId: props.workspaceDescriptor.workspaceId,
                                filePath: file.pathRelativeToWorkspaceRootWithoutExtension,
                                extension: file.extension,
                              })}
                            >
                              {file.pathRelativeToWorkspaceRoot}
                            </Link>
                          ) : (
                            <Text component={TextVariants.p}>{file.pathRelativeToWorkspaceRoot}</Text>
                          )}
                        </DataListCell>,
                      ]}
                    />
                  </DataListItemRow>
                </DataListItem>
              </React.Fragment>
            ))}
          </DataList>
        </>
      )}
    </DrawerPanelContent>
  );
}
