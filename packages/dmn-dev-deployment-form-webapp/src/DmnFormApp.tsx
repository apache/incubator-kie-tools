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

import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";
import * as React from "react";
import { Route, Switch } from "react-router";
import { HashRouter, Redirect } from "react-router-dom";
import { AppContext } from "./AppContext";
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
                <Switch>
                  {app.data && (
                    <Route
                      path={routes.form.path({
                        filePath: ":filePath*",
                      })}
                    >
                      {({ match }) => {
                        const formData = app.data!.forms.find((form) => form.uri === `/${match!.params.filePath}`);
                        return formData ? <DmnFormPage formData={formData} /> : <Redirect to={routes.error.path({})} />;
                      }}
                    </Route>
                  )}
                  {app.data && (
                    <Route exact={true} path={routes.root.path({})}>
                      <Redirect to={routes.form.path({ filePath: app.data.forms[0].uri.slice(1) })} />
                    </Route>
                  )}
                  <Route path={routes.error.path({})}>
                    <DmnFormErrorPage />
                  </Route>
                  {!app.data && <Redirect to={routes.error.path({})} />}
                  <Route component={NoMatchPage} />
                </Switch>
              </HashRouter>
            )
          }
        </AppContext.Consumer>
      </AppContextProvider>
    </I18nDictionariesProvider>
  );
}
