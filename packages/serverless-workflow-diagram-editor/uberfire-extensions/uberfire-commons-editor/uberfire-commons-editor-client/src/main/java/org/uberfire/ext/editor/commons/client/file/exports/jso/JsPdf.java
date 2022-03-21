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

package org.uberfire.ext.editor.commons.client.file.exports.jso;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Provides the JsInterop API for jsPdf.
 * Provided by the webjar <code>org.webjars.bower.jspdf</code>.
 * @see <a href="https://github.com/MrRio/jsPDF">jsPDF.js</a>
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class JsPdf {

    /**
     * Factory method for JsPdf.
     * @param settings The settings for the new document's.
     * @return A new JsPdf instance.
     */
    @JsOverlay
    public static final JsPdf create(JsPdfSettings settings) {
        return new JsPdf(settings);
    }

    @JsConstructor
    public JsPdf(JsPdfSettings settings) {
    }

    /**
     * Adds text to the document.
     * @param text The text to add.
     * @param x Coordinate (in units declared at inception of PDF document) against left edge of the page.
     * @param y Coordinate (in units declared at inception of PDF document) against upper edge of the page.
     */
    public native void text(String text,
                            int x,
                            int y);

    /**
     * Adds an image  to the document.
     * @param imgData The url data for the image.
     * @param x Coordinate (in units declared at inception of PDF document) against left edge of the page.
     * @param y Coordinate (in units declared at inception of PDF document) against upper edge of the page.
     * @param width The image's width.
     * @param height The image's height.
     */
    public native void addImage(String imgData,
                                String imgType,
                                int x,
                                int y,
                                int width,
                                int height);

    /**
     * Saves the document into a PDF file.
     * @param fileName The name for the generated file.
     */
    public native void save(String fileName);
}
