/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.common.client.dom;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.jboss.errai.common.client.api.annotations.IOCProducer;

/**
 * @deprecated Use Elemental 2 for new development
 *
 * @author Max Barkley <mbarkley@redhat.com>
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Window">Web API</a>
 */
@JsType(isNative = true)
@Deprecated
public abstract class Window {

    private Window() {
    }

    @JsProperty(namespace = JsPackage.GLOBAL)
    @IOCProducer
    public static native Document getDocument();

    /**
     * Decodes a base-64 encoded string.
     * @param encodedStr A string of base-64 encoded data.
     * @return The decoded string data.
     * @see <a href="https://www.w3schools.com/jsref/met_win_atob.asp">Window atob() Method</a>
     */
    @JsMethod(namespace = JsPackage.GLOBAL)
    public static native String atob(String encodedStr);

    /**
     * Displays a dialog box with a specified message, along with an OK and a Cancel button.
     * @param message Specifies the text to display in the confirm box.
     * @return true if the user clicked "OK", return false otherwise.
     * @see <a href="https://www.w3schools.com/jsref/met_win_confirm.asp">Window confirm() Method</a>
     */
    @JsMethod(namespace = JsPackage.GLOBAL)
    public static native boolean confirm(String message);

}
