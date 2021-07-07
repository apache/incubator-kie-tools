/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { I18nDictionariesProvider } from "@kogito-tooling/i18n/dist/react-components";
import * as React from "react";
import { useEffect, useState } from "react";
import { Route, Switch } from "react-router";
import { HashRouter } from "react-router-dom";
import { AppData, DATA_JSON_PATH } from "./AppData";
import { DmnFormErrorPage } from "./DmnFormErrorPage";
import { DmnFormPage } from "./DmnFormPage";
import { DmnFormI18nContext, dmnFormI18nDefaults, dmnFormI18nDictionaries } from "./i18n";
import { NoMatchPage } from "./NoMatchPage";

enum AppState {
  INITIAL,
  READY,
}

export function DmnFormApp() {
  const [appData, setAppData] = useState<AppData>();
  const [appState, setAppState] = useState(AppState.INITIAL);

  useEffect(() => {
    fetch(DATA_JSON_PATH)
      .then((response: any) => response.json())
      .then((appData: AppData) => setAppData(appData))
      .catch((error: any) => console.error(error))
      .finally(() => setAppState(AppState.READY));
  }, []);

  return (
    <I18nDictionariesProvider
      defaults={dmnFormI18nDefaults}
      dictionaries={dmnFormI18nDictionaries}
      initialLocale={navigator.language}
      ctx={DmnFormI18nContext}
    >
      {appState === AppState.READY && (
        <HashRouter>
          <Switch>
            <Route path="/">{appData ? <DmnFormPage appData={appData} /> : <DmnFormErrorPage />}</Route>
            <Route component={NoMatchPage} />
          </Switch>
        </HashRouter>
      )}
    </I18nDictionariesProvider>
  );
}
