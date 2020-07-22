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

import { DeepOptional, TranslationBundle } from "./types";

function createObjectCopy<T>(obj?: T) {
  return Object.assign({} as T, obj);
}

function deepMerge<Bundle>(
  target: DeepOptional<TranslationBundle<Bundle>>,
  source: DeepOptional<TranslationBundle<Bundle>>
) {
  Object.keys(source).forEach((key: Extract<keyof Bundle, string>) => {
    const targetValue = target[key];
    const sourceValue = source[key]!;

    if (typeof sourceValue === ("string" || "function") || typeof targetValue === ("string" || "function")) {
      target[key] = sourceValue;
    } else {
      target[key] = deepMerge(createObjectCopy(targetValue as any), sourceValue);
    }
  });
  return target;
}

export function mergeSelectedDictionaryWithDefault<Bundle>(
  targetDictionary: DeepOptional<TranslationBundle<Bundle>>,
  selectedDictionary?: DeepOptional<TranslationBundle<Bundle>>
) {
  const targetCopy = createObjectCopy(targetDictionary);
  const sourceCopy = createObjectCopy(selectedDictionary);

  return deepMerge(targetCopy, sourceCopy);
}
