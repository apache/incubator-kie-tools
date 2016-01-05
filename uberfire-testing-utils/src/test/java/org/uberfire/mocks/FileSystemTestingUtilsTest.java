/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.mocks;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.*;

public class FileSystemTestingUtilsTest {

    private static FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();

    @BeforeClass
    public static void setup() throws IOException {
        fileSystemTestingUtils.setup();
    }

    @AfterClass
    public static void cleanup() {
        fileSystemTestingUtils.cleanup();
    }

    @Test
    public void fsUtilsSanityCheck() throws IOException, InterruptedException {
        IOService ioService = fileSystemTestingUtils.getIoService();
        Path init = ioService.get(URI.create("git://amend-repo-test/init.file"));
        String expected = "setupFS!";
        ioService.write(init, expected);
        assertEquals(expected, ioService.readAllString(init));
    }
}