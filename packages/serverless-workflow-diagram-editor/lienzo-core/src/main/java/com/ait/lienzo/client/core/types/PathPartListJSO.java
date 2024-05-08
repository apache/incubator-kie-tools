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

package com.ait.lienzo.client.core.types;

import elemental2.core.JsIterable;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsArrayLike;

@JsType(isNative = true, name = "Array", namespace = JsPackage.GLOBAL)
public class PathPartListJSO implements JsIterable<PathPartEntryJSO>,
                                        JsArrayLike<PathPartEntryJSO> {

    @JsOverlay
    public static final PathPartListJSO make() {
        return new PathPartListJSO();
    }

    protected PathPartListJSO() {
    }

    public native int push(PathPartEntryJSO... var_args);

    @JsOverlay
    public final PathPartEntryJSO get(final int i) {
        return getAt(i);
    }

    @JsOverlay
    public final int length() {
        return getLength();
    }
}
