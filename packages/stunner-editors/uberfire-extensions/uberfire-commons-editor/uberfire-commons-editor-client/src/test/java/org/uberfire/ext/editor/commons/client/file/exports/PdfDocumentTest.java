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

import java.util.List;

import org.junit.Test;
import org.uberfire.ext.editor.commons.file.exports.PdfExportPreferences;

import static org.junit.Assert.*;

public class PdfDocumentTest {

    @Test
    public void testText() {
        PdfDocument document = PdfDocument.create(PdfExportPreferences.create(PdfExportPreferences.Orientation.PORTRAIT,
                                                                              PdfExportPreferences.Unit.MM,
                                                                              PdfExportPreferences.Format.A4));
        document.addText("text1",
                         13,
                         453);
        // Assert settings.
        assertEquals(PdfExportPreferences.Orientation.PORTRAIT,
                     document.getSettings().getOrientation());
        assertEquals(PdfExportPreferences.Unit.MM,
                     document.getSettings().getUnit());
        assertEquals(PdfExportPreferences.Format.A4,
                     document.getSettings().getFormat());
        // Assert entries.
        final List<PdfDocument.PdfEntry> pdfEntries = document.getPdfEntries();
        assertNotNull(pdfEntries);
        assertEquals(1,
                     pdfEntries.size());
        assertTrue(pdfEntries.get(0) instanceof PdfDocument.Text);
        final PdfDocument.Text entry = (PdfDocument.Text) pdfEntries.get(0);
        assertEquals("text1",
                     entry.getText());
        assertEquals(13,
                     entry.getX());
        assertEquals(453,
                     entry.getY());
    }

    @Test
    public void testImage() {
        PdfDocument document = PdfDocument.create(PdfExportPreferences.create(PdfExportPreferences.Orientation.LANDSCAPE,
                                                                              PdfExportPreferences.Unit.MM,
                                                                              PdfExportPreferences.Format.A4));
        document.addImage("data-url1",
                          "jpeg",
                          13,
                          453,
                          345,
                          234);
        // Assert settings.
        assertEquals(PdfExportPreferences.Orientation.LANDSCAPE,
                     document.getSettings().getOrientation());
        assertEquals(PdfExportPreferences.Unit.MM,
                     document.getSettings().getUnit());
        assertEquals(PdfExportPreferences.Format.A4,
                     document.getSettings().getFormat());
        // Assert entries.
        final List<PdfDocument.PdfEntry> pdfEntries = document.getPdfEntries();
        assertNotNull(pdfEntries);
        assertEquals(1,
                     pdfEntries.size());
        assertTrue(pdfEntries.get(0) instanceof PdfDocument.Image);
        final PdfDocument.Image entry = (PdfDocument.Image) pdfEntries.get(0);
        assertEquals("data-url1",
                     entry.getToDataURL());
        assertEquals("jpeg",
                     entry.getImgType());
        assertEquals(13,
                     entry.getX());
        assertEquals(453,
                     entry.getY());
        assertEquals(345,
                     entry.getWidth());
        assertEquals(234,
                     entry.getHeight());
    }

    @Test
    public void testCustomSettings() {
        PdfDocument document = PdfDocument.create(PdfExportPreferences.create(PdfExportPreferences.Orientation.LANDSCAPE,
                                                                              PdfExportPreferences.Unit.IN,
                                                                              PdfExportPreferences.Format.B6));
        // Assert settings.
        assertEquals(PdfExportPreferences.Orientation.LANDSCAPE,
                     document.getSettings().getOrientation());
        assertEquals(PdfExportPreferences.Unit.IN,
                     document.getSettings().getUnit());
        assertEquals(PdfExportPreferences.Format.B6,
                     document.getSettings().getFormat());
    }
}
