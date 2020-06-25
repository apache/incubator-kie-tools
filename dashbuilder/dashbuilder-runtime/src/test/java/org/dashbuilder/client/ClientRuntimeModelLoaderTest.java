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

package org.dashbuilder.client;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.perspective.RuntimePerspectiveGenerator;
import org.dashbuilder.shared.model.RuntimeModel;
import org.dashbuilder.shared.service.RuntimeModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ClientRuntimeModelLoaderTest {

    CallerMock<RuntimeModelService> importModelServiceCaller;

    @Mock
    RuntimeModelService runtimeModelService;

    @Mock
    RuntimePerspectiveGenerator runtimePerspectiveGenerator;

    ClientRuntimeModelLoader clientRuntimeModelLoader;

    @Before
    public void setup() {
        importModelServiceCaller = new CallerMock<>(runtimeModelService);
        clientRuntimeModelLoader = new ClientRuntimeModelLoader(importModelServiceCaller, runtimePerspectiveGenerator);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadModelSuccess() {
        LayoutTemplate perspective = mock(LayoutTemplate.class);
        List<LayoutTemplate> perspectives = Arrays.asList(perspective);
        RuntimeModel runtimeModel = new RuntimeModel(null, perspectives);
        when(runtimeModelService.getRuntimeModel(anyString())).thenReturn(Optional.of(runtimeModel));
       
        Consumer<RuntimeModel> runtimeModelConsumer = mock(Consumer.class);
        Command empty = mock(Command.class);
        BiConsumer<Object, Throwable> error = mock(BiConsumer.class);
        
        clientRuntimeModelLoader.loadModel("", runtimeModelConsumer, empty, error);

        verify(runtimeModelConsumer).accept(runtimeModel);
        verify(empty, times(0)).execute();
        verify(error, times(0)).accept(any(), any());
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testLoadModelNotFound() {
        when(runtimeModelService.getRuntimeModel(anyString())).thenReturn(Optional.empty());
        
        Consumer<RuntimeModel> runtimeModelConsumer = mock(Consumer.class);
        Command empty = mock(Command.class);
        BiConsumer<Object, Throwable> error = mock(BiConsumer.class);
        
        clientRuntimeModelLoader.loadModel("", runtimeModelConsumer, empty, error);

        verify(runtimeModelConsumer, times(0)).accept(any());
        verify(empty, times(1)).execute();
        verify(error, times(0)).accept(any(), any());
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testLoadModelError() {
        when(runtimeModelService.getRuntimeModel(anyString())).thenThrow(new RuntimeException());
        
        Consumer<RuntimeModel> runtimeModelConsumer = mock(Consumer.class);
        Command empty = mock(Command.class);
        BiConsumer<Object, Throwable> error = mock(BiConsumer.class);
        
        clientRuntimeModelLoader.loadModel("", runtimeModelConsumer, empty, error);

        verify(runtimeModelConsumer, times(0)).accept(any());
        verify(empty, times(0)).execute();
        verify(error, times(1)).accept(any(), any());
    }

}