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

import { Breadcrumb, BreadcrumbItem } from "@patternfly/react-core/components/Breadcrumb";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import * as React from "react";
import { useEffect } from "react";
import { setPageTitle } from "../PageTitle";
import { routes } from "../navigation/Routes";
import { SETTINGS_PAGE_SECTION_TITLE } from "./SettingsContext";

export type SettingsPageContainerProps = {
  pageTitle: string;
  subtitle?: string | React.ReactNode;
  children?: React.ReactNode;
};

export function SettingsPageContainer(props: SettingsPageContainerProps) {
  const { pageTitle, subtitle, children } = props;

  useEffect(() => {
    setPageTitle([SETTINGS_PAGE_SECTION_TITLE, pageTitle]);
  }, [pageTitle]);

  return (
    <Page
      breadcrumb={
        <Breadcrumb>
          <BreadcrumbItem to={"#" + routes.home.path({})}>Home</BreadcrumbItem>
          <BreadcrumbItem to={"#" + routes.settings.home.path({})}>Settings</BreadcrumbItem>
          <BreadcrumbItem isActive>{pageTitle}</BreadcrumbItem>
        </Breadcrumb>
      }
    >
      <PageSection variant={"light"}>
        <TextContent>
          <Text component={TextVariants.h1}>{pageTitle}</Text>
          {subtitle && <Text component={TextVariants.p}>{subtitle}</Text>}
        </TextContent>
      </PageSection>

      {children}
    </Page>
  );
}
