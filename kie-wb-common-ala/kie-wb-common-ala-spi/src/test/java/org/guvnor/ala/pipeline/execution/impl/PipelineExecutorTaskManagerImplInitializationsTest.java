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

package org.guvnor.ala.pipeline.execution.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PipelineExecutorTaskManagerImplInitializationsTest
        extends PipelineExecutorTaskManagerImplTestBase {

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testExecutorServiceInitialization() {
        taskManager.init();
        verify(taskManagerHelper,
               times(1)).createExecutorService();
        assertNotNull(taskManager.executor);
    }

    @Test
    public void testPipelineExecutorInitialization() {
        taskManager.init();
        verify(taskManagerHelper,
               times(1)).createPipelineExecutor();
    }

    @Test
    public void testLocalListenerInitialization() {
        taskManager.init();
        assertNotNull(taskManager.localListener);
    }

    @Test
    public void testExternalListenersInitialization() {
        taskManager.init();
        verify(eventListenersInstance,
               times(1)).iterator();
        verify(taskManagerHelper,
               times(1)).createExternalListeners();
        assertEquals(externalListeners,
                     taskManager.externalListeners);
    }
}
