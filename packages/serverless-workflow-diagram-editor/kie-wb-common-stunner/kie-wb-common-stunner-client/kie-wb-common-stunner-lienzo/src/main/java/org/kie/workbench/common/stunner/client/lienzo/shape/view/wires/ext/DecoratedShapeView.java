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

package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.ControlHandleList;
import com.ait.lienzo.client.core.shape.wires.IControlHandle;
import com.ait.lienzo.client.core.shape.wires.WiresShapeControlHandleList;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.ViewEventHandlerManager;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresScalableContainer;
import org.kie.workbench.common.stunner.core.client.shape.view.HasSize;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;

/**
 * This Shape is an extension of WiresShapeViewExt, but instead
 * of the need of a multipath instance for constructing it, which is being used
 * for attaching the different control points to the view instance, it only
 * requires a single Lienzo shape instance that is being wrapped by an instance
 * of a multi-path.
 * So the internal multi-path instance "decorates" the given shape and the others
 * shape's children, if any, by providing a non visible square in which
 * the different control points.
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
    private final WiresScalableContainer scalableContainer;

    public DecoratedShapeView(final ViewEventType[] supportedEventTypes,
                              final WiresScalableContainer scalableContainer,
                              final Shape<?> theShape,
                              final double width,
                              final double height) {
        super(setupDecorator(new MultiPath(),
                             0,
                             0,
                             width,
                             height),
              scalableContainer);
        setEventHandlerManager(new ViewEventHandlerManager(getGroup(),
                                                           theShape,
                                                           supportedEventTypes));
        this.theShape = theShape;
        this.scalableContainer = scalableContainer;
        theShape.setFillBoundsForSelection(true);
        scalableContainer.addScalable(theShape);
        resizeDecorator(0,
                        0,
                        width,
                        height);
    }

    @Override
    public Shape<?> getShape() {
        return theShape;
    }

    @Override
    public Shape<?> getAttachableShape() {
        return getPath();
    }

    public DecoratedShapeView addScalableChild(final IPrimitive<?> child) {
        scalableContainer.addScalable(child);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setSize(final double width,
                     final double height) {
        // Ensure controls exist to perform resizing.
        loadControls(IControlHandle.ControlHandleStandardType.RESIZE);
        getControls().resize(0d,
                             0d,
                             width,
                             height);
        refresh();
        return cast();
    }

    @Override
    public T setMinWidth(Double minWidth) {
        getPath().setMinWidth(minWidth);
        return cast();
    }

    @Override
    public T setMaxWidth(Double maxWidth) {
        getPath().setMaxWidth(maxWidth);
        return cast();
    }

    @Override
    public T setMinHeight(Double minHeight) {
        getPath().setMinHeight(minHeight);
        return cast();
    }

    @Override
    public T setMaxHeight(Double maxHeight) {
        getPath().setMaxHeight(maxHeight);
        return cast();
    }

    @Override
    protected WiresShapeControlHandleList createControlHandles(final IControlHandle.ControlHandleType type,
                                                               final ControlHandleList controls) {
        return new DecoratedWiresShapeControlHandleList(type,
                                                        controls);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DecoratedWiresShapeControlHandleList getControls() {
        return (DecoratedWiresShapeControlHandleList) super.getControls();
    }

    @Override
    public void destroy() {
        scalableContainer.destroy();
        super.destroy();
    }

    void resizeDecorator(final double x,
                         final double y,
                         final double width,
                         final double height) {
        setupDecorator(getPath(),
                       x,
                       y,
                       width,
                       height);
    }

    private final class DecoratedWiresShapeControlHandleList extends WiresShapeControlHandleList {

        public DecoratedWiresShapeControlHandleList(final IControlHandle.ControlHandleType controlsType,
                                                    final ControlHandleList controls) {
            super(DecoratedShapeView.this,
                  controlsType,
                  controls);
        }

        public void resize(final Double x,
                           final Double y,
                           final double width,
                           final double height) {
            resize(x,
                   y,
                   width,
                   height,
                   true);
            updateControlPoints(ControlPointType.RESIZE);
        }

        @Override
        protected void resize(final Double x,
                              final Double y,
                              final double width,
                              final double height,
                              final boolean refresh) {
            // First let's resize the scalable container.
            DecoratedShapeView.this.resizeDecorator(null != x ? x : 0,
                                                    null != y ? y : 0,
                                                    width,
                                                    height);
            // Delegate the resize operation to the parent class.
            super.resize(x,
                         y,
                         width,
                         height,
                         refresh);
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
                .setStrokeAlpha(0);
    }
}
