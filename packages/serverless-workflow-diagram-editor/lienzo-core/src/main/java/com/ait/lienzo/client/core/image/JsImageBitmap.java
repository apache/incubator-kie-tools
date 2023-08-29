/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package com.ait.lienzo.client.core.image;

import elemental2.dom.DomGlobal;
import elemental2.dom.ImageBitmap;
import elemental2.dom.Response;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

@JsType(isNative = true, namespace = "<global>", name = "ImageBitmap")
public class JsImageBitmap implements ImageBitmap {

    @JsProperty
    public native int getHeight();

    @JsProperty
    public native int getWidth();

    public native void close();

    @JsOverlay
    public static void loadImageBitmap(final String url, final JsImageBitmapCallback callback) {
        DomGlobal.fetch(url)
                .then(Response::blob)
                .then(DomGlobal::createImageBitmap)
                .then(image -> {
                    callback.onSuccess(Js.uncheckedCast(image));
                    return null;
                }).catch_(error -> {
            callback.onError(error);
            return null;
        });
    }

}
