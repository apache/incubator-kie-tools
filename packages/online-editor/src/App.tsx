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

import { File } from "@kie-tooling-core/editor/dist/channel";
import * as React from "react";
import { Route, Switch } from "react-router";
import { BrowserRouter } from "react-router-dom";
import { GlobalContext, GlobalContextProvider } from "./common/GlobalContext";
import { EditorPage } from "./editor/EditorPage";
import { DownloadHubModal } from "./home/DownloadHubModal";
import { HomePage } from "./home/HomePage";
import { NoMatchPage } from "./NoMatchPage";
import { I18nDictionariesProvider } from "@kie-tooling-core/i18n/dist/react-components";
import { OnlineI18nContext, onlineI18nDefaults, onlineI18nDictionaries } from "./common/i18n";
import { SettingsContextProvider } from "./settings/SettingsContext";
import { KieToolingExtendedServicesContextProvider } from "./editor/KieToolingExtendedServices/KieToolingExtendedServicesContextProvider";

interface Props {
  externalFile?: File;
  senderTabId?: string;
}

export function App(props: Props) {
  return (
    <BrowserRouter>
      <I18nDictionariesProvider
        defaults={onlineI18nDefaults}
        dictionaries={onlineI18nDictionaries}
        initialLocale={navigator.language}
        ctx={OnlineI18nContext}
      >
        <GlobalContextProvider externalFile={props.externalFile} senderTabId={props.senderTabId}>
          <KieToolingExtendedServicesContextProvider>
            <SettingsContextProvider>
              <GlobalContext.Consumer>
                {({ routes }) => (
                  <Switch>
                    <Route path={routes.editor.url({ type: ":type" })}>
                      <EditorPage />
                    </Route>
                    <Route exact={true} path={routes.home.url({})}>
                      <HomePage />
                    </Route>
                    <Route exact={true} path={routes.downloadHub.url({})}>
                      <HomePage />
                      <DownloadHubModal />
                    </Route>
                    <Route component={NoMatchPage} />
                  </Switch>
                )}
              </GlobalContext.Consumer>
            </SettingsContextProvider>
          </KieToolingExtendedServicesContextProvider>
        </GlobalContextProvider>
      </I18nDictionariesProvider>
    </BrowserRouter>
  );
}
