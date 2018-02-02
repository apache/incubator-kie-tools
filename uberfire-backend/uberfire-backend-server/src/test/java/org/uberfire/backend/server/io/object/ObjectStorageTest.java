/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.backend.server.io.object;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.mocks.FileSystemTestingUtils;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.*;

public class ObjectStorageTest {

    private static FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();

    static {
        System.setProperty("errai.devel.nocache",
                           "true");
        System.out.println("Working Dir: " + new File("").getAbsoluteFile().getAbsolutePath());
    }

    private IOService ioService;
    private FileSystem fileSystem;
    private SessionInfo sessionInfo;
    private ObjectStorageImpl objectStorage;

    @Before
    public void setup() throws IOException {
        fileSystemTestingUtils.setup();
        sessionInfo = mockSessionInfo();
        fileSystem = mockFileSystem();
        ioService = mockIoService(fileSystem);
        objectStorage = new ObjectStorageImpl(ioService);
        objectStorage.init(URI.create("default://object/store"));
    }

    @After
    public void cleanupFileSystem() {
        fileSystemTestingUtils.cleanup();
    }

    @Test
    public void testForAFileDoesNotExist() {
        final String file = "my.object.file";
        final boolean objectExists = objectStorage.exists("/forks/" + file);

        assertFalse(objectExists);
    }

    @Test
    public void testWriteFileAndCheckThatExists() {
        objectStorage.write("/forks/uberfire.txt",
                            "content");

        boolean exists = objectStorage.exists("/forks/uberfire.txt");
        assertTrue(exists);
    }

    @Test
    public void testWriteAndRead() {
        final String fileContent = "content";
        objectStorage.write("/forks/uberfire",
                            fileContent);

        String content = objectStorage.read("/forks/uberfire");
        assertEquals(fileContent,
                     content);
    }

    @Test
    public void testWriteAndReadList() {
        final ArrayList<String> fileContent = new ArrayList<String>();
        fileContent.add("uberfire");
        objectStorage.write("/forks/uberfire",
                            fileContent);

        List<String> content = objectStorage.read("/forks/uberfire");
        assertEquals(fileContent.size(),
                     content.size());
        assertEquals(fileContent.get(0),
                     content.get(0));
    }

    @Test
    public void testWithDoubleSlashPath() {

        final String fileContent = "content";
        objectStorage.write("/forks/uberfire",
                            fileContent);

        String content = objectStorage.read("/forks/uberfire");
        assertEquals(fileContent,
                     content);
    }

    @Test
    public void testWriteTwoTimes() {

        objectStorage.write("/forks/uberfire",
                            "content a");
        objectStorage.write("/forks/uberfire",
                            "content b");

        String content = objectStorage.read("/forks/uberfire");
        assertEquals("content b",
                     content);
    }

    @Test
    public void testWriteAndDelete() {

        objectStorage.write("/forks/uberfire",
                            "content b");
        assertTrue(objectStorage.exists("/forks/uberfire"));
        objectStorage.delete("/forks/uberfire");
        assertFalse(objectStorage.exists("/forks/uberfire"));
    }

    private SessionInfo mockSessionInfo() {
        return new SessionInfoMock();
    }

    private FileSystem mockFileSystem() {
        return fileSystemTestingUtils.getFileSystem();
    }

    private IOService mockIoService(final FileSystem fileSystem) {
        final IOService ioService = spy(fileSystemTestingUtils.getIoService());

        doNothing().when(ioService).startBatch(any(FileSystem.class));
        doNothing().when(ioService).endBatch();
        doReturn(fileSystem).when(ioService).newFileSystem(any(URI.class),
                                                           anyMap());

        return ioService;
    }
}
