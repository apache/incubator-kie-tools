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
import { useMemo, useState } from "react";
import { I18nContext } from "./I18nContext";
import { DeepOptional, Dictionary, DictionaryInterpolation } from "./types";

export const I18nProvider = <D extends Dictionary<D>>(props: {
  defaults: { locale: string; dictionary: D };
  dictionaries: Map<string, DeepOptional<D>>;
  children: React.ReactNode;
}) => {
  const [locale, setLocale] = useState(props.defaults.locale);
  const dictionary = useMemo(() => {
    const selectedDictionary =
      props.dictionaries.get(locale) ?? props.dictionaries.get(locale.split("-").shift()!) ?? {};

    return immutableDeepMerge(props.defaults.dictionary, selectedDictionary);
  }, [locale]);

  return <I18nContext.Provider value={{ locale, setLocale, dictionary }}>{props.children}</I18nContext.Provider>;
};

function deepMerge<D>(target: Dictionary<D>, source: DeepOptional<Dictionary<D>>) {
  Object.keys(source).forEach((key: Extract<keyof D, string>) => {
    const sourceValue = source[key];

    if (!sourceValue) {
      return;
    }
    if (typeof sourceValue === "string" || typeof sourceValue === "function") {
      target[key] = sourceValue as string | DictionaryInterpolation;
    } else {
      target[key] = deepMerge(
        createObjectCopy(target[key] as Dictionary<any>),
        sourceValue as Dictionary<any>
      );
    }
  });
  return target;
}

export function immutableDeepMerge<D>(
  target: Dictionary<D>,
  source: DeepOptional<Dictionary<D>>
) {
  const targetCopy = createObjectCopy(target);
  return deepMerge(targetCopy, source);
}

function createObjectCopy<T>(obj?: T) {
  return Object.assign({} as T, obj);
}
