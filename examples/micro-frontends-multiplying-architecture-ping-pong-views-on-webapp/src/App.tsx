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
import { useEffect, useState } from "react";
import { Brand } from "@patternfly/react-core/dist/js/components/Brand";
import { Nav, NavItem, NavList } from "@patternfly/react-core/dist/js/components/Nav";
import { Page } from "@patternfly/react-core/dist/js/components/Page";
import { PageHeader } from "@patternfly/react-core/deprecated";
import { HashRouter as Router, Link, Route, Routes } from "react-router-dom";
import { PingPongReactIFrameViewsPage } from "./React/PingPongReactIFrameViewsPage";
import { PingPongReactDivViewsPage } from "./React/PingPongReactDivViewsPage";
import { PingPongAngularIFrameViewsPage } from "./Angular/PingPongAngularIFrameViewsPage";
import { PingPongMixedViewsPage } from "./Mixed/PingPongMixedViewsPage";
import { PingPongAngularDivViewsPage } from "./Angular/PingPongAngularDivViewsPage";

enum Location {
  PING_PONG_REACT_IFRAME_PAGES = "/page/ping-pong-react/iframe-pages",
  PING_PONG_REACT_DIV_PAGES = "/page/ping-pong-react/div-pages",
  PING_PONG_ANGULAR_IFRAME_PAGES = "/page/ping-pong-angular/iframe-pages",
  PING_PONG_ANGULAR_DIV_PAGES = "/page/ping-pong-angular/div-pages",
  PING_PONG_MIXED_PAGES = "/page/ping-pong-mixed",
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
  }, []);

  return (
    <Router>
      <Page
        header={
          <PageHeader
            logo={<Brand src={"logo.png"} alt="Logo" />}
            topNav={
              <Nav onSelect={(_event, e) => setLocation(e.itemId as Location)} aria-label="Nav" variant="horizontal">
                <NavList>
                  <NavItem itemId={Location.HOME} isActive={location === Location.HOME}>
                    <Link to={Location.HOME}>Home</Link>
                  </NavItem>
                  <NavItem
                    itemId={Location.PING_PONG_REACT_IFRAME_PAGES}
                    isActive={location === Location.PING_PONG_REACT_IFRAME_PAGES}
                  >
                    <Link to={Location.PING_PONG_REACT_IFRAME_PAGES}>React + IFrames</Link>
                  </NavItem>
                  <NavItem
                    itemId={Location.PING_PONG_REACT_DIV_PAGES}
                    isActive={location === Location.PING_PONG_REACT_DIV_PAGES}
                  >
                    <Link to={Location.PING_PONG_REACT_DIV_PAGES}>React + Divs</Link>
                  </NavItem>
                  <NavItem
                    itemId={Location.PING_PONG_ANGULAR_IFRAME_PAGES}
                    isActive={location === Location.PING_PONG_ANGULAR_IFRAME_PAGES}
                  >
                    <Link to={Location.PING_PONG_ANGULAR_IFRAME_PAGES}>Angular + IFrames</Link>
                  </NavItem>
                  <NavItem
                    itemId={Location.PING_PONG_ANGULAR_DIV_PAGES}
                    isActive={location === Location.PING_PONG_ANGULAR_DIV_PAGES}
                  >
                    <Link to={Location.PING_PONG_ANGULAR_DIV_PAGES}>Angular + Divs</Link>
                  </NavItem>
                  <NavItem
                    itemId={Location.PING_PONG_MIXED_PAGES}
                    isActive={location === Location.PING_PONG_MIXED_PAGES}
                  >
                    <Link to={Location.PING_PONG_MIXED_PAGES}>All mixed</Link>
                  </NavItem>
                </NavList>
              </Nav>
            }
          />
        }
      >
        <Routes>
          <Route path={"/"} element={<p>Select a page</p>} />
          <Route path={Location.PING_PONG_REACT_IFRAME_PAGES} element={<PingPongReactIFrameViewsPage />} />
          <Route path={Location.PING_PONG_REACT_DIV_PAGES} element={<PingPongReactDivViewsPage />} />
          <Route path={Location.PING_PONG_ANGULAR_IFRAME_PAGES} element={<PingPongAngularIFrameViewsPage />} />
          <Route path={Location.PING_PONG_ANGULAR_DIV_PAGES} element={<PingPongAngularDivViewsPage />} />
          <Route path={Location.PING_PONG_MIXED_PAGES} element={<PingPongMixedViewsPage />} />
        </Routes>
      </Page>
    </Router>
  );
}
