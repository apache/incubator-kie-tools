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

import React from "react";
import { Nav, NavItem, NavList } from "@patternfly/react-core/dist/js/components/Nav";
import { Link, useLocation } from "react-router-dom";
import { ouiaAttribute } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { useDevUIAppContext } from "../../contexts/DevUIAppContext";

const DevUINav: React.FC = () => {
  const location = useLocation();
  const { isWorkflowEnabled: isWorkflowEnabled, availablePages } = useDevUIAppContext();

  return (
    <Nav aria-label="Nav" theme="dark">
      <NavList>
        {isWorkflowEnabled && (
          <>
            {(!availablePages || availablePages.includes("Workflows")) && (
              <NavItem key={"workflows-nav"} isActive={location.pathname === "/Workflows"}>
                <Link to="/Workflows" {...ouiaAttribute("data-ouia-navigation-name", "workflows-nav")}>
                  Workflows
                </Link>
              </NavItem>
            )}
            {(!availablePages || availablePages.includes("Forms")) && (
              <NavItem key={"forms-list-nav"} isActive={location.pathname === "/Forms"}>
                <Link to="/Forms" {...ouiaAttribute("data-ouia-navigation-name", "forms-list-nav")}>
                  Forms
                </Link>
              </NavItem>
            )}
            {(!availablePages || availablePages.includes("Monitoring")) && (
              <NavItem key={"monitoring-nav"} isActive={location.pathname.startsWith("/Monitoring")}>
                <Link to="/Monitoring" {...ouiaAttribute("data-ouia-navigation-name", "monitoring-nav")}>
                  Monitoring
                </Link>
              </NavItem>
            )}
            {(!availablePages || availablePages.includes("CustomDashboard")) && (
              <NavItem key={"customDashboard-nav"} isActive={location.pathname.startsWith("/CustomDashboard")}>
                <Link to="/CustomDashboard" {...ouiaAttribute("data-ouia-navigation-name", "customDashboard-nav")}>
                  Dashboards
                </Link>
              </NavItem>
            )}
          </>
        )}
      </NavList>
    </Nav>
  );
};

export default DevUINav;
