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
import { useContext } from "react";
import { GlobalContext } from "../common/GlobalContext";
import { ExternalLinkAltIcon } from "@patternfly/react-icons";
import * as electron from "electron";
import { Link } from "react-router-dom";

export function LearnMorePage() {
  const context = useContext(GlobalContext);

  const externalLink = (event: React.MouseEvent<HTMLElement>, link: string) => {
    console.log(typeof event);
    event.preventDefault();
    electron.shell.openExternal(link);
  };
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
              <Text component={TextVariants.p}>
                BPMN files are used to generate processes. Brief text explaining airedale the big cheese. Danish fontina
                cheesy grin airedale danish fontina taleggio the big cheese macaroni cheese port-salut. Edam fromage
                lancashire feta caerphilly everyone loves chalk and cheese brie. Red leicester parmesan cheese and
                biscuits cheesy feet blue castello cheesecake fromage frais smelly cheese.
              </Text>
              <Text component={TextVariants.p}>
                <Button
                  className={"''"}
                  component={"a"}
                  isBlock={false}
                  isInline={true}
                  type={"button"}
                  variant={"link"}
                  onClick={event => externalLink(event, "http://kogito.kie.org")}
                >
                  Read more <ExternalLinkAltIcon className="pf-u-ml-xs" />
                </Button>
              </Text>
            </TextContent>
          </CardBody>
          <CardFooter className={"''"} component={"div"}>
            <Button variant="secondary" onClick={() => context.fileActions.createNewFile("bpmn")}>
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
              <Text component={TextVariants.p}>
                DMN files are used to generate processes. Those options are already baked in with this model shoot me an
                email clear blue water but we need distributors to evangelize the new line to local markets, but fire up
                your browser. Strategic high-level 30,000 ft view. Drill down re-inventing the wheel at the end of the
                day but curate imagineer, or to be inspired is to become creative.
              </Text>
              <Text component={TextVariants.p}>
                <Button
                  className={"''"}
                  component={"a"}
                  isBlock={false}
                  isInline={true}
                  type={"button"}
                  variant={"link"}
                  onClick={event => externalLink(event, "http://kogito.kie.org")}
                >
                  Read more <ExternalLinkAltIcon className="pf-u-ml-xs" />
                </Button>
              </Text>
            </TextContent>
          </CardBody>
          <CardFooter className={"''"} component={"div"}>
            <Button variant="secondary" onClick={() => context.fileActions.createNewFile("dmn")}>
              Create DMN
            </Button>
          </CardFooter>
        </Card>
        <Card className={"''"} span={12} style={{ gridColumn: "span 12" }}>
          <CardHeader className={"''"}>
            <Title size={"lg"} className={"''"} headingLevel={"h2"}>
              About Business Modeler Preview and Kogito
            </Title>
          </CardHeader>
          <CardBody className={"''"}>
            <Split gutter="lg">
              <SplitItem isFilled={true}>
                Business Modeler Preview and Kogito are open source projects sponsored by Red Hat. Leverage agile
                frameworks to provide a robust synopsis for high level overviews. Iterative approaches to corporate
                strategy foster collaborative thinking to further the overall value proposition. Organically grow the
                holistic world view of disruptive innovation via workplace diversity and empowerment.
                <Button
                  className={"pf-u-ml-xs"}
                  component={"a"}
                  isBlock={false}
                  isInline={true}
                  type={"button"}
                  variant={"link"}
                  onClick={event => externalLink(event, "http://kogito.kie.org")}
                >
                  Documentation here.
                </Button>
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
                      onClick={event => externalLink(event, "http://kogito.kie.org")}
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
                      onClick={event => externalLink(event, "http://kogito.kie.org")}
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
                      onClick={event => externalLink(event, "http://kogito.kie.org")}
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
