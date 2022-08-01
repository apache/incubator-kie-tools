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

import { YardEditorI18n } from "..";
import { en as en_common } from "@kie-tools/i18n-common-dictionary";

export const en: YardEditorI18n = {
  ...en_common,
  decisionElementsTab: {
    emptyStateBody: "Your yard file doesn't have any Decision element. Please add a new element",
    emptyStateTitle: "No decision elements",
    tabTitle: "Decision Elements",
  },
  decisionInputsTab: {
    emptyStateBody: "Your yard file doesn't have any Decision input. Please add a new input",
    emptyStateTitle: "No decision input",
    name: "Name",
    tabTitle: "Decision Inputs",
    type: "Type",
  },
  generalTab: {
    expressionLang: "Expression language version",
    kind: "Type",
    name: "Name",
    specVersion: "Specification version",
    tabTitle: "General",
  },
};
