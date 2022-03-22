/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
public class PopoverOptions {

    @JsProperty
    public native void setAnimation(Boolean animation);

    @JsProperty
    public native void setContainer(String container);

    @JsProperty
    public native void setContent(String content);

    @JsProperty
    public native void setContent(PopoverCallback callback);

    @JsProperty
    public native void setDelay(int delay);

    @JsProperty
    public native void setHtml(Boolean html);

    @JsProperty
    public native void setPlacement(String placement);

    @JsProperty
    public native void setSelector(String selector);

    @JsProperty
    public native void setTemplate(String template);

    @JsProperty
    public native void setTemplate(PopoverCallback callback);

    @JsProperty
    public native void setTitle(String title);

    @JsProperty
    public native void setTitle(PopoverCallback callback);

    @JsProperty
    public native void setTrigger(String trigger);

    @JsProperty
    public native void setViewport(String viewport);

    @JsProperty
    public native void setViewport(PopoverCallback callback);

    @JsFunction
    @FunctionalInterface
    public interface PopoverCallback {

        Object getValue(HTMLElement element);
    }
}
