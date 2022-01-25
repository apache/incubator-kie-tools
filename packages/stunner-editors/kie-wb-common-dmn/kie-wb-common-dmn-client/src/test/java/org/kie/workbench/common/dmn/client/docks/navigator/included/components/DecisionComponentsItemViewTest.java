/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.docks.navigator.included.components;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLImageElement;
import elemental2.dom.HTMLParagraphElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DMNElement;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.client.DMNShapeSet;
import org.kie.workbench.common.dmn.client.shape.factory.DMNShapeFactory;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.ShapeGlyphDragHandler;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.ShapeGlyphDragHandler.Callback;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.ShapeGlyphDragHandler.Item;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.event.BuildCanvasShapeEvent;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DecisionComponentsItemView_DuplicatedNode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.WARNING;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionComponentsItemViewTest {

    @Mock
    private HTMLImageElement icon;

    @Mock
    private HTMLHeadingElement name;

    @Mock
    private HTMLDivElement decisionComponentItem;

    @Mock
    private HTMLParagraphElement file;

    @Mock
    private DMNShapeSet dmnShapeSet;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private ShapeGlyphDragHandler shapeGlyphDragHandler;

    @Mock
    private EventSourceMock<BuildCanvasShapeEvent> buildCanvasShapeEvent;

    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    private ClientTranslationService clientTranslationService;

    @Mock
    private DecisionComponentsItem presenter;

    @Captor
    private ArgumentCaptor<NotificationEvent> notificationEventArgumentCaptor;

    @Captor
    private ArgumentCaptor<BuildCanvasShapeEvent> buildCanvasShapeEventArgumentCaptor;

    private DecisionComponentsItemView view;

    @Before
    public void setup() {
        view = spy(new DecisionComponentsItemView(icon, name, file, dmnShapeSet, sessionManager, shapeGlyphDragHandler, buildCanvasShapeEvent, decisionComponentItem, notificationEvent, clientTranslationService));
        view.init(presenter);
    }

    @Test
    public void testSetIcon() {
        final String iconURI = "http://src.icon.url";
        icon.src = "something";

        view.setIcon(iconURI);

        assertEquals(iconURI, icon.src);
    }

    @Test
    public void testSetName() {
        final String name = "name";
        this.name.textContent = "something";

        view.setName(name);

        assertEquals(name, this.name.textContent);
    }

    @Test
    public void testSetFile() {
        final String file = "file";
        this.file.textContent = "something";

        view.setFile(file);

        assertEquals(file, this.file.textContent);
    }

    @Test
    public void testDecisionComponentItemMouseDown() {

        final MouseDownEvent mouseDownEvent = mock(MouseDownEvent.class);
        final Callback proxy = mock(Callback.class);
        final DRGElement drgElement = mock(DRGElement.class);
        final DMNShapeFactory factory = mock(DMNShapeFactory.class);
        final ShapeGlyphDragHandler.Item item = mock(ShapeGlyphDragHandler.Item.class);
        final Glyph glyph = mock(Glyph.class);
        final int x = 10;
        final int y = 20;

        when(dmnShapeSet.getShapeFactory()).thenReturn(factory);
        when(presenter.getDrgElement()).thenReturn(drgElement);
        when(factory.getGlyph(any())).thenReturn(glyph);
        when(mouseDownEvent.getX()).thenReturn(x);
        when(mouseDownEvent.getY()).thenReturn(y);

        doReturn(proxy).when(view).makeDragProxyCallbackImpl(drgElement, factory);
        doReturn(item).when(view).makeDragHandler(glyph);

        view.decisionComponentItemMouseDown(mouseDownEvent);

        verify(shapeGlyphDragHandler).show(item, x, y, proxy);
    }

    @Test
    public void testMakeDragProxyCallbackImplWhenNodeIsDuplicated() {

        final ShapeFactory factory = mock(ShapeFactory.class);
        final DRGElement drgElement = mock(DRGElement.class);
        final int x = 10;
        final int y = 20;
        final String expectedWarnMessage = "This 'DRGElement' already exists!";
        final NotificationEvent.NotificationType expectedWarnType = WARNING;
        final Graph<?, Node> graph = mock(Graph.class);
        final List<Node> nodes = new ArrayList<>();

        final String id1 = "123";
        final String id2 = "123";

        nodes.add(createNode(id1));
        nodes.add(createNode(id2));

        when(graph.nodes()).thenReturn(nodes);
        when(drgElement.getId()).thenReturn(new Id(id1));
        when(clientTranslationService.getValue(DecisionComponentsItemView_DuplicatedNode)).thenReturn(expectedWarnMessage);
        doReturn(graph).when(view).getGraph();

        view.setIsImported(true);
        view.makeDragProxyCallbackImpl(drgElement, factory).onComplete(x, y);

        verify(buildCanvasShapeEvent, never()).fire(any());
        verify(notificationEvent).fire(notificationEventArgumentCaptor.capture());

        final NotificationEvent notificationEvent = notificationEventArgumentCaptor.getValue();

        assertEquals(expectedWarnMessage, notificationEvent.getNotification());
        assertEquals(expectedWarnType, notificationEvent.getType());
    }

    @Test
    public void testMakeDragProxyCallbackImplWhenNodeIsDuplicatedButIsNotImported() {

        final ClientSession currentSession = mock(ClientSession.class);
        final AbstractCanvasHandler canvasHandler = mock(AbstractCanvasHandler.class);
        final ShapeFactory factory = mock(ShapeFactory.class);
        final DRGElement drgElement = mock(DRGElement.class);
        final int x = 10;
        final int y = 20;

        final Graph<?, Node> graph = mock(Graph.class);
        final List<Node> nodes = new ArrayList<>();

        final String id1 = "123";
        final String id2 = "123";

        nodes.add(createNode(id1));
        nodes.add(createNode(id2));

        when(graph.nodes()).thenReturn(nodes);
        when(drgElement.getId()).thenReturn(new Id(id1));
        when(sessionManager.getCurrentSession()).thenReturn(currentSession);
        when(currentSession.getCanvasHandler()).thenReturn(canvasHandler);

        doReturn(graph).when(view).getGraph();
        view.setIsImported(false);

        view.makeDragProxyCallbackImpl(drgElement, factory).onComplete(x, y);

        verify(buildCanvasShapeEvent).fire(any());
        verify(notificationEvent, never()).fire(notificationEventArgumentCaptor.capture());
    }

    private Node createNode(final String id) {

        final Node n1 = mock(Node.class);
        final View v1 = mock(View.class);
        final DMNElement d1 = mock(DMNElement.class);
        final Id id1 = mock(Id.class);

        when(n1.getContent()).thenReturn(v1);
        when(v1.getDefinition()).thenReturn(d1);
        when(d1.getId()).thenReturn(id1);
        when(id1.getValue()).thenReturn(id);

        return n1;
    }

    @Test
    public void testMakeDragProxyCallbackImplWhenNodeIsNotDuplicated() {

        final ShapeFactory factory = mock(ShapeFactory.class);
        final DRGElement drgElement1 = mock(DRGElement.class);
        final DRGElement drgElement2 = mock(DRGElement.class);
        final DRGElement drgElement3 = mock(DRGElement.class);
        final ClientSession currentSession = mock(ClientSession.class);
        final AbstractCanvasHandler canvasHandler = mock(AbstractCanvasHandler.class);
        final Graph<?, Node> graph = mock(Graph.class);
        final List<Node> nodes = new ArrayList<>();
        final int x = 10;
        final int y = 20;
        final String id1 = "123";
        final String id2 = "456";
        final String id3 = "789";

        nodes.add(createNode(id1));
        nodes.add(createNode(id2));

        when(graph.nodes()).thenReturn(nodes);

        when(drgElement1.getId()).thenReturn(new Id(id1));
        when(drgElement2.getId()).thenReturn(new Id(id2));
        when(drgElement3.getId()).thenReturn(new Id(id3));

        when(sessionManager.getCurrentSession()).thenReturn(currentSession);
        when(currentSession.getCanvasHandler()).thenReturn(canvasHandler);

        doReturn(graph).when(view).getGraph();

        view.makeDragProxyCallbackImpl(drgElement3, factory).onComplete(x, y);

        verify(notificationEvent, never()).fire(any());
        verify(buildCanvasShapeEvent).fire(buildCanvasShapeEventArgumentCaptor.capture());

        final BuildCanvasShapeEvent canvasShapeEvent = buildCanvasShapeEventArgumentCaptor.getValue();

        assertEquals(canvasHandler, canvasShapeEvent.getCanvasHandler());
        assertEquals(drgElement3, canvasShapeEvent.getDefinition());
        assertEquals(factory, canvasShapeEvent.getShapeFactory());
        assertEquals(x, canvasShapeEvent.getClientX(), 0.1);
        assertEquals(y, canvasShapeEvent.getClientY(), 0.1);
    }

    @Test
    public void testMakeDragHandler() {

        final Glyph glyph = mock(Glyph.class);

        final Item item = view.makeDragHandler(glyph);

        assertEquals(16, item.getHeight());
        assertEquals(16, item.getWidth());
        assertEquals(glyph, item.getShape());
    }
}
