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
import { DownloadIcon, ExternalLinkAltIcon } from "@patternfly/react-icons/dist/js/icons";
import { Link, useLocation, matchPath } from "react-router-dom";
import { SONATAFLOW_DEPLOYMENT_DOCUMENTATION_URL } from "../../AppConstants";
import { routes } from "../../routes";

export function BasePageNav() {
  const location = useLocation();

  return (
    <Nav aria-label="Global NAV" theme="dark">
      <NavList>
        <NavItem
          itemId={0}
          key={"Workflows-nav"}
          isActive={
            location.pathname === routes.workflows.home.path({}) ||
            matchPath({ path: routes.workflows.form.path({ workflowId: ":workflowId" }) }, location.pathname) !==
              null ||
            location.pathname === routes.workflows.cloudEvent.path({})
          }
          ouiaId="workflows-nav"
        >
          <Link to={routes.home.path({})}>Workflows</Link>
        </NavItem>

        <NavItem
          itemId={0}
          key={"RuntimeToolsWorkflowInstances-nav"}
          isActive={
            location.pathname === routes.runtimeTools.workflowInstances.path({}) ||
            matchPath(
              {
                path: routes.runtimeTools.workflowDetails.path({ workflowId: ":workflowId" }),
              },
              location.pathname
            ) !== null
          }
          ouiaId="runtime-tools-workflow-instances-nav"
        >
          <Link to={routes.runtimeTools.workflowInstances.path({})}>Workflow Instances</Link>
        </NavItem>

        <NavItem
          itemId={3}
          key={"OpenApi-nav"}
          onClick={() => handleDownloadClick(routes.openApiJson.path({}), "openapi.json")}
        >
          Open API &nbsp;
          <DownloadIcon />
        </NavItem>

        <NavItem itemId={3} key={"Documentation-nav"}>
          <a href={SONATAFLOW_DEPLOYMENT_DOCUMENTATION_URL} target="_blank" rel="noopener noreferrer">
            Documentation &nbsp;
            <ExternalLinkAltIcon />
          </a>
        </NavItem>
      </NavList>
    </Nav>
  );
}

async function handleDownloadClick(url: string, filename: string) {
  const response = await fetch(url);
  const blobUrl = URL.createObjectURL(await response.blob());

  const a = document.createElement("a");
  a.style.display = "none";
  a.href = blobUrl;
  a.download = filename;
  document.body.appendChild(a);
  a.click();

  window.URL.revokeObjectURL(blobUrl);
}
