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
import org.jboss.errai.common.client.dom.Blob;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class TextFileExportTest extends AbstractFileExportTest {

    private TextFileExport tested;

    @Before
    public void setup() {
        tested = new TextFileExport(fileSaver);
    }

    @Test
    public void testExport() {
        final TextContent content = TextContent.create("testing");
        tested.export(content, FILE_NAME);
        verify(fileSaver,
               times(1)).accept(any(Blob.class),
                                eq(FILE_NAME));
    }
}
