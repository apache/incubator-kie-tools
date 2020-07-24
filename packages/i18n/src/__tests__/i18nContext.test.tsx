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
import { I18nProvider, immutableDeepMerge } from "../i18nProvider";
import { DeepOptional, TranslationBundle, TranslationBundleInterpolation } from "../types";
import { render } from "@testing-library/react";
import { useTranslation } from "../hook";

interface DummyBundle extends TranslationBundle<DummyBundle> {
  greeting: (name: string) => string;
  welcome: string;
  modal: {
    title: string;
    text: string;
  };
}

const interpolationFunction: TranslationBundleInterpolation = (name: string) => `Hi ${name}!`;
const dummyDefault: DummyBundle = {
  greeting: interpolationFunction,
  welcome: "Welcome",
  modal: {
    title: "My title",
    text: "My text"
  }
};

function DummyComponent() {
  const { i18n } = useTranslation<DummyBundle>();
  return <p data-testid="dummy-component">{JSON.stringify(i18n)}</p>;
}

describe("I18nProvider", () => {
  describe("I18nProvider::component", () => {
    it("should have the same dictionary as the dummy", () => {
      const dictionaries = new Map([["en", dummyDefault]]);

      const { getByTestId } = render(
        <I18nProvider defaults={{ locale: "en", dictionary: dummyDefault }} dictionaries={dictionaries}>
          <DummyComponent />
        </I18nProvider>
      );

      expect(getByTestId("dummy-component")).toHaveTextContent(JSON.stringify(dummyDefault));
    });

    it("should use the provided default dictionary due to `en` dictionary doesn't exist", () => {
      const dictionaries = new Map([["en-US", dummyDefault]]);

      const { getByTestId } = render(
        <I18nProvider defaults={{ locale: "en", dictionary: dummyDefault }} dictionaries={dictionaries}>
          <DummyComponent />
        </I18nProvider>
      );

      expect(getByTestId("dummy-component")).toHaveTextContent(JSON.stringify(dummyDefault));
    });

    it("should use the `en` dictionary due to the `en-US` doesn't exist and it is the location prefix", () => {
      const dummyOptional: DeepOptional<DummyBundle> = {
        welcome: "Welcome!!!"
      };

      const dictionaries = new Map([
        ["en", dummyOptional],
        ["en-UK", dummyDefault]
      ]);

      const { getByTestId } = render(
        <I18nProvider defaults={{ locale: "en-US", dictionary: dummyDefault }} dictionaries={dictionaries}>
          <DummyComponent />
        </I18nProvider>
      );

      expect(getByTestId("dummy-component")).toHaveTextContent(
        JSON.stringify(immutableDeepMerge(dummyDefault, dummyOptional))
      );
    });
  });

  describe("I18nProvider::mergeSelectedDictionaryWithDefault", () => {
    it("should override the welcome property on dummyDefault and create a new object", () => {
      const dummyOptional: DeepOptional<DummyBundle> = {
        welcome: "Bienvenido"
      };

      const merged = immutableDeepMerge(dummyDefault, dummyOptional);

      expect(merged).toEqual({
        greeting: interpolationFunction,
        welcome: "Bienvenido",
        modal: {
          title: "My title",
          text: "My text"
        }
      });
      expect(dummyDefault).toEqual({
        greeting: interpolationFunction,
        welcome: "Welcome",
        modal: {
          title: "My title",
          text: "My text"
        }
      });
      expect(dummyOptional).toEqual({
        welcome: "Bienvenido"
      });
    });

    it("shouldn't override the welcome property on dummyDefault", () => {
      const dummyOptional: DeepOptional<DummyBundle> = {
        welcome: undefined
      };

      const merged = immutableDeepMerge(dummyDefault, dummyOptional);

      expect(merged).toEqual({
        greeting: interpolationFunction,
        welcome: "Welcome",
        modal: {
          title: "My title",
          text: "My text"
        }
      });
      expect(dummyDefault).toEqual({
        greeting: interpolationFunction,
        welcome: "Welcome",
        modal: {
          title: "My title",
          text: "My text"
        }
      });
      expect(dummyOptional).toEqual({
        welcome: undefined
      });
    });

    it("should override the interpolation function", () => {
      const dummyInterpolationFunction = (name: string, lastLogin: number) => `Hi ${name}. Last login: ${lastLogin}`;
      const dummyOptional: DeepOptional<DummyBundle> = {
        greeting: dummyInterpolationFunction
      };

      const merged = immutableDeepMerge(dummyDefault, dummyOptional);

      expect(merged).toEqual({
        greeting: dummyInterpolationFunction,
        welcome: "Welcome",
        modal: {
          title: "My title",
          text: "My text"
        }
      });
      expect(dummyDefault).toEqual({
        greeting: interpolationFunction,
        welcome: "Welcome",
        modal: {
          title: "My title",
          text: "My text"
        }
      });
      expect(dummyOptional).toEqual({
        greeting: dummyInterpolationFunction
      });
    });

    it("should override the nested properties that were specified", () => {
      const dummyOptional: DeepOptional<DummyBundle> = {
        welcome: "Bienvenido",
        modal: {
          title: "Mi título"
        }
      };

      const merged = immutableDeepMerge(dummyDefault, dummyOptional);

      expect(merged).toEqual({
        greeting: interpolationFunction,
        welcome: "Bienvenido",
        modal: {
          title: "Mi título",
          text: "My text"
        }
      });
      expect(dummyDefault).toEqual({
        greeting: interpolationFunction,
        welcome: "Welcome",
        modal: {
          title: "My title",
          text: "My text"
        }
      });
      expect(dummyOptional).toEqual({
        welcome: "Bienvenido",
        modal: {
          title: "Mi título"
        }
      });
    });

    it("shouldn't override the nested object with a undefined value", () => {
      const dummyOptional: DeepOptional<DummyBundle> = {
        modal: {
          title: undefined
        }
      };

      const merged = immutableDeepMerge(dummyDefault, dummyOptional);

      expect(merged).toEqual({
        greeting: interpolationFunction,
        welcome: "Welcome",
        modal: {
          title: "My title",
          text: "My text"
        }
      });
      expect(dummyDefault).toEqual({
        greeting: interpolationFunction,
        welcome: "Welcome",
        modal: {
          title: "My title",
          text: "My text"
        }
      });
      expect(dummyOptional).toEqual({
        modal: {
          title: undefined
        }
      });
    });
  });
});
