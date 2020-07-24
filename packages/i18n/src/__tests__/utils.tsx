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

/* istanbul ignore file */
import * as React from "react";
import { TranslationBundle, TranslationBundleInterpolation } from "../types";
import { useTranslation } from "../hook";

export interface DummyBundle extends TranslationBundle<DummyBundle> {
  greeting: (name: string) => string;
  welcome: string;
  modal: {
    title: string;
    text: string;
  };
}

export const interpolationFunction: TranslationBundleInterpolation = (name: string) => `Hi ${name}!`;
export const dummyDefault: DummyBundle = {
  greeting: interpolationFunction,
  welcome: "Welcome",
  modal: {
    title: "My title",
    text: "My text"
  }
};

export function DummyComponent() {
  const { i18n } = useTranslation<DummyBundle>();
  return <p data-testid="dummy-component">{JSON.stringify(i18n)}</p>;
}
