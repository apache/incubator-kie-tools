import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { useGlobals } from "../common/GlobalContext";
import { useHistory } from "react-router";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
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
import { Gallery, GalleryItem } from "@patternfly/react-core/dist/js/layouts/Gallery";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateSecondaryActions,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { TrashIcon } from "@patternfly/react-icons/dist/js/icons/trash-icon";
import { LocalFile, useWorkspaces } from "../workspace/WorkspacesContext";
import { QueryParams } from "../common/Routes";
import { useQueryParams } from "../queryParams/QueryParamsContext";
import { OnlineEditorPage } from "./pageTemplate/OnlineEditorPage";
import { useWorkspaceDescriptorsPromise } from "../workspace/hooks/WorkspacesHooks";
import { useWorkspacePromise } from "../workspace/hooks/WorkspaceHooks";
import { SUPPORTED_FILES_EDITABLE } from "../workspace/SupportedFiles";
import { FolderIcon } from "@patternfly/react-icons/dist/js/icons/folder-icon";
import { TaskIcon } from "@patternfly/react-icons/dist/js/icons/task-icon";
import { FileLabel } from "../workspace/pages/FileLabel";
import { PromiseStateWrapper } from "../workspace/hooks/PromiseState";
import { Skeleton } from "@patternfly/react-core/dist/js/components/Skeleton";
import { WorkspaceDescriptor } from "../workspace/model/WorkspaceDescriptor";

export function HomePage() {
  const globals = useGlobals();
  const history = useHistory();
  const workspaces = useWorkspaces();
  const queryParams = useQueryParams();
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

  return (
    <OnlineEditorPage>
      <Gallery maxWidths={{ default: "100%", lg: "50%", md: "100%" }}>
        <GalleryItem>
          <PageSection isFilled={true} style={{ height: "100%" }}>
            <Stack hasGutter={true}>
              <StackItem>
                <PageSection variant={"light"} className={"pf-u-box-shadow-md"}>
                  <TextContent>
                    <Text component={TextVariants.h1}>Create Workspace</Text>
                    <Text component={TextVariants.p}>
                      Start a new Workspace from a model or create an empty one and add files later.
                    </Text>
                    <Flex alignItems={{ default: "alignItemsFlexEnd" }}>
                      <Flex grow={{ default: "grow" }}>
                        <FlexItem>
                          <Button
                            isLarge
                            variant={ButtonVariant.secondary}
                            onClick={() => {
                              history.push({
                                pathname: globals.routes.newModel.path({
                                  extension: "bpmn",
                                }),
                              });
                            }}
                          >
                            BPMN
                          </Button>
                        </FlexItem>
                        <FlexItem>
                          <Button
                            isLarge
                            variant={ButtonVariant.secondary}
                            onClick={() => {
                              history.push({
                                pathname: globals.routes.newModel.path({
                                  extension: "dmn",
                                }),
                              });
                            }}
                          >
                            DMN
                          </Button>
                        </FlexItem>
                        <FlexItem>
                          <Button
                            isLarge
                            variant={ButtonVariant.secondary}
                            onClick={() => {
                              history.push({
                                pathname: globals.routes.newModel.path({
                                  extension: "pmml",
                                }),
                              });
                            }}
                          >
                            PMML
                          </Button>
                        </FlexItem>
                      </Flex>
                      <Flex>
                        <FlexItem>
                          <Button
                            variant="link"
                            onClick={() => {
                              workspaces.createWorkspaceFromLocal([]).then(({ descriptor }) => {
                                history.push({
                                  pathname: globals.routes.workspaceOverview.path({
                                    workspaceId: descriptor.workspaceId,
                                  }),
                                });
                              });
                            }}
                          >
                            Create empty Workspace
                          </Button>
                        </FlexItem>
                      </Flex>
                    </Flex>
                  </TextContent>
                </PageSection>
              </StackItem>

              <StackItem>
                <PageSection variant={"light"} className={"pf-u-box-shadow-md"}>
                  <TextContent>
                    <Text component={TextVariants.h1}>Import Workspace</Text>
                  </TextContent>
                  <br />
                  <Gallery maxWidths={{ default: "100%", lg: "49%", md: "100%" }} hasGutter={true}>
                    <GalleryItem>
                      <Card isFullHeight={true}>
                        <CardTitle>From URL</CardTitle>
                        <CardBody>
                          <TextContent>
                            <Text component={TextVariants.p}>Gist (gist.github.com) URLs are supported.</Text>
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
                            variant={ButtonVariant.secondary}
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
                    </GalleryItem>
                    <GalleryItem>
                      <Card isFullHeight={true}>
                        <CardTitle>Upload</CardTitle>
                        <CardBody>
                          <TextContent>
                            <Text component={TextVariants.p}>Create a Workspace from a folder on your computer.</Text>
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
                            variant={ButtonVariant.secondary}
                            onClick={() => {
                              if (filesToUpload.length === 0) {
                                return;
                              }

                              workspaces.createWorkspaceFromLocal(filesToUpload).then(({ descriptor }) => {
                                history.push({
                                  pathname: globals.routes.workspaceOverview.path({
                                    workspaceId: descriptor.workspaceId,
                                  }),
                                });
                              });
                            }}
                          >
                            Import
                          </Button>
                        </CardFooter>
                      </Card>
                    </GalleryItem>
                  </Gallery>
                </PageSection>
              </StackItem>
              <StackItem>
                <PageSection variant={"light"}>
                  <TextContent>
                    <Text component={TextVariants.h1}>Try samples</Text>
                    <Text component={TextVariants.p}>Start from sample models.</Text>
                    <Flex>
                      <FlexItem>
                        <Button
                          isLarge
                          variant={ButtonVariant.tertiary}
                          onClick={() => {
                            history.push({
                              pathname: globals.routes.importModel.path({ extension: "bpmn" }),
                              search: globals.routes.importModel
                                .queryArgs(queryParams)
                                .with(QueryParams.URL, globals.routes.static.sample.path({ type: "bpmn" }))
                                .toString(),
                            });
                          }}
                        >
                          BPMN
                        </Button>
                      </FlexItem>
                      <FlexItem>
                        <Button
                          isLarge
                          variant={ButtonVariant.tertiary}
                          onClick={() => {
                            history.push({
                              pathname: globals.routes.importModel.path({ extension: "dmn" }),
                              search: globals.routes.importModel
                                .queryArgs(queryParams)
                                .with(QueryParams.URL, globals.routes.static.sample.path({ type: "dmn" }))
                                .toString(),
                            });
                          }}
                        >
                          DMN
                        </Button>
                      </FlexItem>
                      <FlexItem>
                        <Button
                          isLarge
                          variant={ButtonVariant.tertiary}
                          onClick={() => {
                            history.push({
                              pathname: globals.routes.importModel.path({ extension: "pmml" }),
                              search: globals.routes.importModel
                                .queryArgs(queryParams)
                                .with(QueryParams.URL, globals.routes.static.sample.path({ type: "pmml" }))
                                .toString(),
                            });
                          }}
                        >
                          PMML
                        </Button>
                      </FlexItem>
                    </Flex>
                  </TextContent>
                </PageSection>
              </StackItem>
            </Stack>
          </PageSection>
        </GalleryItem>
        {/**/}
        <GalleryItem>
          <PageSection isFilled={true} style={{ height: "100%" }}>
            <PageSection variant={"light"}>
              <PromiseStateWrapper
                promise={workspaceDescriptorsPromise}
                rejected={() => <></>}
                resolved={(workspaceDescriptors) => (
                  <>
                    <TextContent>
                      {workspaceDescriptors.length > 0 && (
                        <Stack hasGutter={true}>
                          {workspaceDescriptors.map((descriptor: WorkspaceDescriptor) => (
                            <StackItem key={descriptor.name}>
                              <WorkspaceCard workspaceId={descriptor.workspaceId} />
                            </StackItem>
                          ))}
                        </Stack>
                      )}
                      {workspaceDescriptors.length === 0 && (
                        <EmptyState>
                          <EmptyStateIcon icon={CubesIcon} />
                          <Title headingLevel="h4" size="lg">
                            {`Nothing here.`}
                          </Title>
                          <EmptyStateBody>{`Start by adding a new model`}</EmptyStateBody>
                          <EmptyStateSecondaryActions>
                            <TextContent>
                              <Flex grow={{ default: "grow" }}>
                                <FlexItem>
                                  <Button
                                    isLarge
                                    variant={ButtonVariant.secondary}
                                    onClick={() => {
                                      history.push({
                                        pathname: globals.routes.newModel.path({
                                          extension: "bpmn",
                                        }),
                                      });
                                    }}
                                  >
                                    BPMN
                                  </Button>
                                </FlexItem>
                                <FlexItem>
                                  <Button
                                    isLarge
                                    variant={ButtonVariant.secondary}
                                    onClick={() => {
                                      history.push({
                                        pathname: globals.routes.newModel.path({
                                          extension: "dmn",
                                        }),
                                      });
                                    }}
                                  >
                                    DMN
                                  </Button>
                                </FlexItem>
                                <FlexItem>
                                  <Button
                                    isLarge
                                    variant={ButtonVariant.secondary}
                                    onClick={() => {
                                      history.push({
                                        pathname: globals.routes.newModel.path({
                                          extension: "pmml",
                                        }),
                                      });
                                    }}
                                  >
                                    PMML
                                  </Button>
                                </FlexItem>
                              </Flex>
                            </TextContent>
                          </EmptyStateSecondaryActions>
                        </EmptyState>
                      )}
                    </TextContent>
                  </>
                )}
              />
            </PageSection>
          </PageSection>
        </GalleryItem>
      </Gallery>
      <div className={"kogito-tooling--build-info"}>{process.env["WEBPACK_REPLACE__buildInfo"]}</div>
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

function WorkspaceCard(props: { workspaceId: string }) {
  const globals = useGlobals();
  const history = useHistory();
  const workspaces = useWorkspaces();
  const [isHovered, setHovered] = useState(false);
  const workspacePromise = useWorkspacePromise(props.workspaceId);

  const deleteWorkspaceIcon = useMemo(() => {
    return (
      isHovered && (
        <CardActions>
          <Button
            variant={ButtonVariant.plain}
            onClick={(e) => {
              e.stopPropagation();
              if (workspacePromise.data) {
                workspaces.workspaceService.delete(workspacePromise.data.descriptor, { broadcast: true });
              }
            }}
          >
            <TrashIcon />
          </Button>
        </CardActions>
      )
    );
  }, [isHovered, workspacePromise, workspaces.workspaceService]);

  const editableFiles = useMemo(
    () => workspacePromise.data?.files.filter((file) => SUPPORTED_FILES_EDITABLE.includes(file.extension)) ?? [],
    [workspacePromise]
  );

  const workspaceName = useMemo(
    () => (workspacePromise.data ? workspacePromise.data.descriptor.name : null),
    [workspacePromise.data]
  );

  const createdDate = useMemo(
    () => (workspacePromise.data ? new Date(workspacePromise.data.descriptor.createdDateISO).toLocaleString() : null),
    [workspacePromise.data]
  );

  const lastUpdatedDate = useMemo(
    () =>
      workspacePromise.data ? new Date(workspacePromise.data.descriptor.lastUpdatedDateISO).toLocaleString() : null,
    [workspacePromise.data]
  );

  return (
    <PromiseStateWrapper
      promise={workspacePromise}
      pending={<WorkspaceLoadingCard />}
      rejected={() => <>ERROR</>}
      resolved={(workspace) => (
        <>
          {editableFiles.length === 1 && (
            <Card
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
                  <CardTitle>
                    <TextContent>
                      <Text component={TextVariants.h3}>
                        <TaskIcon />
                        &nbsp;&nbsp;
                        {editableFiles[0].nameWithoutExtension}
                        &nbsp;&nbsp;
                        <FileLabel extension={editableFiles[0].extension} />
                      </Text>
                    </TextContent>
                  </CardTitle>
                </CardHeaderMain>
                {deleteWorkspaceIcon}
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
              onMouseOver={() => setHovered(true)}
              onMouseLeave={() => setHovered(false)}
              isHoverable={true}
              isCompact={true}
              style={{ cursor: "pointer" }}
              onClick={() => {
                history.push({
                  pathname: globals.routes.workspaceOverview.path({
                    workspaceId: props.workspaceId,
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
                {deleteWorkspaceIcon}
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
