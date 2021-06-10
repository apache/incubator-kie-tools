/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.wires.OptionalBounds;
import com.ait.lienzo.client.core.types.BoundingBox;
import java.util.function.Supplier;

public class WiresShapeLocationBounds {

    private final Supplier<BoundingBox> absoluteShapeBounds;
    private       OptionalBounds        constraints;

    public WiresShapeLocationBounds(final Supplier<BoundingBox> absoluteShapeBounds) {
        this.absoluteShapeBounds = absoluteShapeBounds;
    }

    public WiresShapeLocationBounds setBounds(final OptionalBounds constraints) {
        this.constraints = constraints;
        return this;
    }

    public boolean isOutOfBounds(final double dx,
                                 final double dy) {
        if (null != constraints) {
            final BoundingBox shapeBB = absoluteShapeBounds.get();
            final double shapeMinX = shapeBB.getMinX() + dx;
            final double shapeMinY = shapeBB.getMinY() + dy;
            final double shapeMaxX = shapeMinX + (shapeBB.getMaxX() - shapeBB.getMinX());
            final double shapeMaxY = shapeMinY + (shapeBB.getMaxY() - shapeBB.getMinY());
            return constraints.lessOrEqualThanMinX(shapeMinX) ||
                    constraints.biggerOrEqualThanMaxX(shapeMaxX) ||
                    constraints.lessOrEqualThanMinY(shapeMinY) ||
                    constraints.biggerOrEqualThanMaxY(shapeMaxY);
        }
        return false;
    }

    public OptionalBounds getBounds() {
        return constraints;
    }

    public void clear() {
        constraints = null;
    }

}
