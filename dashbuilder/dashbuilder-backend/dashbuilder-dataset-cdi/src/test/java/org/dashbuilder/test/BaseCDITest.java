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
package org.dashbuilder.test;

import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

import static org.mockito.Mockito.*;

public class BaseCDITest {

    @Deployment
    public static Archive<?> createTestArchive()  {
        return ShrinkWrapHelper.createJavaArchive().addPackages(true,
                                                             "org.uberfire.backend.server.spaces")
                .addAsManifestResource(EmptyAsset.INSTANCE, "org.dashbuilder.pojo.Bean.config")
                .addAsManifestResource(EmptyAsset.INSTANCE, "org.dashbuilder.pojo.BeanExt.config")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.config")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    protected IOService ioService;

    @Produces @Named("ioStrategy")
    public IOService mockIOService() throws Exception {
        return getIOService();
    }

    public IOService getIOService() throws Exception {
        if (ioService == null) {
            ioService = mock(IOService.class);
            FileSystem fileSystem = mock(FileSystem.class);
            Iterable iterable = mock(Iterable.class);
            Iterator iterator = mock(Iterator.class);
            Path path = mock(Path.class);

            when(ioService.newFileSystem(any(URI.class), any(Map.class))).thenReturn(fileSystem);
            when(ioService.getFileSystem(any(URI.class))).thenReturn(fileSystem);

            when(fileSystem.getRootDirectories()).thenReturn(iterable);
            when(fileSystem.supportedFileAttributeViews()).thenReturn(new HashSet<String>());
            when(iterable.iterator()).thenReturn(iterator);
            when(iterator.next()).thenReturn(path);

            when(path.resolve(anyString())).thenReturn(path);
            when(path.resolve(any(Path.class))).thenReturn(path);
            when(path.toUri()).thenReturn(new URI("uri"));
            when(path.getFileName()).thenReturn(path);
            when(path.getFileSystem()).thenReturn(fileSystem);
        }
        return ioService;
    }
}
