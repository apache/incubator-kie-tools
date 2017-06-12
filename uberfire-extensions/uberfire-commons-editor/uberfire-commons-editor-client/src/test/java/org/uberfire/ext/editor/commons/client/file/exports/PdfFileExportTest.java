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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.editor.commons.client.file.exports.jso.JsPdf;
import org.uberfire.ext.editor.commons.file.exports.PdfExportPreferences;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class PdfFileExportTest {

    private PdfFileExport tested;
    private PdfDocument content;

    @Mock
    private JsPdf fileExport;

    @Before
    public void setup() {
        content = PdfDocument.create(PdfExportPreferences.create(PdfExportPreferences.Orientation.PORTRAIT,
                                                                 PdfExportPreferences.Unit.MM,
                                                                 PdfExportPreferences.Format.A4));
        tested = new PdfFileExport();
    }

    @Test
    public void saveAsTextTest() {
        content.addText("test",
                        10,
                        20);
        tested.export(content,
                      "file1",
                      fileExport);
        verify(fileExport,
               times(1)).text(eq("test"),
                              eq(10),
                              eq(20));
        verify(fileExport).save(eq("file1"));
    }

    @Test
    public void saveAsImageTest() {
        final String dataUrl = "data:image/jpeg;base64,9j/4AAQSkZJRgABAQEASABIAAD";
        content.addImage(dataUrl,
                         "jpeg",
                         10,
                         20,
                         100,
                         200);
        tested.export(content,
                      "file2",
                      fileExport);
        verify(fileExport,
               times(1)).addImage(eq(dataUrl),
                                  eq("jpeg"),
                                  eq(10),
                                  eq(20),
                                  eq(100),
                                  eq(200));
        verify(fileExport).save(eq("file2"));
    }
}
