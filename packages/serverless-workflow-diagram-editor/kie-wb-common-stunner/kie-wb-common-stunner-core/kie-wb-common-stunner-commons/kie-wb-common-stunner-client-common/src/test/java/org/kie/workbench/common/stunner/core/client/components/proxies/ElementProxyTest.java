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


package org.kie.workbench.common.stunner.core.client.components.proxies;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.DefaultCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.shape.ElementShape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.stubs.ManagedInstanceStub;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ElementProxyTest {

    private static final String SHAPE_UUID = "proxyShape1";

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private DefaultCanvasCommandFactory commandFactory;
    private ManagedInstanceStub<DefaultCanvasCommandFactory> commandFactories;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> commandManager;

    @Mock
    private EventSourceMock<CanvasSelectionEvent> selectionEvent;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private AbstractCanvas canvas;

    @Mock
    private ElementShape proxyShape;

    @Mock
    private SessionManager sessionManager;

    private ElementProxy tested;
    private ElementProxyViewMock<ElementShape> view;

    @Before
    public void setUp() {
        commandFactories = new ManagedInstanceStub<>(commandFactory);
        when(proxyShape.getUUID()).thenReturn(SHAPE_UUID);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        view = spy(new ElementProxyViewMock<>());
        tested = spy(new ElementProxy(commandManager, selectionEvent, commandFactories, definitionUtils, sessionManager)
                             .setCanvasHandler(canvasHandler)
                             .setView(view)
                             .setProxyBuilder(() -> proxyShape));
    }

    @Test
    public void testStart() {
        double x = 15d;
        double y = 1.5d;
        tested.start(x, y);
        verify(view, times(1)).setCanvas(eq(canvas));
        verify(view, times(1)).onCreate(any());
        verify(view, times(1)).onAccept(any());
        verify(view, times(1)).onDestroy(any());
        verify(view, times(1)).start(eq(x), eq(y));
    }

    @Test
    public void testShapeBuilder() {
        tested.start(1, 2);
        ElementShape shape = view.getShapeBuilder().get();
        assertEquals(proxyShape, shape);
        verify(proxyShape, times(1)).applyState(eq(ShapeState.SELECTED));
        verify(commandManager, times(1)).start();
        verify(commandManager, never()).rollback();
        verify(commandManager, never()).complete();
    }

    @Test
    public void testShapeAcceptor() {
        tested.start(1, 2);
        view.getShapeAcceptor().accept(proxyShape);
        verify(commandManager, times(1)).complete();
        verify(commandManager, never()).rollback();
        verify(commandManager, never()).start();
        verify(tested, times(1)).select(eq(SHAPE_UUID));
    }

    @Test
    public void testShapeDestroyer() {
        tested.start(1, 2);
        view.getShapeDestroyer().accept(proxyShape);
        InOrder cmOrder = inOrder(commandManager);
        cmOrder.verify(commandManager, times(1)).rollback();
        cmOrder.verify(commandManager, times(1)).complete();
        verify(commandManager, never()).start();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        Command c = mock(Command.class);
        tested.execute(c);
        verify(commandManager, times(1)).execute(eq(canvasHandler), eq(c));
        verify(commandManager, never()).allow(any(), any());
    }

    static class ElementProxyViewMock<S extends ElementShape>
            extends AbstractShapeProxyView<S> {

        @Override
        public void start(final double x,
                          final double y) {
        }

        @Override
        protected void doDestroy() {

        }
    }
}
