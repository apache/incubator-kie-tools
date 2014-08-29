/*
   Copyright (c) 2014 Ahome' Innovation Technologies. All rights reserved.

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

public class NFastDoubleArrayJSO extends JavaScriptObject
{
    protected NFastDoubleArrayJSO()
    {
    }

    public static NFastDoubleArrayJSO make()
    {
        return JavaScriptObject.createArray().cast();
    }

    public final native int size()
    /*-{
        return this.length;
    }-*/;

    public final native double get(int indx)
    /*-{
        return this[indx];
    }-*/;

    public final native void add(double value)
    /*-{
        this[this.length] = value;
    }-*/;

    public final native void push(double value)
    /*-{
        this[this.length] = value;
    }-*/;

    public final native double shift()
    /*-{
        return this.shift();
    }-*/;

    public final native void clear()
    /*-{
        this.length = 0;
    }-*/;

    public final native NFastDoubleArrayJSO sort()
    /*-{
        return this.slice().sort(function(a, b){return a - b});
    }-*/;

    public final native NFastDoubleArrayJSO uniq()
    /*-{
        return this.slice().sort(function(a, b){return a - b}).reduce(function(a,b) {
            if(a.slice(-1)[0] !== b) {
                a.push(b);
            }
            return a;
        },[]);
    }-*/;
}
