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
import { useContext } from "react";
import { I18nContextType } from "@kie-tooling-core/i18n/dist/react-components";
import { en } from "./locales";
import { ChromeExtensionI18n } from "./ChromeExtensionI18n";
import { I18nDefaults, I18nDictionaries } from "@kie-tooling-core/i18n/dist/core";

export const chromeExtensionI18nDefaults: I18nDefaults<ChromeExtensionI18n> = {
  locale: navigator.language,
  dictionary: en,
};

export const chromeExtensionI18nDictionaries: I18nDictionaries<ChromeExtensionI18n> = new Map([["en", en]]);
export const ChromeExtensionI18nContext = React.createContext<I18nContextType<ChromeExtensionI18n>>({} as any);

export function useChromeExtensionI18n() {
  return useContext(ChromeExtensionI18nContext);
}
