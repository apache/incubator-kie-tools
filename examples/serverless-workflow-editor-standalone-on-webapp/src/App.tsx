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
import { SwfStandaloneEditorPage } from "./Pages/SwfStandaloneEditorPage";
import { SwfStandaloneDiagramOnlyEditorPage } from "./Pages/SwfStandaloneDiagramOnlyEditorPage";
import { SwfStandaloneTextOnlyEditorPage } from "./Pages/SwfStandaloneTextOnlyEditorPage";

enum Location {
  SWF_STANDALONE_EDITOR = "/page/swf-standalone-editor",
  SWF_STANDALONE_EDITOR_DIAGRAM_ONLY = "/page/swf-standalone-editor-diagram-only",
  SWF_STANDALONE_EDITOR_TEXT_ONLY = "/page/swf-standalone-editor-text-only",
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
                    itemId={Location.SWF_STANDALONE_EDITOR}
                    isActive={location === Location.SWF_STANDALONE_EDITOR}
                  >
                    <Link to={Location.SWF_STANDALONE_EDITOR}>Serverless Workflow Editor (standalone)</Link>
                  </NavItem>
                  <NavItem
                    itemId={Location.SWF_STANDALONE_EDITOR_DIAGRAM_ONLY}
                    isActive={location === Location.SWF_STANDALONE_EDITOR_DIAGRAM_ONLY}
                  >
                    <Link to={Location.SWF_STANDALONE_EDITOR_DIAGRAM_ONLY}>Diagram only</Link>
                  </NavItem>
                  <NavItem
                    itemId={Location.SWF_STANDALONE_EDITOR_TEXT_ONLY}
                    isActive={location === Location.SWF_STANDALONE_EDITOR_TEXT_ONLY}
                  >
                    <Link to={Location.SWF_STANDALONE_EDITOR_TEXT_ONLY}>Text only</Link>
                  </NavItem>
                </NavList>
              </Nav>
            }
          />
        }
      >
        <Routes>
          <Route path={"/"} element={<p>Select a page</p>} />
          <Route path={Location.SWF_STANDALONE_EDITOR} element={<SwfStandaloneEditorPage />} />
          <Route path={Location.SWF_STANDALONE_EDITOR_DIAGRAM_ONLY} element={<SwfStandaloneDiagramOnlyEditorPage />} />
          <Route path={Location.SWF_STANDALONE_EDITOR_TEXT_ONLY} element={<SwfStandaloneTextOnlyEditorPage />} />
        </Routes>
      </Page>
    </Router>
  );
}
