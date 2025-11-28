/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.session;

import elemental2.promise.IThenable;
import elemental2.promise.Promise;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.diagram.DiagramTypeClientService;
import org.kie.workbench.common.stunner.bpmn.client.workitem.WorkItemDefinitionClientService;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BPMNSessionInitializerTest {

    @Mock
    private WorkItemDefinitionClientService workItemDefinitionService;

    @Mock
    private Promise promise;

    @Mock
    private DiagramTypeClientService diagramTypeService;

    private BPMNSessionInitializer tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        doReturn(promise).when(workItemDefinitionService).call(any(Metadata.class));
        doReturn(promise).when(promise).then(any(IThenable.ThenOnFulfilledCallbackFn.class));
        doReturn(promise).when(promise).catch_(any(Promise.CatchOnRejectedCallbackFn.class));
        tested = new BPMNSessionInitializer(workItemDefinitionService, diagramTypeService);
    }

    @Test
    public void testInit() {
        Metadata metadata = mock(Metadata.class);
        Command callback = mock(Command.class);
        tested.init(metadata, callback);
        verify(diagramTypeService).loadDiagramType(metadata);
        verify(workItemDefinitionService, times(1)).call(eq(metadata));
    }
}
