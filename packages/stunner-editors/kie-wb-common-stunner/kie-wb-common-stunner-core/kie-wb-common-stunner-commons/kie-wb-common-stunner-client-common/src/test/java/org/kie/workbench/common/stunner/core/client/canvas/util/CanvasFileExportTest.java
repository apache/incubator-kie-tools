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


package org.kie.workbench.common.stunner.core.client.canvas.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.export.CanvasExport;
import org.kie.workbench.common.stunner.core.client.canvas.export.CanvasExportSettings;
import org.kie.workbench.common.stunner.core.client.canvas.export.CanvasURLExportSettings;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.editor.commons.client.file.exports.FileExport;
import org.uberfire.ext.editor.commons.client.file.exports.ImageDataUriContent;
import org.uberfire.ext.editor.commons.client.file.exports.PdfDocument;
import org.uberfire.ext.editor.commons.client.file.exports.svg.IContext2D;
import org.uberfire.ext.editor.commons.client.file.exports.svg.SvgFileExport;
import org.uberfire.ext.editor.commons.file.exports.FileExportsPreferences;
import org.uberfire.ext.editor.commons.file.exports.PdfExportPreferences;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CanvasFileExportTest {

    private static final String TITLE = "theTitle";
    private static final String JPG_DATA_URI = "data:image/jpeg;base64,the-jpg-data-goes-here";
    private static final String PNG_DATA_URI = "data:image/png;base64,the-png-data-goes-here";

    @Mock
    private CanvasExport<AbstractCanvasHandler> canvasExport;

    @Mock
    private FileExport<ImageDataUriContent> imageFileExport;

    @Mock
    private FileExport<PdfDocument> pdfFileExport;

    @Mock
    private SvgFileExport svgFileExport;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private FileExportsPreferences preferences;

    private CanvasFileExport tested;

    @Mock
    private IContext2D context2D;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getTitle()).thenReturn(TITLE);
        final PdfExportPreferences defaultPdfPrefs = PdfExportPreferences
                .create(PdfExportPreferences.Orientation.PORTRAIT,
                        PdfExportPreferences.Unit.MM,
                        PdfExportPreferences.Format.A4);
        when(preferences.getPdfPreferences()).thenReturn(defaultPdfPrefs);
        this.tested = spy(new CanvasFileExport(canvasExport,
                                               imageFileExport,
                                               pdfFileExport,
                                               preferences,
                                               svgFileExport));
    }

    @Test
    public void testExportToJpg() {
        when(canvasExport.toImageData(eq(canvasHandler),
                                      any(CanvasURLExportSettings.class))).thenReturn(JPG_DATA_URI);
        tested.exportToJpg(canvasHandler,
                           "file1");
        final ArgumentCaptor<ImageDataUriContent> contentArgumentCaptor =
                ArgumentCaptor.forClass(ImageDataUriContent.class);
        verify(imageFileExport,
               times(1)).export(contentArgumentCaptor.capture(),
                                eq("file1.jpg"));
        verify(pdfFileExport,
               never()).export(any(PdfDocument.class),
                               anyString());
        final ImageDataUriContent imageDataUriContent = contentArgumentCaptor.getValue();
        assertEquals(JPG_DATA_URI,
                     imageDataUriContent.getUri());
    }

    @Test
    public void testExportToPng() {
        when(canvasExport.toImageData(eq(canvasHandler),
                                      any(CanvasURLExportSettings.class))).thenReturn(PNG_DATA_URI);
        tested.exportToPng(canvasHandler,
                           "file1");
        final ArgumentCaptor<ImageDataUriContent> contentArgumentCaptor =
                ArgumentCaptor.forClass(ImageDataUriContent.class);
        verify(imageFileExport,
               times(1)).export(contentArgumentCaptor.capture(),
                                eq("file1.png"));
        verify(pdfFileExport,
               never()).export(any(PdfDocument.class),
                               anyString());
        final ImageDataUriContent imageDataUriContent = contentArgumentCaptor.getValue();
        assertEquals(PNG_DATA_URI,
                     imageDataUriContent.getUri());
    }

    @Test
    public void testExportToPdf() {
        when(canvasExport.toImageData(eq(canvasHandler),
                                      any(CanvasURLExportSettings.class))).thenReturn(JPG_DATA_URI);
        verify(imageFileExport,
               never()).export(any(ImageDataUriContent.class),
                               anyString());
        tested.exportToPdf(canvasHandler,
                           "file1");
        verify(pdfFileExport,
               times(1)).export(any(PdfDocument.class),
                                eq("file1.pdf"));
    }

    @Test
    public void testExportToSVG() {
        when(canvasExport.toContext2D(eq(canvasHandler), any(CanvasExportSettings.class))).thenReturn(context2D);
        tested.exportToSvg(canvasHandler, "file1");
        verify(svgFileExport, times(1)).export(context2D, "file1.svg");
    }

    @Test
    public void testExportToPNG() {

        final String expectedImage = "image";

        when(canvasExport.toImageData(any(), any())).thenReturn(expectedImage);

        final String actualImage = tested.exportToPng(canvasHandler);

        assertEquals(expectedImage, actualImage);
    }
}
