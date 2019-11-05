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

package org.drools.workbench.screens.scenariosimulation.businesscentral.client.editor.strategies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.google.gwtmockito.GwtMockitoTestRunner;
import junit.framework.TestCase;
import org.drools.workbench.screens.scenariosimulation.client.TestProperties;
import org.drools.workbench.screens.scenariosimulation.client.editor.AbstractScenarioSimulationEditorTest;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;
import org.uberfire.client.callbacks.Callback;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.soup.project.datamodel.oracle.ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class BusinessCentralDMODataManagementStrategyTest extends AbstractScenarioSimulationEditorTest {

    private BusinessCentralDMODataManagementStrategy businessCentralDmoDataManagementStrategy;

    @Mock
    private AsyncPackageDataModelOracle oracleMock;

    private BusinessCentralDMODataManagementStrategy.ResultHolder factModelTreeHolderlocal;

    private FactModelTuple factModelTupleLocal;

    @Before
    public void setup() {
        super.setup();
        factModelTupleLocal = new FactModelTuple(new TreeMap<>(), new TreeMap<>());
        factModelTreeHolderlocal = new BusinessCentralDMODataManagementStrategy.ResultHolder();
        factModelTreeHolderlocal.setFactModelTuple(factModelTupleLocal);
        when(oracleMock.getFQCNByFactName(TestProperties.FACT_NAME)).thenReturn(TestProperties.FULL_FACT_CLASSNAME);
        when(oracleFactoryMock.makeAsyncPackageDataModelOracle(observablePathMock, modelLocal, content.getDataModel())).thenReturn(oracleMock);
        this.businessCentralDmoDataManagementStrategy = spy(new BusinessCentralDMODataManagementStrategy(oracleFactoryMock) {
            {
                this.oracle = oracleMock;
                this.factModelTreeHolder = factModelTreeHolderlocal;
            }
        });
    }

    @Test
    public void populateTestToolsWithFactTuple() {
        final FactModelTuple factModelTupleMock = mock(FactModelTuple.class);
        factModelTreeHolderlocal.setFactModelTuple(factModelTupleMock);
        doNothing().when(businessCentralDmoDataManagementStrategy).storeData(eq(factModelTupleMock), eq(testToolsPresenterMock), eq(scenarioSimulationContextLocal));
        businessCentralDmoDataManagementStrategy.populateTestTools(testToolsPresenterMock, scenarioSimulationContextLocal);
        verify(businessCentralDmoDataManagementStrategy, times(1)).storeData(eq(factModelTupleMock), eq(testToolsPresenterMock), eq(scenarioSimulationContextLocal));
    }

    @Test
    public void populateTestToolsWithoutFactTupleEmptyOracle() {
        factModelTreeHolderlocal.setFactModelTuple(null);
        Map<String, List<String>> alreadyAssignedProperties = new HashMap<>();
        doReturn(alreadyAssignedProperties).when(businessCentralDmoDataManagementStrategy).getPropertiesToHide(scenarioGridModelMock);
        String[] emptyFactTypes = {};
        when(oracleMock.getFactTypes()).thenReturn(emptyFactTypes);
        businessCentralDmoDataManagementStrategy.populateTestTools(testToolsPresenterMock, scenarioSimulationContextLocal);
        verify(businessCentralDmoDataManagementStrategy, never()).aggregatorCallback(eq(testToolsPresenterMock), anyInt(), any(SortedMap.class), eq(scenarioSimulationContextLocal), isA(List.class));
        verify(oracleMock, never()).getFieldCompletions(anyString(), any(Callback.class));
    }

    @Test
    public void populateTestToolsWithoutFactTupleWithOnlyDataObjects() {
        factModelTreeHolderlocal.setFactModelTuple(null);
        Map<String, List<String>> alreadyAssignedProperties = new HashMap<>();
        doReturn(alreadyAssignedProperties).when(businessCentralDmoDataManagementStrategy).getPropertiesToHide(scenarioGridModelMock);
        String[] notEmptyFactTypes = getRandomStringArray();
        when(oracleMock.getFactTypes()).thenReturn(notEmptyFactTypes);
        businessCentralDmoDataManagementStrategy.populateTestTools(testToolsPresenterMock, scenarioSimulationContextLocal);
        verify(businessCentralDmoDataManagementStrategy, times(1)).aggregatorCallback(eq(testToolsPresenterMock), anyInt(), any(SortedMap.class), eq(scenarioSimulationContextLocal), isA(List.class));
        for (String factType : notEmptyFactTypes) {
            verify(oracleMock, times(1)).getFieldCompletions(eq(factType), any(Callback.class));
        }
    }

    @Test
    public void populateTestToolsWithoutFactTupleWithDataObjectsAndSimpleJavaTypes() {
        factModelTreeHolderlocal.setFactModelTuple(null);
        Map<String, List<String>> alreadyAssignedProperties = new HashMap<>();
        doReturn(alreadyAssignedProperties).when(businessCentralDmoDataManagementStrategy).getPropertiesToHide(scenarioGridModelMock);
        String[] dataObjectTypes = getRandomStringArray();
        String[] simpleJavaTypes = getSimpleTypeArray();
        String[] notEmptyFactTypes = mergeArrays(dataObjectTypes, simpleJavaTypes);
        when(oracleMock.getFactTypes()).thenReturn(notEmptyFactTypes);
        businessCentralDmoDataManagementStrategy.populateTestTools(testToolsPresenterMock, scenarioSimulationContextLocal);
        verify(businessCentralDmoDataManagementStrategy, times(1)).aggregatorCallback(eq(testToolsPresenterMock), anyInt(), any(SortedMap.class), eq(scenarioSimulationContextLocal), isA(List.class));
        for (String factType : dataObjectTypes) {
            verify(oracleMock, times(1)).getFieldCompletions(eq(factType), any(Callback.class));
        }
    }

    @Test
    public void populateTestToolsWithoutFactTupleWithoutDataObjectsWithSimpleJavaTypes() {
        factModelTreeHolderlocal.setFactModelTuple(null);
        Map<String, List<String>> alreadyAssignedProperties = new HashMap<>();
        doReturn(alreadyAssignedProperties).when(businessCentralDmoDataManagementStrategy).getPropertiesToHide(scenarioGridModelMock);
        String[] notEmptyFactTypes = getSimpleTypeArray();
        List<String> simpleJavaTypes = Arrays.asList(notEmptyFactTypes);
        when(oracleMock.getFactTypes()).thenReturn(notEmptyFactTypes);
        businessCentralDmoDataManagementStrategy.populateTestTools(testToolsPresenterMock, scenarioSimulationContextLocal);
        verify(businessCentralDmoDataManagementStrategy, never()).aggregatorCallback(eq(testToolsPresenterMock), anyInt(), any(SortedMap.class), eq(scenarioSimulationContextLocal), isA(List.class));
        verify(businessCentralDmoDataManagementStrategy, times(1)).aggregatorCallbackMethod(eq(testToolsPresenterMock), eq(0), any(SortedMap.class), eq(scenarioSimulationContextLocal), eq(null), eq(simpleJavaTypes));
    }

    @Test
    public void manageScenarioSimulationModelContent() {
        businessCentralDmoDataManagementStrategy.manageScenarioSimulationModelContent(observablePathMock, content);
        assertEquals(businessCentralDmoDataManagementStrategy.oracle, oracleMock);
    }

    @Test
    public void isADataType() {
        when(oracleMock.getFactTypes()).thenReturn(new String[]{});
        commonIsADataType("TEST", false);
        when(oracleMock.getFactTypes()).thenReturn(new String[]{"TEST"});
        commonIsADataType("TOAST", false);
        commonIsADataType("TEST", true);
    }

    @Test
    public void fieldCompletionsCallbackMethod() {
        ModelField[] result = {};
        Callback<FactModelTree> aggregatorCallbackMock = mock(Callback.class);
        businessCentralDmoDataManagementStrategy.fieldCompletionsCallbackMethod(TestProperties.FACT_NAME, result, aggregatorCallbackMock);
        verify(businessCentralDmoDataManagementStrategy, times(1)).getFactModelTree(eq(TestProperties.FACT_NAME), eq(result));
        verify(aggregatorCallbackMock, times(1)).callback(isA(FactModelTree.class));
    }

    @Test
    public void getFactModelTree() {
        Map<String, String> simpleProperties = getSimplePropertiesInner();
        final ModelField[] modelFields = getModelFieldsInner(simpleProperties);
        final FactModelTree retrieved = businessCentralDmoDataManagementStrategy.getFactModelTree(TestProperties.FACT_NAME, modelFields);
        assertNotNull(retrieved);
        Assert.assertEquals(TestProperties.FACT_NAME, retrieved.getFactName());
        assertEquals("", retrieved.getFullPackage());
    }

    @Test
    public void getInstanceMap() {
        FactModelTree toPopulate = getFactModelTreeInner(randomAlphabetic(3));
        final Map<String, String> simpleProperties = toPopulate.getSimpleProperties();
        final Collection<String> values = simpleProperties.values();
        SortedMap<String, FactModelTree> factTypeFieldsMap = getFactTypeFieldsMapInner(values);
        SortedMap<String, FactModelTree> retrieved = businessCentralDmoDataManagementStrategy.getInstanceMap(factTypeFieldsMap);
        assertNotNull(retrieved);
    }

    @Test
    public void populateGenericTypeMap() {
        commonPopulateGenericTypeMap(true);
        commonPopulateGenericTypeMap(false);
    }

    @Test
    public void aggregatorCallbackMethodWithResultAllElements() {
        SortedMap<String, FactModelTree> factTypeFieldsMap = getRandomMap();
        String resultName = "RESULT_NAME";
        FactModelTree resultMock = mock(FactModelTree.class);
        when(resultMock.getFactName()).thenReturn(resultName);
        List<String> simpleJavaTypes = Arrays.asList(getSimpleTypeArray());
        factModelTreeHolderlocal.setFactModelTuple(null);
        businessCentralDmoDataManagementStrategy.aggregatorCallbackMethod(testToolsPresenterMock, factTypeFieldsMap.size() + 1, factTypeFieldsMap, scenarioSimulationContextLocal, resultMock, simpleJavaTypes);
        verify(resultMock, times(1)).getFactName();
        assertTrue(factTypeFieldsMap.containsKey(resultName));
        assertEquals(resultMock, factTypeFieldsMap.get(resultName));
        factTypeFieldsMap.values().forEach(factModelTree -> verify(businessCentralDmoDataManagementStrategy, times(1)).populateFactModelTree(eq(factModelTree), eq(factTypeFieldsMap)));
        assertNotNull(factModelTreeHolderlocal.getFactModelTuple());
        verify(businessCentralDmoDataManagementStrategy, times(1)).storeData(eq(factModelTreeHolderlocal.getFactModelTuple()), eq(testToolsPresenterMock), eq(scenarioSimulationContextLocal));
    }

    @Test
    public void aggregatorCallbackMethodWithResultNotAllElements() {
        SortedMap<String, FactModelTree> factTypeFieldsMap = getRandomMap();
        String resultName = "RESULT_NAME";
        FactModelTree resultMock = mock(FactModelTree.class);
        when(resultMock.getFactName()).thenReturn(resultName);
        List<String> simpleJavaTypes = Arrays.asList(getSimpleTypeArray());
        factModelTreeHolderlocal.setFactModelTuple(null);
        businessCentralDmoDataManagementStrategy.aggregatorCallbackMethod(testToolsPresenterMock, factTypeFieldsMap.size() + 10, factTypeFieldsMap, scenarioSimulationContextLocal, resultMock, simpleJavaTypes);
        verify(resultMock, times(1)).getFactName();
        assertTrue(factTypeFieldsMap.containsKey(resultName));
        assertEquals(resultMock, factTypeFieldsMap.get(resultName));
        verify(businessCentralDmoDataManagementStrategy, never()).populateFactModelTree(isA(FactModelTree.class), isA(SortedMap.class));
        assertNull(factModelTreeHolderlocal.getFactModelTuple());
        verify(businessCentralDmoDataManagementStrategy, never()).storeData(isA(FactModelTuple.class), eq(testToolsPresenterMock), eq(scenarioSimulationContextLocal));
    }

    @Test
    public void aggregatorCallbackMethodWithoutResultAllElements() {
        SortedMap<String, FactModelTree> factTypeFieldsMap = getRandomMap();
        List<String> simpleJavaTypes = Arrays.asList(getSimpleTypeArray());
        factModelTreeHolderlocal.setFactModelTuple(null);
        int previousSizeMap = factTypeFieldsMap.size();
        businessCentralDmoDataManagementStrategy.aggregatorCallbackMethod(testToolsPresenterMock, factTypeFieldsMap.size(), factTypeFieldsMap, scenarioSimulationContextLocal, null, simpleJavaTypes);
        assertEquals(previousSizeMap, factTypeFieldsMap.size());
        factTypeFieldsMap.values().forEach(factModelTree -> verify(businessCentralDmoDataManagementStrategy, times(1)).populateFactModelTree(eq(factModelTree), eq(factTypeFieldsMap)));
        assertNotNull(factModelTreeHolderlocal.getFactModelTuple());
        verify(businessCentralDmoDataManagementStrategy, times(1)).storeData(eq(factModelTreeHolderlocal.getFactModelTuple()), eq(testToolsPresenterMock), eq(scenarioSimulationContextLocal));
    }

    @Test
    public void aggregatorCallbackMethodWithOutResultNotAllElements() {
        SortedMap<String, FactModelTree> factTypeFieldsMap = getRandomMap();
        List<String> simpleJavaTypes = Arrays.asList(getSimpleTypeArray());
        int previousSizeMap = factTypeFieldsMap.size();
        factModelTreeHolderlocal.setFactModelTuple(null);
        businessCentralDmoDataManagementStrategy.aggregatorCallbackMethod(testToolsPresenterMock, factTypeFieldsMap.size() + 10, factTypeFieldsMap, scenarioSimulationContextLocal, null, simpleJavaTypes);
        assertEquals(previousSizeMap, factTypeFieldsMap.size());
        verify(businessCentralDmoDataManagementStrategy, never()).populateFactModelTree(isA(FactModelTree.class), isA(SortedMap.class));
        assertNull(factModelTreeHolderlocal.getFactModelTuple());
        verify(businessCentralDmoDataManagementStrategy, never()).storeData(isA(FactModelTuple.class), eq(testToolsPresenterMock), eq(scenarioSimulationContextLocal));
    }

    private void commonIsADataType(String value, boolean expected) {
        boolean retrieved = businessCentralDmoDataManagementStrategy.isADataType(value);
        if (expected) {
            TestCase.assertTrue(retrieved);
        } else {
            assertFalse(retrieved);
        }
    }

    private void commonPopulateGenericTypeMap(boolean isList) {
        Map<String, List<String>> toPopulate = new HashMap<>();
        String factName = "FACT_NAME";
        String propertyName = "PROPERTY_NAME";
        String factType = "Book";
        String fullFactType = "com." + factType;
        when(oracleMock.getParametricFieldType(factName, propertyName)).thenReturn(factType);
        when(oracleMock.getFQCNByFactName(factType)).thenReturn(fullFactType);
        businessCentralDmoDataManagementStrategy.populateGenericTypeMap(toPopulate, factName, propertyName, isList);
        assertTrue(toPopulate.containsKey(propertyName));
        final List<String> retrieved = toPopulate.get(propertyName);
        if (!isList) {
            assertEquals(String.class.getName(), retrieved.get(0));
        }
        assertEquals(fullFactType, retrieved.get(retrieved.size() - 1));
    }

    private ModelField[] getModelFieldsInner(Map<String, String> simpleProperties) {
        List<ModelField> toReturn = new ArrayList<>();
        simpleProperties.forEach((key, value) -> toReturn.add(getModelFieldInner(key, value, "String")));
        return toReturn.toArray(new ModelField[toReturn.size()]);
    }

    private ModelField getModelFieldInner(final String name,
                                          final String clazz,
                                          final String type) {
        return new ModelField(name,
                              clazz,
                              REGULAR_CLASS,
                              ModelField.FIELD_ORIGIN.DECLARED,
                              FieldAccessorsAndMutators.BOTH, type);
    }

    private FactModelTree getFactModelTreeInner(String factName) {
        return new FactModelTree(factName, AbstractScenarioSimulationEditorTest.SCENARIO_PACKAGE, getSimplePropertiesInner(), new HashMap<>());
    }

    private SortedMap<String, FactModelTree> getFactTypeFieldsMapInner(Collection<String> keys) {
        return new TreeMap<>(keys.stream()
                                     .collect(Collectors.toMap(key -> key,
                                                               key -> (FactModelTree) getFactModelTreeInner(key))));
    }

    private Map<String, String> getSimplePropertiesInner() {
        String[] keys = getRandomStringArray();
        return Arrays.stream(keys)
                .collect(Collectors.toMap(key -> key,
                                          key -> key += "_VALUE"));
    }

    private String[] getRandomStringArray() {
        return new String[]{randomAlphabetic(3), randomAlphabetic(4), randomAlphabetic(5)};
    }

    private String[] getSimpleTypeArray() {
        return new String[]{"Integer", "String"};
    }

    private String[] mergeArrays(String[] first, String[] second) {
        String[] toReturn = new String[first.length + second.length];
        for (int i = 0; i < first.length; i++) {
            toReturn[i] = first[i];
        }
        for (int i = 0; i < second.length; i++) {
            toReturn[i + first.length] = second[i];
        }
        return toReturn;
    }

    private SortedMap<String, FactModelTree> getRandomMap() {
        SortedMap<String, FactModelTree> toReturn = new TreeMap<>();
        String[] dataObjects = getRandomStringArray();
        for (String factName : dataObjects) {
            toReturn.put(factName, getFactModelTreeInner(factName));
        }
        return toReturn;
    }
}

