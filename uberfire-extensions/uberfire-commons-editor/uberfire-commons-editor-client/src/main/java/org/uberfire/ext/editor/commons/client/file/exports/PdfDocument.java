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

package org.uberfire.ext.editor.commons.client.file.exports;

import java.util.LinkedList;
import java.util.List;

import org.uberfire.ext.editor.commons.file.exports.PdfExportPreferences;

/**
 * The pdf document's content model.
 */
public final class PdfDocument {

    private final List<PdfEntry> entries = new LinkedList<>();
    private final PdfExportPreferences settings;

    /**
     * Factory method that allows custom PDF document's settings.
     * It creates a new PDF document instance using the specified settings.
     * @return A new PDF document instance.
     */
    public static PdfDocument create(final PdfExportPreferences settings) {
        return new PdfDocument(settings);
    }

    private PdfDocument(PdfExportPreferences settings) {
        this.settings = settings;
    }

    /**
     * Adds text to the document.
     * @param text The text to add.
     * @param x Coordinate (in units declared at inception of PDF document) against left edge of the page.
     * @param y Coordinate (in units declared at inception of PDF document) against upper edge of the page.
     */
    public void addText(final String text,
                        final int x,
                        final int y) {
        entries.add(new Text(text,
                             x,
                             y));
    }

    /**
     * Adds an image  to the document.
     * @param urlData The url data for the image.
     * @param x Coordinate (in units declared at inception of PDF document) against left edge of the page.
     * @param y Coordinate (in units declared at inception of PDF document) against upper edge of the page.
     * @param width The image's width.
     * @param height The image's height.
     */
    public void addImage(final String urlData,
                         final String imgType,
                         final int x,
                         final int y,
                         final int width,
                         final int height) {
        entries.add(new Image(urlData,
                              imgType,
                              x,
                              y,
                              width,
                              height));
    }

    public PdfExportPreferences getSettings() {
        return settings;
    }

    List<PdfEntry> getPdfEntries() {
        return entries;
    }

    interface PdfEntry {

    }

    static final class Text implements PdfEntry {

        private final String text;
        private final int x;
        private final int y;

        private Text(final String text,
                     final int x,
                     final int y) {
            this.text = text;
            this.x = x;
            this.y = y;
        }

        public String getText() {
            return text;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    static final class Image implements PdfEntry {

        private final String toDataURL;
        private final String imgType;
        private final int x;
        private final int y;
        private final int width;
        private final int height;

        private Image(final String toDataURL,
                      final String imgType,
                      final int x,
                      final int y,
                      final int width,
                      final int height) {
            this.toDataURL = toDataURL;
            this.imgType = imgType;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public String getToDataURL() {
            return toDataURL;
        }

        public String getImgType() {
            return imgType;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }
}
