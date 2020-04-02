/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import {
  PageSection,
  Grid,
  Card,
  Title,
  TextContent,
  CardFooter,
  CardHeader,
  CardBody,
  Stack,
  SplitItem,
  Split,
  StackItem,
  Text,
  TextVariants
} from "@patternfly/react-core";
import { Button } from "@patternfly/react-core/dist/js/components/Button/Button";
import { useCallback, useContext } from "react";
import { GlobalContext } from "../common/GlobalContext";
import { ExternalLinkAltIcon } from "@patternfly/react-icons";
import * as electron from "electron";

export function LearnMorePage() {
  const context = useContext(GlobalContext);

  const externalLink = useCallback((event: React.MouseEvent<HTMLElement>, link: string) => {
    event.preventDefault();
    electron.shell.openExternal(link).catch(e => {
      console.error("Error while opening link: " + e);
    });
  }, []);

  return (
    <PageSection>
      <Grid sm={12} lg={6} gutter="lg">
        <Card className={"''"}>
          <CardHeader className={"''"}>
            <Title size={"lg"} className={"''"} headingLevel={"h2"}>
              Why BPMN?
            </Title>
          </CardHeader>
          <CardBody className={"''"}>
            <TextContent>
              <Text component={TextVariants.p}>BPMN files are used to generate business processes.</Text>
              <Text component={TextVariants.p}>
                <Button
                  className={"''"}
                  component={"a"}
                  isBlock={false}
                  isInline={true}
                  type={"button"}
                  variant={"link"}
                  onClick={event => externalLink(event, "https://www.omg.org/bpmn/")}
                >
                  Read more <ExternalLinkAltIcon className="pf-u-ml-xs" />
                </Button>
              </Text>
            </TextContent>
          </CardBody>
          <CardFooter className={"''"} component={"div"}>
            <Button variant="secondary" onClick={() => electron.ipcRenderer.send("createNewFile", { type: "bpmn" })}>
              Create BPMN
            </Button>
          </CardFooter>
        </Card>
        <Card className={"''"}>
          <CardHeader className={"''"}>
            <Title size={"lg"} className={"''"} headingLevel={"h2"}>
              Why DMN?
            </Title>
          </CardHeader>
          <CardBody className={"''"}>
            <TextContent>
              <Text component={TextVariants.p}>DMN files are used to generate decision models.</Text>
              <Text component={TextVariants.p}>
                <Button
                  className={"''"}
                  component={"a"}
                  isBlock={false}
                  isInline={true}
                  type={"button"}
                  variant={"link"}
                  onClick={event => externalLink(event, "https://www.omg.org/dmn/")}
                >
                  Read more <ExternalLinkAltIcon className="pf-u-ml-xs" />
                </Button>
              </Text>
              <Text component={TextVariants.p}>
                <Button
                  className={"''"}
                  component={"a"}
                  isBlock={false}
                  isInline={true}
                  type={"button"}
                  variant={"link"}
                  onClick={event => externalLink(event, "http://learn-dmn-in-15-minutes.com/")}
                >
                  Learn DMN in 15 minutes <ExternalLinkAltIcon className="pf-u-ml-xs" />
                </Button>
              </Text>
            </TextContent>
          </CardBody>
          <CardFooter className={"''"} component={"div"}>
            <Button variant="secondary" onClick={() => electron.ipcRenderer.send("createNewFile", { type: "dmn" })}>
              Create DMN
            </Button>
          </CardFooter>
        </Card>
        <Card className={"''"} span={12} style={{ gridColumn: "span 12" }}>
          <CardHeader className={"''"}>
            <Title size={"lg"} className={"''"} headingLevel={"h2"}>
              About Business Modeler Preview
            </Title>
          </CardHeader>
          <CardBody className={"''"}>
            <Split gutter="lg">
              <SplitItem isFilled={true}>
                These simple BPMN and DMN editors are here to allow you to collaborate quickly and to help introduce you
                to the new tools and capabilities of Process Automation. Feel free to get in touch in the
                <Button
                  className={"pf-u-ml-xs"}
                  component={"a"}
                  isBlock={false}
                  isInline={true}
                  type={"button"}
                  variant={"link"}
                  onClick={event => externalLink(event, "https://groups.google.com/forum/#!forum/kogito-development")}
                >
                  forum
                </Button>
                .
              </SplitItem>
              <SplitItem>
                <Stack>
                  <StackItem>
                    <Button
                      className={"''"}
                      component={"a"}
                      isBlock={false}
                      isInline={false}
                      type={"button"}
                      variant={"link"}
                      onClick={event => externalLink(event, "https://github.com/kiegroup/kogito-tooling/releases")}
                    >
                      Get GitHub Chrome extension <ExternalLinkAltIcon className="pf-u-ml-xs" />
                    </Button>
                  </StackItem>
                  <StackItem>
                    <Button
                      className={"''"}
                      component={"a"}
                      isBlock={false}
                      isInline={false}
                      type={"button"}
                      variant={"link"}
                      onClick={event => externalLink(event, "https://github.com/kiegroup/kogito-tooling/releases")}
                    >
                      Get VSCode extension <ExternalLinkAltIcon className="pf-u-ml-xs" />
                    </Button>
                  </StackItem>
                  <StackItem>
                    <Button
                      className={"''"}
                      component={"a"}
                      isBlock={false}
                      isInline={false}
                      type={"button"}
                      variant={"link"}
                      onClick={event => externalLink(event, "https://www.redhat.com/en/about/open-source")}
                    >
                      Red Hat and open source <ExternalLinkAltIcon className="pf-u-ml-xs" />
                    </Button>
                  </StackItem>
                  <StackItem>
                    <Button
                      className={"''"}
                      component={"a"}
                      isBlock={false}
                      isInline={false}
                      type={"button"}
                      variant={"link"}
                      onClick={event => externalLink(event, "http://kogito.kie.org")}
                    >
                      Kogito website <ExternalLinkAltIcon className="pf-u-ml-xs" />
                    </Button>
                  </StackItem>
                </Stack>
              </SplitItem>
            </Split>
          </CardBody>
        </Card>
      </Grid>
    </PageSection>
  );
}
