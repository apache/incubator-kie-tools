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

package org.kie.workbench.common.stunner.cm.client.canvas;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.enterprise.event.Event;

import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.canvas.view.LienzoPanel;
import org.kie.workbench.common.stunner.cm.client.shape.CaseManagementShape;
import org.kie.workbench.common.stunner.cm.client.shape.view.CaseManagementShapeView;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.Layer;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasDrawnEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementCanvasPresenterTest {

    @Spy
    private Event<CanvasClearEvent> canvasClearEvent = new EventSourceMock<>();

    @Spy
    private Event<CanvasShapeAddedEvent> canvasShapeAddedEvent = new EventSourceMock<>();

    @Spy
    private Event<CanvasShapeRemovedEvent> canvasShapeRemovedEvent = new EventSourceMock<>();

    @Spy
    private Event<CanvasDrawnEvent> canvasDrawnEvent = new EventSourceMock<>();

    @Spy
    private Event<CanvasFocusedEvent> canvasFocusedEvent = new EventSourceMock<>();

    @Mock
    private Layer layer;

    @Mock
    private LienzoPanel lienzoPanel;

    @Mock
    private Shape parent;

    @Mock
    private ShapeView parentView;

    @Mock
    private Shape child;

    @Mock
    private ShapeView childView;

    @Mock
    private CaseManagementCanvasView view;

    private CaseManagementCanvasPresenter presenter;

    @Before
    public void setup() {
        when(parent.getShapeView()).thenReturn(parentView);
        when(child.getShapeView()).thenReturn(childView);

        when(view.getWiresManager()).thenReturn(mock(WiresManager.class));

        this.presenter = new CaseManagementCanvasPresenter(canvasClearEvent,
                                                           canvasShapeAddedEvent,
                                                           canvasShapeRemovedEvent,
                                                           canvasDrawnEvent,
                                                           canvasFocusedEvent,
                                                           layer,
                                                           view,
                                                           lienzoPanel);

        doNothing().when(canvasShapeAddedEvent).fire(any(CanvasShapeAddedEvent.class));
        doNothing().when(canvasShapeRemovedEvent).fire(any(CanvasShapeRemovedEvent.class));
    }

    @Test
    public void addChildShapeToCaseManagementCanvasView() {
        presenter.addChildShape(parent,
                                child,
                                1);

        verify(view,
               times(1)).addChildShape(eq(parentView),
                                       eq(childView),
                                       eq(1));
    }

    @Test
    public void clearShapes() {
        CaseManagementShape shape0 = createShape("0");
        CaseManagementShape shape1 = createShape("1");
        CaseManagementShape shape11 = createShape("11");
        CaseManagementShape shape12 = createShape("12");
        CaseManagementShape shape2 = createShape("2");
        CaseManagementShape shape21 = createShape("21");
        CaseManagementShape shape22 = createShape("22");

        shape0.getShapeView().add(shape1.getShapeView());
        shape0.getShapeView().add(shape2.getShapeView());
        shape1.getShapeView().add(shape11.getShapeView());
        shape1.getShapeView().add(shape12.getShapeView());
        shape2.getShapeView().add(shape21.getShapeView());
        shape2.getShapeView().add(shape22.getShapeView());

        CaseManagementCanvasPresenter presenter = new CaseManagementCanvasPresenter(canvasClearEvent,
                                                                                    canvasShapeAddedEvent,
                                                                                    canvasShapeRemovedEvent,
                                                                                    canvasDrawnEvent,
                                                                                    canvasFocusedEvent,
                                                                                    layer,
                                                                                    view,
                                                                                    lienzoPanel) {

            private List<CaseManagementShape> shapesValidation = new LinkedList<CaseManagementShape>() {{
                this.add(shape0);
                this.add(shape1);
                this.add(shape11);
                this.add(shape12);
                this.add(shape2);
                this.add(shape21);
                this.add(shape22);
            }};

            @Override
            public Canvas deleteShape(Shape shape) {
                assertSame(shapesValidation.remove(0), shape);

                return super.deleteShape(shape);
            }
        };

        presenter.addShape(shape1);
        presenter.addShape(shape2);
        presenter.addShape(shape21);
        presenter.addShape(shape22);
        presenter.addShape(shape11);
        presenter.addShape(shape12);
        presenter.addShape(shape0);

        presenter.clearShapes();
    }

    private CaseManagementShape createShape(String name) {
        CaseManagementShapeView shapeView = new CaseManagementShapeView(name,
                                                                        new SVGPrimitiveShape(new Rectangle(0d, 0d)),
                                                                        0d,
                                                                        0d,
                                                                        false);
        shapeView.setUUID(UUID.randomUUID().toString());
        CaseManagementShape shape = new CaseManagementShape(shapeView);
        shape.setUUID(shapeView.getUUID());
        return shape;
    }
}
