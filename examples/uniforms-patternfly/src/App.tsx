/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import "@patternfly/react-core/dist/styles/base.css";
import * as React from "react";
import { useEffect, useState } from "react";
import { Brand, Nav, NavItem, NavList, Page, PageHeader } from "@patternfly/react-core";
import { HashRouter as Router, Link, Route, Switch } from "react-router-dom";
import { Home } from "./Home";
import "../static/resources/styles.css";

enum Location {
  HOME = "/",
}

export function App() {
  /**
   * State which determines what is the current route.
   */
  const [location, setLocation] = useState(Location.HOME);

  /**
   * On the first render, the location state is determined by the current URL.
   */
  useEffect(() => {
    setLocation(window.location.hash.slice(1) as Location); //Remove trailing '#' from route to match the Location enum.
  });

  return (
    <Router>
      <Page
        header={
          <PageHeader
            logo={<Brand src={"logo.png"} alt="Logo" />}
            topNav={
              <Nav onSelect={(e) => setLocation(e.itemId as Location)} aria-label="Nav" variant="horizontal">
                <NavList>
                  <NavItem itemId={Location.HOME} isActive={location === Location.HOME}>
                    <Link to={Location.HOME}>Home</Link>
                  </NavItem>
                </NavList>
              </Nav>
            }
          />
        }
      >
        <Switch>
          <Route exact={true} path={"/"}>
            <Home />
          </Route>
        </Switch>
      </Page>
    </Router>
  );
}
