/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { List, ListItem } from "@patternfly/react-core/dist/js/components/List";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { ExternalLinkAltIcon } from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";
import * as React from "react";
import { routes } from "../routes";
import { BasePage } from "./BasePage";

export function HomePage() {
  return (
    <BasePage>
      <PageSection>
        <TextContent>
          <Text component={TextVariants.h3}>Welcome to your deployment</Text>
        </TextContent>
        <br />
        <TextContent>
          <Text component={TextVariants.h5}>Explore:</Text>
        </TextContent>
        <br />
        <List>
          <LinkListItem title="Swagger UI" href={routes.swaggerUi.path({})} />
          <LinkListItem title="Open API" href={routes.openApi.path({})} />
          <LinkListItem title="Metrics" href={routes.metrics.path({})} />
        </List>
      </PageSection>
    </BasePage>
  );
}

function LinkListItem(props: { title: string; href: string }) {
  return (
    <ListItem>
      <Text component={TextVariants.a} href={props.href} target="_blank">
        {props.title}&nbsp;&nbsp;
        <ExternalLinkAltIcon />
      </Text>
    </ListItem>
  );
}
