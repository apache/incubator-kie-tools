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

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * The jsPDF' settings,
 * It's being exported just as an Object with the following properties:
 * - <code>orientation</code>
 * - <code>unit</code>
 * - <code>format</code>
 */
@JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
public class JsPdfSettings {

    /**
     * Factory method for the jsPdf' settings.
     * @param orientation The document's orientation. It can be <code>portrait</code> or <ocde>landscape</ocde>.
     * @param unit Values are <code>pm</code>, <code>mm</code>, <code>cm</code> or <code>in</code>.
     * @param format Any of the allowed PDF page formats. See PdfSettings.Format.
     * @return A new jsPdf' settings instance.
     */
    @JsOverlay
    public static JsPdfSettings create(String orientation,
                                       String unit,
                                       String format) {
        final JsPdfSettings instance = new JsPdfSettings();
        instance.setOrientation(orientation);
        instance.setUnit(unit);
        instance.setFormat(format);
        return instance;
    }

    @JsProperty
    public native String getOrientation();

    @JsProperty
    public native void setOrientation(String orientation);

    @JsProperty
    public native String getUnit();

    @JsProperty
    public native void setUnit(String unit);

    @JsProperty
    public native String getFormat();

    @JsProperty
    public native void setFormat(String format);
}
