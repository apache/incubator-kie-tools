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
import { GlobalContext, GlobalContextType } from "../app/components/common/GlobalContext";
import { ResourceContentServiceFactory } from "../app/components/common/ChromeResourceContentService";
import { GitHubContext, GitHubContextType } from "../app/components/common/GitHubContext";
import { Logger } from "../Logger";
import { Dependencies } from "../app/Dependencies";
import { EditorEnvelopeLocator, EnvelopeMapping } from "@kogito-tooling/editor-envelope-protocol";

export function usingTestingGlobalContext(children: React.ReactElement, ctx?: Partial<GlobalContextType>) {
  const txtEnvelopeMapping: EnvelopeMapping = {
    envelopePath: "chrome-testing://https://my-url.com/",
    resourcesPathPrefix: "envelope"
  };

  const editorEnvelopeLocator: EditorEnvelopeLocator = {
    targetOrigin: "localhost:8888",
    mapping: new Map([["txt", txtEnvelopeMapping]])
  };

  const usedCtx = {
    id: "test-extension123",
    envelopeLocator: editorEnvelopeLocator,
    githubAuthTokenCookieName: "test-github-pat-name",
    logger: new Logger("test-extension"),
    dependencies: new Dependencies(),
    extensionIconUrl: "/extension/icon.jpg",
    resourceContentServiceFactory: new ResourceContentServiceFactory(),
    externalEditorManager: {
      name: "Test Online Editor",
      getLink: jest.fn(path => `https://external-editor-link/${path}`),
      listenToComeBack: jest.fn(),
      open: jest.fn()
    },
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

export function usingTestingGitHubContext(
  children: React.ReactElement,
  ctx?: Pick<GitHubContextType, keyof GitHubContextType>
) {
  const usedCtx = {
    octokit: jest.fn(),
    setToken: jest.fn(),
    token: "",
    userIsLoggedIn: jest.fn(() => true),
    ...ctx
  };
  return {
    ctx: usedCtx,
    wrapper: <GitHubContext.Provider value={usedCtx}>{children}</GitHubContext.Provider>
  };
}
