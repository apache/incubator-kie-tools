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
import { useTranslation } from "../hook";
import { DummyBundle, dummyDefault } from "./utils";
import { renderHook } from "@testing-library/react-hooks";
import { I18nProvider, immutableDeepMerge } from "../i18nProvider";
import { DeepOptional } from "../types";
import { act } from "react-test-renderer";

const renderHookWithI18nProvider = (wrapperProps: {
  locale: string;
  dictionary: DummyBundle;
  dictionaries: Map<string, DeepOptional<DummyBundle>>;
}) => {
  const { result } = renderHook(() => useTranslation<DummyBundle>(), {
    wrapper: props => (
      <I18nProvider
        defaults={{ locale: wrapperProps.locale, dictionary: wrapperProps.dictionary }}
        dictionaries={wrapperProps.dictionaries}
        children={<React.Fragment />}
        {...props}
      />
    )
  });
  return { result };
};

describe("useTranslation", () => {
  it("should be returned the default dictionary and the actual locale", () => {
    const dictionaries = new Map([["en-US", dummyDefault]]);

    const { result } = renderHookWithI18nProvider({ locale: "en", dictionary: dummyDefault, dictionaries });
    expect(result.current.i18n).toEqual(dummyDefault);
    expect(result.current.locale).toEqual("en");
  });

  it("should change the dictionary with the setLocale function", () => {
    const dummyOptional: DeepOptional<DummyBundle> = {
      welcome: "Bem vindo!"
    };

    const dictionaries = new Map([
      ["en-US", dummyDefault],
      ["pt-BR", dummyOptional]
    ]);

    const { result } = renderHookWithI18nProvider({ locale: "en", dictionary: dummyDefault, dictionaries });
    expect(result.current.i18n).toEqual(dummyDefault);
    expect(result.current.locale).toEqual("en");

    act(() => {
      result.current.setLocale("pt-BR");
    });

    expect(result.current.i18n).toEqual(immutableDeepMerge(dummyDefault, dummyOptional));
    expect(result.current.locale).toEqual("pt-BR");
  });
});
