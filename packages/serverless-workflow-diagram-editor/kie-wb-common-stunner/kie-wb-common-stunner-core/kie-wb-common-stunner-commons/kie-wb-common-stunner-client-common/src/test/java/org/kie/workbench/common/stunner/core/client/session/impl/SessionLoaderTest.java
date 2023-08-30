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


package org.kie.workbench.common.stunner.core.client.session.impl;

import java.lang.annotation.Annotation;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistryLoader;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.stubs.ManagedInstanceStub;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SessionLoaderTest {

    private static final String DEF_SET_ID = "ds1";

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private StunnerPreferencesRegistryLoader preferencesRegistryLoader;

    @Mock
    private SessionInitializer initializer;
    private ManagedInstance<SessionInitializer> initializerInstances;

    @Mock
    private Metadata metadata;

    @Mock
    private Annotation qualifier;

    @Mock
    private StunnerPreferences preferences;

    private SessionLoader sessionLoader;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(metadata.getDefinitionSetId()).thenReturn(DEF_SET_ID);
        when(definitionUtils.getQualifier(eq(DEF_SET_ID))).thenReturn(qualifier);
        initializerInstances = spy(new ManagedInstanceStub<>(initializer));
        doAnswer(invocation -> {
            ParameterizedCommand<StunnerPreferences> callback = (ParameterizedCommand<StunnerPreferences>) invocation.getArguments()[1];
            callback.execute(preferences);
            return null;
        }).when(preferencesRegistryLoader).load(eq(metadata),
                                                any(ParameterizedCommand.class),
                                                any(ParameterizedCommand.class));
        sessionLoader = new SessionLoader(definitionUtils,
                                          preferencesRegistryLoader,
                                          initializerInstances);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoad() {
        ParameterizedCommand<StunnerPreferences> completeCallback = mock(ParameterizedCommand.class);
        ParameterizedCommand<Throwable> errorCallback = mock(ParameterizedCommand.class);
        sessionLoader.load(metadata,
                           completeCallback,
                           errorCallback);
        verify(initializerInstances, times(1)).select(eq(DefinitionManager.DEFAULT_QUALIFIER));
        verify(initializerInstances, times(1)).select(eq(qualifier));
        verify(initializer, times(1)).init(eq(metadata),
                                           any(Command.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroy() {
        ParameterizedCommand<StunnerPreferences> completeCallback = mock(ParameterizedCommand.class);
        ParameterizedCommand<Throwable> errorCallback = mock(ParameterizedCommand.class);
        sessionLoader.load(metadata,
                           completeCallback,
                           errorCallback);
        sessionLoader.destroy();
        verify(initializer, atLeastOnce()).destroy();
        verify(initializerInstances, times(1)).destroyAll();
        assertTrue(sessionLoader.getInitializers().isEmpty());
    }
}
