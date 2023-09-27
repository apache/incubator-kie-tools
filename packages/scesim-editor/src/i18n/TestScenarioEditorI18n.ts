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

interface TestScenarioEditorDictionary extends ReferenceDictionary {
  drawer: {
    dataObject: {
      description: string;
      title: string;
    };
    cheatSheet: {
      paragraph1: string;
      paragraph2a: string;
      paragraph2b: string;
      paragraph3: string;
      title: string;
    };
    settings: {
      assetType: string;
      dmnModel: string;
      dmnName: string;
      dmnNameSpace: string;
      fileName: string;
      kieSessionRule: string;
      kieSessionRulePlaceholder: string;
      kieSessionRuleTooltip: string;
      ruleFlowGroup: string;
      ruleFlowGroupPlaceholder: string;
      ruleFlowGroupTooltip: string;
      statelessSessionRule: string;
      statelessSessionRuleTooltip: string;
      testSkipped: string;
      testSkippedTooltip: string;
      title: string;
    };
  };
  sidebar: {
    cheatSheetTooltip: string;
    dataObjectsTooltip: string;
    settingsTooltip: string;
  };
  tab: {
    backgroundTabTitle: string;
    scenarioTabTitle: string;
  };
}

export default interface TestScenarioEditorI18n extends TestScenarioEditorDictionary, CommonI18n {}
