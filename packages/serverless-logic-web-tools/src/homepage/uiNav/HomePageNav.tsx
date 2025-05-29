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

import * as React from "react";
import { Nav, NavExpandable, NavItem, NavList } from "@patternfly/react-core/dist/js/components/Nav";
import { Link, matchPath } from "react-router-dom";
import { ExternalLinkAltIcon } from "@patternfly/react-icons/dist/js/icons";
import { routes } from "../../navigation/Routes";
import { SERVERLESS_LOGIC_WEBTOOLS_DOCUMENTATION_URL } from "../../AppConstants";
import { useSettings } from "../../settings/SettingsContext";
import { useMemo } from "react";

export function HomePageNav(props: { pathname: string }) {
  const settings = useSettings();

  const runtimeToolsNavItem = useMemo(() => {
    if (settings.runtimeTools.config.dataIndexUrl) {
      return (
        <NavExpandable title="Runtime Tools" groupId="runtime-tools-nav-group">
          <NavItem
            itemId={3}
            key={"RuntimeToolsWorkflowDefinitions-nav"}
            isActive={props.pathname === routes.runtimeToolsWorkflowDefinitions.path({})}
            ouiaId="runtime-tools-workflow-definitions-nav"
          >
            <Link to={routes.runtimeToolsWorkflowDefinitions.path({})}>Workflow Definitions</Link>
          </NavItem>
          <NavItem
            itemId={4}
            key={"RuntimeToolsWorkflowInstances-nav"}
            isActive={props.pathname === routes.runtimeToolsWorkflowInstances.path({})}
            ouiaId="runtime-tools-workflow-instances-nav"
          >
            <Link to={routes.runtimeToolsWorkflowInstances.path({})}>Workflow Instances</Link>
          </NavItem>
        </NavExpandable>
      );
    }

    return (
      <NavItem
        itemId={3}
        key={"RuntimeTools-nav"}
        isActive={props.pathname === routes.settings.runtime_tools.path({})}
        ouiaId="runtime-tools-nav"
      >
        <Link to={routes.settings.runtime_tools.path({})}>Runtime Tools</Link>
      </NavItem>
    );
  }, [settings, props.pathname]);

  return (
    <>
      <Nav aria-label="Global NAV" theme="dark">
        <NavList>
          <NavItem
            itemId={0}
            key={"Overview-nav"}
            isActive={props.pathname === routes.home.path({})}
            ouiaId="overview-nav"
          >
            <Link to={routes.home.path({})}>Overview</Link>
          </NavItem>

          <NavItem
            itemId={1}
            key={"Recent-models-nav"}
            isActive={
              props.pathname === routes.recentModels.path({}) ||
              matchPath({ path: routes.workspaceWithFiles.path({ workspaceId: ":workspaceId" }) }, props.pathname) !==
                null
            }
            ouiaId="recent-models-nav"
          >
            <Link to={routes.recentModels.path({})}>Recent Models</Link>
          </NavItem>

          <NavItem
            itemId={2}
            key={"SampleCatalog-nav"}
            isActive={props.pathname === routes.sampleCatalog.path({})}
            ouiaId="samples-catalog-nav"
          >
            <Link to={routes.sampleCatalog.path({})}>Sample Catalog</Link>
          </NavItem>

          {runtimeToolsNavItem}

          <NavItem itemId={6} key={"Documentation-nav"} className="chr-c-navigation__additional-links">
            <a href={SERVERLESS_LOGIC_WEBTOOLS_DOCUMENTATION_URL} target="_blank" rel="noopener noreferrer">
              Documentation
              <ExternalLinkAltIcon />
            </a>
          </NavItem>
        </NavList>
      </Nav>
    </>
  );
}
