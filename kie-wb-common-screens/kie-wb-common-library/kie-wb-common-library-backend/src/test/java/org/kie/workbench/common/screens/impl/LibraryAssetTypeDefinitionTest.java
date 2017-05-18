/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.impl;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class LibraryAssetTypeDefinitionTest {

    private String fileName;

    private boolean isFolder;

    private boolean isAccepted;

    private LibraryAssetTypeDefinition definition;

    @Before
    public void setup() {
        this.definition = new LibraryAssetTypeDefinition() {
            @Override
            boolean isFolder(final Path path) {
                return isFolder;
            }
        };
    }

    public LibraryAssetTypeDefinitionTest(final String fileName,
                                          final boolean isFolder,
                                          final boolean isAccepted) {
        this.fileName = fileName;
        this.isFolder = isFolder;
        this.isAccepted = isAccepted;
    }

    @Parameterized.Parameters(name = "{index}: file={0}, isFolder={1}, isAccepted={2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[][]{
                        {"file.drl", false, true},
                        {".file.drl", false, false},
                        {"folder", true, false},
                        {".folder", true, false}
                }
        );
    }

    @Test
    public void checkAccept() {
        final Path path = mock(Path.class);
        when(path.getFileName()).thenReturn(fileName);
        assertEquals(isAccepted,
                     definition.accept(path));
    }
}
