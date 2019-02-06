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

package org.kie.workbench.common.stunner.client.widgets.explorer.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.DOMGlyphRenderers;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.AbstractChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TreeExplorerTest {

    private static final String SHAPE_SET_ID = "shape-set-id";

    @Mock
    TextPropertyProviderFactory textPropertyProviderFactory;

    @Mock
    TextPropertyProvider textPropertyProvider;

    @Mock
    EventSourceMock<CanvasSelectionEvent> elementSelectedEvent;

    @Mock
    DefinitionUtils definitionUtils;

    @Mock
    ShapeManager shapeManager;

    @Mock
    TreeExplorerView view;

    @Mock
    ManagedInstance<TreeExplorer.View> views;

    @Mock
    DOMGlyphRenderers domGlyphRenderers;

    @Mock
    Graph graph;

    @Mock
    Diagram diagram;

    @Mock
    Metadata metadata;

    @Mock
    CanvasHandler canvasHandler;

    @Mock
    Canvas canvas;

    @Mock
    DefinitionManager definitionManager;

    @Mock
    AdapterManager adapterManager;

    @Mock
    DefinitionAdapter definitionAdapter;

    @Mock
    ShapeSet shapeSet;

    @Mock
    ShapeFactory shapeFactory;

    @Mock
    Glyph glyph;

    @Mock
    IsElement isElement;

    private ChildrenTraverseProcessor childrenTraverseProcessor;

    private TreeExplorer testedTree;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(views.get()).thenReturn(view);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getDiagram().getGraph()).thenReturn(graph);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getShapeSetId()).thenReturn(SHAPE_SET_ID);
        when(textPropertyProviderFactory.getProvider(any(Element.class))).thenReturn(textPropertyProvider);
        when(definitionUtils.getDefinitionManager()).thenReturn(definitionManager);
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(definitionAdapter.getId(anyObject())).thenReturn(DefinitionId.build("defId"));
        when(shapeManager.getShapeSet(eq(SHAPE_SET_ID))).thenReturn(shapeSet);
        when(shapeSet.getShapeFactory()).thenReturn(shapeFactory);
        when(shapeFactory.getGlyph(eq("defId"))).thenReturn(glyph);
        when(domGlyphRenderers.render(eq(glyph),
                                      anyDouble(),
                                      anyDouble())).thenReturn(isElement);
        when(graph.nodes()).thenReturn(getMockNodes());

        this.childrenTraverseProcessor = spy(new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()));

        this.testedTree = createTestInstance();
    }

    @Test
    public void testClear() {
        testedTree.show(canvasHandler);
        testedTree.clear();
        verify(view,
               atLeastOnce()).clear();
    }

    @Test
    public void testDestroy() {
        testedTree.show(canvasHandler);
        testedTree.destroy();
        verify(view,
               times(1)).destroy();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShow() {
        testedTree.show(canvasHandler);
        verify(view,
               times(1)).init(eq(testedTree));
        verify(childrenTraverseProcessor,
               times(1)).traverse(eq(graph),
                                  any(AbstractChildrenTraverseCallback.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShowWithSingleNodeWithNoNameAndWithNoTitle() {
        when(graph.nodes()).thenReturn(getMockNodes("node1"));

        testedTree.show(canvasHandler);

        verify(childrenTraverseProcessor,
               times(1)).traverse(eq(graph),
                                  any(AbstractChildrenTraverseCallback.class));

        verify(view).addItem(eq("node1"),
                             eq(TreeExplorer.NO_NAME),
                             any(IsWidget.class),
                             anyBoolean(),
                             anyBoolean());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShowWithSingleNodeWithNameAndWithNoTitle() {
        when(graph.nodes()).thenReturn(getMockNodes("node1"));
        when(textPropertyProvider.supports(any(Element.class))).thenReturn(true);
        when(textPropertyProvider.getText(any(Element.class))).thenReturn("my-name");

        testedTree.show(canvasHandler);

        verify(childrenTraverseProcessor,
               times(1)).traverse(eq(graph),
                                  any(AbstractChildrenTraverseCallback.class));

        verify(view).addItem(eq("node1"),
                             eq("my-name"),
                             any(IsWidget.class),
                             anyBoolean(),
                             anyBoolean());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShowWithSingleNodeWithNoNameAndWithTitle() {
        when(graph.nodes()).thenReturn(getMockNodes("node1"));
        when(definitionAdapter.getTitle(anyObject())).thenReturn("my-title");

        testedTree.show(canvasHandler);

        verify(childrenTraverseProcessor,
               times(1)).traverse(eq(graph),
                                  any(AbstractChildrenTraverseCallback.class));

        verify(view).addItem(eq("node1"),
                             eq("my-title"),
                             any(IsWidget.class),
                             anyBoolean(),
                             anyBoolean());
    }

    @Test
    public void testNotFiringSelectionEventFromObservers() {
        testedTree = spy(createTestInstance());
        final CanvasClearEvent clearEvent = mock(CanvasClearEvent.class);
        when(clearEvent.getCanvas()).thenReturn(canvas);
        testedTree.show(canvasHandler);
        Command runnable = () -> {
            assertTrue(testedTree.isEventInProgress());
            testedTree.onSelect("someUUID");
            verify(elementSelectedEvent,
                   never()).fire(any(CanvasSelectionEvent.class));
        };
        testedTree.checkEventContext(clearEvent,
                                     runnable);
        assertFalse(testedTree.isEventInProgress());
    }

    @Test
    public void testOnElementUpdated_childHighlighted() {
        final Node parent = mock(Node.class);
        when(parent.getUUID()).thenReturn("PARENT_UUID");
        when(parent.getContent()).thenReturn(mock(View.class));

        final Node child = mock(Node.class);
        when(child.getUUID()).thenReturn("CHILD_UUID");
        when(child.getContent()).thenReturn(mock(View.class));

        final Edge edge = mock(Edge.class);
        when(edge.getSourceNode()).thenReturn(parent);
        when(edge.getTargetNode()).thenReturn(child);
        when(edge.getContent()).thenReturn(mock(Child.class));

        final List<Edge> edges = Collections.singletonList(edge);
        when(parent.getOutEdges()).thenReturn(edges);
        when(child.getInEdges()).thenReturn(edges);

        when(view.isItemChanged(anyString(), anyString(), anyString(), any(OptionalInt.class))).thenReturn(true);

        testedTree.show(canvasHandler);
        final CanvasElementUpdatedEvent event = new CanvasElementUpdatedEvent(canvasHandler, child);
        testedTree.onCanvasElementUpdatedEvent(event);

        verify(view, times(1)).setSelectedItem(eq("CHILD_UUID"));
    }

    private TreeExplorer createTestInstance() {
        return new TreeExplorer(childrenTraverseProcessor,
                                textPropertyProviderFactory,
                                elementSelectedEvent,
                                definitionUtils,
                                shapeManager,
                                domGlyphRenderers,
                                views) {

            @Override
            ElementWrapperWidget<?> wrapIconElement(final IsElement icon) {
                return mock(ElementWrapperWidget.class);
            }
        };
    }

    private Iterable<Node<Content, Edge>> getMockNodes(final String... uuids) {
        final List<Node<Content, Edge>> nodes = new ArrayList<>();
        for (String uuid : uuids) {
            final Node<Content, Edge> node = new NodeImpl<>(uuid);
            node.setContent(new Content());
            nodes.add(node);
        }
        return nodes;
    }

    private static class Content implements View<String> {

        @Override
        public String getDefinition() {
            return "definition";
        }

        @Override
        public void setDefinition(final String definition) {

        }

        @Override
        public Bounds getBounds() {
            return null;
        }

        @Override
        public void setBounds(final Bounds bounds) {

        }
    }
}
