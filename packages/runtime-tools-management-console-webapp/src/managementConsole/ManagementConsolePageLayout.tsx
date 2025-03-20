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

import { Page, PageSection, PageSidebar, PageSidebarBody } from "@patternfly/react-core/dist/js/components/Page";
import { PageHeader, PageHeaderTools } from "@patternfly/react-core/deprecated";
import React, { useState, useMemo, useCallback, useEffect } from "react";
import { Brand } from "@patternfly/react-core/dist/js/components/Brand";
import { useEnv } from "../env/hooks/EnvContext";
import { useRoutes } from "../navigation/Hooks";
import { useHistory } from "react-router";
import { ManagementConsoleToolbar } from "./ManagementConsoleToolbar";
import { AboutButton } from "../aboutModal/AboutButton";
import { PageSectionHeader } from "@kie-tools/runtime-tools-components/dist/components/PageSectionHeader";
import { BreadcrumbPathType } from "../runtime/RuntimePageLayoutContext";

type Props = {
  children: React.ReactNode;
  disabledHeader?: boolean;
  currentPageTile?: string;
  breadcrumbText?: string[];
  breadcrumbPath?: BreadcrumbPathType;
  nav?: React.ReactNode;
};

export const ManagementConsolePageLayout: React.FC<Props> = ({
  children,
  disabledHeader = true,
  currentPageTile,
  breadcrumbText,
  breadcrumbPath,
  nav,
}) => {
  const { env } = useEnv();
  const routes = useRoutes();
  const history = useHistory();
  const [isNavOpen, setIsNavOpen] = useState(true);

  const onNavToggle = useCallback(() => {
    setIsNavOpen((currentValue) => !currentValue);
  }, []);

  const onClickBrand = useCallback(() => {
    history.push(routes.home.path({}));
  }, [history, routes.home]);

  const Header = useMemo(() => {
    return (
      <PageHeader
        logo={
          <>
            <Brand
              src={routes.static.images.appLogoReverse.path({})}
              alt={env.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_APP_NAME}
              onClick={onClickBrand}
            />
            <AboutButton />
          </>
        }
        headerTools={
          !disabledHeader && (
            <>
              <PageHeaderTools>
                <ManagementConsoleToolbar />
              </PageHeaderTools>
            </>
          )
        }
        showNavToggle
        isNavOpen={isNavOpen}
        onNavToggle={onNavToggle}
      />
    );
  }, [
    routes.static.images.appLogoReverse,
    env.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_APP_NAME,
    onClickBrand,
    disabledHeader,
    isNavOpen,
    onNavToggle,
  ]);

  const Sidebar = useMemo(
    () =>
      nav && (
        <PageSidebar isSidebarOpen={isNavOpen} theme="dark" data-testid="page-sidebar">
          <PageSidebarBody>{nav}</PageSidebarBody>
        </PageSidebar>
      ),
    [isNavOpen, nav]
  );

  return (
    <React.Fragment>
      <Page header={Header} sidebar={Sidebar} className="kogito-consoles-common--PageLayout">
        {!disabledHeader && (
          <PageSectionHeader
            titleText={currentPageTile ?? ""}
            breadcrumbText={breadcrumbText}
            breadcrumbPath={breadcrumbPath}
          />
        )}
        <PageSection>{children}</PageSection>
      </Page>
    </React.Fragment>
  );
};
