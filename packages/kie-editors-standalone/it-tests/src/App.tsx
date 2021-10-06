/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { BrowserRouter as Router, Link, Route, Switch } from "react-router-dom";
import { DmnEditorComponent } from "./components/DmnEditorComponent";
import { BpmnEditorComponent } from "./components/BpmnEditorComponent";
import { ContentType } from "@kie-tooling-core/workspace/dist/api";
import processWithWidDefinition from "raw-loader!./resources/processWithWidDefinition.bpmn2";
import customWorkItemWid from "raw-loader!./resources/widDefinitions.wid";

export function App() {
  return (
    <Router>
      <>
        <div style={{ display: "flex", justifyContent: "space-between" }}>
          <Link to="/dmn-read-only">DMN Read Only</Link>
          <Link to="/dmn-editable">DMN Editable</Link>
          <Link to="/bpmn-editable">BPMN Editable</Link>
          <Link to="/bpmn-read-only">BPMN Read Only</Link>
          <Link to="/bpmn-workitem">BPMN Workitem</Link>
          <Link to="/both-bpmn-dmn">Both BPMN DMN</Link>
        </div>
        <br />
        <Switch>
          <Route
            exact={true}
            path="/dmn-read-only"
            render={() => (
              <DmnEditorComponent
                origin={"*"}
                key="dmn-read-only"
                id="dmn-read-only"
                readOnly={true}
                initialContent={Promise.resolve("")}
              />
            )}
          />
          <Route
            exact={true}
            path="/dmn-editable"
            render={() => (
              <DmnEditorComponent
                origin={"*"}
                key="dmn-editable"
                id="dmn-editable"
                readOnly={false}
                initialContent={Promise.resolve("")}
              />
            )}
          />
          <Route
            exact={true}
            path="/bpmn-editable"
            render={() => (
              <BpmnEditorComponent
                origin={"*"}
                key="bpmn-editable"
                id="bpmn-editable"
                readOnly={false}
                initialContent={Promise.resolve("")}
              />
            )}
          />
          <Route
            exact={true}
            path="/bpmn-read-only"
            render={() => (
              <BpmnEditorComponent
                origin={"*"}
                key="bpmn-read-only"
                id="bpmn-read-only"
                readOnly={true}
                initialContent={Promise.resolve("")}
              />
            )}
          />
          <Route
            exact={true}
            path="/bpmn-workitem"
            render={() => (
              <BpmnEditorComponent
                origin={"*"}
                id="bpmn-workitem"
                readOnly={false}
                initialContent={Promise.resolve(processWithWidDefinition)}
                resources={
                  new Map([
                    [
                      "custom-workitem.wid",
                      {
                        contentType: ContentType.TEXT,
                        content: Promise.resolve(customWorkItemWid),
                      },
                    ],
                  ])
                }
              />
            )}
          />
          <Route
            exact={true}
            path="/both-bpmn-dmn"
            render={() => (
              <>
                <BpmnEditorComponent
                  origin={"*"}
                  id="both-bpmn"
                  readOnly={false}
                  initialContent={Promise.resolve("")}
                />
                <DmnEditorComponent origin={"*"} id="both-dmn" readOnly={false} initialContent={Promise.resolve("")} />
              </>
            )}
          />
        </Switch>
      </>
    </Router>
  );
}
