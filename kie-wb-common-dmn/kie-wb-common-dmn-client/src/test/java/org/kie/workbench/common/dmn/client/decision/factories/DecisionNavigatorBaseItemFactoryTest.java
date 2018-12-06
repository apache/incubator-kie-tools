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

package org.kie.workbench.common.dmn.client.decision.factories;

import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNDiagram;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorPresenter;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasFocusedShapeEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem.Type.ITEM;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DecisionNavigatorBaseItemFactory_NoName;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionNavigatorBaseItemFactoryTest {

    @Mock
    private DecisionNavigatorNestedItemFactory nestedItemFactory;

    @Mock
    private DecisionNavigatorPresenter decisionNavigatorPresenter;

    @Mock
    private TextPropertyProviderFactory textPropertyProviderFactory;

    @Mock
    private EventSourceMock<CanvasFocusedShapeEvent> canvasFocusedSelectionEvent;

    @Mock
    private EventSourceMock<CanvasSelectionEvent> canvasSelectionEvent;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private TranslationService translationService;

    @Mock
    private CanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Graph graph;

    @Mock
    private Node<View, Edge> node;

    @Mock
    private Element<View> element;

    @Mock
    private DefinitionAdapter<Object> objectDefinitionAdapter;

    private DecisionNavigatorBaseItemFactory factory;

    @Before
    public void setup() {
        factory = spy(new DecisionNavigatorBaseItemFactory(nestedItemFactory,
                                                           decisionNavigatorPresenter,
                                                           textPropertyProviderFactory,
                                                           canvasFocusedSelectionEvent,
                                                           canvasSelectionEvent,
                                                           definitionUtils,
                                                           translationService));

        when(decisionNavigatorPresenter.getHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMakeItem() {

        final String itemUUID = "itemUUID";
        final String childUUID = "childUUID";
        final String graphUUID = "graphUUID";
        final String label = "label";
        final Command onClick = mock(Command.class);
        final Node<Definition, Edge> diagramNode = mock(Node.class);
        final Definition diagramDefinition = mock(Definition.class);
        final DMNDiagram diagram = mock(DMNDiagram.class);
        final DecisionNavigatorItem child = new DecisionNavigatorItem(childUUID);
        final List<DecisionNavigatorItem> nestedItems = singletonList(child);

        when(node.getUUID()).thenReturn(itemUUID);
        doReturn(label).when(factory).getLabel(node);
        doReturn(onClick).when(factory).makeOnClickCommand(node);
        doReturn(nestedItems).when(factory).makeNestedItems(node);

        when(graph.nodes()).thenReturn(Collections.singletonList(diagramNode));
        when(diagramNode.getContent()).thenReturn(diagramDefinition);
        when(diagramDefinition.getDefinition()).thenReturn(diagram);
        when(diagramNode.getUUID()).thenReturn(graphUUID);

        final DecisionNavigatorItem item = factory.makeItem(node, ITEM);

        assertEquals(itemUUID, item.getUUID());
        assertEquals(label, item.getLabel());
        assertEquals(onClick, item.getOnClick());
        assertEquals(graphUUID, item.getParentUUID());
        assertEquals(asTreeSet(child), item.getChildren());
    }

    @Test
    public void testMakeOnClickCommand() {

        final CanvasHandler canvasHandler = mock(CanvasHandler.class);
        final Canvas canvas = mock(Canvas.class);
        final String uuid = "uuid";
        final CanvasSelectionEvent canvasSelection = new CanvasSelectionEvent(canvasHandler, uuid);
        final CanvasFocusedShapeEvent canvasFocusedShape = new CanvasFocusedShapeEvent(canvasHandler, uuid);

        when(node.getUUID()).thenReturn(uuid);
        when(decisionNavigatorPresenter.getHandler()).thenReturn(canvasHandler);
        doReturn(canvasSelection).when(factory).makeCanvasSelectionEvent(canvasHandler, uuid);
        doReturn(canvasFocusedShape).when(factory).makeCanvasFocusedShapeEvent(canvasHandler, uuid);
        doReturn(canvas).when(canvasHandler).getCanvas();

        factory.makeOnClickCommand(node).execute();

        verify(canvasSelectionEvent).fire(canvasSelection);
        verify(canvasFocusedSelectionEvent).fire(canvasFocusedShape);
        verify(canvas).focus();
    }

    @Test
    public void testGetLabelWheNameIsValidAndTitleIsValid() {

        final String title = "title";
        final String name = "name";

        doReturn(name).when(factory).getName(element);
        doReturn(title).when(factory).getTitle(element);

        final String label = factory.getLabel(element);

        assertEquals(label, name);
    }

    @Test
    public void testGetLabelWheNameIsBlankAndTitleIsValid() {

        final String title = "title";
        final String name = "";

        doReturn(name).when(factory).getName(element);
        doReturn(title).when(factory).getTitle(element);

        final String label = factory.getLabel(element);

        assertEquals(label, title);
    }

    @Test
    public void testGetLabelWheNameIsNullAndTitleIsValid() {

        final String title = "title";
        final String name = null;

        doReturn(name).when(factory).getName(element);
        doReturn(title).when(factory).getTitle(element);

        final String label = factory.getLabel(element);

        assertEquals(label, title);
    }

    @Test
    public void testGetLabelWheTitleIsNull() {

        final String title = null;
        final String name = null;
        final String noName = "- No name -";

        when(translationService.format(DecisionNavigatorBaseItemFactory_NoName)).thenReturn(noName);
        doReturn(name).when(factory).getName(element);
        doReturn(title).when(factory).getTitle(element);

        final String label = factory.getLabel(element);

        assertEquals(label, noName);
    }

    @Test
    public void testGetLabelWheTitleIsBlank() {

        final String title = "";
        final String name = null;
        final String noName = "- No name -";

        when(translationService.format(DecisionNavigatorBaseItemFactory_NoName)).thenReturn(noName);
        doReturn(name).when(factory).getName(element);
        doReturn(title).when(factory).getTitle(element);

        final String label = factory.getLabel(element);

        assertEquals(label, noName);
    }

    @Test
    public void testGetName() {

        final TextPropertyProvider provider = mock(TextPropertyProvider.class);
        final String expectedName = "text";

        when(textPropertyProviderFactory.getProvider(element)).thenReturn(provider);
        when(provider.getText(element)).thenReturn(expectedName);

        final String actualName = factory.getName(element);

        assertEquals(expectedName, actualName);
    }

    @Test
    public void testGetTitle() {

        final DefinitionManager definitionManager = mock(DefinitionManager.class);
        final AdapterManager adapters = mock(AdapterManager.class);
        final View content = mock(View.class);
        final Object definition = mock(Object.class);
        final String expectedTitle = "title";

        when(definitionUtils.getDefinitionManager()).thenReturn(definitionManager);
        when(definitionManager.adapters()).thenReturn(adapters);
        when(adapters.forDefinition()).thenReturn(objectDefinitionAdapter);
        when(element.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(definition);

        when(objectDefinitionAdapter.getTitle(definition)).thenReturn(expectedTitle);

        final String actualTitle = factory.getTitle(element);

        assertEquals(expectedTitle, actualTitle);
    }

    @Test
    public void testMakeNestedItemsWhenItemHasNestedItems() {

        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);

        when(nestedItemFactory.hasNestedElement(node)).thenReturn(true);
        when(nestedItemFactory.makeItem(node)).thenReturn(item);

        final List<DecisionNavigatorItem> nestedItems = factory.makeNestedItems(node);

        assertEquals(singletonList(item), nestedItems);
    }

    @Test
    public void testMakeNestedItemsWhenItemDoesNotNestedItems() {

        when(nestedItemFactory.hasNestedElement(node)).thenReturn(false);

        final List<DecisionNavigatorItem> nestedItems = factory.makeNestedItems(node);

        assertEquals(emptyList(), nestedItems);
    }

    private TreeSet<DecisionNavigatorItem> asTreeSet(final DecisionNavigatorItem child) {
        return new TreeSet<DecisionNavigatorItem>() {{
            add(child);
        }};
    }
}
