/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.workbench.type;

import org.junit.Test;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.uberfire.workbench.type.FileNameUtil.removeExtension;
import static org.junit.Assert.assertEquals;

public class FileNameUtilTest {

    @Test
    public void removeExtensionFromFileNameTest() {
        assertNull(removeExtension((String) null, ".java"));
        assertEquals("file.java", removeExtension("file.java", null));
        assertEquals("file.java", removeExtension("file.java", ""));
        assertEquals("file", removeExtension("file.java", "java"));
    }

    @Test
    public void removeExtensionFromPathTest() {
        final Path path = mock(Path.class);
        doReturn("file.java").when(path).getFileName();

        assertNull(removeExtension((Path) null, ".java"));
        assertEquals("file.java", removeExtension(path, (String) null));
        assertEquals("file.java", removeExtension(path, ""));
        assertEquals("file", removeExtension(path, "java"));
    }

    @Test
    public void removeResourceTypeDefinitionSuffixFromPathTest() {
        final Path path = mock(Path.class);
        doReturn("file.java").when(path).getFileName();

        final ResourceTypeDefinition resourceTypeDefinition = mock(ResourceTypeDefinition.class);
        doReturn("java").when(resourceTypeDefinition).getSuffix();

        assertNull(removeExtension((Path) null, ".java"));
        assertEquals("file", removeExtension(path, resourceTypeDefinition));
    }
}
