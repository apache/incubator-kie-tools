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

package org.kie.workbench.common.stunner.core.api;

import java.util.Collection;

import org.kie.workbench.common.stunner.core.profile.Profile;

/**
 * Entry point for handling the different Definition Set Profiles present on the context.
 */
public interface ProfileManager {

    /**
     * Returns all registered profiles.
     * @return A collection of profiles
     */
    Collection<Profile> getAllProfiles();

    /**
     * Returns a given profile by its identifier
     * @return A profile with the given id, if any, otherwise the return value
     * depends on the implementations.
     */
    Profile getProfile(String profileId);

    /**
     * Returns all profiles registered for a given Definition Set domain.
     * @return A collection of profiles
     */
    Collection<Profile> getProfiles(String definitionSetId);

    /**
     * Returns a given profile by its identifier and its Definition Set domain.
     * @return A profile with the given id, if any, otherwise the return value
     * depends on the implementations.
     */
    Profile getProfile(String definitionSetId, String profileId);
}
