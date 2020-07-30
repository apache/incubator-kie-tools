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
import java.util.Collections;
import java.util.Map;

import org.dashbuilder.backend.RuntimeOptions;
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
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.apache.commons.io.FilenameUtils;

import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
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

    @InjectMocks
    RuntimeModelRegistryImpl registry;

    private Path tempFile;
    private Path tempFile2;

    @Before
    public void init() throws IOException {
        tempFile = Files.createTempFile("test", RuntimeOptions.DASHBOARD_EXTENSION);
        tempFile2 = Files.createTempFile("test", RuntimeOptions.DASHBOARD_EXTENSION);
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
        
        String file1 = tempFile.toString();
        String file2 = tempFile2.toString();
        
        String importId1 = FilenameUtils.getBaseName(tempFile.toFile().getPath());
        String importId2 = FilenameUtils.getBaseName(tempFile2.toFile().getPath());
        
        RuntimeModel runtimeModel1 = mock(RuntimeModel.class);
        RuntimeModel runtimeModel2 = mock(RuntimeModel.class);
        
        when(runtimeModels.isEmpty()).thenReturn(true);
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
        String id = "ID";
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
        
        verify(runtimeModels, Mockito.times(0)).get(eq(id));
        verify(runtimeModels).values();
    }

}