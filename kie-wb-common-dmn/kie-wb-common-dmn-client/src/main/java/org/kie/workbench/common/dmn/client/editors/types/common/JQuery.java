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

package org.kie.workbench.common.dmn.client.editors.types.common;

import com.google.gwt.core.client.JavaScriptObject;
import elemental2.dom.Node;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

import static jsinterop.annotations.JsPackage.GLOBAL;

@JsType(isNative = true)
public abstract class JQuery {

    @JsMethod(namespace = GLOBAL, name = "jQuery")
    public native static JQuery $(final Node selector);

    public native JQuery animate(final JavaScriptObject properties,
                                 final int duration);

    public native JQuery on(final String event,
                            final CallbackFunction callbackFunction);

    public native JQuery append(final JQuery jQueryElement);

    public native JQuery css(final JavaScriptObject properties);

    public native JQuery detach();

    @JsFunction
    public interface CallbackFunction {

        void call(final JQueryEvent event);
    }
}
