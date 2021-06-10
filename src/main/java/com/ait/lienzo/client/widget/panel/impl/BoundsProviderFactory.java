/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package com.ait.lienzo.client.widget.panel.impl;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.client.widget.panel.BoundsProvider;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import java.util.function.Function;

public class BoundsProviderFactory
{
    private BoundsProviderFactory()
    {

    }

    public static class PrimitivesBoundsProvider extends FunctionalBoundsProvider<PrimitivesBoundsProvider>
    {
        @Override
        public NFastArrayList<BoundingBox> getAll(final Layer layer)
        {
            NFastArrayList<BoundingBox>   result = new NFastArrayList<>();
            NFastArrayList<IPrimitive<?>> shapes = layer.getChildNodes();
            if (null != shapes)
            {
                for (IPrimitive<?> shape : shapes.asList())
                {
                    BoundingBox boundingBox = shape.getComputedBoundingPoints().getBoundingBox();
                    result.add(boundingBox);
                }
            }
            return result;
        }
    }

    public static class WiresBoundsProvider extends FunctionalBoundsProvider<WiresBoundsProvider>
    {
        @Override
        public NFastArrayList<BoundingBox> getAll(final Layer layer)
        {
            final WiresLayer wiresLayer = WiresManager.get(layer).getLayer();
            return getAll(wiresLayer);
        }

        public NFastArrayList<BoundingBox> getAll(final WiresLayer wiresLayer)
        {
            final NFastArrayList<BoundingBox> result      = new NFastArrayList<>();
            final NFastArrayList<WiresShape>  childShapes = wiresLayer.getChildShapes();
            if (null != childShapes)
            {
                for (WiresShape shape : childShapes.asList())
                {
                    final Point2D     location    = shape.getLocation();
                    final BoundingBox boundingBox = shape.getGroup().getBoundingBox();
                    result.add(BoundingBox.fromDoubles(location.getX(),
                                                       location.getY(),
                                                       location.getX() + boundingBox.getWidth(),
                                                       location.getY() + boundingBox.getHeight()));
                }
            }
            return result;
        }
    }

    public abstract static class FunctionalBoundsProvider<T extends FunctionalBoundsProvider>
            implements BoundsProvider
    {
        public static final double                        PADDING = 25d;

        private             Function<BoundingBox, Bounds> boundsBuilder;

        private             double                        padding;

        protected FunctionalBoundsProvider()
        {
            this.padding = PADDING;
            this.boundsBuilder = boundingBox -> buildBounds(boundingBox);
        }

        public T setBoundsBuilder(final Function<BoundingBox, Bounds> boundsBuilder)
        {
            this.boundsBuilder = boundsBuilder;
            return cast();
        }

        public T setPadding(final double value)
        {
            this.padding = value;
            return cast();
        }

        public abstract NFastArrayList<BoundingBox> getAll(Layer layer);

        @Override
        public Bounds get(Layer layer)
        {
            if (null != layer)
            {
                final NFastArrayList<BoundingBox> boxes = getAll(layer);
                if (boxes != null && !boxes.isEmpty())
                {
                    return build(boxes);
                }
            }
            return Bounds.empty();
        }

        public Bounds build(final NFastArrayList<BoundingBox> boxes)
        {
            if (null != boxes)
            {
                final BoundingBox result = new BoundingBox();
                result.add(0, 0);
                for (BoundingBox box : boxes.asList())
                {
                    result.addBoundingBox(box);
                }
                if (result.getMinX() < 0)
                {
                    result.addX(result.getMinY() - padding);
                }
                if (result.getMinY() < 0)
                {
                    result.addY(result.getMinY() - padding);
                }
                result.add(result.getMaxX() + padding,
                           result.getMaxY() + padding);
                return boundsBuilder.apply(result);
            }
            return Bounds.empty();
        }

        @SuppressWarnings("unchecked")
        private T cast()
        {
            return (T) this;
        }
    }

    public static Bounds join(Bounds b1, Bounds b2)
    {
        if (null != b1 && null == b2)
        {
            return b1;
        }
        if (null != b2 && null == b1)
        {
            return b2;
        }
        if (null == b1)
        {
            return Bounds.empty();
        }
        BoundingBox boundsBB = BoundingBox.fromDoubles(b1.getX(),
                                                       b1.getY(),
                                                       b1.getX() + b1.getWidth(),
                                                       b1.getY() + b1.getHeight());
        BoundingBox visibleBB = BoundingBox.fromDoubles(b2.getX(),
                                                        b2.getY(),
                                                        b2.getX() + b2.getWidth(),
                                                        b2.getY() + b2.getHeight());
        BoundingBox result = boundsBB.addBoundingBox(visibleBB);
        return Bounds.build(result.getX(),
                            result.getY(),
                            result.getWidth(),
                            result.getHeight());
    }

    public static Bounds buildBounds(final BoundingBox box)
    {
        final double x      = box.getX();
        final double y      = box.getY();
        final double width  = box.getWidth();
        final double height = box.getHeight();
        return Bounds.build(x,
                            y,
                            width,
                            height);
    }

    public static double computeWidth(final double ratio,
                                      final double height)
    {
        return height * ratio;
    }

    public static int computeWidth(final double ratio,
                                   final int height)
    {
        return (int) computeWidth(ratio, (double) height);
    }

    public static double computeHeight(final double ratio,
                                       final double width)
    {
        return width * (1 / ratio);
    }

    public static int computeHeight(final double ratio,
                                    final int width)
    {
        return (int) computeHeight(ratio, (double) width);
    }

    public static Bounds computeBoundsAspectRatio(final double ratio,
                                                  final BoundingBox box)
    {
        final double x      = box.getX();
        final double y      = box.getY();
        final double width  = box.getWidth();
        final double height = box.getHeight();
        final double cr     = width / height;
        double       rw     = width;
        double       rh     = height;
        if (cr > ratio)
        {
            rh = computeHeight(ratio, width);
        }
        else
        {
            rw = computeWidth(ratio, height);
        }
        return Bounds.build(x,
                            y,
                            rw,
                            rh);
    }
}
