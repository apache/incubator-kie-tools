/*
 * Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.
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
import com.ait.tooling.nativetools.client.collection.NFastDoubleArrayJSO;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

/**
 * In-memory array implementation stub for class <code>com.ait.lienzo.client.core.types.PathPartEntryJSO</code>.
 * 
 * @author Roger Martinez
 * @since 1.0
 * 
 */
@StubClass("com.ait.lienzo.client.core.types.PathPartEntryJSO")
public class PathPartEntryJSO extends JavaScriptObject
{
    public static final int     UNDEFINED_PATH_PART        = 0;

    public static final int     LINETO_ABSOLUTE            = 1;

    public static final int     MOVETO_ABSOLUTE            = 2;

    public static final int     BEZIER_CURVETO_ABSOLUTE    = 3;

    public static final int     QUADRATIC_CURVETO_ABSOLUTE = 4;

    public static final int     ARCTO_ABSOLUTE             = 5;

    public static final int     CLOSE_PATH_PART            = 6;

    public static final int     CANVAS_ARCTO_ABSOLUTE      = 7;

    private int                 command;

    private NFastDoubleArrayJSO arrayJSO;

    public static PathPartEntryJSO make(int c, NFastDoubleArrayJSO p)
    {
        return new PathPartEntryJSO(c, p);
    }

    public PathPartEntryJSO()
    {
    }

    protected PathPartEntryJSO(int c, NFastDoubleArrayJSO p)
    {
        this.command = c;

        this.arrayJSO = p;
    }

    public String toJSONString()
    {
        return new JSONObject(this).toString();
    }

    public int getCommand()
    {
        return command;
    }

    public NFastDoubleArrayJSO getPoints()
    {
        return arrayJSO;
    }
}
