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

import { EditorEnvelopeLocator } from "@kie-tooling-core/editor/dist/api";
import { File } from "@kie-tooling-core/editor/dist/channel";
import { I18nDictionariesProvider } from "@kie-tooling-core/i18n/dist/react-components";
import * as React from "react";
import { useMemo } from "react";
import { Route, Switch } from "react-router";
import { HashRouter } from "react-router-dom";
import { GithubService } from "./common/GithubService";
import { GlobalContext } from "./common/GlobalContext";
import { OnlineI18nContext, onlineI18nDefaults, onlineI18nDictionaries } from "./common/i18n";
import { Routes } from "./common/Routes";
import { EditorPage } from "./editor/EditorPage";
import { DownloadHubModal } from "./home/DownloadHubModal";
import { HomePage } from "./home/HomePage";
import { NoMatchPage } from "./NoMatchPage";
import { WorkspaceContext } from "./workspace/WorkspaceContext";
import { WorkspaceContextProvider } from "./workspace/WorkspaceContextProvider";

interface Props {
  file?: File;
  readonly: boolean;
  external: boolean;
  senderTabId?: string;
  githubService: GithubService;
  editorEnvelopeLocator: EditorEnvelopeLocator;
}

export function App(props: Props) {
  const routes = useMemo(() => new Routes(), []);

  return (
    <I18nDictionariesProvider
      defaults={onlineI18nDefaults}
      dictionaries={onlineI18nDictionaries}
      initialLocale={navigator.language}
      ctx={OnlineI18nContext}
    >
      <GlobalContext.Provider
        value={{
          routes,
          editorEnvelopeLocator: props.editorEnvelopeLocator,
          readonly: props.readonly,
          external: props.external,
          senderTabId: props.senderTabId,
          githubService: props.githubService,
          isChrome: !!window.chrome,
        }}
      >
        <WorkspaceContextProvider file={props.file} githubService={props.githubService}>
          <HashRouter>
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
          </HashRouter>
        </WorkspaceContextProvider>
      </GlobalContext.Provider>
    </I18nDictionariesProvider>
  );
}
