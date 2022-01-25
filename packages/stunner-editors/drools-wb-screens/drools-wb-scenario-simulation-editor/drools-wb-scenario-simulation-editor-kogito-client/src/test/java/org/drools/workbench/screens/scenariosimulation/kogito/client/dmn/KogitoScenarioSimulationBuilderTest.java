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
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.model.KogitoDMNModel;
import org.drools.workbench.screens.scenariosimulation.kogito.client.services.ScenarioSimulationKogitoDMNMarshallerService;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class KogitoScenarioSimulationBuilderTest {

    @InjectMocks @Spy
    private KogitoScenarioSimulationBuilder kogitoScenarioSimulationBuilderSpy;
    @Mock
    private Callback<ScenarioSimulationModel> callbackMock;
    @Mock
    private ErrorCallback<Object> errorCallbackMock;
    @Mock
    private FactModelTuple factModelTupleMock;
    @Mock
    private JSITDefinitions jsitDefinitionsMock;
    @Mock
    private ScenarioSimulationKogitoDMNDataManager kogitoDMNDataManagerMock;
    @Mock
    private ScenarioSimulationKogitoDMNMarshallerService dmnMarshallerServiceMock;
    @Captor
    private ArgumentCaptor<Callback<KogitoDMNModel>> callbackArgumentCaptor;
    @Captor
    private ArgumentCaptor<Path> pathArgumentCaptor;
    @Captor
    private ArgumentCaptor<ScenarioSimulationModel> scenarioSimulationModelArgumentCaptor;

    private KogitoDMNModel kogitoDMNModel;

    @Before
    public void setup() {
        kogitoDMNModel = new KogitoDMNModel(jsitDefinitionsMock, Collections.emptyMap());
        when(kogitoDMNDataManagerMock.getFactModelTuple(kogitoDMNModel)).thenReturn(factModelTupleMock);
        when(jsitDefinitionsMock.getNamespace()).thenReturn("namespace");
        when(jsitDefinitionsMock.getName()).thenReturn("name");
    }

    @Test
    public void populateRule() {
        kogitoScenarioSimulationBuilderSpy.populateScenarioSimulationModelRULE("session", callbackMock);
        verify(kogitoScenarioSimulationBuilderSpy, times(1)).createRULESimulation();
        verify(kogitoScenarioSimulationBuilderSpy, times(1)).createBackground();
        verify(kogitoScenarioSimulationBuilderSpy, times(1)).createRULESettings("session");
        verify(callbackMock, times(1)).callback(scenarioSimulationModelArgumentCaptor.capture());
        assertNotNull(scenarioSimulationModelArgumentCaptor.getValue());
        assertNotNull(scenarioSimulationModelArgumentCaptor.getValue().getBackground());
        assertNotNull(scenarioSimulationModelArgumentCaptor.getValue().getSimulation());
        assertNotNull(scenarioSimulationModelArgumentCaptor.getValue().getSettings());
    }

    @Test
    public void populateDMN() {
        kogitoScenarioSimulationBuilderSpy.populateScenarioSimulationModelDMN("src/file.dmn", callbackMock, errorCallbackMock);
        verify(kogitoScenarioSimulationBuilderSpy, times(1)).createBackground();
        verify(dmnMarshallerServiceMock, times(1)).getDMNContent(pathArgumentCaptor.capture(), callbackArgumentCaptor.capture(), eq(errorCallbackMock));
        assertEquals("file.dmn", pathArgumentCaptor.getValue().getFileName());
        assertEquals("src/file.dmn", pathArgumentCaptor.getValue().toURI());
        callbackArgumentCaptor.getValue().callback(kogitoDMNModel);
        verify(kogitoDMNDataManagerMock, times(1)).getFactModelTuple(kogitoDMNModel);
        verify(kogitoScenarioSimulationBuilderSpy, times(1)).createDMNSimulation(factModelTupleMock);
        verify(kogitoScenarioSimulationBuilderSpy, times(1)).createDMNSettings("name", "namespace", "src/file.dmn");
        verify(callbackMock, times(1)).callback(scenarioSimulationModelArgumentCaptor.capture());
        assertNotNull(scenarioSimulationModelArgumentCaptor.getValue());
        assertNotNull(scenarioSimulationModelArgumentCaptor.getValue().getBackground());
        assertNotNull(scenarioSimulationModelArgumentCaptor.getValue().getSimulation());
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
        factModelTree.addSimpleProperty(propertyName, new FactModelTree.PropertyTypeName(propertyType));

        hiddenFacts.put("recursive", factModelTree);

        kogitoScenarioSimulationBuilderSpy.addFactMapping(factMappingExtractorMock,
                                                          factModelTree,
                                                          new ArrayList<>(),
                                                          hiddenFacts);

        verify(factMappingExtractorMock, times(1))
                .getFactMapping(
                        factModelTree,
                        propertyName,
                        Arrays.asList("myFact", "recursiveProperty"),
                        propertyType);

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
        nested1.addSimpleProperty(propertyName, new FactModelTree.PropertyTypeName(propertyType));
        String propertyType2 = Boolean.class.getCanonicalName();
        String propertyName2 = "booleanProperty";
        nested2.addSimpleProperty(propertyName2, new FactModelTree.PropertyTypeName(propertyType2));

        hiddenFacts.put("tNested", nested1);
        hiddenFacts.put("tNested2", nested2);

        kogitoScenarioSimulationBuilderSpy.addFactMapping(factMappingExtractorMock,
                                                          factModelTree,
                                                          new ArrayList<>(),
                                                          hiddenFacts);

        verify(factMappingExtractorMock, times(1))
                .getFactMapping(
                        nested1,
                        propertyName,
                        Arrays.asList("myFact", "nestedProperty"),
                        propertyType);
        verify(factMappingExtractorMock, times(1))
                .getFactMapping(
                        nested2,
                        propertyName2,
                        Arrays.asList("myFact", "nestedProperty2"),
                        propertyType2);

        verify(factMappingExtractorMock, times(2))
                .getFactMapping(
                        any(),
                        any(),
                        any(),
                        any());
    }

}
