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
import { BrowserRouter as Router, Switch, Route, Link } from "react-router-dom";
import { DmnEditorComponent } from "../components/DmnEditorComponent";
import { BpmnEditorComponent } from "../components/BpmnEditorComponent";
import { ContentType } from "@kogito-tooling/channel-common-api";
import { customWorkItemWid } from "./widDefinitions";
import { processWithWidDefinition } from "./processWithWidDefinition.js"

export const EditorPage: React.FC<{}> = () => {
  return (
    <Router>
      <>
        <ul>
          <li>
            <Link to="/dmn-read-only">DMN Read Only</Link>
          </li>
          <li>
            <Link to="/dmn-editable">DMN Editable</Link>
          </li>
          <li>
            <Link to="/bpmn-editable">BPMN Editable</Link>
          </li>
          <li>
            <Link to="/bpmn-read-only">BPMN Read Only</Link>
          </li>
          <li>
            <Link to="/bpmn-workitem">BPMN Workitem</Link>
          </li>
          <li>
            <Link to="/both-bpmn-dmn">Both BPMN DMN</Link>
          </li>
        </ul>
        <Switch>
          <Route
            exact={true}
            path="/dmn-read-only"
            render={() => (
              <DmnEditorComponent
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
                key="bpmn-workitem"
                id="bpmn-workitem"
                readOnly={false}
                initialContent={Promise.resolve(processWithWidDefinition)}
                resources={
                  new Map([
                    [
                      "custom-workitem.wid",
                      {
                        type: ContentType.TEXT,
                        content: customWorkItemWid,
                        path: "custom-workitem.wid"
                      }
                    ]
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
                <BpmnEditorComponent id="both-bpmn" readOnly={false} initialContent={Promise.resolve("")} />
                <DmnEditorComponent id="both-dmn" readOnly={false} initialContent={Promise.resolve("")} />
              </>
            )}
          />
        </Switch>
      </>
    </Router>
  );
};
