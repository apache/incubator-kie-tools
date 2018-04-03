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
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class LibraryAssetTypeDefinitionTest {

    private String fileName;

    private boolean isFolder;

    private boolean isAccepted;

    private LibraryIndexer definition;

    @Before
    public void setup() {

        ResourceTypeDefinition resourceTypeDefinition = mock(ResourceTypeDefinition.class);
        when(resourceTypeDefinition.accept(any())).thenReturn(isAccepted);

        final Path p = mock(Path.class);
        when(p.getFileName()).thenReturn(fileName);

        this.definition = spy(new LibraryIndexer());

        doReturn(this.isFolder).when(this.definition).isFolder(any());
        doReturn(new HashSet<>(Arrays.asList(resourceTypeDefinition))).when(this.definition).getVisibleResourceTypes();
        doReturn(p).when(this.definition).convertPath(any());
        doReturn(fileName.startsWith(".")).when(this.definition).isHidden(any());
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
                        {".folder", true, false},
                        {"persistence.xml", false, false},
                        {"pom.xml", false, false},
                        {"kmodule.xml", false, false},
                        {"project.repositories", false, false},
                        {"kie-deployment-descriptor.xml", false, false}
                }
        );
    }

    @Test
    public void checkAccept() {
        final org.uberfire.java.nio.file.Path nioPath = mock(org.uberfire.java.nio.file.Path.class);
        assertEquals(isAccepted,
                     definition.supportsPath(nioPath));
    }
}
