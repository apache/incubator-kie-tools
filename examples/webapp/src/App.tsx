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
import { Base64PngPage } from "./Pages/Base64Png/Base64PngPage";
import { BpmnPage } from "./Pages/KogitoEditors/BpmnPage";
import { DmnPage } from "./Pages/KogitoEditors/DmnPage";
import { TodoListViewPage } from "./Pages/TodoList/TodoListViewPage";
import { PingPongReactIFrameViewsPage } from "./Pages/PingPong/React/PingPongReactIFrameViewsPage";
import { PingPongReactDivViewsPage } from "./Pages/PingPong/React/PingPongReactDivViewsPage";
import { Home } from "./Home";
import "../static/resources/styles.css";
import { DmnStandaloneEditorPage } from "./Pages/StandaloneEditors/DmnStandaloneEditorPage";
import { SwfStandaloneEditorPage } from "./Pages/StandaloneEditors/SwfStandaloneEditorPage";
import { PingPongAngularIFrameViewsPage } from "./Pages/PingPong/Angular/PingPongAngularIFrameViewsPage";
import { PingPongMixedViewsPage } from "./Pages/PingPong/Mixed/PingPongMixedViewsPage";
import { PingPongAngularDivViewsPage } from "./Pages/PingPong/Angular/PingPongAngularDivViewsPage";

enum Location {
  BPMN = "/editor/bpmn",
  DMN = "/editor/dmn",
  BASE46PNG = "/editor/base64png",
  TODO_LIST = "/page/todo-list",
  PING_PONG_REACT_IFRAME_PAGES = "/page/ping-pong-react/iframe-pages",
  PING_PONG_REACT_DIV_PAGES = "/page/ping-pong-react/div-pages",
  PING_PONG_ANGULAR_IFRAME_PAGES = "/page/ping-pong-angular/iframe-pages",
  PING_PONG_ANGULAR_DIV_PAGES = "/page/ping-pong-angular/div-pages",
  PING_PONG_MIXED_PAGES = "/page/ping-pong-mixed",
  DMN_STANDALONE_EDITOR = "/page/dmn-standalone-editor",
  SWF_STANDALONE_EDITOR = "/page/swf-standalone-editor",
  SWF_STANDALONE_EDITOR_DIAGRAM_ONLY = "/page/swf-standalone-editor-diagram-only",
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
                  <NavItem itemId={Location.BASE46PNG} isActive={location === Location.BASE46PNG}>
                    <Link to={Location.BASE46PNG}>Base64 PNG Editor</Link>
                  </NavItem>
                  <NavItem itemId={Location.BPMN} isActive={location === Location.BPMN}>
                    <Link to={Location.BPMN}>BPMN Editor</Link>
                  </NavItem>
                  <NavItem itemId={Location.DMN} isActive={location === Location.DMN}>
                    <Link to={Location.DMN}>DMN Editor</Link>
                  </NavItem>
                  <NavItem itemId={Location.TODO_LIST} isActive={location === Location.TODO_LIST}>
                    <Link to={Location.TODO_LIST}>'To do' list View</Link>
                  </NavItem>
                  <NavItem
                    itemId={Location.PING_PONG_REACT_IFRAME_PAGES}
                    isActive={location === Location.PING_PONG_REACT_IFRAME_PAGES}
                  >
                    <Link to={Location.PING_PONG_REACT_IFRAME_PAGES}>Ping-Pong React IFrame</Link>
                  </NavItem>
                  <NavItem
                    itemId={Location.PING_PONG_REACT_DIV_PAGES}
                    isActive={location === Location.PING_PONG_REACT_DIV_PAGES}
                  >
                    <Link to={Location.PING_PONG_REACT_DIV_PAGES}>Ping-Pong React Div</Link>
                  </NavItem>
                  <NavItem
                    itemId={Location.PING_PONG_ANGULAR_IFRAME_PAGES}
                    isActive={location === Location.PING_PONG_ANGULAR_IFRAME_PAGES}
                  >
                    <Link to={Location.PING_PONG_ANGULAR_IFRAME_PAGES}>Ping-Pong Angular IFrame</Link>
                  </NavItem>
                  <NavItem
                    itemId={Location.PING_PONG_ANGULAR_DIV_PAGES}
                    isActive={location === Location.PING_PONG_ANGULAR_DIV_PAGES}
                  >
                    <Link to={Location.PING_PONG_ANGULAR_DIV_PAGES}>Ping-Pong Angular Div</Link>
                  </NavItem>
                  <NavItem
                    itemId={Location.PING_PONG_MIXED_PAGES}
                    isActive={location === Location.PING_PONG_MIXED_PAGES}
                  >
                    <Link to={Location.PING_PONG_MIXED_PAGES}>Ping-Pong Mixed Views</Link>
                  </NavItem>
                  <NavItem
                    itemId={Location.DMN_STANDALONE_EDITOR}
                    isActive={location === Location.DMN_STANDALONE_EDITOR}
                  >
                    <Link to={Location.DMN_STANDALONE_EDITOR}>DMN Standalone Editor</Link>
                  </NavItem>
                  <NavItem
                    itemId={Location.SWF_STANDALONE_EDITOR}
                    isActive={location === Location.SWF_STANDALONE_EDITOR}
                  >
                    <Link to={Location.SWF_STANDALONE_EDITOR}>SWF Standalone Editor</Link>
                  </NavItem>
                  <NavItem
                    itemId={Location.SWF_STANDALONE_EDITOR_DIAGRAM_ONLY}
                    isActive={location === Location.SWF_STANDALONE_EDITOR_DIAGRAM_ONLY}
                  >
                    <Link to={Location.SWF_STANDALONE_EDITOR_DIAGRAM_ONLY}>SWF Standalone Editor - Diagram only</Link>
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
          <Route path={Location.BASE46PNG}>
            <Base64PngPage />
          </Route>
          <Route path={Location.BPMN}>
            <BpmnPage />
          </Route>
          <Route path={Location.DMN}>
            <DmnPage />
          </Route>
          <Route path={Location.TODO_LIST}>
            <TodoListViewPage />
          </Route>
          <Route path={Location.PING_PONG_REACT_IFRAME_PAGES}>
            <PingPongReactIFrameViewsPage />
          </Route>
          <Route path={Location.PING_PONG_REACT_DIV_PAGES}>
            <PingPongReactDivViewsPage />
          </Route>
          <Route path={Location.PING_PONG_ANGULAR_IFRAME_PAGES}>
            <PingPongAngularIFrameViewsPage />
          </Route>
          <Route path={Location.PING_PONG_ANGULAR_DIV_PAGES}>
            <PingPongAngularDivViewsPage />
          </Route>
          <Route path={Location.PING_PONG_MIXED_PAGES}>
            <PingPongMixedViewsPage />
          </Route>
          <Route path={Location.DMN_STANDALONE_EDITOR}>
            <DmnStandaloneEditorPage />
          </Route>
          <Route path={Location.SWF_STANDALONE_EDITOR}>
            <SwfStandaloneEditorPage isDiagramOnlyEditor={false} />
          </Route>
          <Route path={Location.SWF_STANDALONE_EDITOR_DIAGRAM_ONLY}>
            <SwfStandaloneEditorPage isDiagramOnlyEditor={true} />
          </Route>
        </Switch>
      </Page>
    </Router>
  );
}
