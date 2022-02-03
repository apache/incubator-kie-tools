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
import { useContext } from "react";
import { en } from "./locales";
import { I18nContextType } from "@kie-tools-core/i18n/dist/react-components";
import { BoxedExpressionEditorI18n } from "./BoxedExpressionEditorI18n";
import { I18nDefaults, I18nDictionaries } from "@kie-tools-core/i18n/dist/core";

export const boxedExpressionEditorI18nDefaults: I18nDefaults<BoxedExpressionEditorI18n> = {
  locale: "en",
  dictionary: en,
};
export const boxedExpressionEditorDictionaries: I18nDictionaries<BoxedExpressionEditorI18n> = new Map([["en", en]]);
export const BoxedExpressionEditorI18nContext = React.createContext<I18nContextType<BoxedExpressionEditorI18n>>(
  {} as never
);

export function useBoxedExpressionEditorI18n(): I18nContextType<BoxedExpressionEditorI18n> {
  return useContext(BoxedExpressionEditorI18nContext);
}
