/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.renderer.c3.client.jsbinding;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class C3Tick {
    
    @JsOverlay
    static C3Tick create(FormatterCallback callback) {
        C3Tick instance = new C3Tick();
        instance.setFormat(callback);
        return instance;
    }

    @JsProperty
    public native void setFormat(FormatterCallback callback);
    
    @JsProperty
    public native void setFit(boolean fit);
    
    @JsProperty
    public native void setRotate(int rotate);

    @JsFunction
    @FunctionalInterface
    public interface FormatterCallback {
        
        String callback(String value);
    
    }
        
}