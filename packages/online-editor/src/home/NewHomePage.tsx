import * as React from "react";
import { useState } from "react";
import { Page, PageHeader, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Brand } from "@patternfly/react-core/dist/js/components/Brand";
import { useGlobals } from "../common/GlobalContext";
import { useOnlineI18n } from "../common/i18n";
import { useHistory } from "react-router";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import {
  Card,
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

export function NewHomePage() {
  const globals = useGlobals();
  const { i18n } = useOnlineI18n();
  const history = useHistory();

  // TODO
  // get workspaces from `useWorkspaces` hook and delete this state.
  const [workspaces, setWorkspaces] = useState<
    Array<{ name: string; createdIn: Date; lastUpdatedIn: Date; filesCount: number; modelsCount: number }>
  >([
    { name: "Workspace 1", createdIn: new Date(), lastUpdatedIn: new Date(), filesCount: 12, modelsCount: 3 },
    { name: "Workspace 2", createdIn: new Date(), lastUpdatedIn: new Date(), filesCount: 124, modelsCount: 45 },
  ]);

  return (
    <Page
      className="kogito--editor-landing"
      header={
        <PageHeader
          logo={<Brand src={globals.routes.static.images.homeLogo.path({})} alt="Logo" />}
          logoProps={{ onClick: () => history.push({ pathname: globals.routes.home.path({}) }) }}
        />
      }
    >
      <PageSection variant="dark" className="kogito--editor-landing__title-section">
        <TextContent>
          <Title size="3xl" headingLevel="h1">
            {i18n.homePage.header.title}
          </Title>
          <Text>{i18n.homePage.header.welcomeText}</Text>
          <Text component={TextVariants.small} className="pf-u-text-align-right">
            {`${i18n.terms.poweredBy} `}
            <Brand
              src={globals.routes.static.images.kogitoLogoWhite.path({})}
              alt="Kogito Logo"
              style={{ height: "1em", verticalAlign: "text-bottom" }}
            />
          </Text>
        </TextContent>
      </PageSection>
      <Gallery maxWidths={{ default: "100%", lg: "50%", md: "100%" }}>
        <GalleryItem>
          <PageSection isFilled={true} style={{ height: "100%" }}>
            <Stack hasGutter={true}>
              <StackItem>
                <PageSection variant={"light"} style={{ boxShadow: "0px 0px 6px 0px #b1b1b1" }}>
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
                              // TODO
                              // create workspace with empty BPMN file
                              // navigate to #/workspaces/[name]/file/new-file.bpmn
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
                              // TODO
                              // create workspace with empty DMN file
                              // navigate to #/workspaces/[name]/file/new-file.dmn
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
                              // TODO
                              // create workspace with empty PMML file
                              // navigate to #/workspaces/[name]/file/new-file.pmml
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
                              // TODO
                              // create workspace without files
                              // navigate to #/workspaces/[name]/overview
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
                <PageSection variant={"light"} style={{ boxShadow: "0px 0px 6px 0px #b1b1b1" }}>
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
                            onChange={() => {
                              // TODO
                              // validate URL. if validated, change button to "primary"
                            }}
                          />
                        </CardBody>
                        <CardFooter>
                          <Button
                            variant={ButtonVariant.secondary}
                            onClick={() => {
                              // TODO
                              // enable `Enter` key to submit.
                              // navigate to #/workspace/new?url=[url]
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
                            // TODO
                            // navigate to #/sketch?file=sample/sample.bpmn
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
                            // TODO
                            // navigate to #/sketch?file=sample/sample.dmn
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
                            // TODO
                            // navigate to #/sketch?file=sample/sample.pmml
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
                            // TODO
                            // navigate to #/sketch/bpmn
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
                            // TODO
                            // navigate to #/sketch/dmn
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
                            // TODO
                            // navigate to #/sketch/pmml
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
                {workspaces.length > 0 && (
                  <Stack hasGutter={true}>
                    {workspaces.map((workspace) => (
                      <StackItem key={workspace.name}>
                        <Card
                          isHoverable={true}
                          isCompact={true}
                          style={{ cursor: "pointer" }}
                          onClick={() => {
                            // TODO
                            // navigate to #/workspace/${workspace.name}/overview
                          }}
                        >
                          <CardHeader>
                            <CardHeaderMain>
                              <CardTitle>{workspace.name}</CardTitle>
                            </CardHeaderMain>
                          </CardHeader>

                          <CardBody>
                            <TextContent>
                              <Text component={TextVariants.p}>
                                {`Created at: ${workspace.createdIn.toLocaleString()}, Last updated at: ${workspace.lastUpdatedIn.toLocaleString()}`}
                              </Text>
                              <Text component={TextVariants.p}>
                                {`${workspace.filesCount} files, ${workspace.modelsCount} models`}
                              </Text>
                            </TextContent>
                          </CardBody>
                        </Card>
                      </StackItem>
                    ))}
                  </Stack>
                )}
                {workspaces.length === 0 && (
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
    </Page>
  );
}
