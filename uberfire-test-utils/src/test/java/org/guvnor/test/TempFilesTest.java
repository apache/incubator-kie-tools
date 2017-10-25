/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.guvnor.test;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TempFilesTest {

    private TempFiles tempFiles;

    @Before
    public void setUp() throws Exception {
        tempFiles = new TempFiles();
    }

    @Test
    public void testDirectory() throws Exception {
        final File tempDirectory = tempFiles.createTempDirectory("mydir");

        assertTrue(tempDirectory.exists());

        tempFiles.deleteFiles();

        assertFalse(tempDirectory.exists());
    }

    @Test
    public void testFiles() throws Exception {
        final File pomFile = tempFiles.createTempFile("mydir/pom.xml");
        final File javaClass = tempFiles.createTempFile("mydir/org/test/Person.java");

        assertTrue(pomFile.exists());
        assertTrue(javaClass.exists());

        tempFiles.deleteFiles();

        assertFalse(pomFile.exists());
        assertFalse(javaClass.exists());
    }
}