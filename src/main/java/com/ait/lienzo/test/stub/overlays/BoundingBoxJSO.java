/*
 * Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.
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

package com.ait.lienzo.test.stub.overlays;

import com.ait.lienzo.test.annotation.StubClass;
import com.google.gwt.core.client.JavaScriptObject;

@StubClass("com.ait.lienzo.client.core.types.BoundingBox$BoundingBoxJSO")
public class BoundingBoxJSO extends JavaScriptObject
{
    private double minx;

    private double miny;

    private double maxx;

    private double maxy;

    protected BoundingBoxJSO()
    {
    }

    protected BoundingBoxJSO(final double minx, final double miny, final double maxx, final double maxy)
    {
        this.minx = minx;
        this.miny = miny;
        this.maxx = maxx;
        this.maxy = maxy;
    }

    public static BoundingBoxJSO make(final double minx, final double miny, final double maxx, final double maxy)
    {
        return new BoundingBoxJSO(minx, miny, maxx, maxy);
    }

    public double getMinX()
    {
        return this.minx;
    }

    public double getMinY()
    {
        return this.miny;
    }

    public double getMaxX()
    {
        return this.maxx;
    }

    public double getMaxY()
    {
        return this.maxy;
    }

    public void addX(final double x)
    {
        if (x < this.minx)
        {
            this.minx = x;
        }
        if (x > this.maxx)
        {
            this.maxx = x;
        }
    }

    public void addY(final double y)
    {
        if (y < this.miny)
        {
            this.miny = y;
        }
        if (y > this.maxy)
        {
            this.maxy = y;
        }
    }
}