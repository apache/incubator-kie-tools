/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.expressions;

import java.util.Collections;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.editors.drd.DRDNameChanger;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasDomainObjectListener;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ExpressionEditorControlImplTest {

    @Mock
    private DMNSession session;

    @Mock
    private ExpressionEditorView view;

    @Mock
    private DecisionNavigatorPresenter decisionNavigator;

    @Mock
    private DMNGraphUtils dmnGraphUtils;

    @Mock
    private DMNDiagramsSession dmnDiagramsSession;

    @Mock
    private EventSourceMock<CanvasElementUpdatedEvent> canvasElementUpdatedEvent;

    @Mock
    private ExpressionEditorView.Presenter editor;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Graph<?, Node> graph;

    @Mock
    private Node node;

    @Mock
    private DomainObject domainObject;

    @Mock
    private CanvasSelectionEvent event;

    @Mock
    private DRDNameChanger drdNameChanger;

    @Captor
    private ArgumentCaptor<CanvasDomainObjectListener> domainObjectListenerCaptor;

    @Captor
    private ArgumentCaptor<CanvasElementUpdatedEvent> canvasElementUpdatedEventCaptor;

    private ExpressionEditorControlImpl control;

    @Before
    public void setup() {
        this.control = spy(new ExpressionEditorControlImpl(view,
                                                           decisionNavigator,
                                                           dmnGraphUtils,
                                                           dmnDiagramsSession,
                                                           canvasElementUpdatedEvent,
                                                           drdNameChanger));
        doReturn(editor).when(control).makeExpressionEditor(any(ExpressionEditorView.class),
                                                            any(DecisionNavigatorPresenter.class),
                                                            any(DMNGraphUtils.class),
                                                            any(DMNDiagramsSession.class));
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
    }

    @Test
    public void testBind() {
        control.bind(session);

        assertNotNull(control.getExpressionEditor());
        verify(editor).bind(session);
        verify(canvasHandler).addDomainObjectListener(any(CanvasDomainObjectListener.class));
    }

    @Test
    public void testBindDomainObjectListenerWithNodeMatch() {
        final Definition definition = mock(Definition.class);
        when(graph.nodes()).thenReturn(Collections.singletonList(node));
        when(node.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(domainObject);
        when(domainObject.getDomainObjectUUID()).thenReturn("uuid");

        control.bind(session);

        verify(canvasHandler).addDomainObjectListener(domainObjectListenerCaptor.capture());

        final CanvasDomainObjectListener domainObjectListener = domainObjectListenerCaptor.getValue();
        domainObjectListener.update(domainObject);

        verify(canvasElementUpdatedEvent).fire(canvasElementUpdatedEventCaptor.capture());

        final CanvasElementUpdatedEvent canvasElementUpdatedEvent = canvasElementUpdatedEventCaptor.getValue();
        assertThat(canvasElementUpdatedEvent.getCanvasHandler()).isEqualTo(canvasHandler);
        assertThat(canvasElementUpdatedEvent.getElement()).isEqualTo(node);
    }

    @Test
    public void testBindDomainObjectListenerWithNoNodeMatch() {
        when(graph.nodes()).thenReturn(Collections.emptyList());

        control.bind(session);

        verify(canvasHandler).addDomainObjectListener(domainObjectListenerCaptor.capture());

        final CanvasDomainObjectListener domainObjectListener = domainObjectListenerCaptor.getValue();
        domainObjectListener.update(domainObject);

        verify(canvasElementUpdatedEvent, never()).fire(any(CanvasElementUpdatedEvent.class));
    }

    @Test
    public void testDoInit() {
        assertNull(control.getExpressionEditor());

        control.doInit();

        assertNull(control.getExpressionEditor());
    }

    @Test
    public void testDoDestroy() {
        control.bind(session);

        verify(canvasHandler).addDomainObjectListener(domainObjectListenerCaptor.capture());

        control.doDestroy();

        assertNull(control.getExpressionEditor());
        final CanvasDomainObjectListener domainObjectListener = domainObjectListenerCaptor.getValue();
        verify(canvasHandler).removeDomainObjectListener(domainObjectListener);
    }

    @Test
    public void testOnCanvasFocusedSelectionEventWhenBoundSameSession() {
        when(event.getCanvasHandler()).thenReturn(canvasHandler);

        control.bind(session);

        control.onCanvasFocusedSelectionEvent(event);

        verify(editor).exit();
    }

    @Test
    public void testOnCanvasFocusedSelectionEventWhenBoundDifferentSession() {
        when(event.getCanvasHandler()).thenReturn(mock(CanvasHandler.class));

        control.bind(session);

        control.onCanvasFocusedSelectionEvent(event);

        verify(editor, never()).exit();
    }

    @Test
    public void testOnCanvasFocusedSelectionEventWhenNotBound() {
        control.onCanvasFocusedSelectionEvent(event);

        verifyNoMoreInteractions(editor);
    }

    @Test
    public void testOnCanvasElementUpdated() {
        control.bind(session);

        final CanvasElementUpdatedEvent event = new CanvasElementUpdatedEvent(canvasHandler, node);

        control.onCanvasElementUpdated(event);

        verify(editor).handleCanvasElementUpdated(event);
    }
}
