/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import { useDevUIAppContext } from "../../contexts/DevUIAppContext";
import { ouiaAttribute } from "@kie-tools/runtime-tools-components/dist/ouiaTools";

const DevUINav: React.FC = () => {
  const location = useLocation();
  const { isProcessEnabled, customLabels, availablePages } = useDevUIAppContext();

  return (
    <Nav aria-label="Nav" theme="dark">
      <NavList>
        {isProcessEnabled && (
          <>
            {(!availablePages || availablePages.includes("Processes")) && (
              <NavItem key={"processes-nav"} isActive={location.pathname === "/Processes"}>
                <Link to="/Processes" {...ouiaAttribute("data-ouia-navigation-name", "processes-nav")}>
                  {customLabels.pluralProcessLabel}
                </Link>
              </NavItem>
            )}
            {(!availablePages || availablePages.includes("Jobs")) && (
              <NavItem key={"jobs-management-nav"} isActive={location.pathname === "/Jobs"}>
                <Link to="/Jobs" {...ouiaAttribute("data-ouia-navigation-name", "jobs-management-nav")}>
                  Jobs
                </Link>
              </NavItem>
            )}
            {(!availablePages || availablePages.includes("Tasks")) && (
              <NavItem key={"tasks-nav"} isActive={location.pathname === "/Tasks"}>
                <Link to="/Tasks" {...ouiaAttribute("data-ouia-navigation-name", "tasks-nav")}>
                  Tasks
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
          </>
        )}
      </NavList>
    </Nav>
  );
};

export default DevUINav;
