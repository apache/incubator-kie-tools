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

package org.kie.workbench.common.stunner.core.client.canvas;

import java.util.Collections;
import java.util.List;

import jakarta.enterprise.event.Event;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementsClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasElementListener;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.QueueGraphExecutionContext;
import org.kie.workbench.common.stunner.core.client.shape.ElementShape;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.index.MutableIndex;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BaseCanvasHandlerTest {

    private BaseCanvasHandler canvasHandler;

    @Mock
    private ClientDefinitionManager clientDefinitionManager;

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> commandFactory;

    @Mock
    private RuleManager ruleManager;

    @Mock
    private GraphUtils graphUtils;

    @Mock
    private GraphIndexBuilder<? extends MutableIndex<Node, Edge>> indexBuilder;

    @Mock
    private ShapeManager shapeManager;

    @Mock
    private TextPropertyProviderFactory textPropertyProviderFactory;

    @Mock
    private Event<CanvasElementAddedEvent> canvasElementAddedEvent;

    @Mock
    private Event<CanvasElementRemovedEvent> canvasElementRemovedEvent;

    @Mock
    private Event<CanvasElementUpdatedEvent> canvasElementUpdatedEvent;

    @Mock
    private Event<CanvasElementsClearEvent> canvasElementsClearEvent;

    @Mock
    private QueueGraphExecutionContext queueGraphExecutionContext;

    @Mock
    private CanvasElementListener updateListener;

    @Before
    public void setup() {
        canvasHandler = spy(new CanvasHandlerImpl(clientDefinitionManager,
                                                  commandFactory,
                                                  ruleManager,
                                                  graphUtils,
                                                  indexBuilder,
                                                  shapeManager,
                                                  textPropertyProviderFactory,
                                                  canvasElementAddedEvent,
                                                  canvasElementRemovedEvent,
                                                  canvasElementUpdatedEvent,
                                                  canvasElementsClearEvent));
    }

    @Test
    public void checkApplyElementMutationOnPosition() {
        final ElementShape shape = mock(ElementShape.class);
        final Element candidate = mock(Element.class);
        final boolean applyPosition = true;
        final boolean applyProperties = false;
        final MutationContext mutationContext = mock(MutationContext.class);
        canvasHandler.applyElementMutation(shape,
                                           candidate,
                                           applyPosition,
                                           applyProperties,
                                           mutationContext);

        verify(shape, atLeastOnce()).applyPosition(any(), any());
        verify(canvasElementUpdatedEvent, atLeastOnce()).fire(any());
    }

    @Test
    public void checkApplyElementMutationNotifyQueued() {
        final ElementShape shape = mock(ElementShape.class);
        final Element candidate = mock(Element.class);
        final boolean applyPosition = true;
        final boolean applyProperties = false;
        final MutationContext mutationContext = mock(MutationContext.class);
        canvasHandler.applyElementMutation(shape,
                                           candidate,
                                           applyPosition,
                                           applyProperties,
                                           mutationContext);

        verify(shape, atLeastOnce()).applyPosition(any(), any());
        verify(canvasElementUpdatedEvent, atLeastOnce()).fire(any());
        verify(queueGraphExecutionContext, never()).addElement(candidate);
    }

    @Test
    public void checkApplyElementMutationNullQueue() {
        final ElementShape shape = mock(ElementShape.class);
        final Element candidate = mock(Element.class);
        final boolean applyPosition = true;
        final boolean applyProperties = false;
        final MutationContext mutationContext = mock(MutationContext.class);
        canvasHandler.applyElementMutation(shape,
                                           candidate,
                                           applyPosition,
                                           applyProperties,
                                           mutationContext);

        verify(shape, atLeastOnce()).applyPosition(any(), any());
        verify(canvasElementUpdatedEvent, atLeastOnce()).fire(any());
        verify(queueGraphExecutionContext, never()).addElement(candidate);
    }

    @Test
    public void checkNotifyElementUpdatedOnNonQueuedContext() {
        canvasHandler.addRegistrationListener(updateListener);
        final ElementShape shape = mock(ElementShape.class);
        final Element candidate = mock(Element.class);
        final boolean applyPosition = true;
        final boolean applyProperties = false;
        final MutationContext mutationContext = mock(MutationContext.class);
        canvasHandler.applyElementMutation(shape,
                                           candidate,
                                           applyPosition,
                                           applyProperties,
                                           mutationContext);

        verify(shape, atLeastOnce()).applyPosition(any(), any());
        verify(canvasElementUpdatedEvent, atLeastOnce()).fire(any());
        verify(queueGraphExecutionContext, never()).addElement(candidate);
    }

    @Test
    public void checkNotifyElementUpdatedAndListenerUpdated() {
        canvasHandler.addRegistrationListener(updateListener);

        final List<Element> updatedElements = Collections.singletonList(mock(Element.class));

        canvasHandler.doBatchUpdate(updatedElements);
        verify(updateListener, times(1)).updateBatch(any());
    }

    @Test
    public void checkGraphIndexDereference() {
        final Command loadCallback = mock(Command.class);
        final Diagram diagram = mock(Diagram.class);
        MutableIndex graphIndex0 = mock(MutableIndex.class);
        MutableIndex graphIndex1 = mock(MutableIndex.class);

        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(mock(Graph.class));
        doNothing().when(loadCallback).execute();

        when(indexBuilder.build(any(Graph.class))).thenReturn(graphIndex0);
        canvasHandler.buildGraphIndex(loadCallback);

        when(indexBuilder.build(any(Graph.class))).thenReturn(graphIndex1);
        canvasHandler.buildGraphIndex(loadCallback);

        verify(graphIndex0, times(1)).clear();
        verify(graphIndex1, never()).clear();
        assertNotEquals(graphIndex0, canvasHandler.getGraphIndex());
        verify((CanvasHandlerImpl) canvasHandler, times(2)).cleanGraphIndex();
    }
}
