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
package org.kie.workbench.common.dmn.client.shape.view.decisionservice;

import java.util.Collections;
import java.util.stream.StreamSupport;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.shape.Attributes;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.IControlHandleFactory;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStepEvent;
import com.ait.lienzo.client.core.types.DragBounds;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.test.annotation.Settings;
import com.ait.lienzo.test.translator.GWTTranslatorInterceptor;
import com.ait.lienzo.test.translator.LienzoJSOStubTranslatorInterceptor;
import com.ait.lienzo.test.translator.LienzoNodeTranslatorInterceptor;
import com.ait.lienzo.test.translator.LienzoStubTranslatorInterceptor;
import com.ait.lienzo.test.translator.StripFinalModifiersTranslatorInterceptor;
import com.google.gwt.event.shared.HandlerManager;
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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
//This clones DefaultSettingsHolder but overrides JSOStub for DragBounds$DragBoundsJSO
@Settings(
        stubs = {
                com.ait.lienzo.test.stub.overlays.BoundingBoxJSO.class,
                com.ait.lienzo.test.stub.overlays.TransformJSO.class,
                com.ait.lienzo.test.stub.overlays.ShadowJSO.class,
                com.ait.lienzo.test.stub.overlays.NObjectJSO.class,
                com.ait.lienzo.test.stub.overlays.Point2DJSO.class,
                com.ait.lienzo.test.stub.overlays.JsArray.class,
                com.ait.lienzo.test.stub.overlays.JsArrayMixed.class,
                com.ait.lienzo.test.stub.overlays.PathPartListJSO.class,
                com.ait.lienzo.test.stub.overlays.PathPartEntryJSO.class,
                com.ait.lienzo.test.stub.overlays.Point2DArrayJSO.class,
                com.ait.lienzo.test.stub.overlays.NArrayBaseJSO.class,
                com.ait.lienzo.test.stub.overlays.NFastDoubleArrayJSO.class,
                com.ait.lienzo.test.stub.overlays.OptionalNodeFields.class,
                com.ait.lienzo.test.stub.overlays.OptionalShapeFields.class,
                com.ait.lienzo.test.stub.overlays.OptionalGroupOfFields.class,
                com.ait.lienzo.test.stub.Attributes.class,
                com.ait.lienzo.test.stub.NFastArrayList.class,
                com.ait.lienzo.test.stub.NFastStringMap.class,
                org.kie.workbench.common.dmn.client.shape.view.decisionservice.DragBoundsJSO.class
        },
        jsoStubs = {
                "com.ait.tooling.nativetools.client.collection.NFastStringHistogram$NFastStringHistogramJSO",
                "com.ait.tooling.nativetools.client.collection.NFastStringSet$NFastStringSetJSO",
        },
        translators = {
                LienzoStubTranslatorInterceptor.class,
                LienzoJSOStubTranslatorInterceptor.class,
                StripFinalModifiersTranslatorInterceptor.class,
                LienzoNodeTranslatorInterceptor.class,
                GWTTranslatorInterceptor.class
        })
public class DecisionServiceSVGShapeViewTest {

    private static final double WIDTH = 100.0;

    private static final double HEIGHT = 200.0;

    @Mock
    private SVGPrimitiveShape svgPrimitive;

    @Mock
    private Shape shape;

    @Mock
    private Attributes attributes;

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
        when(svgPrimitive.get()).thenReturn(shape);
        when(shape.getAttributes()).thenReturn(attributes);
        when(shape.asNode()).thenReturn(shapeNode);
        when(attributes.getDouble(Attribute.WIDTH.getProperty())).thenReturn(WIDTH);
        when(attributes.getDouble(Attribute.HEIGHT.getProperty())).thenReturn(HEIGHT);

        this.nodeDragStartEvent = new NodeDragStartEvent(dragContext);
        this.nodeDragMoveEvent = new NodeDragMoveEvent(dragContext);
        this.nodeDragEndEvent = new NodeDragEndEvent(dragContext);

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
        view.getHandlerManager().fireEvent(new WiresResizeStepEvent(view, nodeDragMoveEvent, 0, 0, WIDTH, HEIGHT));

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

        handlerManager.getHandler(MoveDividerStartEvent.TYPE, 0).onMoveDividerStart(new MoveDividerStartEvent(view,
                                                                                                              nodeDragStartEvent));
        verify(dragHandler).start(any(DragEvent.class));

        handlerManager.getHandler(MoveDividerStepEvent.TYPE, 0).onMoveDividerStep(new MoveDividerStepEvent(view,
                                                                                                           nodeDragMoveEvent));
        verify(dragHandler).handle(any(DragEvent.class));

        handlerManager.getHandler(MoveDividerEndEvent.TYPE, 0).onMoveDividerEnd(new MoveDividerEndEvent(view,
                                                                                                        nodeDragEndEvent));
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
    public void testShapeControlResizeHandlerMoveDividerEvents() {
        final MoveDividerControlHandle moveDividerControlHandle = getMoveDividerControlHandle();

        view.addDividerDragHandler(dragHandler);

        moveDividerControlHandle.getControl().fireEvent(nodeDragStartEvent);
        verify(dragHandler).start(any(DragEvent.class));

        moveDividerControlHandle.getControl().fireEvent(nodeDragMoveEvent);
        verify(dragHandler).handle(any(DragEvent.class));

        moveDividerControlHandle.getControl().fireEvent(nodeDragEndEvent);
        verify(dragHandler).end(any(DragEvent.class));
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
