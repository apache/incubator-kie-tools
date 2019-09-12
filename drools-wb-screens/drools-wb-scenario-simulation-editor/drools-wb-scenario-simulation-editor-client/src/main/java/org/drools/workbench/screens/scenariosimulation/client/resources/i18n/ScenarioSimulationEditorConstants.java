/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.scenariosimulation.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * ScenarioSimulationEditor I18N constants
 */
public interface ScenarioSimulationEditorConstants
        extends
        Messages {

    ScenarioSimulationEditorConstants INSTANCE = GWT.create(ScenarioSimulationEditorConstants.class);

    String newScenarioSimulationDescription();

    String factColumnHeader();

    String fieldColumnHeader();

    String contextColumnHeader();

    String addScenarioSimulation();

    String scenarioSimulationEditorTitle();

    String scenarioSimulationResourceTypeDescription();

    String remove();

    String invalidDefinitionDisabled();

    String testTools();

    String testReport();

    String testEditor();

    String scenarioCheatSheet();

    String ruleCheatSheet1();

    String ruleCheatSheet2();

    String ruleCheatSheet3();

    String ruleCheatSheet4();

    String ruleCheatSheet5();

    String ruleCheatSheet6();

    String ruleCheatSheet7();

    String ruleCheatSheet8();

    String ruleCheatSheet9();

    String ruleCheatSheet10();

    String ruleCheatSheet11();

    String ruleCheatSheet12();

    String ruleCheatSheet13();

    String ruleCheatSheet14();

    String ruleCheatSheet15();

    String ruleCheatSheet16();

    String ruleCheatSheet17();

    String ruleCheatSheet18();

    String ruleCheatSheet19();

    String or();

    String ruleCheatSheet20();

    String ruleCheatSheet21();

    String ruleCheatSheet22();

    String dmnCheatSheet1();

    String dmnCheatSheet2();

    String dmnCheatSheet3();

    String dmnCheatSheet4();

    String dmnCheatSheet5();

    String dmnCheatSheet6();

    String dmnCheatSheet7();

    String and();

    String dmnCheatSheet8();

    String dmnCheatSheet9();

    String dmnCheatSheet10();

    String dmnCheatSheet11();

    String dmnCheatSheet12();

    String dmnCheatSheet13();

    String dmnCheatSheet14();

    String dmnCheatSheet15();

    String dmnCheatSheet16();

    String dmnCheatSheet17();

    String forExample();

    String runScenarioSimulation();

    String expect();

    String insertColumnLeft();

    String insertColumnRight();

    String deleteColumn();

    String deleteInstance();

    String duplicateInstance();

    String insertRowBelow();

    String scenario();

    String given();

    String insertRowAbove();

    String deleteRow();

    String duplicateRow();

    String runSingleScenario();

    String prependRow();

    String appendRow();

    String insertLeftmostColumn();

    String insertRightmostColumn();

    String description();

    String insertValue();

    String deleteValues();

    String dateFormatPlaceholder();

    String deleteScenarioMainTitle();

    String deleteScenarioMainQuestion();

    String deleteScenarioText1();

    String deleteScenarioTextQuestion();

    String deleteScenarioTextDanger();

    String preserveDeleteScenarioMainTitle();

    String preserveDeleteScenarioMainQuestion();

    String preserveDeleteScenarioText1();

    String preserveDeleteScenarioTextQuestion();

    String preserveDeleteScenarioTextOption1();

    String preserveDeleteScenarioTextOption2();

    String preserveValues();

    String defineValidType();

    String changeType();

    String changeTypeMainTitle();

    String changeTypeMainQuestion();

    String changeTypeText1();

    String changeTypeTextQuestion();

    String changeTypeTextDanger();

    String undo();

    String redo();

    String sourceType();

    String chooseDMN();

    String chooseValidDMNAsset();

    String removeCollectionMainTitle();

    String removeCollectionMainQuestion();

    String removeCollectionText1();

    String removeCollectionQuestion();

    String removeCollectionWarningText();

    String collectionError();

    String selectImportFile();

    String importLabel();

    String settings();

    String dmnPathErrorLabel(String dmnPath);

    String coverageReport();

    String uploadWarning();

    String importFailedMessage();

    String importErrorTitle();

    String executed();

    String notCovered();

    String decisionsEvaluated();

    String rulesFired();

    String coverageNotSupportedForRule();

    String runATestToSeeCoverageReport();

    String running();

    String simpleTypes();

    String complexTypes();

    String complexCustomInstances();

    String simpleCustomInstances();

    String reportAvailableLabel();

    String reportExecutedLabel();

    String reportCoverageLabel();

    String numberOfTimesDecisionEvaluated();

    String reportAvailableRuleLabel();

    String reportExecutedRuleLabel();

    String reportCoverageRuleLabel();

    String numberOfTimesRulesFired();

    String errorReason();

    String keep();

    String close();

    String apply();

    String errorPopoverMessageFailedWithError(String expectedValue, String errorValue);

    String errorPopoverMessageFailedWithException(String errorMsg);

    String skipSimulation();

    String noDecisionsAvailable();

    String noRulesAvailable();

    String instanceTitleAssignedError(String title);

    String validationErrorTitle();

    String validationErrorMessage();

    String validationFailedNotification();

    String validationSucceed();
}
