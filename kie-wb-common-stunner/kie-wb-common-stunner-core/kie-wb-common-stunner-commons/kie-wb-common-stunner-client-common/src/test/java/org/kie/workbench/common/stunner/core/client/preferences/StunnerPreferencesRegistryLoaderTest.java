/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.preferences;

import java.lang.annotation.Annotation;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.ManagedInstanceStub;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.mvp.ParameterizedCommand;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
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

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        when(definitionUtils.getQualifier(eq(DEF_SET_ID))).thenReturn(qualifier);
        preferencesHolders = spy(new ManagedInstanceStub<>(preferencesHolder));
        tested = new StunnerPreferencesRegistryLoader(definitionUtils,
                                                      preferencesHolders,
                                                      preferences);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoad() {
        final ParameterizedCommand<StunnerPreferences> loadCompleteCallback = mock(ParameterizedCommand.class);
        final ParameterizedCommand<Throwable> errorCallback = mock(ParameterizedCommand.class);
        final StunnerPreferences bean = mock(StunnerPreferences.class);
        doAnswer(invocation -> {
            ((ParameterizedCommand<StunnerPreferences>) invocation.getArguments()[0]).execute(bean);
            return null;
        }).when(preferences).load(any(ParameterizedCommand.class),
                                  any(ParameterizedCommand.class));
        tested.load(DEF_SET_ID,
                    loadCompleteCallback,
                    errorCallback);
        verify(preferencesHolders, times(1)).select(eq(qualifier));
        verify(loadCompleteCallback, times(1)).execute(eq(bean));
        verify(errorCallback, never()).execute(any(Throwable.class));
        verify(preferencesHolder, times(1)).set(eq(bean));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadError() {
        final ParameterizedCommand<StunnerPreferences> loadCompleteCallback = mock(ParameterizedCommand.class);
        final ParameterizedCommand<Throwable> errorCallback = mock(ParameterizedCommand.class);
        final Throwable errorInstance = mock(Throwable.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((ParameterizedCommand<Throwable>) invocation.getArguments()[1]).execute(errorInstance);
                return null;
            }
        }).when(preferences).load(any(ParameterizedCommand.class),
                                  any(ParameterizedCommand.class));
        tested.load(DEF_SET_ID,
                    loadCompleteCallback,
                    errorCallback);
        verify(preferencesHolders, times(1)).select(eq(qualifier));
        verify(errorCallback, times(1)).execute(eq(errorInstance));
        verify(loadCompleteCallback, never()).execute(any(StunnerPreferences.class));
        verify(preferencesHolder, never()).set(any(StunnerPreferences.class));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(preferencesHolders, times(1)).destroyAll();
    }
}
