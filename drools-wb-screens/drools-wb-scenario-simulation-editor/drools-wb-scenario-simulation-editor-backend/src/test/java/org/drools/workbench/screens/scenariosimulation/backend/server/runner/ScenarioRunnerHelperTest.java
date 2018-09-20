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
import java.util.List;
import java.util.Map;

import org.drools.workbench.screens.scenariosimulation.backend.server.model.Dispute;
import org.drools.workbench.screens.scenariosimulation.backend.server.model.Person;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioInput;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioOutput;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioResult;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValue;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.stream.Collectors.toList;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelper.extractExpectedValues;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelper.extractGivenValues;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelper.getScenarioResults;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelper.groupByFactIdentifierAndFilter;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelper.validateAssertion;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioRunnerHelper.verifyConditions;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ScenarioRunnerHelperTest {

    private static final String NAME = "NAME";
    private static final double AMOUNT = 10;
    private static final String TEST_DESCRIPTION = "Test description";

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

    @Mock
    private EachTestNotifier singleNotifier;

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

        firstNameExpectedExpressionIdentifier = ExpressionIdentifier.create("First Name Expected", FactMappingType.EXPECTED);
        firstNameExpectedFactMapping = simulation.getSimulationDescriptor().addFactMapping(personFactIdentifier, firstNameExpectedExpressionIdentifier);
        firstNameExpectedFactMapping.addExpressionElement("firstName", String.class.getCanonicalName());

        amountExpectedExpressionIdentifier = ExpressionIdentifier.create("Amount Expected", FactMappingType.EXPECTED);
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
        List<ScenarioInput> scenario1Inputs = extractGivenValues(simulation.getSimulationDescriptor(), scenario1.getUnmodifiableFactMappingValues());
        assertEquals(1, scenario1Inputs.size());

        List<ScenarioInput> scenario2Inputs = extractGivenValues(simulation.getSimulationDescriptor(), scenario2.getUnmodifiableFactMappingValues());
        assertEquals(2, scenario2Inputs.size());
    }

    @Test
    public void extractExpectedValuesTest() {
        List<ScenarioOutput> scenario1Outputs = extractExpectedValues(scenario1.getUnmodifiableFactMappingValues());
        assertEquals(1, scenario1Outputs.size());

        List<ScenarioOutput> scenario2Outputs = extractExpectedValues(scenario2.getUnmodifiableFactMappingValues());
        assertEquals(2, scenario2Outputs.size());
    }

    @Test
    public void verifyConditionsTest() {
        List<ScenarioInput> scenario1Inputs = extractGivenValues(simulation.getSimulationDescriptor(), scenario1.getUnmodifiableFactMappingValues());
        List<ScenarioOutput> scenario1Outputs = extractExpectedValues(scenario1.getUnmodifiableFactMappingValues());

        List<ScenarioResult> scenario1Results = verifyConditions(simulation.getSimulationDescriptor(), scenario1Inputs, scenario1Outputs);
        assertEquals(1, scenario1Results.size());

        List<ScenarioInput> scenario2Inputs = extractGivenValues(simulation.getSimulationDescriptor(), scenario2.getUnmodifiableFactMappingValues());
        List<ScenarioOutput> scenario2Outputs = extractExpectedValues(scenario2.getUnmodifiableFactMappingValues());

        List<ScenarioResult> scenario2Results = verifyConditions(simulation.getSimulationDescriptor(), scenario2Inputs, scenario2Outputs);
        assertEquals(2, scenario2Results.size());
    }

    @Test
    public void getScenarioResultsTest() {
        List<ScenarioInput> scenario1Inputs = extractGivenValues(simulation.getSimulationDescriptor(), scenario1.getUnmodifiableFactMappingValues());
        List<ScenarioOutput> scenario1Outputs = extractExpectedValues(scenario1.getUnmodifiableFactMappingValues());

        assertTrue(scenario1Inputs.size() > 0);

        ScenarioInput input1 = scenario1Inputs.get(0);

        scenario1Outputs = scenario1Outputs.stream().filter(elem -> elem.getFactIdentifier().equals(input1.getFactIdentifier())).collect(toList());
        List<ScenarioResult> scenario1Results = getScenarioResults(simulation.getSimulationDescriptor(), scenario1Outputs, input1);

        assertEquals(1, scenario1Results.size());

        List<ScenarioInput> scenario2Inputs = extractGivenValues(simulation.getSimulationDescriptor(), scenario2.getUnmodifiableFactMappingValues());
        List<ScenarioOutput> scenario2Outputs = extractExpectedValues(scenario2.getUnmodifiableFactMappingValues());

        assertTrue(scenario2Inputs.size() > 0);

        ScenarioInput input2 = scenario2Inputs.get(0);

        scenario2Outputs = scenario2Outputs.stream().filter(elem -> elem.getFactIdentifier().equals(input2.getFactIdentifier())).collect(toList());
        List<ScenarioResult> scenario2Results = getScenarioResults(simulation.getSimulationDescriptor(), scenario2Outputs, input2);

        assertEquals(1, scenario2Results.size());
    }

    @Test
    public void validateAssertionTest() {

        List<ScenarioResult> scenarioFailResult = new ArrayList<>();
        scenarioFailResult.add(new ScenarioResult(disputeFactIdentifier, amountNameExpectedFactMappingValue, "SOMETHING_ELSE", false));
        try {
            validateAssertion(scenarioFailResult, scenario2, singleNotifier);
            fail();
        } catch (ScenarioException ignored) {
        }

        verify(singleNotifier, times(1)).addFailedAssumption(any());

        reset(singleNotifier);

        List<ScenarioResult> scenarioSuccessResult = new ArrayList<>();
        scenarioSuccessResult.add(new ScenarioResult(disputeFactIdentifier, amountNameExpectedFactMappingValue, amountNameExpectedFactMappingValue.getRawValue(), true));
        validateAssertion(scenarioSuccessResult, scenario2, singleNotifier);

        verify(singleNotifier, times(0)).addFailedAssumption(any());
    }

    @Test
    public void groupByFactIdentifierAndFilterTest() {
        Map<FactIdentifier, List<FactMappingValue>> scenario1Given = groupByFactIdentifierAndFilter(scenario1.getUnmodifiableFactMappingValues(), FactMappingType.GIVEN);
        Map<FactIdentifier, List<FactMappingValue>> scenario1Expected = groupByFactIdentifierAndFilter(scenario1.getUnmodifiableFactMappingValues(), FactMappingType.EXPECTED);
        Map<FactIdentifier, List<FactMappingValue>> scenario2Given = groupByFactIdentifierAndFilter(scenario2.getUnmodifiableFactMappingValues(), FactMappingType.GIVEN);
        Map<FactIdentifier, List<FactMappingValue>> scenario2Expected = groupByFactIdentifierAndFilter(scenario2.getUnmodifiableFactMappingValues(), FactMappingType.EXPECTED);

        assertEquals(1, scenario1Given.keySet().size());
        assertEquals(1, scenario1Expected.keySet().size());
        assertEquals(2, scenario2Given.keySet().size());
        assertEquals(2, scenario2Expected.keySet().size());

        assertEquals(1, scenario1Given.get(personFactIdentifier).size());
        assertEquals(1, scenario1Expected.get(personFactIdentifier).size());
        assertEquals(1, scenario2Given.get(disputeFactIdentifier).size());
        assertEquals(1, scenario2Expected.get(disputeFactIdentifier).size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void groupByFactIdentifierAndFilterFailTest() {
        List<FactMappingValue> fail = new ArrayList<>();
        fail.add(new FactMappingValue(personFactIdentifier, null, null));
        groupByFactIdentifierAndFilter(fail, FactMappingType.GIVEN);
    }
}