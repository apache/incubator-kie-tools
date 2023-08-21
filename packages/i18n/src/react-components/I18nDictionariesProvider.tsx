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
import { useCallback, useMemo, useState } from "react";
import { I18n, I18nDefaults, I18nDictionaries, ReferenceDictionary } from "../core";
import { I18nContextType } from "./I18nContext";

export interface I18nDictionariesProviderProps<D extends ReferenceDictionary> {
  defaults: I18nDefaults<D>;
  dictionaries: I18nDictionaries<D>;
  initialLocale?: string;
  ctx: React.Context<I18nContextType<D>>;
  children: React.ReactNode;
}

export const I18nDictionariesProvider = <D extends ReferenceDictionary>(props: I18nDictionariesProviderProps<D>) => {
  const [locale, setLocale] = useState(props.initialLocale ?? props.defaults.locale);

  const i18n = useMemo(
    () => new I18n(props.defaults, props.dictionaries, locale),
    [locale, props.defaults, props.dictionaries]
  );

  const setNewLocale = useCallback(
    (newLocale: string) => {
      i18n.setLocale(newLocale);
      setLocale(newLocale);
    },
    [i18n]
  );

  const value = useMemo(
    () => ({
      locale,
      setLocale: setNewLocale,
      i18n: i18n.getCurrent(),
    }),
    [i18n, locale, setNewLocale]
  );

  return <props.ctx.Provider value={value}>{props.children}</props.ctx.Provider>;
};
