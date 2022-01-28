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

import * as React from "react";
import { useContext } from "react";
import { en } from "./locales";
import { I18nContextType } from "@kie-tools-core/i18n/dist/react-components";
import { ImportJavaClassesWizardI18n } from "./ImportJavaClassesWizardI18n";
import { I18nDefaults, I18nDictionaries } from "@kie-tools-core/i18n/dist/core";

export const importJavaClassesWizardI18nDefaults: I18nDefaults<ImportJavaClassesWizardI18n> = {
  locale: "en",
  dictionary: en,
};
export const importJavaClassesWizardI18nDictionaries: I18nDictionaries<ImportJavaClassesWizardI18n> = new Map([
  ["en", en],
]);
export const ImportJavaClassesWizardI18nContext = React.createContext<I18nContextType<ImportJavaClassesWizardI18n>>(
  {} as never
);

export function useImportJavaClassesWizardI18n(): I18nContextType<ImportJavaClassesWizardI18n> {
  return useContext(ImportJavaClassesWizardI18nContext);
}
