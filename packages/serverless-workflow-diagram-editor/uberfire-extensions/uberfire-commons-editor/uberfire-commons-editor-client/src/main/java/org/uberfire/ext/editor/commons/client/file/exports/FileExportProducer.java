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


package org.uberfire.ext.editor.commons.client.file.exports;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.uberfire.ext.editor.commons.client.file.exports.jso.FileExportScriptInjector;
import org.uberfire.ext.editor.commons.client.file.exports.svg.SvgFileExport;

/**
 * The FileExport bean factory.
 * Also ensures the sources are injected on demand.
 */
@ApplicationScoped
public class FileExportProducer {

    private final FileExportScriptInjector fsScriptInjector;

    protected FileExportProducer() {
        this(null);
    }

    @Inject
    public FileExportProducer(final FileExportScriptInjector fsScriptInjector) {
        this.fsScriptInjector = fsScriptInjector;
    }

    @PostConstruct
    public void init() {
        fsScriptInjector.inject();
    }

    @Produces
    public TextFileExport forText() {
        return new TextFileExport();
    }

    @Produces
    public PdfFileExport forPDF() {
        return new PdfFileExport();
    }

    @Produces
    public ImageFileExport forImage() {
        return new ImageFileExport();
    }

    @Produces
    public SvgFileExport forSvg() {
        return new SvgFileExport();
    }
}
