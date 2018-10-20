/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ait.lienzo.client.widget.panel.scrollbars;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.tooling.common.api.java.util.function.BiPredicate;
import com.ait.tooling.common.api.java.util.function.Function;

import java.util.LinkedList;
import java.util.List;

class ScrollBounds
{
    private final ScrollablePanelHandler scrollHandler;

    ScrollBounds(final ScrollablePanelHandler scrollHandler)
    {
        this.scrollHandler = scrollHandler;
    }

    private List<Double> getBounds(Function<Bounds, Double> function)
    {
        final List<Double> result = new LinkedList<>();
        Bounds             bounds = scrollHandler.getPanel().getBounds();
        result.add(function.apply(bounds));
        return result;
    }

    double maxBoundX()
    {

        final List<Double> boundsValues = getBounds(new Function<Bounds, Double>()
        {
            @Override
            public Double apply(Bounds bounds)
            {
                return bounds.getX() + bounds.getWidth();
            }
        });

        return maxValue(boundsValues);
    }

    double maxBoundY()
    {

        final List<Double> boundsValues = getBounds(new Function<Bounds, Double>()
        {
            @Override
            public Double apply(Bounds bounds)
            {
                return bounds.getY() + bounds.getHeight();
            }
        });

        return maxValue(boundsValues);
    }

    double minBoundX()
    {

        final List<Double> boundsValues = getBounds(new Function<Bounds, Double>()
        {
            @Override
            public Double apply(Bounds bounds)
            {
                return bounds.getX();
            }
        });

        return minValueRelativeToCenter(boundsValues);
    }

    double minBoundY()
    {
        final List<Double> boundsValues = getBounds(new Function<Bounds, Double>()
        {
            @Override
            public Double apply(Bounds bounds)
            {
                return bounds.getY();
            }
        });

        return minValueRelativeToCenter(boundsValues);
    }

    Layer getLayer()
    {
        return scrollHandler.getLayer();
    }

    private static double obtainValue(final List<Double> boundsValues,
                                      final double defaultValue,
                                      final BiPredicate<Double, Double> predicate)
    {
        double result = defaultValue;
        for (Double value : boundsValues)
        {
            if (predicate.test(value, result))
            {
                result = value;
            }
        }
        return result;
    }

    private static double maxValue(final List<Double> boundsValues)
    {
        return obtainValue(boundsValues,
                           0d,
                           new BiPredicate<Double, Double>()
                           {
                               @Override
                               public boolean test(Double value, Double result)
                               {
                                   return value > result;
                               }
                           });
    }

    private static double minValueRelativeToCenter(final List<Double> boundsValues)
    {
        final double min = minValue(boundsValues);
        return min > 0 ? 0 : min;
    }

    private static double minValue(final List<Double> boundsValues)
    {
        return obtainValue(boundsValues,
                           Double.MAX_VALUE,
                           new BiPredicate<Double, Double>()
                           {
                               @Override
                               public boolean test(Double value, Double result)
                               {
                                   return value < result;
                               }
                           });
    }
}
