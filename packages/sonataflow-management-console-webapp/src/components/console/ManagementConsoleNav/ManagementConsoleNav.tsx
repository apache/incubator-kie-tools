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
import * as React from "react";
import { Nav, NavItem, NavList } from "@patternfly/react-core/dist/js/components/Nav";
import { Link } from "react-router-dom";
import { ouiaAttribute } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { routes } from "../../../navigation/Routes";

interface IOwnProps {
  pathname: string;
}

const ManagementConsoleNav: React.FC<IOwnProps> = ({ pathname }) => {
  return (
    <Nav aria-label="Nav" theme="dark" ouiaId="navigation-list">
      <NavList>
        <NavItem
          key={"workflow-instances-nav"}
          isActive={pathname === "/WorkflowInstances"}
          ouiaId="workflow-instances"
        >
          <Link
            to={routes.runtimeToolsWorkflowInstances.path({})}
            {...ouiaAttribute("data-ouia-navigation-name", "workflow-instances")}
          >
            Workflow Instances
          </Link>
        </NavItem>
        <NavItem
          key={"workflow-definitions-nav"}
          isActive={pathname === "/WorkflowDefinitions"}
          ouiaId="workflow-definitions"
        >
          <Link
            to={routes.runtimeToolsWorkflowDefinitions.path({})}
            {...ouiaAttribute("data-ouia-navigation-name", "workflow-definitions")}
          >
            Workflow Definitions
          </Link>
        </NavItem>
        <NavItem
          key={"trigger-cloud-event-nav"}
          isActive={pathname === "/TriggerCloudEvent"}
          ouiaId="trigger-cloud-event"
        >
          <Link
            to={routes.runtimeToolsTriggerCloudEvent.path({})}
            {...ouiaAttribute("data-ouia-navigation-name", "trigger-cloud-event")}
          >
            Trigger Cloud Event
          </Link>
        </NavItem>
        <NavItem key={"monitoring-nav"} isActive={pathname === "/Monitoring"} ouiaId="monitoring">
          <Link to={routes.monitoring.path({})} {...ouiaAttribute("data-ouia-navigation-name", "monitoring")}>
            Monitoring
          </Link>
        </NavItem>
      </NavList>
    </Nav>
  );
};

export default ManagementConsoleNav;
