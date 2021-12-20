/*
   Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.client.core.shape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.shared.core.types.ShapeType;
import jsinterop.annotations.JsProperty;

public abstract class AbstractDirectionalMultiPointShape<T extends AbstractDirectionalMultiPointShape<T> & IDirectionalMultiPointShape<T>> extends AbstractOffsetMultiPointShape<T> implements IDirectionalMultiPointShape<T> {

    @JsProperty
    private Direction headDirection;

    @JsProperty
    private Direction tailDirection;

    @JsProperty
    private double correctionOffset = LienzoCore.get().getDefaultConnectorOffset();

    protected AbstractDirectionalMultiPointShape(final ShapeType type) {
        super(type);
    }

    @Override
    public IDirectionalMultiPointShape<?> asDirectionalMultiPointShape() {
        return this;
    }

    @Override
    public Direction getHeadDirection() {
        return this.headDirection;
    }

    @Override
    public T setHeadDirection(final Direction direction) {
        this.headDirection = direction;

        return refresh();
    }

    @Override
    public Direction getTailDirection() {
        return this.tailDirection;
    }

    @Override
    public T setTailDirection(final Direction direction) {
        this.tailDirection = direction;

        return refresh();
    }

    public final T setCorrectionOffset(double offset) {
        this.correctionOffset = offset;

        return refresh();
    }

    public final double getCorrectionOffset() {
        return this.correctionOffset;
    }

    @Override
    protected List<Attribute> getBoundingBoxAttributesComposed(final List<Attribute> attributes) {
        final ArrayList<Attribute> list = new ArrayList<Attribute>(super.getBoundingBoxAttributesComposed(attributes));

        list.addAll(Arrays.asList(Attribute.HEAD_DIRECTION, Attribute.TAIL_DIRECTION));

        return list;
    }

    @Override
    public Point2D adjustPoint(double x, double y, double deltaX, double deltaY) {
        return new Point2D(x, y);
    }

    @Override
    public Shape<T> copyTo(Shape<T> other) {
        super.copyTo(other);
        ((IDirectionalMultiPointShape<T>) other).setHeadDirection(headDirection);
        ((IDirectionalMultiPointShape<T>) other).setTailDirection(tailDirection);
        ((IDirectionalMultiPointShape<T>) other).setCorrectionOffset(correctionOffset);

        return other;
    }
}
