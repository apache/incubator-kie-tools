/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import { DictionaryInterpolation, ReferenceDictionary, TranslatedDictionary } from "./Dictionary";

function deepMerge<D>(target: ReferenceDictionary, source: TranslatedDictionary<ReferenceDictionary>) {
  Object.keys(source).forEach((key: Extract<keyof D, string>) => {
    const sourceValue = source[key];

    if (!sourceValue) {
      return;
    }
    if (typeof sourceValue === "string" || typeof sourceValue === "function") {
      target[key] = sourceValue as string | DictionaryInterpolation;
    } else {
      target[key] = deepMerge(createObjectCopy(target[key] as ReferenceDictionary), sourceValue as ReferenceDictionary);
    }
  });
  return target;
}

export function immutableDeepMerge<D extends ReferenceDictionary>(
  target: ReferenceDictionary,
  source: TranslatedDictionary<ReferenceDictionary>
) {
  const targetCopy = createObjectCopy(target);
  return deepMerge(targetCopy, source);
}

function createObjectCopy<T extends Record<string, unknown>>(obj?: T) {
  return Object.assign({} as T, obj);
}
