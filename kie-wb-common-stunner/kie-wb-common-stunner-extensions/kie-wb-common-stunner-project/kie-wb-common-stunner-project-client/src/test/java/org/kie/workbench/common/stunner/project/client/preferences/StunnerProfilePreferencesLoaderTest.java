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

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.profile.api.preferences.ProfilePreferences;
import org.kie.workbench.common.stunner.core.api.ProfileManager;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.profile.FullProfile;
import org.kie.workbench.common.stunner.core.profile.Profile;
import org.kie.workbench.common.stunner.project.profile.ProjectProfile;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.ParameterizedCommand;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StunnerProfilePreferencesLoaderTest {

    private static final String DEF_SET_ID = "ds1";
    private static final FullProfile DEFAULT_PROFILE = new FullProfile();
    private static final org.kie.workbench.common.profile.api.preferences.Profile PROJECT_PROFILE =
            org.kie.workbench.common.profile.api.preferences.Profile.PLANNER_AND_RULES;

    @Mock
    private ProfileManager profileManager;

    @Mock
    private ProfilePreferences profilePreferences;

    @Mock
    private Metadata metadata;

    @Mock
    private ProjectProfile profile;

    private StunnerProfilePreferencesLoader tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(metadata.getDefinitionSetId()).thenReturn(DEF_SET_ID);
        when(profile.getProjectProfileName()).thenReturn(org.kie.workbench.common.profile.api.preferences.Profile.PLANNER_AND_RULES.getName());
        doAnswer(invocation -> {
            ParameterizedCommand<ProfilePreferences> callback =
                    (ParameterizedCommand<ProfilePreferences>) invocation.getArguments()[0];
            callback.execute(profilePreferences);
            return null;
        }).when(profilePreferences).load(any(ParameterizedCommand.class),
                                         any(ParameterizedCommand.class));
        when(profilePreferences.getProfile()).thenReturn(PROJECT_PROFILE);
        when(profileManager.getProfiles(eq(DEF_SET_ID))).thenReturn(Collections.singletonList(profile));
        tested = new StunnerProfilePreferencesLoader(profileManager,
                                                     profilePreferences,
                                                     DEFAULT_PROFILE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoad() {
        ParameterizedCommand<Profile> loadCallback = mock(ParameterizedCommand.class);
        ParameterizedCommand<Throwable> errorCallback = mock(ParameterizedCommand.class);
        tested.load(metadata,
                    loadCallback,
                    errorCallback);
        verify(errorCallback, never()).execute(any(Throwable.class));
        verify(loadCallback, times(1)).execute(eq(profile));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadDefaultProfile() {
        when(profile.getProjectProfileName()).thenReturn(org.kie.workbench.common.profile.api.preferences.Profile.FULL.getName());
        ParameterizedCommand<Profile> loadCallback = mock(ParameterizedCommand.class);
        ParameterizedCommand<Throwable> errorCallback = mock(ParameterizedCommand.class);
        tested.load(metadata,
                    loadCallback,
                    errorCallback);
        verify(errorCallback, never()).execute(any(Throwable.class));
        verify(loadCallback, times(1)).execute(eq(DEFAULT_PROFILE));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testError() {
        final Throwable exception = mock(Throwable.class);
        doAnswer(invocation -> {
            ParameterizedCommand<Throwable> callback =
                    (ParameterizedCommand<Throwable>) invocation.getArguments()[1];
            callback.execute(exception);
            return null;
        }).when(profilePreferences).load(any(ParameterizedCommand.class),
                                         any(ParameterizedCommand.class));
        ParameterizedCommand<Profile> loadCallback = mock(ParameterizedCommand.class);
        ParameterizedCommand<Throwable> errorCallback = mock(ParameterizedCommand.class);
        tested.load(metadata,
                    loadCallback,
                    errorCallback);
        verify(errorCallback, times(1)).execute(eq(exception));
        verify(loadCallback, never()).execute(any(Profile.class));
    }
}
