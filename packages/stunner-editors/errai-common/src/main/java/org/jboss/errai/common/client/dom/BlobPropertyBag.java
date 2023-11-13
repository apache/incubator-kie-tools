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


package org.jboss.errai.common.client.dom;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 *
 * @deprecated Use Elemental 2 for new development
 *
 * The Blob Property Bag.
 * @see <a href="https://www.w3.org/TR/2012/WD-FileAPI-20121025/#dfn-BlobPropertyBag">BlobPropertyBag</a>
 * <p>
 * Notice looking at the docs, the IDL for the BlobPropertyBag specifies it as a dictionary, which at least
 * must contain the <code>type</code> property.This way, the jsinterop type is being exported as
 * an <code>Object</code> with a <code>type</code> property too.
 */
@JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
@Deprecated
public class BlobPropertyBag {

    @JsOverlay
    public static String PLAIN_TEXT_UTF8 = "text/plain;charset=utf-8";
    @JsOverlay
    public static String IMAGE_JPG = "image/jpg";
    @JsOverlay
    public static String IMAGE_PNG = "image/png";

    @JsOverlay
    public static BlobPropertyBag create(final String type) {
        final BlobPropertyBag blobPropertyBag = new BlobPropertyBag();
        blobPropertyBag.setType(type);
        return blobPropertyBag;
    }

    @JsOverlay
    public static BlobPropertyBag createPlainTextType() {
        return create(PLAIN_TEXT_UTF8);
    }

    @JsOverlay
    public static BlobPropertyBag createImageJpgType() {
        return create(IMAGE_JPG);
    }

    @JsOverlay
    public static BlobPropertyBag createImagePngType() {
        return create(IMAGE_PNG);
    }

    /**
     * @return The ASCII-encoded string in lower case
     * representing the media type of the Blob.
     */
    @JsProperty
    public native String getType();

    @JsProperty
    public native void setType(String type);
}
