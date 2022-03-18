/*
   Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.

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

package com.ait.lienzo.tools.client.collection;

import java.util.Iterator;
import java.util.List;

import elemental2.core.JsIterable;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;

@JsType(isNative = true, name = "Array", namespace = JsPackage.GLOBAL)
public abstract class NArrayBase<M> implements JsIterable<M>,
                                               JsArrayLike<M> {

    protected NArrayBase() {
    }

    @JsOverlay
    public final void clear() {
        setSize(0);
    }

    @JsOverlay
    public final String join() {
        return join(",");
    }

    @JsOverlay
    public final boolean isNull(final int index) {
        if ((index < 0) || (index >= size())) {
            return true;
        }
        return getAt(index) == null;
    }

    @JsOverlay
    public final boolean isDefined(final int index) {
        if ((index < 0) || (index >= size())) {
            return false;
        }
        return getAt(index) == Js.undefined();
    }

    @JsOverlay
    public final boolean isEmpty() {
        return getLength() < 1;
    }

    @JsOverlay
    public final int size() {
        return getLength();
    }

    public native int push(M... var_args);

    @JsOverlay
    public final void setSize(int size) {
        if (size < 0) {
            size = 0;
        }
        if (getLength() < size) {
            while (getLength() < size) {
                this.pop();
            }
        } else {
            setLength(size);
        }
    }

    public native M pop();

    public native M shift();

    public native M unshift(M value);

    public final native void splice(int beg, int removed);

    public native M[] splice(int index, int howMany, M... var_args);

    public native M[] slice();

    public final native M[] slice(int beg);

    public final native M[] slice(int beg, int end);

    public native M[] reverse();

    public final native String join(String separator);

    public final native M concat(M value);

    public native M[] copyWithin(int start, int end);

    public native M[] concat(M... items);

    @JsOverlay
    public final Iterator<M> iterator() {
        return asList().iterator();
    }

    @JsOverlay
    public final List<M> toList() {
        return asList();
    }
}
