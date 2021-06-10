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
package com.ait.lienzo.client.core.shape.wires.layout.direction;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.layout.AbstractContainerLayout;
import com.ait.lienzo.client.core.shape.wires.layout.IContainerLayout;
import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionLayout.Direction;
import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionLayout.Orientation;
import com.ait.lienzo.client.core.types.BoundingBox;
import java.util.function.Function;

public class DirectionContainerLayout extends AbstractContainerLayout<DirectionLayout>
{
    public DirectionContainerLayout(final IPrimitive parent)
    {
        super(parent);
    }

    private BoundingBox getChildBoundingBox(final IPrimitive<?> child, Orientation orientation)
    {
        BoundingBox childBoundingBox = child.getBoundingBox();
        return (Orientation.VERTICAL.equals(orientation)) ?
               BoundingBox.fromDoubles(childBoundingBox.getMinY(), childBoundingBox.getMinX(), childBoundingBox.getHeight(),
                                       childBoundingBox.getWidth()) :
               childBoundingBox;
    }

    public IPrimitive get() {
        return child;
    }

    IPrimitive child;

    @Override
    public IContainerLayout add(final IPrimitive child, final DirectionLayout layout)
    {
        if (child == null)
        {
            throw new IllegalArgumentException("Child should not be null");
        }

        this.child = child;


        final DirectionLayout currentLayout = getLayout(layout);

        final BoundingBox childBoundingBox  = getChildBoundingBox(child, currentLayout.getOrientation());
        final BoundingBox parentBoundingBox = getParentBoundingBox();

        final Function<Direction, Double> margins = currentLayout::getMargin;

        //Horizontal Alignment
        setHorizontalAlignment(child, HorizontalLayoutFactory.get(currentLayout.getReferencePosition())
                .apply(parentBoundingBox, childBoundingBox, currentLayout.getHorizontalAlignment(),
                       currentLayout.getOrientation(), margins));
        //Vertical Alignment
        setVerticalAlignment(child, VerticalLayoutFactory.get(currentLayout.getReferencePosition())
                .apply(parentBoundingBox, childBoundingBox, currentLayout.getVerticalAlignment(),
                       currentLayout.getOrientation(), margins));

        return super.add(child, currentLayout);
    }

    protected void setHorizontalAlignment(IPrimitive child, double x) {
        child.setX(x);
    }

    protected void setVerticalAlignment(IPrimitive child, double y) {
        child.setY(y);
    }

    @Override
    public DirectionLayout getDefaultLayout()
    {
        return new DirectionLayout.Builder().build();
    }
}