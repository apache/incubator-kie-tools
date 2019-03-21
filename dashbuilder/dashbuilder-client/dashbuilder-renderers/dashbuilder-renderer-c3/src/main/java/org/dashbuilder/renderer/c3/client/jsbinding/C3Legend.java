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

import org.dashbuilder.displayer.Position;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class C3Legend {
    
    @JsOverlay
    static C3Legend create(boolean show, String position) {
        C3Legend instance = new C3Legend();
        instance.setShow(show);
        instance.setPosition(position);
        return instance;
    }
    
    @JsOverlay
    public static String convertPosition(Position position) {
        // Not all positions are supported by C3.
        switch(position) {
        case BOTTOM:
            return position.name().toLowerCase();
        case IN:
            return "inset";
        case RIGHT:
            return position.name().toLowerCase();
        default:
            return "";
        }
    }
    
    @JsProperty
    public native void setShow(boolean show);
    
    @JsProperty
    public native void setPosition(String position);
    
}