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

package com.ait.lienzo.test.stub.overlays;

import java.util.LinkedList;

import com.ait.lienzo.client.core.types.PathPartEntryJSO;
import com.ait.lienzo.test.annotation.StubClass;
import jsinterop.annotations.JsOverlay;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/5/19
 */
@StubClass("com.ait.lienzo.client.core.types.PathPartListJSO")
public class PathPartListJSO extends LinkedList<PathPartEntryJSO> {

    @JsOverlay
    public static final PathPartListJSO make() {
        return new PathPartListJSO();
    }

    protected PathPartListJSO() {
    }

    public int push(PathPartEntryJSO... var_args) {
        for (int i = 0; i < var_args.length; i++) {
            add(var_args[i]);
        }
        return size();
    }

    @JsOverlay
    public final PathPartEntryJSO get(final int i) {
        return super.get(i);
    }

    @JsOverlay
    public final int length() {
        return size();
    }

    public void setLength(int length) {

    }
}
