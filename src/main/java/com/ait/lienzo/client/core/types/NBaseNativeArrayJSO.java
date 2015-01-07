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

public class NBaseNativeArrayJSO<T extends NBaseNativeArrayJSO<T>> extends JavaScriptObject
{
    public static NBaseNativeArrayJSO<?> make()
    {
        return JavaScriptObject.createArray().cast();
    }

    protected NBaseNativeArrayJSO()
    {
    }

    public final NativeInternalType getNativeTypeOf(int index)
    {
        return NativeInternalType.getNativeTypeOf(this, index);
    }

    public final boolean isEmpty()
    {
        return (0 == size());
    }

    public final native int size()
    /*-{
        return this.length;
    }-*/;

    public final native void setSize(int length)
    /*-{
        if (length < 0) {
            length = 0;
        }
        if (this.length < length) {
            while (this.length < length) {
                this.pop();
            }
        } else {
            this.length = length;
        }
    }-*/;

    public final void clear()
    {
        setSize(0);
    };

    public final native void splice(int beg, int removed)
    /*-{
        this.splice(beg, removed);
    }-*/;

    public final native void reverse()
    /*-{
        this.reverse();
    }-*/;

    public final native T concat(T value)
    /*-{
        return this.concat(value);
    }-*/;

    public final native T copy()
    /*-{
        return this.concat();
    }-*/;

    public final native T slice(int beg)
    /*-{
        return this.slice(beg);
    }-*/;

    public final native T slice(int beg, int end)
    /*-{
        return this.slice(beg, end);
    }-*/;

    public final native void spliceValueOf(int beg, int removed, NBaseNativeArrayJSO<?> value)
    /*-{
        if (null == value) {
            this.splice(beg, removed, null);
        } else {
            this.splice(beg, removed, value.valueOf());
        }
    }-*/;

    public final native String join(String separator)
    /*-{
        return this.join(separator);
    }-*/;

    public final native boolean isNull(int index)
    /*-{
        return this[index] == null;
    }-*/;
}
