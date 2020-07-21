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
import { FunctionComponent, useMemo, useState } from "react";
import { I18nContext } from "./i18nContext";
import { DeepOptional, TranslationBundle } from "./types";
import { deepMerge } from "./utils";

export const I18nContextComponent: FunctionComponent<{
  defaultLocale: string;
  dictionaries: Map<string, DeepOptional<TranslationBundle<any>>>;
}> = props => {
  const [locale, setLocale] = useState(props.defaultLocale);
  const dictionary = useMemo(() => {
    const selectedDictionary = props.dictionaries.get(locale) ?? props.dictionaries.get(locale.split("-").shift()!);

    return deepMerge(props.dictionaries.get(props.defaultLocale)!, selectedDictionary);
  }, [locale]);

  return <I18nContext.Provider value={{ locale, setLocale, dictionary }}>{props.children}</I18nContext.Provider>;
};
