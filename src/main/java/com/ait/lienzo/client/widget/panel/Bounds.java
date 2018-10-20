/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package com.ait.lienzo.client.widget.panel;

public class Bounds
{
    private double x;

    private double y;

    private double width;

    private double height;

    public static Bounds build(final double x,
                               final double y,
                               final double width,
                               final double height)
    {
        return new Bounds(x, y, width, height);
    }

    public static Bounds relativeBox(final double width,
                                     final double height)
    {
        return new Bounds(0d, 0d, width, height);
    }

    public static Bounds empty()
    {
        return new Bounds(0d, 0d, 0d, 0d);
    }

    private Bounds(final double x,
                   final double y,
                   final double width,
                   final double height)
    {
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
    }

    public double getX()
    {
        return x;
    }

    public Bounds setX(double x)
    {
        this.x = x;
        return this;
    }

    public double getY()
    {
        return y;
    }

    public Bounds setY(double y)
    {
        this.y = y;
        return this;
    }

    public double getWidth()
    {
        return width;
    }

    public Bounds setWidth(double width)
    {
        if (!(width >= 0))
        {
            throw new IllegalStateException("Width must be positive");
        }
        this.width = width;
        return this;
    }

    public double getHeight()
    {
        return height;
    }

    public Bounds setHeight(double height)
    {
        if (!(height >= 0))
        {
            throw new IllegalStateException("Height must be positive");
        }
        this.height = height;
        return this;
    }

    @Override public String toString()
    {
        return "Bounds [" + x + ", " + y + ", " + width + ", " + height + "]";
    }
}
