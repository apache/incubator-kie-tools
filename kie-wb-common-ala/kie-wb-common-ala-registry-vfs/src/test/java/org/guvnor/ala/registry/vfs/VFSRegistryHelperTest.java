/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.registry.vfs;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.guvnor.ala.marshalling.Marshaller;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

import static org.guvnor.ala.AlaSPITestCommons.mockList;
import static org.guvnor.ala.registry.vfs.VFSRegistryHelper.PROVISIONING_BRANCH;
import static org.guvnor.ala.registry.vfs.VFSRegistryHelper.PROVISIONING_PATH;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VFSRegistryHelperTest {

    private static final String DIRECTORY_NAME = "DIRECTORY_NAME";

    private static final String MARSHALLED_VALUE = "MARSHALLED_VALUE";

    private static final String MARSHALLED_ENTRY = "MARSHALLED_ENTRY";

    private static final String ERROR_MESSAGE = "ERROR_MESSAGE";

    private static final int ENTRY_COUNT = 10;

    @Mock
    private VFSMarshallerRegistry marshallerRegistry;

    @Mock
    private IOService ioService;

    @Mock
    private FileSystem fileSystem;

    private VFSRegistryHelper registryHelper;

    @Mock
    private Path provisioningPath;

    @Mock
    private VFSRegistryEntryMarshaller entryMarshaller;

    @Mock
    private Path path;

    @Mock
    private Marshaller marshaller;

    @Mock
    private Object value;

    @Mock
    private VFSRegistryEntry entry;

    @Mock
    private Path rootPath;

    private List<Path> entryPaths;

    private List<Object> expectedObjects;

    private List<VFSRegistryEntry> entries;

    @Mock
    private DirectoryStream.Filter<Path> filter;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        when(fileSystem.getPath(PROVISIONING_BRANCH,
                                PROVISIONING_PATH)).thenReturn(provisioningPath);

        when(marshallerRegistry.get(VFSRegistryEntry.class)).thenReturn(entryMarshaller);

        registryHelper = spy(new VFSRegistryHelper(marshallerRegistry,
                                                   ioService,
                                                   fileSystem));
        registryHelper.init();
        verify(fileSystem,
               times(1)).getPath(PROVISIONING_BRANCH,
                                 PROVISIONING_PATH);
    }

    @Test
    public void testEnsureDirectoryWhenDirectoryExists() {
        Path path = mock(Path.class);
        when(provisioningPath.resolve(DIRECTORY_NAME)).thenReturn(path);
        when(ioService.exists(path)).thenReturn(true);

        Path result = registryHelper.ensureDirectory(DIRECTORY_NAME);
        verify(provisioningPath,
               times(1)).resolve(DIRECTORY_NAME);
        verify(ioService,
               never()).createDirectory(path);

        assertEquals(path,
                     result);
    }

    @Test
    public void testEnsureDirectoryWhenDirectoryNotExists() {
        Path path = mock(Path.class);
        Path createdPath = mock(Path.class);
        when(provisioningPath.resolve(DIRECTORY_NAME)).thenReturn(path);
        when(ioService.exists(path)).thenReturn(false);
        when(ioService.createDirectory(path)).thenReturn(createdPath);

        Path result = registryHelper.ensureDirectory(DIRECTORY_NAME);
        verify(provisioningPath,
               times(1)).resolve(DIRECTORY_NAME);
        verify(ioService,
               times(1)).createDirectory(path);

        assertEquals(createdPath,
                     result);
    }

    @Test
    public void testMd5Hex() {
        String result = registryHelper.md5Hex(null);
        assertEquals("",
                     result);

        result = registryHelper.md5Hex(DIRECTORY_NAME);
        assertEquals(DigestUtils.md5Hex(DIRECTORY_NAME),
                     result);
    }

    @Test
    public void testStoreEntryWhenMarshallerNotExists() throws Exception {
        Object value = mock(Object.class);
        when(marshallerRegistry.get(value.getClass())).thenReturn(null);
        expectedException.expectMessage("No marshaller was found for class: " + value.getClass());

        registryHelper.storeEntry(mock(Path.class),
                                  value);
    }

    @Test
    public void testStoreEntryWhenMarshallerExists() throws Exception {
        when(marshallerRegistry.get(value.getClass())).thenReturn(marshaller);
        when(marshaller.marshal(value)).thenReturn(MARSHALLED_VALUE);

        VFSRegistryEntry expectedEntry = new VFSRegistryEntry(value.getClass().getName(),
                                                              MARSHALLED_VALUE);
        when(entryMarshaller.marshal(expectedEntry)).thenReturn(MARSHALLED_ENTRY);

        registryHelper.storeEntry(path,
                                  value);

        verify(marshallerRegistry,
               times(1)).get(value.getClass());
        verify(registryHelper,
               times(1)).writeBatch(path,
                                    MARSHALLED_ENTRY);
    }

    @Test
    public void testReadEntryWhenMarshallerNotExists() throws Exception {
        when(ioService.readAllString(path)).thenReturn(MARSHALLED_ENTRY);
        when(entryMarshaller.unmarshal(MARSHALLED_ENTRY)).thenReturn(entry);

        when(entry.getContentType()).thenReturn(Object.class.getName());
        when(marshallerRegistry.get(Object.class.getClass())).thenReturn(null);

        expectedException.expectMessage("No marshaller was found for class: " + entry.getContentType());
        registryHelper.readEntry(path);
    }

    @Test
    public void testReadEntry() throws Exception {
        when(ioService.readAllString(path)).thenReturn(MARSHALLED_ENTRY);
        when(entryMarshaller.unmarshal(MARSHALLED_ENTRY)).thenReturn(entry);

        when(entry.getContentType()).thenReturn(Object.class.getName());
        when(entry.getContent()).thenReturn(MARSHALLED_VALUE);
        when(marshallerRegistry.get(any(Class.class))).thenReturn(marshaller);

        Object unmarshalledValue = mock(Object.class);
        when(marshaller.unmarshal(MARSHALLED_VALUE)).thenReturn(unmarshalledValue);

        Object result = registryHelper.readEntry(path);
        assertEquals(unmarshalledValue,
                     result);
    }

    @Test
    public void testReadEntries() throws Exception {
        prepareReadEntries();

        List<Object> result = registryHelper.readEntries(rootPath,
                                                         filter);
        assertEquals(expectedObjects,
                     result);
        for (Path path : entryPaths) {
            verify(registryHelper,
                   times(1)).readEntry(path);
        }
    }

    @Test
    public void testReadEntriesWithError() throws Exception {
        prepareReadEntries();

        //make an arbitrary path reading to fail.
        int failingIndex = 5;
        when(marshaller.unmarshal(entries.get(failingIndex).getContent())).thenThrow(new IOException(ERROR_MESSAGE));

        expectedException.expectMessage(ERROR_MESSAGE);
        registryHelper.readEntries(rootPath,
                                   filter);
        for (int i = 0; i < failingIndex; i++) {
            verify(registryHelper,
                   times(1));
        }
    }

    @Test
    public void testWriteBatch() {
        when(path.getFileSystem()).thenReturn(fileSystem);
        registryHelper.writeBatch(path,
                                  MARSHALLED_VALUE);
        verify(ioService,
               times(1)).startBatch(fileSystem);
        verify(ioService,
               times(1)).write(path,
                               MARSHALLED_VALUE);
        verify(ioService,
               times(1)).endBatch();
    }

    @Test
    public void testDeleteBatch() {
        when(path.getFileSystem()).thenReturn(fileSystem);
        registryHelper.deleteBatch(path);

        verify(ioService,
               times(1)).startBatch(fileSystem);
        verify(ioService,
               times(1)).deleteIfExists(path);
        verify(ioService,
               times(1)).endBatch();
    }

    private void prepareReadEntries() throws Exception {
        entryPaths = mockList(Path.class,
                              ENTRY_COUNT);
        Iterator<Path> pathIterator = entryPaths.iterator();

        expectedObjects = mockList(Object.class,
                                   ENTRY_COUNT);

        entries = mockList(VFSRegistryEntry.class,
                           ENTRY_COUNT);

        DirectoryStream<Path> directoryStream = mock(DirectoryStream.class);
        when(directoryStream.iterator()).thenReturn(pathIterator);

        when(ioService.newDirectoryStream(rootPath,
                                          filter)).thenReturn(directoryStream);

        for (int i = 0; i < ENTRY_COUNT; i++) {
            VFSRegistryEntry entry = entries.get(i);
            when(ioService.readAllString(entryPaths.get(i))).thenReturn(MARSHALLED_ENTRY + i);
            when(entryMarshaller.unmarshal(MARSHALLED_ENTRY + i)).thenReturn(entry);
            when(entry.getContentType()).thenReturn(Object.class.getName());
            when(entry.getContent()).thenReturn(MARSHALLED_VALUE + i);
            when(marshallerRegistry.get(any(Class.class))).thenReturn(marshaller);
            when(marshaller.unmarshal(MARSHALLED_VALUE + i)).thenReturn(expectedObjects.get(i));
        }
    }
}
