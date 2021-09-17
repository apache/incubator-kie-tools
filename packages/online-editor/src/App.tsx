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
import { Redirect, Route, Switch } from "react-router";
import { HashRouter } from "react-router-dom";
import { GlobalContext, GlobalContextProvider, SupportedFileExtensions } from "./common/GlobalContext";
import { EditorPage } from "./editor/EditorPage";
import { I18nDictionariesProvider } from "@kie-tooling-core/i18n/dist/react-components";
import { OnlineI18nContext, onlineI18nDefaults, onlineI18nDictionaries } from "./common/i18n";
import { SettingsContextProvider } from "./settings/SettingsContext";
import { KieToolingExtendedServicesContextProvider } from "./editor/KieToolingExtendedServices/KieToolingExtendedServicesContextProvider";
import { HomePage } from "./home/HomePage";
import { DownloadHubModal } from "./home/DownloadHubModal";
import { NoMatchPage } from "./NoMatchPage";

interface Props {
  externalFile?: File;
  senderTabId?: string;
}

export function App(props: Props) {
  return (
    <HashRouter>
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
                {({ routes, editorEnvelopeLocator }) => (
                  <Switch>
                    <Route path={routes.editor({ extension: ":extension" })}>
                      {(match) => {
                        const extension = match.match?.params.extension ?? "";
                        return (
                          <>
                            {editorEnvelopeLocator.mapping.has(extension) ? (
                              <EditorPage forExtension={extension as SupportedFileExtensions} />
                            ) : (
                              <Redirect to={routes.home()} />
                            )}
                          </>
                        );
                      }}
                    </Route>
                    <Route exact={true} path={routes.home()}>
                      <HomePage />
                    </Route>
                    <Route exact={true} path={routes.downloadHub()}>
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
    </HashRouter>
  );
}
