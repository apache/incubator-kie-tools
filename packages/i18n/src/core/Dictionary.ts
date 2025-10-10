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

import { Wrapped } from "./Wrapped";

export interface I18nDefaults<D extends ReferenceDictionary<D>> {
  locale: string;
  dictionary: D;
}

export type I18nDictionaries<D extends ReferenceDictionary<D>> = Map<string, TranslatedDictionary<D>>;

export type DictionaryInterpolation = (...args: Array<string | number>) => string;

export type ReferenceDictionary<D extends ReferenceDictionary<D>> = {
  [K in keyof D]: D[K] extends string
    ? string
    : D[K] extends (...args: any[]) => string
      ? DictionaryInterpolation
      : D[K] extends Wrapped<string>
        ? Wrapped<string>
        : D[K] extends Array<string | number | Wrapped<string>>
          ? Array<string | number | Wrapped<string>>
          : D[K] extends Record<string, any>
            ? ReferenceDictionary<D[K]>
            : never;
};

// Locales that aren't the default should implement this interface
export type TranslatedDictionary<D extends ReferenceDictionary<D>> = DeepOptional<D>;

type DeepOptional<D> = { [K in keyof D]?: DeepOptional<D[K]> };
