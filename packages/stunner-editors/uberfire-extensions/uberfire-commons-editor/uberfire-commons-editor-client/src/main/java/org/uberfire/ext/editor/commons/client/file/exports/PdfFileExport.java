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

import org.uberfire.ext.editor.commons.client.file.exports.jso.JsPdf;
import org.uberfire.ext.editor.commons.client.file.exports.jso.JsPdfSettings;
import org.uberfire.ext.editor.commons.file.exports.PdfExportPreferences;

public class PdfFileExport implements FileExport<PdfDocument> {

    @Override
    public void export(final PdfDocument content,
                       final String fileName) {
        export(content,
               fileName,
               createNewDocument(content));
    }

    void export(final PdfDocument content,
                final String fileName,
                final JsPdf jsFileExport) {
        content.getPdfEntries()
                .forEach(entry -> processEntry(jsFileExport,
                                               entry));
        jsFileExport.save(fileName);
    }

    private void processEntry(final JsPdf jsFileExport,
                              final PdfDocument.PdfEntry entry) {
        if (entry instanceof PdfDocument.Text) {
            final PdfDocument.Text text = (PdfDocument.Text) entry;
            jsFileExport.text(text.getText(),
                              text.getX(),
                              text.getY());
        } else if (entry instanceof PdfDocument.Image) {
            final PdfDocument.Image image = (PdfDocument.Image) entry;
            jsFileExport.addImage(image.getToDataURL(),
                                  image.getImgType(),
                                  image.getX(),
                                  image.getY(),
                                  image.getWidth(),
                                  image.getHeight());
        }
    }

    private static JsPdf createNewDocument(final PdfDocument content) {
        final PdfExportPreferences settings = content.getSettings();
        final String orientation = settings.getOrientation().name().toLowerCase();
        final String unit = settings.getUnit().name().toLowerCase();
        final String format = settings.getFormat().name().toLowerCase();
        final JsPdfSettings jsPdfSettings = JsPdfSettings.create(orientation,
                                                                 unit,
                                                                 format);
        return JsPdf.create(jsPdfSettings);
    }
}
