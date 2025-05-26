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
import { HashRouter, Route, Routes } from "react-router-dom";
import { AppContextProvider } from "./context/AppContextProvider";
import { AppI18nContext, appI18nDefaults, appI18nDictionaries } from "./i18n";
import { HomePage } from "./pages/HomePage";
import { NoMatchPage } from "./pages/NoMatchPage";
import { routes } from "./routes";

export function App() {
  return (
    <I18nDictionariesProvider
      defaults={appI18nDefaults}
      dictionaries={appI18nDictionaries}
      initialLocale={navigator.language}
      ctx={AppI18nContext}
    >
      <HashRouter>
        <AppContextProvider>
          <Routes>
            <Route path={routes.dashboard.path({ filePath: ":filePath" })} element={<HomePage />} />
            <Route path={routes.root.path({})} element={<HomePage />} />
            <Route path={"*"} element={<NoMatchPage />} />
          </Routes>
        </AppContextProvider>
      </HashRouter>
    </I18nDictionariesProvider>
  );
}
