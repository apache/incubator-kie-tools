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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.AbstractControlHandle;
import com.ait.lienzo.client.core.shape.wires.IControlHandle;
import com.ait.lienzo.client.core.shape.wires.IControlHandleFactory;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.shape.wires.decorator.PointHandleDecorator;
import com.ait.lienzo.client.core.types.DragBounds;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DefaultDragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.shared.core.types.DragConstraint;
import com.ait.lienzo.shared.core.types.DragMode;
import com.ait.lienzo.tools.client.event.HandlerManager;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import com.ait.lienzo.tools.client.event.HandlerRegistrationManager;
import com.ait.lienzo.tools.client.event.INodeEvent;
import org.kie.workbench.common.dmn.api.property.dimensions.GeneralRectangleDimensionsSet;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeViewImpl;

import static com.ait.lienzo.client.core.shape.AbstractMultiPathPartShape.DefaultMultiPathShapeHandleFactory.R0;
import static com.ait.lienzo.client.core.shape.AbstractMultiPathPartShape.DefaultMultiPathShapeHandleFactory.R1;
import static com.ait.lienzo.client.core.shape.AbstractMultiPathPartShape.DefaultMultiPathShapeHandleFactory.animate;
import static com.ait.lienzo.client.core.shape.AbstractMultiPathPartShape.DefaultMultiPathShapeHandleFactory.getControlPrimitive;

public class DecisionServiceSVGShapeView extends SVGShapeViewImpl {

    private final DecisionServiceDividerLine divider;
    private final HandlerRegistrationManager registrationManager = new HandlerRegistrationManager();
    private final DecisionServiceControlHandleFactory decisionServiceControlHandleFactory;

    public DecisionServiceSVGShapeView(final String name,
                                       final SVGPrimitiveShape svgPrimitive,
                                       final double width,
                                       final double height,
                                       final boolean resizable) {
        super(name,
              svgPrimitive,
              width,
              height,
              resizable);

        final Shape<?> shape = getPath();
        this.divider = new DecisionServiceDividerLine(() -> shape.getBoundingBox().getWidth());
        this.decisionServiceControlHandleFactory = new DecisionServiceControlHandleFactory(divider,
                                                                                           shape.getControlHandleFactory(),
                                                                                           () -> shape.getBoundingBox().getWidth(),
                                                                                           () -> shape.getBoundingBox().getHeight());
        shape.setControlHandleFactory(decisionServiceControlHandleFactory);
        addChild(divider.asSVGPrimitiveShape());

        addWiresResizeStepHandler(event -> {
            decisionServiceControlHandleFactory
                    .getMoveDividerControlHandle()
                    .ifPresent(handle -> handle.getControl().setX(shape.getBoundingBox().getWidth() / 2));
        });
    }

    public DecisionServiceSVGShapeView addDividerDragHandler(final DragHandler dragHandler) {
        final HandlerManager handlerManager = getHandlerManager();
        final HandlerRegistration dragStartRegistration =
                handlerManager.addHandler(MoveDividerStartEvent.TYPE,
                                          (MoveDividerStartHandler) event -> dragHandler.start(buildDragEvent(event)));
        final HandlerRegistration dragStepRegistration = handlerManager.addHandler(MoveDividerStepEvent.TYPE,
                                                                                   (MoveDividerStepHandler) event -> dragHandler.handle(buildDragEvent(event)));
        final HandlerRegistration dragEndRegistration = handlerManager.addHandler(MoveDividerEndEvent.TYPE,
                                                                                  (MoveDividerEndHandler) event -> dragHandler.end(buildDragEvent(event)));
        final HandlerRegistration[] registrations = new HandlerRegistration[]{dragStartRegistration, dragStepRegistration, dragEndRegistration};
        getEventHandlerManager().addHandlersRegistration(ViewEventType.DRAG, registrations);

        return this;
    }

    public double getDividerLineY() {
        return divider.getY();
    }

    public void setDividerLineY(final double y) {
        divider.setY(y);
        decisionServiceControlHandleFactory
                .getMoveDividerControlHandle()
                .ifPresent(handle -> handle.getControl().setY(y));
    }

    @Override
    //Override to increase visibility for Unit Tests
    public HandlerManager getHandlerManager() {
        return super.getHandlerManager();
    }

    @Override
    public void destroy() {
        registrationManager.destroy();
        super.destroy();
    }

    class DecisionServiceControlHandleFactory implements IControlHandleFactory {

        private final DecisionServiceDividerLine divider;
        private final IControlHandleFactory delegateControlHandleFactory;
        private final Supplier<Double> dragBoundsWidthSupplier;
        private final Supplier<Double> dragBoundsHeightSupplier;

        private Optional<MoveDividerControlHandle> moveDividerControlHandle = Optional.empty();

        DecisionServiceControlHandleFactory(final DecisionServiceDividerLine divider,
                                            final IControlHandleFactory delegateControlHandleFactory,
                                            final Supplier<Double> dragBoundsWidthSupplier,
                                            final Supplier<Double> dragBoundsHeightSupplier) {
            this.divider = divider;
            this.delegateControlHandleFactory = delegateControlHandleFactory;
            this.dragBoundsWidthSupplier = dragBoundsWidthSupplier;
            this.dragBoundsHeightSupplier = dragBoundsHeightSupplier;
        }

        Optional<MoveDividerControlHandle> getMoveDividerControlHandle() {
            return moveDividerControlHandle;
        }

        @Override
        public Map<IControlHandle.ControlHandleType, IControlHandleList> getControlHandles(final IControlHandle.ControlHandleType... types) {
            final Map<IControlHandle.ControlHandleType, IControlHandleList> controlHandles = delegateControlHandleFactory.getControlHandles(types);
            appendMoveDividerControlPoint(controlHandles);
            return controlHandles;
        }

        @Override
        public Map<IControlHandle.ControlHandleType, IControlHandleList> getControlHandles(final List<IControlHandle.ControlHandleType> types) {
            final Map<IControlHandle.ControlHandleType, IControlHandleList> controlHandles = delegateControlHandleFactory.getControlHandles(types);
            appendMoveDividerControlPoint(controlHandles);
            return controlHandles;
        }

        private void appendMoveDividerControlPoint(final Map<IControlHandle.ControlHandleType, IControlHandleList> controlHandles) {
            final IControlHandleList resizeControlHandles = controlHandles.get(IControlHandle.ControlHandleStandardType.RESIZE);
            if (!moveDividerControlHandle.isPresent()) {
                moveDividerControlHandle = Optional.of(getMoveDividerControlHandle(divider,
                                                                                   resizeControlHandles,
                                                                                   new Point2D(dragBoundsWidthSupplier.get() / 2, 0)));
                setupMoveDividerEventHandlers();
            }
            resizeControlHandles.add(moveDividerControlHandle.get());
        }

        private void setupMoveDividerEventHandlers() {
            moveDividerControlHandle.ifPresent(handle -> {
                final IPrimitive<?> control = handle.getControl();
                registrationManager.register(control.addNodeDragStartHandler(this::moveDividerStart));
                registrationManager.register(control.addNodeDragMoveHandler(this::moveDividerMove));
                registrationManager.register(control.addNodeDragEndHandler(this::moveDividerEnd));
            });
        }

        private void moveDividerStart(final NodeDragStartEvent event) {
            MoveDividerStartEvent e = new MoveDividerStartEvent(event.getRelativeElement());
            e.override(DecisionServiceSVGShapeView.this, event);
            fireMoveDividerEvent(e);
        }

        private void moveDividerMove(final NodeDragMoveEvent event) {
            MoveDividerStepEvent e = new MoveDividerStepEvent(event.getRelativeElement());
            e.override(DecisionServiceSVGShapeView.this, event);
            fireMoveDividerEvent(e);
        }

        private void moveDividerEnd(final NodeDragEndEvent event) {
            MoveDividerEndEvent e = new MoveDividerEndEvent(event.getRelativeElement());
            e.override(DecisionServiceSVGShapeView.this, event);
            fireMoveDividerEvent(e);
        }

        private void fireMoveDividerEvent(final INodeEvent event) {
            moveDividerControlHandle.ifPresent(handle -> {
                divider.setY(handle.getControl().getY());
                getHandlerManager().fireEvent(event);
            });
        }

        private MoveDividerControlHandle getMoveDividerControlHandle(final DecisionServiceDividerLine divider,
                                                                     final IControlHandleList resizeControlHandles,
                                                                     final Point2D controlPointOffset) {
            final Circle controlShape = getControlPrimitive(R0,
                                                            controlPointOffset.getX(),
                                                            controlPointOffset.getY(),
                                                            divider,
                                                            DragMode.SAME_LAYER).setDragConstraint(DragConstraint.VERTICAL);
            final MoveDividerControlHandle handle = new MoveDividerControlHandle(controlShape,
                                                                                 resizeControlHandles,
                                                                                 dragBoundsWidthSupplier,
                                                                                 dragBoundsHeightSupplier);

            animate(handle, AnimationProperty.Properties.RADIUS(R1), AnimationProperty.Properties.RADIUS(R0));

            return handle;
        }
    }

    static class MoveDividerControlHandle extends AbstractControlHandle {

        private final Circle controlShape;

        public MoveDividerControlHandle(final Circle controlShape,
                                        final IControlHandleList resizeControlHandles,
                                        final Supplier<Double> dragBoundsWidthSupplier,
                                        final Supplier<Double> dragBoundsHeightSupplier) {
            this.controlShape = controlShape;
            final MoveDividerDragHandler handler = new MoveDividerDragHandler(controlShape,
                                                                              resizeControlHandles,
                                                                              this,
                                                                              dragBoundsWidthSupplier,
                                                                              dragBoundsHeightSupplier);
            controlShape.setDragConstraints(handler);
            register(controlShape.addNodeDragEndHandler(handler));
        }

        @Override
        public IPrimitive<?> getControl() {
            return controlShape;
        }

        @Override
        public void destroy() {
            super.destroy();
        }

        @Override
        public ControlHandleType getType() {
            return ControlHandleStandardType.RESIZE;
        }
    }

    static class MoveDividerDragHandler implements DragConstraintEnforcer,
                                                   NodeDragEndHandler {

        private final Circle controlShape;
        private final IControlHandleList resizeControlHandles;
        private final MoveDividerControlHandle moveDividerControlHandle;
        private final Supplier<Double> dragBoundsWidthSupplier;
        private final Supplier<Double> dragBoundsHeightSupplier;
        private final DragConstraintEnforcer delegateDragConstraintEnforcer = new DefaultDragConstraintEnforcer();

        MoveDividerDragHandler(final Circle controlShape,
                               final IControlHandleList resizeControlHandles,
                               final MoveDividerControlHandle moveDividerControlHandle,
                               final Supplier<Double> dragBoundsWidthSupplier,
                               final Supplier<Double> dragBoundsHeightSupplier) {
            this.controlShape = controlShape;
            this.resizeControlHandles = resizeControlHandles;
            this.moveDividerControlHandle = moveDividerControlHandle;
            this.dragBoundsWidthSupplier = dragBoundsWidthSupplier;
            this.dragBoundsHeightSupplier = dragBoundsHeightSupplier;
        }

        @Override
        public void startDrag(final DragContext dragContext) {
            dragContext.getNode().setDragBounds(makeDragBounds());
            delegateDragConstraintEnforcer.startDrag(dragContext);

            if ((moveDividerControlHandle.isActive()) && (resizeControlHandles.isActive())) {
                controlShape.setFillColor(PointHandleDecorator.STROKE_COLOR);
                doSafeDraw();
            }
        }

        @Override
        public boolean adjust(final Point2D dxy) {
            return delegateDragConstraintEnforcer.adjust(dxy);
        }

        @Override
        public void onNodeDragEnd(final NodeDragEndEvent event) {
            if ((moveDividerControlHandle.isActive()) && (resizeControlHandles.isActive())) {
                controlShape.setFillColor(PointHandleDecorator.MAIN_COLOR);
                doSafeDraw();
            }
        }

        private void doSafeDraw() {
            //In Unit Tests the ControlShape is not attached to a Layer so draw() operations fail.
            Optional.ofNullable(controlShape.getLayer()).ifPresent(Layer::draw);
        }

        private DragBounds makeDragBounds() {
            final double width = dragBoundsWidthSupplier.get();
            final double height = dragBoundsHeightSupplier.get();
            return new DragBounds(0,
                                  GeneralRectangleDimensionsSet.DEFAULT_HEIGHT,
                                  width,
                                  height - GeneralRectangleDimensionsSet.DEFAULT_HEIGHT);
        }
    }
}
