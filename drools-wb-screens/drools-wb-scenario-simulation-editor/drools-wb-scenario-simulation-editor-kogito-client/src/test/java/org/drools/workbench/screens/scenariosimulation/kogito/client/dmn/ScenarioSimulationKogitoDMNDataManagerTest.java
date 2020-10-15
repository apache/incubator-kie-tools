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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.namespace.QName;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.feel.BuiltInType;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecision;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationItem;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInputData;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITItemDefinition;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITUnaryTests;
import org.mockito.Mock;
import org.mockito.Spy;

import static junit.framework.TestCase.assertNotNull;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;
import static org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.ScenarioSimulationKogitoDMNDataManager.TYPEREF_QNAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioSimulationKogitoDMNDataManagerTest {

    public static final String NAMESPACE = "namespace";
    public static final String TYPE_NAME = "name";
    public static final String ID = "id";

    @Spy
    private ScenarioSimulationKogitoDMNDataManager scenarioSimulationKogitoDMNDataManagerSpy;
    @Mock
    private JSITItemDefinition jsitItemDefinitionMock;
    @Mock
    private JSITItemDefinition jsitItemDefinitionNestedMock;
    @Mock
    private JSITDefinitions jsiITDefinitionsMock;
    @Mock
    private JSITDecision jsiITDecisionMock;
    @Mock
    private JSITInputData jsiITInputDataMock;
    @Mock
    private JSITInformationItem jsiITInformationItemInputMock;
    @Mock
    private JSITInformationItem jsiITInformationItemDecisionMock;

    private List<JSITItemDefinition> jstiItemDefinitions;
    private List<JSITDRGElement> jsitdrgElements;
    private List<JSITDRGElement> drgElements;
    private Map<QName, String> attributesMapInput;
    private Map<QName, String> attributesMapDecision;
    private Map<String, JSITItemDefinition> allDefinitions;

    @Before
    public void setup() {
        jstiItemDefinitions = new ArrayList<>();
        jsitdrgElements = new ArrayList<>();
        drgElements = new ArrayList<>();
        attributesMapInput = new HashMap<>();
        attributesMapDecision = new HashMap<>();
        allDefinitions = new HashMap<>();

        doReturn(true).when(scenarioSimulationKogitoDMNDataManagerSpy).isJSITInputData(eq(jsiITInputDataMock));
        doReturn(true).when(scenarioSimulationKogitoDMNDataManagerSpy).isJSITDecision(eq(jsiITDecisionMock));
        doReturn(attributesMapInput).when(scenarioSimulationKogitoDMNDataManagerSpy).getOtherAttributesMap(eq(jsiITInformationItemInputMock));
        doReturn(attributesMapDecision).when(scenarioSimulationKogitoDMNDataManagerSpy).getOtherAttributesMap(eq(jsiITInformationItemDecisionMock));
        when(jsiITDefinitionsMock.getNamespace()).thenReturn(NAMESPACE);
        when(jsiITDefinitionsMock.getItemDefinition()).thenReturn(jstiItemDefinitions);
        when(jsiITDefinitionsMock.getDrgElement()).thenReturn(jsitdrgElements);
        when(jsiITInputDataMock.getVariable()).thenReturn(jsiITInformationItemInputMock);
        when(jsiITDecisionMock.getVariable()).thenReturn(jsiITInformationItemDecisionMock);
        when(jsitItemDefinitionMock.getName()).thenReturn(TYPE_NAME);
        when(jsitItemDefinitionMock.getId()).thenReturn(ID);
        when(jsitItemDefinitionMock.getIsCollection()).thenReturn(false);
    }

    @Test
    public void getDMNTypeFromMaps() {
        Map<String, ClientDMNType> dmnTypesMap = scenarioSimulationKogitoDMNDataManagerSpy.getDMNDataTypesMap(jstiItemDefinitions, NAMESPACE);
        attributesMapInput.put(TYPEREF_QNAME, "number");
        ClientDMNType clientDMNType = scenarioSimulationKogitoDMNDataManagerSpy.getDMNTypeFromMaps(dmnTypesMap, attributesMapInput);
        assertNotNull(clientDMNType);
        assertTrue(BuiltInType.NUMBER.equals(clientDMNType.getFeelType()));
    }

    @Test
    public void getDMNTypeFromMaps_NullTypeRef() {
        Map<String, ClientDMNType> dmnTypesMap = scenarioSimulationKogitoDMNDataManagerSpy.getDMNDataTypesMap(jstiItemDefinitions, NAMESPACE);
        attributesMapInput.put(TYPEREF_QNAME, null);
        ClientDMNType clientDMNType = scenarioSimulationKogitoDMNDataManagerSpy.getDMNTypeFromMaps(dmnTypesMap, attributesMapInput);
        assertNotNull(clientDMNType);
        assertTrue(BuiltInType.ANY.equals(clientDMNType.getFeelType()));
    }

    @Test
    public void testIndexDefinitionsByName() {
        final JSITItemDefinition definition1 = mock(JSITItemDefinition.class);
        final JSITItemDefinition definition2 = mock(JSITItemDefinition.class);
        final JSITItemDefinition definition3 = mock(JSITItemDefinition.class);
        final String name1 = "name1";
        final String name2 = "name2";
        final String name3 = "name3";
        final List<JSITItemDefinition> list = Arrays.asList(definition1, definition2, definition3);

        when(definition1.getName()).thenReturn(name1);
        when(definition2.getName()).thenReturn(name2);
        when(definition3.getName()).thenReturn(name3);

        final Map<String, JSITItemDefinition> index = scenarioSimulationKogitoDMNDataManagerSpy.indexDefinitionsByName(list);

        assertEquals(3, index.size());
        assertTrue(index.containsKey(name1));
        assertTrue(index.containsKey(name2));
        assertTrue(index.containsKey(name3));
        assertEquals(definition1, index.get(name1));
        assertEquals(definition2, index.get(name2));
        assertEquals(definition3, index.get(name3));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIndexDefinitionsUnmodifiable() {
        final JSITItemDefinition definition1 = mock(JSITItemDefinition.class);
        final String name1 = "name1";
        final List<JSITItemDefinition> list = Arrays.asList(definition1);

        when(definition1.getName()).thenReturn(name1);

        final Map<String, JSITItemDefinition> index = scenarioSimulationKogitoDMNDataManagerSpy.indexDefinitionsByName(list);

        index.put("name2", mock(JSITItemDefinition.class));
    }

    @Test
    public void testGetOrCreateDMNType() {
        final String namespace = "namespace";
        final String typeOneName = "tTypeOne";
        final String typeTwoName = "tTypeTwo";
        final String typeThreeName = "tTypeThree";
        final JSITItemDefinition definition1 = createJSITItemDefinitionMock(typeOneName);
        final JSITItemDefinition definition2 = createJSITItemDefinitionMock(typeTwoName);
        final JSITItemDefinition definition3 = createJSITItemDefinitionMock(typeThreeName);

        final Map<String, JSITItemDefinition> indexed = scenarioSimulationKogitoDMNDataManagerSpy.indexDefinitionsByName(Arrays.asList(definition1, definition2, definition3));
        final Map<String, ClientDMNType> createdTypes = new HashMap<>();
        final ClientDMNType dmnTypeOne = mock(ClientDMNType.class);
        final ClientDMNType dmnTypeTwo = mock(ClientDMNType.class);
        final ClientDMNType dmnTypeThree = mock(ClientDMNType.class);

        doReturn(dmnTypeOne).when(scenarioSimulationKogitoDMNDataManagerSpy).createDMNType(indexed,
                                                                                           definition1,
                                                                                           namespace,
                                                                                           createdTypes);

        doReturn(dmnTypeTwo).when(scenarioSimulationKogitoDMNDataManagerSpy).createDMNType(indexed,
                                                                                           definition2,
                                                                                           namespace,
                                                                                           createdTypes);

        doReturn(dmnTypeThree).when(scenarioSimulationKogitoDMNDataManagerSpy).createDMNType(indexed,
                                                                                             definition3,
                                                                                             namespace,
                                                                                             createdTypes);

        final ClientDMNType actualDmnTypeOne = scenarioSimulationKogitoDMNDataManagerSpy.getOrCreateDMNType(indexed,
                                                                                                            typeOneName,
                                                                                                            namespace,
                                                                                                            createdTypes);

        final ClientDMNType actualDmnTypeTwo = scenarioSimulationKogitoDMNDataManagerSpy.getOrCreateDMNType(indexed,
                                                                                                            typeTwoName,
                                                                                                            namespace,
                                                                                                            createdTypes);

        final ClientDMNType actualDmnTypeThree = scenarioSimulationKogitoDMNDataManagerSpy.getOrCreateDMNType(indexed,
                                                                                                              typeThreeName,
                                                                                                              namespace,
                                                                                                              createdTypes);

        assertEquals(dmnTypeOne, actualDmnTypeOne);
        assertEquals(dmnTypeTwo, actualDmnTypeTwo);
        assertEquals(dmnTypeThree, actualDmnTypeThree);
    }

    @Test
    public void testGetOrCreateDMNTypeWhenTypeIsAlreadyCreated() {
        final String namespace = "namespace";
        final String typeOneName = "tTypeOne";
        final JSITItemDefinition definition1 = createJSITItemDefinitionMock(typeOneName);
        final Map<String, JSITItemDefinition> indexed = scenarioSimulationKogitoDMNDataManagerSpy.indexDefinitionsByName(Arrays.asList(definition1));
        final ClientDMNType dmnTypeOne = mock(ClientDMNType.class);
        final Map<String, ClientDMNType> createdTypes = new HashMap<>();
        createdTypes.put(typeOneName, dmnTypeOne);

        final ClientDMNType actualDmnTypeOne = scenarioSimulationKogitoDMNDataManagerSpy.getOrCreateDMNType(indexed,
                                                                                                            typeOneName,
                                                                                                            namespace,
                                                                                                            createdTypes);

        verify(scenarioSimulationKogitoDMNDataManagerSpy, never()).createDMNType(indexed,
                                                                                 definition1,
                                                                                 namespace,
                                                                                 createdTypes);
        assertEquals(dmnTypeOne, actualDmnTypeOne);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetOrCreateDMNTypeWhenTypeIsNotFound() {
        final String namespace = "namespace";
        final Map<String, JSITItemDefinition> indexed = new HashMap<>();
        final Map<String, ClientDMNType> createdTypes = new HashMap<>();

        scenarioSimulationKogitoDMNDataManagerSpy.getOrCreateDMNType(indexed,
                                                                     "unknownType",
                                                                     namespace,
                                                                     createdTypes);
    }

    private JSITItemDefinition createJSITItemDefinitionMock(final String name) {
        final JSITItemDefinition mock = mock(JSITItemDefinition.class);
        when(mock.getName()).thenReturn(name);
        return mock;
    }

    @Test
    public void getDMNTypesMapEmptyItemDefinitions() {
        Map<String, ClientDMNType> dmnTypesMap = scenarioSimulationKogitoDMNDataManagerSpy.getDMNDataTypesMap(jstiItemDefinitions, NAMESPACE);
        // It must contains all elements defined in BuiltInType ENUM
        assertTrue(dmnTypesMap.size() == 14);
        for (Map.Entry<String, ClientDMNType> entry : dmnTypesMap.entrySet()) {
            assertNotNull(entry.getValue().getFeelType());
            assertTrue(Arrays.stream(entry.getValue().getFeelType().getNames()).anyMatch(entry.getKey()::equals));
        }
        for (BuiltInType builtInType : BuiltInType.values()) {
            Arrays.stream(builtInType.getNames()).forEach(name -> assertNotNull(dmnTypesMap.get(name)));
        }
    }

    @Test
    public void getDMNTypesMapWithItemDefinitions() {
        jstiItemDefinitions.add(jsitItemDefinitionMock);
        Map<String, ClientDMNType> dmnTypesMap = scenarioSimulationKogitoDMNDataManagerSpy.getDMNDataTypesMap(jstiItemDefinitions, NAMESPACE);
        // It must contains all elements defined in BuiltInType ENUM + one defined jstiItemDefinitionMock item
        assertTrue(dmnTypesMap.size() == 15);
        for (BuiltInType builtInType : BuiltInType.values()) {
            Arrays.stream(builtInType.getNames()).forEach(name -> assertNotNull(dmnTypesMap.get(name)));
        }
        assertNotNull(dmnTypesMap.get(TYPE_NAME));
        assertNull(dmnTypesMap.get(TYPE_NAME).getFeelType());
        assertEquals(TYPE_NAME, dmnTypesMap.get(TYPE_NAME).getName());
        assertEquals(NAMESPACE, dmnTypesMap.get(TYPE_NAME).getNamespace());
        assertFalse(dmnTypesMap.get(TYPE_NAME).isCollection());
    }

    @Test
    public void testGetCustomWithItemComponentDMNTypes() {
        // tPeople[structure]
        //    address[structure]
        //       country[string]

        final JSITItemDefinition tPeople = mock(JSITItemDefinition.class);
        final String tPeopleType = "tPeopleType";
        final String addressFieldName = "address";
        final String countryFieldName = "country";

        when(tPeople.getName()).thenReturn(tPeopleType);

        final JSITItemDefinition address = mock(JSITItemDefinition.class);
        when(address.getName()).thenReturn(addressFieldName);
        final List<JSITItemDefinition> tPeopleFields = Arrays.asList(address);

        final JSITItemDefinition country = mock(JSITItemDefinition.class);
        when(country.getName()).thenReturn(countryFieldName);
        when(country.getTypeRef()).thenReturn(BuiltInType.STRING.getName());
        final List<JSITItemDefinition> tAddressFields = Arrays.asList(country);

        when(tPeople.getItemComponent()).thenReturn(tPeopleFields);
        when(address.getItemComponent()).thenReturn(tAddressFields);

        final List<JSITItemDefinition> definitions = Arrays.asList(tPeople);

        final Map<String, ClientDMNType> dmnTypesMap = scenarioSimulationKogitoDMNDataManagerSpy.getDMNDataTypesMap(definitions, "namespace");

        assertTrue(dmnTypesMap.containsKey(tPeopleType));

        final ClientDMNType dmnPeopleType = dmnTypesMap.get(tPeopleType);

        assertEquals(1, dmnPeopleType.getFields().size());
        assertTrue(dmnPeopleType.getFields().containsKey(addressFieldName));
        assertNull(dmnPeopleType.getFeelType());
        assertFalse(dmnPeopleType.isCollection());
        assertTrue(dmnPeopleType.isComposite());

        ClientDMNType addressType = dmnPeopleType.getFields().get(addressFieldName);
        assertNotNull(addressType);
        assertEquals(1, addressType.getFields().size());
        assertTrue(addressType.getFields().containsKey(countryFieldName));
        assertNull(addressType.getFeelType());
        assertFalse(addressType.isCollection());
        assertTrue(addressType.isComposite());

        ClientDMNType countryType = addressType.getFields().get(countryFieldName);
        assertEquals(0, countryType.getFields().size());
        assertEquals(BuiltInType.STRING, countryType.getFeelType());
        assertFalse(countryType.isCollection());
        assertFalse(countryType.isComposite());

        // It must contains all elements defined in BuiltInType ENUM
        assertTrue(dmnTypesMap.size() == 15);
        for (BuiltInType builtInType : BuiltInType.values()) {
            Arrays.stream(builtInType.getNames()).forEach(name -> assertNotNull(dmnTypesMap.get(name)));
        }
    }

    @Test
    public void testGetCustomInheritsSimpleCustomDMNTypes() {
        // tPeople[string]
        // tMen[tPeople]

        final JSITItemDefinition tPeople = mock(JSITItemDefinition.class);
        final JSITItemDefinition tMen = mock(JSITItemDefinition.class);

        final String tPeopleType = "tPeopleType";
        final String tMenType = "tMenType";

        when(tMen.getName()).thenReturn(tMenType);
        when(tMen.getTypeRef()).thenReturn(tPeopleType);

        when(tPeople.getName()).thenReturn(tPeopleType);
        when(tPeople.getTypeRef()).thenReturn(BuiltInType.STRING.getName());

        final List<JSITItemDefinition> definitions = Arrays.asList(tPeople, tMen);

        final Map<String, ClientDMNType> dmnTypesMap = scenarioSimulationKogitoDMNDataManagerSpy.getDMNDataTypesMap(definitions, "namespace");

        assertTrue(dmnTypesMap.containsKey(tPeopleType));

        final ClientDMNType dmnPeopleType = dmnTypesMap.get(tPeopleType);

        assertTrue(dmnPeopleType.getFields().isEmpty());
        assertEquals(BuiltInType.STRING, dmnPeopleType.getFeelType());
        assertFalse(dmnPeopleType.isCollection());
        assertFalse(dmnPeopleType.isComposite());

        final ClientDMNType dmnMenType = dmnTypesMap.get(tMenType);

        assertTrue(dmnMenType.getFields().isEmpty());
        assertEquals((BuiltInType.STRING), dmnMenType.getFeelType());
        assertFalse(dmnMenType.isCollection());
        assertFalse(dmnMenType.isComposite());

        // It must contains all elements defined in BuiltInType ENUM
        assertTrue(dmnTypesMap.size() == 16);
        for (BuiltInType builtInType : BuiltInType.values()) {
            Arrays.stream(builtInType.getNames()).forEach(name -> assertNotNull(dmnTypesMap.get(name)));
        }
    }

    @Test
    public void testGetCustomInheritsImportedSimpleCustomDMNTypes() {
        // imported.tPeople[string]
        // tMen[tPeople]

        final JSITItemDefinition tPeople = mock(JSITItemDefinition.class);
        final JSITItemDefinition tMen = mock(JSITItemDefinition.class);

        final String tPeopleType = "tPeopleType";
        final String importedtPeopleType = "imported.tPeopleType";

        final String tMenType = "tMenType";

        when(tMen.getName()).thenReturn(tMenType);
        when(tMen.getTypeRef()).thenReturn(importedtPeopleType);

        when(tPeople.getName()).thenReturn(tPeopleType);
        when(tPeople.getTypeRef()).thenReturn(BuiltInType.STRING.getName());

        final List<JSITItemDefinition> definitions = Arrays.asList(tPeople, tMen);

        final Map<String, ClientDMNType> dmnTypesMap = scenarioSimulationKogitoDMNDataManagerSpy.getDMNDataTypesMap(definitions, "namespace");

        assertTrue(dmnTypesMap.containsKey(tPeopleType));

        final ClientDMNType dmnPeopleType = dmnTypesMap.get(tPeopleType);

        assertTrue(dmnPeopleType.getFields().isEmpty());
        assertEquals(BuiltInType.STRING, dmnPeopleType.getFeelType());
        assertFalse(dmnPeopleType.isCollection());
        assertFalse(dmnPeopleType.isComposite());

        final ClientDMNType dmnMenType = dmnTypesMap.get(tMenType);

        assertTrue(dmnMenType.getFields().isEmpty());
        assertEquals((BuiltInType.STRING), dmnMenType.getFeelType());
        assertFalse(dmnMenType.isCollection());
        assertFalse(dmnMenType.isComposite());

        // It must contains all elements defined in BuiltInType ENUM
        assertTrue(dmnTypesMap.size() == 16);
        for (BuiltInType builtInType : BuiltInType.values()) {
            Arrays.stream(builtInType.getNames()).forEach(name -> assertNotNull(dmnTypesMap.get(name)));
        }
    }

    @Test
    public void testGetCustomCollectionInheritsSimpleCustomDMNTypes() {
        // tPeople[string]
        // tMen[tPeople] isCollection

        final JSITItemDefinition tPeople = mock(JSITItemDefinition.class);
        final JSITItemDefinition tMen = mock(JSITItemDefinition.class);

        final String tPeopleType = "tPeopleType";
        final String tMenType = "tMenType";

        when(tMen.getName()).thenReturn(tMenType);
        when(tMen.getTypeRef()).thenReturn(tPeopleType);
        when(tMen.getIsCollection()).thenReturn(true);

        when(tPeople.getName()).thenReturn(tPeopleType);
        when(tPeople.getTypeRef()).thenReturn(BuiltInType.STRING.getName());

        final List<JSITItemDefinition> definitions = Arrays.asList(tPeople, tMen);

        final Map<String, ClientDMNType> dmnTypesMap = scenarioSimulationKogitoDMNDataManagerSpy.getDMNDataTypesMap(definitions, "namespace");

        assertTrue(dmnTypesMap.containsKey(tPeopleType));

        final ClientDMNType dmnPeopleType = dmnTypesMap.get(tPeopleType);

        assertTrue(dmnPeopleType.getFields().isEmpty());
        assertEquals(BuiltInType.STRING, dmnPeopleType.getFeelType());
        assertFalse(dmnPeopleType.isCollection());
        assertFalse(dmnPeopleType.isComposite());

        final ClientDMNType dmnMenType = dmnTypesMap.get(tMenType);

        assertTrue(dmnMenType.getFields().isEmpty());
        assertEquals(BuiltInType.STRING, dmnMenType.getFeelType());
        assertTrue(dmnMenType.isCollection());
        assertFalse(dmnMenType.isComposite());

        // It must contains all elements defined in BuiltInType ENUM
        assertTrue(dmnTypesMap.size() == 16);
        for (BuiltInType builtInType : BuiltInType.values()) {
            Arrays.stream(builtInType.getNames()).forEach(name -> assertNotNull(dmnTypesMap.get(name)));
        }
    }

    @Test
    public void testGetCustomInheritsSimpleCustomCollectionDMNTypes() {
        // tPeople[string] isCollection
        // tMen[tPeople]

        final JSITItemDefinition tPeople = mock(JSITItemDefinition.class);
        final JSITItemDefinition tMen = mock(JSITItemDefinition.class);

        final String tPeopleType = "tPeopleType";
        final String tMenType = "tMenType";

        when(tMen.getName()).thenReturn(tMenType);
        when(tMen.getTypeRef()).thenReturn(tPeopleType);

        when(tPeople.getName()).thenReturn(tPeopleType);
        when(tPeople.getTypeRef()).thenReturn(BuiltInType.STRING.getName());
        when(tPeople.getIsCollection()).thenReturn(true);

        final List<JSITItemDefinition> definitions = Arrays.asList(tPeople, tMen);

        final Map<String, ClientDMNType> dmnTypesMap = scenarioSimulationKogitoDMNDataManagerSpy.getDMNDataTypesMap(definitions, "namespace");

        assertTrue(dmnTypesMap.containsKey(tPeopleType));

        final ClientDMNType dmnPeopleType = dmnTypesMap.get(tPeopleType);

        assertTrue(dmnPeopleType.getFields().isEmpty());
        assertEquals(BuiltInType.STRING, dmnPeopleType.getFeelType());
        assertTrue(dmnPeopleType.isCollection());
        assertFalse(dmnPeopleType.isComposite());

        final ClientDMNType dmnMenType = dmnTypesMap.get(tMenType);

        assertTrue(dmnMenType.getFields().isEmpty());
        assertEquals(BuiltInType.STRING, dmnMenType.getFeelType());
        assertTrue(dmnMenType.isCollection());
        assertFalse(dmnMenType.isComposite());

        // It must contains all elements defined in BuiltInType ENUM
        assertTrue(dmnTypesMap.size() == 16);
        for (BuiltInType builtInType : BuiltInType.values()) {
            Arrays.stream(builtInType.getNames()).forEach(name -> assertNotNull(dmnTypesMap.get(name)));
        }
    }

    @Test
    public void testGetRecursiveDMNTypes() {
        // tAddress contains tCountry which contains tPeople which contains tAddress:
        // tPeople[structure]
        //    address[tAddress]
        // tAddress[structure]
        //     country[tCountry]
        // tCountry[structure]
        //     president[tPeople]

        final JSITItemDefinition tPeople = mock(JSITItemDefinition.class);
        final JSITItemDefinition tAddress = mock(JSITItemDefinition.class);
        final JSITItemDefinition tCountry = mock(JSITItemDefinition.class);
        final String tPeopleType = "tPeopleType";
        final String tAddressType = "tAddressType";
        final String tCountryType = "tCountryType";
        final String addressFieldName = "address";
        final String countryFieldName = "president";
        final String peopleFieldName = "people";

        when(tPeople.getName()).thenReturn(tPeopleType);
        when(tAddress.getName()).thenReturn(tAddressType);
        when(tCountry.getName()).thenReturn(tCountryType);

        final JSITItemDefinition address = mock(JSITItemDefinition.class);
        when(address.getTypeRef()).thenReturn(tAddressType);
        when(address.getName()).thenReturn(addressFieldName);
        final List<JSITItemDefinition> tPeopleFields = Arrays.asList(address);

        final JSITItemDefinition country = mock(JSITItemDefinition.class);
        when(country.getName()).thenReturn(countryFieldName);
        when(country.getTypeRef()).thenReturn(tCountryType);
        final List<JSITItemDefinition> tAddressFields = Arrays.asList(country);

        final JSITItemDefinition people = mock(JSITItemDefinition.class);
        when(people.getTypeRef()).thenReturn(tPeopleType);
        when(people.getName()).thenReturn(peopleFieldName);
        final List<JSITItemDefinition> tCountryFields = Arrays.asList(people);

        when(tPeople.getItemComponent()).thenReturn(tPeopleFields);
        when(tAddress.getItemComponent()).thenReturn(tAddressFields);
        when(tCountry.getItemComponent()).thenReturn(tCountryFields);

        final List<JSITItemDefinition> definitions = Arrays.asList(tPeople, tAddress, tCountry);

        final Map<String, ClientDMNType> dmnTypesMap = scenarioSimulationKogitoDMNDataManagerSpy.getDMNDataTypesMap(definitions, "namespace");

        assertTrue(dmnTypesMap.containsKey(tPeopleType));
        assertTrue(dmnTypesMap.containsKey(tAddressType));
        assertTrue(dmnTypesMap.containsKey(tCountryType));

        final ClientDMNType dmnPeopleType = dmnTypesMap.get(tPeopleType);
        final ClientDMNType dmnAddressType = dmnTypesMap.get(tAddressType);
        final ClientDMNType dmnCountryType = dmnTypesMap.get(tCountryType);

        assertEquals(1, dmnPeopleType.getFields().size());
        assertTrue(dmnPeopleType.getFields().containsKey(addressFieldName));
        assertEquals(dmnAddressType, dmnPeopleType.getFields().get(addressFieldName));
        assertFalse(dmnPeopleType.isCollection());
        assertTrue(dmnPeopleType.isComposite());

        assertEquals(1, dmnAddressType.getFields().size());
        assertTrue(dmnAddressType.getFields().containsKey(countryFieldName));
        assertEquals(dmnCountryType, dmnAddressType.getFields().get(countryFieldName));
        assertFalse(dmnAddressType.isCollection());
        assertTrue(dmnAddressType.isComposite());

        assertEquals(1, dmnCountryType.getFields().size());
        assertTrue(dmnCountryType.getFields().containsKey(peopleFieldName));
        assertEquals(dmnPeopleType, dmnCountryType.getFields().get(peopleFieldName));
        assertFalse(dmnCountryType.isCollection());
        assertTrue(dmnCountryType.isComposite());

        // It must contains all elements defined in BuiltInType ENUM
        assertTrue(dmnTypesMap.size() == 17);
        for (BuiltInType builtInType : BuiltInType.values()) {
            Arrays.stream(builtInType.getNames()).forEach(name -> assertNotNull(dmnTypesMap.get(name)));
        }
    }

    @Test
    public void testGetRecursiveAndDataTypeCollectionDMNTypes() {
        // tAddress contains tCountry which contains tPeople which contains tAddress:
        // tPeople[structure] isCollection TRUE
        //    address[tAddress]
        // tAddress[structure]
        //     country[tCountry]
        // tCountry[structure]
        //     president[tPeople]

        final JSITItemDefinition tPeople = mock(JSITItemDefinition.class);
        final JSITItemDefinition tAddress = mock(JSITItemDefinition.class);
        final JSITItemDefinition tCountry = mock(JSITItemDefinition.class);
        final String tPeopleType = "tPeopleType";
        final String tAddressType = "tAddressType";
        final String tCountryType = "tCountryType";
        final String addressFieldName = "address";
        final String countryFieldName = "president";
        final String peopleFieldName = "people";

        when(tPeople.getName()).thenReturn(tPeopleType);
        when(tAddress.getName()).thenReturn(tAddressType);
        when(tCountry.getName()).thenReturn(tCountryType);
        when(tPeople.getIsCollection()).thenReturn(true);

        final JSITItemDefinition address = mock(JSITItemDefinition.class);
        when(address.getTypeRef()).thenReturn(tAddressType);
        when(address.getName()).thenReturn(addressFieldName);
        final List<JSITItemDefinition> tPeopleFields = Arrays.asList(address);

        final JSITItemDefinition country = mock(JSITItemDefinition.class);
        when(country.getName()).thenReturn(countryFieldName);
        when(country.getTypeRef()).thenReturn(tCountryType);
        final List<JSITItemDefinition> tAddressFields = Arrays.asList(country);

        final JSITItemDefinition people = mock(JSITItemDefinition.class);
        when(people.getTypeRef()).thenReturn(tPeopleType);
        when(people.getName()).thenReturn(peopleFieldName);
        final List<JSITItemDefinition> tCountryFields = Arrays.asList(people);

        when(tPeople.getItemComponent()).thenReturn(tPeopleFields);
        when(tAddress.getItemComponent()).thenReturn(tAddressFields);
        when(tCountry.getItemComponent()).thenReturn(tCountryFields);

        final List<JSITItemDefinition> definitions = Arrays.asList(tPeople, tAddress, tCountry);

        final Map<String, ClientDMNType> dmnTypesMap = scenarioSimulationKogitoDMNDataManagerSpy.getDMNDataTypesMap(definitions, "namespace");

        assertTrue(dmnTypesMap.containsKey(tPeopleType));
        assertTrue(dmnTypesMap.containsKey(tAddressType));
        assertTrue(dmnTypesMap.containsKey(tCountryType));

        final ClientDMNType dmnPeopleType = dmnTypesMap.get(tPeopleType);
        final ClientDMNType dmnAddressType = dmnTypesMap.get(tAddressType);
        final ClientDMNType dmnCountryType = dmnTypesMap.get(tCountryType);

        assertEquals(1, dmnPeopleType.getFields().size());
        assertTrue(dmnPeopleType.getFields().containsKey(addressFieldName));
        assertEquals(dmnAddressType, dmnPeopleType.getFields().get(addressFieldName));
        assertTrue(dmnPeopleType.isCollection());
        assertTrue(dmnPeopleType.isComposite());

        assertEquals(1, dmnAddressType.getFields().size());
        assertTrue(dmnAddressType.getFields().containsKey(countryFieldName));
        assertEquals(dmnCountryType, dmnAddressType.getFields().get(countryFieldName));
        assertFalse(dmnAddressType.isCollection());
        assertTrue(dmnAddressType.isComposite());

        assertEquals(1, dmnCountryType.getFields().size());
        assertTrue(dmnCountryType.getFields().containsKey(peopleFieldName));
        assertEquals(dmnPeopleType, dmnCountryType.getFields().get(peopleFieldName));
        assertFalse(dmnCountryType.isCollection());
        assertTrue(dmnCountryType.isComposite());

        // It must contains all elements defined in BuiltInType ENUM
        assertTrue(dmnTypesMap.size() == 17);
        for (BuiltInType builtInType : BuiltInType.values()) {
            Arrays.stream(builtInType.getNames()).forEach(name -> assertNotNull(dmnTypesMap.get(name)));
        }
    }

    @Test
    public void testGetRecursiveAndFieldCollectionsDMNTypes() {
        // tAddress contains tCountry which contains tPeople which contains tAddress:
        // tPeople[structure]
        //    address[tAddress]
        // tAddress[structure]
        //     country[tCountry]
        // tCountry[structure]
        //     president[tPeople] isCollection TRUE
        //     regions[string] isCollection TRUE
        //     name[string]

        final JSITItemDefinition tPeople = mock(JSITItemDefinition.class);
        final JSITItemDefinition tAddress = mock(JSITItemDefinition.class);
        final JSITItemDefinition tCountry = mock(JSITItemDefinition.class);
        final String tPeopleType = "tPeopleType";
        final String tAddressType = "tAddressType";
        final String tCountryType = "tCountryType";
        final String addressFieldName = "address";
        final String countryFieldName = "president";
        final String peopleFieldName = "people";
        final String regionsName = "regions";
        final String nameLabel = "name";

        when(tPeople.getName()).thenReturn(tPeopleType);
        when(tAddress.getName()).thenReturn(tAddressType);
        when(tCountry.getName()).thenReturn(tCountryType);

        final JSITItemDefinition address = mock(JSITItemDefinition.class);
        when(address.getTypeRef()).thenReturn(tAddressType);
        when(address.getName()).thenReturn(addressFieldName);
        final List<JSITItemDefinition> tPeopleFields = Arrays.asList(address);

        final JSITItemDefinition country = mock(JSITItemDefinition.class);
        when(country.getName()).thenReturn(countryFieldName);
        when(country.getTypeRef()).thenReturn(tCountryType);
        final List<JSITItemDefinition> tAddressFields = Arrays.asList(country);

        final JSITItemDefinition people = mock(JSITItemDefinition.class);
        when(people.getTypeRef()).thenReturn(tPeopleType);
        when(people.getName()).thenReturn(peopleFieldName);
        when(people.getIsCollection()).thenReturn(true);
        final JSITItemDefinition regions = mock(JSITItemDefinition.class);
        when(regions.getTypeRef()).thenReturn(BuiltInType.STRING.getName());
        when(regions.getName()).thenReturn(regionsName);
        when(regions.getIsCollection()).thenReturn(true);
        final JSITItemDefinition nameField = mock(JSITItemDefinition.class);
        when(nameField.getTypeRef()).thenReturn(BuiltInType.STRING.getName());
        when(nameField.getName()).thenReturn(nameLabel);
        final List<JSITItemDefinition> tCountryFields = Arrays.asList(people, regions, nameField);

        when(tPeople.getItemComponent()).thenReturn(tPeopleFields);
        when(tAddress.getItemComponent()).thenReturn(tAddressFields);
        when(tCountry.getItemComponent()).thenReturn(tCountryFields);

        final List<JSITItemDefinition> definitions = Arrays.asList(tPeople, tAddress, tCountry);

        final Map<String, ClientDMNType> dmnTypesMap = scenarioSimulationKogitoDMNDataManagerSpy.getDMNDataTypesMap(definitions, "namespace");

        assertTrue(dmnTypesMap.containsKey(tPeopleType));
        assertTrue(dmnTypesMap.containsKey(tAddressType));
        assertTrue(dmnTypesMap.containsKey(tCountryType));

        final ClientDMNType dmnPeopleType = dmnTypesMap.get(tPeopleType);
        final ClientDMNType dmnAddressType = dmnTypesMap.get(tAddressType);
        final ClientDMNType dmnCountryType = dmnTypesMap.get(tCountryType);
        final ClientDMNType stringType = dmnTypesMap.get(BuiltInType.STRING.getName());

        assertEquals(1, dmnPeopleType.getFields().size());
        assertTrue(dmnPeopleType.getFields().containsKey(addressFieldName));
        assertEquals(dmnAddressType, dmnPeopleType.getFields().get(addressFieldName));
        assertNull(dmnPeopleType.getFeelType());
        assertFalse(dmnPeopleType.isCollection());
        assertTrue(dmnPeopleType.isComposite());

        assertEquals(1, dmnAddressType.getFields().size());
        assertTrue(dmnAddressType.getFields().containsKey(countryFieldName));
        assertEquals(dmnCountryType, dmnAddressType.getFields().get(countryFieldName));
        assertNull(dmnAddressType.getFeelType());
        assertFalse(dmnAddressType.isCollection());
        assertTrue(dmnAddressType.isComposite());

        assertEquals(3, dmnCountryType.getFields().size());
        assertTrue(dmnCountryType.getFields().containsKey(peopleFieldName));
        assertNotEquals(dmnPeopleType, dmnCountryType.getFields().get(peopleFieldName));
        assertTrue(dmnCountryType.getFields().get(peopleFieldName).isCollection());
        assertTrue(dmnCountryType.getFields().get(peopleFieldName).isComposite());
        assertEquals(tPeopleType, dmnCountryType.getFields().get(peopleFieldName).getName());
        assertTrue(dmnCountryType.getFields().containsKey(regionsName));
        assertNull(dmnCountryType.getFeelType());
        assertNotEquals(stringType, dmnCountryType.getFields().get(regionsName));
        assertTrue(dmnCountryType.getFields().get(regionsName).isCollection());
        assertFalse(dmnCountryType.getFields().get(regionsName).isComposite());
        assertEquals(BuiltInType.STRING.getName(), dmnCountryType.getFields().get(regionsName).getName());
        assertTrue(dmnCountryType.getFields().containsKey(nameLabel));
        assertEquals(stringType, dmnCountryType.getFields().get(nameLabel));
        assertEquals(BuiltInType.STRING, stringType.getFeelType());
        assertFalse(stringType.isCollection());
        assertFalse(stringType.isComposite());

        // It must contains all elements defined in BuiltInType ENUM
        assertTrue(dmnTypesMap.size() == 17);
        for (BuiltInType builtInType : BuiltInType.values()) {
            Arrays.stream(builtInType.getNames()).forEach(name -> assertNotNull(dmnTypesMap.get(name)));
        }
    }

    @Test
    public void createDMNTypeItemDefinitionSimpleTypeRefNoFields() {
        Map<String, ClientDMNType> defaultTypesMap = new HashMap<>(scenarioSimulationKogitoDMNDataManagerSpy.getDMNDataTypesMap(Collections.emptyList(), NAMESPACE));
        allDefinitions = scenarioSimulationKogitoDMNDataManagerSpy.indexDefinitionsByName(Arrays.asList(jsitItemDefinitionMock));
        when(jsitItemDefinitionMock.getItemComponent()).thenReturn(new ArrayList<>());
        when(jsitItemDefinitionMock.getTypeRef()).thenReturn(BuiltInType.STRING.getName());
        ClientDMNType clientDmnType = scenarioSimulationKogitoDMNDataManagerSpy.createDMNType(allDefinitions,
                                                                                              jsitItemDefinitionMock,
                                                                                              NAMESPACE,
                                                                                              defaultTypesMap);
        assertEquals(NAMESPACE, clientDmnType.getNamespace());
        assertEquals(TYPE_NAME, clientDmnType.getName());
        assertFalse(clientDmnType.isCollection());
        assertFalse(clientDmnType.isComposite());
        assertTrue(clientDmnType.getFields().isEmpty());
        assertEquals(BuiltInType.STRING, clientDmnType.getFeelType());
        assertNull(clientDmnType.getBaseType());
        assertTrue(defaultTypesMap.containsKey(TYPE_NAME));
        assertTrue(defaultTypesMap.containsValue(clientDmnType));
    }

    @Test
    public void createDMNTypeItemDefinitionSimpleTypeRefNoFieldsWithAllowedValues() {
        Map<String, ClientDMNType> defaultTypesMap = new HashMap<>(scenarioSimulationKogitoDMNDataManagerSpy.getDMNDataTypesMap(Collections.emptyList(), NAMESPACE));
        allDefinitions = scenarioSimulationKogitoDMNDataManagerSpy.indexDefinitionsByName(Arrays.asList(jsitItemDefinitionMock));
        when(jsitItemDefinitionMock.getItemComponent()).thenReturn(new ArrayList<>());
        when(jsitItemDefinitionMock.getTypeRef()).thenReturn(BuiltInType.STRING.getName());
        JSITUnaryTests jsitUnaryTestsMock = mock(JSITUnaryTests.class);
        when(jsitUnaryTestsMock.getText()).thenReturn("value1, value2, value3");
        when(jsitItemDefinitionMock.getAllowedValues()).thenReturn(jsitUnaryTestsMock);
        ClientDMNType clientDmnType = scenarioSimulationKogitoDMNDataManagerSpy.createDMNType(allDefinitions,
                                                                                              jsitItemDefinitionMock,
                                                                                              NAMESPACE,
                                                                                              defaultTypesMap);
        assertEquals(NAMESPACE, clientDmnType.getNamespace());
        assertEquals(TYPE_NAME, clientDmnType.getName());
        assertFalse(clientDmnType.isCollection());
        assertFalse(clientDmnType.isComposite());
        assertTrue(clientDmnType.getFields().isEmpty());
        assertEquals(BuiltInType.STRING, clientDmnType.getFeelType());
        assertEquals("string", clientDmnType.getBaseType().getName());
        assertTrue(defaultTypesMap.containsKey(TYPE_NAME));
        assertTrue(defaultTypesMap.containsValue(clientDmnType));
    }

    @Test
    public void createDMNTypeSimpleItemComponentSimpleTypeRefNoFields() {
        Map<String, ClientDMNType> defaultTypesMap = new HashMap<>(scenarioSimulationKogitoDMNDataManagerSpy.getDMNDataTypesMap(Collections.emptyList(), NAMESPACE));
        when(jsitItemDefinitionMock.getItemComponent()).thenReturn(new ArrayList<>());
        when(jsitItemDefinitionMock.getTypeRef()).thenReturn(BuiltInType.STRING.getName());
        ClientDMNType clientDmnType = scenarioSimulationKogitoDMNDataManagerSpy.createDMNType(allDefinitions,
                                                                                              jsitItemDefinitionMock,
                                                                                              NAMESPACE,
                                                                                              defaultTypesMap);
        assertEquals(NAMESPACE, clientDmnType.getNamespace());
        assertEquals(TYPE_NAME, clientDmnType.getName());
        assertFalse(clientDmnType.isCollection());
        assertFalse(clientDmnType.isComposite());
        assertTrue(clientDmnType.getFields().isEmpty());
        assertEquals(BuiltInType.STRING, clientDmnType.getFeelType());
        assertFalse(defaultTypesMap.containsKey(TYPE_NAME));
        assertFalse(defaultTypesMap.containsValue(clientDmnType));
    }

    @Test
    public void createDMNTypeItemDefinitionWithField() {
        Map<String, ClientDMNType> defaultTypesMap = new HashMap<>(scenarioSimulationKogitoDMNDataManagerSpy.getDMNDataTypesMap(Collections.emptyList(), NAMESPACE));
        when(jsitItemDefinitionMock.getItemComponent()).thenReturn(Arrays.asList(jsitItemDefinitionNestedMock));
        when(jsitItemDefinitionNestedMock.getName()).thenReturn("tNested");
        when(jsitItemDefinitionNestedMock.getId()).thenReturn(ID);
        when(jsitItemDefinitionNestedMock.getTypeRef()).thenReturn(BuiltInType.STRING.getName());
        allDefinitions = scenarioSimulationKogitoDMNDataManagerSpy.indexDefinitionsByName(Arrays.asList(jsitItemDefinitionMock));
        ClientDMNType clientDmnType = scenarioSimulationKogitoDMNDataManagerSpy.createDMNType(allDefinitions,
                                                                                              jsitItemDefinitionMock,
                                                                                              NAMESPACE,
                                                                                              defaultTypesMap);
        assertEquals(NAMESPACE, clientDmnType.getNamespace());
        assertEquals(TYPE_NAME, clientDmnType.getName());
        assertFalse(clientDmnType.isCollection());
        assertTrue(clientDmnType.isComposite());
        assertNotNull(clientDmnType.getFields());
        assertTrue(clientDmnType.getFields().size() == 1);
        assertEquals(BuiltInType.STRING, clientDmnType.getFields().get("tNested").getFeelType());
        assertFalse(clientDmnType.getFields().get("tNested").isCollection());
        assertFalse(clientDmnType.getFields().get("tNested").isComposite());
        assertNull(clientDmnType.getFeelType());
        assertTrue(defaultTypesMap.containsKey(TYPE_NAME));
        assertTrue(defaultTypesMap.containsValue(clientDmnType));
    }

    @Test
    public void createDMNTypeItemComponentWithField() {
        Map<String, ClientDMNType> defaultTypesMap = new HashMap<>(scenarioSimulationKogitoDMNDataManagerSpy.getDMNDataTypesMap(Collections.emptyList(), NAMESPACE));
        when(jsitItemDefinitionMock.getItemComponent()).thenReturn(Arrays.asList(jsitItemDefinitionNestedMock));
        when(jsitItemDefinitionNestedMock.getName()).thenReturn("tNested");
        when(jsitItemDefinitionNestedMock.getId()).thenReturn(ID);
        when(jsitItemDefinitionNestedMock.getTypeRef()).thenReturn(BuiltInType.STRING.getName());
        ClientDMNType clientDmnType = scenarioSimulationKogitoDMNDataManagerSpy.createDMNType(allDefinitions,
                                                                                              jsitItemDefinitionMock,
                                                                                              NAMESPACE,
                                                                                              defaultTypesMap);
        assertEquals(NAMESPACE, clientDmnType.getNamespace());
        assertEquals(TYPE_NAME, clientDmnType.getName());
        assertFalse(clientDmnType.isCollection());
        assertTrue(clientDmnType.isComposite());
        assertNotNull(clientDmnType.getFields());
        assertTrue(clientDmnType.getFields().size() == 1);
        assertEquals(BuiltInType.STRING, clientDmnType.getFields().get("tNested").getFeelType());
        assertFalse(clientDmnType.getFields().get("tNested").isCollection());
        assertFalse(clientDmnType.getFields().get("tNested").isComposite());
        assertNull(clientDmnType.getFields().get("tNested").getBaseType());
        assertNull(clientDmnType.getFeelType());
        assertFalse(defaultTypesMap.containsKey(TYPE_NAME));
        assertFalse(defaultTypesMap.containsValue(clientDmnType));
    }

    @Test
    public void createDMNTypeItemComponentWithFieldWithAllowedValues() {
        Map<String, ClientDMNType> defaultTypesMap = new HashMap<>(scenarioSimulationKogitoDMNDataManagerSpy.getDMNDataTypesMap(Collections.emptyList(), NAMESPACE));
        when(jsitItemDefinitionMock.getItemComponent()).thenReturn(Arrays.asList(jsitItemDefinitionNestedMock));
        when(jsitItemDefinitionNestedMock.getName()).thenReturn("tNested");
        when(jsitItemDefinitionNestedMock.getId()).thenReturn(ID);
        when(jsitItemDefinitionNestedMock.getTypeRef()).thenReturn(BuiltInType.STRING.getName());
        JSITUnaryTests jsitUnaryTestsMock = mock(JSITUnaryTests.class);
        when(jsitUnaryTestsMock.getText()).thenReturn("value1, value2, value3");
        when(jsitItemDefinitionNestedMock.getAllowedValues()).thenReturn(jsitUnaryTestsMock);
        ClientDMNType clientDmnType = scenarioSimulationKogitoDMNDataManagerSpy.createDMNType(allDefinitions,
                                                                                              jsitItemDefinitionMock,
                                                                                              NAMESPACE,
                                                                                              defaultTypesMap);
        assertEquals(NAMESPACE, clientDmnType.getNamespace());
        assertEquals(TYPE_NAME, clientDmnType.getName());
        assertFalse(clientDmnType.isCollection());
        assertTrue(clientDmnType.isComposite());
        assertNotNull(clientDmnType.getFields());
        assertTrue(clientDmnType.getFields().size() == 1);
        assertEquals(BuiltInType.STRING, clientDmnType.getFields().get("tNested").getFeelType());
        assertFalse(clientDmnType.getFields().get("tNested").isCollection());
        assertFalse(clientDmnType.getFields().get("tNested").isComposite());
        assertEquals("string", clientDmnType.getFields().get("tNested").getBaseType().getName());
        assertNull(clientDmnType.getFeelType());
        assertFalse(defaultTypesMap.containsKey(TYPE_NAME));
        assertFalse(defaultTypesMap.containsValue(clientDmnType));
    }

    @Test
    public void createDMNTypeItemDefinitionWithCollectionField() {
        Map<String, ClientDMNType> defaultTypesMap = new HashMap<>(scenarioSimulationKogitoDMNDataManagerSpy.getDMNDataTypesMap(Collections.emptyList(), NAMESPACE));
        when(jsitItemDefinitionMock.getItemComponent()).thenReturn(Arrays.asList(jsitItemDefinitionNestedMock));
        when(jsitItemDefinitionNestedMock.getName()).thenReturn("tNested");
        when(jsitItemDefinitionNestedMock.getId()).thenReturn(ID);
        when(jsitItemDefinitionNestedMock.getTypeRef()).thenReturn(BuiltInType.STRING.getName());
        when(jsitItemDefinitionNestedMock.getIsCollection()).thenReturn(true);
        allDefinitions = scenarioSimulationKogitoDMNDataManagerSpy.indexDefinitionsByName(Arrays.asList(jsitItemDefinitionMock));
        ClientDMNType clientDmnType = scenarioSimulationKogitoDMNDataManagerSpy.createDMNType(allDefinitions,
                                                                                              jsitItemDefinitionMock,
                                                                                              NAMESPACE,
                                                                                              defaultTypesMap);
        assertEquals(NAMESPACE, clientDmnType.getNamespace());
        assertEquals(TYPE_NAME, clientDmnType.getName());
        assertFalse(clientDmnType.isCollection());
        assertTrue(clientDmnType.isComposite());
        assertNotNull(clientDmnType.getFields());
        assertTrue(clientDmnType.getFields().size() == 1);
        assertEquals(BuiltInType.STRING, clientDmnType.getFields().get("tNested").getFeelType());
        assertTrue(clientDmnType.getFields().get("tNested").isCollection());
        assertFalse(clientDmnType.getFields().get("tNested").isComposite());
        assertNull(clientDmnType.getFeelType());
        assertTrue(defaultTypesMap.containsKey(TYPE_NAME));
        assertTrue(defaultTypesMap.containsValue(clientDmnType));
    }

    @Test
    public void getFactModelTupleEmptyElements() {
        FactModelTuple factModelTuple = scenarioSimulationKogitoDMNDataManagerSpy.getFactModelTuple(jsiITDefinitionsMock);
        assertTrue(factModelTuple.getVisibleFacts().isEmpty());
        assertTrue(factModelTuple.getHiddenFacts().isEmpty());
    }

    @Test
    public void getFactModelTupleSimpleInputData() {
        when(jsiITInputDataMock.getName()).thenReturn("inputDataName");
        drgElements.add(jsiITInputDataMock);
        when(jsiITDefinitionsMock.getDrgElement()).thenReturn(drgElements);
        attributesMapInput.put(TYPEREF_QNAME, "number");
        FactModelTuple factModelTuple = scenarioSimulationKogitoDMNDataManagerSpy.getFactModelTuple(jsiITDefinitionsMock);
        assertTrue(factModelTuple.getVisibleFacts().size() == 1);
        FactModelTree inputDataNameFact = factModelTuple.getVisibleFacts().get("inputDataName");
        assertNotNull(inputDataNameFact);
        assertTrue(inputDataNameFact.getSimpleProperties().size() == 1);
        assertEquals("number", inputDataNameFact.getSimpleProperties().get(VALUE).getTypeName());
        assertEquals("number", inputDataNameFact.getSimpleProperties().get(VALUE).getPropertyTypeNameToVisualize());
        assertFalse(inputDataNameFact.getSimpleProperties().get(VALUE).getBaseTypeName().isPresent());
    }

    @Test
    public void getFactModelTupleSimpleDecisionData() {
        when(jsiITInformationItemDecisionMock.getName()).thenReturn("inputDecisionName");
        drgElements.add(jsiITDecisionMock);
        when(jsiITDefinitionsMock.getDrgElement()).thenReturn(drgElements);
        attributesMapDecision.put(TYPEREF_QNAME, "string");
        FactModelTuple factModelTuple = scenarioSimulationKogitoDMNDataManagerSpy.getFactModelTuple(jsiITDefinitionsMock);
        assertTrue(factModelTuple.getVisibleFacts().size() == 1);
        FactModelTree decisionDataNameFact = factModelTuple.getVisibleFacts().get("inputDecisionName");
        assertNotNull(decisionDataNameFact);
        assertTrue(decisionDataNameFact.getSimpleProperties().size() == 1);
        assertEquals("string", decisionDataNameFact.getSimpleProperties().get(VALUE).getTypeName());
        assertEquals("string", decisionDataNameFact.getSimpleProperties().get(VALUE).getPropertyTypeNameToVisualize());
        assertFalse(decisionDataNameFact.getSimpleProperties().get(VALUE).getBaseTypeName().isPresent());
    }

    @Test
    public void getFactModelTupleSimpleInputAndDecisionData() {
        when(jsiITInputDataMock.getName()).thenReturn("inputDataName");
        when(jsiITInformationItemDecisionMock.getName()).thenReturn("inputDecisionName");
        drgElements.add(jsiITDecisionMock);
        drgElements.add(jsiITInputDataMock);
        when(jsiITDefinitionsMock.getDrgElement()).thenReturn(drgElements);
        attributesMapInput.put(TYPEREF_QNAME, "number");
        attributesMapDecision.put(TYPEREF_QNAME, "string");
        FactModelTuple factModelTuple = scenarioSimulationKogitoDMNDataManagerSpy.getFactModelTuple(jsiITDefinitionsMock);
        assertTrue(factModelTuple.getVisibleFacts().size() == 2);
        FactModelTree inputDataNameFact = factModelTuple.getVisibleFacts().get("inputDataName");
        assertNotNull(inputDataNameFact);
        assertTrue(inputDataNameFact.getSimpleProperties().size() == 1);
        assertEquals("number", inputDataNameFact.getSimpleProperties().get(VALUE).getTypeName());
        assertEquals("number", inputDataNameFact.getSimpleProperties().get(VALUE).getPropertyTypeNameToVisualize());
        assertFalse(inputDataNameFact.getSimpleProperties().get(VALUE).getBaseTypeName().isPresent());
        FactModelTree decisionDataNameFact = factModelTuple.getVisibleFacts().get("inputDecisionName");
        assertNotNull(decisionDataNameFact);
        assertTrue(decisionDataNameFact.getSimpleProperties().size() == 1);
        assertEquals("string", decisionDataNameFact.getSimpleProperties().get(VALUE).getTypeName());
        assertEquals("string", decisionDataNameFact.getSimpleProperties().get(VALUE).getPropertyTypeNameToVisualize());
        assertFalse(decisionDataNameFact.getSimpleProperties().get(VALUE).getBaseTypeName().isPresent());
    }

    @Test
    public void createTopLevelFactModelTreeSimpleNoCollection() {
        // Single property retrieve
        ClientDMNType simpleString = getSimpleNoCollection();
        FactModelTree retrieved = scenarioSimulationKogitoDMNDataManagerSpy.createTopLevelFactModelTree("testPath", simpleString, new TreeMap<>(), FactModelTree.Type.INPUT);
        Assert.assertNotNull(retrieved);
        assertEquals("testPath", retrieved.getFactName());
        assertEquals(1, retrieved.getSimpleProperties().size());
        assertTrue(retrieved.getSimpleProperties().containsKey(VALUE));
        assertEquals(simpleString.getName(), retrieved.getSimpleProperties().get(VALUE).getTypeName());
        assertFalse(retrieved.getSimpleProperties().get(VALUE).getBaseTypeName().isPresent());
        assertEquals(simpleString.getName(), retrieved.getSimpleProperties().get(VALUE).getPropertyTypeNameToVisualize());
        assertTrue(retrieved.getGenericTypesMap().isEmpty());
    }

    @Test
    public void createTopLevelFactModelTreeSimpleCollection() {
        // Single property collection retrieve
        ClientDMNType simpleCollectionString = getSimpleCollection();
        TreeMap<String, FactModelTree> hiddenFactSimpleCollection = new TreeMap<>();
        FactModelTree retrieved = scenarioSimulationKogitoDMNDataManagerSpy.createTopLevelFactModelTree("testPath", simpleCollectionString, hiddenFactSimpleCollection, FactModelTree.Type.INPUT);
        Assert.assertNotNull(retrieved);
        assertEquals("testPath", retrieved.getFactName());
        assertEquals(1, retrieved.getSimpleProperties().size());
        assertTrue(retrieved.getSimpleProperties().containsKey(VALUE));
        assertEquals("java.util.List", retrieved.getSimpleProperties().get(VALUE).getTypeName());
        assertFalse(retrieved.getSimpleProperties().get(VALUE).getBaseTypeName().isPresent());
        assertEquals("java.util.List", retrieved.getSimpleProperties().get(VALUE).getPropertyTypeNameToVisualize());        assertTrue(retrieved.getExpandableProperties().isEmpty());
        assertEquals(1, retrieved.getGenericTypesMap().size());
        assertTrue(retrieved.getGenericTypesMap().containsKey(VALUE));
        Assert.assertNotNull(retrieved.getGenericTypesMap().get(VALUE));
        assertEquals(1, retrieved.getGenericTypesMap().get(VALUE).size());
        assertEquals(simpleCollectionString.getName(), retrieved.getGenericTypesMap().get(VALUE).get(0));
    }

    @Test
    public void createTopLevelFactModelTreeCompositeNoCollectionBaseType() {
        // Single property retrieve
        ClientDMNType composite = getSingleCompositeWithBaseTypeField();
        FactModelTree retrieved = scenarioSimulationKogitoDMNDataManagerSpy.createTopLevelFactModelTree("testPath", composite, new TreeMap<>(), FactModelTree.Type.INPUT);
        assertNotNull(retrieved);
        assertEquals("testPath", retrieved.getFactName());
        assertEquals(2, retrieved.getSimpleProperties().size());
        assertTrue(retrieved.getSimpleProperties().containsKey("name"));
        assertEquals(TYPE_NAME, retrieved.getSimpleProperties().get("name").getTypeName());
        assertFalse(retrieved.getSimpleProperties().get("name").getBaseTypeName().isPresent());
        assertEquals(TYPE_NAME, retrieved.getSimpleProperties().get("name").getPropertyTypeNameToVisualize());
        //
        assertTrue(retrieved.getSimpleProperties().containsKey("gender"));
        assertEquals("gender", retrieved.getSimpleProperties().get("gender").getTypeName());
        assertEquals("string", retrieved.getSimpleProperties().get("gender").getBaseTypeName().get());
        assertEquals("string", retrieved.getSimpleProperties().get("gender").getPropertyTypeNameToVisualize());
        assertTrue(retrieved.getExpandableProperties().isEmpty());
        assertTrue(retrieved.getGenericTypesMap().isEmpty());
    }

    @Test
    public void createTopLevelFactModelTreeCompositeNoCollection() {
        // Single property retrieve
        ClientDMNType compositePerson = getSingleCompositeWithSimpleCollection();
        FactModelTree retrieved = scenarioSimulationKogitoDMNDataManagerSpy.createTopLevelFactModelTree("testPath", compositePerson, new TreeMap<>(), FactModelTree.Type.INPUT);
        Assert.assertNotNull(retrieved);
        assertEquals("testPath", retrieved.getFactName());
        assertEquals(2, retrieved.getSimpleProperties().size());
        assertTrue(retrieved.getSimpleProperties().containsKey("friends"));
        assertEquals("java.util.List", retrieved.getSimpleProperties().get("friends").getTypeName());
        assertFalse(retrieved.getSimpleProperties().get("friends").getBaseTypeName().isPresent());
        assertEquals("java.util.List", retrieved.getSimpleProperties().get("friends").getPropertyTypeNameToVisualize());
        assertTrue(retrieved.getSimpleProperties().containsKey("name"));
        assertEquals(TYPE_NAME, retrieved.getSimpleProperties().get("name").getTypeName());
        assertFalse(retrieved.getSimpleProperties().get("name").getBaseTypeName().isPresent());
        assertEquals(TYPE_NAME, retrieved.getSimpleProperties().get("name").getPropertyTypeNameToVisualize());
        //
        assertEquals(1, retrieved.getGenericTypesMap().size());
        assertTrue(retrieved.getGenericTypesMap().containsKey("friends"));
        assertEquals(compositePerson.getFields().get("friends").getName(), retrieved.getGenericTypesMap().get("friends").get(0));
        //
        assertEquals(2, retrieved.getExpandableProperties().size());
        assertTrue(retrieved.getExpandableProperties().containsKey("EXPANDABLE_PROPERTY_PHONENUMBERS"));
        assertEquals("tPhoneNumber", retrieved.getExpandableProperties().get("EXPANDABLE_PROPERTY_PHONENUMBERS"));
        assertTrue(retrieved.getExpandableProperties().containsKey("EXPANDABLE_PROPERTY_DETAILS"));
        assertEquals("tDetails", retrieved.getExpandableProperties().get("EXPANDABLE_PROPERTY_DETAILS"));
    }

    @Test
    public void createTopLevelFactModelTreeCompositeCollection() {
        // Single property collection retrieve
        ClientDMNType compositePerson = getCompositeCollection();
        TreeMap<String, FactModelTree> hiddenFactSimpleCollection = new TreeMap<>();
        FactModelTree retrieved = scenarioSimulationKogitoDMNDataManagerSpy.createTopLevelFactModelTree("testPath", compositePerson, hiddenFactSimpleCollection, FactModelTree.Type.INPUT);
        Assert.assertNotNull(retrieved);
        assertEquals("testPath", retrieved.getFactName());
        assertEquals(1, retrieved.getSimpleProperties().size());
        assertTrue(retrieved.getSimpleProperties().containsKey(VALUE));
        assertEquals("java.util.List", retrieved.getSimpleProperties().get(VALUE).getTypeName());
        assertFalse(retrieved.getSimpleProperties().get(VALUE).getBaseTypeName().isPresent());
        assertEquals("java.util.List", retrieved.getSimpleProperties().get(VALUE).getPropertyTypeNameToVisualize());        assertTrue(retrieved.getExpandableProperties().isEmpty());
        assertEquals(1, retrieved.getGenericTypesMap().size());
        assertTrue(retrieved.getGenericTypesMap().containsKey(VALUE));
        Assert.assertNotNull(retrieved.getGenericTypesMap().get(VALUE));
        assertEquals(1, retrieved.getGenericTypesMap().get(VALUE).size());
        assertEquals(compositePerson.getName(), retrieved.getGenericTypesMap().get(VALUE).get(0));
    }

    private ClientDMNType getSimpleNoCollection() {
        return new ClientDMNType(NAMESPACE, TYPE_NAME, null, false, false, null, null);
    }

    private ClientDMNType getSimpleCollection() {
        return new ClientDMNType(NAMESPACE, TYPE_NAME, null, true, false, null, null);
    }

    private ClientDMNType getSingleCompositeWithSimpleCollection() {
        Map<String, ClientDMNType> phoneNumberCompositeFields = new HashMap<>();
        phoneNumberCompositeFields.put("PHONENUMBER_PREFIX", new ClientDMNType(null, TYPE_NAME, null, false, null));
        phoneNumberCompositeFields.put("PHONENUMBER_NUMBER", new ClientDMNType(null, TYPE_NAME, null, false, null));
        ClientDMNType phoneNumberComposite = new ClientDMNType(NAMESPACE, "tPhoneNumber", null, false, true, phoneNumberCompositeFields, null);

        Map<String, ClientDMNType> detailsCompositeFields = new HashMap<>();

        detailsCompositeFields.put("gender", new ClientDMNType(null, TYPE_NAME, null, false, null));
        detailsCompositeFields.put("weight", new ClientDMNType(null, TYPE_NAME, null, false, null));
        ClientDMNType detailsComposite = new ClientDMNType(NAMESPACE, "tDetails", "tDetails", false, true, detailsCompositeFields, null);

        ClientDMNType nameSimple = new ClientDMNType(null, TYPE_NAME, null, false, null);

        ClientDMNType friendsSimpleCollection = new ClientDMNType(null, TYPE_NAME, null, true, null);

        Map<String, ClientDMNType> compositePersonField = new HashMap<>();
        compositePersonField.put("friends", friendsSimpleCollection);
        compositePersonField.put("EXPANDABLE_PROPERTY_PHONENUMBERS", phoneNumberComposite);
        compositePersonField.put("EXPANDABLE_PROPERTY_DETAILS", detailsComposite);
        compositePersonField.put("name", nameSimple);
        return new ClientDMNType(NAMESPACE, TYPE_NAME, null, false, true, compositePersonField, null);
    }

    protected ClientDMNType getCompositeCollection() {
        Map<String, ClientDMNType> phoneNumberCompositeFields = new HashMap<>();
        phoneNumberCompositeFields.put("PHONENUMBER_PREFIX", new ClientDMNType(null, TYPE_NAME, null, false, null));
        phoneNumberCompositeFields.put("PHONENUMBER_NUMBER", new ClientDMNType(null, TYPE_NAME, null, false, null));
        ClientDMNType phoneNumberComposite = new ClientDMNType(NAMESPACE, "tPhoneNumber", null, false, true, phoneNumberCompositeFields, null);

        Map<String, ClientDMNType> detailsCompositeFields = new HashMap<>();
        detailsCompositeFields.put("gender", new ClientDMNType(null, TYPE_NAME, null, false, null));
        detailsCompositeFields.put("weight", new ClientDMNType(null, TYPE_NAME, null, false, null));
        ClientDMNType detailsComposite = new ClientDMNType(NAMESPACE, "tDetails", "tDetails", false, true, detailsCompositeFields, null);

        ClientDMNType nameSimple = new ClientDMNType(null, TYPE_NAME, null, false, null);

        ClientDMNType friendsSimpleCollection = new ClientDMNType(null, TYPE_NAME, null, true, null);

        Map<String, ClientDMNType> compositePersonField = new HashMap<>();
        compositePersonField.put("friends", friendsSimpleCollection);
        compositePersonField.put("EXPANDABLE_PROPERTY_PHONENUMBERS", phoneNumberComposite);
        compositePersonField.put("EXPANDABLE_PROPERTY_DETAILS", detailsComposite);
        compositePersonField.put("name", nameSimple);

        return new ClientDMNType(NAMESPACE, TYPE_NAME, null, true, true, compositePersonField, null);
    }

    private ClientDMNType getSingleCompositeWithBaseTypeField() {
        // Complex object retrieve
        ClientDMNType toReturn = new ClientDMNType(null, "tComposite", null, false, true);
        ClientDMNType genderDMNType = new ClientDMNType(null, "gender", null, false, null);
        genderDMNType.setBaseType(new ClientDMNType(null, "string", null, false, null));

        toReturn.addField("gender", genderDMNType);
        toReturn.addField("name", new ClientDMNType(null, TYPE_NAME, null, false, null));

        return toReturn;
    }
}