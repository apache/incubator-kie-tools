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

import java.io.IOException;
import java.net.URI;

import org.drools.java.nio.file.api.FileSystemProviders;
import org.drools.java.nio.file.spi.FileSystemProvider;
import org.junit.Test;

import static org.junit.Assert.*;

public class FileSystemProviderTest {

    @Test
    public void defaultMustExist() throws IOException {
        assertNotNull("default provider must exist", FileSystemProviders.getDefaultProvider());
    }

    @Test
    public void installedProviders() throws IOException {
        assertNotNull(FileSystemProviders.installedProviders());
        assertTrue(FileSystemProviders.installedProviders().size() >= 1);
    }

    @Test
    public void shouldResolveDefault() throws IOException {
        assertNotNull(FileSystemProviders.resolveProvider(URI.create("default:///")));
    }

    @Test
    public void shouldResolveInstalledProviders() throws IOException {
        for (FileSystemProvider provider : FileSystemProviders.installedProviders()) {
            assertNotNull(FileSystemProviders.resolveProvider(URI.create(provider.getScheme() + ":///")));
        }
    }

}
