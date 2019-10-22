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

package org.kie.workbench.common.dmn.client.docks.navigator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.docks.navigator.factories.DecisionNavigatorItemFactory;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionNavigatorChildrenTraverseTest {

    @Mock
    private ChildrenTraverseProcessor traverseProcessor;

    @Mock
    private DecisionNavigatorItemFactory itemFactory;

    @Mock
    private Node<View, Edge> node;

    @Mock
    private Node<View, Edge> parent;

    @Mock
    private List<Node<View, Edge>> nodes;

    private DecisionNavigatorChildrenTraverse traverse;

    @Before
    public void setup() {
        traverse = spy(new DecisionNavigatorChildrenTraverse(traverseProcessor, itemFactory));
    }

    @Test
    public void testGetItems() {

        final Graph graph = mock(Graph.class);
        final DecisionNavigatorChildrenTraverse.TraverseCallback traverseCallback =
                mock(DecisionNavigatorChildrenTraverse.TraverseCallback.class);
        final List<DecisionNavigatorItem> expectedItems = new ArrayList<>();

        doReturn(traverseCallback).when(traverse).makeTraverseCallback();
        when(traverseCallback.getItems()).thenReturn(expectedItems);

        final List<DecisionNavigatorItem> actualItems = traverse.getItems(graph);

        verify(traverseProcessor).traverse(graph, traverseCallback);
        assertEquals(expectedItems, actualItems);
    }

    @Test
    public void testTraverseCallbackStartNodeTraversalWithoutParents() {

        final DecisionNavigatorChildrenTraverse.TraverseCallback traverseCallback = spy(traverse.makeTraverseCallback());
        final DecisionNavigatorItem item = makeItem("item");
        final List<DecisionNavigatorItem> items = new ArrayList<>();

        doReturn(items).when(traverseCallback).getItems();
        when(itemFactory.makeRoot(node)).thenReturn(item);

        traverseCallback.startNodeTraversal(node);

        assertEquals(items, Collections.singletonList(item));
    }

    @Test
    public void testTraverseCallbackStartNodeTraversalWithParents() {

        final DecisionNavigatorChildrenTraverse.TraverseCallback traverseCallback = spy(traverse.makeTraverseCallback());
        final DecisionNavigatorItem item = makeItem("item");
        final DecisionNavigatorItem parentItem = makeItem("parent");

        when(nodes.get(0)).thenReturn(parent);
        when(itemFactory.makeItem(node)).thenReturn(item);

        doReturn(Optional.of(parentItem)).when(traverseCallback).findItem(parent);

        traverseCallback.startNodeTraversal(nodes, node);

        verify(parentItem).addChild(item);
    }

    @Test
    public void testFindItem() {

        final DecisionNavigatorChildrenTraverse.TraverseCallback traverseCallback = spy(traverse.makeTraverseCallback());
        final DecisionNavigatorItem item1 = makeItem("123");
        final DecisionNavigatorItem item2 = makeItem("ABC");
        final DecisionNavigatorItem item3 = makeItem("456");
        final List<DecisionNavigatorItem> items = Arrays.asList(item1, item2, item3);

        when(node.getUUID()).thenReturn("ABC");
        doReturn(items).when(traverseCallback).getItems();

        final Optional<DecisionNavigatorItem> actualItem = traverseCallback.findItem(node);
        final Optional<DecisionNavigatorItem> expectedItem = Optional.ofNullable(item2);

        assertEquals(expectedItem, actualItem);
    }

    private DecisionNavigatorItem makeItem(final String uuid) {
        return spy(new DecisionNavigatorItem(uuid));
    }
}
