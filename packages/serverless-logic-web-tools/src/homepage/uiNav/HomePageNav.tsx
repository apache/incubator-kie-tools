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

import * as React from "react";
import { Nav, NavItem, NavList } from "@patternfly/react-core";
import { Link } from "react-router-dom";

export function HomePageNav(props: { pathname: string }) {
  return (
    <Nav aria-label="Global NAV" theme="dark">
      <NavList>
        <NavItem itemId={0} key={"Overview-nav"} isActive={props.pathname === "/"}>
          <Link to="/">Overview</Link>
        </NavItem>

        <NavItem itemId={1} key={"Serverless-models-nav"} isActive={props.pathname === "/ServerlessModels"}>
          <Link to="/ServerlessModels">Serverless Models</Link>
        </NavItem>

        <NavItem itemId={2} key={"SampleCatalog-nav"} isActive={props.pathname === "/SampleCatalog"}>
          <Link to="/SampleCatalog">Sample Catalog</Link>
        </NavItem>

        <NavItem itemId={3} key={"Documentation-nav"} isActive={props.pathname === "/Documentation"}>
          <Link to="/Documentation">Documentation</Link>
        </NavItem>
      </NavList>
    </Nav>
  );
}
