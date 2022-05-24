/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.external.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.dashbuilder.external.service.ComponentLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ComponentAssetProviderImplTest {

    @Mock
    ComponentLoader componentLoader;

    @InjectMocks
    ComponentAssetProviderImpl componentAssetProviderImpl;

    private Path componentsDir;

    @Before
    public void prepare() throws IOException {
        componentsDir = Files.createTempDirectory("components");
        when(componentLoader.getExternalComponentsDir()).thenReturn(componentsDir.toString());

    }

    @After
    public void after() {
        FileUtils.deleteQuietly(componentsDir.toFile());
    }

    @Test
    public void testExternalComponentAsset() throws Exception {
        String componentFileContent = "abc";
        String componentId = "c1";
        String componentFileName = "testFile";
        String assetPath = createComponentFile(componentId, componentFileName, componentFileContent);

        when(componentLoader.isExternalComponentsEnabled()).thenReturn(true);
        String assetFileLoadedContent = IOUtils.toString(componentAssetProviderImpl.openAsset(assetPath), StandardCharsets.UTF_8);

        assertEquals(componentFileContent, assetFileLoadedContent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExternalComponentAssetWithExternalComponentsDisabled() throws Exception {
        String componentFileContent = "abc";
        String componentId = "c1";
        String componentFileName = "testFile";

        String assetPath = createComponentFile(componentId, componentFileName, componentFileContent);

        when(componentLoader.isExternalComponentsEnabled()).thenReturn(false);
        componentAssetProviderImpl.openAsset(assetPath);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAvoidTraversalPath() throws Exception {
        String componentFileContent = "abc";
        String componentId = "c1";
        String componentFileName = "testFile";

        createComponentFile(componentId, componentFileName, componentFileContent);
        Path shouldNotBeAccessible = Files.createTempFile("should_not_be_accessible", "");
        Path relativePath = componentsDir.relativize(shouldNotBeAccessible);

        when(componentLoader.isExternalComponentsEnabled()).thenReturn(true);
        try {
            componentAssetProviderImpl.openAsset(relativePath.toString());
        } catch (Exception e) {
            throw e;
        } finally {
            FileUtils.deleteQuietly(shouldNotBeAccessible.toFile());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInternalComponentAssetPathTraversal() throws Exception {
        assertNotNull(componentAssetProviderImpl.openAsset("../../../dashbuilder-components.properties"));
    }
    
    @Test
    public void testFixSlashes() throws Exception {
        assertEquals("/abc/cde", componentAssetProviderImpl.fixSlashes("\\abc\\cde"));
        assertEquals("/abc/cde", componentAssetProviderImpl.fixSlashes("/abc/cde"));
        assertEquals("", componentAssetProviderImpl.fixSlashes(null));
    }

    private String createComponentFile(String componentId, String fileName, String fileContent) throws Exception {
        Path componentDir = componentsDir.resolve(componentId);
        Path componentFile = componentDir.resolve(fileName);

        if (!componentDir.toFile().exists()) {
            Files.createDirectory(componentDir);
        }
        Files.createFile(componentFile);
        Files.write(componentFile, fileContent.getBytes(StandardCharsets.UTF_8));

        return componentId + "/" + fileName;
    }

}