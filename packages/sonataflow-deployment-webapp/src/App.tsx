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
import { Route, Switch } from "react-router";
import { HashRouter, Redirect } from "react-router-dom";
import { AppContextProvider } from "./context/AppContextProvider";
import { OpenApiContextProvider } from "./context/OpenApiContextProvider";
import { Workflows } from "./pages/Workflows/";
import { NoMatchPage } from "./pages/NoMatchPage";
import { routes } from "./routes";
import { WorkflowFormPage } from "./pages/Workflows/WorkflowFormPage";
import { CloudEventFormPage } from "./pages/Workflows/CloudEventFormPage";

export function App() {
  return (
    <AppContextProvider>
      <OpenApiContextProvider>
        <HashRouter>
          <Switch>
            <Route path={routes.workflows.form.path({ workflowId: ":workflowId" })}>
              {({ match }) => <WorkflowFormPage workflowId={match!.params.workflowId!} />}
            </Route>
            <Route path={routes.workflows.cloudEvent.path({})}>
              <CloudEventFormPage />
            </Route>
            <Route path={routes.workflows.home.path({})}>
              <Workflows />
            </Route>
            <Route path={routes.home.path({})}>
              <Redirect to={routes.workflows.home.path({})} />
            </Route>
            <Route component={NoMatchPage} />
          </Switch>
        </HashRouter>
      </OpenApiContextProvider>
    </AppContextProvider>
  );
}
