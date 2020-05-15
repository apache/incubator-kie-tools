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

import * as React from "react";
import { GlobalContext, GlobalContextType } from "../webview/common/GlobalContext";
import { DesktopRouter } from "../webview/common/DesktopRouter";
import { EnvelopeBusOuterMessageHandlerFactory } from "../webview/editor/EnvelopeBusOuterMessageHandlerFactory";
import { GwtEditorRoutes } from "@kogito-tooling/kie-bc-editors";

export function usingTestingGlobalContext(children: React.ReactElement, ctx?: Partial<GlobalContextType>) {
  const usedCtx = {
    router: new DesktopRouter(
      new GwtEditorRoutes({
        bpmnPath: "editors/bpmn",
        dmnPath: "editors/dmn",
        scesimPath: "editors/scesim"
      })
    ),
    envelopeBusOuterMessageHandlerFactory: new EnvelopeBusOuterMessageHandlerFactory(),
    iframeTemplateRelativePath: "envelope/index.html",
    file: { filePath: "test.dmn", fileContent: "", fileType: "dmn" }
  };
  return {
    ctx: usedCtx,
    wrapper: (
      <GlobalContext.Provider key={""} value={usedCtx}>
        {children}
      </GlobalContext.Provider>
    )
  };
}
