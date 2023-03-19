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
import {
  GlobalContext,
  GlobalContextType,
} from "@kie-tools-core/chrome-extension/dist/app/components/common/GlobalContext";
import { ResourceContentServiceFactory } from "@kie-tools-core/chrome-extension/dist/app/components/common/ChromeResourceContentService";
import {
  GitHubContext,
  GitHubContextType,
} from "@kie-tools-core/chrome-extension/dist/app/components/common/GitHubContext";
import { Logger } from "@kie-tools-core/chrome-extension/dist/Logger";
import { Dependencies } from "@kie-tools-core/chrome-extension/dist/app/Dependencies";
import { EditorEnvelopeLocator, EnvelopeContentType, EnvelopeMapping } from "@kie-tools-core/editor/dist/api";
import { I18nDictionariesProvider, I18nDictionariesProviderProps } from "@kie-tools-core/i18n/dist/react-components";
import {
  ChromeExtensionI18nContext,
  chromeExtensionI18nDictionaries,
  chromeExtensionI18nDefaults,
} from "@kie-tools-core/chrome-extension/dist/app/i18n";
import { ChromeExtensionI18n } from "@kie-tools-core/chrome-extension/dist/app/i18n";

export function usingTestingGlobalContext(children: React.ReactElement, ctx?: Partial<GlobalContextType>) {
  const editorEnvelopeLocator = new EditorEnvelopeLocator("localhost:8888", [
    new EnvelopeMapping({
      type: "txt",
      filePathGlob: "**/*.txt",
      resourcesPathPrefix: "envelope",
      envelopeContent: { type: EnvelopeContentType.PATH, path: "chrome-testing://https://my-url.com/" },
    }),
  ]);

  const usedCtx: GlobalContextType = {
    id: "test-extension123",
    envelopeLocator: editorEnvelopeLocator,
    githubAuthTokenCookieName: "test-github-pat-name",
    logger: new Logger("test-extension"),
    dependencies: new Dependencies(),
    extensionIconUrl: "/extension/icon.jpg",
    resourceContentServiceFactory: new ResourceContentServiceFactory(),
    externalEditorManager: {
      name: "Test Online Editor",
      getLink: jest.fn((path) => `https://external-editor-link/${path}`),
      open: jest.fn(),
    },
    ...ctx,
  };

  return {
    ctx: usedCtx,
    wrapper: (
      <GlobalContext.Provider key={""} value={usedCtx}>
        {children}
      </GlobalContext.Provider>
    ),
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
    ...ctx,
  };
  return {
    ctx: usedCtx,
    wrapper: <GitHubContext.Provider value={usedCtx}>{children}</GitHubContext.Provider>,
  };
}

export function usingTestingChromeExtensionI18nContext(
  children: React.ReactElement,
  ctx?: Partial<I18nDictionariesProviderProps<ChromeExtensionI18n>>
) {
  const usedCtx: I18nDictionariesProviderProps<ChromeExtensionI18n> = {
    defaults: chromeExtensionI18nDefaults,
    dictionaries: chromeExtensionI18nDictionaries,
    ctx: ChromeExtensionI18nContext,
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
