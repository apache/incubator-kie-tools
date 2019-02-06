/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.graph.util;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.commons.util.Sets;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertySetAdapter;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphUtilsTest {

    private static final String PROPERTY = "property";

    private static final String PROPERTY_ID = "property.id";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_ATTRIBUTE1 = "attribute1";
    private static final String FIELD_ATTRIBUTE2 = "attribute2";
    private static final String FIELD_WITH_NAMESPACE = "attribute1.attribute2.name";

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private AdapterManager adapterManager;

    @Mock
    private DefinitionAdapter definitionAdapter;

    @Mock
    private PropertyAdapter propertyAdapter;

    @Mock
    private Element<? extends Definition> element;

    @Mock
    private Object definition;

    @Mock
    private Object property;

    @Mock
    private PropertySetAdapter<Object> propertySetAdapter;

    private TestingGraphMockHandler graphTestHandler;
    private TestingGraphInstanceBuilder.TestGraph4 graphInstance;

    @Mock
    private Object property1;

    @Mock
    private Object property2;

    @Mock
    private Definition content;

    @Before
    public void setup() {
        this.graphTestHandler = new TestingGraphMockHandler();
        graphInstance = TestingGraphInstanceBuilder.newGraph4(graphTestHandler);
    }

    @Test
    public void hasChildrenTest() {
        boolean hasChildren = GraphUtils.hasChildren(graphInstance.parentNode);
        assertTrue(hasChildren);
    }

    @Test
    public void notHasChildrenTest() {
        boolean hasChildren = GraphUtils.hasChildren(graphInstance.startNode);
        assertFalse(hasChildren);
    }

    @Test
    public void countChildrenTest() {
        Long countChildren = GraphUtils.countChildren(graphInstance.parentNode);
        assertEquals(Long.valueOf(4),
                     countChildren);
    }

    @Test
    public void checkBoundsExceededTest() {
        Bounds parentBounds = Bounds.create(50d, 50d, 200d, 200d);

        Bounds childBounds = Bounds.create(51d, 51d, 199d, 199d);
        assertTrue(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = Bounds.create(51d, 51d, 200d, 200d);
        assertTrue(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = Bounds.create(50d, 50d, 199d, 199d);
        assertTrue(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = Bounds.create(50d, 50d, 200d, 200d);
        assertTrue(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = Bounds.create(49d, 49d, 201d, 201d);
        assertFalse(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = Bounds.create(49d, 49d, 200d, 200d);
        assertFalse(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = Bounds.create(49d, 49d, 199d, 199d);
        assertFalse(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = Bounds.create(49d, 49d, 201d, 201d);
        assertFalse(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = Bounds.create(50d, 49d, 201d, 201d);
        assertFalse(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = Bounds.create(50d, 49d, 201d, 201d);
        assertFalse(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));
    }

    @Test
    public void isDockedNodeTest() {
        assertTrue(GraphUtils.isDockedNode(graphInstance.dockedNode));
        assertFalse(GraphUtils.isDockedNode(graphInstance.startNode));
        assertFalse(GraphUtils.isDockedNode(graphInstance.intermNode));
        assertFalse(GraphUtils.isDockedNode(graphInstance.endNode));
    }

    @Test
    public void getDockedNodesTest() {
        List<Node> dockedNodes = GraphUtils.getDockedNodes(graphInstance.intermNode);
        assertEquals(dockedNodes.size(), 1);
        assertEquals(dockedNodes.get(0), graphInstance.dockedNode);
    }

    @Test
    public void getChildNodesTest() {
        List<Node> dockedNodes = GraphUtils.getChildNodes(graphInstance.parentNode);
        assertEquals(dockedNodes.size(), 4);
        assertEquals(dockedNodes.get(0), graphInstance.startNode);
        assertEquals(dockedNodes.get(1), graphInstance.intermNode);
        assertEquals(dockedNodes.get(2), graphInstance.endNode);
        assertEquals(dockedNodes.get(3), graphInstance.dockedNode);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetPropertyForNullElement() {
        assertNull(GraphUtils.getProperty(definitionManager, (Element) null, PROPERTY_ID));
    }

    @Test
    public void testGetPropertyForNonNullElement() {
        setupDefinitionManager();

        assertEquals(PROPERTY, GraphUtils.getProperty(definitionManager, element, PROPERTY_ID));
    }

    @Test
    public void testGetPropertyForElementUsingField() {
        setupDefinitionManager();
        when(definitionAdapter.getProperty(definition, FIELD_NAME)).thenReturn(Optional.of(property));

        assertEquals(property, GraphUtils.getProperty(definitionManager, element, FIELD_NAME));
    }

    @Test
    public void testGetPropertyByFieldHasProperty() {
        setupDefinitionManager();
        when(definitionAdapter.getProperty(definition, FIELD_NAME)).thenReturn(Optional.of(property));

        final Object field = GraphUtils.getPropertyByField(definitionManager, definition, FIELD_NAME);
        assertEquals(field, property);

        verify(propertySetAdapter, never()).getProperty(any(), anyString());
    }

    @Test
    public void testGetPropertyByFieldHasPropertySet() {
        setupDefinitionManager();
        when(definitionAdapter.getProperty(definition, FIELD_NAME)).thenReturn(Optional.empty());
        when(adapterManager.forPropertySet()).thenReturn(propertySetAdapter);
        when(propertySetAdapter.getProperty(definition, FIELD_NAME)).thenAnswer((i) -> Optional.of(property));

        final Object field = GraphUtils.getPropertyByField(definitionManager, definition, FIELD_NAME);
        assertEquals(field, property);

        verify(definitionAdapter).getProperty(definition, FIELD_NAME);
        verify(propertySetAdapter).getProperty(definition, FIELD_NAME);
    }

    @Test
    public void testGetPropertyByFieldHasNoProperty() {
        setupDefinitionManager();
        when(definitionAdapter.getProperty(definition, FIELD_NAME)).thenReturn(Optional.empty());
        when(adapterManager.forPropertySet()).thenReturn(propertySetAdapter);
        when(propertySetAdapter.getProperty(definition, FIELD_NAME)).thenAnswer((i) -> Optional.empty());

        final Object field = GraphUtils.getPropertyByField(definitionManager, definition, FIELD_NAME);
        assertNull(field);

        verify(definitionAdapter).getProperty(definition, FIELD_NAME);
        verify(propertySetAdapter).getProperty(definition, FIELD_NAME);
    }

    @Test
    public void testGetPropertyByFieldWithNameSpace() {
        setupDefinitionManager();
        when(definitionAdapter.getProperty(definition, FIELD_ATTRIBUTE1)).thenReturn(Optional.of(property1));
        when(definitionAdapter.getProperty(property1, FIELD_ATTRIBUTE2)).thenReturn(Optional.of(property2));
        when(definitionAdapter.getProperty(property2, FIELD_NAME)).thenReturn(Optional.of(property));

        final Object field = GraphUtils.getPropertyByField(definitionManager, definition, FIELD_WITH_NAMESPACE);
        assertEquals(field, property);

        verify(definitionAdapter).getProperty(definition, FIELD_ATTRIBUTE1);
        verify(definitionAdapter).getProperty(property1, FIELD_ATTRIBUTE2);
        verify(definitionAdapter).getProperty(property2, FIELD_NAME);
        verify(propertySetAdapter, never()).getProperty(any(), anyString());
    }

    @SuppressWarnings("unchecked")
    private void setupDefinitionManager() {
        when(element.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(definition);
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(definitionAdapter.getProperties(eq(content))).thenReturn(new Sets.Builder<String>().add(PROPERTY).build());
        when(definitionAdapter.getProperties(any(DomainObject.class))).thenReturn(new Sets.Builder<String>().add(PROPERTY).build());
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
        when(propertyAdapter.getId(PROPERTY)).thenReturn(PROPERTY_ID);
    }

    @Test
    public void testGetChildIndex() {
        final OptionalInt index1 = GraphUtils.getChildIndex(graphInstance.parentNode, TestingGraphInstanceBuilder.START_NODE_UUID);
        assertTrue(index1.isPresent());
        assertEquals(0, index1.getAsInt());

        final OptionalInt index2 = GraphUtils.getChildIndex(graphInstance.parentNode, TestingGraphInstanceBuilder.INTERM_NODE_UUID);
        assertTrue(index2.isPresent());
        assertEquals(1, index2.getAsInt());

        final OptionalInt index3 = GraphUtils.getChildIndex(graphInstance.parentNode, TestingGraphInstanceBuilder.END_NODE_UUID);
        assertTrue(index3.isPresent());
        assertEquals(2, index3.getAsInt());

        final OptionalInt index4 = GraphUtils.getChildIndex(graphInstance.parentNode, TestingGraphInstanceBuilder.DOCKED_NODE_UUID);
        assertTrue(index4.isPresent());
        assertEquals(3, index4.getAsInt());

        final OptionalInt index5 = GraphUtils.getChildIndex(graphInstance.parentNode, "node_not_exist");
        assertFalse(index5.isPresent());
    }
}
