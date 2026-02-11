/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * License); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing;
 * software distributed under the License is distributed on an
 * AS IS BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { useContext } from "react";
import { en } from "./locales";
import { I18nContextType } from "@kie-tools-core/i18n/dist/react-components";
import { BpmnEditorI18n } from "./BpmnEditorI18n";
import { I18nDefaults, I18nDictionaries } from "@kie-tools-core/i18n/dist/core";

export const bpmnEditorI18nDefaults: I18nDefaults<BpmnEditorI18n> = {
  locale: "en",
  dictionary: en,
};
export const bpmnEditorDictionaries: I18nDictionaries<BpmnEditorI18n> = new Map([["en", en]]);
export const BpmnEditorI18nContext = React.createContext<I18nContextType<BpmnEditorI18n>>({} as never);

export function useBpmnEditorI18n(): I18nContextType<BpmnEditorI18n> {
  return useContext(BpmnEditorI18nContext);
}
