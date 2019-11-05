/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.backend.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValidationError;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNScenarioValidationTest {

    @Mock
    private DMNModel dmnModelMock;

    private Map<String, DecisionNode> mapOfMockDecisions = new HashMap<>();

    private Settings settingsLocal;

    @Before
    public void init() {
        settingsLocal = new Settings();
        settingsLocal.setType(ScenarioSimulationModel.Type.DMN);
        when(dmnModelMock.getDecisionByName(anyString()))
                .thenAnswer(invocation -> mapOfMockDecisions.get(invocation.getArguments()[0]));
    }

    @After
    public void end() {
        mapOfMockDecisions.clear();
    }

    @Test
    public void validate() {
        DMNScenarioValidation validationSpy = spy(new DMNScenarioValidation() {
            @Override
            protected DMNModel getDMNModel(KieContainer kieContainer, String dmnPath) {
                return dmnModelMock;
            }
        });

        // Test 0 - skip empty or not GIVEN/EXPECT columns
        Simulation test0 = new Simulation();
        test0.getScesimModelDescriptor().addFactMapping(
                FactIdentifier.DESCRIPTION,
                ExpressionIdentifier.create("value", FactMappingType.OTHER));
        test0.getScesimModelDescriptor().addFactMapping(
                FactIdentifier.EMPTY,
                ExpressionIdentifier.create("value", FactMappingType.GIVEN));

        List<FactMappingValidationError> errorsTest0 = validationSpy.validate(test0, settingsLocal, null);
        checkResult(errorsTest0);

        // Test 1 - simple type
        Simulation test1 = new Simulation();
        test1.getScesimModelDescriptor().addFactMapping(
                FactIdentifier.create("mySimpleType", "tMYSIMPLETYPE"),
                ExpressionIdentifier.create("value", FactMappingType.GIVEN));

        createDMNType("mySimpleType", "mySimpleType");

        List<FactMappingValidationError> errorsTest1 = validationSpy.validate(test1, settingsLocal,null);
        checkResult(errorsTest1);

        // Test 2 - nested field
        Simulation test2 = new Simulation();
        // nameFM is valid
        FactIdentifier myComplexFactIdentifier = FactIdentifier.create("myComplexType", "tMYCOMPLEXTYPE");
        FactMapping nameFM = test2.getScesimModelDescriptor().addFactMapping(
                myComplexFactIdentifier,
                ExpressionIdentifier.create("name", FactMappingType.GIVEN));
        nameFM.addExpressionElement("tMYCOMPLEXTYPE", "tMYCOMPLEXTYPE");
        nameFM.addExpressionElement("name", "tNAME");

        createDMNType("myComplexType", "myComplexType", "name");

        // parentFM is valid
        FactMapping parentFM = test2.getScesimModelDescriptor().addFactMapping(
                myComplexFactIdentifier,
                ExpressionIdentifier.create("parent", FactMappingType.EXPECT));
        parentFM.addExpressionElement("tMYCOMPLEXTYPE", "tMYCOMPLEXTYPE");
        parentFM.addExpressionElement("parent", "tPARENT");

        createDMNType("myComplexType", "myComplexType", "parent");

        List<FactMappingValidationError> errorsTest2 = validationSpy.validate(test2, settingsLocal,null);
        checkResult(errorsTest2);

        // parentFM is not valid anymore
        parentFM.addExpressionElement("notExisting", "notExisting");
        errorsTest2 = validationSpy.validate(test2, settingsLocal,null);
        checkResult(errorsTest2, "Impossible to find field 'notExisting' in type 'tPARENT'");

        // nameWrongTypeFM has a wrong type
        FactMapping nameWrongTypeFM = test2.getScesimModelDescriptor().addFactMapping(
                myComplexFactIdentifier,
                ExpressionIdentifier.create("parent2", FactMappingType.EXPECT));
        nameWrongTypeFM.addExpressionElement("tMYCOMPLEXTYPE", "tMYCOMPLEXTYPE");
        nameWrongTypeFM.addExpressionElement("name", Integer.class.getCanonicalName());
        errorsTest2 = validationSpy.validate(test2, settingsLocal,null);
        checkResult(errorsTest2,
                    "Impossible to find field 'notExisting' in type 'tPARENT'",
                    "Field type has changed: old 'java.lang.Integer', current 'tNAME'");

        // Test 3 - list
        Simulation test3 = new Simulation();
        // topLevelListFM is valid
        FactMapping topLevelListFM = test3.getScesimModelDescriptor().addFactMapping(
                FactIdentifier.create("myList", List.class.getCanonicalName()),
                ExpressionIdentifier.create("name", FactMappingType.GIVEN));
        topLevelListFM.addExpressionElement("tPERSON", List.class.getCanonicalName());
        topLevelListFM.setGenericTypes(Collections.singletonList("tPERSON"));

        createDMNType("myList", "person");
        when(mapOfMockDecisions.get("myList").getResultType().isCollection()).thenReturn(true);

        // addressesFM is valid
        FactMapping addressesFM = test3.getScesimModelDescriptor().addFactMapping(
                FactIdentifier.create("myComplexObject", "tMYCOMPLEXOBJECT"),
                ExpressionIdentifier.create("addresses", FactMappingType.EXPECT));
        addressesFM.addExpressionElement("tMYCOMPLEXOBJECT", "tMYCOMPLEXOBJECT");
        addressesFM.addExpressionElement("addresses", List.class.getCanonicalName());
        addressesFM.setGenericTypes(Collections.singletonList("tADDRESSES"));

        createDMNType("myComplexObject", "myComplexObject", "addresses");
        when(mapOfMockDecisions.get("myComplexObject").getResultType().getFields().get("addresses").isCollection()).thenReturn(true);

        List<FactMappingValidationError> errorsTest3 = validationSpy.validate(test3, settingsLocal,null);
        checkResult(errorsTest3);

        // Test 4 - complex type changed
        Simulation test4 = new Simulation();
        FactMapping factMappingChanged = test4.getScesimModelDescriptor().addFactMapping(
                FactIdentifier.create("mySimpleType", "tMYSIMPLETYPE"),
                ExpressionIdentifier.create("value", FactMappingType.GIVEN));
        factMappingChanged.addExpressionElement("tMYSIMPLETYPE", "tMYSIMPLETYPE");

        createDMNType("mySimpleType", "mySimpleType", "name");

        List<FactMappingValidationError> errorsTest4 = validationSpy.validate(test4, settingsLocal,null);
        checkResult(errorsTest4, "Node type has changed: old 'tMYSIMPLETYPE', current 'tMYSIMPLETYPE'");

        // Test 5 - not existing node
        Simulation test5 = new Simulation();
        FactMapping factMappingNodeRemoved = test5.getScesimModelDescriptor().addFactMapping(
                FactIdentifier.create("mySimpleType", "tMYSIMPLETYPE"),
                ExpressionIdentifier.create("value", FactMappingType.GIVEN));
        factMappingNodeRemoved.addExpressionElement("tMYSIMPLETYPE", "tMYSIMPLETYPE");

        when(dmnModelMock.getDecisionByName(anyString())).thenReturn(null);
        List<FactMappingValidationError> errorsTest5 = validationSpy.validate(test5, settingsLocal,null);
        checkResult(errorsTest5, "Node type has changed: old 'tMYSIMPLETYPE', current 'node not found'");
    }

    private void checkResult(List<FactMappingValidationError> validationErrors, String... expectedErrors) {
        if (expectedErrors.length == 0) {
            assertEquals(0, validationErrors.size());
        }

        for (String expectedError : expectedErrors) {
            assertTrue("Expected error: '" + expectedError + "' not found",
                       validationErrors.stream().anyMatch(
                               validationError -> Objects.equals(expectedError, validationError.getErrorMessage())));
        }
    }

    private void createDMNType(String decisionName, String rootType, String... steps) {
        DecisionNode decisionNodeMock = getOrCreateDecisionNode(decisionName, rootType);

        DMNType currentType = decisionNodeMock.getResultType();
        for (String step : steps) {
            currentType = addStep(currentType, step);
        }

        mapOfMockDecisions.put(decisionName, decisionNodeMock);
    }

    private DecisionNode getOrCreateDecisionNode(String decisionName, String typeName) {
        DecisionNode decisionNodeMock;
        if (mapOfMockDecisions.containsKey(decisionName)) {
            decisionNodeMock = mapOfMockDecisions.get(decisionName);
            String decisionTypeName = decisionNodeMock.getResultType().getName();
            if (!Objects.equals(decisionTypeName, createDMNTypeName(typeName))) {
                throw new IllegalArgumentException(
                        "Decision with name " + decisionName + " already created of type " + decisionTypeName);
            }
        } else {
            decisionNodeMock = mock(DecisionNode.class);
            mapOfMockDecisions.put(decisionName, decisionNodeMock);
            when(decisionNodeMock.getName()).thenReturn(decisionName);
            DMNType initDMNType = initDMNType(typeName);
            when(decisionNodeMock.getResultType()).thenReturn(initDMNType);
        }
        return decisionNodeMock;
    }

    private DMNType addStep(DMNType dmnTypeMock, String field) {
        DMNType nestedDmnTypeMock = initDMNType(field);
        dmnTypeMock.getFields().put(field, nestedDmnTypeMock);
        return nestedDmnTypeMock;
    }

    private DMNType initDMNType(String name) {
        DMNType dmnTypeMock = mock(BaseDMNTypeImpl.class);
        when(dmnTypeMock.getFields()).thenReturn(new HashMap<>());
        String type = createDMNTypeName(name);
        when(dmnTypeMock.getName()).thenReturn(type);
        when(dmnTypeMock.isComposite())
                .thenAnswer(invocation -> ((DMNType) invocation.getMock()).getFields().size() != 0);
        return dmnTypeMock;
    }

    private String createDMNTypeName(String name) {
        return "t" + name.toUpperCase();
    }
}