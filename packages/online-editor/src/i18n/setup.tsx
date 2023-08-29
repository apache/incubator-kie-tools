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

import * as React from "react";
import { useContext } from "react";
import { I18nContextType, I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";
import { OnlineI18n } from "./OnlineI18n";
import { en } from "./locales";
import { de } from "./locales";
import { I18nDefaults, I18nDictionaries } from "@kie-tools-core/i18n/dist/core";

export const onlineI18nDefaults: I18nDefaults<OnlineI18n> = { locale: "en", dictionary: en };
export const onlineI18nDictionaries: I18nDictionaries<OnlineI18n> = new Map([
  ["en", en],
  ["de", de],
]);
export const OnlineI18nContext = React.createContext<I18nContextType<OnlineI18n>>({} as any);

export function OnlineI18nContextProvider(props: { children: any }) {
  return (
    <I18nDictionariesProvider
      defaults={onlineI18nDefaults}
      dictionaries={onlineI18nDictionaries}
      initialLocale={navigator.language}
      ctx={OnlineI18nContext}
    >
      {props.children}
    </I18nDictionariesProvider>
  );
}

export function useOnlineI18n() {
  return useContext(OnlineI18nContext);
}
