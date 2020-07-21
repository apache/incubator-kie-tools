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

interface ObjectType {
  [x: string]: any;
}
const isObject = (obj?: ObjectType) => obj && typeof obj === "object";

function createDeepOptionalTranslationBundleEmptyObject<Bundle>(obj?: DeepOptional<TranslationBundle<Bundle>>) {
  return Object.assign({} as DeepOptional<TranslationBundle<Bundle>>, obj);
}

export function deepMerge<Bundle extends ObjectType>(
  target: DeepOptional<TranslationBundle<Bundle>>,
  source?: DeepOptional<TranslationBundle<Bundle>>
) {
  const targetCopy = createDeepOptionalTranslationBundleEmptyObject(target);
  const sourceCopy = createDeepOptionalTranslationBundleEmptyObject(source);

  Object.keys(sourceCopy).forEach((key: keyof Bundle) => {
    const targetValue = targetCopy[key];
    const sourceValue = sourceCopy[key];

    if (isObject(sourceValue)) {
      targetCopy[key] = deepMerge(createDeepOptionalTranslationBundleEmptyObject(targetValue), sourceValue);
    } else {
      targetCopy[key] = sourceValue;
    }
  });

  return targetCopy;
}
