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

import { Wrapped } from "./Wrapped";

export interface I18nDefaults<D extends ReferenceDictionary> {
  locale: string;
  dictionary: D;
}

export type I18nDictionaries<D extends ReferenceDictionary> = Map<string, TranslatedDictionary<D>>;

export type DictionaryInterpolation = (...args: Array<string | number>) => string;

export type ReferenceDictionary = {
  [k: string]: string | DictionaryInterpolation | Array<string | number | Wrapped<string>> | ReferenceDictionary;
};

// Locales that aren't the default should implement this interface
export type TranslatedDictionary<D extends ReferenceDictionary> = DeepOptional<D>;

type DeepOptional<D> = { [K in keyof D]?: DeepOptional<D[K]> };
