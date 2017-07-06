/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * Explorer I18N constants
 */
public interface GuidedDecisionTableConstants
        extends
        Messages {

    GuidedDecisionTableConstants INSTANCE = GWT.create(GuidedDecisionTableConstants.class);
    String COLON = ":";

    //Decision Table
    String NoPatternBindingsAvailable();

    String OtherwiseCellLabel();

    String Description();

    String Analysis();

    String negatedPattern();

    String Delete();

    String YouMustEnterAColumnHeaderValueDescription();

    String ThatColumnNameIsAlreadyInUsePleasePickAnother();

    String ColumnHeaderDescription();

    String HideThisColumn();

    String Choose();

    String Pattern();

    String Field();

    String ValueList();

    String ValueListsExplanation();

    String optionalValueList();

    String DefaultValue();

    String LimitedEntryValue();

    String LogicallyInsert();

    String OK();

    String FactType();

    String FieldType();

    String Binding();

    String LogicallyAssertAFactTheFactWillBeDeletedWhenTheSupportingEvidenceIsRemoved();

    String Edit();

    String EditDisabled();

    String Fact();

    String UpdateEngineWithChanges();

    String UpdateFact();

    String UpdateDescription();

    String WorkItemNameColon();

    String WorkItemParameterNameColon();

    String LiteralValue();

    String Formula();

    String Predicate();

    String CalculationType();

    String Operator();

    String DTLabelOverCEPWindow();

    String DTLabelFromEntryPoint();

    String Predicates();

    String PredicatesInfo();

    String Options();

    String EditThisActionColumnConfiguration();

    String DeleteActionColumnWarning(String p0);

    String NewColumn();

    String Metadata1();

    String Attribute();

    String EditThisColumnsConfiguration();

    String UnableToDeletePatterns();

    String UnableToDeleteConditionColumns();

    String UnableToDeleteConditionColumn0(String p0);

    String DeleteConditionColumnWarning0(String p0);

    String UseRowNumber();

    String ReverseOrder();

    //Audit Log
    String DecisionTableAuditLog();

    String DecisionTableAuditLogEvents();

    String DecisionTableAuditLogEventDeleteColumn();

    String DecisionTableAuditLogEventDeleteRow();

    String DecisionTableAuditLogEventInsertColumn();

    String DecisionTableAuditLogEventUpdateColumn();

    String DecisionTableAuditLogEventInsertRow();

    String AuditLogEntryOn1(String date,
                            String who);

    String DecisionTableAuditLogInsertRowAt0(int index);

    String DecisionTableAuditLogDeleteRowAt0(int index);

    String DecisionTableAuditLogDeleteColumn0(String header);

    String DecisionTableAuditLogInsertAttribute0(String attribute);

    String DecisionTableAuditLogInsertMetadata0(String metadata);

    String DecisionTableAuditLogInsertCondition0(String conditionHeader);

    String DecisionTableAuditLogInsertActionInsertFact0(String actionInsertFactHeader);

    String DecisionTableAuditLogInsertActionSetField0(String actionSetFieldHeader);

    String DecisionTableAuditLogInsertColumn0(String header);

    String DecisionTableAuditLogInsertWorkItemExecuteColumn0(String header);

    String DecisionTableAuditLogInsertWorkItemInsertFactColumn0(String header);

    String DecisionTableAuditLogInsertWorkItemSetFieldColumn0(String header);

    String DecisionTableAuditLogWorkItemName();

    String DecisionTableAuditLogWorkItemParameterName();

    String DecisionTableAuditLogWorkItemParameterValue();

    String DecisionTableAuditLogWorkItemParameterValueOnly0(String value);

    String DecisionTableAuditLogWorkItemParameterClassName();

    String DecisionTableAuditLogNoEntries();

    String Value();

    String BoundVariable();

    String DecisionTableAuditLogUpdateCondition(String value);

    String DecisionTableAuditLogUpdateAction(String value);

    String DecisionTableAuditLogUpdateColumn(String value);

    String DecisionTableAuditLogUpdateAttribute(String value);

    String AreYouSureYouWantToRemoveThisItem();

    String InsertYourCommentsHere();

    String ColumnHeader();

    //Wizard
    String UseWizardToBuildAsset();

    String DecisionTableWizard();

    String DecisionTableWizardSummary();

    String DecisionTableWizardFactPatterns();

    String DecisionTableWizardFactPatternConstraints();

    String DecisionTableWizardNoAvailablePatterns();

    String DecisionTableWizardNoChosenPatterns();

    String DecisionTableWizardAvailableTypes();

    String DecisionTableWizardChosenTypes();

    String DecisionTableWizardDuplicateBindings();

    String DecisionTableWizardAvailableFields();

    String DecisionTableWizardChosenConditions();

    String DecisionTableWizardNoAvailableFields();

    String DecisionTableWizardNoChosenFields();

    String DecisionTableWizardIncompleteConditions();

    String DecisionTableWizardNameAlreadyInUse();

    String DecisionTableWizardPredicate();

    String DecisionTableWizardPredicateExpression();

    String DecisionTableWizardActionSetFields();

    String DecisionTableWizardChosenFields();

    String DecisionTableWizardActionInsertFacts();

    String DecisionTableWizardIncompleteActions();

    String DecisionTableWizardSummaryInvalidName();

    String DecisionTableWizardColumnExpansion();

    String DecisionTableWizardAvailableColumns();

    String DecisionTableWizardChosenColumns();

    String DecisionTableWizardNoAvailableColumns();

    String DecisionTableWizardNoChosenColumns();

    String DecisionTableWizardDescriptionSummaryPage();

    String DecisionTableWizardDescriptionFactPatternsPage();

    String DecisionTableWizardDescriptionFactPatternConstraintsPage();

    String DecisionTableWizardDescriptionActionSetFieldsPage();

    String DecisionTableWizardDescriptionActionInsertFactFieldsPage();

    String DecisionTableWizardDescriptionExpandColumnsPage();

    String DecisionTableWizardExpandInFull();

    String DecisionTableWizardImports();

    String DecisionTableWizardDescriptionImportsPage();

    String DecisionTableWizardNoAvailableImports();

    String DecisionTableWizardNoChosenImports();

    String DecisionTableWizardAvailableImports();

    String DecisionTableWizardChosenImports();

    String DecisionTableWizardCannotRemoveImport();

    String BindingFact();

    String BindingDescription();

    String TableFormat();

    String TableFormatExtendedEntry();

    String TableFormatLimitedEntry();

    String NameColon();

    String PathColon();

    //NewResourceHandler
    String NewGuidedDecisionTableDescription();

    String NewGuidedDecisionTableGraphDescription();

    String AllTheRulesInherit();

    String guidedDecisionTableResourceTypeDescription();

    String guidedDecisionTableGraphResourceTypeDescription();

    String DataCutToClipboardMessage();

    String DataCopiedToClipboardMessage();

    String HitPolicy();

    String NoneHitPolicy();

    String UniqueHitPolicy();

    String FirstHitPolicy();

    String RuleOrderHitPolicy();

    String None();

    String HasPriorityOverRow();

    String ResolvedHitPolicy();

    String NoColumnsAvailable();

    String AddColumn();

    String EditColumns();
}
