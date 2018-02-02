/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.builder.service;

import java.util.function.Consumer;

import org.guvnor.common.services.project.model.Module;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.backend.builder.core.Builder;
import org.kie.workbench.common.services.backend.builder.core.LRUBuilderCache;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BuildInfoServiceTest {

    @Mock
    private BuildServiceImpl buildService;

    private BuildInfoService buildInfoService;

    @Mock
    private LRUBuilderCache cache;

    @Mock
    private Module module;

    @Mock
    private Builder builder;

    @Mock
    private Builder builderNotBuilt;

    @Before
    public void setUp() {
        buildInfoService = new BuildInfoService(buildService, cache);
    }

    @Test
    public void testGetBuildInfoWhenModuleIsBuilt() {
        when(cache.getBuilder(module)).thenReturn(builder);
        when(builder.isBuilt()).thenReturn(true);
        BuildInfo expectedBuildInfo = new BuildInfoImpl(builder);

        BuildInfo result = buildInfoService.getBuildInfo(module);

        assertEquals(expectedBuildInfo, result);
        verify(cache, times(1)).getBuilder(module);
        verify(buildService, never()).build(eq(module), any(Consumer.class));
    }

    @Test
    public void testGetBuildInfoWhenModuleIsNotBuilt() {
        //the builder exists, but was never built.
        when(cache.getBuilder(module)).thenReturn(builderNotBuilt);
        when(builderNotBuilt.isBuilt()).thenReturn(false);
        testBuildIsRequired();
    }

    @Test
    public void testGetBuildInfoWhenBuilerNotExist() {
        //the builder don't exists.
        when(cache.getBuilder(module)).thenReturn(null);
        testBuildIsRequired();
    }

    private void testBuildIsRequired() {
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Consumer consumer = (Consumer) invocation.getArguments()[1];
                consumer.accept(builder);
                return null;
            }
        }).when(buildService).build(eq(module), any(Consumer.class));

        BuildInfo result = buildInfoService.getBuildInfo(module);
        BuildInfo expectedBuildInfo = new BuildInfoImpl(builder);

        assertEquals(expectedBuildInfo, result);
        verify(cache, times(1)).getBuilder(module);
        verify(buildService, times(1)).build(eq(module), any(Consumer.class));
    }
}