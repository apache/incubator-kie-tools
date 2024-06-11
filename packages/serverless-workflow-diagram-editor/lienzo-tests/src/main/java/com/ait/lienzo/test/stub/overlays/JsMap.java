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

import java.util.HashMap;
import java.util.Map;

import com.ait.lienzo.test.annotation.StubClass;
import elemental2.core.JsIteratorIterable;

import static org.mockito.Mockito.mock;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/8/19
 */
@StubClass("elemental2.core.JsMap")
public class JsMap<KEY, VALUE> {

    public int size = 0;

    Map<KEY, VALUE> map = new HashMap<>();

    public VALUE get(KEY key) {
        return map.get(key);
    }

    public JsMap<KEY, VALUE> set(KEY key, VALUE value) {
        map.put(key, value);
        this.size = map.size();
        return this;
    }

    public boolean has(KEY key) {
        return map.containsKey(key);
    }

    public JsIteratorIterable<VALUE> values() {
        return mock(JsIteratorIterable.class);
    }

    public Object forEach(elemental2.core.JsMap.ForEachCallbackFn<? super KEY, ? super VALUE> callback) {
        return mock(Object.class);
    }
}
