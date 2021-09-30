import * as React from "react";
import { useState } from "react";
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
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { ArrowRightIcon } from "@patternfly/react-icons/dist/js/icons/arrow-right-icon";
import { WorkspaceOverview } from "../workspace/model/WorkspaceOverview";
import { useWorkspaces } from "../workspace/WorkspacesContext";
import { QueryParams } from "../common/Routes";
import { useQueryParams } from "../queryParams/QueryParamsContext";
import { OnlineEditorPage } from "./pageTemplate/OnlineEditorPage";
import { useWorkspaceOverviews } from "../workspace/hooks/WorkspacesHooks";

export function NewHomePage() {
  const globals = useGlobals();
  const history = useHistory();
  const workspaces = useWorkspaces();
  const queryParams = useQueryParams();
  const workspaceOverviews = useWorkspaceOverviews();
  const [url, setUrl] = useState("");

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
                                pathname: globals.routes.newWorkspaceWithEmptyFile.path({
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
                                pathname: globals.routes.newWorkspaceWithEmptyFile.path({
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
                                pathname: globals.routes.newWorkspaceWithEmptyFile.path({
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
                            <Text component={TextVariants.p}>
                              GitHub (github.com) and Gist (gist.github.com) URLs are supported.
                            </Text>
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
                                pathname: globals.routes.newWorkspaceWithUrl.path({}),
                                search: globals.routes.newWorkspaceWithUrl.queryString({ url: url }),
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
                            onChange={() => {
                              // TODO
                              // validate URL. if validated, change button to "primary"
                            }}
                            type="file"
                            /* @ts-expect-error directory and webkitdirectory are not available but works*/
                            webkitdirectory=""
                          />
                        </CardBody>
                        <CardFooter>
                          <Button
                            variant={ButtonVariant.secondary}
                            onClick={() => {
                              // TODO
                              // create workspace with uploaded files
                              // navigate to #/workspace/[name]/overview
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
                    <Text component={TextVariants.p}>
                      Start from sample models and create a Workspace later directly from it.
                    </Text>
                    <Flex>
                      <FlexItem>
                        <Button
                          isLarge
                          variant={ButtonVariant.tertiary}
                          onClick={() => {
                            history.push({
                              pathname: globals.routes.sketchWithUrl.path({ extension: "bpmn" }),
                              search: globals.routes.editor
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
                              pathname: globals.routes.sketchWithUrl.path({ extension: "dmn" }),
                              search: globals.routes.editor
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
                              pathname: globals.routes.sketchWithUrl.path({ extension: "pmml" }),
                              search: globals.routes.editor
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
              <StackItem>
                <PageSection variant={"light"}>
                  <TextContent>
                    <Text component={TextVariants.h1}>Sketch</Text>
                    <Text component={TextVariants.p}>
                      {`Quickly sketch a model without a Workspace. You can create a new Workspace directly from your Sketch if you'd like. Note that Sketches aren't saved.`}
                    </Text>
                    <Flex>
                      <FlexItem>
                        <Button
                          isLarge
                          variant={ButtonVariant.tertiary}
                          onClick={() => {
                            history.push({
                              pathname: globals.routes.sketchWithEmptyFile.path({
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
                          variant={ButtonVariant.tertiary}
                          onClick={() => {
                            history.push({
                              pathname: globals.routes.sketchWithEmptyFile.path({
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
                          variant={ButtonVariant.tertiary}
                          onClick={() => {
                            history.push({
                              pathname: globals.routes.sketchWithEmptyFile.path({
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
                </PageSection>
              </StackItem>
            </Stack>
          </PageSection>
        </GalleryItem>
        {/**/}
        <GalleryItem>
          <PageSection isFilled={true} style={{ height: "100%" }}>
            <PageSection variant={"light"}>
              <TextContent>
                <Text component={TextVariants.h1}>Workspaces</Text>
                <br />
                {workspaceOverviews.length > 0 && (
                  <Stack hasGutter={true}>
                    {workspaceOverviews.map((workspace: WorkspaceOverview) => (
                      <StackItem key={workspace.name}>
                        <WorkspaceCard workspace={workspace} />
                      </StackItem>
                    ))}
                  </Stack>
                )}
                {workspaceOverviews.length === 0 && (
                  <EmptyState>
                    <EmptyStateIcon icon={CubesIcon} />
                    <Title headingLevel="h4" size="lg">
                      {`You currently don't have any Workspaces.`}
                    </Title>
                    <EmptyStateBody>{`Use the cards on the left to create or import a new Workspace.`}</EmptyStateBody>
                  </EmptyState>
                )}
              </TextContent>
            </PageSection>
          </PageSection>
        </GalleryItem>
      </Gallery>
      <div className={"kogito-tooling--build-info"}>{process.env["WEBPACK_REPLACE__buildInfo"]}</div>
    </OnlineEditorPage>
  );
}

function WorkspaceCard(props: { workspace: WorkspaceOverview }) {
  const globals = useGlobals();
  const history = useHistory();
  const [isHovered, setHovered] = useState(false);
  return (
    <Card
      onMouseOver={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
      isHoverable={true}
      isCompact={true}
      style={{ cursor: "pointer" }}
      onClick={() => {
        history.push({
          pathname: globals.routes.workspaceOverview.path({
            workspaceId: props.workspace.workspaceId,
          }),
        });
      }}
    >
      <CardHeader>
        <CardHeaderMain>
          <CardTitle>{props.workspace.name}</CardTitle>
        </CardHeaderMain>
        {isHovered && (
          <CardActions>
            <Button variant={ButtonVariant.link}>
              Open <ArrowRightIcon />
            </Button>
          </CardActions>
        )}
      </CardHeader>

      <CardBody>
        <TextContent>
          <Text component={TextVariants.p}>
            {`Created at: ${props.workspace.createdIn.toLocaleString()}, Last updated at: ${props.workspace.lastUpdatedIn.toLocaleString()}`}
          </Text>
          <Text component={TextVariants.p}>
            {`${props.workspace.filesCount} files, ${props.workspace.modelsCount} models`}
          </Text>
        </TextContent>
      </CardBody>
    </Card>
  );
}
