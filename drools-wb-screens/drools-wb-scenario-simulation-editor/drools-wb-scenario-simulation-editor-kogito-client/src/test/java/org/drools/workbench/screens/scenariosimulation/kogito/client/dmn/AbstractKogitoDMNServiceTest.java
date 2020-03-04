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
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
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
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;

import static junit.framework.TestCase.assertNotNull;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;
import static org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.AbstractKogitoDMNService.TYPEREF_QNAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractKogitoDMNServiceTest {

    public static final String NAMESPACE = "namespace";
    public static final String TYPE_NAME = "name";
    public static final String ID = "id";

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

    private AbstractKogitoDMNService abstractKogitoDMNServiceSpy;
    private List<JSITItemDefinition> jstiItemDefinitions;
    private List<JSITDRGElement> jsitdrgElements;
    private List<JSITDRGElement> drgElements;
    private Map<QName, String> attributesMapInput;
    private Map<QName, String> attributesMapDecision;

    @Before
    public void setup() {
        jstiItemDefinitions = new ArrayList<>();
        jsitdrgElements = new ArrayList<>();
        drgElements = new ArrayList<>();
        attributesMapInput = new HashMap<>();
        attributesMapDecision = new HashMap<>();
        abstractKogitoDMNServiceSpy = spy(new AbstractKogitoDMNService() {
            @Override
            public void getDMNContent(Path path, RemoteCallback<String> remoteCallback, ErrorCallback<Object> errorCallback) {
                //Do nothing
            }
        });
        doReturn(true).when(abstractKogitoDMNServiceSpy).isJSITInputData(eq(jsiITInputDataMock));
        doReturn(true).when(abstractKogitoDMNServiceSpy).isJSITDecision(eq(jsiITDecisionMock));
        doReturn(attributesMapInput).when(abstractKogitoDMNServiceSpy).getOtherAttributesMap(eq(jsiITInformationItemInputMock));
        doReturn(attributesMapDecision).when(abstractKogitoDMNServiceSpy).getOtherAttributesMap(eq(jsiITInformationItemDecisionMock));
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
    public void getFactModelTupleEmptyElements() {
        attributesMapInput.put(TYPEREF_QNAME, "number");
        FactModelTuple factModelTuple = abstractKogitoDMNServiceSpy.getFactModelTuple(jsiITDefinitionsMock);
        assertTrue(factModelTuple.getVisibleFacts().isEmpty());
        assertTrue(factModelTuple.getHiddenFacts().isEmpty());
    }

    @Test
    public void getFactModelTupleSimpleInputData() {
        when(jsiITInputDataMock.getName()).thenReturn("inputDataName");
        drgElements.add(jsiITInputDataMock);
        when(jsiITDefinitionsMock.getDrgElement()).thenReturn(drgElements);
        attributesMapInput.put(TYPEREF_QNAME, "number");
        FactModelTuple factModelTuple = abstractKogitoDMNServiceSpy.getFactModelTuple(jsiITDefinitionsMock);
        assertTrue(factModelTuple.getVisibleFacts().size() == 1);
        FactModelTree inputDataNameFact = factModelTuple.getVisibleFacts().get("inputDataName");
        assertNotNull(inputDataNameFact);
        assertTrue(inputDataNameFact.getSimpleProperties().size() == 1);
        assertTrue(inputDataNameFact.getSimpleProperties().values().contains("number"));
    }

    @Test
    public void getFactModelTupleSimpleDecisionData() {
        when(jsiITInformationItemDecisionMock.getName()).thenReturn("inputDecisionName");
        drgElements.add(jsiITDecisionMock);
        when(jsiITDefinitionsMock.getDrgElement()).thenReturn(drgElements);
        attributesMapDecision.put(TYPEREF_QNAME, "string");
        FactModelTuple factModelTuple = abstractKogitoDMNServiceSpy.getFactModelTuple(jsiITDefinitionsMock);
        assertTrue(factModelTuple.getVisibleFacts().size() == 1);
        FactModelTree decisionDataNameFact = factModelTuple.getVisibleFacts().get("inputDecisionName");
        assertNotNull(decisionDataNameFact);
        assertTrue(decisionDataNameFact.getSimpleProperties().size() == 1);
        assertTrue(decisionDataNameFact.getSimpleProperties().values().contains("string"));
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
        FactModelTuple factModelTuple = abstractKogitoDMNServiceSpy.getFactModelTuple(jsiITDefinitionsMock);
        assertTrue(factModelTuple.getVisibleFacts().size() == 2);
        FactModelTree inputDataNameFact = factModelTuple.getVisibleFacts().get("inputDataName");
        assertNotNull(inputDataNameFact);
        assertTrue(inputDataNameFact.getSimpleProperties().size() == 1);
        assertTrue(inputDataNameFact.getSimpleProperties().values().contains("number"));
        FactModelTree decisionDataNameFact = factModelTuple.getVisibleFacts().get("inputDecisionName");
        assertNotNull(decisionDataNameFact);
        assertTrue(decisionDataNameFact.getSimpleProperties().size() == 1);
        assertTrue(decisionDataNameFact.getSimpleProperties().values().contains("string"));
    }

    @Test
    public void getDMNTypeFromMaps() {
        Map<String, ClientDMNType> dmnTypesMap = abstractKogitoDMNServiceSpy.getDMNTypesMap(jstiItemDefinitions, NAMESPACE);
        attributesMapInput.put(TYPEREF_QNAME, "number");
        ClientDMNType clientDMNType = abstractKogitoDMNServiceSpy.getDMNTypeFromMaps(dmnTypesMap, attributesMapInput);
        assertNotNull(clientDMNType);
        assertTrue(BuiltInType.NUMBER.equals(clientDMNType.getFeelType()));
    }

    @Test
    public void getDMNTypesMapEmptyItemDefinitions() {
        Map<String, ClientDMNType> dmnTypesMap = abstractKogitoDMNServiceSpy.getDMNTypesMap(jstiItemDefinitions, NAMESPACE);
        // It must contains all elements defined in BuiltInType ENUM without ANY
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
        Map<String, ClientDMNType> dmnTypesMap = abstractKogitoDMNServiceSpy.getDMNTypesMap(jstiItemDefinitions, NAMESPACE);
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
    public void getItemDefinitionComparator() {
        JSITItemDefinition jSITItemDefinitionPrimitive = mock(JSITItemDefinition.class);
        JSITItemDefinition jSITItemDefinitionSubItem = mock(JSITItemDefinition.class);
        JSITItemDefinition jSITItemDefinitionSubSubItem = mock(JSITItemDefinition.class);

        when(jSITItemDefinitionPrimitive.getName()).thenReturn("number");
        when(jSITItemDefinitionSubItem.getName()).thenReturn("tSub");
        when(jSITItemDefinitionSubItem.getTypeRef()).thenReturn("number");
        when(jSITItemDefinitionSubSubItem.getName()).thenReturn("tSubSub");
        when(jSITItemDefinitionSubSubItem.getTypeRef()).thenReturn("tSub");

        List<JSITItemDefinition> itemDefinitions = new ArrayList<>();
        /* Test 1 */
        itemDefinitions.add(jSITItemDefinitionPrimitive);
        itemDefinitions.add(jSITItemDefinitionSubItem);
        itemDefinitions.add(jSITItemDefinitionSubSubItem);
        Collections.sort(itemDefinitions, abstractKogitoDMNServiceSpy.getItemDefinitionComparator());
        assertSame(jSITItemDefinitionPrimitive, itemDefinitions.get(0));
        assertSame(jSITItemDefinitionSubItem, itemDefinitions.get(1));
        assertSame(jSITItemDefinitionSubSubItem, itemDefinitions.get(2));
        /* Test 2 */
        itemDefinitions.clear();
        itemDefinitions.add(jSITItemDefinitionSubItem);
        itemDefinitions.add(jSITItemDefinitionPrimitive);
        itemDefinitions.add(jSITItemDefinitionSubSubItem);
        Collections.sort(itemDefinitions, abstractKogitoDMNServiceSpy.getItemDefinitionComparator());
        assertSame(jSITItemDefinitionPrimitive, itemDefinitions.get(0));
        assertSame(jSITItemDefinitionSubItem, itemDefinitions.get(1));
        assertSame(jSITItemDefinitionSubSubItem, itemDefinitions.get(2));
        /* Test 3 */
        itemDefinitions.clear();
        itemDefinitions.add(jSITItemDefinitionSubItem);
        itemDefinitions.add(jSITItemDefinitionSubSubItem);
        itemDefinitions.add(jSITItemDefinitionPrimitive);
        Collections.sort(itemDefinitions, abstractKogitoDMNServiceSpy.getItemDefinitionComparator());
        assertSame(jSITItemDefinitionPrimitive, itemDefinitions.get(0));
        assertSame(jSITItemDefinitionSubItem, itemDefinitions.get(1));
        assertSame(jSITItemDefinitionSubSubItem, itemDefinitions.get(2));
        /* Test 4 */
        itemDefinitions.clear();
        itemDefinitions.add(jSITItemDefinitionPrimitive);
        itemDefinitions.add(jSITItemDefinitionSubSubItem);
        itemDefinitions.add(jSITItemDefinitionSubItem);
        Collections.sort(itemDefinitions, abstractKogitoDMNServiceSpy.getItemDefinitionComparator());
        assertSame(jSITItemDefinitionPrimitive, itemDefinitions.get(0));
        assertSame(jSITItemDefinitionSubItem, itemDefinitions.get(1));
        assertSame(jSITItemDefinitionSubSubItem, itemDefinitions.get(2));
        /* Test 5 */
        itemDefinitions.clear();
        itemDefinitions.add(jSITItemDefinitionSubSubItem);
        itemDefinitions.add(jSITItemDefinitionPrimitive);
        itemDefinitions.add(jSITItemDefinitionSubItem);
        Collections.sort(itemDefinitions, abstractKogitoDMNServiceSpy.getItemDefinitionComparator());
        assertSame(jSITItemDefinitionPrimitive, itemDefinitions.get(0));
        assertSame(jSITItemDefinitionSubItem, itemDefinitions.get(1));
        assertSame(jSITItemDefinitionSubSubItem, itemDefinitions.get(2));
        /* Test 6 */
        itemDefinitions.clear();
        itemDefinitions.add(jSITItemDefinitionSubSubItem);
        itemDefinitions.add(jSITItemDefinitionSubItem);
        itemDefinitions.add(jSITItemDefinitionPrimitive);
        Collections.sort(itemDefinitions, abstractKogitoDMNServiceSpy.getItemDefinitionComparator());
        assertSame(jSITItemDefinitionPrimitive, itemDefinitions.get(0));
        assertSame(jSITItemDefinitionSubItem, itemDefinitions.get(1));
        assertSame(jSITItemDefinitionSubSubItem, itemDefinitions.get(2));
    }

    @Test
    public void getDMNTypeNullItems() {
        ClientDMNType clientDmnType = abstractKogitoDMNServiceSpy.getDMNType(jsitItemDefinitionMock,
                                                                             NAMESPACE,
                                                                             abstractKogitoDMNServiceSpy.getDMNTypesMap(jstiItemDefinitions, NAMESPACE));
        assertEquals(NAMESPACE, clientDmnType.getNamespace());
        assertEquals(TYPE_NAME, clientDmnType.getName());
        assertFalse(clientDmnType.isCollection());
        assertNull(clientDmnType.getFeelType());
    }

    @Test
    public void getDMNTypeEmptyItems() {
        when(jsitItemDefinitionMock.getItemComponent()).thenReturn(new ArrayList<>());
        ClientDMNType clientDmnType = abstractKogitoDMNServiceSpy.getDMNType(jsitItemDefinitionMock,
                                                                             NAMESPACE,
                                                                             abstractKogitoDMNServiceSpy.getDMNTypesMap(jstiItemDefinitions, NAMESPACE));
        assertEquals(NAMESPACE, clientDmnType.getNamespace());
        assertEquals(TYPE_NAME, clientDmnType.getName());
        assertFalse(clientDmnType.isCollection());
        assertFalse(clientDmnType.isComposite());
        assertTrue(clientDmnType.getFields().isEmpty());
        assertNull(clientDmnType.getFeelType());
    }

    @Test
    public void getDMNTypeItems() {
        when(jsitItemDefinitionMock.getItemComponent()).thenReturn(Arrays.asList(jsitItemDefinitionNestedMock));
        ClientDMNType clientDmnType = abstractKogitoDMNServiceSpy.getDMNType(jsitItemDefinitionMock,
                                                                             NAMESPACE,
                                                                             new HashMap<>());
        assertEquals(NAMESPACE, clientDmnType.getNamespace());
        assertEquals(TYPE_NAME, clientDmnType.getName());
        assertFalse(clientDmnType.isCollection());
        assertTrue(clientDmnType.isComposite());
        assertNotNull(clientDmnType.getFields());
        assertTrue(clientDmnType.getFields().size() == 1);
        assertNull(clientDmnType.getFeelType());
    }

    @Test
    public void getDMNTypeItemsIsCollection() {
        when(jsitItemDefinitionMock.getIsCollection()).thenReturn(true);
        when(jsitItemDefinitionMock.getItemComponent()).thenReturn(Arrays.asList(jsitItemDefinitionNestedMock));
        ClientDMNType clientDmnType = abstractKogitoDMNServiceSpy.getDMNType(jsitItemDefinitionMock,
                                                                             NAMESPACE,
                                                                             new HashMap<>());
        assertEquals(NAMESPACE, clientDmnType.getNamespace());
        assertEquals(TYPE_NAME, clientDmnType.getName());
        assertTrue(clientDmnType.isCollection());
        assertTrue(clientDmnType.isComposite());
        assertNotNull(clientDmnType.getFields());
        assertTrue(clientDmnType.getFields().size() == 1);
        assertNull(clientDmnType.getFeelType());
    }

    @Test
    public void getDMNTypeItemsInheritedFields() {
        JSITItemDefinition jSITItemDefinitionSubItem = mock(JSITItemDefinition.class);
        JSITItemDefinition jSITItemDefinitionSubSubItem = mock(JSITItemDefinition.class);
        JSITItemDefinition jSITItemDefinitionNestedSubItem = mock(JSITItemDefinition.class);
        JSITItemDefinition jSITItemDefinitionNestedSubSubItem = mock(JSITItemDefinition.class);

        when(jSITItemDefinitionSubItem.getName()).thenReturn("tSub");
        when(jSITItemDefinitionSubItem.getTypeRef()).thenReturn("number");
        when(jSITItemDefinitionSubSubItem.getName()).thenReturn("tSubSub");
        when(jSITItemDefinitionSubSubItem.getTypeRef()).thenReturn("tSub");

        when(jSITItemDefinitionNestedSubItem.getName()).thenReturn("subField");
        when(jSITItemDefinitionNestedSubSubItem.getName()).thenReturn("subSubField");

        Map<String, ClientDMNType> dmnTypes = abstractKogitoDMNServiceSpy.getDMNTypesMap(jstiItemDefinitions, NAMESPACE);

        when(jSITItemDefinitionSubItem.getItemComponent()).thenReturn(Arrays.asList(jSITItemDefinitionNestedSubItem));
        ClientDMNType clientDmnTypeSub = abstractKogitoDMNServiceSpy.getDMNType(jSITItemDefinitionSubItem,
                                                                                NAMESPACE,
                                                                                dmnTypes);
        assertEquals(NAMESPACE, clientDmnTypeSub.getNamespace());
        assertEquals("tSub", clientDmnTypeSub.getName());
        assertFalse(clientDmnTypeSub.isCollection());
        assertTrue(clientDmnTypeSub.isComposite());
        assertNotNull(clientDmnTypeSub.getFields());
        assertTrue(clientDmnTypeSub.getFields().size() == 1);
        assertNull(clientDmnTypeSub.getFeelType());

        dmnTypes.put("tSub", clientDmnTypeSub);

        when(jSITItemDefinitionSubSubItem.getItemComponent()).thenReturn(Arrays.asList(jSITItemDefinitionNestedSubSubItem));
        ClientDMNType clientDmnTypeSubSub = abstractKogitoDMNServiceSpy.getDMNType(jSITItemDefinitionSubSubItem,
                                                                                   NAMESPACE,
                                                                                   dmnTypes);
        assertEquals(NAMESPACE, clientDmnTypeSubSub.getNamespace());
        assertEquals("tSubSub", clientDmnTypeSubSub.getName());
        assertFalse(clientDmnTypeSubSub.isCollection());
        assertTrue(clientDmnTypeSubSub.isComposite());
        assertNotNull(clientDmnTypeSubSub.getFields());
        assertTrue(clientDmnTypeSubSub.getFields().size() == 2);
        assertNull(clientDmnTypeSubSub.getFeelType());
        assertTrue(clientDmnTypeSubSub.getFields().keySet().containsAll(clientDmnTypeSub.getFields().keySet()));
    }

    @Test
    public void getDMNTypeItemsInheritedOnly() {
        JSITItemDefinition jSITItemDefinitionSubItem = mock(JSITItemDefinition.class);
        JSITItemDefinition jSITItemDefinitionSubSubItem = mock(JSITItemDefinition.class);
        JSITItemDefinition jSITItemDefinitionNestedSubItem = mock(JSITItemDefinition.class);

        when(jSITItemDefinitionSubItem.getName()).thenReturn("tSub");
        when(jSITItemDefinitionSubItem.getTypeRef()).thenReturn("number");
        when(jSITItemDefinitionSubSubItem.getName()).thenReturn("tSubSub");
        when(jSITItemDefinitionSubSubItem.getTypeRef()).thenReturn("tSub");

        when(jSITItemDefinitionNestedSubItem.getName()).thenReturn("subField");

        Map<String, ClientDMNType> dmnTypes = abstractKogitoDMNServiceSpy.getDMNTypesMap(jstiItemDefinitions, NAMESPACE);

        when(jSITItemDefinitionSubItem.getItemComponent()).thenReturn(Arrays.asList(jSITItemDefinitionNestedSubItem));
        ClientDMNType clientDmnTypeSub = abstractKogitoDMNServiceSpy.getDMNType(jSITItemDefinitionSubItem,
                                                                                NAMESPACE,
                                                                                dmnTypes);
        assertEquals(NAMESPACE, clientDmnTypeSub.getNamespace());
        assertEquals("tSub", clientDmnTypeSub.getName());
        assertFalse(clientDmnTypeSub.isCollection());
        assertTrue(clientDmnTypeSub.isComposite());
        assertNotNull(clientDmnTypeSub.getFields());
        assertTrue(clientDmnTypeSub.getFields().size() == 1);
        assertNull(clientDmnTypeSub.getFeelType());

        dmnTypes.put("tSub", clientDmnTypeSub);

        ClientDMNType clientDmnTypeSubSub = abstractKogitoDMNServiceSpy.getDMNType(jSITItemDefinitionSubSubItem,
                                                                                   NAMESPACE,
                                                                                   dmnTypes);
        assertEquals(NAMESPACE, clientDmnTypeSubSub.getNamespace());
        assertEquals("tSubSub", clientDmnTypeSubSub.getName());
        assertFalse(clientDmnTypeSubSub.isCollection());
        assertTrue(clientDmnTypeSubSub.isComposite());
        assertNotNull(clientDmnTypeSubSub.getFields());
        assertTrue(clientDmnTypeSubSub.getFields().size() == 1);
        assertNull(clientDmnTypeSubSub.getFeelType());
        assertEquals(clientDmnTypeSub.getFields().keySet(), clientDmnTypeSubSub.getFields().keySet());
    }

    @Test
    public void getDMNTypeItemsInheritedCollections() {
        JSITItemDefinition jSITItemDefinitionSubItem = mock(JSITItemDefinition.class);
        JSITItemDefinition jSITItemDefinitionSubSubItem = mock(JSITItemDefinition.class);
        JSITItemDefinition jSITItemDefinitionNestedSubItem = mock(JSITItemDefinition.class);
        JSITItemDefinition jSITItemDefinitionNestedSubSubItem = mock(JSITItemDefinition.class);

        when(jSITItemDefinitionSubItem.getName()).thenReturn("tSub");
        when(jSITItemDefinitionSubItem.getTypeRef()).thenReturn("number");
        when(jSITItemDefinitionSubItem.getIsCollection()).thenReturn(true);
        when(jSITItemDefinitionSubSubItem.getName()).thenReturn("tSubSub");
        when(jSITItemDefinitionSubSubItem.getTypeRef()).thenReturn("tSub");
        when(jSITItemDefinitionSubSubItem.getIsCollection()).thenReturn(false);

        when(jSITItemDefinitionNestedSubItem.getName()).thenReturn("subField");
        when(jSITItemDefinitionNestedSubSubItem.getName()).thenReturn("subSubField");

        Map<String, ClientDMNType> dmnTypes = abstractKogitoDMNServiceSpy.getDMNTypesMap(jstiItemDefinitions, NAMESPACE);

        when(jSITItemDefinitionSubItem.getItemComponent()).thenReturn(Arrays.asList(jSITItemDefinitionNestedSubItem));
        ClientDMNType clientDmnTypeSub = abstractKogitoDMNServiceSpy.getDMNType(jSITItemDefinitionSubItem,
                                                                                NAMESPACE,
                                                                                dmnTypes);
        assertEquals(NAMESPACE, clientDmnTypeSub.getNamespace());
        assertEquals("tSub", clientDmnTypeSub.getName());
        assertTrue(clientDmnTypeSub.isCollection());
        assertTrue(clientDmnTypeSub.isComposite());
        assertNotNull(clientDmnTypeSub.getFields());
        assertTrue(clientDmnTypeSub.getFields().size() == 1);
        assertNull(clientDmnTypeSub.getFeelType());

        dmnTypes.put("tSub", clientDmnTypeSub);

        when(jSITItemDefinitionSubSubItem.getItemComponent()).thenReturn(Arrays.asList(jSITItemDefinitionNestedSubSubItem));
        ClientDMNType clientDmnTypeSubSub = abstractKogitoDMNServiceSpy.getDMNType(jSITItemDefinitionSubSubItem,
                                                                                   NAMESPACE,
                                                                                   dmnTypes);
        assertEquals(NAMESPACE, clientDmnTypeSubSub.getNamespace());
        assertEquals("tSubSub", clientDmnTypeSubSub.getName());
        assertTrue(clientDmnTypeSubSub.isCollection()); // It must inherit the value from its "super item"
        assertTrue(clientDmnTypeSubSub.isComposite());
        assertNotNull(clientDmnTypeSubSub.getFields());
        assertTrue(clientDmnTypeSubSub.getFields().size() == 2);
        assertNull(clientDmnTypeSubSub.getFeelType());
    }

    @Test(expected = IllegalStateException.class)
    public void getDMNTypeItemsWrongSort() {
        JSITItemDefinition jSITItemDefinitionSubItem = mock(JSITItemDefinition.class);
        JSITItemDefinition jSITItemDefinitionSubSubItem = mock(JSITItemDefinition.class);
        JSITItemDefinition jSITItemDefinitionNestedSubItem = mock(JSITItemDefinition.class);
        JSITItemDefinition jSITItemDefinitionNestedSubSubItem = mock(JSITItemDefinition.class);

        when(jSITItemDefinitionSubItem.getName()).thenReturn("tSub");
        when(jSITItemDefinitionSubItem.getTypeRef()).thenReturn("number");
        when(jSITItemDefinitionSubSubItem.getName()).thenReturn("tSubSub");
        when(jSITItemDefinitionSubSubItem.getTypeRef()).thenReturn("tSub");

        when(jSITItemDefinitionNestedSubItem.getName()).thenReturn("subField");
        when(jSITItemDefinitionNestedSubSubItem.getName()).thenReturn("subSubField");

        Map<String, ClientDMNType> dmnTypes = abstractKogitoDMNServiceSpy.getDMNTypesMap(jstiItemDefinitions, NAMESPACE);

        when(jSITItemDefinitionSubSubItem.getItemComponent()).thenReturn(Arrays.asList(jSITItemDefinitionNestedSubSubItem));
        ClientDMNType clientDmnTypeSubSub = abstractKogitoDMNServiceSpy.getDMNType(jSITItemDefinitionSubSubItem,
                                                                                   NAMESPACE,
                                                                                   dmnTypes);
        assertEquals(NAMESPACE, clientDmnTypeSubSub.getNamespace());
        assertEquals("tSubSub", clientDmnTypeSubSub.getName());
        assertFalse(clientDmnTypeSubSub.isCollection());
        assertTrue(clientDmnTypeSubSub.isComposite());
        assertNotNull(clientDmnTypeSubSub.getFields());
        assertTrue(clientDmnTypeSubSub.getFields().size() == 2);
        assertNull(clientDmnTypeSubSub.getFeelType());

        dmnTypes.put("tSubSub", clientDmnTypeSubSub);

        when(jSITItemDefinitionSubItem.getItemComponent()).thenReturn(Arrays.asList(jSITItemDefinitionNestedSubItem));
        ClientDMNType clientDmnTypeSub = abstractKogitoDMNServiceSpy.getDMNType(jSITItemDefinitionSubItem,
                                                                                NAMESPACE,
                                                                                dmnTypes);
        assertEquals(NAMESPACE, clientDmnTypeSub.getNamespace());
        assertEquals("tSub", clientDmnTypeSub.getName());
        assertFalse(clientDmnTypeSub.isCollection());
        assertTrue(clientDmnTypeSub.isComposite());
        assertNotNull(clientDmnTypeSub.getFields());
        assertTrue(clientDmnTypeSub.getFields().size() == 1);
        assertNull(clientDmnTypeSub.getFeelType());
    }

    @Test
    public void createTopLevelFactModelTreeSimpleNoCollection() {
        // Single property retrieve
        ClientDMNType simpleString = getSimpleNoCollection();
        FactModelTree retrieved = abstractKogitoDMNServiceSpy.createTopLevelFactModelTree("testPath", simpleString, new TreeMap<>(), FactModelTree.Type.INPUT);
        Assert.assertNotNull(retrieved);
        assertEquals("testPath", retrieved.getFactName());
        assertEquals(1, retrieved.getSimpleProperties().size());
        assertTrue(retrieved.getSimpleProperties().containsKey(VALUE));
        assertEquals(simpleString.getName(), retrieved.getSimpleProperties().get(VALUE));
        assertTrue(retrieved.getExpandableProperties().isEmpty());
        assertTrue(retrieved.getGenericTypesMap().isEmpty());
    }

    @Test
    public void createTopLevelFactModelTreeSimpleCollection() {
        // Single property collection retrieve
        ClientDMNType simpleCollectionString = getSimpleCollection();
        TreeMap<String, FactModelTree> hiddenFactSimpleCollection = new TreeMap<>();
        FactModelTree retrieved = abstractKogitoDMNServiceSpy.createTopLevelFactModelTree("testPath", simpleCollectionString, hiddenFactSimpleCollection, FactModelTree.Type.INPUT);
        Assert.assertNotNull(retrieved);
        assertEquals("testPath", retrieved.getFactName());
        assertEquals(1, retrieved.getSimpleProperties().size());
        assertTrue(retrieved.getSimpleProperties().containsKey(VALUE));
        assertEquals("java.util.List", retrieved.getSimpleProperties().get(VALUE));
        assertTrue(retrieved.getExpandableProperties().isEmpty());
        assertEquals(1, retrieved.getGenericTypesMap().size());
        assertTrue(retrieved.getGenericTypesMap().containsKey(VALUE));
        Assert.assertNotNull(retrieved.getGenericTypesMap().get(VALUE));
        assertEquals(1, retrieved.getGenericTypesMap().get(VALUE).size());
        assertEquals(simpleCollectionString.getName(), retrieved.getGenericTypesMap().get(VALUE).get(0));
    }

    @Test
    public void createTopLevelFactModelTreeCompositeNoCollection() {
        // Single property retrieve
        ClientDMNType compositePerson = getSingleCompositeWithSimpleCollection();
        FactModelTree retrieved = abstractKogitoDMNServiceSpy.createTopLevelFactModelTree("testPath", compositePerson, new TreeMap<>(), FactModelTree.Type.INPUT);
        Assert.assertNotNull(retrieved);
        assertEquals("testPath", retrieved.getFactName());
        assertEquals(2, retrieved.getSimpleProperties().size());
        assertTrue(retrieved.getSimpleProperties().containsKey("friends"));
        assertEquals("java.util.List", retrieved.getSimpleProperties().get("friends"));
        assertTrue(retrieved.getSimpleProperties().containsKey(TYPE_NAME));
        assertEquals(TYPE_NAME, retrieved.getSimpleProperties().get(TYPE_NAME));
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
        FactModelTree retrieved = abstractKogitoDMNServiceSpy.createTopLevelFactModelTree("testPath", compositePerson, hiddenFactSimpleCollection, FactModelTree.Type.INPUT);
        Assert.assertNotNull(retrieved);
        assertEquals("testPath", retrieved.getFactName());
        assertEquals(1, retrieved.getSimpleProperties().size());
        assertTrue(retrieved.getSimpleProperties().containsKey(VALUE));
        assertEquals("java.util.List", retrieved.getSimpleProperties().get(VALUE));
        assertTrue(retrieved.getExpandableProperties().isEmpty());
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
}


