/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import React from "react";
import { Nav, NavItem, NavList } from "@patternfly/react-core/dist/js/components/Nav";
import { useMemo } from "react";
import { Link } from "react-router-dom";
import { useRoutes } from "../../navigation/Hooks";
import { isBrowserChromiumBased } from "../../workspace/startupBlockers/SupportedBrowsers";

export function SettingsPageNav(props: { pathname: string }) {
  const routes = useRoutes();

  const isChromiumBased = useMemo(isBrowserChromiumBased, []);

  return (
    <>
      <div className="chr-c-app-title">Settings</div>
      <Nav aria-label="Global NAV" theme="dark">
        <NavList>
          <NavItem itemId={0} key={`Settings-github-nav`} isActive={props.pathname === routes.settings.github.path({})}>
            <Link to={routes.settings.github.path({})}>GitHub</Link>
          </NavItem>
          <NavItem
            itemId={0}
            key={`Settings-extended_services-nav`}
            isActive={props.pathname === routes.settings.extended_services.path({})}
          >
            <Link to={routes.settings.extended_services.path({})}>Extended Services</Link>
          </NavItem>
          <NavItem
            itemId={0}
            key={`Settings-openshift-nav`}
            isActive={props.pathname === routes.settings.openshift.path({})}
          >
            <Link to={routes.settings.openshift.path({})}>OpenShift</Link>
          </NavItem>
          <NavItem
            itemId={0}
            key={`Settings-service_account-nav`}
            isActive={props.pathname === routes.settings.service_account.path({})}
          >
            <Link to={routes.settings.service_account.path({})}>Service Account</Link>
          </NavItem>
          <NavItem
            itemId={0}
            key={`Settings-service_registry-nav`}
            isActive={props.pathname === routes.settings.service_registry.path({})}
          >
            <Link to={routes.settings.service_registry.path({})}>Service Registry</Link>
          </NavItem>
          <NavItem
            itemId={0}
            key={`Settings-feature_preview-nav`}
            isActive={props.pathname === routes.settings.feature_preview.path({})}
          >
            <Link to={routes.settings.feature_preview.path({})}>Feature Preview</Link>
          </NavItem>
          {isChromiumBased && (
            <NavItem
              itemId={0}
              key={`Settings-storage-nav`}
              isActive={props.pathname === routes.settings.storage.path({})}
            >
              <Link to={routes.settings.storage.path({})}>Storage</Link>
            </NavItem>
          )}
        </NavList>
      </Nav>
    </>
  );
}
