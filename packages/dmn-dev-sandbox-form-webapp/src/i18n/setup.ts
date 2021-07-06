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

import { I18nDefaults, I18nDictionaries } from "@kogito-tooling/i18n/dist/core";
import { I18nContextType } from "@kogito-tooling/i18n/dist/react-components";
import * as React from "react";
import { useContext } from "react";
import { DmnFormI18n } from "./DmnFormI18n";
import { en } from "./locales";

export const dmnFormI18nDefaults: I18nDefaults<DmnFormI18n> = { locale: "en", dictionary: en };
export const dmnFormI18nDictionaries: I18nDictionaries<DmnFormI18n> = new Map([["en", en]]);
export const DmnFormI18nContext = React.createContext<I18nContextType<DmnFormI18n>>({} as any);

export function useDmnFormI18n() {
  return useContext(DmnFormI18nContext);
}
