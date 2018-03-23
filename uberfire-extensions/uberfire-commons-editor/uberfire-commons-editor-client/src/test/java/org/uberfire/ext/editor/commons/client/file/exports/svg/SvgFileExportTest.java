/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.editor.commons.client.file.exports.svg;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.Blob;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.ext.editor.commons.client.file.exports.AbstractFileExportTest;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class SvgFileExportTest extends AbstractFileExportTest{

    private static final String SVG = "svg content";
    private SvgFileExport svgFileExport;

    @Mock
    private IContext2D context;

    @Before
    public void setUp() throws Exception {
        Mockito.when(context.getSerializedSvg()).thenReturn(SVG);
        this.svgFileExport = new SvgFileExport(fileSaver);
    }

    @Test
    public void testExport() {
        svgFileExport.export(context,FILE_NAME);
        verify(context).getSerializedSvg();
        verify(fileSaver).accept(any(Blob.class), eq(FILE_NAME));
    }
}