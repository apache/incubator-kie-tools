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
package com.ait.lienzo.client.core.shape.wires.layout.label;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.layout.AbstractContainerLayout;
import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionContainerLayout;
import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionLayout.Orientation;
import com.ait.lienzo.client.core.shape.wires.layout.size.IMaxSizeLayout;
import com.ait.lienzo.client.core.shape.wires.layout.size.SizeConstraintsContainerLayout;
import com.ait.lienzo.client.core.types.BoundingBox;

public class LabelContainerLayout extends AbstractContainerLayout<LabelLayout> implements IMaxSizeLayout<LabelLayout>
{
    private final SizeConstraintsContainerLayout m_sizeConstraintsContainerLayout;

    private final DirectionContainerLayout       m_directionContainerLayout;

    public LabelContainerLayout(final IPrimitive parent)
    {
        this(parent, new SizeConstraintsContainerLayout(parent), new DirectionContainerLayout(parent));
    }

    protected LabelContainerLayout(final IPrimitive parent, final SizeConstraintsContainerLayout sizeConstraintsContainerLayout, final DirectionContainerLayout directionContainerLayout)
    {
        super(parent);
        m_sizeConstraintsContainerLayout = sizeConstraintsContainerLayout;
        m_directionContainerLayout = directionContainerLayout;
    }

    @Override
    public LabelContainerLayout add(final IPrimitive<?> child, final LabelLayout layout)
    {
        if (child == null)
        {
            throw new IllegalArgumentException("Child should not be null");
        }

        final LabelLayout currentLayout = getLayout(layout);

        m_sizeConstraintsContainerLayout.add(child, currentLayout.getSizeConstraints());
        m_directionContainerLayout.add(child, currentLayout.getDirectionLayout());
        super.add(child, currentLayout);
        return this;
    }

    @Override
    public BoundingBox getMaxSize(final IPrimitive<?> child)
    {

        final LabelLayout layout = getLayout(child);
        if (layout == null)
        {
            return new BoundingBox();
        }
        final Orientation orientation = layout.getDirectionLayout().getOrientation();
        final BoundingBox boundaries  = m_sizeConstraintsContainerLayout.getMaxSize(child);
        switch (orientation)
        {
            case VERTICAL:
                return BoundingBox.fromDoubles(boundaries.getMinY(), boundaries.getMinX(), boundaries.getMaxY(),
                                               boundaries.getMaxX());
            case HORIZONTAL:
            default:
                return boundaries;
        }
    }

    @Override
    public LabelLayout getDefaultLayout()
    {
        return new LabelLayout.Builder().build();
    }
}