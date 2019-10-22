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

package org.kie.workbench.common.stunner.bpmn.project.backend.forms.conditions.parser;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.Condition;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.project.backend.forms.conditions.parser.ConditionTestCommons.binaryFunctions;
import static org.kie.workbench.common.stunner.bpmn.project.backend.forms.conditions.parser.ConditionTestCommons.buildBinaryConditionScript;
import static org.kie.workbench.common.stunner.bpmn.project.backend.forms.conditions.parser.ConditionTestCommons.buildTernaryConditionScript;
import static org.kie.workbench.common.stunner.bpmn.project.backend.forms.conditions.parser.ConditionTestCommons.buildUnaryConditionScript;
import static org.kie.workbench.common.stunner.bpmn.project.backend.forms.conditions.parser.ConditionTestCommons.stringParams;
import static org.kie.workbench.common.stunner.bpmn.project.backend.forms.conditions.parser.ConditionTestCommons.ternaryFunctions;
import static org.kie.workbench.common.stunner.bpmn.project.backend.forms.conditions.parser.ConditionTestCommons.unaryFunctions;
import static org.kie.workbench.common.stunner.bpmn.project.backend.forms.conditions.parser.ConditionTestCommons.variableParams;

public class ConditionGeneratorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testGenerateUnaryFunctions() throws Exception {
        for (String function : unaryFunctions) {
            testGenerateUnaryCondition(function);
        }
    }

    @Test
    public void testGenerateBinaryFunctions() throws Exception {
        for (String function : binaryFunctions) {
            testGenerateBinaryFunction(function);
        }
    }

    @Test
    public void testGenerateTernaryFunctions() throws Exception {
        for (String function : ternaryFunctions) {
            testGenerateTernaryFunction(function);
        }
    }

    @Test
    public void testMissingConditionError() throws Exception {
        ConditionGenerator generator = new ConditionGenerator();
        expectedException.expectMessage("A condition must be provided");
        generator.generateScript(null);
    }

    @Test
    public void testFunctionNotFoundError() throws Exception {
        ConditionGenerator generator = new ConditionGenerator();
        Condition condition = new Condition("SomeNonExistingFunction");
        expectedException.expectMessage("Function SomeNonExistingFunction was not found in current functions definitions");
        generator.generateScript(condition);
    }

    @Test
    public void testParamIsNullError() throws Exception {
        ConditionGenerator generator = new ConditionGenerator();
        Condition condition = new Condition("startsWith");
        condition.addParam("variable");
        condition.addParam(null);
        expectedException.expectMessage("Parameter can not be null nor empty");
        generator.generateScript(condition);
    }

    private void testGenerateUnaryCondition(String function) throws GenerateConditionException {
        ConditionGenerator generator = new ConditionGenerator();
        for (String param : variableParams) {
            Condition condition = new Condition(function);
            condition.addParam(param);
            String expectedScript = buildUnaryConditionScript(function, param);
            assertEquals(expectedScript, generator.generateScript(condition));
        }
    }

    private void testGenerateBinaryFunction(String function) throws GenerateConditionException {
        ConditionGenerator generator = new ConditionGenerator();
        for (int i = 0; i < variableParams.size(); i++) {
            Condition condition = new Condition(function);
            condition.addParam(variableParams.get(i));
            condition.addParam(stringParams.get(i));
            String expectedScript = buildBinaryConditionScript(function, variableParams.get(i), stringParams.get(i));
            assertEquals(expectedScript, generator.generateScript(condition));
        }
    }

    private void testGenerateTernaryFunction(String function) throws GenerateConditionException {
        ConditionGenerator generator = new ConditionGenerator();
        for (int i = 0; i < variableParams.size(); i++) {
            Condition condition = new Condition(function);
            condition.addParam(variableParams.get(i));
            condition.addParam(stringParams.get(i));
            condition.addParam(stringParams.get(i));
            String expectedScript = buildTernaryConditionScript(function, variableParams.get(i), stringParams.get(i), stringParams.get(i));
            assertEquals(expectedScript, generator.generateScript(condition));
        }
    }
}
