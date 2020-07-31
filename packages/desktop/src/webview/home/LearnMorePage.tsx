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
import { I18nHtml } from "@kogito-tooling/i18n";

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
              <I18nHtml>{i18n.learnMorePage.bpmn.title}</I18nHtml>
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
                  <I18nHtml>{i18n.learnMorePage.readMore}</I18nHtml> <ExternalLinkAltIcon className="pf-u-ml-xs" />
                </Button>
              </Text>
            </TextContent>
          </CardBody>
          <CardFooter component={"div"}>
            <Button variant="secondary" onClick={() => electron.ipcRenderer.send("createNewFile", { type: "bpmn" })}>
              <I18nHtml>{i18n.learnMorePage.bpmn.create}</I18nHtml>
            </Button>
          </CardFooter>
        </Card>
        <Card>
          <CardHeader>
            <Title size={"lg"} headingLevel={"h2"}>
              <I18nHtml>{i18n.learnMorePage.dmn.title}</I18nHtml>
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
                  <I18nHtml>{i18n.learnMorePage.readMore}</I18nHtml>
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
                  <I18nHtml>{i18n.learnMorePage.dmn.learn}</I18nHtml> <ExternalLinkAltIcon className="pf-u-ml-xs" />
                </Button>
              </Text>
            </TextContent>
          </CardBody>
          <CardFooter component={"div"}>
            <Button variant="secondary" onClick={() => electron.ipcRenderer.send("createNewFile", { type: "dmn" })}>
              <I18nHtml>{i18n.learnMorePage.dmn.create}</I18nHtml>
            </Button>
          </CardFooter>
        </Card>
        <Card span={12} style={{ gridColumn: "span 12" }}>
          <CardHeader>
            <Title size={"lg"} headingLevel={"h2"}>
              <I18nHtml>{i18n.learnMorePage.about}</I18nHtml>
            </Title>
          </CardHeader>
          <CardBody>
            <Split hasGutter={true}>
              <SplitItem isFilled={true}>
                <I18nHtml>`${i18n.learnMorePage.editorsExplanation} `</I18nHtml>
                <Button
                  type={"button"}
                  variant={"link"}
                  isInline={true}
                  onClick={event => externalLink(event, "https://groups.google.com/forum/#!forum/kogito-development")}
                >
                  <I18nHtml>{i18n.terms.forum.toLowerCase()}</I18nHtml>
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
                      <I18nHtml>{i18n.learnMorePage.getChromeExtension}</I18nHtml>
                      <ExternalLinkAltIcon className="pf-u-ml-xs" />
                    </Button>
                  </StackItem>
                  <StackItem>
                    <Button
                      type={"button"}
                      variant={"link"}
                      onClick={event => externalLink(event, "https://github.com/kiegroup/kogito-tooling/releases")}
                    >
                      <I18nHtml>{i18n.learnMorePage.getVsCodeExtension}</I18nHtml>
                      <ExternalLinkAltIcon className="pf-u-ml-xs" />
                    </Button>
                  </StackItem>
                  <StackItem>
                    <Button
                      type={"button"}
                      variant={"link"}
                      onClick={event => externalLink(event, "https://www.redhat.com/en/about/open-source")}
                    >
                      <I18nHtml>{i18n.learnMorePage.redHatOpenSource}</I18nHtml>
                      <ExternalLinkAltIcon className="pf-u-ml-xs" />
                    </Button>
                  </StackItem>
                  <StackItem>
                    <Button
                      type={"button"}
                      variant={"link"}
                      onClick={event => externalLink(event, "http://kogito.kie.org")}
                    >
                      <I18nHtml>{i18n.learnMorePage.kogitoWebsite}</I18nHtml>
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
