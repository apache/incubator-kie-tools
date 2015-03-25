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

import com.google.gwt.json.client.JSONArray;

public class NFastDoubleArrayJSO extends NBaseNativeArrayJSO<NFastDoubleArrayJSO>
{
    protected NFastDoubleArrayJSO()
    {
    }

    public static NFastDoubleArrayJSO make()
    {
        return NBaseNativeArrayJSO.make().cast();
    }

    public static NFastDoubleArrayJSO make(double d, double... list)
    {
        NFastDoubleArrayJSO jso = make();

        jso.push(d, list);

        return jso;
    }

    public final double[] toArray()
    {
        final int size = size();

        final double[] array = new double[size];

        for (int i = 0; i < size; i++)
        {
            array[i] = get(i);
        }
        return array;
    }

    public final String toJSONString()
    {
        return new JSONArray(this).toString();
    }

    public final void push(double d, double... list)
    {
        push(d);

        for (int i = 0, s = list.length; i < s; i++)
        {
            push(list[i]);
        }
    }

    public final native double get(int indx)
    /*-{
        return this[indx];
    }-*/;

    public final native double pop()
    /*-{
        return this.pop();
    }-*/;

    public final native void push(double value)
    /*-{
        this[this.length] = value;
    }-*/;

    public final native double shift()
    /*-{
        return this.shift();
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

    public final native boolean contains(double value)
    /*-{
        return (value in this);
    }-*/;
}
