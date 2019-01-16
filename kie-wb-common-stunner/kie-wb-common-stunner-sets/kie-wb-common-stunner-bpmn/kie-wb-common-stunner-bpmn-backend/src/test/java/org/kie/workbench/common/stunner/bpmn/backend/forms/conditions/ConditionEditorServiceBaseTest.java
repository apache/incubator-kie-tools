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

package org.kie.workbench.common.stunner.bpmn.backend.forms.conditions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.Condition;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.ConditionEditorService;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.FieldMetadata;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.FunctionDef;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.GenerateConditionResult;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.ParseConditionResult;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.TypeMetadata;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.TypeMetadataQuery;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.TypeMetadataQueryResult;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.backend.forms.conditions.parser.ConditionTestCommons.binaryFunctions;
import static org.kie.workbench.common.stunner.bpmn.backend.forms.conditions.parser.ConditionTestCommons.buildBinaryConditionScript;
import static org.kie.workbench.common.stunner.bpmn.backend.forms.conditions.parser.ConditionTestCommons.buildBinaryConditionScripts;
import static org.kie.workbench.common.stunner.bpmn.backend.forms.conditions.parser.ConditionTestCommons.failingBinaryFunctionExpressions;
import static org.kie.workbench.common.stunner.bpmn.backend.forms.conditions.parser.ConditionTestCommons.stringParams;
import static org.kie.workbench.common.stunner.bpmn.backend.forms.conditions.parser.ConditionTestCommons.variableParams;

@RunWith(MockitoJUnitRunner.class)
public abstract class ConditionEditorServiceBaseTest {

    protected ConditionEditorService service;

    protected Path path;

    @Before
    public void setUp() {
        service = createService();
    }

    protected abstract ConditionEditorService createService();

    @Test
    public void testFindAvailableFunctions() {
        String[] numericFunctions = {"between", "equalsTo", "greaterThan", "greaterOrEqualThan", "lessThan", "lessOrEqualThan", "isNull"};
        String[] stringFunctions = {"contains", "isEmpty", "startsWith", "endsWith", "equalsTo", "isNull"};
        String[] booleanFunctions = {"isTrue", "isFalse", "isNull"};
        String[] objectFunctions = {"isNull"};

        testFindAvailableFunctions(Short.class.getName(), numericFunctions);
        testFindAvailableFunctions(Integer.class.getName(), numericFunctions);
        testFindAvailableFunctions(Long.class.getName(), numericFunctions);
        testFindAvailableFunctions(Float.class.getName(), numericFunctions);
        testFindAvailableFunctions(Double.class.getName(), numericFunctions);
        testFindAvailableFunctions(BigDecimal.class.getName(), numericFunctions);
        testFindAvailableFunctions(BigInteger.class.getName(), numericFunctions);

        testFindAvailableFunctions(String.class.getName(), stringFunctions);
        testFindAvailableFunctions(Boolean.class.getName(), booleanFunctions);
        testFindAvailableFunctions(Object.class.getName(), objectFunctions);
    }

    private void testFindAvailableFunctions(String clazz, String[] expectedFunctions) {
        List<FunctionDef> functionDefs = service.findAvailableFunctions(path, clazz);
        assertEquals(functionDefs.size(), expectedFunctions.length);
        for (String expectedFunction : expectedFunctions) {
            assertTrue(functionDefs.stream()
                               .anyMatch(functionDef -> expectedFunction.equals(functionDef.getName())));
        }
    }

    @Test
    public void testFindMetadata() {
        Set<String> types = new HashSet<>();
        types.add(Bean1.class.getName());
        types.add(Bean2.class.getName());
        types.add("non.existing.class.Bean");
        TypeMetadataQuery query = new TypeMetadataQuery(path, types);
        TypeMetadataQueryResult result = service.findMetadata(query);
        assertTrue(result.getMissingTypes().contains("non.existing.class.Bean"));
        TypeMetadata bean1Metadata = result.getTypeMetadatas().stream().filter(typeMetadata -> typeMetadata.getType().equals(Bean1.class.getName())).findFirst().orElse(null);
        TypeMetadata bean2Metadata = result.getTypeMetadatas().stream().filter(typeMetadata -> typeMetadata.getType().equals(Bean2.class.getName())).findFirst().orElse(null);

        assertNotNull(bean1Metadata);
        assertNotNull(bean2Metadata);

        assertHasField(bean1Metadata, "fieldBean1_1", String.class.getName(), "getFieldBean1_1", "setFieldBean1_1");
        assertHasField(bean1Metadata, "fieldBean1_2", "int", "getFieldBean1_2", null);
        assertHasField(bean1Metadata, "fieldBean1_3", "boolean", "isFieldBean1_3", "setFieldBean1_3");
        assertHasField(bean1Metadata, "fieldBean1_4", Integer.class.getName(), null, "setFieldBean1_4");

        assertHasField(bean2Metadata, "fieldBean1_1", String.class.getName(), "getFieldBean1_1", "setFieldBean1_1");
        assertHasField(bean2Metadata, "fieldBean1_2", "int", "getFieldBean1_2", null);
        assertHasField(bean2Metadata, "fieldBean1_3", "boolean", "isFieldBean1_3", "setFieldBean1_3");
        assertHasField(bean2Metadata, "fieldBean1_4", Integer.class.getName(), null, "setFieldBean1_4");
        assertHasField(bean2Metadata, "fieldBean2_1", String.class.getName(), "getFieldBean2_1", "setFieldBean2_1");
        assertHasField(bean2Metadata, "fieldBean2_2", Object.class.getName(), "getFieldBean2_2", "setFieldBean2_2");
    }

    private void assertHasField(TypeMetadata typeMetadata, String fieldName, String type, String accessor, String mutator) {
        FieldMetadata fieldMetadata = typeMetadata.getFieldMetadata().stream().filter(fieldMetadata1 -> fieldMetadata1.getName().equals(fieldName)).findFirst().orElse(null);
        assertNotNull(fieldMetadata);
        assertEquals(type, fieldMetadata.getType());
        assertEquals(accessor, fieldMetadata.getAccessor());
        assertEquals(mutator, fieldMetadata.getMutator());
    }

    @Test
    public void testParseConditionSuccessful() {
        for (String function : binaryFunctions) {
            testParseConditionSuccessful(function);
        }
    }

    @Test
    public void testParseConditionUnsuccessful() {
        for (String function : binaryFunctions) {
            testParseConditionUnsuccessful(function);
        }
    }

    @Test
    public void testGenerateConditionSuccessful() {
        for (String function : binaryFunctions) {
            testGenerateConditionSuccessful(function);
        }
    }

    @Test
    public void testGenerateConditionUnSuccessful() {
        GenerateConditionResult result = service.generateCondition(null);
        assertTrue(result.hasError());
        assertEquals("A condition must be provided", result.getError());

        Condition condition = new Condition("SomeNonExistingFunction");
        result = service.generateCondition(condition);
        assertTrue(result.hasError());
        assertEquals("Function SomeNonExistingFunction was not found in current functions definitions", result.getError());

        condition = new Condition("startsWith");
        condition.addParam("variable");
        condition.addParam(null);
        result = service.generateCondition(condition);
        assertTrue(result.hasError());
        assertEquals("Parameter can not be null nor empty", result.getError());
    }

    private void testParseConditionSuccessful(String function) {
        List<String> conditions = buildBinaryConditionScripts(function);
        for (int i = 0; i < conditions.size(); i++) {
            ParseConditionResult result = service.parseCondition(conditions.get(i));
            assertFalse(result.hasError());
            assertEquals(function, result.getCondition().getFunction());
            assertEquals(2, result.getCondition().getParams().size());
            assertEquals(variableParams.get(i), result.getCondition().getParams().get(0));
            assertEquals(stringParams.get(i), result.getCondition().getParams().get(1));
        }
    }

    private void testParseConditionUnsuccessful(String function) {
        failingBinaryFunctionExpressions.forEach(binaryFunctionExpression -> {
            String expression = String.format(binaryFunctionExpression, "KieFunctions." + function);
            ParseConditionResult result = service.parseCondition(expression);
            assertTrue(result.hasError());
        });
    }

    private void testGenerateConditionSuccessful(String function) {
        for (int i = 0; i < variableParams.size(); i++) {
            Condition condition = new Condition(function);
            condition.addParam(variableParams.get(i));
            condition.addParam(stringParams.get(i));
            GenerateConditionResult result = service.generateCondition(condition);
            assertFalse(result.hasError());
            String expectedScript = buildBinaryConditionScript(function, variableParams.get(i), stringParams.get(i));
            assertEquals(expectedScript, result.getExpression());
        }
    }
}
