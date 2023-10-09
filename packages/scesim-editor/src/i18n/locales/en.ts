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
  alerts: {
    dmnDataNotAvailable:
      "It's still not possibile to retrieve the DMN information from your DMN file. Work in progress.",
    ruleDataNotAvailable:
      "It's not possibile to retrieve the Java Classes from your project. This feature is still not supported.",
    dmnDataRetrievedFromScesim:
      "Impossibile to retrieve DMN Nodes from the given DMN file, therefore they have been restored from the scesim file. These data might be NOT synchronized. You can view and edit this asset, but dropping a column could lose its related DMN Node data.",
    ruleDataRetrievedFromScesim:
      "Impossibile to retrieve the Java Classes from your project, therefore they have been restored from the scesim file. These data might be NOT synchronized. You can view and edit this asset, but dropping a column could lose its related Java Class data.",
  },
  drawer: {
    cheatSheet: {
      paragraph1: "To start off, use contextual menus in the table to insert or edit or delete new columns and rows.",
      paragraph2: (dataObjectsDrawer: string) =>
        `Before defining the individual test scenarios, verify the instance and field mappings for each column. If any changes are necessary, use the ${dataObjectsDrawer} to update the mapping.`,
      paragraph3: (backgroundTab: string, testScenarioTab: string) =>
        `If the same GIVEN data is shared with the multiple test scenarios, you can use the ${backgroundTab} tab to define them only once. The way to create a column in the ${backgroundTab} tab is the same as of the ${testScenarioTab} tab.`,
      paragraph4: "Now, define the test scenarios. Each cell of the table must contain a valid expression.",
      paragraph5DMN:
        "Values in the GIVEN part of the table are evaluated as literal expressions, whereas values in the EXPECT section are evaluated as unary expressions. The correctness of these unary tests is verified using the results from a DMN evaluation.",
      paragraph6DMN: "DMN-based scenarios use the FEEL expression language. Supported data types are:",
      paragraph6Rule: "In rule-based scenarios, the following expressions are supported:",
      expression1DMN:
        'numbers and strings (strings must be delimited by quotation marks), for example "John Doe" or ""',
      expression1Rule:
        "= (or no symbol) specifies equality of a value. This is the default operator of each column and the only operator that is supported in the GIVEN section of the table.",
      expression2DMN: "boolean values (true, false, and null)",
      expression2Rule: "!, !=, <> specify inequality of a value. This operator can be combined with other operators.",
      expression3DMN: 'dates and time, for example date("2019-05-13") or time("14:10:00+02:00")',
      expression3Rule:
        "<, >, <=, >= specify a comparison: less than, greater than, less or equals than, and greater or equals than.",
      expression4DMN: 'Days and time duration, for example duration("P1DT23H12M30S")',
      expression4Rule:
        "[value1, value2, value3] specifies a list of values. If one or more values are valid, the scenario definition is evaluated as true.",
      expression5DMN: 'Years and months duration, for example duration("P3Y5M")',
      expression5Rule:
        "expression1; expression2; expression3 specifies a list of expressions. If all expressions are valid, the scenario definition is evaluated as true.",
      expression6DMN: "functions",
      expression6Rule: "To define an empty string, use =, [], or ;",
      expression7DMN: 'contexts, for example {x : 5, even : false, type : "integer number"}',
      expression7Rule: "To define a null value, use null.",
      expression8DMN: "ranges and lists, for example [1 .. 10] or [2, 3, 4, 5]",
      expression8Rule: "An empty cell is skipped from the evaluation.",
      expression9DMN:
        "In EXPECT section the type could also just a boolean with the result of the assertion and you can use ? identifier to access value to check.",
      expression9Rule:
        "To specify a MVEL expression (or invoke a Java method) just put # at the beginning of the expression. In GIVEN section, return type of the expression has to be the same one of type of the column. In EXPECT section the type could also just a boolean with the result of the assertion and you can use actualValue identifier to access value to check.",
      expression10DMN: "An empty cell is skipped from the evaluation.",
      title: "Cheatsheet",
    },
    dataObjects: {
      clearSelection: "Clear selection",
      collapseAll: "Collapse all",
      dataObjectsDescriptionDMN: "DMN Nodes are Input or Decision nodes defined in your DMN asset.",
      dataObjectsDescriptionRule: "Java Classes are the required Input or Output data by your DRL asset.",
      descriptionDMN:
        "To edit a test scenario definition, select a grid's column and assign it a DMN Node attribute using the below selector",
      descriptionRule:
        "To edit a test scenario definition, select a grid's column and assign it a Java Class field using the below selector",
      emptyDataObjectsTitleDMN: "No DMN Nodes",
      emptyDataObjectsTitleRule: "No Java Classes",
      emptyDataObjectsDescriptionDMN: "Impossible to retrieve the DMN Nodes data from the linked DMN file.",
      emptyDataObjectsDescriptionRule: "Impossible to retrieve the Java Classes from project.",
      expandAll: "Expand all",
      insertDataObject: "Assign",
      titleDMN: "DMN Nodes selector",
      titleRule: "Java Classes selector",
    },
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
    dataObjectsTooltip: "Selector: It provides a tool to edit your Scenarios",
    settingsTooltip: "Setting: Properties of this Test Scenario asset",
  },
  tab: {
    backgroundTabTitle: "Background",
    scenarioTabTitle: "Test Scenario",
  },
};
