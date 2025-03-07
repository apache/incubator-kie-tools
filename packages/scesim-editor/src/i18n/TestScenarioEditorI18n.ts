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

import { CommonI18n } from "@kie-tools/i18n-common-dictionary";
import { ReferenceDictionary } from "@kie-tools-core/i18n/dist/core";

interface TestScenarioEditorDictionary extends ReferenceDictionary {
  alerts: {
    ruleDataNotAvailable: string;
    dmnDataRetrievedFromScesim: string;
    ruleDataRetrievedFromScesim: string;
  };
  creationPanel: {
    assetsGroup: string;
    assetsOption: {
      dmn: string;
      noChoice: string;
      rule: string;
    };
    autoFillTable: string;
    autoFillTableTooltip: string;
    createButton: string;
    dmnGroup: string;
    dmnNoChoice: string;
    dmnNoPresent: string;
    kieSessionGroup: string;
    kieAgendaGroup: string;
    optional: string;
    statelessSession: string;
    testSkip: string;
    title: string;
  };
  drawer: {
    cheatSheet: {
      expression1DMN: string;
      expression1Rule: string;
      expression2DMN: string;
      expression2Rule: string;
      expression3DMN: string;
      expression3Rule: string;
      expression4DMN: string;
      expression4Rule: string;
      expression5DMN: string;
      expression5Rule: string;
      expression6DMN: string;
      expression6Rule: string;
      expression7DMN: string;
      expression7Rule: string;
      expression8DMN: string;
      expression8Rule: string;
      expression9DMN: string;
      expression9Rule: string;
      expression10DMN: string;
      paragraph1: string;
      paragraph2: (testScenarioTab: string) => string;
      paragraph3: (backgroundTab: string, testScenarioTab: string) => string;
      paragraph4: string;
      paragraph5DMN: string;
      paragraph6DMN: string;
      paragraph6Rule: string;
      title: string;
    };
    dataSelector: {
      clearSelection: string;
      collapseAll: string;
      dataObjectsDescriptionDMN: string;
      dataObjectsDescriptionRule: string;
      descriptionDMN: string;
      descriptionRule: string;
      emptyDataObjectsTitle: string;
      emptyDataObjectsTitleDMN: string;
      emptyDataObjectsTitleRule: string;
      emptyDataObjectsDescription: string;
      emptyDataObjectsDescriptionDMN: string;
      emptyDataObjectsDescriptionRule: string;
      expandAll: string;
      insertDataObject: string;
      insertDataObjectTooltipColumnSelectionMessage: string;
      insertDataObjectTooltipDataObjectSelectionMessage: string;
      insertDataObjectTooltipDataObjectAlreadyAssignedMessage: string;
      insertDataObjectTooltipDataObjectAssignMessage: string;
      titleDMN: string;
      titleRule: string;
    };
    settings: {
      assetType: string;
      dmnModel: string;
      dmnModelReferenceError: string;
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
  errorFallBack: {
    title: string;
    body: string;
    lastActionButton: string;
    fileIssueHref: string;
  };
  sidebar: {
    cheatSheetTooltip: string;
    dataSelectorTooltip: string;
    settingsTooltip: string;
  };
  tab: {
    backgroundTabTitle: string;
    backgroundTabInfo: string;
    scenarioTabTitle: string;
    scenarioTabInfo: string;
  };
  table: {
    simulation: { singleEntry: string };
    background: {};
    copy: string;
    cut: string;
    delete: string;
    duplicate: string;
    deleteField: string;
    deleteInstance: string;
    expect: string;
    field: string;
    given: string;
    insert: string;
    insertAbove: string;
    insertBelow: string;
    insertLeftField: string;
    insertRightField: string;
    insertLeftInstance: string;
    insertRightInstance: string;
    instance: string;
    paste: string;
    reset: string;
    selection: string;
  };
}

export default interface TestScenarioEditorI18n extends TestScenarioEditorDictionary, CommonI18n {}
