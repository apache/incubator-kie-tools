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
package org.kie.workbench.common.dmn.client.shape.view;

import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.AbstractDirectionalMultiPointShape;
import com.ait.lienzo.client.core.shape.Attributes;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.ShapeType;

public class DirectionalLine extends AbstractDirectionalMultiPointShape<DirectionalLine> {

    public DirectionalLine(final double x1,
                           final double y1,
                           final double x2,
                           final double y2) {
        this(new Point2D(x1,
                         y1),
             new Point2D(x2,
                         y2));
    }

    public DirectionalLine(final Point2D start,
                           final Point2D end) {
        this(new Point2DArray(start,
                              end));
    }

    public DirectionalLine(final Point2DArray points) {
        super(ShapeType.LINE);

        setControlPoints(points);
    }

    public DirectionalLine setControlPoints(final Point2DArray points) {
        getAttributes().setControlPoints(points);

        return refresh();
    }

    public Point2DArray getControlPoints() {
        return getAttributes().getControlPoints();
    }

    @Override
    public DirectionalLine refresh() {
        getPathPartList().clear();

        return this;
    }

    @Override
    @SuppressWarnings("unused")
    public Point2D adjustPoint(final double x,
                               final double y,
                               final double deltaX,
                               final double deltaY) {
        //DeltaX and DeltaY are not used by DM.
        return new Point2D(x, y);
    }

    @Override
    public boolean isControlPointShape() {
        return false;
    }

    @Override
    protected boolean fill(final Context2D context,
                           final Attributes attr,
                           final double alpha) {
        return false;
    }

    @Override
    public boolean parse(final Attributes attr) {
        final Point2DArray points = attr.getControlPoints();
        if (null != points && points.size() > 1) {
            final Point2D p1 = points.get(0);
            final double x1 = p1.getX();
            final double y1 = p1.getY();
            final Point2D p2 = points.get(points.size() - 1);
            final double x2 = p2.getX();
            final double y2 = p2.getY();
            getPathPartList()
                    .M(x1,
                       y1)
                    .L(x2,
                       y2);
            return true;
        }
        return false;
    }

    @Override
    public Point2D getTailOffsetPoint() {
        return getControlPoints().get(0);
    }

    @Override
    public Point2D getHeadOffsetPoint() {
        return getControlPoints().get(1);
    }

    @Override
    public DirectionalLine setPoint2DArray(final Point2DArray points) {
        return setControlPoints(points);
    }

    @Override
    public Point2DArray getPoint2DArray() {
        return getControlPoints();
    }

    @Override
    public List<Attribute> getBoundingBoxAttributes() {
        return getBoundingBoxAttributesComposed(Attribute.CONTROL_POINTS);
    }

    @Override
    public BoundingBox getBoundingBox() {
        if (getPathPartList().size() < 1
                && !parse(getAttributes())) {
            return new BoundingBox(0,
                                   0,
                                   0,
                                   0);
        }
        return getPathPartList().getBoundingBox();
    }
}
