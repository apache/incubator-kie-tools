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


package org.kie.workbench.common.stunner.core.profile;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.ProfileManager;
import org.kie.workbench.common.stunner.core.diagram.Metadata;

@ApplicationScoped
public class DomainProfileManager {

    private final DefinitionManager definitionManager;
    private final ProfileManager profileManager;
    private final FullProfile defaultProfile;

    @Inject
    public DomainProfileManager(final DefinitionManager definitionManager,
                                final ProfileManager profileManager,
                                final FullProfile defaultProfile) {
        this.definitionManager = definitionManager;
        this.profileManager = profileManager;
        this.defaultProfile = defaultProfile;
    }

    public List<String> getAllDefinitions(final Metadata metadata) {
        return getDefinitionsByProfile(metadata.getDefinitionSetId(),
                                       metadata.getProfileId());
    }

    public Predicate<String> isDefinitionIdAllowed(final Metadata metadata) {
        return getDefinitionProfile(metadata)
                .map(DomainProfile::definitionAllowedFilter)
                .orElse(defaultProfile.definitionAllowedFilter());
    }

    private Optional<DomainProfile> getDefinitionProfile(final Metadata metadata) {
        return getDefinitionProfile(metadata.getDefinitionSetId(),
                                    metadata.getProfileId());
    }

    private List<String> getDefinitionsByProfile(final String definitionSetId,
                                                 final String profileId) {
        final Object definitionSet = definitionManager.definitionSets().getDefinitionSetById(definitionSetId);
        final Set<String> definitions = definitionManager.adapters().forDefinitionSet().getDefinitions(definitionSet);
        return getDefinitionProfile(definitionSetId,
                                    profileId)
                .map(profile -> definitions.stream()
                        .filter(profile.definitionAllowedFilter()))
                .orElse(definitions.stream())
                .collect(Collectors.toList());
    }

    private Optional<DomainProfile> getDefinitionProfile(final String definitionSetId,
                                                         final String profileId) {
        final Profile profile = profileManager.getProfile(definitionSetId, profileId);
        if (profile instanceof DomainProfile) {
            return Optional.of((DomainProfile) profile);
        }
        return Optional.empty();
    }
}
