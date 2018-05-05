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

package org.kie.workbench.common.stunner.svg.client.shape.view;

import java.util.Collections;
import java.util.List;

import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.LayoutContainer;
import com.ait.lienzo.client.core.types.Shadow;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.LienzoShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.BoundingBox;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

public final class SVGPrimitiveShape
        extends SVGPrimitive<Shape<?>>
        implements LienzoShapeView<SVGPrimitiveShape> {

    public SVGPrimitiveShape(Shape<?> shape) {
        this(shape,
             false,
             null);
    }

    public SVGPrimitiveShape(Shape<?> shape,
                             boolean scalable,
                             LayoutContainer.Layout layout) {
        super(shape,
              scalable,
              layout);
    }

    @Override
    public SVGPrimitiveShape setUUID(final String uuid) {
        get().setID(uuid);
        return this;
    }

    @Override
    public String getUUID() {
        return get().getID();
    }

    @Override
    public double getShapeX() {
        return get().getX();
    }

    @Override
    public double getShapeY() {
        return get().getY();
    }

    @Override
    public Point2D getShapeAbsoluteLocation() {
        final com.ait.lienzo.client.core.types.Point2D location = get().getAbsoluteLocation();
        return Point2D.create(location.getX(),
                              location.getY());
    }

    @Override
    public SVGPrimitiveShape setShapeLocation(final Point2D location) {
        get().setLocation(new com.ait.lienzo.client.core.types.Point2D(location.getX(),
                                                                       location.getY()));
        return this;
    }

    @Override
    public double getAlpha() {
        return get().getAlpha();
    }

    @Override
    public SVGPrimitiveShape setAlpha(final double alpha) {
        get().setAlpha(alpha);
        return this;
    }

    @Override
    public String getFillColor() {
        return get().getFillColor();
    }

    @Override
    public SVGPrimitiveShape setFillColor(final String color) {
        get().setFillColor(color);
        return this;
    }

    @Override
    public double getFillAlpha() {
        return get().getFillAlpha();
    }

    @Override
    public SVGPrimitiveShape setFillAlpha(final double alpha) {
        get().setFillAlpha(alpha);
        return this;
    }

    @Override
    public String getStrokeColor() {
        return get().getStrokeColor();
    }

    @Override
    public SVGPrimitiveShape setStrokeColor(final String color) {
        get().setStrokeColor(color);
        return this;
    }

    @Override
    public double getStrokeAlpha() {
        return get().getStrokeAlpha();
    }

    @Override
    public SVGPrimitiveShape setStrokeAlpha(final double alpha) {
        get().setStrokeAlpha(alpha);
        return this;
    }

    @Override
    public double getStrokeWidth() {
        return get().getStrokeWidth();
    }

    @Override
    public SVGPrimitiveShape setStrokeWidth(final double width) {
        get().setStrokeWidth(width);
        return this;
    }

    @Override
    public SVGPrimitiveShape setDragEnabled(final boolean draggable) {
        get().setDraggable(draggable);
        return this;
    }

    @Override
    public SVGPrimitiveShape moveToTop() {
        get().moveToTop();
        return this;
    }

    @Override
    public SVGPrimitiveShape moveToBottom() {
        get().moveToBottom();
        return this;
    }

    @Override
    public SVGPrimitiveShape moveUp() {
        get().moveUp();
        return this;
    }

    @Override
    public SVGPrimitiveShape moveDown() {
        get().moveDown();
        return this;
    }

    @Override
    public BoundingBox getBoundingBox() {
        final com.ait.lienzo.client.core.types.BoundingBox bb = get().getBoundingBox();
        return new BoundingBox(bb.getMinX(),
                               bb.getMinY(),
                               bb.getMaxX(),
                               bb.getMaxY());
    }

    @Override
    public void removeFromParent() {
        get().removeFromParent();
    }

    @Override
    public List<Shape<?>> getDecorators() {
        return Collections.singletonList(get());
    }

    @Override
    public SVGPrimitiveShape setShadow(final String color,
                                       final int blur,
                                       final double offx,
                                       final double offy) {
        get().setShadow(new Shadow(color,
                                   blur,
                                   offx,
                                   offy));
        return this;
    }

    @Override
    public SVGPrimitiveShape removeShadow() {
        get().setShadow(null);
        return this;
    }
}
