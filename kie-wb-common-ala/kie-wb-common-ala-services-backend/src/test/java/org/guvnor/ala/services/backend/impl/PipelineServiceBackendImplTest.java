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

package org.guvnor.ala.services.backend.impl;

import java.util.List;

import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.PipelineConfig;
import org.guvnor.ala.runtime.providers.ProviderType;
import org.guvnor.ala.services.api.PipelineService;
import org.guvnor.ala.services.api.itemlist.PipelineConfigsList;
import org.junit.Before;
import org.junit.Test;

import static org.guvnor.ala.AlaSPITestCommons.mockList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PipelineServiceBackendImplTest {

    private PipelineService pipelineService;

    private PipelineServiceBackendImpl pipelineServiceBackend;

    //arbitrary random values
    private static final int ELEMENTS_COUNT = 10;
    private static final Integer PAGE = 1234;
    private static final Integer PAGE_SIZE = 10;
    private static final Boolean SORT_ORDER = Boolean.FALSE;
    private static final String SORT = "SORT";
    private static final String PROVIDER_TYPE_NAME = "PROVIDER_TYPE_NAME";
    private static final String PROVIDER_TYPE_VERSION = "PROVIDER_TYPE_VERSION";
    private static final String PIPELINE_NAME = "PIPELINE_NAME";
    private static final String PIPELINE_EXECUTION_ID = "PIPELINE_EXECUTION_ID";

    private ProviderType providerType;

    @Before
    public void setUp() {
        providerType = mock(ProviderType.class);
        when(providerType.getProviderTypeName()).thenReturn(PROVIDER_TYPE_NAME);
        when(providerType.getVersion()).thenReturn(PROVIDER_TYPE_VERSION);

        pipelineService = mock(PipelineService.class);
        pipelineServiceBackend = new PipelineServiceBackendImpl(pipelineService);
    }

    @Test
    public void testGetPipelineConfigs() {
        List<PipelineConfig> values = mockList(PipelineConfig.class,
                                               ELEMENTS_COUNT);
        PipelineConfigsList list = new PipelineConfigsList(values);
        when(pipelineService.getPipelineConfigs(PAGE,
                                                PAGE_SIZE,
                                                SORT,
                                                SORT_ORDER)).thenReturn(list);
        List<PipelineConfig> result = pipelineServiceBackend.getPipelineConfigs(PAGE,
                                                                                PAGE_SIZE,
                                                                                SORT,
                                                                                SORT_ORDER);
        verify(pipelineService,
               times(1)).getPipelineConfigs(PAGE,
                                            PAGE_SIZE,
                                            SORT,
                                            SORT_ORDER);
        assertEquals(result,
                     values);
    }

    @Test
    public void testGetPipelineConfigsForProviderType() {
        List<PipelineConfig> values = mockList(PipelineConfig.class,
                                               ELEMENTS_COUNT);
        PipelineConfigsList list = new PipelineConfigsList(values);
        when(pipelineService.getPipelineConfigs(PROVIDER_TYPE_NAME,
                                                PROVIDER_TYPE_VERSION,
                                                PAGE,
                                                PAGE_SIZE,
                                                SORT,
                                                SORT_ORDER)).thenReturn(list);

        List<PipelineConfig> result = pipelineServiceBackend.getPipelineConfigs(providerType,
                                                                                PAGE,
                                                                                PAGE_SIZE,
                                                                                SORT,
                                                                                SORT_ORDER);
        verify(pipelineService,
               times(1)).getPipelineConfigs(PROVIDER_TYPE_NAME,
                                            PROVIDER_TYPE_VERSION,
                                            PAGE,
                                            PAGE_SIZE,
                                            SORT,
                                            SORT_ORDER);
        assertEquals(result,
                     values);
    }

    @Test
    public void testGetPipelineNames() {
        List<String> values = mock(List.class);
        when(pipelineService.getPipelineNames(PROVIDER_TYPE_NAME,
                                              PROVIDER_TYPE_VERSION,
                                              PAGE,
                                              PAGE_SIZE,
                                              SORT,
                                              SORT_ORDER)).thenReturn(values);
        List<String> result = pipelineServiceBackend.getPipelineNames(providerType,
                                                                      PAGE,
                                                                      PAGE_SIZE,
                                                                      SORT,
                                                                      SORT_ORDER);
        verify(pipelineService,
               times(1)).getPipelineNames(PROVIDER_TYPE_NAME,
                                          PROVIDER_TYPE_VERSION,
                                          PAGE,
                                          PAGE_SIZE,
                                          SORT,
                                          SORT_ORDER);

        assertEquals(values,
                     result);
    }

    @Test
    public void testNewPipelineForProvider() {
        PipelineConfig pipelineConfig = mock(PipelineConfig.class);
        when(pipelineService.newPipeline(pipelineConfig,
                                         providerType)).thenReturn(PIPELINE_NAME);
        String result = pipelineServiceBackend.newPipeline(pipelineConfig,
                                                           providerType);

        verify(pipelineService,
               times(1)).newPipeline(pipelineConfig,
                                     providerType);
        assertEquals(PIPELINE_NAME,
                     result);
    }

    @Test
    public void testNewPipeline() {
        PipelineConfig pipelineConfig = mock(PipelineConfig.class);
        when(pipelineService.newPipeline(pipelineConfig)).thenReturn(PIPELINE_NAME);
        String result = pipelineServiceBackend.newPipeline(pipelineConfig);

        verify(pipelineService,
               times(1)).newPipeline(pipelineConfig);
        assertEquals(PIPELINE_NAME,
                     result);
    }

    @Test
    public void testRunPipelineSync() {
        testRunPipeline(false);
    }

    @Test
    public void testRunPipelineAsync() {
        testRunPipeline(true);
    }

    private void testRunPipeline(boolean async) {
        Input input = mock(Input.class);

        when(pipelineService.runPipeline(PIPELINE_NAME,
                                         input,
                                         async)).thenReturn(PIPELINE_EXECUTION_ID);
        String result = pipelineServiceBackend.runPipeline(PIPELINE_NAME,
                                                           input,
                                                           async);
        verify(pipelineService,
               times(1)).runPipeline(PIPELINE_NAME,
                                     input,
                                     async);
        assertEquals(PIPELINE_EXECUTION_ID,
                     result);
    }

    @Test
    public void testStopPipelineExecution() {
        pipelineServiceBackend.stopPipelineExecution(PIPELINE_EXECUTION_ID);
        verify(pipelineService,
               times(1)).stopPipelineExecution(PIPELINE_EXECUTION_ID);
    }

    @Test
    public void testDeletePipelineExecution() {
        pipelineServiceBackend.deletePipelineExecution(PIPELINE_EXECUTION_ID);
        verify(pipelineService,
               times(1)).deletePipelineExecution(PIPELINE_EXECUTION_ID);
    }
}
