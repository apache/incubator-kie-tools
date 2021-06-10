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

import java.util.HashMap;
import java.util.Map;

import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionLayout.Direction;
import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionLayout.HorizontalAlignment;
import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionLayout.Orientation;
import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionLayout.ReferencePosition;
import com.ait.lienzo.client.core.types.BoundingBox;
import java.util.function.Function;

public class HorizontalLayoutFactory
{

    private static final Map<ReferencePosition, DirectionLayoutBuilder<HorizontalAlignment>> BUILDERS = builders();

    private static Map<ReferencePosition, DirectionLayoutBuilder<HorizontalAlignment>> builders()
    {
        final Map<ReferencePosition, DirectionLayoutBuilder<HorizontalAlignment>> builders = new HashMap<>();
        builders.put(ReferencePosition.OUTSIDE, new OuterHorizontalLayoutBuilder());
        builders.put(ReferencePosition.INSIDE, new InnerHorizontalLayoutBuilder());
        return builders;
    }

    private HorizontalLayoutFactory()
    {

    }

    public static DirectionLayoutBuilder<HorizontalAlignment> get(ReferencePosition referencePosition)
    {
        return BUILDERS.get(referencePosition);
    }

    protected static final class OuterHorizontalLayoutBuilder implements DirectionLayoutBuilder<HorizontalAlignment>
    {
        @Override
        public Double apply(final BoundingBox parentBoundingBox, final BoundingBox childBoundingBox,
                final HorizontalAlignment alignment, final Orientation orientation,
                final Function<Direction, Double> margins)
        {
            switch (alignment)
            {

            case RIGHT:
                return parentBoundingBox.getMaxX() + margins.apply(HorizontalAlignment.RIGHT);

            case CENTER:
                return getCenter(parentBoundingBox, childBoundingBox, margins.apply(HorizontalAlignment.LEFT));

            case LEFT:
            default:
                return 0d - childBoundingBox.getWidth() - margins.apply(HorizontalAlignment.LEFT);
            }
        }
    }

    protected static final class InnerHorizontalLayoutBuilder implements DirectionLayoutBuilder<HorizontalAlignment>
    {
        @Override
        public Double apply(final BoundingBox parentBoundingBox, final BoundingBox childBoundingBox,
                final HorizontalAlignment alignment, final Orientation orientation,
                final Function<Direction, Double> margins)
        {
            switch (alignment)
            {

            case RIGHT:
                return parentBoundingBox.getMaxX() - childBoundingBox.getWidth() - margins
                        .apply(HorizontalAlignment.RIGHT);

            case CENTER:
                return getCenter(parentBoundingBox, childBoundingBox, margins.apply(HorizontalAlignment.LEFT));
            case LEFT:
            default:
                return 0d + margins.apply(HorizontalAlignment.LEFT);
            }
        }
    }

    private static double getCenter(final BoundingBox parentBoundingBox, final BoundingBox childBoundingBox,
            final double margin)
    {
        final double parentCenter = parentBoundingBox.getMinX() + (parentBoundingBox.getWidth() / 2);
        final double childCenter  = childBoundingBox.getWidth() / 2;
        final double x            = parentCenter - childCenter;
        return (x > margin || parentCenter < childCenter) ? x : margin;
    }
}
