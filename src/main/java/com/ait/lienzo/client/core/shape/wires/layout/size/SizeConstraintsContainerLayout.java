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
package com.ait.lienzo.client.core.shape.wires.layout.size;

import java.util.HashMap;
import java.util.Map;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.layout.AbstractContainerLayout;
import com.ait.lienzo.client.core.shape.wires.layout.size.SizeConstraints.Type;
import com.ait.lienzo.client.core.types.BoundingBox;
import java.util.function.BiFunction;

public class SizeConstraintsContainerLayout extends AbstractContainerLayout<SizeConstraints>
        implements IMaxSizeLayout<SizeConstraints>
{
    private static final Map<Type, BiFunction<SizeConstraints, BoundingBox, BoundingBox>> SIZE_BUILDERS = sizeBuilders();

    private static Map<Type, BiFunction<SizeConstraints, BoundingBox, BoundingBox>> sizeBuilders()
    {
        final Map<Type, BiFunction<SizeConstraints, BoundingBox, BoundingBox>> sizeBuilders = new HashMap<>();
        sizeBuilders.put(Type.RAW, (sizeConstraints, parentBoundingBox) -> BoundingBox.fromDoubles(0, 0, sizeConstraints.getWidth() - sizeConstraints.getMarginX(),
                                                                                           sizeConstraints.getHeight() - sizeConstraints.getMarginY()));
        sizeBuilders.put(Type.PERCENTAGE, (sizeConstraints, parentBoundingBox) -> {
            double width = sizeConstraints.getWidth() * (parentBoundingBox.getWidth() / 100) - sizeConstraints
                    .getMarginX();
            double height = sizeConstraints.getHeight() * (parentBoundingBox.getHeight() / 100) - sizeConstraints
                    .getMarginY();
            return BoundingBox.fromDoubles(0, 0, width, height);
        });
        return sizeBuilders;
    }

    public SizeConstraintsContainerLayout(final IPrimitive parentBoundingBox)
    {
        super(parentBoundingBox);
    }

    @Override
    public BoundingBox getMaxSize(final IPrimitive<?> child)
    {
        final SizeConstraints layout = getLayout(child);
        if (layout == null)
        {
            return new BoundingBox();
        }
        return SIZE_BUILDERS.get(layout.getType()).apply(layout, getParentBoundingBox());
    }

    @Override
    public SizeConstraints getDefaultLayout()
    {
        return new SizeConstraints(100, 100, Type.PERCENTAGE);
    }
}
