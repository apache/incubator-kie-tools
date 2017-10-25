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
import java.util.Collection;
import java.util.List;

import org.guvnor.ala.pipeline.execution.PipelineExecutorTask;
import org.guvnor.ala.pipeline.execution.PipelineExecutorTrace;
import org.guvnor.ala.pipeline.execution.RegistrableOutput;
import org.guvnor.ala.runtime.RuntimeId;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class InMemoryPipelineExecutorRegistryTest {

    protected InMemoryPipelineExecutorRegistry pipelineExecutorRegistry;

    protected static final String PIPELINE_EXECUTION_ID = "PIPELINE_EXECUTION_ID";

    protected static final String RUNTIME_ID = "RUNTIME_ID";

    protected static final int TRACES_COUNT = 10;

    protected PipelineExecutorTrace trace;

    @Before
    public void setUp() {
        trace = mock(PipelineExecutorTrace.class);
        when(trace.getTaskId()).thenReturn(PIPELINE_EXECUTION_ID);

        pipelineExecutorRegistry = new InMemoryPipelineExecutorRegistry();
    }

    @Test
    public void testRegister() {
        pipelineExecutorRegistry.register(trace);
        PipelineExecutorTrace result = pipelineExecutorRegistry.getExecutorTrace(PIPELINE_EXECUTION_ID);
        assertEquals(trace,
                     result);
    }

    @Test
    public void testDeregister() {
        pipelineExecutorRegistry.register(trace);
        PipelineExecutorTrace result = pipelineExecutorRegistry.getExecutorTrace(PIPELINE_EXECUTION_ID);
        assertEquals(trace,
                     result);
        pipelineExecutorRegistry.deregister(trace.getTaskId());
        result = pipelineExecutorRegistry.getExecutorTrace(PIPELINE_EXECUTION_ID);
        assertNull(result);
    }

    @Test
    public void testGetExecutorTrace() {
        pipelineExecutorRegistry.register(trace);
        PipelineExecutorTrace result = pipelineExecutorRegistry.getExecutorTrace(PIPELINE_EXECUTION_ID);
        assertEquals(trace,
                     result);
    }

    @Test
    public void testGetExecutorTraces() {
        List<PipelineExecutorTrace> traces = new ArrayList<>();
        for (int i = 0; i < TRACES_COUNT; i++) {
            PipelineExecutorTrace trace = mock(PipelineExecutorTrace.class);
            when(trace.getTaskId()).thenReturn(PIPELINE_EXECUTION_ID + Integer.toString(i));
            traces.add(trace);
        }
        traces.forEach(trace -> pipelineExecutorRegistry.register(trace));

        Collection<PipelineExecutorTrace> result = pipelineExecutorRegistry.getExecutorTraces();
        assertEquals(traces.size(),
                     result.size());
        for (PipelineExecutorTrace trace : traces) {
            assertTrue(result.contains(trace));
        }
    }

    @Test
    public void getExecutorTraceByRuntimeId() {
        RuntimeIdMock runtimeId = mock(RuntimeIdMock.class);
        when(runtimeId.getId()).thenReturn(RUNTIME_ID);

        PipelineExecutorTask task = mock(PipelineExecutorTask.class);
        when(task.getOutput()).thenReturn(runtimeId);

        when(trace.getTask()).thenReturn(task);

        pipelineExecutorRegistry.register(trace);

        PipelineExecutorTrace result = pipelineExecutorRegistry.getExecutorTrace(runtimeId);
        assertEquals(trace,
                     result);
    }

    private interface RuntimeIdMock
            extends RuntimeId,
                    RegistrableOutput {

    }
}
