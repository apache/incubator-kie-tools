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

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls;

import java.util.Arrays;
import java.util.Collections;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresShapeView;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext.WiresShapeViewExt;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasPanel;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactoryStub;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.BoundingBox;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ResizeEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ResizeHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ResizeControlImplTest {

    private static final String ELEMENT_UUID = "element-uuid1";
    private static final String ELEMENT_UUID_2 = "element-uuid2";
    private static final Bounds ELEMENT_BOUNDS = Bounds.create(10d, 20d, 30d, 40d);

    @Mock
    private CanvasCommandManager<AbstractCanvasHandler> commandManager;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private AbstractCanvas canvas;

    @Mock
    private AbstractCanvas.CanvasView canvasView;

    @Mock
    private CanvasPanel canvasPanel;

    @Mock
    private Node element;

    @Mock
    private View elementContent;

    @Mock
    private Shape<WiresShapeView> shape;

    @Mock
    private WiresShapeViewExt shapeView;

    @Mock
    private CanvasSelectionEvent elementsSelectedEvent;

    private ResizeControlImpl tested;
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        canvasCommandFactory = spy(new CanvasCommandFactoryStub());
        when(shapeView.supports(ViewEventType.RESIZE)).thenReturn(true);
        when(element.getUUID()).thenReturn(ELEMENT_UUID);
        when(element.getContent()).thenReturn(elementContent);
        when(elementContent.getBounds()).thenReturn(ELEMENT_BOUNDS);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getAbstractCanvas()).thenReturn(canvas);
        when(canvas.getView()).thenReturn(canvasView);
        when(canvasView.getPanel()).thenReturn(canvasPanel);
        when(canvas.getShape(eq(ELEMENT_UUID))).thenReturn(shape);
        when(canvas.getShapes()).thenReturn(Collections.singletonList(shape));
        when(shape.getUUID()).thenReturn(ELEMENT_UUID);
        when(shape.getShapeView()).thenReturn(shapeView);
        when(elementsSelectedEvent.getIdentifiers()).thenReturn(Arrays.asList(ELEMENT_UUID,
                                                                              ELEMENT_UUID_2));
        tested = new ResizeControlImpl(canvasCommandFactory);
        tested.setCommandManagerProvider(() -> commandManager);
    }

    @Test
    public void testRegister() {
        tested.init(canvasHandler);
        assertFalse(tested.isRegistered(element));
        tested.register(element);
        verify(shapeView,
               times(1)).supports(eq(ViewEventType.RESIZE));
        verify(shapeView,
               times(1)).addHandler(eq(ViewEventType.RESIZE),
                                    any(ResizeHandler.class));
        assertTrue(tested.isRegistered(element));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeregister() {
        tested.init(canvasHandler);
        tested.register(element);
        tested.deregister(element);
        verify(shapeView,
               times(1)).removeHandler(any(ViewHandler.class));
        assertFalse(tested.isRegistered(element));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testResize() {
        tested.init(canvasHandler);
        assertFalse(tested.isRegistered(element));
        tested.register(element);
        verify(shapeView,
               times(1)).supports(eq(ViewEventType.RESIZE));
        ArgumentCaptor<ResizeHandler> resizeHandlerArgumentCaptor =
                ArgumentCaptor.forClass(ResizeHandler.class);
        verify(shapeView,
               times(1)).addHandler(eq(ViewEventType.RESIZE),
                                    resizeHandlerArgumentCaptor.capture());
        final CanvasCommand expectedCommand = mock(CanvasCommand.class);
        doAnswer(invocationOnMock -> expectedCommand).when(canvasCommandFactory).resize(eq(element), any(BoundingBox.class));
        ResizeHandler resizeHandler = resizeHandlerArgumentCaptor.getValue();
        double x = 121.45d;
        double y = 23.456d;
        double width = 100d;
        double height = 200d;
        ResizeEvent event = new ResizeEvent(x,
                                            y,
                                            x,
                                            y,
                                            width,
                                            height);
        resizeHandler.end(event);

        ArgumentCaptor<CanvasCommand> commandArgumentCaptor =
                ArgumentCaptor.forClass(CanvasCommand.class);
        verify(commandManager,
               times(1)).execute(eq(canvasHandler),
                                 commandArgumentCaptor.capture());
        CanvasCommand command = commandArgumentCaptor.getValue();
        assertNotNull(command);
        assertEquals(expectedCommand, command);
    }

    @Test
    public void testOnCanvasSelectionEvent() {
        tested.onCanvasSelectionEvent(elementsSelectedEvent);
        verify(elementsSelectedEvent, times(2)).getIdentifiers();
    }
}
