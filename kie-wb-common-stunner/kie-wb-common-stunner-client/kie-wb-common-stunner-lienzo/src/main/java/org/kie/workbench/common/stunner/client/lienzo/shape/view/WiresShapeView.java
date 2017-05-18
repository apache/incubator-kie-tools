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

package org.kie.workbench.common.stunner.client.lienzo.shape.view;

import java.util.Collections;
import java.util.List;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.LayoutContainer;
import com.ait.lienzo.client.core.shape.wires.WiresLayoutContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.DragBounds;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.core.client.shape.view.HasDecorators;
import org.kie.workbench.common.stunner.core.client.shape.view.HasDragBounds;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.lienzo.core.shape.wires.WiresDragConstraintEnforcer;

public class WiresShapeView<T> extends WiresShape
        implements
        ShapeView<T>,
        HasDragBounds<T>,
        HasDecorators<Shape<?>> {

    private String uuid;
    private WiresDragConstraintEnforcer dragEnforcer;

    public WiresShapeView(final MultiPath path) {
        this(path,
             null);
    }

    public WiresShapeView(final MultiPath path,
                          final LayoutContainer layoutContainer) {
        super(path,
              null != layoutContainer ? layoutContainer : new WiresLayoutContainer());
        // Ensure path bounds are available on the selection context.
        path.setFillBoundsForSelection(true);
    }

    public Shape<?> getShape() {
        return getPath();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setUUID(final String uuid) {
        this.uuid = uuid;
        WiresUtils.assertShapeUUID(this.getGroup(),
                                   uuid);
        return (T) this;
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    public double getShapeX() {
        return getContainer().getAttributes().getX();
    }

    @Override
    public double getShapeY() {
        return getContainer().getAttributes().getY();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setShapeX(final double x) {
        getContainer().getAttributes().setX(x);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setShapeY(final double y) {
        getContainer().getAttributes().setY(y);
        return (T) this;
    }

    @Override
    public double getAlpha() {
        return getContainer().getAttributes().getAlpha();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setAlpha(final double alpha) {
        getContainer().getAttributes().setAlpha(alpha);
        return (T) this;
    }

    @Override
    public Point2D getShapeAbsoluteLocation() {
        return WiresUtils.getAbsolute(getContainer());
    }

    @Override
    public String getFillColor() {
        return getShape().getFillColor();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setFillColor(final String color) {
        getShape().setFillColor(color);
        return (T) this;
    }

    @Override
    public double getFillAlpha() {
        return getShape().getFillAlpha();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setFillAlpha(final double alpha) {
        getShape().setFillAlpha(alpha);
        return (T) this;
    }

    @Override
    public String getStrokeColor() {
        return getShape().getStrokeColor();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setStrokeColor(final String color) {
        getShape().setStrokeColor(color);
        return (T) this;
    }

    @Override
    public double getStrokeAlpha() {
        return getShape().getStrokeAlpha();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setStrokeAlpha(final double alpha) {
        getShape().setStrokeAlpha(alpha);
        return (T) this;
    }

    @Override
    public double getStrokeWidth() {
        return getShape().getStrokeWidth();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setStrokeWidth(final double width) {
        getShape().setStrokeWidth(width);
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setDragBounds(final double x1,
                           final double y1,
                           final double x2,
                           final double y2) {
        final DragBounds dragBounds = new DragBounds(x1,
                                                     y1,
                                                     x2,
                                                     y2);
        if (null == dragEnforcer) {
            dragEnforcer = WiresDragConstraintEnforcer.enforce(this,
                                                               dragBounds);
        } else {
            dragEnforcer.setDragBounds(dragBounds);
        }
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T unsetDragBounds() {
        if (null != dragEnforcer) {
            dragEnforcer.remove();
            dragEnforcer = null;
        }
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T moveToTop() {
        getContainer().moveToTop();
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T moveToBottom() {
        getContainer().moveToBottom();
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T moveUp() {
        getContainer().moveUp();
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T moveDown() {
        getContainer().moveDown();
        return (T) this;
    }

    @Override
    public void destroy() {
        super.destroy();
        unsetDragBounds();
    }

    public WiresDragConstraintEnforcer getDragEnforcer() {
        return dragEnforcer;
    }

    @Override
    public List<Shape<?>> getDecorators() {
        return Collections.singletonList(getShape());
    }
}
