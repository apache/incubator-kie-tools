/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.appformer.kogito.bridge.client.guided.tour.service.api;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Native class defined into the Guided Tour component.
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class Rect {

    @JsProperty
    public native void setBottom(final int bottom);

    @JsProperty
    public native void setHeight(final int height);

    @JsProperty
    public native void setLeft(final int left);

    @JsProperty
    public native void setRight(final int right);

    @JsProperty
    public native void setTop(final int top);

    @JsProperty
    public native void setWidth(final int width);

    @JsProperty
    public native void setX(final int x);

    @JsProperty
    public native void setY(final int y);

    @JsOverlay
    public static Rect NONE() {
        return new Rect();
    }
}
