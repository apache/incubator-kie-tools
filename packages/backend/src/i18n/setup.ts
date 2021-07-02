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

import { I18nDefaults, I18nDictionaries } from "@kie-tooling-core/i18n/dist/core";
import { BackendI18n } from "./BackendI18n";
import { en } from "./locales";

export const backendI18nDefaults: I18nDefaults<BackendI18n> = { locale: "en", dictionary: en };
export const backendI18nDictionaries: I18nDictionaries<BackendI18n> = new Map([["en", en]]);
