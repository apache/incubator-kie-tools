/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { EMPTY_FILE_DMN } from "@kogito-tooling/editor/dist/channel";
import * as React from "react";
import { Route, Switch } from "react-router";
import { HashRouter } from "react-router-dom";
import { GithubService } from "../common/GithubService";
import { GlobalContext, GlobalContextType } from "../common/GlobalContext";
import { Routes } from "../common/Routes";
import { EnvelopeMapping } from "@kogito-tooling/editor/dist/api";
import { I18nDictionariesProvider, I18nDictionariesProviderProps } from "@kogito-tooling/i18n/dist/react-components";
import { onlineI18nDefaults, onlineI18nDictionaries, OnlineI18nContext } from "../common/i18n";
import { OnlineI18n } from "../common/i18n";

export function usingTestingGlobalContext(children: React.ReactElement, ctx?: Partial<GlobalContextType>) {
  const envelopeMapping: EnvelopeMapping = {
    envelopePath: "envelope/envelope.html",
    resourcesPathPrefix: "",
  };

  const usedCtx: GlobalContextType = {
    file: EMPTY_FILE_DMN,
    routes: new Routes(),
    editorEnvelopeLocator: {
      targetOrigin: window.location.origin,
      mapping: new Map([
        ["dmn", envelopeMapping],
        ["bpmn", envelopeMapping],
        ["bpmn2", envelopeMapping],
      ]),
    },
    readonly: false,
    external: false,
    senderTabId: undefined,
    githubService: new GithubService(),
    ...ctx,
  };
  return {
    ctx: usedCtx,
    wrapper: (
      <GlobalContext.Provider key={""} value={usedCtx}>
        <HashRouter>
          <Switch>
            <Route exact={true} path={usedCtx.routes.home.url({})}>
              {children}
            </Route>
          </Switch>
        </HashRouter>
      </GlobalContext.Provider>
    ),
  };
}

export function usingTestingOnlineI18nContext(
  children: React.ReactElement,
  ctx?: Partial<I18nDictionariesProviderProps<OnlineI18n>>
) {
  const usedCtx: I18nDictionariesProviderProps<OnlineI18n> = {
    defaults: onlineI18nDefaults,
    dictionaries: onlineI18nDictionaries,
    ctx: OnlineI18nContext,
    children,
    ...ctx,
  };
  return {
    ctx: usedCtx,
    wrapper: (
      <I18nDictionariesProvider defaults={usedCtx.defaults} dictionaries={usedCtx.dictionaries} ctx={usedCtx.ctx}>
        {usedCtx.children}
      </I18nDictionariesProvider>
    ),
  };
}
