/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { I18nDefaults, I18nDictionaries } from "@kie-tools-core/i18n/dist/core";
import {
  I18nContextType,
  I18nDictionariesProvider,
  I18nDictionariesProviderProps,
} from "@kie-tools-core/i18n/dist/react-components";
import * as React from "react";
import { useContext } from "react";
import { ChromeExtensionI18n } from "./ChromeExtensionI18n";
import { en } from "./locales";

export const chromeExtensionI18nDefaults: I18nDefaults<ChromeExtensionI18n> = { locale: "en", dictionary: en };
export const chromeExtensionI18nDictionaries: I18nDictionaries<ChromeExtensionI18n> = new Map([["en", en]]);
export const ChromeExtensionI18nContext = React.createContext<I18nContextType<ChromeExtensionI18n>>({} as any);

export function ChromeExtensionI18nContextProvider(props: { children: any }) {
  return (
    <I18nDictionariesProvider
      defaults={chromeExtensionI18nDefaults}
      dictionaries={chromeExtensionI18nDictionaries}
      initialLocale={navigator.language}
      ctx={ChromeExtensionI18nContext}
    >
      {props.children}
    </I18nDictionariesProvider>
  );
}

export function useChromeExtensionI18n() {
  return useContext(ChromeExtensionI18nContext);
}
