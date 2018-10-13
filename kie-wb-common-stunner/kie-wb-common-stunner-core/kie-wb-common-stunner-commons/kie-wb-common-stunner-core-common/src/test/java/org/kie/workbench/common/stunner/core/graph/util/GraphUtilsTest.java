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
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphUtilsTest {

    private static final String PROPERTY = "property";

    private static final String PROPERTY_ID = "property.id";

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
    private DomainObject domainObject;

    private TestingGraphMockHandler graphTestHandler;
    private TestingGraphInstanceBuilder.TestGraph4 graphInstance;

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
        Bounds parentBounds = new BoundsImpl(new BoundImpl(50d, 50d), new BoundImpl(200d, 200d));

        Bounds childBounds = new BoundsImpl(new BoundImpl(51d, 51d), new BoundImpl(199d, 199d));
        assertTrue(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = new BoundsImpl(new BoundImpl(51d, 51d), new BoundImpl(200d, 200d));
        assertTrue(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = new BoundsImpl(new BoundImpl(50d, 50d), new BoundImpl(199d, 199d));
        assertTrue(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = new BoundsImpl(new BoundImpl(50d, 50d), new BoundImpl(200d, 200d));
        assertTrue(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = new BoundsImpl(new BoundImpl(49d, 49d), new BoundImpl(201d, 201d));
        assertFalse(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = new BoundsImpl(new BoundImpl(49d, 49d), new BoundImpl(200d, 200d));
        assertFalse(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = new BoundsImpl(new BoundImpl(49d, 49d), new BoundImpl(199d, 199d));
        assertFalse(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = new BoundsImpl(new BoundImpl(49d, 49d), new BoundImpl(201d, 201d));
        assertFalse(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = new BoundsImpl(new BoundImpl(50d, 49d), new BoundImpl(201d, 201d));
        assertFalse(GraphUtils.checkBoundsExceeded(parentBounds, childBounds));

        childBounds = new BoundsImpl(new BoundImpl(51d, 49d), new BoundImpl(201d, 201d));
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
    @SuppressWarnings("unchecked")
    public void testGetPropertyForNullDomainObject() {
        assertNull(GraphUtils.getProperty(definitionManager, (DomainObject) null, PROPERTY_ID));
    }

    @Test
    public void testGetPropertyForNonNullDomainObject() {
        setupDefinitionManager();

        assertEquals(PROPERTY, GraphUtils.getProperty(definitionManager, domainObject, PROPERTY_ID));
    }

    @SuppressWarnings("unchecked")
    private void setupDefinitionManager() {
        final Definition<String> definition = mock(Definition.class);
        final String content = "content";
        when(element.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(content);

        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(definitionAdapter.getProperties(eq(content))).thenReturn(new Sets.Builder<String>().add(PROPERTY).build());
        when(definitionAdapter.getProperties(any(DomainObject.class))).thenReturn(new Sets.Builder<String>().add(PROPERTY).build());
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
        when(propertyAdapter.getId(PROPERTY)).thenReturn(PROPERTY_ID);
    }
}
