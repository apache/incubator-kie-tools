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

import java.util.List;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.shared.core.types.ShapeType;
import jsinterop.annotations.JsProperty;

public abstract class AbstractOffsetMultiPointShape<T extends AbstractOffsetMultiPointShape<T> & IOffsetMultiPointShape<T>> extends AbstractMultiPointShape<T> implements IOffsetMultiPointShape<T> {

    @JsProperty
    private double headOffset;

    @JsProperty
    private double tailOffset;

    protected AbstractOffsetMultiPointShape(final ShapeType type) {
        super(type);
    }

    @Override
    public IOffsetMultiPointShape<?> asOffsetMultiPointShape() {
        return this;
    }

    public double getTailOffset() {
        return this.tailOffset;
    }

    public T setTailOffset(final double offset) {
        this.tailOffset = offset;

        return refresh();
    }

    public double getHeadOffset() {
        return this.headOffset;
    }

    public T setHeadOffset(final double offset) {
        this.headOffset = offset;

        return refresh();
    }

    protected List<Attribute> getBoundingBoxAttributesComposed(final Attribute... compose) {
        return getBoundingBoxAttributesComposed(asAttributes(compose));
    }

    protected List<Attribute> getBoundingBoxAttributesComposed(final List<Attribute> attributes) {
        return asAttributes(attributes, Attribute.HEAD_OFFSET, Attribute.TAIL_OFFSET);
    }

    @Override
    protected boolean prepare(final Context2D context, final double alpha) {
        final boolean prepared = isPathPartListPrepared();

        if (prepared) {
            context.path(getPathPartList());
        }
        return prepared;
    }

    public boolean isPathPartListPrepared() {
        if (getPathPartList().size() < 1) {
            return parse();
        }

        return true;
    }

    public abstract boolean parse();
}
