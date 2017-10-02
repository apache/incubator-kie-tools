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

package org.kie.workbench.common.stunner.core.client.canvas.controls.drag;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragContext;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseEnterHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseExitHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DragControlImplTest extends AbstractControlTest {

    private DragControlImpl dragControl;

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    private DragContext dragContext;

    @Mock
    private Command resetCommand;

    @Mock
    private View view;

    @Mock
    private Bounds bounds;

    @Mock
    private Bounds.Bound bound;

    @Mock
    private CanvasCommand<AbstractCanvasHandler> updatePositionCommand;

    @Mock
    private RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;

    @Mock
    private CanvasCommandManager commandManager;

    @Before
    public void setUp() {
        super.setUp();

        dragControl = new DragControlImpl(canvasCommandFactory);
        dragControl.enable(canvasHandler);
        dragControl.setCommandManagerProvider(commandManagerProvider);

        dragContext = new DragContext(0,
                                      0,
                                      resetCommand);
        when(element.getContent()).thenReturn(view);
        when(view.getBounds()).thenReturn(bounds);
        when(bounds.getLowerRight()).thenReturn(bound);
        when(bounds.getUpperLeft()).thenReturn(bound);
        when(canvasCommandFactory.updatePosition(any(Node.class),
                                                 eq(0d),
                                                 eq(0d))).thenReturn(updatePositionCommand);
        when(commandManagerProvider.getCommandManager()).thenReturn(commandManager);
    }

    @Test
    public void testOnKeyDownEvent() throws Exception {
        testStartDrag();
        dragControl.onKeyDownEvent(KeyboardEvent.Key.ESC);

        verify(resetCommand,
               times(1)).execute();
        verify(shapeEventHandler,
               times(1)).removeHandler(any(DragHandler.class));
        assertNull(dragControl.dragContext);
        assertNull(dragControl.selectedElement);
    }

    @Test
    public void testRegister() throws Exception {
        DragHandler dragHandler = testStartDrag();
        testEndDrag(dragHandler);

        verify(shapeEventHandler,
               times(1)).addHandler(eq(ViewEventType.MOUSE_ENTER),
                                    any(MouseEnterHandler.class));
        verify(shapeEventHandler,
               times(1)).addHandler(eq(ViewEventType.MOUSE_EXIT),
                                    any(MouseExitHandler.class));
    }

    private DragHandler testStartDrag() {
        dragControl.register(element);

        final ArgumentCaptor<DragHandler> dragHandlerArgumentCaptor =
                ArgumentCaptor.forClass(DragHandler.class);

        verify(shapeEventHandler,
               times(1)).addHandler(eq(ViewEventType.DRAG),
                                    dragHandlerArgumentCaptor.capture());

        final DragHandler dragHandler = dragHandlerArgumentCaptor.getValue();

        final DragEvent event = new DragEvent(0,
                                              0,
                                              1,
                                              1,
                                              dragContext);
        dragHandler.start(event);
        assertTrue(dragControl.dragContext == dragContext);
        assertEquals(dragControl.selectedElement,
                     element);

        return dragHandler;
    }

    private DragHandler testEndDrag(DragHandler dragHandler) {
        final DragEvent event = new DragEvent(2,
                                              2,
                                              3,
                                              3,
                                              dragContext);
        dragHandler.end(event);

        verify(canvasCommandFactory,
               times(1)).updatePosition(element,
                                        0d,
                                        0d);
        verify(commandManager,
               times(1)).allow(canvasHandler,
                               updatePositionCommand);

        return dragHandler;
    }
}