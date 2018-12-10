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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.editor.AbstractScenarioSimulationEditorTest;
import org.drools.workbench.screens.scenariosimulation.client.models.FactModelTree;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.soup.project.datamodel.oracle.ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMODataManagementStrategyTest extends AbstractScenarioSimulationEditorTest {

    private DMODataManagementStrategy dmoDataManagementStrategy;

    @Mock
    private AsyncPackageDataModelOracle oracleMock;

    private final String FACT_NAME = "FACT_NAME";

    private final String FULL_FACT_CLASSNAME = "FULL_FACT_CLASSNAME";

    @Before
    public void setup() {
        super.setup();
        when(oracleMock.getFQCNByFactName(FACT_NAME)).thenReturn(FULL_FACT_CLASSNAME);
        when(oracleFactoryMock.makeAsyncPackageDataModelOracle(observablePathMock, model, content.getDataModel())).thenReturn(oracleMock);
        this.dmoDataManagementStrategy = spy(new DMODataManagementStrategy(oracleFactoryMock) {
            {
                this.oracle = oracleMock;
            }
        });
    }

    @Test
    public void populateRightPanel() {
        String[] emptyFactTypes = {};
        when(oracleMock.getFactTypes()).thenReturn(emptyFactTypes);
        dmoDataManagementStrategy.populateRightPanel(rightPanelPresenterMock, scenarioGridModelMock);
        verify(dmoDataManagementStrategy, never()).aggregatorCallback(eq(rightPanelPresenterMock), anyInt(), any(SortedMap.class), eq(scenarioGridModelMock));
        verify(oracleMock, never()).getFieldCompletions(anyString(), any(Callback.class));
        //
        String[] notEmptyFactTypes = getRandomStringArray();
        when(oracleMock.getFactTypes()).thenReturn(notEmptyFactTypes);
        dmoDataManagementStrategy.populateRightPanel(rightPanelPresenterMock, scenarioGridModelMock);
        verify(dmoDataManagementStrategy, times(1)).aggregatorCallback(eq(rightPanelPresenterMock), anyInt(), any(SortedMap.class), eq(scenarioGridModelMock));
        for (String factType : notEmptyFactTypes) {
            verify(oracleMock, times(1)).getFieldCompletions(eq(factType), any(Callback.class));
        }
    }

    @Test
    public void manageScenarioSimulationModelContent() {
        dmoDataManagementStrategy.manageScenarioSimulationModelContent(observablePathMock, content);
        assertEquals(dmoDataManagementStrategy.oracle, oracleMock);
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
    public void populateFactModelTree() {
        FactModelTree toPopulate = getFactModelTreeInner(randomAlphabetic(3));
        final FactModelTree spied = spy(toPopulate);
        final Map<String, String> simpleProperties = toPopulate.getSimpleProperties();
        final Set<String> keys = simpleProperties.keySet();
        final Collection<String> values = simpleProperties.values();
        SortedMap<String, FactModelTree> factTypeFieldsMap = getFactTypeFieldsMapInner(values);
        dmoDataManagementStrategy.populateFactModelTree(toPopulate, factTypeFieldsMap);
        keys.forEach(key -> {
            final String value = simpleProperties.get(key);
            final String factName = factTypeFieldsMap.get(value).getFactName();
            verify(spied, times(1)).addExpandableProperty(eq(key), eq(factName));
        });
        assertTrue(toPopulate.getSimpleProperties().isEmpty());
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
        return new FactModelTree(factName, SCENARIO_PACKAGE, getSimplePropertiesInner());
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
}