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

package org.guvnor.ala.registry.inmemory;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.registry.PipelineRegistry;
import org.guvnor.ala.runtime.providers.ProviderType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.guvnor.ala.AlaSPITestCommons.mockProviderTypeSPI;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InMemoryPipelineRegistryTest {

    protected static final int ELEMENTS_COUNT = 10;

    protected static final String PIPELINE_ID = "PIPELINE_ID";

    protected InMemoryPipelineRegistry pipelineRegistry;

    @Mock
    protected Pipeline pipeline;

    protected ProviderType providerType;

    @Before
    public void setUp() {
        when(pipeline.getName()).thenReturn(PIPELINE_ID);
        providerType = mockProviderTypeSPI("");

        pipelineRegistry = new InMemoryPipelineRegistry();
    }

    @Test
    public void testRegisterPipeline() {
        verifyPipelineIsNotRegistered(pipeline);
        pipelineRegistry.registerPipeline(pipeline);
        verifyPipelineIsRegistered(pipeline);
    }

    @Test
    public void testRegisterPipelineForProviderType() {
        verifyPipelineIsNotRegistered(pipeline);
        pipelineRegistry.registerPipeline(pipeline,
                                          providerType);
        verifyPipelineIsRegisteredForProviderType(pipeline,
                                                  providerType);
    }

    @Test
    public void testGetPipelineByName() {
        verifyPipelineIsNotRegistered(pipeline);
        pipelineRegistry.registerPipeline(pipeline);
        Pipeline result = pipelineRegistry.getPipelineByName(PIPELINE_ID);
        assertEquals(pipeline,
                     result);
    }

    @Test
    public void testGetPipelines() {
        List<Pipeline> result = pipelineRegistry.getPipelines(0,
                                                              1000,
                                                              PipelineRegistry.PIPELINE_NAME_SORT,
                                                              true);
        assertTrue(result.isEmpty());

        List<Pipeline> pipelines = mockPipelineList("",
                                                    ELEMENTS_COUNT);
        pipelines.forEach(pipeline -> pipelineRegistry.registerPipeline(pipeline));

        result = pipelineRegistry.getPipelines(0,
                                               1000,
                                               PipelineRegistry.PIPELINE_NAME_SORT,
                                               true);
        for (Pipeline pipeline : pipelines) {
            assertTrue(result.contains(pipeline));
        }
    }

    @Test
    public void testGetProviderType() {
        ProviderType result = pipelineRegistry.getProviderType(PIPELINE_ID);
        assertNull(result);

        pipelineRegistry.registerPipeline(pipeline,
                                          providerType);
        result = pipelineRegistry.getProviderType(PIPELINE_ID);
        assertEquals(providerType,
                     result);
    }

    @Test
    public void getPipelinesForProvider() {
        ProviderType providerType1 = mockProviderTypeSPI("providerType1");
        ProviderType providerType2 = mockProviderTypeSPI("providerType2");

        List<Pipeline> result = pipelineRegistry.getPipelines(0,
                                                              1000,
                                                              PipelineRegistry.PIPELINE_NAME_SORT,
                                                              true);
        assertTrue(result.isEmpty());

        //register pipelines for providerType, providerType1, and providerType2
        List<Pipeline> pipelines = mockPipelineList("providerType",
                                                    ELEMENTS_COUNT);
        registerPipelinesForProviderType(pipelines,
                                         providerType);

        registerPipelinesForProviderType(mockPipelineList("providerType1",
                                                          ELEMENTS_COUNT),
                                         providerType1);
        registerPipelinesForProviderType(mockPipelineList("providerType2",
                                                          ELEMENTS_COUNT),
                                         providerType2);

        result = pipelineRegistry.getPipelines(0,
                                               1000,
                                               PipelineRegistry.PIPELINE_NAME_SORT,
                                               true);
        assertEquals(3 * ELEMENTS_COUNT,
                     result.size());
        result = pipelineRegistry.getPipelines(providerType.getProviderTypeName(),
                                               providerType.getVersion(),
                                               0,
                                               1000,
                                               PipelineRegistry.PIPELINE_NAME_SORT,
                                               true);
        assertEquals(pipelines.size(),
                     result.size());
        for (Pipeline pipeline : result) {
            assertTrue(result.contains(pipeline));
        }
    }

    private void verifyPipelineIsNotRegistered(Pipeline pipeline) {
        List<Pipeline> pipelines = pipelineRegistry.getPipelines(0,
                                                                 1000,
                                                                 PipelineRegistry.PIPELINE_NAME_SORT,
                                                                 true);
        assertFalse(pipelines.contains(pipeline));
    }

    private void verifyPipelineIsRegistered(Pipeline pipeline) {
        List<Pipeline> pipelines = pipelineRegistry.getPipelines(0,
                                                                 1000,
                                                                 PipelineRegistry.PIPELINE_NAME_SORT,
                                                                 true);
        assertTrue(pipelines.contains(pipeline));
    }

    private void verifyPipelineIsRegisteredForProviderType(Pipeline pipeline,
                                                           ProviderType providerType) {
        List<Pipeline> pipelines = pipelineRegistry.getPipelines(providerType.getProviderTypeName(),
                                                                 providerType.getVersion(),
                                                                 0,
                                                                 1000,
                                                                 PipelineRegistry.PIPELINE_NAME_SORT,
                                                                 true);
        assertTrue(pipelines.contains(pipeline));
    }

    private static List<Pipeline> mockPipelineList(String suffix,
                                                   final int count) {
        List<Pipeline> pipelines = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Pipeline pipeline = mock(Pipeline.class);
            when(pipeline.getName()).thenReturn("Pipeline.name." + suffix + Integer.toString(i));
            pipelines.add(pipeline);
        }
        return pipelines;
    }

    private void registerPipelinesForProviderType(List<Pipeline> pipelines,
                                                  ProviderType providerType) {
        pipelines.forEach(pipeline -> pipelineRegistry.registerPipeline(pipeline,
                                                                        providerType));
    }
}
