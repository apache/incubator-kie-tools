/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.backend.metadata.attribute;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.java.nio.base.AbstractPath;
import org.uberfire.java.nio.base.AttrsStorage;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GeneratedFileAttributesViewTest {

    @Test
    public void readAttributesGeneratedFile() {
        readAttributesTest(true);
    }

    @Test
    public void readAttributesNonGeneratedFile() {
        readAttributesTest(false);
    }

    private void readAttributesTest(final boolean generated) {
        AttrsStorage attrsStorage = mock(AttrsStorage.class);
        when(attrsStorage.getContent()).thenReturn(new HashMap<String, Object>() {{
            put(GeneratedAttributesView.GENERATED_ATTRIBUTE_NAME,
                generated);
        }});
        AbstractPath path = mock(AbstractPath.class);
        when(path.getAttrStorage()).thenReturn(attrsStorage);

        FileSystemProvider fileSystemProvider = mock(FileSystemProvider.class);
        BasicFileAttributeView basicFileAttributeView = mock(BasicFileAttributeView.class);
        when(basicFileAttributeView.readAttributes()).thenReturn(mock(BasicFileAttributes.class));
        when(fileSystemProvider.getFileAttributeView(any(),
                                                     any())).thenReturn(basicFileAttributeView);
        FileSystem fileSystem = mock(FileSystem.class);
        when(fileSystem.provider()).thenReturn(fileSystemProvider);
        when(path.getFileSystem()).thenReturn(fileSystem);

        GeneratedAttributesView view = new GeneratedAttributesView(path);

        GeneratedFileAttributes generatedFileAttributes = view.readAttributes();

        assertEquals(generated,
                     generatedFileAttributes.isGenerated());
    }
}
