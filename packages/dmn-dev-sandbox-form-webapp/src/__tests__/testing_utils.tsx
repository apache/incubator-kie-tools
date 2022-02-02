/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { I18nDictionariesProvider, I18nDictionariesProviderProps } from "@kie-tools-core/i18n/dist/react-components";
import * as React from "react";
import { AppContext, AppContextType } from "../AppContext";
import { DmnFormI18n, DmnFormI18nContext, dmnFormI18nDefaults, dmnFormI18nDictionaries } from "../i18n";

export function usingTestingDmnFormI18nContext(
  children: React.ReactElement,
  ctx?: Partial<I18nDictionariesProviderProps<DmnFormI18n>>
) {
  const usedCtx: I18nDictionariesProviderProps<DmnFormI18n> = {
    defaults: dmnFormI18nDefaults,
    dictionaries: dmnFormI18nDictionaries,
    ctx: DmnFormI18nContext,
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

export function usingTestingAppContext(children: React.ReactElement, ctx?: Partial<AppContextType>) {
  const usedCtx: AppContextType = {
    fetchDone: true,
    ...ctx,
  };

  return {
    ctx: usedCtx,
    wrapper: <AppContext.Provider value={usedCtx}>{children}</AppContext.Provider>,
  };
}
