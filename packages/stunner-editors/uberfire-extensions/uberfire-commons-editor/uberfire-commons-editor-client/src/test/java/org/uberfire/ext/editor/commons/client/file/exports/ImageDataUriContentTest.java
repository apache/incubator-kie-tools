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

import static org.junit.Assert.*;

public class ImageDataUriContentTest {

    private ImageDataUriContent imageContent;

    @Before
    public void setup() {
    }

    @Test
    public void testPng() {
        this.imageContent = ImageDataUriContent.create("data:image/png;base64,9j/4AAQSkZJRgABAQEASABIAAD");
        assertEquals("data:image/png;base64,9j/4AAQSkZJRgABAQEASABIAAD",
                     imageContent.getUri());
        assertEquals("image/png",
                     imageContent.getMimeType());
        assertEquals("9j/4AAQSkZJRgABAQEASABIAAD",
                     imageContent.getData());
    }

    @Test
    public void testJpg() {
        this.imageContent = ImageDataUriContent.create("data:image/jpg;base64,9j/4AAQSkZJRgABAQEASABIAAD");
        assertEquals("data:image/jpg;base64,9j/4AAQSkZJRgABAQEASABIAAD",
                     imageContent.getUri());
        assertEquals("image/jpg",
                     imageContent.getMimeType());
        assertEquals("9j/4AAQSkZJRgABAQEASABIAAD",
                     imageContent.getData());
    }

    @Test
    public void testOtherTypes() {
        this.imageContent = ImageDataUriContent.create("data:image/svg;base64,9j/4AAQSkZJRgABAQEASABIAAD");
        assertEquals("data:image/svg;base64,9j/4AAQSkZJRgABAQEASABIAAD",
                     imageContent.getUri());
        assertEquals("image/svg",
                     imageContent.getMimeType());
        assertEquals("9j/4AAQSkZJRgABAQEASABIAAD",
                     imageContent.getData());
        this.imageContent = ImageDataUriContent.create("data:image/svg,9j/4AAQSkZJRgABAQEASABIAAD");
        assertEquals("data:image/svg,9j/4AAQSkZJRgABAQEASABIAAD",
                     imageContent.getUri());
        assertEquals("image/svg",
                     imageContent.getMimeType());
        assertEquals("9j/4AAQSkZJRgABAQEASABIAAD",
                     imageContent.getData());
    }
}
