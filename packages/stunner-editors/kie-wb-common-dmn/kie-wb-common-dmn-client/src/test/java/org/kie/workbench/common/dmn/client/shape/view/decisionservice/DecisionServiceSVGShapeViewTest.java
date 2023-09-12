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
package org.kie.workbench.common.dmn.client.shape.view.decisionservice;

import java.util.Collections;
import java.util.stream.StreamSupport;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.IControlHandleFactory;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStepEvent;
import com.ait.lienzo.client.core.types.DragBounds;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.event.HandlerManager;
import elemental2.dom.HTMLElement;
import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.property.dimensions.GeneralRectangleDimensionsSet;
import org.kie.workbench.common.dmn.client.shape.view.decisionservice.DecisionServiceSVGShapeView.MoveDividerControlHandle;
import org.kie.workbench.common.dmn.client.shape.view.decisionservice.DecisionServiceSVGShapeView.MoveDividerDragHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragHandler;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;
import org.mockito.Mock;

import static com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleStandardType.RESIZE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DecisionServiceSVGShapeViewTest {

    private static final double WIDTH = 100.0;

    private static final double HEIGHT = 200.0;

    @Mock
    private SVGPrimitiveShape svgPrimitive;

    private Rectangle shape;

    @Mock
    private Node shapeNode;

    @Mock
    private DragHandler dragHandler;

    @Mock
    private DragContext dragContext;

    private NodeDragStartEvent nodeDragStartEvent;

    private NodeDragMoveEvent nodeDragMoveEvent;

    private NodeDragEndEvent nodeDragEndEvent;

    private DecisionServiceSVGShapeView view;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        shape = spy(new Rectangle(WIDTH, HEIGHT));
        when(svgPrimitive.get()).thenReturn((Shape) shape);
        when(shape.asNode()).thenReturn(shapeNode);

        this.nodeDragStartEvent = spy(new NodeDragStartEvent(mock(HTMLElement.class)));
        when(this.nodeDragStartEvent.getDragContext()).thenReturn(dragContext);
        this.nodeDragMoveEvent = spy(new NodeDragMoveEvent(mock(HTMLElement.class)));
        when(this.nodeDragMoveEvent.getDragContext()).thenReturn(dragContext);
        this.nodeDragEndEvent = spy(new NodeDragEndEvent(mock(HTMLElement.class)));
        when(this.nodeDragEndEvent.getDragContext()).thenReturn(dragContext);

        this.view = new DecisionServiceSVGShapeView("name",
                                                    svgPrimitive,
                                                    WIDTH,
                                                    HEIGHT,
                                                    true);
    }

    @Test
    public void testGetDividerLineY() {
        assertThat(view.getDividerLineY()).isEqualTo(0.0);
    }

    @Test
    public void testSetDividerLineY() {
        view.setDividerLineY(50.0);

        assertThat(getMoveDividerControlHandle().getControl().getY()).isEqualTo(50.0);
    }

    private MoveDividerControlHandle getMoveDividerControlHandle() {
        final IControlHandleFactory controlHandleFactory = view.getPath().getControlHandleFactory();
        final IControlHandleList controlHandles = controlHandleFactory.getControlHandles(RESIZE).get(RESIZE);
        return StreamSupport
                .stream(controlHandles.spliterator(), false)
                .filter(ch -> ch instanceof MoveDividerControlHandle)
                .map(ch -> (MoveDividerControlHandle) ch)
                .findFirst()
                .get();
    }

    @Test
    public void testResize() {
        WiresResizeStepEvent wiresResizeStepEvent = new WiresResizeStepEvent(mock(HTMLElement.class));
        wiresResizeStepEvent.override(view, nodeDragMoveEvent, 0, 0, WIDTH, HEIGHT);
        view.getHandlerManager().fireEvent(wiresResizeStepEvent);

        assertThat(getMoveDividerControlHandle().getControl().getX()).isEqualTo(WIDTH / 2);
    }

    @Test
    public void testAddDividerDragHandler() {
        view.addDividerDragHandler(dragHandler);

        final HandlerManager handlerManager = view.getHandlerManager();

        assertThat(handlerManager.isEventHandled(MoveDividerStartEvent.TYPE)).isTrue();
        assertThat(handlerManager.isEventHandled(MoveDividerStepEvent.TYPE)).isTrue();
        assertThat(handlerManager.isEventHandled(MoveDividerEndEvent.TYPE)).isTrue();

        assertThat(handlerManager.getHandlerCount(MoveDividerStartEvent.TYPE)).isEqualTo(1);
        assertThat(handlerManager.getHandlerCount(MoveDividerStepEvent.TYPE)).isEqualTo(1);
        assertThat(handlerManager.getHandlerCount(MoveDividerEndEvent.TYPE)).isEqualTo(1);

        MoveDividerStartEvent moveDividerStartEvent = new MoveDividerStartEvent(mock(HTMLElement.class));
        moveDividerStartEvent.override(view, nodeDragStartEvent);
        handlerManager.getHandler(MoveDividerStartEvent.TYPE, 0).onMoveDividerStart(moveDividerStartEvent);
        verify(dragHandler).start(any(DragEvent.class));

        MoveDividerStepEvent moveDividerStepEvent = new MoveDividerStepEvent(mock(HTMLElement.class));
        moveDividerStepEvent.override(view, nodeDragMoveEvent);
        handlerManager.getHandler(MoveDividerStepEvent.TYPE, 0).onMoveDividerStep(moveDividerStepEvent);
        verify(dragHandler).handle(any(DragEvent.class));

        MoveDividerEndEvent moveDividerEndEvent = new MoveDividerEndEvent(mock(HTMLElement.class));
        moveDividerEndEvent.override(view, nodeDragEndEvent);
        handlerManager.getHandler(MoveDividerEndEvent.TYPE, 0).onMoveDividerEnd(moveDividerEndEvent);
        verify(dragHandler).end(any(DragEvent.class));
    }

    @Test
    public void testShapeControlHandleFactory() {
        final IControlHandleFactory controlHandleFactory = view.getPath().getControlHandleFactory();
        assertThat(controlHandleFactory).isInstanceOf(DecisionServiceSVGShapeView.DecisionServiceControlHandleFactory.class);
    }

    @Test
    public void testShapeControlResizeHandles() {
        final IControlHandleFactory controlHandleFactory = view.getPath().getControlHandleFactory();
        final IControlHandleList controlHandles = controlHandleFactory.getControlHandles(Collections.singletonList(RESIZE)).get(RESIZE);

        assertThat(controlHandles.size()).isGreaterThan(0);
        assertThat(controlHandles).areExactly(1, new Condition<>(ch -> ch instanceof MoveDividerControlHandle,
                                                                 "Is a MoveDividerControlHandle"));
    }

    @Test
    public void testShapeControlResizeHandlersWithList() {
        final IControlHandleFactory controlHandleFactory = view.getPath().getControlHandleFactory();
        final IControlHandleList controlHandles = controlHandleFactory.getControlHandles(RESIZE).get(RESIZE);

        assertThat(controlHandles.size()).isGreaterThan(0);
        assertThat(controlHandles).areExactly(1, new Condition<>(ch -> ch instanceof MoveDividerControlHandle,
                                                                 "Is a MoveDividerControlHandle"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDragConstraintHandler() {
        final MoveDividerControlHandle moveDividerControlHandle = getMoveDividerControlHandle();
        final IPrimitive control = moveDividerControlHandle.getControl();
        when(dragContext.getNode()).thenReturn(control);

        final MoveDividerDragHandler dragConstraints = (MoveDividerDragHandler) getMoveDividerControlHandle().getControl().getDragConstraints();
        dragConstraints.startDrag(dragContext);

        final DragBounds dragBounds = control.getDragBounds();
        assertThat(dragBounds.getX1()).isEqualTo(0.0);
        assertThat(dragBounds.getY1()).isEqualTo(GeneralRectangleDimensionsSet.DEFAULT_HEIGHT);
        assertThat(dragBounds.getX2()).isEqualTo(WIDTH);
        assertThat(dragBounds.getY2()).isEqualTo(HEIGHT - GeneralRectangleDimensionsSet.DEFAULT_HEIGHT);
    }
}
