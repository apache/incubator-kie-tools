/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.util.js;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class KeyValue {

    private Object key;
    private Object value;

    private KeyValue() {

    }

    @JsOverlay
    public static final KeyValue create(Object key, Object value) {
        final KeyValue instance = new KeyValue();
        instance.key = key;
        instance.value = value;
        return instance;
    }

    @JsOverlay
    public final Object getKey() {
        return key;
    }

    @JsOverlay
    public final Object getValue() {
        return value;
    }
}
