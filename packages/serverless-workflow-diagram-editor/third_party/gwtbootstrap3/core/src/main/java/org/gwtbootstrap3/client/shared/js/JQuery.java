package org.gwtbootstrap3.client.shared.js;

/*
 * #%L
 * GwtBootstrap3
 * %%
 * Copyright (C) 2013 - 2018 GwtBootstrap3
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

import static jsinterop.annotations.JsPackage.GLOBAL;

/**
 * jQuery and Bootstrap methods wrapper
 * @author Thiago Ricciardi
 *
 */
@JsType(isNative = true, namespace = GLOBAL, name = "jQuery")
public class JQuery {

    @JsMethod(namespace = GLOBAL, name = "jQuery")
    public static native JQuery $(Element element);

    @JsMethod(namespace = GLOBAL, name = "jQuery")
    public static native JQuery $(HTMLElement element);

    @JsMethod(namespace = GLOBAL, name = "jQuery")
    public static native JQuery $(String selector);

    @JsMethod(namespace = GLOBAL, name = "jQuery")
    public static native JQuery $();

    public native JQuery button(String method);

    public native String html();

    public native JQuery html(String htmlString);

    public native JQuery on(String events, EventHandler function);

    public native JQuery off(String events);

    public native JQuery alert(String method);

    public native JQuery carousel(String method);

    public native JQuery carousel(int slideNumber);

    public native JQuery collapse(String method);

    public native JQuery modal(String method);

    public native JQuery popover();

    public native JQuery popover(String method);

    public native JQuery popover(Object value);

    public native JQuery tab(String method);

    public native JQuery tooltip();

    public native JQuery tooltip(String method);

    public native void carousel(Element e, int interval, String pause, boolean wrap);

    public native Object data(String dataTarget);


    public native JQuery affix(JsPropertyMap param);
}
