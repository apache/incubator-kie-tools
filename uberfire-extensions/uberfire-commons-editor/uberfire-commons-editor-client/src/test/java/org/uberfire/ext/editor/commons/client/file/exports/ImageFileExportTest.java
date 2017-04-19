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

import java.util.function.BiConsumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.Blob;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ImageFileExportTest {

    @Mock
    private BiConsumer<Blob, String> fileSaver;

    private ImageFileExport tested;

    @Before
    public void setup() {
        tested = new ImageFileExport(fileSaver);
    }

    @Test
    public void testExport() {
        ImageDataUriContent imageContent = ImageDataUriContent.create("data:image/jpeg;base64,9j/4AAQSkZJRgABAQEASABIAAD");
        assertEquals("data:image/jpeg;base64,9j/4AAQSkZJRgABAQEASABIAAD",
                     imageContent.getUri());
        assertEquals("image/jpeg",
                     imageContent.getMimeType());
        assertEquals("9j/4AAQSkZJRgABAQEASABIAAD",
                     imageContent.getData());
        tested.export(imageContent,
                      "image-file");
        verify(fileSaver,
               times(1)).accept(any(Blob.class),
                                eq("image-file"));
    }
}
