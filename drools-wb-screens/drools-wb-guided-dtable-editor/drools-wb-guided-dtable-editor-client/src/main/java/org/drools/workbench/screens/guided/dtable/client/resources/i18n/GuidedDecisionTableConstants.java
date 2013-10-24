/*
 * Copyright 2012 JBoss Inc
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

    public static final GuidedDecisionTableConstants INSTANCE = GWT.create( GuidedDecisionTableConstants.class );

    //Decision Table
    String NoPatternBindingsAvailable();

    String AddRow();

    String Otherwise();

    String Analyze();

    String Description();

    String Analysis();

    String negatedPattern();

    String Retract();

    String WorkItemAction();

    String YouMustEnterAColumnHeaderValueDescription();

    String ThatColumnNameIsAlreadyInUsePleasePickAnother();

    String ColumnHeaderDescription();

    String HideThisColumn();

    String ApplyChanges();

    String DecisionTableBRLFragmentNothingDefined();

    String Choose();

    String ActionColumnConfigurationInsertingANewFact();

    String Pattern();

    String Field();

    String ValueList();

    String ValueListsExplanation();

    String optionalValueList();

    String DefaultValue();

    String LimitedEntryValue();

    String LogicallyInsertColon();

    String YouMustEnterAColumnPattern();

    String YouMustEnterAColumnField();

    String EditTheFieldThatThisColumnOperatesOn();

    String ChooseAPatternThatThisColumnAddsDataTo();

    String pleaseChooseFactType();

    String OK();

    String ChooseExistingPatternToAddColumnTo();

    String ORwithEmphasis();

    String CreateNewFactPattern();

    String NewFactSelectTheType();

    String FactType();

    String Binding();

    String PleaseEnterANameForFact();

    String PleaseEnterANameThatIsNotTheSameAsTheFactType();

    String PleaseEnterANameThatIsNotAlreadyUsedByAnotherPattern();

    String LogicallyInsertANewFact();

    String LogicallyAssertAFactTheFactWillBeRetractedWhenTheSupportingEvidenceIsRemoved();

    String Edit();

    String EditDisabled();

    String ColumnConfigurationRetractAFact();

    String FactToRetractColon();

    String ColumnConfigurationSetAFieldOnAFact();

    String ChooseABoundFactThatThisColumnPertainsTo();

    String Fact();

    String UpdateEngineWithChanges();

    String YouMustEnterAColumnFact();

    String pleaseChooseABoundFactForThisColumn();

    String pleaseChooseAFactPatternFirst();

    String UpdateFact();

    String UpdateDescription();

    String ChooseFact();

    String ColumnConfigurationWorkItemInsertFact();

    String BindActionFieldToWorkItem();

    String NoWorkItemsAvailable();

    String ColumnConfigurationWorkItem();

    String WorkItemNameColon();

    String WorkItemInputParameters();

    String ColumnConfigurationWorkItemSetField();

    String ActionBRLFragmentConfiguration();

    String ConditionBRLFragmentConfiguration();

    String LiteralValue();

    String Formula();

    String Predicate();

    String ConditionColumnConfiguration();

    String ChooseAnExistingPatternThatThisColumnAddsTo();

    String CalculationType();

    String EditTheOperatorThatIsUsedToCompareDataWithThisField();

    String Operator();

    String DTLabelOverCEPWindow();

    String DTLabelFromEntryPoint();

    String PleaseSelectOrEnterField();

    String NotifyNoSelectedOperator();

    String notNeededForPredicate();

    String pleaseSelectAPatternFirst();

    String pleaseSelectAField();

    String pleaseChooseAFieldFirst();

    String Predicates();

    String PredicatesInfo();

    String SetTheOperator();

    String noOperator();

    String CreateANewFactPattern();

    String negatePattern();

    String OverCEPWindow();

    String ConfigureColumnsNote();

    String DecisionTable();

    String ConditionColumns();

    String ActionColumns();

    String Options();

    String EditThisActionColumnConfiguration();

    String RemoveThisActionColumn();

    String DeleteActionColumnWarning( String p0 );

    String NewColumn();

    String AddNewColumn();

    String AddNewMetadataOrAttributeColumn();

    String AddNewConditionSimpleColumn();

    String SetTheValueOfAField();

    String SetTheValueOfAFieldOnANewFact();

    String RetractAnExistingFact();

    String IncludeAdvancedOptions();

    String AddNewConditionBRLFragment();

    String WorkItemActionSetField();

    String WorkItemActionInsertFact();

    String AddNewActionBRLFragment();

    String Config();

    String AddAnOptionToTheRule();

    String AddMetadataToTheRule();

    String Metadata1();

    String Attribute();

    String TypeOfColumn();

    String EditThisColumnsConfiguration();

    String RemoveThisConditionColumn();

    String UnableToDeletePatterns();

    String UnableToDeleteConditionColumns();

    String UnableToDeleteConditionColumn0( String p0 );

    String DeleteConditionColumnWarning0( String p0 );

    String Attributes();

    String UseRowNumber();

    String ReverseOrder();

    String RemoveThisAttribute();

    String RemoveThisMetadata();

    String DeleteItem();

    String NewItem();

    //Audit Log
    String DecisionTableAuditLog();

    String DecisionTableAuditLogEvents();

    String DecisionTableAuditLogEventDeleteColumn();

    String DecisionTableAuditLogEventDeleteRow();

    String DecisionTableAuditLogEventInsertColumn();

    String DecisionTableAuditLogEventUpdateColumn();

    String DecisionTableAuditLogEventInsertRow();

    String AuditLogEntryOn1( String date,
                             String who );

    String DecisionTableAuditLogInsertRowAt0( int index );

    String DecisionTableAuditLogDeleteRowAt0( int index );

    String DecisionTableAuditLogDeleteColumn0( String header );

    String DecisionTableAuditLogInsertAttribute0( String attribute );

    String DecisionTableAuditLogInsertMetadata0( String metadata );

    String DecisionTableAuditLogInsertCondition0( String conditionHeader );

    String DecisionTableAuditLogInsertActionInsertFact0( String actionInsertFactHeader );

    String DecisionTableAuditLogInsertActionSetField0( String actionSetFieldHeader );

    String DecisionTableAuditLogInsertColumn0( String header );

    String DecisionTableAuditLogNoEntries();

    String FactTypeColon();

    String FieldColon();

    String OperatorColon();

    String ValueColon();

    String BoundVariableColon();

    String DecisionTableAuditLog0Was1( String value,
                                       String originalValue );

    String DecisionTableAuditLogUpdateCondition0Was1( String value,
                                                      String originalValue );

    String DecisionTableAuditLogUpdateAction0Was1( String value,
                                                   String originalValue );

    String DecisionTableAuditLogUpdateColumn0Was1( String value,
                                                   String originalValue );

    String DecisionTableAuditLogUpdateAttribute( String value );

    String AreYouSureYouWantToRemoveThisItem();

    String InsertYourCommentsHere();

    //Wizard
    String UseWizardToBuildAsset();

    String DecisionTableWizard();

    String DecisionTableWizardSummary();

    String DecisionTableWizardFactPatterns();

    String DecisionTableWizardFactPatternConstraints();

    String DecisionTableWizardActions();

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

    String DecisionTableWizardPredicate();

    String DecisionTableWizardPredicateExpression();

    String MandatoryField();

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

    String AllTheRulesInherit();
}
