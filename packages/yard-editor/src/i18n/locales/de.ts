/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { YardEditorI18n } from "..";
import { de as de_common } from "@kie-tools/i18n-common-dictionary";

export const de: YardEditorI18n = {
  ...de_common,
  decisionElementsTab: {
    emptyStateBody: "Ihre Yard-Datei enthält kein Entscheidungselement. Bitte fügen Sie ein neues Element hinzu",
    emptyStateTitle: "Keine Entscheidungselemente",
    tabTitle: "Entscheidungselemente",
  },
  decisionInputsTab: {
    emptyStateBody: "Ihre Yard-Datei enthält keine Eingaben zur Entscheidung. Bitte fügen Sie eine neue Eingabe hinzu",
    emptyStateTitle: "Kein Eingaben zur Entscheidung",
    name: "Name",
    tabTitle: "Eingaben zur Entscheidung",
    type: "Typ",
  },
  generalTab: {
    expressionLang: "Sprachversion des Ausdrucks",
    kind: "Typ",
    name: "Name",
    specVersion: "Sprachversion",
    tabTitle: "Allgemein",
  },
};
