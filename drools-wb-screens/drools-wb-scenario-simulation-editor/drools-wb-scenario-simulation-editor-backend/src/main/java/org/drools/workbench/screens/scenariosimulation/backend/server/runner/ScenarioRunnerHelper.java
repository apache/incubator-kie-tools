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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.drools.workbench.screens.scenariosimulation.backend.server.OperatorEvaluator;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioInput;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioOutput;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioResult;
import org.drools.workbench.screens.scenariosimulation.backend.server.util.ScenarioBeanUtil;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionElement;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValue;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValueOperator;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.junit.internal.runners.model.EachTestNotifier;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.RequestContext;

import static java.util.stream.Collectors.toList;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.ScenarioExecutableBuilder.createBuilder;
import static org.drools.workbench.screens.scenariosimulation.backend.server.util.ScenarioBeanUtil.fillBean;

public class ScenarioRunnerHelper {

    private static final Map<String, Class<?>> primitiveMap = new HashMap<>();

    static {
        primitiveMap.put("boolean", boolean.class);
        primitiveMap.put("int", int.class);
        primitiveMap.put("long", long.class);
        primitiveMap.put("double", double.class);
        primitiveMap.put("float", float.class);
    }

    private ScenarioRunnerHelper() {

    }

    public static List<ScenarioInput> extractGivenValues(SimulationDescriptor simulationDescriptor, List<FactMappingValue> factMappingValues, ClassLoader classLoader) {
        List<ScenarioInput> scenarioInput = new ArrayList<>();

        Map<FactIdentifier, List<FactMappingValue>> groupByFactIdentifier =
                groupByFactIdentifierAndFilter(factMappingValues, FactMappingType.GIVEN);

        for (Map.Entry<FactIdentifier, List<FactMappingValue>> entry : groupByFactIdentifier.entrySet()) {

            FactIdentifier factIdentifier = entry.getKey();

            // for each fact, create a map of path to fields and values to set
            Map<List<String>, Object> paramsForBean = getParamsForBean(simulationDescriptor,
                                                                       factIdentifier,
                                                                       entry.getValue(),
                                                                       classLoader);

            Object bean = fillBean(factIdentifier.getClassName(), paramsForBean, classLoader);

            scenarioInput.add(new ScenarioInput(factIdentifier, bean));
        }

        return scenarioInput;
    }

    public static List<ScenarioOutput> extractExpectedValues(List<FactMappingValue> factMappingValues) {
        List<ScenarioOutput> scenarioOutput = new ArrayList<>();

        Map<FactIdentifier, List<FactMappingValue>> groupByFactIdentifier =
                groupByFactIdentifierAndFilter(factMappingValues, FactMappingType.EXPECTED);

        for (Map.Entry<FactIdentifier, List<FactMappingValue>> entry : groupByFactIdentifier.entrySet()) {

            FactIdentifier factIdentifier = entry.getKey();

            scenarioOutput.add(new ScenarioOutput(factIdentifier, entry.getValue()));
        }

        return scenarioOutput;
    }

    public static RequestContext executeScenario(KieContainer kieContainer, List<ScenarioInput> given) {
        ScenarioExecutableBuilder scenarioExecutableBuilder = createBuilder(kieContainer);
        given.stream().map(ScenarioInput::getValue).forEach(scenarioExecutableBuilder::insert);
        return scenarioExecutableBuilder.run();
    }

    public static List<ScenarioResult> verifyConditions(SimulationDescriptor simulationDescriptor,
                                                        List<ScenarioInput> inputData,
                                                        List<ScenarioOutput> outputData,
                                                        ClassLoader classLoader) {
        List<ScenarioResult> scenarioResult = new ArrayList<>();

        List<FactIdentifier> inputIds = inputData.stream().map(ScenarioInput::getFactIdentifier).collect(Collectors.toList());

        List<String> outputNotValidIds = outputData.stream().map(ScenarioOutput::getFactIdentifier)
                .filter(output -> !inputIds.contains(output)).map(FactIdentifier::getName).collect(Collectors.toList());
        if (outputNotValidIds.size() > 0) {
            throw new ScenarioException("Some expected conditions are not linked to any given facts: " + String.join(", ", outputNotValidIds));
        }

        for (ScenarioInput input : inputData) {
            FactIdentifier factIdentifier = input.getFactIdentifier();
            List<ScenarioOutput> assertionOnFact = outputData.stream().filter(elem -> Objects.equals(elem.getFactIdentifier(), factIdentifier)).collect(toList());

            // check if this fact has something to check
            if (assertionOnFact.size() < 1) {
                continue;
            }

            scenarioResult.addAll(getScenarioResults(simulationDescriptor, assertionOnFact, input, classLoader));
        }

        return scenarioResult;
    }

    public static List<ScenarioResult> getScenarioResults(SimulationDescriptor simulationDescriptor,
                                                          List<ScenarioOutput> scenarioOutputsPerFact,
                                                          ScenarioInput input,
                                                          ClassLoader classLoader) {
        FactIdentifier factIdentifier = input.getFactIdentifier();
        Object factInstance = input.getValue();
        List<ScenarioResult> scenarioResults = new ArrayList<>();
        for (ScenarioOutput scenarioOutput : scenarioOutputsPerFact) {
            List<FactMappingValue> expectedResults = scenarioOutput.getExpectedResult();

            for (FactMappingValue expectedResult : expectedResults) {
                FactMappingValueOperator operator = expectedResult.getOperator();

                ExpressionIdentifier expressionIdentifier = expectedResult.getExpressionIdentifier();

                FactMapping factMapping = simulationDescriptor.getFactMapping(factIdentifier, expressionIdentifier)
                        .orElseThrow(() -> new IllegalStateException("Wrong expression, this should not happen"));

                List<String> pathToValue = factMapping.getExpressionElements().stream().map(ExpressionElement::getStep).collect(toList());
                Object expectedValue = convertValue(factMapping.getClassName(), expectedResult.getCleanValue(), classLoader);
                Object resultValue = ScenarioBeanUtil.navigateToObject(factInstance, pathToValue, false);

                Boolean conditionResult = new OperatorEvaluator().evaluate(operator, resultValue, expectedValue);

                scenarioResults.add(new ScenarioResult(factIdentifier, expectedResult, resultValue, conditionResult));
            }
        }
        return scenarioResults;
    }

    public static void validateAssertion(List<ScenarioResult> scenarioResults, Scenario scenario, EachTestNotifier singleNotifier) {
        boolean scenarioFailed = false;
        for (ScenarioResult scenarioResult : scenarioResults) {
            if (scenarioResult.getResult() == null || !scenarioResult.getResult()) {
                singleNotifier.addFailedAssumption(
                        new ScenarioAssumptionViolatedException(scenario, scenarioResult, new StringBuilder().append("Scenario '").append(scenario.getDescription())
                                .append("' has wrong assertion").toString()));
                scenarioFailed = true;
            }
        }

        if (scenarioFailed) {
            throw new ScenarioException("Scenario '" + scenario.getDescription() + "' failed");
        }
    }

    public static Map<List<String>, Object> getParamsForBean(SimulationDescriptor simulationDescriptor,
                                                             FactIdentifier factIdentifier,
                                                             List<FactMappingValue> factMappingValues,
                                                             ClassLoader classLoader) {
        Map<List<String>, Object> paramsForBean = new HashMap<>();

        for (FactMappingValue factMappingValue : factMappingValues) {
            ExpressionIdentifier expressionIdentifier = factMappingValue.getExpressionIdentifier();

            FactMapping factMapping = simulationDescriptor.getFactMapping(factIdentifier, expressionIdentifier)
                    .orElseThrow(() -> new IllegalStateException("Wrong expression, this should not happen"));

            List<String> pathToField = factMapping.getExpressionElements().stream()
                    .map(ExpressionElement::getStep).collect(toList());

            Object parsedValue = convertValue(factMapping.getClassName(), factMappingValue.getCleanValue(), classLoader);
            paramsForBean.put(pathToField, parsedValue);
        }

        return paramsForBean;
    }

    public static Map<FactIdentifier, List<FactMappingValue>> groupByFactIdentifierAndFilter(List<FactMappingValue> factMappingValues,
                                                                                             FactMappingType type) {
        Map<FactIdentifier, List<FactMappingValue>> groupByFactIdentifier = new HashMap<>();
        for (FactMappingValue factMappingValue : factMappingValues) {
            FactIdentifier factIdentifier = factMappingValue.getFactIdentifier();
            ExpressionIdentifier expressionIdentifier = factMappingValue.getExpressionIdentifier();
            if (expressionIdentifier == null) {
                throw new IllegalArgumentException("ExpressionIdentifier malformed");
            }

            if (!Objects.equals(expressionIdentifier.getType(), type)) {
                continue;
            }

            groupByFactIdentifier.computeIfAbsent(factIdentifier, key -> new ArrayList<>())
                    .add(factMappingValue);
        }
        return groupByFactIdentifier;
    }

    public static Object convertValue(String className, Object cleanValue, ClassLoader classLoader) {
        try {
            Class<?> clazz = loadClass(className, classLoader);

            // if it is not a String, it has to be an instance of the desired type
            if (!(cleanValue instanceof String)) {
                if (clazz.isInstance(cleanValue)) {
                    return cleanValue;
                }
                if (!isPrimitive(className) && cleanValue == null) {
                    return null;
                }
                throw new IllegalArgumentException(new StringBuilder().append("Object ").append(cleanValue)
                                                           .append(" is not a String or an instance of ").append(className).toString());
            }

            String value = (String) cleanValue;

            if (clazz.isAssignableFrom(String.class)) {
                return value;
            } else if (clazz.isAssignableFrom(Boolean.class) || clazz.isAssignableFrom(boolean.class)) {
                return Boolean.parseBoolean(value);
            } else if (clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(int.class)) {
                return Integer.parseInt(value);
            } else if (clazz.isAssignableFrom(Long.class) || clazz.isAssignableFrom(long.class)) {
                return Long.parseLong(value);
            } else if (clazz.isAssignableFrom(Double.class) || clazz.isAssignableFrom(double.class)) {
                return Double.parseDouble(value);
            } else if (clazz.isAssignableFrom(Float.class) || clazz.isAssignableFrom(float.class)) {
                return Float.parseFloat(value);
            }

            throw new IllegalArgumentException(new StringBuilder().append("Class ").append(className)
                                                       .append(" is not supported").toString());
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(new StringBuilder().append("Class ").append(className)
                                                       .append(" cannot be resolved").toString(), e);
        }
    }

    private static Class<?> loadClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
        if (primitiveMap.containsKey(className)) {
            return primitiveMap.get(className);
        }
        return classLoader.loadClass(className);
    }

    private static boolean isPrimitive(String className) {
        return primitiveMap.containsKey(className);
    }
}
