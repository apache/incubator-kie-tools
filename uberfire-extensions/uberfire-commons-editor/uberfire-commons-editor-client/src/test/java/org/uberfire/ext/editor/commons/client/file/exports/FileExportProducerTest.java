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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.editor.commons.client.file.exports.jso.FileExportScriptInjector;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FileExportProducerTest {

    private FileExportProducer fs;

    @Mock
    private FileExportScriptInjector scriptInjector;

    @Before
    public void setup() {
        fs = new FileExportProducer(scriptInjector);
    }

    @Test
    public void testInit() {
        fs.init();
        verify(scriptInjector,
               times(1)).inject();
    }

    @Test
    public void ProduceTextFileSaverTest() {
        TextFileExport textFileSaver = fs.forText();
        assertNotNull(textFileSaver);
    }

    @Test
    public void producePDFFileSaverTest() {
        PdfFileExport pdfFileSaver = fs.forPDF();
        assertNotNull(pdfFileSaver);
    }

    @Test
    public void produceImageFileSaver() {
        ImageFileExport imageFileSaver = fs.forImage();
        assertNotNull(imageFileSaver);
    }
}
