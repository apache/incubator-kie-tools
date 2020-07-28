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
import { EnvelopeMapping } from "@kogito-tooling/editor-envelope-protocol";

export function usingTestingGlobalContext(children: React.ReactElement, ctx?: Partial<GlobalContextType>) {

  const dmnEnvelopeMapping: EnvelopeMapping = {
    envelopePath: "envelope/envelope.html",
    resourcesPathPrefix: ""
  };

  const usedCtx = {
    editorEnvelopeLocator: { targetOrigin: window.location.origin, mapping: new Map([["dmn", dmnEnvelopeMapping]]) },
    file: { filePath: "test.dmn", fileContent: "", fileType: "dmn" },
    ...ctx
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
