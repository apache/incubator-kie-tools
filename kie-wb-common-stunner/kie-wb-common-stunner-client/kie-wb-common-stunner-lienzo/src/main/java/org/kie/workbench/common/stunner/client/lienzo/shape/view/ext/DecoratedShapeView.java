/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.shape.view.ext;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.ControlHandleList;
import com.ait.lienzo.client.core.shape.wires.IControlHandle;
import com.ait.lienzo.client.core.shape.wires.LayoutContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShapeControlHandleList;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.ColorName;
import org.kie.workbench.common.stunner.core.client.shape.view.HasSize;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;

/**
 * This Shape is an extension of WiresShapeViewExt, but instead
 * of the need of a multipath instance for constructing it, which is being used
 * for attaching the different control points to the view instance, it only
 * requires a single Lienzo shape instance that is being wrapped by an instance
 * of a multi-path.
 * So the internal multi-path instance "decorates" the given shape and childre, if
 * any, by providing a non visible square in which the different control points.
 * <p>
 * This way any kind of path or primitive instance can be added
 * as child for this shape and even if that instance cannot be resized
 * or supports some control point handler, due to it's state, the already
 * provided multi-path instance is used for these goals.
 * <p>
 * When scaling this shape, it scales all children for fitting the given
 * new size, and the multi-path instance is rebuild to provide the right
 * magnets and control points.
 */
public class DecoratedShapeView<T extends WiresShapeViewExt>
        extends WiresShapeViewExt<T>
        implements HasSize<T> {

    private final Shape<?> theShape;
    protected final Group transformableContainer = new Group();
    protected double width = 0;
    protected double height = 0;

    public DecoratedShapeView(final ViewEventType[] supportedEventTypes,
                              final LayoutContainer layoutContainer,
                              final Shape<?> theShape,
                              final double width,
                              final double height) {
        super(supportedEventTypes,
              setupDecorator(new MultiPath(),
                             0,
                             0,
                             width,
                             height),
              layoutContainer);
        this.theShape = theShape;
        this.theShape.setFillBoundsForSelection(true);
        initializeHandlerManager(getGroup(),
                                 theShape,
                                 supportedEventTypes);
        initializeTextView();
        getGroup().add(transformableContainer);
        transformableContainer.add(theShape);
        resize(0,
               0,
               width,
               height,
               false);
    }

    @Override
    public Shape<?> getShape() {
        return theShape;
    }

    @Override
    public Shape<?> getAttachableShape() {
        return theShape;
    }

    public DecoratedShapeView addScalableChild(final IPrimitive<?> child) {
        transformableContainer.add(child);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setSize(final double width,
                     final double height) {
        resize(0,
               0,
               width,
               height,
               true);
        return (T) this;
    }

    @Override
    protected WiresShapeControlHandleList createControlHandles(final IControlHandle.ControlHandleType type,
                                                               final ControlHandleList controls) {
        return new DecoratedWiresShapeControlHandleList(type,
                                                        controls);
    }

    @Override
    protected void initialize(final ViewEventType[] supportedEventTypes) {
        // Initialize handlers for the primitive shape instead that on the path, as parent does.
    }

    void resize(final double x,
                final double y,
                final double width,
                final double height,
                final boolean refresh) {
        // Avoid recurrent calls, if any.
        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;
            final BoundingBox bb = transformableContainer.getBoundingBox();
            final double sx = width / bb.getWidth();
            final double sy = height / bb.getHeight();
            setupDecorator(getPath(),
                           x,
                           y,
                           width,
                           height);
            transformableContainer.setX(x).setY(y).setScale(sx,
                                                            sy);
        }
        if (refresh) {
            refresh();
        }
    }

    private final class DecoratedWiresShapeControlHandleList extends WiresShapeControlHandleList {

        public DecoratedWiresShapeControlHandleList(final IControlHandle.ControlHandleType controlsType,
                                                    final ControlHandleList controls) {
            super(DecoratedShapeView.this,
                  controlsType,
                  controls);
        }

        @Override
        protected void resize(final Double x,
                              final Double y,
                              final double width,
                              final double height,
                              final boolean refresh) {
            super.resize(x,
                         y,
                         width,
                         height,
                         refresh);
            DecoratedShapeView.this.resize(null != x ? x : 0,
                                           null != y ? y : 0,
                                           width,
                                           height,
                                           false);
        }
    }

    private static MultiPath setupDecorator(final MultiPath path,
                                            final double x,
                                            final double y,
                                            final double width,
                                            final double height) {
        return path.clear().rect(x,
                                 y,
                                 width,
                                 height)
                .setStrokeColor(ColorName.BLACK)
                .setStrokeAlpha(0)
                .setFillAlpha(0.001);
    }
}