/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

public final class PathPartEntryJSO extends JavaScriptObject
{
    public static final int UNDEFINED_PATH_PART        = 0;

    public static final int LINETO_ABSOLUTE            = 1;

    public static final int MOVETO_ABSOLUTE            = 2;

    public static final int BEZIER_CURVETO_ABSOLUTE    = 3;

    public static final int QUADRATIC_CURVETO_ABSOLUTE = 4;

    public static final int ARCTO_ABSOLUTE             = 5;

    public static final int CLOSE_PATH_PART            = 6;

    public static final native PathPartEntryJSO make(int c, NFastDoubleArrayJSO p)
    /*-{
        return {command: c, points: p};
    }-*/;

    protected PathPartEntryJSO()
    {
    }

    public final String toJSONString()
    {
        return new JSONObject(this).toString();
    }

    public final native int getCommand()
    /*-{
        return this.command;
    }-*/;

    public final native NFastDoubleArrayJSO getPoints()
    /*-{
        return this.points;
    }-*/;
}
