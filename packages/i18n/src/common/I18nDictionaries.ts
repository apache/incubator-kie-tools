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

import { Defaults, ReferenceDictionary, TranslatedDictionary } from "../Dictionary";
import { immutableDeepMerge } from "../immutableDeepMerge";

export class I18nDictionaries<D extends ReferenceDictionary<D>> {
  private locale: string;
  private dictionary: ReferenceDictionary<D>;

  constructor(
    private readonly defaults: Defaults<D>,
    private readonly dictionaries: Map<string, TranslatedDictionary<D>>,
    private readonly startingLocale = defaults.locale
  ) {
    if (startingLocale !== defaults.locale) {
      const startingDictionary =
        dictionaries.get(startingLocale) ?? dictionaries.get(startingLocale.split("-").shift()!) ?? {};

      this.dictionary = immutableDeepMerge(defaults.dictionary, startingDictionary);
    } else {
      this.dictionary = defaults.dictionary;
    }
  }

  public setLocale(locale: string) {
    this.locale = locale;
    if (locale !== this.defaults.locale) {
      const selectedDictionary =
        this.dictionaries.get(locale) ?? this.dictionaries.get(locale.split("-").shift()!) ?? {};

      this.dictionary = immutableDeepMerge(this.defaults.dictionary, selectedDictionary);
    } else {
      this.dictionary = this.defaults.dictionary;
    }
  }

  public getI18n() {
    return { i18n: this.dictionary };
  }

  public getLocale() {
    return this.locale;
  }
}
