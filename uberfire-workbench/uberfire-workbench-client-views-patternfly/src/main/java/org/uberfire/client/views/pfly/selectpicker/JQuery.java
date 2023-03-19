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

import com.google.gwt.core.client.JavaScriptObject;
import elemental2.dom.Element;
import elemental2.dom.Node;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

import static jsinterop.annotations.JsPackage.GLOBAL;

@JsType(isNative = true)
public abstract class JQuery {

    @JsProperty(namespace = GLOBAL, name = "jQuery")
    public static JQuery $;

    @JsMethod(namespace = GLOBAL, name = "jQuery")
    public native static JQuery $(final Node selector);

    @JsMethod(namespace = GLOBAL, name = "jQuery")
    public native static JQuery $(final String selector);

    public native JQuery animate(final JavaScriptObject properties,
                                 final int duration);

    public native JQuery on(final String event,
                            final CallbackFunction callbackFunction);

    public native JQuery append(final JQuery jQueryElement);

    public native JQuery css(final JavaScriptObject properties);

    public native JQuery detach();

    public native JQueryList<Element> filter(final String selector);

    public native JQueryElementOffset offset();

    public native boolean contains(final Element container,
                                   final Element contained);

    @JsFunction
    public interface CallbackFunction {

        void call(final JQueryEvent event);
    }
}
