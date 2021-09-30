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

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class C3AxisInfo {
    
    @JsOverlay
    static C3AxisInfo create(boolean rotated, C3AxisX x, C3AxisY y) {
        C3AxisInfo instance = new C3AxisInfo();
        instance.setRotated(rotated);
        instance.setX(x);
        instance.setY(y);
        return instance;
    }
    
    @JsProperty
    public native void setRotated(boolean rotated);
    
    @JsProperty
    public native void setX(C3AxisX x);
    
    @JsProperty
    public native C3AxisX getX();
    
    @JsProperty
    public native void setY(C3AxisY y);
    
    @JsProperty
    public native C3AxisY getY();
}