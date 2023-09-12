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

import elemental2.dom.Blob;
import elemental2.dom.BlobPropertyBag;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@PrepareForTest({BlobPropertyBag.class, ImageFileExport.class })
@RunWith(PowerMockRunner.class)
public class ImageFileExportTest extends AbstractFileExportTest {

    private ImageFileExport tested;

    @Before
    public void setup() {
        tested = spy(new ImageFileExport(fileSaver));

        BlobPropertyBag bag = mock(BlobPropertyBag.class);
        Blob blob = mock(Blob.class);

        PowerMockito.mockStatic(BlobPropertyBag.class);
        PowerMockito.mockStatic(ImageFileExport.class);

        when(BlobPropertyBag.create()).thenReturn(bag);
        when(ImageFileExport.dataImageAsBlob(anyString(), anyString())).thenReturn(blob);
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
                      FILE_NAME);
        verify(fileSaver,
               times(1)).accept(any(Blob.class),
                                eq(FILE_NAME));
    }
}
