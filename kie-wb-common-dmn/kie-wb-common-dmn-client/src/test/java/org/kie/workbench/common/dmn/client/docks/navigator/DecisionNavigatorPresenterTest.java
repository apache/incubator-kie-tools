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
import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.appformer.client.context.Channel;
import org.appformer.client.context.EditorContextProvider;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.docks.navigator.factories.DecisionNavigatorItemFactory;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponents;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreePresenter;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.Mock;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DecisionNavigatorPresenter_DecisionNavigator;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionNavigatorPresenterTest {

    @Mock
    private DecisionNavigatorPresenter.View view;

    @Mock
    private DecisionNavigatorTreePresenter treePresenter;

    @Mock
    private DecisionComponents decisionComponents;

    @Mock
    private DecisionNavigatorObserver decisionNavigatorObserver;

    @Mock
    private DecisionNavigatorChildrenTraverse navigatorChildrenTraverse;

    @Mock
    private DecisionNavigatorItemFactory itemFactory;

    @Mock
    private TranslationService translationService;

    @Mock
    private EditorContextProvider context;

    private DecisionNavigatorPresenter presenter;

    @Before
    public void setup() {
        presenter = spy(new DecisionNavigatorPresenter(view,
                                                       treePresenter,
                                                       decisionComponents,
                                                       decisionNavigatorObserver,
                                                       navigatorChildrenTraverse,
                                                       itemFactory,
                                                       translationService,
                                                       context));
    }

    @Test
    public void testGetDiagram() {

        final CanvasHandler handler = mock(CanvasHandler.class);
        final Diagram expectedDiagram = mock(Diagram.class);

        doNothing().when(presenter).refreshTreeView();
        doReturn(expectedDiagram).when(handler).getDiagram();

        presenter.setHandler(handler);

        final Diagram actual = presenter.getDiagram();

        assertEquals(expectedDiagram, actual);
    }

    @Test
    public void testSetup() {

        presenter.setup();

        verify(presenter).initialize();
        verify(presenter).setupView();
    }

    @Test
    public void testInitialize() {

        presenter.initialize();

        verify(view).init(presenter);
        verify(decisionNavigatorObserver).init(presenter);
    }

    @Test
    public void testSetupViewWhenChannelIsVSCodeOrDefault() {
        testSetupViewWhenDecisionComponentsContainerIsVisible(Channel.VSCODE);
        testSetupViewWhenDecisionComponentsContainerIsVisible(Channel.DEFAULT);
    }

    @Test
    public void testSetupViewWhenChannelIsNotVSCodeOrDefault() {
        testSetupViewWhenDecisionComponentsContainerIsNotVisible(Channel.GITHUB);
        testSetupViewWhenDecisionComponentsContainerIsNotVisible(Channel.ONLINE);
        testSetupViewWhenDecisionComponentsContainerIsNotVisible(Channel.DESKTOP);
    }

    private void testSetupViewWhenDecisionComponentsContainerIsNotVisible(final Channel channel) {
        final DecisionNavigatorTreePresenter.View treeView = mock(DecisionNavigatorTreePresenter.View.class);
        final DecisionComponents.View decisionComponentsView = mock(DecisionComponents.View.class);
        when(context.getChannel()).thenReturn(channel);
        when(treePresenter.getView()).thenReturn(treeView);
        when(decisionComponents.getView()).thenReturn(decisionComponentsView);

        presenter.setupView();

        verify(view).setupMainTree(treeView);
        verify(view, never()).showDecisionComponentsContainer();
        verify(view, never()).setupDecisionComponents(decisionComponentsView);
        verify(view, atLeastOnce()).hideDecisionComponentsContainer();
    }

    private void testSetupViewWhenDecisionComponentsContainerIsVisible(final Channel channel) {
        final DecisionNavigatorTreePresenter.View treeView = mock(DecisionNavigatorTreePresenter.View.class);
        final DecisionComponents.View decisionComponentsView = mock(DecisionComponents.View.class);
        when(context.getChannel()).thenReturn(channel);
        when(treePresenter.getView()).thenReturn(treeView);
        when(decisionComponents.getView()).thenReturn(decisionComponentsView);

        presenter.setupView();

        verify(view).setupMainTree(treeView);
        verify(view, atLeastOnce()).showDecisionComponentsContainer();
        verify(view, atLeastOnce()).setupDecisionComponents(decisionComponentsView);
        verify(view, never()).hideDecisionComponentsContainer();
    }

    @Test
    public void testGetView() {
        assertEquals(view, presenter.getView());
    }

    @Test
    public void testGetTitle() {

        final String expectedTitle = "Decision Navigator";
        when(translationService.format(DecisionNavigatorPresenter_DecisionNavigator)).thenReturn(expectedTitle);

        final String actualTitle = presenter.getTitle();

        assertEquals(expectedTitle, actualTitle);
    }

    @Test
    public void testGetDefaultPosition() {

        final CompassPosition expected = CompassPosition.WEST;
        final Position actual = presenter.getDefaultPosition();

        assertEquals(expected, actual);
    }

    @Test
    public void testGetTreePresenter() {
        assertEquals(treePresenter, presenter.getTreePresenter());
    }

    @Test
    public void testGetHandler() {

        final CanvasHandler expectedCanvasHandler = mock(CanvasHandler.class);
        doNothing().when(presenter).refreshTreeView();
        presenter.setHandler(expectedCanvasHandler);

        final CanvasHandler actualCanvasHandler = presenter.getHandler();

        assertEquals(expectedCanvasHandler, actualCanvasHandler);
    }

    @Test
    public void testSetHandler() {

        final CanvasHandler expectedCanvasHandler = mock(CanvasHandler.class);
        doNothing().when(presenter).refreshTreeView();

        presenter.setHandler(expectedCanvasHandler);

        verify(presenter).refreshTreeView();
        verify(presenter).refreshComponentsView();
        assertEquals(expectedCanvasHandler, presenter.getHandler());
    }

    @Test
    public void testRefreshTreeView() {

        final ArrayList<DecisionNavigatorItem> items = new ArrayList<>();
        doReturn(items).when(presenter).getItems();

        presenter.refreshTreeView();

        verify(treePresenter).setupItems(items);
    }

    @Test
    public void testGetItems() {

        final CanvasHandler canvasHandler = mock(CanvasHandler.class);
        final Diagram diagram = mock(Diagram.class);
        final Graph graph = mock(Graph.class);
        final List<DecisionNavigatorItem> expectedItems = singletonList(mock(DecisionNavigatorItem.class));

        doNothing().when(presenter).refreshTreeView();
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
        when(navigatorChildrenTraverse.getItems(graph)).thenReturn(expectedItems);

        presenter.setHandler(canvasHandler);

        final List<DecisionNavigatorItem> actualItems = presenter.getItems();

        assertEquals(expectedItems, actualItems);
    }

    @Test
    public void testGetItemsWhenDiagramIsNull() {

        final CanvasHandler canvasHandler = mock(CanvasHandler.class);
        final List<DecisionNavigatorItem> expectedItems = emptyList();

        when(canvasHandler.getDiagram()).thenReturn(null);

        presenter.setHandler(canvasHandler);

        final List<DecisionNavigatorItem> actualItems = presenter.getItems();

        assertEquals(expectedItems, actualItems);
    }

    @Test
    public void testGetGraphWhenDiagramIsNull() {

        final CanvasHandler canvasHandler = mock(CanvasHandler.class);

        when(canvasHandler.getDiagram()).thenReturn(null);

        presenter.setHandler(canvasHandler);

        final Optional<Graph> actualGraph = presenter.getGraph();

        assertFalse(actualGraph.isPresent());
    }

    @Test
    public void testGetGraphWhenHandlerIsNull() {

        presenter.setHandler(null);

        final Optional<Graph> actualGraph = presenter.getGraph();

        assertFalse(actualGraph.isPresent());
    }

    @Test
    public void testGetGraphWhenGraphExists() {

        final CanvasHandler canvasHandler = mock(CanvasHandler.class);
        final Diagram diagram = mock(Diagram.class);
        final Graph expectedGraph = mock(Graph.class);

        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(expectedGraph);

        presenter.setHandler(canvasHandler);

        final Optional<Graph> actualGraph = presenter.getGraph();

        assertTrue(actualGraph.isPresent());
        assertEquals(expectedGraph, actualGraph.get());
    }

    @Test
    public void testAddOrUpdateElement() {

        final Element element = mock(Node.class);
        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);

        doReturn(item).when(presenter).makeItem(element);

        presenter.addOrUpdateElement(element);

        verify(treePresenter).addOrUpdateItem(item);
    }

    @Test
    public void testAddOrUpdateElementWhenElementIsNotNode() {

        final Element element = mock(Edge.class);
        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);

        presenter.addOrUpdateElement(element);

        verify(treePresenter, never()).addOrUpdateItem(item);
    }

    @Test
    public void testUpdateElement() {

        final Element element = mock(Node.class);
        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);

        doReturn(item).when(presenter).makeItem(element);

        presenter.updateElement(element);

        verify(treePresenter).updateItem(item);
    }

    @Test
    public void testUpdateElementWhenElementIsNotNode() {

        final Element element = mock(Edge.class);
        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);

        presenter.updateElement(element);

        verify(treePresenter, never()).addOrUpdateItem(item);
    }

    @Test
    public void testRemoveElement() {

        final Element element = mock(Node.class);
        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);

        doReturn(item).when(presenter).makeItem(element);

        presenter.removeElement(element);

        verify(treePresenter).remove(item);
    }

    @Test
    public void testRemoveElementWhenElementIsNotNode() {

        final Element element = mock(Edge.class);
        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);

        presenter.removeElement(element);

        verify(treePresenter, never()).addOrUpdateItem(item);
    }

    @Test
    public void testMakeItem() {

        final Element element = mock(Element.class);
        final Node node = mock(Node.class);

        when(element.asNode()).thenReturn(node);

        presenter.makeItem(element);

        verify(itemFactory).makeItem(node);
    }

    @Test
    public void testRemoveAllElements() {
        presenter.removeAllElements();

        verify(treePresenter).removeAllItems();
        verify(decisionComponents).removeAllItems();
    }

    @Test
    public void testClearSelections() {
        presenter.clearSelections();

        verify(treePresenter).deselectItem();
    }

    @Test
    public void testOnRefreshDecisionComponents() {
        presenter.onRefreshDecisionComponents(mock(RefreshDecisionComponents.class));

        verify(presenter).refreshComponentsView();
    }

    @Test
    public void testRefreshComponentsView() {

        final CanvasHandler canvasHandler = mock(CanvasHandler.class);
        final Diagram diagram = mock(Diagram.class);
        final Graph expectedGraph = mock(Graph.class);

        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(expectedGraph);

        doReturn(Optional.of(canvasHandler)).when(presenter).getOptionalHandler();

        presenter.refreshComponentsView();

        verify(decisionComponents).refresh(diagram);
    }
}
