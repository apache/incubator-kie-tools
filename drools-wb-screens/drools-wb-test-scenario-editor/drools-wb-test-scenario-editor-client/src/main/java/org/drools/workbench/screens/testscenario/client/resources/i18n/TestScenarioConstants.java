package org.drools.workbench.screens.testscenario.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface
        TestScenarioConstants
        extends Messages {

    public static final TestScenarioConstants INSTANCE = GWT.create(TestScenarioConstants.class);

    String ActivateRuleFlowGroup();

    String RemoveThisRuleFlowActivation();

    String MoreDotDot();

    String AddAnotherSectionOfDataAndExpectations();

    String ChooseDotDotDot();

    String ChooseAFieldToAdd();

    String CALL();

    String AddANewDataInputToThisScenario();

    String NewInput();

    String showListButton();

    String loadingList1();

    String globals();

    String configuration();

    String AddInputDataAndExpectationsHere();

    String EnterRuleNameScenario();

    String OK();

    String pleaseChoose1();

    String AddAnotherFieldToThisSoYouCanSetItsValue();

    String CallAMethodOnAFactScenario();

    String Add();

    String Wizard();

    String ChooseAMethodToInvoke();

    String AreYouSureToRemoveCallMethod();

    String RemoveCallMethod();

    String AddANewRule();

    String RemoveSelectedRule();

    String PleaseChooseARuleToRemove();

    String AllowTheseRulesToFire();

    String PreventTheseRulesFromFiring();

    String AllRulesMayFire();

    String SelectRule();

    String AddAField();

    String UseRealDateAndTime();

    String UseASimulatedDateAndTime();

    String AddANewExpectation();

    String NewExpectation();

    String Rule();

    String FactValue();

    String AnyFactThatMatches();

    String EXPECT();

    String AreYouSureYouWantToRemoveThisItem();

    String RemoveTheColumnForScenario(String name);

    String CanTRemoveThisColumnAsTheName0IsBeingUsed(String name);

    String AreYouSureYouWantToRemoveColumn0(String name);

    String RemoveThisRow();

    String AreYouSureYouWantToRemoveRow0(String fieldName);

    String AreYouSureYouWantToRemoveThisBlockOfData();

    String RemoveThisBlockOfData();

    String ValueFor0(String name);

    String Choose();

    String property0RulesFiredIn1Ms(Long numberOfRulesFired, Long executionTimeResult);

    String ShowRulesFired();

    String RulesFired();

    String GIVEN();

    String NewGlobal();

    String GlobalColon();

    String AddANewGlobalToThisScenario();

    String TheName0IsAlreadyInUsePleaseChooseAnotherName(String text);

    String missingGlobalsWarning();

    String globalForScenario(String factType);

    String CantRemoveThisBlockAsOneOfTheNamesIsBeingUsed();

    String insertForScenario(String factType);

    String AElementToAddInCollectionList();

    String FieldValue();

    String LiteralValue();

    String Literal();

    String LiteralValTip();

    String AdvancedSection();

    String BoundVariable();

    String modifyForScenario(String factType);

    String InsertANewFact1();

    String ModifyAnExistingFactScenario();

    String DeleteAnExistingFactScenario();

    String FactName();

    String YouMustEnterAValidFactName();

    String TheFactName0IsAlreadyInUsePleaseChooseAnotherName(String factName);

    String DeleteFacts();

    String RemoveThisDeleteStatement();

    String ExpectRules();

    String ActualResult(String s);

    String DeleteTheExpectationForThisFact();

    String AreYouSureYouWantToRemoveThisExpectation();

    String scenarioFactTypeHasValues(String type, String name);

    String AFactOfType0HasValues(String name);

    String AddAFieldToThisExpectation();

    String equalsScenario();

    String doesNotEqualScenario();

    String AdvancedOptions();

    String AVariable();

    String ABoundVariable();

    String BoundVariableTip();

    String RunScenario();

    String RunScenarioTip();

    String BuildingAndRunningScenario();

    String Results();

    String SummaryColon();

    String AuditLogColon();

    String ShowEventsButton();

    String MaxRuleFiringsReachedWarning(int number);

    String packageConfigurationProblem1();

    String BadDateFormatPleaseTryAgainTryTheFormatOf0(String format);

    String currentDateAndTime();

    String CreateNewFact();

    String CreateNewFactTip();

    String Fact();

    String GuidedList();

    String AGuidedList();

    String AGuidedListTip();

    String RemoveThisFieldExpectation();

    String AreYouSureYouWantToRemoveThisFieldExpectation(String fieldName);

    String firedAtLeastOnce();

    String didNotFire();

    String firedThisManyTimes();

    String RemoveThisRuleExpectation();

    String AreYouSureYouWantToRemoveThisRuleExpectation();

    String AddFieldToFact();

    String Text();

    String TestPassed();

    String Success();

    String ThereWereTestFailures();

    String NewTestScenarioDescription();

    String TestScenario();
    
    String PleaseInputSessionName();
    
    String SessionName();
    
    String TestScenarios();
   
    String RunAllScenarios();

    String TestScenarioParamFileName(String fileName);

    String testScenarioResourceTypeDescription();

}
