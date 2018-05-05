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

package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.IDrawable;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.LayoutContainer;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresLayoutContainer;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.LienzoShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.BoundingBox;
import org.kie.workbench.common.stunner.core.client.shape.view.HasDragBounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

public class WiresShapeView<T>
        extends WiresShape
        implements LienzoShapeView<T>,
                   HasDragBounds<T> {

    private String uuid;

    public WiresShapeView(final MultiPath path) {
        this(path,
             null);
    }

    public WiresShapeView(final MultiPath path,
                          final LayoutContainer layoutContainer) {
        super(path,
              null != layoutContainer ? layoutContainer : new WiresLayoutContainer());
        setListening(false);
    }

    public Shape<?> getShape() {
        return getPath();
    }

    @Override
    public T setUUID(final String uuid) {
        this.uuid = uuid;
        WiresUtils.assertShapeUUID(this.getGroup(),
                                   uuid);
        return cast();
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
    public T setShapeLocation(final Point2D location) {
        setLocation(new com.ait.lienzo.client.core.types.Point2D(location.getX(),
                                                                 location.getY()));
        shapeMoved();
        return cast();
    }

    @Override
    public double getAlpha() {
        return getContainer().getAttributes().getAlpha();
    }

    @Override
    public T setAlpha(final double alpha) {
        getContainer().getAttributes().setAlpha(alpha);
        return cast();
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
        return cast();
    }

    @Override
    public double getFillAlpha() {
        return getShape().getFillAlpha();
    }

    @Override
    public T setFillAlpha(final double alpha) {
        getShape().setFillAlpha(alpha);
        return cast();
    }

    @Override
    public String getStrokeColor() {
        return getShape().getStrokeColor();
    }

    @Override
    public T setStrokeColor(final String color) {
        getShape().setStrokeColor(color);
        return cast();
    }

    @Override
    public double getStrokeAlpha() {
        return getShape().getStrokeAlpha();
    }

    @Override
    public T setStrokeAlpha(final double alpha) {
        getShape().setStrokeAlpha(alpha);
        return cast();
    }

    @Override
    public double getStrokeWidth() {
        return getShape().getStrokeWidth();
    }

    @Override
    public T setStrokeWidth(final double width) {
        getShape().setStrokeWidth(width);
        return cast();
    }

    @Override
    public T setDragEnabled(boolean draggable) {
        setDraggable(draggable);
        return cast();
    }

    @Override
    public T setDragBounds(final double x1,
                           final double y1,
                           final double x2,
                           final double y2) {
        if (null != getControl()) {
            getControl().setBoundsConstraint(new com.ait.lienzo.client.core.types.BoundingBox(x1,
                                                                                              y1,
                                                                                              x2,
                                                                                              y2));
        }
        return cast();
    }

    @Override
    public T unsetDragBounds() {
        if (null != getControl()) {
            getControl().setBoundsConstraint(null);
        }
        return cast();
    }

    @Override
    public T moveToTop() {
        consumeChildrenAndConnectors(IDrawable::moveToTop);
        return cast();
    }

    @Override
    public T moveToBottom() {
        consumeChildrenAndConnectors(IDrawable::moveToBottom);
        return cast();
    }

    @Override
    public T moveUp() {
        consumeChildrenAndConnectors(IDrawable::moveUp);
        return cast();
    }

    @Override
    public T moveDown() {
        consumeChildrenAndConnectors(IDrawable::moveDown);
        return cast();
    }

    @Override
    public BoundingBox getBoundingBox() {
        final com.ait.lienzo.client.core.types.BoundingBox bb = getGroup().getBoundingBox();
        return new BoundingBox(bb.getMinX(),
                               bb.getMinY(),
                               bb.getMaxX(),
                               bb.getMaxY());
    }

    @Override
    public void destroy() {
        super.destroy();
        unsetDragBounds();
    }

    public T setListening(final boolean listening) {
        getPath().setFillBoundsForSelection(listening);
        getPath().setListening(listening);
        return cast();
    }

    @Override
    public List<Shape<?>> getDecorators() {
        return Collections.singletonList(getShape());
    }

    void consumeChildrenAndConnectors(final Consumer<IDrawable> primConsumer) {
        // Move this shape.
        primConsumer.accept(getContainer());
        // Move child shapes.
        final NFastArrayList<WiresShape> childShapes = getChildShapes();
        if (null != childShapes) {
            for (int i = 0; i < childShapes.size(); i++) {
                final WiresShape shape = childShapes.get(i);
                if (shape instanceof WiresShapeView) {
                    ((WiresShapeView) shape).consumeChildrenAndConnectors(primConsumer);
                } else {
                    primConsumer.accept(shape.getContainer());
                }
            }
        }
        // Move connectors.
        if (null != getMagnets()) {
            for (int i = 0; i < getMagnets().size(); i++) {
                final WiresMagnet magnet = getMagnets().getMagnet(i);
                final NFastArrayList<WiresConnection> connections = magnet.getConnections();
                if (null != connections) {
                    for (int j = 0; j < connections.size(); j++) {
                        final WiresConnection connection = connections.get(j);
                        if (null != connection.getConnector()) {
                            primConsumer.accept(connection.getConnector().getGroup());
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private T cast() {
        return (T) this;
    }

    @Override
    public T setShadow(final String color,
                       final int blur,
                       final double offx,
                       final double offy) {
        getShape().setShadow(new Shadow(color,
                                        blur,
                                        offx,
                                        offx));
        return cast();
    }

    @Override
    public T removeShadow() {
        getShape().setShadow(null);
        return cast();
    }
}
