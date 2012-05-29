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

import org.drools.java.nio.IOException;
import org.junit.Test;

import static org.junit.Assert.*;

public class TempTest {

    @Test
    public void createTempFile() {
        try {
            final Path file = Files.createTempFile("foo", null);
            assertNotNull(file);
            assertTrue(file.isAbsolute());
            assertNotNull(file.toUri());
            assertEquals("default", file.toUri().getScheme());

            final Path realFile = file.toRealPath();
            assertNotNull(realFile);
            assertTrue(realFile.isAbsolute());
            assertNotNull(realFile.toUri());
            assertEquals("file", realFile.toUri().getScheme());
        } catch (IOException e) {
            fail("unexpected exception");
        }
    }

    @Test
    public void createTempDirectory() {
        try {
            final Path dir = Files.createTempDirectory("foo", null);
            assertNotNull(dir);
            assertTrue(dir.isAbsolute());
            assertNotNull(dir.toUri());
            assertEquals("default", dir.toUri().getScheme());

            final Path realFile = dir.toRealPath();
            assertNotNull(realFile);
            assertTrue(realFile.isAbsolute());
            assertNotNull(realFile.toUri());
            assertEquals("file", realFile.toUri().getScheme());
        } catch (IOException e) {
            fail("unexpected exception");
        }
    }

}
