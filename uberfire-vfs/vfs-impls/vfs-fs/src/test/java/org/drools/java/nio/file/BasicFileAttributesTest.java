/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.java.nio.file;

import java.util.Map;

import org.drools.java.nio.IOException;
import org.drools.java.nio.file.attribute.BasicFileAttributeView;
import org.drools.java.nio.file.attribute.BasicFileAttributes;
import org.junit.Test;

import static org.junit.Assert.*;

public class BasicFileAttributesTest {

    @Test
    public void checkBasicAttributes() {
        try {
            final Path file = Files.createTempFile("foo", null);

            final BasicFileAttributes attributes = Files.readAttributes(file, BasicFileAttributes.class);

            final BasicFileAttributeView attributesView = Files.getFileAttributeView(file, BasicFileAttributeView.class);

            assertNotNull(attributes);
            assertNotNull(attributesView);
            assertEquals(attributes, attributesView.readAttributes());
            assertNotNull(attributesView.readAttributes());
            assertFalse(attributesView.readAttributes().isDirectory());
            assertTrue(attributesView.readAttributes().isRegularFile());
        } catch (IOException e) {
            fail("unexpected exception");
        }
    }

    @Test
    public void checkStarAttributes() {
        try {
            final Path file = Files.createTempFile("foo", null);

            final Map<String, Object> attributes = Files.readAttributes(file, "*");

            assertNotNull(attributes);
            assertFalse((Boolean) attributes.get("isDirectory"));
            assertTrue((Boolean) attributes.get("isRegularFile"));
        } catch (IOException e) {
            fail("unexpected exception");
        }
    }

}
