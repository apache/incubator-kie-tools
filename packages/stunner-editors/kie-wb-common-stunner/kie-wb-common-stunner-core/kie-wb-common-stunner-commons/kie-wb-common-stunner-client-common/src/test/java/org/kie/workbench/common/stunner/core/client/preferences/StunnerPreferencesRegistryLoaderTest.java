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


package org.kie.workbench.common.stunner.core.client.preferences;

import java.lang.annotation.Annotation;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.stubs.ManagedInstanceStub;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StunnerPreferencesRegistryLoaderTest {

    private static final String DEF_SET_ID = "ds1";

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private StunnerPreferencesRegistryHolder preferencesHolder;
    private ManagedInstance<StunnerPreferencesRegistryHolder> preferencesHolders;

    @Mock
    private StunnerPreferences preferences;

    @Mock
    private Annotation qualifier;

    private StunnerPreferencesRegistryLoader tested;
    private Metadata metadata;

    @Mock
    private StunnerTextPreferences textPreferences;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        metadata = new MetadataImpl.MetadataImplBuilder(DEF_SET_ID).build();
        when(definitionUtils.getQualifier(eq(DEF_SET_ID))).thenReturn(qualifier);
        preferencesHolders = spy(new ManagedInstanceStub<>(preferencesHolder));
        tested = new StunnerPreferencesRegistryLoader(definitionUtils,
                                                      preferencesHolders,
                                                      preferences,
                                                      textPreferences);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoad() {
        final ParameterizedCommand<StunnerPreferences> loadCompleteCallback = mock(ParameterizedCommand.class);
        final ParameterizedCommand<Throwable> errorCallback = mock(ParameterizedCommand.class);
        final StunnerPreferences pre = mock(StunnerPreferences.class);

        tested.load(metadata,
                    loadCompleteCallback,
                    errorCallback);
        verify(preferencesHolders, times(1)).select(eq(qualifier));
        verify(errorCallback, never()).execute(any(Throwable.class));
        verify(preferencesHolder, times(1)).set(eq(textPreferences), eq(StunnerTextPreferences.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadError() {
        final ParameterizedCommand<StunnerPreferences> loadCompleteCallback = mock(ParameterizedCommand.class);
        final ParameterizedCommand<Throwable> errorCallback = mock(ParameterizedCommand.class);
        final Throwable errorInstance = mock(Throwable.class);

        tested.load(metadata,
                    loadCompleteCallback,
                    errorCallback);
        verify(preferencesHolders, times(1)).select(eq(qualifier));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(preferencesHolders, times(1)).destroyAll();
    }
}
