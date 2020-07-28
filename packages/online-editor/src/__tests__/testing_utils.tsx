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

import { EMPTY_FILE_DMN } from "@kogito-tooling/embedded-editor";
import * as React from "react";
import { Route, Switch } from "react-router";
import { HashRouter } from "react-router-dom";
import { GithubService } from "../common/GithubService";
import { GlobalContext, GlobalContextType } from "../common/GlobalContext";
import { Routes } from "../common/Routes";
import { EnvelopeMapping } from "@kogito-tooling/editor-envelope-protocol";

export function usingTestingGlobalContext(children: React.ReactElement, ctx?: Partial<GlobalContextType>) {
  const envelopeMapping: EnvelopeMapping = {
    envelopePath: "envelope/envelope.html",
    resourcesPathPrefix: ""
  };

  const usedCtx = {
    file: EMPTY_FILE_DMN,
    routes: new Routes(),
    editorEnvelopeLocator: {
      targetOrigin: window.location.origin,
      mapping: new Map([
        ["dmn", envelopeMapping],
        ["bpmn", envelopeMapping],
        ["bpmn2", envelopeMapping]
      ])
    },
    readonly: false,
    external: false,
    senderTabId: undefined,
    githubService: new GithubService(),
    ...ctx
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
    )
  };
}
