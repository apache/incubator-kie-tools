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

import static org.junit.Assert.*;

public class PdfContentTest {

    @Test
    public void testText() {
        PdfContent content = PdfContent.create();
        content.addText("text1",
                        13,
                        453);
        final List<PdfContent.PdfEntry> pdfEntries = content.getPdfEntries();
        assertNotNull(pdfEntries);
        assertEquals(1,
                     pdfEntries.size());
        assertTrue(pdfEntries.get(0) instanceof PdfContent.Text);
        final PdfContent.Text entry = (PdfContent.Text) pdfEntries.get(0);
        assertEquals("text1",
                     entry.getText());
        assertEquals(13,
                     entry.getX());
        assertEquals(453,
                     entry.getY());
    }

    @Test
    public void testImage() {
        PdfContent content = PdfContent.create();
        content.addImage("data-url1",
                         "jpeg",
                         13,
                         453,
                         345,
                         234);
        final List<PdfContent.PdfEntry> pdfEntries = content.getPdfEntries();
        assertNotNull(pdfEntries);
        assertEquals(1,
                     pdfEntries.size());
        assertTrue(pdfEntries.get(0) instanceof PdfContent.Image);
        final PdfContent.Image entry = (PdfContent.Image) pdfEntries.get(0);
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
}
