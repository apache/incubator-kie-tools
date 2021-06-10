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
import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionLayout.Orientation;
import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionLayout.ReferencePosition;
import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionLayout.VerticalAlignment;
import com.ait.lienzo.client.core.types.BoundingBox;
import java.util.function.Function;

public class VerticalLayoutFactory
{
    private static final Map<ReferencePosition, DirectionLayoutBuilder<VerticalAlignment>> BUILDERS = builders();

    private static Map<ReferencePosition, DirectionLayoutBuilder<VerticalAlignment>> builders()
    {
        final Map<ReferencePosition, DirectionLayoutBuilder<VerticalAlignment>> builders = new HashMap<>();
        builders.put(ReferencePosition.OUTSIDE, new OuterVerticalLayoutBuilder());
        builders.put(ReferencePosition.INSIDE, new InnerVerticalLayoutBuilder());
        return builders;
    }

    private VerticalLayoutFactory()
    {

    }

    public static DirectionLayoutBuilder<VerticalAlignment> get(ReferencePosition referencePosition)
    {
        return BUILDERS.get(referencePosition);
    }

    protected static final class OuterVerticalLayoutBuilder implements DirectionLayoutBuilder<VerticalAlignment>
    {
        @Override
        public Double apply(final BoundingBox parentBoundingBox, final BoundingBox childBoundingBox,
                            final VerticalAlignment alignment, final Orientation orientation,
                            final Function<Direction, Double> margins)
        {
            switch (alignment)
            {
                case TOP:
                    return parentBoundingBox.getMinY() - childBoundingBox.getHeight() - margins
                            .apply(VerticalAlignment.TOP);
                case BOTTOM:
                    return parentBoundingBox.getMaxY() + margins.apply(VerticalAlignment.BOTTOM) + (Orientation.VERTICAL
                                                                                                            .equals(orientation) ? childBoundingBox.getWidth() : 0);
                case MIDDLE:
                default:
                    return getMiddle(parentBoundingBox, childBoundingBox, orientation,
                                     margins.apply(VerticalAlignment.TOP));
            }
        }
    }

    protected static final class InnerVerticalLayoutBuilder implements DirectionLayoutBuilder<VerticalAlignment>
    {
        @Override
        public Double apply(final BoundingBox parentBoundingBox, final BoundingBox childBoundingBox,
                            final VerticalAlignment alignment, final Orientation orientation,
                            final Function<Direction, Double> margins)
        {
            switch (alignment)
            {
                case TOP:
                    return parentBoundingBox.getMinY() + margins.apply(VerticalAlignment.TOP) + (Orientation.VERTICAL
                                                                                                         .equals(orientation) ? childBoundingBox.getWidth() : 0);
                case BOTTOM:
                    return parentBoundingBox.getMaxY() - childBoundingBox.getHeight() - margins
                            .apply(VerticalAlignment.BOTTOM) - (Orientation.VERTICAL.equals(orientation) ?
                                                                childBoundingBox.getWidth() :
                                                                0);
                case MIDDLE:
                default:
                    return getMiddle(parentBoundingBox, childBoundingBox, orientation,
                                     margins.apply(VerticalAlignment.TOP));
            }
        }
    }

    private static double getMiddle(final BoundingBox parentBoundingBox, final BoundingBox childBoundingBox,
                                    final Orientation orientation, final double margin)
    {
        double y = parentBoundingBox.getMinY() + (parentBoundingBox.getHeight() / 2) + (Orientation.VERTICAL
                                                                                                .equals(orientation) ? (childBoundingBox.getHeight() / 2) : -(childBoundingBox.getHeight() / 2));
        return y > margin ? y : margin;
    }
}
