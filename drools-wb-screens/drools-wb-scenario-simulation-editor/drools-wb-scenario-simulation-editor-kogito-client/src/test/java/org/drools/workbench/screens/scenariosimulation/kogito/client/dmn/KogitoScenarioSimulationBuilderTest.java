/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.kogito.client.dmn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.FactMappingValueType;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class KogitoScenarioSimulationBuilderTest {

    public KogitoScenarioSimulationBuilder kogitoScenarioSimulationBuilderSpy;

    @Before
    public void setup() {
        kogitoScenarioSimulationBuilderSpy = spy(new KogitoScenarioSimulationBuilder());
    }

    @Test
    public void createRULESimulation() {
        Simulation simulation = kogitoScenarioSimulationBuilderSpy.createRULESimulation();
        assertNotNull(simulation);
        assertEquals(1,simulation.getScenarioWithIndex().size());
        ScesimModelDescriptor modelDescriptor = simulation.getScesimModelDescriptor();
        assertNotNull(modelDescriptor);
        assertEquals(4, modelDescriptor.getFactMappings().size());
        //First column
        assertEquals(70, modelDescriptor.getFactMappingByIndex(0).getColumnWidth(), 0);
        assertEquals(FactIdentifier.INDEX.getName(), modelDescriptor.getFactMappingByIndex(0).getFactAlias());
        assertEquals(FactIdentifier.INDEX, modelDescriptor.getFactMappingByIndex(0).getFactIdentifier());
        assertEquals(ExpressionIdentifier.INDEX, modelDescriptor.getFactMappingByIndex(0).getExpressionIdentifier());
        //Second column
        assertEquals(300, modelDescriptor.getFactMappingByIndex(1).getColumnWidth(), 0);
        assertEquals(FactIdentifier.DESCRIPTION.getName(), modelDescriptor.getFactMappingByIndex(1).getFactAlias());
        assertEquals(FactIdentifier.DESCRIPTION, modelDescriptor.getFactMappingByIndex(1).getFactIdentifier());
        assertEquals(ExpressionIdentifier.DESCRIPTION, modelDescriptor.getFactMappingByIndex(1).getExpressionIdentifier());
        assertEquals(FactMappingValueType.NOT_EXPRESSION, modelDescriptor.getFactMappingByIndex(1).getFactMappingValueType());
        //Third column
        assertEquals(114, modelDescriptor.getFactMappingByIndex(2).getColumnWidth(), 0);
        assertEquals("INSTANCE 1", modelDescriptor.getFactMappingByIndex(2).getFactAlias());
        assertEquals(FactIdentifier.EMPTY, modelDescriptor.getFactMappingByIndex(2).getFactIdentifier());
        assertEquals(ExpressionIdentifier.create("1|1", FactMappingType.GIVEN), modelDescriptor.getFactMappingByIndex(2).getExpressionIdentifier());
        assertEquals(FactMappingValueType.NOT_EXPRESSION, modelDescriptor.getFactMappingByIndex(2).getFactMappingValueType());
        //Fourth column
        assertEquals(114, modelDescriptor.getFactMappingByIndex(3).getColumnWidth(), 0);
        assertEquals("INSTANCE 2", modelDescriptor.getFactMappingByIndex(3).getFactAlias());
        assertEquals(FactIdentifier.EMPTY, modelDescriptor.getFactMappingByIndex(3).getFactIdentifier());
        assertEquals(ExpressionIdentifier.create("1|2", FactMappingType.EXPECT), modelDescriptor.getFactMappingByIndex(3).getExpressionIdentifier());
        assertEquals(FactMappingValueType.NOT_EXPRESSION, modelDescriptor.getFactMappingByIndex(3).getFactMappingValueType());
    }

    @Test
    public void createDMNSimulationEmptyFactModuleTuple() {
        FactModelTuple factMappingTuple = new FactModelTuple(Collections.emptySortedMap(), Collections.emptySortedMap());
        Simulation simulation = kogitoScenarioSimulationBuilderSpy.createDMNSimulation(factMappingTuple);
        assertNotNull(simulation);
        assertEquals(1,simulation.getScenarioWithIndex().size());
        ScesimModelDescriptor modelDescriptor = simulation.getScesimModelDescriptor();
        assertNotNull(modelDescriptor);
        assertEquals(4, modelDescriptor.getFactMappings().size());
        //First column
        assertEquals(70, modelDescriptor.getFactMappingByIndex(0).getColumnWidth(), 0);
        assertEquals(FactIdentifier.INDEX.getName(), modelDescriptor.getFactMappingByIndex(0).getFactAlias());
        assertEquals(FactIdentifier.INDEX, modelDescriptor.getFactMappingByIndex(0).getFactIdentifier());
        assertEquals(ExpressionIdentifier.INDEX, modelDescriptor.getFactMappingByIndex(0).getExpressionIdentifier());
        assertEquals(FactMappingValueType.NOT_EXPRESSION, modelDescriptor.getFactMappingByIndex(0).getFactMappingValueType());
        //Second column
        assertEquals(300, modelDescriptor.getFactMappingByIndex(1).getColumnWidth(), 0);
        assertEquals(FactIdentifier.DESCRIPTION.getName(), modelDescriptor.getFactMappingByIndex(1).getFactAlias());
        assertEquals(FactIdentifier.DESCRIPTION, modelDescriptor.getFactMappingByIndex(1).getFactIdentifier());
        assertEquals(ExpressionIdentifier.DESCRIPTION, modelDescriptor.getFactMappingByIndex(1).getExpressionIdentifier());
        assertEquals(FactMappingValueType.NOT_EXPRESSION, modelDescriptor.getFactMappingByIndex(1).getFactMappingValueType());
        //Third column
        assertEquals(114, modelDescriptor.getFactMappingByIndex(2).getColumnWidth(), 0);
        assertEquals("INSTANCE 1", modelDescriptor.getFactMappingByIndex(2).getFactAlias());
        assertEquals(FactIdentifier.EMPTY, modelDescriptor.getFactMappingByIndex(2).getFactIdentifier());
        assertEquals(ExpressionIdentifier.create("1|1", FactMappingType.GIVEN), modelDescriptor.getFactMappingByIndex(2).getExpressionIdentifier());
        assertEquals(FactMappingValueType.NOT_EXPRESSION, modelDescriptor.getFactMappingByIndex(2).getFactMappingValueType());
        //Fourth column
        assertEquals(114, modelDescriptor.getFactMappingByIndex(3).getColumnWidth(), 0);
        assertEquals("INSTANCE 2", modelDescriptor.getFactMappingByIndex(3).getFactAlias());
        assertEquals(FactIdentifier.EMPTY, modelDescriptor.getFactMappingByIndex(3).getFactIdentifier());
        assertEquals(ExpressionIdentifier.create("1|2", FactMappingType.EXPECT), modelDescriptor.getFactMappingByIndex(3).getExpressionIdentifier());
        assertEquals(FactMappingValueType.NOT_EXPRESSION, modelDescriptor.getFactMappingByIndex(3).getFactMappingValueType());
    }


    @Test
    public void getColumn() {
        assertEquals(70, KogitoScenarioSimulationBuilder.getColumnWidth(ExpressionIdentifier.NAME.Index.toString()), 0);
        assertEquals(300, KogitoScenarioSimulationBuilder.getColumnWidth(ExpressionIdentifier.NAME.Description.toString()), 0);
        assertEquals(114, KogitoScenarioSimulationBuilder.getColumnWidth(ExpressionIdentifier.NAME.Other.toString()), 0);
        assertEquals(114, KogitoScenarioSimulationBuilder.getColumnWidth(ExpressionIdentifier.NAME.Expected.toString()), 0);
        assertEquals(114, KogitoScenarioSimulationBuilder.getColumnWidth(ExpressionIdentifier.NAME.Given.toString()), 0);
        assertEquals(114, KogitoScenarioSimulationBuilder.getColumnWidth("test"), 0);
    }

    @Test
    public void addToScenarioRecursive() {
        FactMapping factMappingMock = mock(FactMapping.class);
        KogitoScenarioSimulationBuilder.FactMappingExtractor factMappingExtractorMock = mock(KogitoScenarioSimulationBuilder.FactMappingExtractor.class);
        when(factMappingExtractorMock.getFactMapping(any(), anyString(), any(), anyString())).thenReturn(factMappingMock);

        Map<String, FactModelTree> hiddenFacts = new HashMap<>();

        FactModelTree factModelTree = new FactModelTree("myFact", "", new HashMap<>(), Collections.emptyMap());
        factModelTree.addExpandableProperty("recursiveProperty", "recursive");
        String propertyType = String.class.getCanonicalName();
        String propertyName = "simpleProperty";
        factModelTree.addSimpleProperty(propertyName, propertyType);

        hiddenFacts.put("recursive", factModelTree);

        kogitoScenarioSimulationBuilderSpy.addFactMapping(factMappingExtractorMock,
                                                          factModelTree,
                                                          new ArrayList<>(),
                                                          hiddenFacts);

        verify(factMappingExtractorMock, times(1))
                .getFactMapping(
                        eq(factModelTree),
                        eq(propertyName),
                        eq(Arrays.asList("myFact", "recursiveProperty")),
                        eq(propertyType));

        verify(factMappingExtractorMock, times(2))
                .getFactMapping(
                        any(),
                        any(),
                        any(),
                        any());
    }

    @Test
    public void addToScenarioMultipleNested() {
        FactMapping factMappingMock = mock(FactMapping.class);
        KogitoScenarioSimulationBuilder.FactMappingExtractor factMappingExtractorMock = mock(KogitoScenarioSimulationBuilder.FactMappingExtractor.class);
        when(factMappingExtractorMock.getFactMapping(any(), anyString(), any(), anyString())).thenReturn(factMappingMock);

        Map<String, FactModelTree> hiddenFacts = new HashMap<>();

        FactModelTree factModelTree = new FactModelTree("myFact", "", new HashMap<>(), Collections.emptyMap());
        factModelTree.addExpandableProperty("nestedProperty", "tNested");
        factModelTree.addExpandableProperty("nestedProperty2", "tNested2");

        FactModelTree nested1 = new FactModelTree("tNested1", "", new HashMap<>(), Collections.emptyMap());
        FactModelTree nested2 = new FactModelTree("tNested2", "", new HashMap<>(), Collections.emptyMap());
        String propertyType = String.class.getCanonicalName();
        String propertyName = "stingProperty";
        nested1.addSimpleProperty(propertyName, propertyType);
        String propertyType2 = Boolean.class.getCanonicalName();
        String propertyName2 = "booleanProperty";
        nested2.addSimpleProperty(propertyName2, propertyType2);

        hiddenFacts.put("tNested", nested1);
        hiddenFacts.put("tNested2", nested2);

        kogitoScenarioSimulationBuilderSpy.addFactMapping(factMappingExtractorMock,
                                                          factModelTree,
                                                          new ArrayList<>(),
                                                          hiddenFacts);

        verify(factMappingExtractorMock, times(1))
                .getFactMapping(
                        eq(nested1),
                        eq(propertyName),
                        eq(Arrays.asList("myFact", "nestedProperty")),
                        eq(propertyType));
        verify(factMappingExtractorMock, times(1))
                .getFactMapping(
                        eq(nested2),
                        eq(propertyName2),
                        eq(Arrays.asList("myFact", "nestedProperty2")),
                        eq(propertyType2));

        verify(factMappingExtractorMock, times(2))
                .getFactMapping(
                        any(),
                        any(),
                        any(),
                        any());
    }

}
