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
import { BrowserRouter as Router, Link, Route, Routes } from "react-router-dom";
import { DmnEditorComponent } from "./components/DmnEditorComponent";
import { BpmnEditorComponent } from "./components/BpmnEditorComponent";
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
        <Routes>
          <Route
            path="/dmn-read-only"
            element={
              <DmnEditorComponent
                origin={"*"}
                key="dmn-read-only"
                id="dmn-read-only"
                readOnly={true}
                initialContent={Promise.resolve("")}
              />
            }
          />
          <Route
            path="/dmn-editable"
            element={
              <DmnEditorComponent
                origin={"*"}
                key="dmn-editable"
                id="dmn-editable"
                readOnly={false}
                initialContent={Promise.resolve("")}
              />
            }
          />
          <Route
            path="/bpmn-editable"
            element={
              <BpmnEditorComponent
                origin={"*"}
                key="bpmn-editable"
                id="bpmn-editable"
                readOnly={false}
                initialContent={Promise.resolve("")}
              />
            }
          />
          <Route
            path="/bpmn-read-only"
            element={
              <BpmnEditorComponent
                origin={"*"}
                key="bpmn-read-only"
                id="bpmn-read-only"
                readOnly={true}
                initialContent={Promise.resolve("")}
              />
            }
          />
          <Route
            path="/bpmn-workitem"
            element={
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
                        contentType: "text",
                        content: Promise.resolve(customWorkItemWid),
                      },
                    ],
                  ])
                }
              />
            }
          />
          <Route
            path="/both-bpmn-dmn"
            element={
              <>
                <BpmnEditorComponent
                  origin={"*"}
                  id="both-bpmn"
                  readOnly={false}
                  initialContent={Promise.resolve("")}
                />
                <DmnEditorComponent origin={"*"} id="both-dmn" readOnly={false} initialContent={Promise.resolve("")} />
              </>
            }
          />
        </Routes>
      </>
    </Router>
  );
}
