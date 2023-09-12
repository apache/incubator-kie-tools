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

import { ReferenceDictionary } from "@kie-tools-core/i18n/dist/core";
import { CommonI18n } from "@kie-tools/i18n-common-dictionary";

interface YardEditorDictionary extends ReferenceDictionary {
  decisionElementsTab: {
    emptyStateTitle: string;
    emptyStateBody: string;
    tabTitle: string;
  };
  decisionInputsTab: {
    emptyStateTitle: string;
    emptyStateBody: string;
    name: string;
    tabTitle: string;
    type: string;
  };
  generalTab: {
    expressionLang: string;
    kind: string;
    name: string;
    specVersion: string;
    tabTitle: string;
  };
}

export interface YardEditorI18n extends YardEditorDictionary, CommonI18n {}
