/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import { I18nContextType } from "@kie-tools-core/i18n/dist/react-components";
import { FormDmnI18n } from "./FormDmnI18n";
import { en } from "./locales";
import { I18n, I18nDefaults, I18nDictionaries } from "@kie-tools-core/i18n/dist/core";

export const formDmnI18nDefaults: I18nDefaults<FormDmnI18n> = { locale: "en", dictionary: en };
export const formDmnI18nDictionaries: I18nDictionaries<FormDmnI18n> = new Map([["en", en]]);
export const FormDmnI18nContext = React.createContext<I18nContextType<FormDmnI18n>>({} as any);

export const formDmnI18n = new I18n(formDmnI18nDefaults, formDmnI18nDictionaries);
