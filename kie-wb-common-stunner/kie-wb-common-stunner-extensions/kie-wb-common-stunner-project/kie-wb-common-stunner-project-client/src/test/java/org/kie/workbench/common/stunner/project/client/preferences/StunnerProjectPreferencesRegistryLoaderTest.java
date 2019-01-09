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

package org.kie.workbench.common.stunner.project.client.preferences;

import java.lang.annotation.Annotation;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.ManagedInstanceStub;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistryHolder;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerTextPreferences;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;
import org.kie.workbench.common.stunner.core.profile.Profile;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.assertEquals;
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
public class StunnerProjectPreferencesRegistryLoaderTest {

    private static final String PROFILE_ID = "profId1";
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

    private StunnerProjectPreferencesRegistryLoader tested;
    private Metadata metadata;

    @Mock
    private StunnerProfilePreferencesLoader profilePreferencesLoader;

    @Mock
    private StunnerTextPreferences textPreferences;

    @Mock
    private Profile profile;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        metadata = new MetadataImpl.MetadataImplBuilder(DEF_SET_ID).build();
        when(definitionUtils.getQualifier(eq(DEF_SET_ID))).thenReturn(qualifier);
        preferencesHolders = spy(new ManagedInstanceStub<>(preferencesHolder));
        doAnswer(invocation -> {
            ParameterizedCommand<Profile> callback = (ParameterizedCommand<Profile>) invocation.getArguments()[1];
            callback.execute(profile);
            return null;
        }).when(profilePreferencesLoader).load(eq(metadata),
                                               any(ParameterizedCommand.class),
                                               any(ParameterizedCommand.class));
        when(profile.getProfileId()).thenReturn(PROFILE_ID);
        tested = new StunnerProjectPreferencesRegistryLoader(definitionUtils,
                                                             preferencesHolders,
                                                             preferences,
                                                             textPreferences,
                                                             profilePreferencesLoader);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoad() {
        final ParameterizedCommand<StunnerPreferences> loadCompleteCallback = mock(ParameterizedCommand.class);
        final ParameterizedCommand<Throwable> errorCallback = mock(ParameterizedCommand.class);
        final StunnerPreferences pre = mock(StunnerPreferences.class);
        doAnswer(invocation -> {
            ((ParameterizedCommand<StunnerPreferences>) invocation.getArguments()[0]).execute(pre);
            return null;
        }).when(preferences).load(any(ParameterizedCommand.class),
                                  any(ParameterizedCommand.class));
        tested.load(metadata,
                    loadCompleteCallback,
                    errorCallback);
        assertEquals(PROFILE_ID, metadata.getProfileId());
        verify(preferencesHolders, times(1)).select(eq(qualifier));
        verify(loadCompleteCallback, times(1)).execute(eq(pre));
        verify(errorCallback, never()).execute(any(Throwable.class));
        verify(preferencesHolder, times(1)).set(eq(pre), eq(StunnerPreferences.class));
        verify(preferencesHolder, times(1)).set(eq(textPreferences), eq(StunnerTextPreferences.class));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(preferencesHolders, times(1)).destroyAll();
    }
}
