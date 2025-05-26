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
import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";
import { HashRouter, Navigate, Route, Routes, useParams } from "react-router-dom";
import { AppContext, AppContextType } from "./AppContext";
import { AppContextProvider } from "./AppContextProvider";
import { DmnFormErrorPage } from "./DmnFormErrorPage";
import { DmnFormPage } from "./DmnFormPage";
import { DmnFormI18nContext, dmnFormI18nDefaults, dmnFormI18nDictionaries } from "./i18n";
import { NoMatchPage } from "./NoMatchPage";
import { routes } from "./Routes";

export function DmnFormApp() {
  return (
    <I18nDictionariesProvider
      defaults={dmnFormI18nDefaults}
      dictionaries={dmnFormI18nDictionaries}
      initialLocale={navigator.language}
      ctx={DmnFormI18nContext}
    >
      <AppContextProvider>
        <AppContext.Consumer>
          {(app) =>
            app.fetchDone && (
              <HashRouter>
                <Routes>
                  <Route path={routes.root.path({})} element={<RootPageRouteElement app={app} />} />
                  <Route path={routes.form.path({ modelName: "*" })} element={<DmnFormPageRouteElement app={app} />} />
                  <Route path={routes.error.path({})} element={<DmnFormErrorPage />} />
                  <Route path={"*"} element={<NoMatchPage />} />
                </Routes>
              </HashRouter>
            )
          }
        </AppContext.Consumer>
      </AppContextProvider>
    </I18nDictionariesProvider>
  );
}

function RootPageRouteElement({ app }: { app: AppContextType }) {
  // If we have data, and elements in the form array, we must navigate to the /form route.
  // Otherwise we navigate to the /error route.
  if (app.data !== undefined && app.data?.forms[0] !== undefined) {
    return <Navigate to={routes.form.path({ modelName: app.data.forms[0].modelName })} replace />;
  }
  return <Navigate to={routes.error.path({})} replace />;
}

function DmnFormPageRouteElement({ app }: { app: AppContextType }) {
  const params = useParams();
  const modelName = params["*"];
  const formData = app.data?.forms.find((form) => form.modelName === modelName);

  if (formData !== undefined) {
    return <DmnFormPage formData={formData} />;
  }
  return <Navigate to={routes.error.path({})} />;
}
