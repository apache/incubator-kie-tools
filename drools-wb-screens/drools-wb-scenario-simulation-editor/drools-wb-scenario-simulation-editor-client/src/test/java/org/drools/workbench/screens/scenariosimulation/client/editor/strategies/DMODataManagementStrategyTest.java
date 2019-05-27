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

package org.drools.workbench.screens.scenariosimulation.client.editor.strategies;

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
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.jgroups.util.Util;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;
import org.uberfire.client.callbacks.Callback;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FACT_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FULL_FACT_CLASSNAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.LOWER_CASE_VALUE;
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
public class DMODataManagementStrategyTest extends AbstractDataManagementStrategyTest {

    private DMODataManagementStrategy dmoDataManagementStrategy;

    @Mock
    private AsyncPackageDataModelOracle oracleMock;

    @Before
    public void setup() {
        super.setup();
        when(oracleMock.getFQCNByFactName(FACT_NAME)).thenReturn(FULL_FACT_CLASSNAME);
        when(oracleFactoryMock.makeAsyncPackageDataModelOracle(observablePathMock, modelLocal, content.getDataModel())).thenReturn(oracleMock);
        this.dmoDataManagementStrategy = spy(new DMODataManagementStrategy(oracleFactoryMock, scenarioSimulationContextLocal) {
            {
                this.oracle = oracleMock;
            }
        });
        abstractDataManagementStrategySpy = dmoDataManagementStrategy;
    }

    @Test
    public void populateTestToolsWithFactTuple() {
        final FactModelTuple factModelTupleMock = mock(FactModelTuple.class);
        dmoDataManagementStrategy.factModelTreeHolder.factModelTuple = factModelTupleMock;
        doNothing().when(dmoDataManagementStrategy).storeData(eq(factModelTupleMock), eq(testToolsPresenterMock), eq(scenarioGridModelMock));
        dmoDataManagementStrategy.populateTestTools(testToolsPresenterMock, scenarioGridModelMock);
        verify(dmoDataManagementStrategy, times(1)).storeData(eq(factModelTupleMock), eq(testToolsPresenterMock), eq(scenarioGridModelMock));
    }

    @Test
    public void populateTestToolsWithoutFactTupleEmptyOracle() {
        dmoDataManagementStrategy.factModelTreeHolder.factModelTuple = null;
        Map<String, List<String>> alreadyAssignedProperties = new HashMap<>();
        doReturn(alreadyAssignedProperties).when(dmoDataManagementStrategy).getPropertiesToHide(scenarioGridModelMock);
        String[] emptyFactTypes = {};
        when(oracleMock.getFactTypes()).thenReturn(emptyFactTypes);
        dmoDataManagementStrategy.populateTestTools(testToolsPresenterMock, scenarioGridModelMock);
        verify(dmoDataManagementStrategy, never()).aggregatorCallback(eq(testToolsPresenterMock), anyInt(), any(SortedMap.class), eq(scenarioGridModelMock), isA(List.class));
        verify(oracleMock, never()).getFieldCompletions(anyString(), any(Callback.class));
    }

    @Test
    public void populateTestToolsWithoutFactTupleWithOnlyDataObjects() {
        dmoDataManagementStrategy.factModelTreeHolder.factModelTuple = null;
        Map<String, List<String>> alreadyAssignedProperties = new HashMap<>();
        doReturn(alreadyAssignedProperties).when(dmoDataManagementStrategy).getPropertiesToHide(scenarioGridModelMock);
        String[] notEmptyFactTypes = getRandomStringArray();
        when(oracleMock.getFactTypes()).thenReturn(notEmptyFactTypes);
        dmoDataManagementStrategy.populateTestTools(testToolsPresenterMock, scenarioGridModelMock);
        verify(dmoDataManagementStrategy, times(1)).aggregatorCallback(eq(testToolsPresenterMock), anyInt(), any(SortedMap.class), eq(scenarioGridModelMock), isA(List.class));
        for (String factType : notEmptyFactTypes) {
            verify(oracleMock, times(1)).getFieldCompletions(eq(factType), any(Callback.class));
        }
    }

    @Test
    public void populateTestToolsWithoutFactTupleWithDataObjectsAndSimpleJavaTypes() {
        dmoDataManagementStrategy.factModelTreeHolder.factModelTuple = null;
        Map<String, List<String>> alreadyAssignedProperties = new HashMap<>();
        doReturn(alreadyAssignedProperties).when(dmoDataManagementStrategy).getPropertiesToHide(scenarioGridModelMock);
        String[] dataObjectTypes = getRandomStringArray();
        String[] simpleJavaTypes = getSimpleTypeArray();
        String[] notEmptyFactTypes = mergeArrays(dataObjectTypes, simpleJavaTypes);
        when(oracleMock.getFactTypes()).thenReturn(notEmptyFactTypes);
        dmoDataManagementStrategy.populateTestTools(testToolsPresenterMock, scenarioGridModelMock);
        verify(dmoDataManagementStrategy, times(1)).aggregatorCallback(eq(testToolsPresenterMock), anyInt(), any(SortedMap.class), eq(scenarioGridModelMock), isA(List.class));
        for (String factType : dataObjectTypes) {
            verify(oracleMock, times(1)).getFieldCompletions(eq(factType), any(Callback.class));
        }
    }

    @Test
    public void populateTestToolsWithoutFactTupleWithoutDataObjectsWithSimpleJavaTypes() {
        dmoDataManagementStrategy.factModelTreeHolder.factModelTuple = null;
        Map<String, List<String>> alreadyAssignedProperties = new HashMap<>();
        doReturn(alreadyAssignedProperties).when(dmoDataManagementStrategy).getPropertiesToHide(scenarioGridModelMock);
        String[] notEmptyFactTypes = getSimpleTypeArray();
        List<String> simpleJavaTypes = Arrays.asList(notEmptyFactTypes);
        when(oracleMock.getFactTypes()).thenReturn(notEmptyFactTypes);
        dmoDataManagementStrategy.populateTestTools(testToolsPresenterMock, scenarioGridModelMock);
        verify(dmoDataManagementStrategy, never()).aggregatorCallback(eq(testToolsPresenterMock), anyInt(), any(SortedMap.class), eq(scenarioGridModelMock), isA(List.class));
        verify(dmoDataManagementStrategy, times(1)).aggregatorCallbackMethod(eq(testToolsPresenterMock), eq(0), any(SortedMap.class), eq(scenarioGridModelMock), eq(null), eq(simpleJavaTypes));
    }

    @Test
    public void manageScenarioSimulationModelContent() {
        dmoDataManagementStrategy.manageScenarioSimulationModelContent(observablePathMock, content);
        assertEquals(dmoDataManagementStrategy.oracle, oracleMock);
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
        dmoDataManagementStrategy.fieldCompletionsCallbackMethod(FACT_NAME, result, aggregatorCallbackMock);
        verify(dmoDataManagementStrategy, times(1)).getFactModelTree(eq(FACT_NAME), eq(result));
        verify(aggregatorCallbackMock, times(1)).callback(isA(FactModelTree.class));
    }

    @Test
    public void getFactModelTree() {
        Map<String, String> simpleProperties = getSimplePropertiesInner();
        final ModelField[] modelFields = getModelFieldsInner(simpleProperties);
        final FactModelTree retrieved = dmoDataManagementStrategy.getFactModelTree(FACT_NAME, modelFields);
        assertNotNull(retrieved);
        assertEquals(FACT_NAME, retrieved.getFactName());
        assertEquals("", retrieved.getFullPackage());
    }

    @Test
    public void getSimpleClassFactModelTree() {
        Class[] expectedClazzes = {String.class, Boolean.class, Integer.class, Double.class, Number.class};
        for (Class expectedClazz : expectedClazzes) {
            final FactModelTree retrieved = dmoDataManagementStrategy.getSimpleClassFactModelTree(
                    expectedClazz.getSimpleName(),
                    expectedClazz.getCanonicalName());
            assertNotNull(retrieved);
            String key = expectedClazz.getSimpleName();
            assertEquals(key, retrieved.getFactName());
            String fullName = expectedClazz.getCanonicalName();
            String packageName = fullName.substring(0, fullName.lastIndexOf("."));
            assertEquals(packageName, retrieved.getFullPackage());
            Map<String, String> simpleProperties = retrieved.getSimpleProperties();
            assertNotNull(simpleProperties);
            assertEquals(1, simpleProperties.size());
            Util.assertTrue(simpleProperties.containsKey(LOWER_CASE_VALUE));
            String simplePropertyValue = simpleProperties.get(LOWER_CASE_VALUE);
            assertNotNull(simplePropertyValue);
            assertEquals(fullName, simplePropertyValue);
        }
    }

    @Test
    public void getInstanceMap() {
        FactModelTree toPopulate = getFactModelTreeInner(randomAlphabetic(3));
        final Map<String, String> simpleProperties = toPopulate.getSimpleProperties();
        final Collection<String> values = simpleProperties.values();
        SortedMap<String, FactModelTree> factTypeFieldsMap = getFactTypeFieldsMapInner(values);
        SortedMap<String, FactModelTree> retrieved = dmoDataManagementStrategy.getInstanceMap(factTypeFieldsMap);
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
        dmoDataManagementStrategy.factModelTreeHolder.factModelTuple = null;
        dmoDataManagementStrategy.aggregatorCallbackMethod(testToolsPresenterMock, factTypeFieldsMap.size() + 1, factTypeFieldsMap, scenarioGridModelMock, resultMock, simpleJavaTypes);
        verify(resultMock, times(1)).getFactName();
        assertTrue(factTypeFieldsMap.containsKey(resultName));
        assertEquals(resultMock, factTypeFieldsMap.get(resultName));
        factTypeFieldsMap.values().forEach(factModelTree -> verify(dmoDataManagementStrategy, times(1)).populateFactModelTree(eq(factModelTree), eq(factTypeFieldsMap)));
        assertNotNull(dmoDataManagementStrategy.factModelTreeHolder.factModelTuple);
        verify(dmoDataManagementStrategy, times(1)).storeData(eq(dmoDataManagementStrategy.factModelTreeHolder.factModelTuple), eq(testToolsPresenterMock), eq(scenarioGridModelMock));
    }

    @Test
    public void aggregatorCallbackMethodWithResultNotAllElements() {
        SortedMap<String, FactModelTree> factTypeFieldsMap = getRandomMap();
        String resultName = "RESULT_NAME";
        FactModelTree resultMock = mock(FactModelTree.class);
        when(resultMock.getFactName()).thenReturn(resultName);
        List<String> simpleJavaTypes = Arrays.asList(getSimpleTypeArray());
        dmoDataManagementStrategy.factModelTreeHolder.factModelTuple = null;
        dmoDataManagementStrategy.aggregatorCallbackMethod(testToolsPresenterMock, factTypeFieldsMap.size() + 10, factTypeFieldsMap, scenarioGridModelMock, resultMock, simpleJavaTypes);
        verify(resultMock, times(1)).getFactName();
        assertTrue(factTypeFieldsMap.containsKey(resultName));
        assertEquals(resultMock, factTypeFieldsMap.get(resultName));
        verify(dmoDataManagementStrategy, never()).populateFactModelTree(isA(FactModelTree.class), isA(SortedMap.class));
        assertNull(dmoDataManagementStrategy.factModelTreeHolder.factModelTuple);
        verify(dmoDataManagementStrategy, never()).storeData(isA(FactModelTuple.class), eq(testToolsPresenterMock), eq(scenarioGridModelMock));
    }

    @Test
    public void aggregatorCallbackMethodWithoutResultAllElements() {
        SortedMap<String, FactModelTree> factTypeFieldsMap = getRandomMap();
        List<String> simpleJavaTypes = Arrays.asList(getSimpleTypeArray());
        dmoDataManagementStrategy.factModelTreeHolder.factModelTuple = null;
        int previousSizeMap = factTypeFieldsMap.size();
        dmoDataManagementStrategy.aggregatorCallbackMethod(testToolsPresenterMock, factTypeFieldsMap.size(), factTypeFieldsMap, scenarioGridModelMock, null, simpleJavaTypes);
        assertEquals(previousSizeMap, factTypeFieldsMap.size());
        factTypeFieldsMap.values().forEach(factModelTree -> verify(dmoDataManagementStrategy, times(1)).populateFactModelTree(eq(factModelTree), eq(factTypeFieldsMap)));
        assertNotNull(dmoDataManagementStrategy.factModelTreeHolder.factModelTuple);
        verify(dmoDataManagementStrategy, times(1)).storeData(eq(dmoDataManagementStrategy.factModelTreeHolder.factModelTuple), eq(testToolsPresenterMock), eq(scenarioGridModelMock));
    }

    @Test
    public void aggregatorCallbackMethodWithOutResultNotAllElements() {
        SortedMap<String, FactModelTree> factTypeFieldsMap = getRandomMap();
        List<String> simpleJavaTypes = Arrays.asList(getSimpleTypeArray());
        int previousSizeMap = factTypeFieldsMap.size();
        dmoDataManagementStrategy.factModelTreeHolder.factModelTuple = null;
        dmoDataManagementStrategy.aggregatorCallbackMethod(testToolsPresenterMock, factTypeFieldsMap.size() + 10, factTypeFieldsMap, scenarioGridModelMock, null, simpleJavaTypes);
        assertEquals(previousSizeMap, factTypeFieldsMap.size());
        verify(dmoDataManagementStrategy, never()).populateFactModelTree(isA(FactModelTree.class), isA(SortedMap.class));
        assertNull(dmoDataManagementStrategy.factModelTreeHolder.factModelTuple);
        verify(dmoDataManagementStrategy, never()).storeData(isA(FactModelTuple.class), eq(testToolsPresenterMock), eq(scenarioGridModelMock));
    }

    private void commonIsADataType(String value, boolean expected) {
        boolean retrieved = dmoDataManagementStrategy.isADataType(value);
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
        dmoDataManagementStrategy.populateGenericTypeMap(toPopulate, factName, propertyName, isList);
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
        return new FactModelTree(factName, SCENARIO_PACKAGE, getSimplePropertiesInner(), new HashMap<>());
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

