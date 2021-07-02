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

import { I18nDefaults, I18nDictionaries, ReferenceDictionary, TranslatedDictionary } from "./Dictionary";
import { immutableDeepMerge } from "./immutableDeepMerge";

export class I18n<D extends ReferenceDictionary> {
  private locale: string;
  private dictionary: D;

  constructor(
    private readonly defaults: I18nDefaults<D>,
    private readonly dictionaries: I18nDictionaries<D>,
    private readonly initialLocale = defaults.locale
  ) {
    this.locale = initialLocale;
    this.updateDictionary();
  }

  public setLocale(locale: string): void {
    this.locale = locale;
    this.updateDictionary();
  }

  private updateDictionary() {
    const selectedDictionary =
      this.dictionaries.get(this.locale) ?? this.dictionaries.get(this.locale.split("-").shift()!) ?? {};

    this.dictionary = immutableDeepMerge(this.defaults.dictionary, selectedDictionary) as D;
  }

  public getCurrent(): D {
    return this.dictionary;
  }

  public getLocale(): string {
    return this.locale;
  }
}
