/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import {
  ChannelType,
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeApi,
  KogitoEditorEnvelopeContext,
  KogitoEditorEnvelopeContextType,
} from "@kie-tools-core/editor/dist/api";
import { DefaultKeyboardShortcutsService } from "@kie-tools-core/keyboard-shortcuts/dist/envelope";
import * as React from "react";
import { I18nService } from "@kie-tools-core/i18n/dist/envelope";
import { I18nDictionariesProvider, I18nDictionariesProviderProps } from "@kie-tools-core/i18n/dist/react-components";
import {
  EditorEnvelopeI18n,
  EditorEnvelopeI18nContext,
  editorEnvelopeI18nDefaults,
  editorEnvelopeI18nDictionaries,
} from "@kie-tools-core/editor/dist/envelope/i18n";

export const DEFAULT_TESTING_ENVELOPE_CONTEXT: KogitoEditorEnvelopeContextType<
  KogitoEditorEnvelopeApi,
  KogitoEditorChannelApi
> = {
  shared: {} as any,
  channelApi: {} as any,
  services: {
    keyboardShortcuts: new DefaultKeyboardShortcutsService({} as any),
    i18n: new I18nService(),
  },
  supportedThemes: [],
};

export function usingEnvelopeContext(
  children: React.ReactElement,
  ctx?: Partial<KogitoEditorEnvelopeContextType<KogitoEditorEnvelopeApi, KogitoEditorChannelApi>>
) {
  const usedCtx = { ...DEFAULT_TESTING_ENVELOPE_CONTEXT, ...ctx };
  return {
    ctx: usedCtx,
    wrapper: (
      <KogitoEditorEnvelopeContext.Provider key={""} value={usedCtx}>
        {children}
      </KogitoEditorEnvelopeContext.Provider>
    ),
  };
}

export function usingEditorEnvelopeI18nContext(
  children: React.ReactElement,
  ctx?: Partial<I18nDictionariesProviderProps<EditorEnvelopeI18n>>
) {
  const usedCtx: I18nDictionariesProviderProps<EditorEnvelopeI18n> = {
    defaults: editorEnvelopeI18nDefaults,
    dictionaries: editorEnvelopeI18nDictionaries,
    ctx: EditorEnvelopeI18nContext,
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
