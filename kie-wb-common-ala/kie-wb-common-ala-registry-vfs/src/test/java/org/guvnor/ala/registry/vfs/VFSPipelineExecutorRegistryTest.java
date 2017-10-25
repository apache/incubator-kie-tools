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

package org.guvnor.ala.registry.vfs;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.ala.pipeline.execution.PipelineExecutorTrace;
import org.guvnor.ala.registry.inmemory.InMemoryPipelineExecutorRegistryTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.java.nio.file.Path;

import static org.guvnor.ala.registry.vfs.VFSPipelineExecutorRegistry.PIPELINE_EXECUTOR_REGISTRY_PATH;
import static org.guvnor.ala.registry.vfs.VFSPipelineExecutorRegistry.TRACE_SUFFIX;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VFSPipelineExecutorRegistryTest
        extends InMemoryPipelineExecutorRegistryTest {

    private static final String PIPELINE_EXECUTION_ID_MD5 = "PIPELINE_EXECUTION_ID_MD5";

    @Mock
    private VFSRegistryHelper registryHelper;

    @Mock
    private Path registryRoot;

    private List<Object> traces;

    @Mock
    private Path traceTargetPath;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    @Override
    public void setUp() {
        super.setUp();
        when(registryHelper.ensureDirectory(PIPELINE_EXECUTOR_REGISTRY_PATH)).thenReturn(registryRoot);
        pipelineExecutorRegistry = spy(new VFSPipelineExecutorRegistry(registryHelper));
        ((VFSPipelineExecutorRegistry) pipelineExecutorRegistry).init();
    }

    @Test
    public void testInit() throws Exception {
        traces = new ArrayList<>();
        for (int i = 0; i < TRACES_COUNT; i++) {
            PipelineExecutorTrace trace = mock(PipelineExecutorTrace.class);
            when(trace.getTaskId()).thenReturn(PIPELINE_EXECUTION_ID + Integer.toString(i));
            traces.add(trace);
        }

        when(registryHelper.readEntries(registryRoot,
                                        VFSRegistryHelper.BySuffixFilter.newFilter(TRACE_SUFFIX))).thenReturn(traces);

        ((VFSPipelineExecutorRegistry) pipelineExecutorRegistry).init();

        verify(registryHelper,
               times(2)).ensureDirectory(PIPELINE_EXECUTOR_REGISTRY_PATH);
        verify(registryHelper,
               times(2)).readEntries(registryRoot,
                                     VFSRegistryHelper.BySuffixFilter.newFilter(TRACE_SUFFIX));

        for (Object trace : traces) {
            PipelineExecutorTrace result = pipelineExecutorRegistry.getExecutorTrace(((PipelineExecutorTrace) trace).getTaskId());
            assertNotNull(result);
            assertEquals(trace,
                         result);
        }
    }

    @Test
    @Override
    public void testRegister() {
        prepareTargetPath();
        pipelineExecutorRegistry.register(trace);
        try {
            verify(registryHelper,
                   times(1)).storeEntry(traceTargetPath,
                                        trace);
        } catch (Exception e) {
            //need to catch this exception because parent class method don't throws exceptions,
            //but this will never happen in this scenario.
            fail(e.getMessage());
        }
        PipelineExecutorTrace result = pipelineExecutorRegistry.getExecutorTrace(PIPELINE_EXECUTION_ID);
        assertEquals(trace,
                     result);
    }

    @Test
    public void testRegisterWhenMarshallingErrors() throws Exception {
        prepareTargetPath();
        expectedException.expectMessage("Unexpected error was produced during trace marshalling/storing, trace: " + trace);
        doThrow(new Exception("no matter the message here"))
                .when(registryHelper)
                .storeEntry(traceTargetPath,
                            trace);
        pipelineExecutorRegistry.register(trace);
    }

    @Test
    @Override
    public void testDeregister() {
        prepareTargetPath();
        pipelineExecutorRegistry.register(trace);
        PipelineExecutorTrace result = pipelineExecutorRegistry.getExecutorTrace(PIPELINE_EXECUTION_ID);
        assertEquals(trace,
                     result);

        pipelineExecutorRegistry.deregister(PIPELINE_EXECUTION_ID);
        verify(registryHelper,
               times(1)).deleteBatch(traceTargetPath);
        result = pipelineExecutorRegistry.getExecutorTrace(PIPELINE_EXECUTION_ID);
        assertNull(result);
    }

    private void prepareTargetPath() {
        when(registryHelper.md5Hex(PIPELINE_EXECUTION_ID)).thenReturn(PIPELINE_EXECUTION_ID_MD5);
        String expectedPath = PIPELINE_EXECUTION_ID_MD5 + TRACE_SUFFIX;
        when(registryRoot.resolve(expectedPath)).thenReturn(traceTargetPath);
    }
}
