/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.widgets;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public interface D3 {

    D3 select(String path);

    D3 select(Object object);

    D3 selectAll(String path);

    Zoom zoom();

    CallbackFunction on(String event,
                        CallbackFunction callbackFn);

    D3 call(CallbackFunction function,
            Object... args);

    D3 attr(String name,
            Object value);

    Object attr(String name);

    D3 style(String name,
             Object value);

    @JsProperty
    Transform getZoomIdentity();

    @JsProperty
    <T extends Event> T getEvent();

    Transition transition();

    @JsFunction
    @FunctionalInterface
    interface CallbackFunction {

        void execute();
    }

    @JsType(isNative = true)
    interface Zoom extends D3 {

        void scaleBy(D3 element,
                     double scale);

        void scaleTo(D3 element,
                     double scale);

        void transform(D3 selection,
                       Transform transform);
    }

    @JsType(isNative = true)
    interface Transform extends D3 {

        @JsProperty
        double getX();

        @JsProperty
        double getY();

        @JsProperty
        double getK();
    }

    @JsType(isNative = true)
    interface Transition extends D3 {

        D3 duration(double milis);
    }

    @JsType(isNative = true)
    interface Event {

        @JsProperty
        Object getCurrentTarget();
    }

    @JsType(isNative = true)
    interface ZoomEvent {

        @JsProperty
        Transform getTransform();
    }

    class Builder {

        @JsProperty(name = "d3", namespace = JsPackage.GLOBAL)
        public static native D3 get();
    }
}
