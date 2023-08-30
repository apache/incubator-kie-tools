/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Button, ButtonVariant, Card } from "@patternfly/react-core/dist/js";
import { List, ListItem } from "@patternfly/react-core/dist/js/components/List";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { Stack, StackItem } from "@patternfly/react-core/dist/js/layouts/Stack";
import { ExternalLinkAltIcon } from "@patternfly/react-icons/dist/js/icons";
import * as React from "react";
import { useEffect } from "react";
import { SERVERLESS_LOGIC_WEBTOOLS_DOCUMENTATION_URL } from "../../AppConstants";
import { setPageTitle } from "../../PageTitle";
import { CreateOrImportModelGrid } from "./CreateOrImportModelGrid";

const PAGE_TITLE = "Overview";

export function Overview(props: { isNavOpen: boolean }) {
  useEffect(() => {
    setPageTitle([PAGE_TITLE]);
  }, []);

  return (
    <>
      <PageSection
        className="appsrv-marketing--banner"
        style={
          {
            "--appsrv-marketing--banner--before--BackgroundImage": "url(images/overview-banner-bg.png)",
            "--appsrv-marketing--banner--before--BackgroundPositionY": "-99px",
            "--appsrv-marketing--banner--before--BackgroundRepeat": "no-repeat",
            "--appsrv-marketing--banner--before--BackgroundSize": "478px",
          } as React.CSSProperties
        }
        variant={"light"}
      >
        <Stack hasGutter>
          <Title headingLevel="h1" size="2xl">
            Welcome to Serverless Logic Web Tools
          </Title>
          <StackItem>
            <Text className="appsrv-marketing--banner__tagline pf-u-color-200">
              Add-on service to create and synchronize your Serverless Workflow, Decision files, and Dashbuilder files
            </Text>
            <Text component={TextVariants.p}>
              The Serverless Logic Web Tools is a web application that enables you to create and synchronize your
              Serverless Workflow, Serverless Decision, and Dashbuilder files in a single interface. Also, the
              Serverless Logic Web Tools application provides the integrations that are needed to deploy and test the
              Serverless Workflow models in development mode.
            </Text>
          </StackItem>
          <StackItem>
            <Button
              target="_blank"
              iconPosition="right"
              icon={<ExternalLinkAltIcon />}
              href={SERVERLESS_LOGIC_WEBTOOLS_DOCUMENTATION_URL}
              variant={ButtonVariant.secondary}
              component="a"
            >
              Get Started with Serverless Logic Web Tools
            </Button>
          </StackItem>
        </Stack>
      </PageSection>

      <PageSection className="appsrv-marketing--page-section--marketing">
        <CreateOrImportModelGrid isNavOpen={props.isNavOpen} />
      </PageSection>

      <PageSection isWidthLimited className="appsrv-marketing--page-section--marketing" variant="light">
        <Title className="pf-u-mb-lg" size="xl" headingLevel="h3">
          Use Serverless Logic Web Tools
        </Title>
        <Grid hasGutter>
          <GridItem md={7}>
            <Card className="appsrv-marketing--video">
              <iframe
                width="560"
                height="315"
                src="https://www.youtube.com/embed/W1mjNTfDQxA"
                title="YouTube video player"
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                allowFullScreen
              ></iframe>
            </Card>
          </GridItem>
          <GridItem md={5}>
            <TextContent>
              <Text className="pf-u-color-200 pf-u-ml-md">In this video, you will learn how to:</Text>
              <List className="app-services-ui--icon-list">
                <ListItem>Create a Serverless Workflow, a Dashboard or a Serverless Decision.</ListItem>
                <ListItem>Utilize Code Completions to complete the code.</ListItem>
                <ListItem>Validate the code and utilize the Validation Panel to correct the errors.</ListItem>
                <ListItem>
                  Navigate through the Recent Models section and reopen the model we previously created.
                </ListItem>
                <ListItem>Browse the Samples Catalog and create a new model based on a sample.</ListItem>
                <ListItem>Utilize the diagram and the editor to select the state nodes.</ListItem>
              </List>
            </TextContent>
          </GridItem>
        </Grid>
      </PageSection>
    </>
  );
}
