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


package org.uberfire.ext.editor.commons.client.file.exports.svg;

import elemental2.dom.Blob;
import elemental2.dom.BlobPropertyBag;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.uberfire.ext.editor.commons.client.file.exports.AbstractFileExportTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@PrepareForTest({BlobPropertyBag.class, Blob.ConstructorBlobPartsArrayUnionType.class})
@RunWith(PowerMockRunner.class)
public class SvgFileExportTest extends AbstractFileExportTest{

    private static final String SVG = "svg content";
    private SvgFileExport svgFileExport;

    @Mock
    private IContext2D context;

    @Before
    public void setUp() throws Exception {
        Mockito.when(context.getSerializedSvg()).thenReturn(SVG);
        this.svgFileExport = new SvgFileExport(fileSaver);

        BlobPropertyBag bag = mock(BlobPropertyBag.class);
        Blob.ConstructorBlobPartsArrayUnionType constructorBlobPartsArrayUnionType = mock(Blob.ConstructorBlobPartsArrayUnionType.class);

        PowerMockito.mockStatic(BlobPropertyBag.class);
        PowerMockito.mockStatic(Blob.ConstructorBlobPartsArrayUnionType.class);

        when(BlobPropertyBag.create()).thenReturn(bag);
        when(Blob.ConstructorBlobPartsArrayUnionType.of(any())).thenReturn(constructorBlobPartsArrayUnionType);

    }

    @Test
    public void testExport() {
        svgFileExport.export(context,FILE_NAME);
        verify(context).getSerializedSvg();
        verify(fileSaver).accept(any(Blob.class), eq(FILE_NAME));
    }
}