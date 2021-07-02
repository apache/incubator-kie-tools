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

import * as React from "react";
import { I18nDictionariesProvider } from "@kie-tooling-core/i18n/dist/react-components";
import { immutableDeepMerge } from "@kie-tooling-core/i18n/dist/core/immutableDeepMerge";
import { TranslatedDictionary } from "@kie-tooling-core/i18n/dist/core";
import { render } from "@testing-library/react";
import { DummyContext, DummyDictionary, DummyComponent, dummyDefault, interpolationFunction } from "../utils";

describe("I18nDictionariesProvider", () => {
  describe("I18nDictionariesProvider::component", () => {
    it("should have the same dictionary as the dummy", () => {
      const dictionaries = new Map([["en", dummyDefault]]);

      const { getByTestId } = render(
        <I18nDictionariesProvider
          defaults={{ locale: "en", dictionary: dummyDefault }}
          dictionaries={dictionaries}
          ctx={DummyContext}
        >
          <DummyComponent />
        </I18nDictionariesProvider>
      );

      expect(getByTestId("dummy-component")).toHaveTextContent(JSON.stringify(dummyDefault));
    });

    it("should use the provided default dictionary due to `en` dictionary doesn't exist", () => {
      const dictionaries = new Map([["en-US", dummyDefault]]);

      const { getByTestId } = render(
        <I18nDictionariesProvider
          defaults={{ locale: "en", dictionary: dummyDefault }}
          dictionaries={dictionaries}
          ctx={DummyContext}
        >
          <DummyComponent />
        </I18nDictionariesProvider>
      );

      expect(getByTestId("dummy-component")).toHaveTextContent(JSON.stringify(dummyDefault));
    });

    it("should use the `en` dictionary due to the `en-US` doesn't exist and 'en' is the location prefix", () => {
      const dummyOptional: TranslatedDictionary<DummyDictionary> = {
        welcome: "Welcome!!!",
      };

      const dictionaries = new Map([
        ["en", dummyOptional],
        ["en-UK", dummyDefault],
      ]);

      const { getByTestId } = render(
        <I18nDictionariesProvider
          defaults={{ locale: "en-US", dictionary: dummyDefault }}
          dictionaries={dictionaries}
          ctx={DummyContext}
        >
          <DummyComponent />
        </I18nDictionariesProvider>
      );

      expect(getByTestId("dummy-component")).toHaveTextContent(
        JSON.stringify(immutableDeepMerge(dummyDefault, dummyOptional))
      );
    });
  });

  describe("I18nDictionariesProvider::mergeSelectedDictionaryWithDefault", () => {
    it("should override the welcome property on dummyDefault and create a new object", () => {
      const dummyOptional: TranslatedDictionary<DummyDictionary> = {
        welcome: "Bienvenido",
      };

      const merged = immutableDeepMerge(dummyDefault, dummyOptional);

      expect(merged).toEqual({
        greeting: interpolationFunction,
        welcome: "Bienvenido",
        modal: {
          title: "My title",
          text: "My text",
        },
      });
      expect(dummyDefault).toEqual({
        greeting: interpolationFunction,
        welcome: "Welcome",
        modal: {
          title: "My title",
          text: "My text",
        },
      });
      expect(dummyOptional).toEqual({
        welcome: "Bienvenido",
      });
    });

    it("shouldn't override the welcome property on dummyDefault", () => {
      const dummyOptional: TranslatedDictionary<DummyDictionary> = {
        welcome: undefined,
      };

      const merged = immutableDeepMerge(dummyDefault, dummyOptional);

      expect(merged).toEqual({
        greeting: interpolationFunction,
        welcome: "Welcome",
        modal: {
          title: "My title",
          text: "My text",
        },
      });
      expect(dummyDefault).toEqual({
        greeting: interpolationFunction,
        welcome: "Welcome",
        modal: {
          title: "My title",
          text: "My text",
        },
      });
      expect(dummyOptional).toEqual({
        welcome: undefined,
      });
    });

    it("should override the interpolation function", () => {
      const dummyInterpolationFunction = (name: string, lastLogin: number) => `Hi ${name}. Last login: ${lastLogin}`;
      const dummyOptional: TranslatedDictionary<DummyDictionary> = {
        greeting: dummyInterpolationFunction,
      };

      const merged = immutableDeepMerge(dummyDefault, dummyOptional);

      expect(merged).toEqual({
        greeting: dummyInterpolationFunction,
        welcome: "Welcome",
        modal: {
          title: "My title",
          text: "My text",
        },
      });
      expect(dummyDefault).toEqual({
        greeting: interpolationFunction,
        welcome: "Welcome",
        modal: {
          title: "My title",
          text: "My text",
        },
      });
      expect(dummyOptional).toEqual({
        greeting: dummyInterpolationFunction,
      });
    });

    it("should override the nested properties that were specified", () => {
      const dummyOptional: TranslatedDictionary<DummyDictionary> = {
        welcome: "Bienvenido",
        modal: {
          title: "Mi título",
        },
      };

      const merged = immutableDeepMerge(dummyDefault, dummyOptional);

      expect(merged).toEqual({
        greeting: interpolationFunction,
        welcome: "Bienvenido",
        modal: {
          title: "Mi título",
          text: "My text",
        },
      });
      expect(dummyDefault).toEqual({
        greeting: interpolationFunction,
        welcome: "Welcome",
        modal: {
          title: "My title",
          text: "My text",
        },
      });
      expect(dummyOptional).toEqual({
        welcome: "Bienvenido",
        modal: {
          title: "Mi título",
        },
      });
    });

    it("shouldn't override the nested object with a undefined value", () => {
      const dummyOptional: TranslatedDictionary<DummyDictionary> = {
        modal: {
          title: undefined,
        },
      };

      const merged = immutableDeepMerge(dummyDefault, dummyOptional);

      expect(merged).toEqual({
        greeting: interpolationFunction,
        welcome: "Welcome",
        modal: {
          title: "My title",
          text: "My text",
        },
      });
      expect(dummyDefault).toEqual({
        greeting: interpolationFunction,
        welcome: "Welcome",
        modal: {
          title: "My title",
          text: "My text",
        },
      });
      expect(dummyOptional).toEqual({
        modal: {
          title: undefined,
        },
      });
    });
  });
});
