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

package org.uberfire.client.views.pfly.selectpicker;

import elemental2.dom.Element;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

import static jsinterop.annotations.JsPackage.GLOBAL;

@JsType(isNative = true)
public abstract class JQuerySelectPicker {

    @JsMethod(namespace = GLOBAL, name = "jQuery")
    public native static JQuerySelectPicker $(final Element selector);

    @JsMethod(namespace = GLOBAL, name = "jQuery")
    public native static JQuerySelectPicker $(final String selector);

    public native JQuerySelectPicker selectpicker(final String method);

    public native JQuerySelectPicker selectpicker(final String method,
                                                  final String value);

    public native JQuerySelectPicker on(final String event,
                                        final CallbackFunction callbackFunction);

    public native JQuerySelectPicker off(final String event);

    public native String val();

    @JsFunction
    public interface CallbackFunction {

        void call(final JQuerySelectPickerEvent event);
    }
}
