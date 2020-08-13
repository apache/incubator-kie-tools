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

import { en } from "./en";
import { I18n } from "@kogito-tooling/i18n/dist/common";

export const KOGITO_JIRA_LINK = "https://issues.jboss.org/projects/KOGITO";

export const kieBcEditorsI18nDefaults = { locale: "en", dictionary: en };
export const kieBcEditorsI18nDictionaries = new Map([["en", en]]);

export const kieBcEditorsI18n = new I18n(kieBcEditorsI18nDefaults, kieBcEditorsI18nDictionaries);
