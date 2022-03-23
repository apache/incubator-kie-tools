/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.lienzo.client;

import com.ait.lienzo.client.core.types.JsCanvas;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public class WindowJSCanvas {

    @JsProperty
    private static Object jsCanvas;

    @JsProperty
    private static Object jsCanvasExamples;

    @JsOverlay
    public static final void linkJSCanvas(JsCanvas canvas) {
        WindowJSCanvas.jsCanvas = canvas;
    }

    @JsOverlay
    public static final void linkJSCanvasExamples(JsCanvasExamples jsCanvasExamples) {
        WindowJSCanvas.jsCanvasExamples = jsCanvasExamples;
    }
}
