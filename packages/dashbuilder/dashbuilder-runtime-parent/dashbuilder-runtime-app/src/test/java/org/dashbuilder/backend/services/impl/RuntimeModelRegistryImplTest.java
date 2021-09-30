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

package org.dashbuilder.backend.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.event.Event;

import org.apache.commons.io.FilenameUtils;
import org.dashbuilder.backend.RuntimeOptions;
import org.dashbuilder.shared.event.RemovedRuntimeModelEvent;
import org.dashbuilder.shared.model.DashbuilderRuntimeMode;
import org.dashbuilder.shared.model.RuntimeModel;
import org.dashbuilder.shared.service.ImportValidationService;
import org.dashbuilder.shared.service.RuntimeModelParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RuntimeModelRegistryImplTest {

    @Mock
    ImportValidationService importValidationService;

    @Mock
    Map<String, RuntimeModel> runtimeModels;

    @Mock
    RuntimeModelParser parser;

    @Mock
    RuntimeOptions options;

    @Mock
    Event<RemovedRuntimeModelEvent> removedRuntimeModelEvent;

    @InjectMocks
    RuntimeModelRegistryImpl registry;

    private Path tempFile;
    private Path tempFile2;

    @Before
    public void init() throws IOException {
        tempFile = Files.createTempFile("test", RuntimeOptions.DASHBOARD_EXTENSION);
        tempFile2 = Files.createTempFile("test", RuntimeOptions.DASHBOARD_EXTENSION);
        registry.setRemovedRuntimeModelEvent(removedRuntimeModelEvent);
    }

    @After
    public void cleanup() throws IOException {
        Files.deleteIfExists(tempFile);
        Files.deleteIfExists(tempFile2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterFileEmpty() {
        registry.registerFile("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterFileNull() {
        registry.registerFile(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterFileNotExist() {
        registry.registerFile("file");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterFileInvalid() throws IOException {
        String file = tempFile.toString();

        when(importValidationService.validate(file)).thenReturn(false);

        registry.registerFile(file);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterFileNotAcceptingImports() throws IOException {
        String file = tempFile.toString();

        when(importValidationService.validate(file)).thenReturn(true);
        // to make sure that it is not accepting new imports
        registry.setMode(DashbuilderRuntimeMode.STATIC);

        registry.registerFile(file);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterFileParsingError() throws IOException {
        String file = tempFile.toString();
        String importId = FilenameUtils.getBaseName(tempFile.toFile().getPath());

        when(runtimeModels.isEmpty()).thenReturn(true);
        when(importValidationService.validate(file)).thenReturn(true);
        when(parser.parse(eq(importId), any())).thenThrow(new RuntimeException());

        registry.registerFile(file);
    }

    @Test
    public void testRegisterFileSuccess() throws IOException {
        String file = tempFile.toString();
        String importId = FilenameUtils.getBaseName(tempFile.toFile().getPath());
        RuntimeModel runtimeModel = mock(RuntimeModel.class);

        when(runtimeModels.isEmpty()).thenReturn(true);
        when(importValidationService.validate(file)).thenReturn(true);
        when(parser.parse(eq(importId), any())).thenReturn(runtimeModel);

        registry.registerFile(file);

        verify(runtimeModels).put(eq(importId), eq(runtimeModel));
    }

    @Test
    public void testRegisterFileSuccessMultiMode() throws IOException {
        var file1 = tempFile.toString();
        var file2 = tempFile2.toString();

        var importId1 = FilenameUtils.getBaseName(tempFile.toFile().getPath());
        var importId2 = FilenameUtils.getBaseName(tempFile2.toFile().getPath());

        var runtimeModel1 = mock(RuntimeModel.class);
        var runtimeModel2 = mock(RuntimeModel.class);
        
        when(importValidationService.validate(or(eq(file1), eq(file2)))).thenReturn(true);
        when(parser.parse(eq(importId1), any())).thenReturn(runtimeModel1);
        when(parser.parse(eq(importId2), any())).thenReturn(runtimeModel2);

        registry.setMode(DashbuilderRuntimeMode.MULTIPLE_IMPORT);

        registry.registerFile(file1);
        verify(runtimeModels).put(eq(importId1), eq(runtimeModel1));

        registry.registerFile(file2);
        verify(runtimeModels).put(eq(importId2), eq(runtimeModel2));
    }
    
    

    @Test
    public void testSingle() {
        RuntimeModel model1 = mock(RuntimeModel.class);
        when(runtimeModels.values()).thenReturn(Collections.singleton(model1));
        assertEquals(model1, registry.single().get());
    }

    @Test
    public void testGetInMultipleMode() {
        registry.setMode(DashbuilderRuntimeMode.MULTIPLE_IMPORT);
        var id = "ID";
        registry.get(id);
        verify(runtimeModels).get(eq(id));
    }

    @Test
    public void testGetWithSingleMode() {
        registry.setMode(DashbuilderRuntimeMode.SINGLE_IMPORT);
        String id = "ID";
        RuntimeModel model1 = mock(RuntimeModel.class);

        when(runtimeModels.values()).thenReturn(Collections.singleton(model1));

        assertEquals(model1, registry.single().get());

        verify(runtimeModels, times(0)).get(eq(id));
        verify(runtimeModels).values();
    }

    @Test
    public void testRemoveExistingModel() throws IOException {
        String file = tempFile.toString();

        String importId = FilenameUtils.getBaseName(tempFile.toFile().getPath());
        RuntimeModel runtimeModel = mock(RuntimeModel.class);

        when(options.buildFilePath(eq(importId))).thenReturn(tempFile.toFile().getPath());
        when(runtimeModels.remove(eq(importId))).thenReturn(runtimeModel);

        when(options.isRemoveModelFile()).thenReturn(true);

        registry.remove(importId);

        verify(runtimeModels).remove(eq(importId));
        verify(removedRuntimeModelEvent).fire(any());
        assertFalse(Files.exists(Paths.get(file)));
    }

    @Test
    public void testRemoveWithoutDeletingFile() throws IOException {
        String file = tempFile.toString();
        String importId = FilenameUtils.getBaseName(tempFile.toFile().getPath());
        RuntimeModel runtimeModel = mock(RuntimeModel.class);

        when(runtimeModels.remove(eq(importId))).thenReturn(runtimeModel);

        when(options.isRemoveModelFile()).thenReturn(false);

        registry.remove(importId);

        verify(runtimeModels).remove(eq(importId));
        verify(removedRuntimeModelEvent).fire(any());
        assertTrue(Files.exists(Paths.get(file)));
    }

    @Test
    public void testRemoveNotExistingModel() throws IOException {
        String file = tempFile.toString();
        String importId = FilenameUtils.getBaseName(tempFile.toFile().getPath());

        registry.remove(importId);

        verify(runtimeModels).remove(eq(importId));
        verify(removedRuntimeModelEvent, times(0)).fire(any());
        assertTrue(Files.exists(Paths.get(file)));
    }

    @Test
    public void testClear() throws IOException {
        String importId1 = FilenameUtils.getBaseName(tempFile.toFile().getPath());
        String importId2 = FilenameUtils.getBaseName(tempFile2.toFile().getPath());

        when(options.buildFilePath(eq(importId1))).thenReturn(tempFile.toFile().getPath());
        when(options.buildFilePath(eq(importId2))).thenReturn(tempFile2.toFile().getPath());
        when(options.isRemoveModelFile()).thenReturn(true);

        Set<String> keys = new HashSet<>();
        keys.add(importId1);
        keys.add(importId2);
        when(runtimeModels.keySet()).thenReturn(keys);
        
        when(runtimeModels.remove(importId1)).thenReturn(mock(RuntimeModel.class));
        when(runtimeModels.remove(importId2)).thenReturn(mock(RuntimeModel.class));

        registry.clear();

        verify(removedRuntimeModelEvent, times(2)).fire(any());
        verify(runtimeModels).clear();
        assertFalse(Files.exists(Paths.get(tempFile.toString())));
        assertFalse(Files.exists(Paths.get(tempFile2.toString())));

    }

}