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

package org.drools.workbench.screens.scenariosimulation.backend.server.runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.drools.workbench.screens.scenariosimulation.backend.server.expression.BaseExpressionEvaluator;
import org.drools.workbench.screens.scenariosimulation.backend.server.expression.ExpressionEvaluator;
import org.drools.workbench.screens.scenariosimulation.backend.server.model.Dispute;
import org.drools.workbench.screens.scenariosimulation.backend.server.model.Person;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioInput;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioOutput;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioResult;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioRunnerData;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.SingleFactValueResult;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValue;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static java.util.stream.Collectors.toList;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelper.createExtractorFunction;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelper.extractExpectedValues;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelper.extractGivenValues;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelper.getParamsForBean;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelper.getScenarioResultsFromGivenFacts;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelper.groupByFactIdentifierAndFilter;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelper.validateAssertion;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelper.verifyConditions;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ScenarioRunnerHelperTest {

    private static final String NAME = "NAME";
    private static final double AMOUNT = 10;
    private static final String TEST_DESCRIPTION = "Test description";
    private static final ClassLoader classLoader = ScenarioRunnerHelperTest.class.getClassLoader();
    private static final ExpressionEvaluator expressionEvaluator = new BaseExpressionEvaluator(classLoader);

    private Simulation simulation;
    private FactIdentifier personFactIdentifier;
    private ExpressionIdentifier firstNameGivenExpressionIdentifier;
    private FactMapping firstNameGivenFactMapping;
    private Scenario scenario1;
    private Scenario scenario2;
    private ExpressionIdentifier firstNameExpectedExpressionIdentifier;
    private FactMapping firstNameExpectedFactMapping;
    private FactIdentifier disputeFactIdentifier;
    private ExpressionIdentifier amountGivenExpressionIdentifier;
    private FactMapping amountNameGivenFactMapping;
    private ExpressionIdentifier amountExpectedExpressionIdentifier;
    private FactMapping amountNameExpectedFactMapping;
    private FactMappingValue amountNameExpectedFactMappingValue;

    @Before
    public void setup() {
        simulation = new Simulation();
        personFactIdentifier = FactIdentifier.create("Fact 1", Person.class.getCanonicalName());
        firstNameGivenExpressionIdentifier = ExpressionIdentifier.create("First Name Given", FactMappingType.GIVEN);
        firstNameGivenFactMapping = simulation.getSimulationDescriptor().addFactMapping(personFactIdentifier, firstNameGivenExpressionIdentifier);
        firstNameGivenFactMapping.addExpressionElement("firstName", String.class.getCanonicalName());

        disputeFactIdentifier = FactIdentifier.create("Fact 2", Dispute.class.getCanonicalName());
        amountGivenExpressionIdentifier = ExpressionIdentifier.create("Amount Given", FactMappingType.GIVEN);
        amountNameGivenFactMapping = simulation.getSimulationDescriptor().addFactMapping(disputeFactIdentifier, amountGivenExpressionIdentifier);
        amountNameGivenFactMapping.addExpressionElement("amount", Double.class.getCanonicalName());

        firstNameExpectedExpressionIdentifier = ExpressionIdentifier.create("First Name Expected", FactMappingType.EXPECT);
        firstNameExpectedFactMapping = simulation.getSimulationDescriptor().addFactMapping(personFactIdentifier, firstNameExpectedExpressionIdentifier);
        firstNameExpectedFactMapping.addExpressionElement("firstName", String.class.getCanonicalName());

        amountExpectedExpressionIdentifier = ExpressionIdentifier.create("Amount Expected", FactMappingType.EXPECT);
        amountNameExpectedFactMapping = simulation.getSimulationDescriptor().addFactMapping(disputeFactIdentifier, amountExpectedExpressionIdentifier);
        amountNameExpectedFactMapping.addExpressionElement("amount", Double.class.getCanonicalName());

        scenario1 = simulation.addScenario();
        scenario1.setDescription(TEST_DESCRIPTION);
        scenario1.addMappingValue(personFactIdentifier, firstNameGivenExpressionIdentifier, NAME);
        scenario1.addMappingValue(personFactIdentifier, firstNameExpectedExpressionIdentifier, NAME);

        scenario2 = simulation.addScenario();
        scenario2.setDescription(TEST_DESCRIPTION);
        scenario2.addMappingValue(personFactIdentifier, firstNameGivenExpressionIdentifier, NAME);
        scenario2.addMappingValue(personFactIdentifier, firstNameExpectedExpressionIdentifier, NAME);
        scenario2.addMappingValue(disputeFactIdentifier, amountGivenExpressionIdentifier, AMOUNT);
        amountNameExpectedFactMappingValue = scenario2.addMappingValue(disputeFactIdentifier, amountExpectedExpressionIdentifier, AMOUNT);
    }

    @Test
    public void extractGivenValuesTest() {
        List<ScenarioInput> scenario1Inputs = extractGivenValues(simulation.getSimulationDescriptor(),
                                                                 scenario1.getUnmodifiableFactMappingValues(),
                                                                 classLoader,
                                                                 expressionEvaluator);
        assertEquals(1, scenario1Inputs.size());

        List<ScenarioInput> scenario2Inputs = extractGivenValues(simulation.getSimulationDescriptor(),
                                                                 scenario2.getUnmodifiableFactMappingValues(),
                                                                 classLoader,
                                                                 expressionEvaluator);
        assertEquals(2, scenario2Inputs.size());
    }

    @Test
    public void extractExpectedValuesTest() {
        List<ScenarioOutput> scenario1Outputs = extractExpectedValues(scenario1.getUnmodifiableFactMappingValues());
        assertEquals(1, scenario1Outputs.size());

        scenario2.addOrUpdateMappingValue(FactIdentifier.create("TEST", String.class.getCanonicalName()),
                                          ExpressionIdentifier.create("TEST", FactMappingType.EXPECT),
                                          "TEST");
        List<ScenarioOutput> scenario2Outputs = extractExpectedValues(scenario2.getUnmodifiableFactMappingValues());
        assertEquals(3, scenario2Outputs.size());
        assertEquals(1, scenario2Outputs.stream().filter(ScenarioOutput::isNewFact).count());
    }

    @Test
    public void verifyConditionsTest() {
        List<ScenarioInput> scenario1Inputs = extractGivenValues(simulation.getSimulationDescriptor(),
                                                                 scenario1.getUnmodifiableFactMappingValues(),
                                                                 classLoader,
                                                                 expressionEvaluator);
        List<ScenarioOutput> scenario1Outputs = extractExpectedValues(scenario1.getUnmodifiableFactMappingValues());

        ScenarioRunnerData scenarioRunnerData1 = new ScenarioRunnerData();
        scenario1Inputs.forEach(scenarioRunnerData1::addInput);
        scenario1Outputs.forEach(scenarioRunnerData1::addOutput);

        verifyConditions(simulation.getSimulationDescriptor(),
                         scenarioRunnerData1,
                         expressionEvaluator);
        assertEquals(1, scenarioRunnerData1.getResultData().size());

        List<ScenarioInput> scenario2Inputs = extractGivenValues(simulation.getSimulationDescriptor(),
                                                                 scenario2.getUnmodifiableFactMappingValues(),
                                                                 classLoader,
                                                                 expressionEvaluator);
        List<ScenarioOutput> scenario2Outputs = extractExpectedValues(scenario2.getUnmodifiableFactMappingValues());

        ScenarioRunnerData scenarioRunnerData2 = new ScenarioRunnerData();
        scenario2Inputs.forEach(scenarioRunnerData2::addInput);
        scenario2Outputs.forEach(scenarioRunnerData2::addOutput);

        verifyConditions(simulation.getSimulationDescriptor(),
                         scenarioRunnerData2,
                         expressionEvaluator);
        assertEquals(2, scenarioRunnerData2.getResultData().size());
    }

    @Test
    public void getScenarioResultsTest() {
        List<ScenarioInput> scenario1Inputs = extractGivenValues(simulation.getSimulationDescriptor(),
                                                                 scenario1.getUnmodifiableFactMappingValues(),
                                                                 classLoader,
                                                                 expressionEvaluator);
        List<ScenarioOutput> scenario1Outputs = extractExpectedValues(scenario1.getUnmodifiableFactMappingValues());

        assertTrue(scenario1Inputs.size() > 0);

        ScenarioInput input1 = scenario1Inputs.get(0);

        scenario1Outputs = scenario1Outputs.stream().filter(elem -> elem.getFactIdentifier().equals(input1.getFactIdentifier())).collect(toList());
        List<ScenarioResult> scenario1Results = getScenarioResultsFromGivenFacts(simulation.getSimulationDescriptor(), scenario1Outputs, input1, expressionEvaluator);

        assertEquals(1, scenario1Results.size());
        assertFalse(scenario1Outputs.get(0).getExpectedResult().get(0).isError());

        List<ScenarioInput> scenario2Inputs = extractGivenValues(simulation.getSimulationDescriptor(),
                                                                 scenario2.getUnmodifiableFactMappingValues(),
                                                                 classLoader,
                                                                 expressionEvaluator);
        List<ScenarioOutput> scenario2Outputs = extractExpectedValues(scenario2.getUnmodifiableFactMappingValues());

        assertTrue(scenario2Inputs.size() > 0);

        ScenarioInput input2 = scenario2Inputs.get(0);

        scenario2Outputs = scenario2Outputs.stream().filter(elem -> elem.getFactIdentifier().equals(input2.getFactIdentifier())).collect(toList());
        List<ScenarioResult> scenario2Results = getScenarioResultsFromGivenFacts(simulation.getSimulationDescriptor(), scenario2Outputs, input2, expressionEvaluator);

        assertEquals(1, scenario2Results.size());
        assertFalse(scenario2Outputs.get(0).getExpectedResult().get(0).isError());

        List<ScenarioOutput> newFact = Collections.singletonList(new ScenarioOutput(personFactIdentifier, Collections.emptyList(), true));
        List<ScenarioResult> scenario2NoResults = getScenarioResultsFromGivenFacts(simulation.getSimulationDescriptor(), newFact, input2, expressionEvaluator);

        assertEquals(0, scenario2NoResults.size());

        Person person = new Person();
        person.setFirstName("ANOTHER STRING");
        ScenarioInput newInput = new ScenarioInput(personFactIdentifier, person);

        List<ScenarioResult> scenario3Results = getScenarioResultsFromGivenFacts(simulation.getSimulationDescriptor(), scenario1Outputs, newInput, expressionEvaluator);
        assertTrue(scenario1Outputs.get(0).getExpectedResult().get(0).isError());

        assertEquals(1, scenario3Results.size());
    }

    @Test
    public void validateAssertionTest() {

        List<ScenarioResult> scenarioFailResult = new ArrayList<>();
        scenarioFailResult.add(new ScenarioResult(disputeFactIdentifier, amountNameExpectedFactMappingValue, "SOMETHING_ELSE"));
        try {
            validateAssertion(scenarioFailResult, scenario2);
            fail();
        } catch (ScenarioException ignored) {
        }

        List<ScenarioResult> scenarioSuccessResult = new ArrayList<>();
        scenarioSuccessResult.add(new ScenarioResult(disputeFactIdentifier, amountNameExpectedFactMappingValue, amountNameExpectedFactMappingValue.getRawValue()).setResult(true));
        validateAssertion(scenarioSuccessResult, scenario2);
    }

    @Test
    public void groupByFactIdentifierAndFilterTest() {
        Map<FactIdentifier, List<FactMappingValue>> scenario1Given = groupByFactIdentifierAndFilter(scenario1.getUnmodifiableFactMappingValues(), FactMappingType.GIVEN);
        Map<FactIdentifier, List<FactMappingValue>> scenario1Expected = groupByFactIdentifierAndFilter(scenario1.getUnmodifiableFactMappingValues(), FactMappingType.EXPECT);
        Map<FactIdentifier, List<FactMappingValue>> scenario2Given = groupByFactIdentifierAndFilter(scenario2.getUnmodifiableFactMappingValues(), FactMappingType.GIVEN);
        Map<FactIdentifier, List<FactMappingValue>> scenario2Expected = groupByFactIdentifierAndFilter(scenario2.getUnmodifiableFactMappingValues(), FactMappingType.EXPECT);

        assertEquals(1, scenario1Given.keySet().size());
        assertEquals(1, scenario1Expected.keySet().size());
        assertEquals(2, scenario2Given.keySet().size());
        assertEquals(2, scenario2Expected.keySet().size());

        assertEquals(1, scenario1Given.get(personFactIdentifier).size());
        assertEquals(1, scenario1Expected.get(personFactIdentifier).size());
        assertEquals(1, scenario2Given.get(disputeFactIdentifier).size());
        assertEquals(1, scenario2Expected.get(disputeFactIdentifier).size());

        Scenario scenario = new Scenario();
        scenario.addMappingValue(FactIdentifier.EMPTY, ExpressionIdentifier.DESCRIPTION, null);
        assertEquals(0, groupByFactIdentifierAndFilter(scenario.getUnmodifiableFactMappingValues(), FactMappingType.GIVEN).size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void groupByFactIdentifierAndFilterFailTest() {
        List<FactMappingValue> fail = new ArrayList<>();
        fail.add(new FactMappingValue());
        groupByFactIdentifierAndFilter(fail, FactMappingType.GIVEN);
    }

    @Test
    public void createExtractorFunctionTest() {
        String personName = "Test";
        FactMappingValue factMappingValue = new FactMappingValue(personFactIdentifier, firstNameGivenExpressionIdentifier, personName);
        Function<Object, SingleFactValueResult> extractorFunction = createExtractorFunction(expressionEvaluator, factMappingValue, simulation.getSimulationDescriptor());
        Person person = new Person();

        person.setFirstName(personName);
        assertTrue(extractorFunction.apply(person).isSatisfied());

        person.setFirstName("OtherString");
        assertFalse(extractorFunction.apply(person).isSatisfied());

        Function<Object, SingleFactValueResult> extractorFunction1 = createExtractorFunction(expressionEvaluator,
                                                                                             new FactMappingValue(personFactIdentifier,
                                                                                                                  firstNameGivenExpressionIdentifier,
                                                                                                                  null),
                                                                                             simulation.getSimulationDescriptor());
        SingleFactValueResult nullValue = extractorFunction1.apply(new Person());
        assertTrue(nullValue.isSatisfied());
        assertNull(nullValue.getResult());
    }

    @Test
    public void getParamsForBeanTest() {
        List<FactMappingValue> factMappingValues = new ArrayList<>();
        FactMappingValue factMappingValue = new FactMappingValue(disputeFactIdentifier, amountGivenExpressionIdentifier, "NOT PARSABLE");
        factMappingValues.add(factMappingValue);

        try {
            getParamsForBean(simulation.getSimulationDescriptor(), disputeFactIdentifier, factMappingValues, classLoader, expressionEvaluator);
            fail();
        } catch (ScenarioException ignored) {

        }
        Assert.assertTrue(factMappingValue.isError());
    }
}