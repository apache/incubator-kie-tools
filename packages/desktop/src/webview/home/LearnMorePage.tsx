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
import { useCallback } from "react";
import { ExternalLinkAltIcon } from "@patternfly/react-icons";
import * as electron from "electron";
import { useDesktopI18n } from "../common/i18n/locales";

export function LearnMorePage() {
  const { i18n } = useDesktopI18n();

  const externalLink = useCallback((event: React.MouseEvent<HTMLElement>, link: string) => {
    event.preventDefault();
    electron.shell.openExternal(link).catch(e => {
      console.error("Error while opening link: " + e);
    });
  }, []);

  return (
    <PageSection>
      <Grid sm={12} lg={6} hasGutter={true}>
        <Card>
          <CardHeader>
            <Title size={"lg"} headingLevel={"h2"}>
              {i18n.learnMorePage.bpmn.title}
            </Title>
          </CardHeader>
          <CardBody>
            <TextContent>
              <Text component={TextVariants.p}>{i18n.learnMorePage.bpmn.explanation}</Text>
              <Text component={TextVariants.p}>
                <Button
                  isInline={true}
                  type={"button"}
                  variant={"link"}
                  onClick={event => externalLink(event, "https://www.omg.org/bpmn/")}
                >
                  {i18n.learnMorePage.readMore} <ExternalLinkAltIcon className="pf-u-ml-xs" />
                </Button>
              </Text>
            </TextContent>
          </CardBody>
          <CardFooter component={"div"}>
            <Button variant="secondary" onClick={() => electron.ipcRenderer.send("createNewFile", { type: "bpmn" })}>
              {i18n.learnMorePage.bpmn.create}
            </Button>
          </CardFooter>
        </Card>
        <Card>
          <CardHeader>
            <Title size={"lg"} headingLevel={"h2"}>
              {i18n.learnMorePage.dmn.title}
            </Title>
          </CardHeader>
          <CardBody>
            <TextContent>
              <Text component={TextVariants.p}>{i18n.learnMorePage.dmn.explanation}</Text>
              <Text component={TextVariants.p}>
                <Button
                  isInline={true}
                  type={"button"}
                  variant={"link"}
                  onClick={event => externalLink(event, "https://www.omg.org/dmn/")}
                >
                  {i18n.learnMorePage.readMore}
                  <ExternalLinkAltIcon className="pf-u-ml-xs" />
                </Button>
              </Text>
              <Text component={TextVariants.p}>
                <Button
                  isInline={true}
                  type={"button"}
                  variant={"link"}
                  onClick={event => externalLink(event, "http://learn-dmn-in-15-minutes.com/")}
                >
                  {i18n.learnMorePage.dmn.learn} <ExternalLinkAltIcon className="pf-u-ml-xs" />
                </Button>
              </Text>
            </TextContent>
          </CardBody>
          <CardFooter component={"div"}>
            <Button variant="secondary" onClick={() => electron.ipcRenderer.send("createNewFile", { type: "dmn" })}>
              {i18n.learnMorePage.dmn.create}
            </Button>
          </CardFooter>
        </Card>
        <Card span={12} style={{ gridColumn: "span 12" }}>
          <CardHeader>
            <Title size={"lg"} headingLevel={"h2"}>
              {i18n.learnMorePage.about}
            </Title>
          </CardHeader>
          <CardBody>
            <Split hasGutter={true}>
              <SplitItem isFilled={true}>
                `${i18n.learnMorePage.editorsExplanation} `
                <Button
                  type={"button"}
                  variant={"link"}
                  isInline={true}
                  onClick={event => externalLink(event, "https://groups.google.com/forum/#!forum/kogito-development")}
                >
                  {i18n.terms.forum.toLowerCase()}
                </Button>
                .
              </SplitItem>
              <SplitItem>
                <Stack>
                  <StackItem>
                    <Button
                      type={"button"}
                      variant={"link"}
                      onClick={event => externalLink(event, "https://github.com/kiegroup/kogito-tooling/releases")}
                    >
                      {i18n.learnMorePage.getChromeExtension}
                      <ExternalLinkAltIcon className="pf-u-ml-xs" />
                    </Button>
                  </StackItem>
                  <StackItem>
                    <Button
                      type={"button"}
                      variant={"link"}
                      onClick={event => externalLink(event, "https://github.com/kiegroup/kogito-tooling/releases")}
                    >
                      {i18n.learnMorePage.getVsCodeExtension}
                      <ExternalLinkAltIcon className="pf-u-ml-xs" />
                    </Button>
                  </StackItem>
                  <StackItem>
                    <Button
                      type={"button"}
                      variant={"link"}
                      onClick={event => externalLink(event, "https://www.redhat.com/en/about/open-source")}
                    >
                      {i18n.learnMorePage.redHatOpenSource}
                      <ExternalLinkAltIcon className="pf-u-ml-xs" />
                    </Button>
                  </StackItem>
                  <StackItem>
                    <Button
                      type={"button"}
                      variant={"link"}
                      onClick={event => externalLink(event, "http://kogito.kie.org")}
                    >
                      {i18n.learnMorePage.kogitoWebsite}
                      <ExternalLinkAltIcon className="pf-u-ml-xs" />
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
