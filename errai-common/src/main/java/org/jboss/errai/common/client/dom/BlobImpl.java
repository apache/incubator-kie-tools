/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.common.client.dom;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 *
 * @deprecated Use Elemental 2 for new development
 *
 * A JsInterop blob type implementation which provides
 * a public constructor.
 */
@JsType(isNative = true, name = "Blob", namespace = JsPackage.GLOBAL)
@Deprecated
public class BlobImpl implements Blob {

    /**
     * Factory method which creates a blob from plain string data.
     * @param text The plain string text encoded as UFT-8.
     * @return The blob instance.
     */
    @JsOverlay
    public static BlobImpl create(final String text) {
        return new BlobImpl(new String[]{text},
                            BlobPropertyBag.createPlainTextType());
    }

    /**
     * The blob's exported constructor.
     * @param array Is an Array of ArrayBuffer, ArrayBufferView, Blob, DOMString objects,
     * or a mix of any of such objects.
     * @param options The BlobPropertyBag dictionary.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Blob/Blob">Blob()</a>.
     */
    @JsConstructor
    public BlobImpl(Object[] array,
                    BlobPropertyBag options) {
    }

    @JsProperty
    @Override
    public native int getSize();

    @JsProperty
    @Override
    public native String getType();

    @Override
    public native Blob slice();

    @Override
    public native Blob slice(int start);

    @Override
    public native Blob slice(int start,
                             int end);

    @Override
    public native Blob slice(int start,
                             int end,
                             String contentType);
}
