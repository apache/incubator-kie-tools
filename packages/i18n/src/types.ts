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

// Interpolation function
export type TranslationBundleInterpolation = (...args: Array<string | number>) => string;

// The Bundle type
export type TranslationBundle<Bundle> = {
  [K in keyof Bundle]: string | TranslationBundleInterpolation | TranslationBundle<any>;
};

// Locales that isn't the default should implement this interface
export type DeepOptional<TBundle> = {
  [K in keyof TBundle]?: DeepOptional<TBundle[K]>;
};
