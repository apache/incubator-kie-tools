/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.appformer.kogito.bridge.client.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;

public class JSIUtils {

    private JSIUtils() {
        // Utils class
    }

    /**
     * Helper method to create a new, empty <code>JsArrayLike</code>
     * @return
     */
    public static native <D> JsArrayLike<D> getNativeArray() /*-{
        return [];
    }-*/;

    /**
     * Returns a <code>JsArrayLike</code> where each element represents the <b>unwrapped</b> object (i.e. object.value) of the original one.
     * It the original <code>JsArrayLike</code> is <code>null</code>, returns a new, empty one
     * @param original
     * @param <D>
     * @return
     */
    public static native <D> JsArrayLike<D> getUnwrappedElementsArray(final JsArrayLike<D> original) /*-{
        var toReturn = [];
        if (original != null) {
            toReturn = original.map(function (arrayItem) {
                var retrieved = arrayItem.value
                var toSet = retrieved == null ? arrayItem : retrieved
                return toSet;
            });
        }
        return toReturn;
    }-*/;

    public static<D> List<D> toList(final JsArrayLike<D> jsArrayLike) {
        final List<D> toReturn = new ArrayList<>();
        if (Objects.nonNull(jsArrayLike)) {
            for (int i = 0; (i < jsArrayLike.getLength()); i++) {
                final D toAdd = Js.uncheckedCast(jsArrayLike.getAt(i));
                toReturn.add(toAdd);
            }
        }
        return toReturn;
    }

}
