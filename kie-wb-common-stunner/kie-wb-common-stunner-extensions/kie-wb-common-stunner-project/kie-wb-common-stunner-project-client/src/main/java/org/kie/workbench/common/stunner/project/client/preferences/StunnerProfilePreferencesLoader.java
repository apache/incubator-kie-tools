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

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.profile.api.preferences.ProfilePreferences;
import org.kie.workbench.common.stunner.core.api.ProfileManager;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.profile.FullProfile;
import org.kie.workbench.common.stunner.core.profile.Profile;
import org.kie.workbench.common.stunner.project.profile.ProjectProfile;
import org.uberfire.mvp.ParameterizedCommand;

@ApplicationScoped
public class StunnerProfilePreferencesLoader {

    private final ProfileManager profileManager;
    private final ProfilePreferences profilePreferences;
    private final FullProfile defaultProfile;

    @Inject
    public StunnerProfilePreferencesLoader(final ProfileManager profileManager,
                                           final ProfilePreferences profilePreferences,
                                           final FullProfile defaultProfile) {
        this.profileManager = profileManager;
        this.profilePreferences = profilePreferences;
        this.defaultProfile = defaultProfile;
    }

    public void load(final Metadata metadata,
                     final ParameterizedCommand<Profile> loadCompleteCallback,
                     final ParameterizedCommand<Throwable> errorCallback) {
        profilePreferences
                .load(profilePreferences -> {
                    final String profileName = profilePreferences.getProfile().getName();
                    loadCompleteCallback.execute(getProfileByPreference(metadata,
                                                                        profileName)
                                                         .orElse(defaultProfile));
                }, errorCallback);
    }

    private Optional<Profile> getProfileByPreference(final Metadata metadata,
                                                     final String profileName) {
        return profileManager
                .getProfiles(metadata.getDefinitionSetId())
                .stream()
                .filter(profile -> profile instanceof ProjectProfile)
                .filter(profile -> ((ProjectProfile) profile).getProjectProfileName().equals(profileName))
                .findFirst();
    }
}
