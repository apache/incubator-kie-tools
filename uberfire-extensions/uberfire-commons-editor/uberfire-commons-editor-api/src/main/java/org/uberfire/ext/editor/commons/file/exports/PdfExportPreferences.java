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

package org.uberfire.ext.editor.commons.file.exports;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

import static org.uberfire.preferences.shared.impl.validation.EnumValuePropertyValidator.parseString;

/**
 * The pdf document's settings.
 */
@Portable
public final class PdfExportPreferences {

    public static PdfExportPreferences create(final String orientation,
                                              final String unit,
                                              final String format) {
        return create(Orientation.valueOf(parseString(orientation)),
                      Unit.valueOf(parseString(unit)),
                      Format.valueOf(parseString(format)));
    }

    public static PdfExportPreferences create(final Orientation orientation,
                                              final Unit unit,
                                              final Format format) {
        return new PdfExportPreferences(orientation,
                                        unit,
                                        format);
    }

    public enum Orientation {
        PORTRAIT,
        LANDSCAPE
    }

    public enum Unit {
        PT,
        MM,
        CM,
        IN
    }

    public enum Format {
        A0,
        A1,
        A2,
        A3,
        A4,
        A5,
        A6,
        A7,
        A8,
        A9,
        A10,
        B0,
        B1,
        B2,
        B3,
        B4,
        B5,
        B6,
        B7,
        B8,
        B9,
        B10,
        C0,
        C1,
        C2,
        C3,
        C4,
        C5,
        C6,
        C7,
        C8,
        C9,
        C10,
    }

    private Orientation orientation;
    private Unit unit;
    private Format format;

    private PdfExportPreferences(final @MapsTo("orientation") Orientation orientation,
                                 final @MapsTo("unit") Unit unit,
                                 final @MapsTo("format") Format format) {
        this.orientation = orientation;
        this.unit = unit;
        this.format = format;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(final Orientation orientation) {
        this.orientation = orientation;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(final Unit unit) {
        this.unit = unit;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(final Format format) {
        this.format = format;
    }
}
