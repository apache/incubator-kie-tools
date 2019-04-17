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

package org.kie.workbench.common.forms.jbpm.client.rendering.documents.control.js;

import elemental2.dom.File;
import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = "appformer.forms")
public class Documents {

    @JsMethod
    public static native Documents get();

    @JsMethod
    public native Documents bind(HTMLElement element);

    @JsMethod
    public native Documents onDrop(OnDropCallback callback);

    @JsFunction
    @FunctionalInterface
    public interface OnDropCallback {

        void call(final Document document, final File file);
    }
}
