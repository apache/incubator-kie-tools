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

import java.util.function.Consumer;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.export.CanvasExport;
import org.kie.workbench.common.stunner.core.client.canvas.export.CanvasExportSettings;
import org.kie.workbench.common.stunner.core.client.canvas.export.CanvasURLExportSettings;
import org.uberfire.ext.editor.commons.client.file.exports.FileExport;
import org.uberfire.ext.editor.commons.client.file.exports.ImageDataUriContent;
import org.uberfire.ext.editor.commons.client.file.exports.ImageFileExport;
import org.uberfire.ext.editor.commons.client.file.exports.PdfDocument;
import org.uberfire.ext.editor.commons.client.file.exports.PdfFileExport;
import org.uberfire.ext.editor.commons.client.file.exports.svg.SvgFileExport;
import org.uberfire.ext.editor.commons.file.exports.FileExportsPreferences;
import org.uberfire.ext.editor.commons.file.exports.PdfExportPreferences;

/**
 * A helper client side bean that allows
 * exporting the canvas into different file types.
 */
@ApplicationScoped
public class CanvasFileExport {

    private static Logger LOGGER = Logger.getLogger(CanvasFileExport.class.getName());

    private static final String EXT_PNG = "png";
    private static final String EXT_JPG = "jpg";
    private static final String EXT_PDF = "pdf";
    private static final String EXT_SVG = "svg";

    private final CanvasExport<AbstractCanvasHandler> canvasExport;
    private final FileExport<ImageDataUriContent> imageFileExport;
    private final FileExport<PdfDocument> pdfFileExport;
    private final FileExportsPreferences preferences;
    private final SvgFileExport svgFileExport;

    protected CanvasFileExport() {
        this(null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public CanvasFileExport(final CanvasExport<AbstractCanvasHandler> canvasExport,
                            final ImageFileExport imageFileExport,
                            final PdfFileExport pdfFileExport,
                            final FileExportsPreferences preferences,
                            final SvgFileExport svgFileExport) {
        this.canvasExport = canvasExport;
        this.imageFileExport = imageFileExport;
        this.pdfFileExport = pdfFileExport;
        this.preferences = preferences;
        this.svgFileExport = svgFileExport;
    }

    public void exportToSvg(final AbstractCanvasHandler canvasHandler,
                            final String fileName) {
        final String fullFileName = fileName + "." + getFileExtension(CanvasExport.URLDataType.SVG);
        svgFileExport.export(canvasExport.toContext2D(canvasHandler, CanvasExportSettings.build()), fullFileName);
    }

    public String exportToSvg(final AbstractCanvasHandler canvasHandler) {
        return canvasExport.toContext2D(canvasHandler, CanvasExportSettings.build()).getSerializedSvg();
    }

    public String exportToPng(final AbstractCanvasHandler canvasHandler) {
        final CanvasURLExportSettings settings = CanvasURLExportSettings.build(CanvasExport.URLDataType.PNG);
        return canvasExport.toImageData(canvasHandler, settings);
    }

    public void exportToJpg(final AbstractCanvasHandler canvasHandler,
                            final String fileName) {
        exportImage(canvasHandler,
                    CanvasExport.URLDataType.JPG,
                    fileName);
    }

    public void exportToPng(final AbstractCanvasHandler canvasHandler,
                            final String fileName) {
        exportImage(canvasHandler,
                    CanvasExport.URLDataType.PNG,
                    fileName);
    }

    public void exportToPdf(final AbstractCanvasHandler canvasHandler,
                            final String fileName) {
        loadFileExportPreferences(prefs -> exportToPdf(canvasHandler,
                                                       fileName,
                                                       prefs.getPdfPreferences()));
    }

    private void exportToPdf(final AbstractCanvasHandler canvasHandler,
                             final String fileName,
                             final PdfExportPreferences pdfPreferences) {
        final String dataUrl = toDataImageURL(canvasHandler,
                                              CanvasExport.URLDataType.JPG);
        final String title = canvasHandler.getDiagram().getMetadata().getTitle();
        final PdfDocument content = PdfDocument.create(PdfExportPreferences.create(PdfExportPreferences.Orientation.LANDSCAPE,
                                                                                   pdfPreferences.getUnit(),
                                                                                   pdfPreferences.getFormat()));
        content.addText(title,
                        5,
                        15);
        content.addImage(dataUrl,
                         EXT_JPG,
                         5,
                         40,
                         290,
                         150);
        pdfFileExport.export(content,
                             fileName + "." + EXT_PDF);
    }

    private void exportImage(final AbstractCanvasHandler canvasHandler,
                             final CanvasExport.URLDataType type,
                             final String fileName) {
        final String dataUrl = toDataImageURL(canvasHandler,
                                              type);
        final ImageDataUriContent content = ImageDataUriContent.create(dataUrl);
        imageFileExport.export(content,
                               fileName + "." + getFileExtension(type));
    }

    private String toDataImageURL(final AbstractCanvasHandler canvasHandler,
                                  final CanvasExport.URLDataType urlDataType) {
        return canvasExport.toImageData(canvasHandler,
                                        CanvasURLExportSettings.build(urlDataType));
    }

    private static String getFileExtension(final CanvasExport.URLDataType type) {
        switch (type) {
            case JPG:
                return EXT_JPG;
            case PNG:
                return EXT_PNG;
            case SVG:
                return EXT_SVG;
        }
        throw new UnsupportedOperationException("No mimeType supported for " + type);
    }

    private void loadFileExportPreferences(final Consumer<FileExportsPreferences> preferencesConsumer) {
        preferencesConsumer.accept(preferences);
    }
}
