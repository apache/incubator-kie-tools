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

package org.drools.workbench.screens.scenariosimulation.backend.server;

import java.util.SortedMap;
import java.util.TreeMap;

import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.backend.server.exceptions.WrongDMNTypeException;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class DMNTypeServiceImplTest extends AbstractDMNTest {

    private DMNTypeServiceImpl dmnTypeServiceImpl;

    @Before
    public void init() {
        super.init();
        dmnTypeServiceImpl = new DMNTypeServiceImpl() {
            @Override
            public DMNModel getDMNModel(Path path, String stringPath) {
                return dmnModelLocal;
            }
        };
    }

    @Test
    public void initializeNameAndNamespace() {
        Simulation simulation = new Simulation();
        dmnTypeServiceImpl.initializeNameAndNamespace(simulation, mock(Path.class), "");

        assertEquals(NAMESPACE, simulation.getSimulationDescriptor().getDmnNamespace());
        assertEquals(MODEL_NAME, simulation.getSimulationDescriptor().getDmnName());
    }


    @Test
    public void retrieveFactModelTupleDmnList() throws WrongDMNTypeException {
        setDmnModelLocal("dmn-list.dmn", "https://github.com/kiegroup/drools/kie-dmn/_CC8924B0-D729-4D70-9588-039B5824FFE9", "dmn-list");
        FactModelTuple factModelTuple = dmnTypeServiceImpl.retrieveFactModelTuple(mock(Path.class), null);
        // VisibleFacts should match inputs and decisions on given model
        int expectedVisibleFacts = dmnModelLocal.getInputs().size() + dmnModelLocal.getDecisions().size();
        assertEquals(expectedVisibleFacts, factModelTuple.getVisibleFacts().size());
        // Verify each inputDataNode has been correctly mapped
        dmnModelLocal.getInputs().forEach(inputDataNode -> verifyFactModelTree(factModelTuple, inputDataNode, factModelTuple.getHiddenFacts()));
        // Verify each decisionNode has been correctly mapped
        dmnModelLocal.getDecisions().forEach(decisionNode -> verifyFactModelTree(factModelTuple, decisionNode, factModelTuple.getHiddenFacts()));
    }

    @Test
    public void retrieveFactModelTupleDmnListComposite() throws WrongDMNTypeException {
        setDmnModelLocal("dmn-list-composite.dmn", "https://github.com/kiegroup/drools/kie-dmn/_25BF2679-3109-488F-9AD1-DDBCCEBBE5F1", "dmn-list-composite");
        FactModelTuple factModelTuple = dmnTypeServiceImpl.retrieveFactModelTuple(mock(Path.class), null);
        // VisibleFacts should match inputs and decisions on given model
        int expectedVisibleFacts = dmnModelLocal.getInputs().size() + dmnModelLocal.getDecisions().size();
        assertEquals(expectedVisibleFacts, factModelTuple.getVisibleFacts().size());
        // Verify each inputDataNode has been correctly mapped
        dmnModelLocal.getInputs().forEach(inputDataNode -> verifyFactModelTree(factModelTuple, inputDataNode, factModelTuple.getHiddenFacts()));
        // Verify each decisionNode has been correctly mapped
        dmnModelLocal.getDecisions().forEach(decisionNode -> verifyFactModelTree(factModelTuple, decisionNode, factModelTuple.getHiddenFacts()));
    }


    @Test
    public void createTopLevelFactModelTreeSimpleNoCollection() throws WrongDMNTypeException {
        // Single property retrieve
        DMNType simpleString = getSimpleNoCollection();
        FactModelTree retrieved = dmnTypeServiceImpl.createTopLevelFactModelTree("testPath", simpleString, new TreeMap<>(), FactModelTree.Type.INPUT);
        assertNotNull(retrieved);
        assertEquals("testPath", retrieved.getFactName());
        assertEquals(1, retrieved.getSimpleProperties().size());
        assertTrue(retrieved.getSimpleProperties().containsKey("value"));
        assertEquals(simpleString.getName(), retrieved.getSimpleProperties().get("value"));
        assertTrue(retrieved.getExpandableProperties().isEmpty());
        assertTrue(retrieved.getGenericTypesMap().isEmpty());
    }

    @Test
    public void createTopLevelFactModelTreeSimpleCollection() throws WrongDMNTypeException {
        // Single property collection retrieve
        DMNType simpleCollectionString = getSimpleCollection();
        TreeMap<String, FactModelTree> hiddenFactSimpleCollection = new TreeMap<>();
        FactModelTree retrieved = dmnTypeServiceImpl.createTopLevelFactModelTree("testPath", simpleCollectionString, hiddenFactSimpleCollection, FactModelTree.Type.INPUT);
        assertNotNull(retrieved);
        assertEquals("testPath", retrieved.getFactName());
        assertEquals(1, retrieved.getSimpleProperties().size());
        assertTrue(retrieved.getSimpleProperties().containsKey("value"));
        assertEquals("java.util.List", retrieved.getSimpleProperties().get("value"));
        assertTrue(retrieved.getExpandableProperties().isEmpty());
        assertEquals(1, retrieved.getGenericTypesMap().size());
        assertTrue(retrieved.getGenericTypesMap().containsKey("value"));
        assertNotNull(retrieved.getGenericTypesMap().get("value"));
        assertEquals(1, retrieved.getGenericTypesMap().get("value").size());
        assertEquals(simpleCollectionString.getName(), retrieved.getGenericTypesMap().get("value").get(0));
    }

    @Test
    public void createTopLevelFactModelTreeCompositeNoCollection() throws WrongDMNTypeException {
        // Single property retrieve
        DMNType compositePerson = getSingleCompositeWithSimpleCollection();
        FactModelTree retrieved = dmnTypeServiceImpl.createTopLevelFactModelTree("testPath", compositePerson, new TreeMap<>(), FactModelTree.Type.INPUT);
        assertNotNull(retrieved);
        assertEquals("testPath", retrieved.getFactName());
        assertEquals(2, retrieved.getSimpleProperties().size());
        assertTrue(retrieved.getSimpleProperties().containsKey("friends"));
        assertEquals("java.util.List", retrieved.getSimpleProperties().get("friends"));
        assertTrue(retrieved.getSimpleProperties().containsKey("name"));
        assertEquals(SIMPLE_TYPE_NAME, retrieved.getSimpleProperties().get("name"));
        //
        assertEquals(1, retrieved.getGenericTypesMap().size());
        assertTrue(retrieved.getGenericTypesMap().containsKey("friends"));
        assertEquals(compositePerson.getFields().get("friends").getName(), retrieved.getGenericTypesMap().get("friends").get(0));
        //
        assertEquals(2, retrieved.getExpandableProperties().size());
        assertTrue(retrieved.getExpandableProperties().containsKey(EXPANDABLE_PROPERTY_PHONENUMBERS));
        assertEquals("tPhoneNumber", retrieved.getExpandableProperties().get(EXPANDABLE_PROPERTY_PHONENUMBERS));
        assertTrue(retrieved.getExpandableProperties().containsKey(EXPANDABLE_PROPERTY_DETAILS));
        assertEquals("tDetails", retrieved.getExpandableProperties().get(EXPANDABLE_PROPERTY_DETAILS));
    }

    @Test
    public void createTopLevelFactModelTreeCompositeCollection() throws WrongDMNTypeException {
        // Single property collection retrieve
        DMNType compositePerson = getCompositeCollection();
        TreeMap<String, FactModelTree> hiddenFactSimpleCollection = new TreeMap<>();
        FactModelTree retrieved = dmnTypeServiceImpl.createTopLevelFactModelTree("testPath", compositePerson, hiddenFactSimpleCollection, FactModelTree.Type.INPUT);
        assertNotNull(retrieved);
        assertEquals("testPath", retrieved.getFactName());
        assertEquals(1, retrieved.getSimpleProperties().size());
        assertTrue(retrieved.getSimpleProperties().containsKey("value"));
        assertEquals("java.util.List", retrieved.getSimpleProperties().get("value"));
        assertTrue(retrieved.getExpandableProperties().isEmpty());
        assertEquals(1, retrieved.getGenericTypesMap().size());
        assertTrue(retrieved.getGenericTypesMap().containsKey("value"));
        assertNotNull(retrieved.getGenericTypesMap().get("value"));
        assertEquals(1, retrieved.getGenericTypesMap().get("value").size());
        assertEquals(compositePerson.getName(), retrieved.getGenericTypesMap().get("value").get(0));
    }

    @Test
    public void checkTypeSimpleTopLevelCollection() {
        // top level collection
        SimpleTypeImpl topLevelCollection = getSimpleCollection();
        DMNTypeServiceImpl.ErrorHolder errorHolder = new DMNTypeServiceImpl.ErrorHolder();
        dmnTypeServiceImpl.checkTypeSupport(topLevelCollection, false, errorHolder, "fieldName");
        assertEquals(0, errorHolder.getMultipleNestedObject().size());
        assertEquals(0, errorHolder.getMultipleNestedCollection().size());
    }

    @Test
    public void checkTypeSingleCompositeWithNestedCompositeCollection() {
        // nested collection
        CompositeTypeImpl singleCompositeWithComplexCollection = getSingleCompositeWithNestedCollection();
        DMNTypeServiceImpl.ErrorHolder errorHolder = new DMNTypeServiceImpl.ErrorHolder();
        dmnTypeServiceImpl.checkTypeSupport(singleCompositeWithComplexCollection, false, errorHolder, "fieldName");
        assertEquals(0, errorHolder.getMultipleNestedObject().size());
        assertEquals(1, errorHolder.getMultipleNestedCollection().size());
        assertTrue(errorHolder.getMultipleNestedCollection().contains("fieldName.phoneNumbers.numbers"));
    }

    @Test
    public void checkTypeSingleCompositeWithCollection() {
        // nested object into collection
        CompositeTypeImpl person = new CompositeTypeImpl();
        CompositeTypeImpl complexNumbers = new CompositeTypeImpl(null, "tPhoneNumber", null, false);
        CompositeTypeImpl phoneNumberCompositeCollection = new CompositeTypeImpl(null, "tPhoneNumber", null, true);
        phoneNumberCompositeCollection.addField("complexNumbers", complexNumbers);
        person.addField(EXPANDABLE_PROPERTY_PHONENUMBERS, phoneNumberCompositeCollection);
        DMNTypeServiceImpl.ErrorHolder errorHolder = new DMNTypeServiceImpl.ErrorHolder();
        dmnTypeServiceImpl.checkTypeSupport(person, false, errorHolder, "fieldName");
        assertEquals(1, errorHolder.getMultipleNestedObject().size());
        assertEquals(0, errorHolder.getMultipleNestedCollection().size());
        assertTrue(errorHolder.getMultipleNestedObject().contains("fieldName.phoneNumbers.complexNumbers"));
    }

    /**
     * Verify the <code>FactModelTree</code> generated for a <b>given</b> <code>DMNNode</code> (<code>InputDataNode</code> or <code>DecisionNode</code>)
     * @param factModelTuple
     * @param dmnNode
     * @param hiddenFacts
     */
    private void verifyFactModelTree(FactModelTuple factModelTuple, DMNNode dmnNode, SortedMap<String, FactModelTree> hiddenFacts) {
        // Check the FactModelTree has been mapped between visible facts
        assertTrue(factModelTuple.getVisibleFacts().containsKey(dmnNode.getName()));
        final FactModelTree mappedFactModelTree = factModelTuple.getVisibleFacts().get(dmnNode.getName());
        // Check the FactModelTree is not null
        assertNotNull(mappedFactModelTree);
        DMNType originalType;
        // Retrieving DMNType mapped by original DMNNode
        if (dmnNode instanceof InputDataNode) {
            originalType = ((InputDataNode) dmnNode).getType();
        } else if (dmnNode instanceof DecisionNode) {
            originalType = ((DecisionNode) dmnNode).getResultType();
        } else {
            fail("Unrecognized node type " + dmnNode.getName() + " -> " + dmnNode.getClass().getCanonicalName());
            return;
        }
        if (originalType.isCollection()) { // if original type is a collection
            verifyCollectionDMNType(mappedFactModelTree, originalType, hiddenFacts);
        } else { // Otherwise look inside for specific cases
            if (originalType.isComposite()) {
                verifyCompositeDMNType(mappedFactModelTree, originalType, hiddenFacts);
            } else {
                verifySimpleDMNType(mappedFactModelTree, originalType);
            }
        }
    }

    /**
     * Verify the <code>FactModelTree</code> generated for a <b>collection</b> <code>DMNType</code>
     * @param mappedFactModelTree
     * @param originalType
     * @param hiddenFacts
     */
    private void verifyCollectionDMNType(FactModelTree mappedFactModelTree, DMNType originalType, SortedMap<String, FactModelTree> hiddenFacts) {
        if (originalType.isComposite()) { // a composite collection is a collection of itself, the generic type is the DMNType itself
            if (!mappedFactModelTree.getGenericTypesMap().isEmpty()) {
                assertTrue(mappedFactModelTree.getGenericTypesMap().containsKey("value")); // with "value as key
                final String genericType = mappedFactModelTree.getGenericTypesMap().get("value").get(0); // since this is a list, it just have one generic
                assertTrue(hiddenFacts.containsKey(genericType));
                final FactModelTree genericFactModelTree = hiddenFacts.get(genericType);
                assertNotNull(genericFactModelTree);
                verifyCompositeDMNType(genericFactModelTree, originalType, hiddenFacts);
            } else {
                verifyCompositeDMNType(mappedFactModelTree, originalType, hiddenFacts);
            }
        } else { // otherwise we have to check if it is a "direct" collection or a referenced one
            verifySimpleDMNType(mappedFactModelTree, originalType);
        }
    }

    /**
     * Verify the <code>FactModelTree</code> generated for a <b>composite</b> (<b>not collection</b>) <code>DMNType</code>
     * @param mappedFactModelTree
     * @param originalType
     */
    private void verifyCompositeDMNType(FactModelTree mappedFactModelTree, DMNType originalType, SortedMap<String, FactModelTree> hiddenFacts) {
        originalType.getFields().forEach((key, value) -> {
            if (value.isCollection()) {
                assertTrue(hiddenFacts.containsKey(key));
                verifyCollectionDMNType(hiddenFacts.get(key), value, hiddenFacts);
            } else {
                if (value.isComposite()) { // If it is composite it should be an expandable property
                    assertTrue(mappedFactModelTree.getExpandableProperties().containsKey(key));
                    // Verify that referenced genericType is mapped and not null inside hiddenFacts
                    assertTrue(hiddenFacts.containsKey(value.getName()));
                    assertNotNull(hiddenFacts.get(value.getName()));
                } else {
                    assertTrue(mappedFactModelTree.getSimpleProperties().containsKey(key)); // otherwise a simple one
                }
            }
        });
    }

    /**
     * Verify the <code>FactModelTree</code> generated for a <b>simple</b> (<b>not collection</b>) <code>DMNType</code>
     * @param mappedFactModelTree
     * @param originalType
     */
    private void verifySimpleDMNType(FactModelTree mappedFactModelTree, DMNType originalType) {
        assertTrue(mappedFactModelTree.getSimpleProperties().containsKey("value")); // otherwise a simple one
        assertEquals(originalType.getName(), mappedFactModelTree.getSimpleProperties().get("value"));
    }
}