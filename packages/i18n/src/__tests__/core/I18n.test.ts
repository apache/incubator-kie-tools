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

import { I18n, ReferenceDictionary, TranslatedDictionary } from "../../core";

interface TestI18n extends ReferenceDictionary {
  test: string;
}

const english: TestI18n = {
  test: "this is a test",
};

const portuguese: TranslatedDictionary<ReferenceDictionary> = {
  test: "isso é um teste",
};

const defaults = { locale: "en", dictionary: english };
const dictionaries = new Map([
  ["en", english],
  ["pt", portuguese],
]);

describe("I18n", () => {
  describe("new", () => {
    it("startingLocale", () => {
      const i18n = new I18n(defaults, dictionaries, "pt");
      expect(i18n.getLocale()).toEqual("pt");
      expect(i18n.getCurrent().test).toEqual(portuguese.test);
    });

    it("default locale", () => {
      const i18n = new I18n(defaults, dictionaries);
      expect(i18n.getLocale()).toEqual("en");
      expect(i18n.getCurrent().test).toEqual(english.test);
    });
  });

  it("setLocale", () => {
    const i18n = new I18n(defaults, dictionaries);
    expect(i18n.getLocale()).toEqual("en");
    expect(i18n.getCurrent().test).toEqual(english.test);

    i18n.setLocale("pt");
    expect(i18n.getLocale()).toEqual("pt");
    expect(i18n.getCurrent().test).toEqual(portuguese.test);
  });
});
