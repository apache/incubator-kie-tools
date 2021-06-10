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

package com.ait.lienzo.client.core.types;

import elemental2.core.Global;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType
public class PathPartEntryJSO
{
    public static final int UNDEFINED_PATH_PART        = 0;

    public static final int LINETO_ABSOLUTE            = 1;

    public static final int MOVETO_ABSOLUTE            = 2;

    public static final int BEZIER_CURVETO_ABSOLUTE    = 3;

    public static final int QUADRATIC_CURVETO_ABSOLUTE = 4;

    public static final int ARCTO_ABSOLUTE             = 5;

    public static final int CLOSE_PATH_PART            = 6;

    public static final int CANVAS_ARCTO_ABSOLUTE      = 7;

    @JsProperty
    private int command;

    @JsProperty
    private double[] points;

    public static final PathPartEntryJSO make(int command, double[] points)
    {
        return new PathPartEntryJSO(command, points);
    }

    public PathPartEntryJSO(int command, double[] points)
    {
        this.command = command;
        this.points = points;
    }

    public final String toJSONString()
    {
        return Global.JSON.stringify(this);
    }

    public final int getCommand()
    {
        return this.command;
    }

    public final double[] getPoints()
    {
        return this.points;
    }

    public final PathPartEntryJSO copy()
    {
        int command = getCommand();

        double[] cp = new double[points.length];
        for (int i = 0; i < points.length; i++ )
        {
            cp[i] = points[i];
        }

        return make(command, points);
    }
}