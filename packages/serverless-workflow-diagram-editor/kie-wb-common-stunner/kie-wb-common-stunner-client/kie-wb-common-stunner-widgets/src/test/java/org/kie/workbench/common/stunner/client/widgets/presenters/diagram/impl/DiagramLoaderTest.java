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


package org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistryLoader;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.ParameterizedCommand;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DiagramLoaderTest {

    private static final String DS_ID = "ds1";

    @Mock
    private ClientDiagramService clientDiagramServices;

    @Mock
    private StunnerPreferencesRegistryLoader preferencesRegistryLoader;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private StunnerPreferences preferences;

    @Mock
    private Path path;

    private DiagramLoader tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getDefinitionSetId()).thenReturn(DS_ID);
        doAnswer(invocation -> {
            ParameterizedCommand<StunnerPreferences> callback
                    = (ParameterizedCommand<StunnerPreferences>) invocation.getArguments()[1];
            callback.execute(preferences);
            return null;
        }).when(preferencesRegistryLoader).load(eq(metadata),
                                                any(ParameterizedCommand.class),
                                                any(ParameterizedCommand.class));
        doAnswer(invocation -> {
            ServiceCallback<Diagram> argCallback = (ServiceCallback<Diagram>) invocation.getArguments()[1];
            argCallback.onSuccess(diagram);
            return null;
        }).when(clientDiagramServices).getByPath(eq(path), any(ServiceCallback.class));
        tested = new DiagramLoader(clientDiagramServices,
                                   preferencesRegistryLoader);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadByPah() {
        ServiceCallback<Diagram> callback = mock(ServiceCallback.class);
        tested.loadByPath(path, callback);
        verify(callback, times(1)).onSuccess(eq(diagram));
        verify(preferencesRegistryLoader, times(1)).load(eq(metadata),
                                                         any(ParameterizedCommand.class),
                                                         any(ParameterizedCommand.class));
        verify(callback, never()).onError(any(ClientRuntimeError.class));
    }
}
