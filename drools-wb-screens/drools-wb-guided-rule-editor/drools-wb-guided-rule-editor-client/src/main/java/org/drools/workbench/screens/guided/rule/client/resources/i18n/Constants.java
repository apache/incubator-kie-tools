/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.rule.client.resources.i18n;

import com.google.gwt.i18n.client.Messages;

/**
 * This uses GWT to provide client side compile time resolving of locales. See:
 * http://code.google.com/docreader/#p=google-web-toolkit-doc-1-5&s=google-web-
 * toolkit-doc-1-5&t=DevGuideInternationalization (for more information).
 * <p/>
 * Each method name matches up with a key in Constants.properties (the
 * properties file can still be used on the server). To use this, use
 * <code>GWT.create(Constants.class)</code>.
 */
public interface Constants
        extends
        Messages {

    String AddAnotherFieldToThisSoYouCanSetItsValue();

    String ChooseAMethodToInvoke();

    String AddField();

    String OK();

    String setterLabel( String actionDisplayName,
                        String descFact );

    String FieldValue();

    String LiteralValue();

    String LiteralValTip();

    String Literal();

    String AdvancedSection();

    String Formula();

    String FormulaTip();

    String Version();

    String NewFactPattern();

    String chooseFactType();

    String FormulaEvaluateToAValue();

    String AdvancedOptions();

    String BoundVariable();

    String AVariable();

    String ABoundVariable();

    String BoundVariableTip();

    String NewFormula();

    String FormulaExpressionTip();

    String TheValue0IsNotValidForThisField( String value );

    String AFormula();

    String Error();

    String Type();

    String RemoveThisWholeRestriction();

    String RemoveThisRestriction();

    String AllOf();

    String AnyOf();

    String RemoveThisNestedRestriction();

    String RemoveThisItemFromNestedConstraint();

    String AddMoreOptionsToThisFieldsValues();

    String FormulaBooleanTip();

    String pleaseChoose();

    String Cancel();

    String AddAnOptionToTheRule();

    String AddMetadataToTheRule();

    String AddRow();

    String name();

    String TheVariableName0IsAlreadyTaken( String variableName );

    String BindTheFieldCalled0ToAVariable( String fieldName );

    String BindTheExpressionToAVariable();

    String ShowSubFields();

    String ApplyAConstraintToASubFieldOf0( String parentFieldName );

    String AddFieldsToThisConstraint();

    String AllOfAnd();

    String MultipleConstraintsTip();

    String MultipleFieldConstraint();

    String ModifyConstraintsFor0( String factType );

    String AddSubFieldConstraint();

    String AddARestrictionOnAField();

    String AnyOfOr();

    String MultipleFieldConstraints();

    String MultipleConstraintsTip1();

    String AddANewFormulaStyleExpression();

    String VariableName();

    String Add();

    String Metadata2();

    String Attributes1();

    String Choose();

    String RemoveThisRuleOption();

    String AddAConditionToThisRule();

    String AddAnOptionToTheRuleToModifyItsBehaviorWhenEvaluatedOrExecuted();

    String Metadata3();

    String Attribute1();

    String AddXToListY( String factName,
                        String globalName );

    String RemoveThisAction();

    String RemoveThisItem();

    String AddAConditionToTheRule();

    String FreeFormDrl();

    String ExpressionEditor();

    String ExpressionEditorTip();

    String NoModelTip();

    String AddANewAction();

    String AdvancedOptionsColon();

    String AddFreeFormDrl();

    String AddFreeFormDrlDotDotDot();

    String ThisIsADrlExpressionFreeForm();

    String RemoveThisENTIREConditionAndAllTheFieldConstraintsThatBelongToIt();

    String RemoveThisEntireConditionQ();

    String CanTRemoveThatItemAsItIsUsedInTheActionPartOfTheRule();

    String DeleteItem();

    String AddAField();

    String AddAFieldToThisExpectation();

    String ALiteralValueMeansTheValueAsTypedInIeItsNotACalculation();

    String WHEN();

    String THEN();

    String AddAnActionToThisRule();

    String optionsRuleModeller();

    String clickToAddPattern();

    String clickToAddPatterns();

    String ChangeFieldValuesOf0( String varName );

    String Delete0( String varName );

    String Modify0( String varName );

    String InsertFact0( String fact );

    String LogicallyInsertFact0( String fact );

    String Append0ToList1( String varName,
                           String collectionName );

    String CallMethodOn0( String varName );

    String hide();

    String RemoveThisBlockOfData();

    String ThereIsAAn0With( String patternName );

    String ThereIsAAn0( String patternName );

    String All0with( String patternName );

    String AddFirstNewField();

    String Actions();

    String FrozenAreas();

    String FrozenExplanation();

    String FreezeAreasForEditing();

    String FrozenActions();

    String FrozenConditions();

    String Conditions();

    String MoveUp();

    String MoveDown();

    String Top();

    String Bottom();

    String Line0( int i );

    String PositionColon();

    String ConditionPositionExplanation();

    String ActionPositionExplanation();

    String AddAConditionBelow();

    String AddAnActionBelow();

    String VerifyingItemPleaseWait();

    String TemplateKey();

    String TemplateKeyTip();

    String TemplateData();

    String LoadTemplateData();

    String Edit();

    String OnlyDisplayDSLConditions();

    String OnlyDisplayDSLActions();

    String CouldNotFindTheTypeForVariable0( String variableName );

    String RemoveConstraintValueDefinition();

    String RemoveConstraintValueDefinitionQuestion();

    String RemoveActionValueDefinition();

    String RemoveActionValueDefinitionQuestion();

    String Config();

    String Wizard();

    String Warning();

    String NewItem();

    String NewItemBelow();

    String EditDisabled();

    String InvalidPatternSectionDisabled();

    String NewGuidedRuleDescription();

    String UseDSL();

    String EXTENDS();

    String MetadataNameEmpty();

    String MetadataNotUnique0( String metadata );

    String CustomCode();

    String FunctionCode();

    String ResultCode();

    String ReverseCode();

    String ActionCode();

    String InitCode();

    String Function();

    String Result();

    String Reverse();

    String Action();

    String Init();

    String AreasMarkedWithRedAreMandatoryPleaseSetAValueBeforeSaving();

    String showDslSentences();
    
    String guidedRuleDRLResourceTypeDescription();

    String guidedRuleDSLResourceTypeDescription();

    String PleaseSetTheConstraintValue();

    String FactType0HasAField1ThatHasAnOperatorSetButNoValuePleaseAddAValueOrRemoveTheOperator(String factType, String fieldName);

    String WhenUsingFromTheSourceNeedsToBeSet();

    String PleaseSetTheEntryPoint();

    String ShowOptions();

    String filterHint();
}
