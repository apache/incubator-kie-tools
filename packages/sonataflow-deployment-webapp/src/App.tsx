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

import React from "react";
import { Route, Switch } from "react-router";
import { HashRouter } from "react-router-dom";
import { AppContextProvider } from "./context/AppContextProvider";
import { HomePage } from "./pages/HomePage";
import { NoMatchPage } from "./pages/NoMatchPage";
import { routes } from "./routes";

export function App() {
  return (
    <AppContextProvider>
      <HashRouter>
        <Switch>
          <Route path={routes.root.path({})}>
            <HomePage />
          </Route>
          <Route component={NoMatchPage} />
        </Switch>
      </HashRouter>
    </AppContextProvider>
  );
}
