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

interface LocaleDictionary<T> { [x: string]: DeepOptional<TranslationBundle<T>> }

export class Dictionary<T> {
  private dictionary: Map<string, DeepOptional<TranslationBundle<T>>>;

  constructor() {
    this.dictionary = new Map<string, DeepOptional<TranslationBundle<T>>>();
  }

  public set(...dictionaries: Array<LocaleDictionary<T>>) {
    dictionaries.forEach(dictionary => {
      const key = Object.keys(dictionary)[0]
      this.dictionary.set(key, dictionary[key])
    })
  }

  public get(key: string) {
    return this.dictionary.get(key)
  }
}
