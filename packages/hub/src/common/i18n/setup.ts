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
import { HubI18n } from "./HubI18n";
import { I18nDefaults, I18nDictionaries } from "@kie-tooling-core/i18n/dist/core";

export const hubI18nDefaults: I18nDefaults<HubI18n> = { locale: "en", dictionary: en };
export const hubI18nDictionaries: I18nDictionaries<HubI18n> = new Map([["en", en]]);
export const HubI18nContext = React.createContext<I18nContextType<HubI18n>>({} as any);

export function useHubI18n() {
  return useContext(HubI18nContext);
}
