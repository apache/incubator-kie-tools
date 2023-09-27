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

import { en as en_common } from "@kie-tools/i18n-common-dictionary";
import TestScenarioEditorI18n from "../TestScenarioEditorI18n";

export const en: TestScenarioEditorI18n = {
  ...en_common,
  drawer: {
    dataObject: {
      description:
        'To create a test scenario, define the "Given" and "Expect" columns by using the data object editor below.',
      title: "Data Objects tool",
    },
    cheatSheet: {
      paragraph1: "To start off, use contextual menus in the table to insert or edit or delete new columns and rows.",
      paragraph2a:
        "Before defining the individual test scenarios, verify the instance and field mappings for each column. If any changes are necessary, use the ",
      paragraph2b: " to update the mapping",
      paragraph3a: "If the same GIVEN data is shared with the multiple test scenarios, you can use the ",
      paragraph3b: " tab to define them only once. The way to create a column in the ",
      paragraph3c: " tab is the same as of the ",
      paragraph3d: " tab.",
      title: "Scenario Cheatsheet",
    },
    //(date: string) => `Created at ${date}`
    settings: {
      assetType: "Type",
      dmnModel: "DMN Model",
      dmnName: "DMN Name",
      dmnNameSpace: "DMN Namespace",
      fileName: "Name",
      kieSessionRule: "KIE Session",
      kieSessionRulePlaceholder: "(Optional) KieSession",
      kieSessionRuleTooltip: "Optional value. If not specified, the default session will be used.",
      ruleFlowGroup: "AgendaGroup/RuleFlowGroup",
      ruleFlowGroupPlaceholder: "(Optional) AgendaGroup or RuleFlowGroup.",
      ruleFlowGroupTooltip:
        "Optional value. The defined Scenario will be executed against the provided Agenda Group or RuleFlowGroup.",
      statelessSessionRule: "Stateless Session",
      statelessSessionRuleTooltip:
        "Select or clear this check box to specify if the KIE Session is stateless or not. If the current KieSession is stateless and the check box is not selected, the tests will fail.",
      testSkipped: "Skip this test scenario during the test",
      testSkippedTooltip: "If enabled, all defined Scenarios in this file will be skipped during test execution.",
      title: "Settings",
    },
  },
  sidebar: {
    cheatSheetTooltip: "CheatSheet: Useful information for Test Scenario Usage",
    dataObjectsTooltip: "Data Objects: It provides a tool to edit your Scenarios",
    settingsTooltip: "Setting: Properties of this Test Scenario asset",
  },
  tab: {
    backgroundTabTitle: "Background",
    scenarioTabTitle: "Test Scenario",
  },
};
