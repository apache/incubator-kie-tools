/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.archetype.mgmt.backend.preference;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.shared.preferences.GuvnorPreferenceScopes;
import org.guvnor.common.services.shared.preferences.WorkbenchPreferenceScopeResolutionStrategies;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.kie.workbench.common.screens.archetype.mgmt.shared.preferences.ArchetypePreferences;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;
import org.uberfire.security.Resource;

@ApplicationScoped
public class ArchetypePreferencesManager {

    private static final String EMPTY = "";

    private PreferenceScopeFactory scopeFactory;
    private WorkbenchPreferenceScopeResolutionStrategies workbenchPreferenceScopeResolutionStrategies;
    private ArchetypePreferences archetypePreferences;
    private OrganizationalUnitService ouService;

    public ArchetypePreferencesManager() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public ArchetypePreferencesManager(final PreferenceScopeFactory scopeFactory,
                                       final WorkbenchPreferenceScopeResolutionStrategies workbenchPreferenceScopeResolutionStrategies,
                                       final ArchetypePreferences archetypePreferences,
                                       final OrganizationalUnitService ouService) {
        this.scopeFactory = scopeFactory;
        this.workbenchPreferenceScopeResolutionStrategies = workbenchPreferenceScopeResolutionStrategies;
        this.archetypePreferences = archetypePreferences;
        this.ouService = ouService;
    }

    public void addArchetype(final String archetype) {
        archetypePreferences.load();
        if (addArchetypePreference(archetype)) {
            archetypePreferences.save(getGlobalScope());
        }

        getOuIdentifiers().forEach(identifier -> {
            final PreferenceScopeResolutionStrategyInfo info =
                    workbenchPreferenceScopeResolutionStrategies.getSpaceInfoFor(identifier);

            archetypePreferences.load(info);
            if (addArchetypePreference(archetype)) {
                archetypePreferences.save(info);
            }
        });
    }

    private boolean addArchetypePreference(final String archetype) {
        boolean hasEffect = false;

        if (!containsArchetype(archetype)) {
            final Map<String, Boolean> archetypeSelectionMap = archetypePreferences.getArchetypeSelectionMap();
            archetypeSelectionMap.put(archetype, true);

            if (archetypeSelectionMap.size() == 1) {
                archetypePreferences.setDefaultSelection(archetype);
            }
            hasEffect = true;
        }

        return hasEffect;
    }

    public void removeArchetype(final String archetype) {
        archetypePreferences.load();
        if (removeArchetypePreference(archetype)) {
            archetypePreferences.save(getGlobalScope());
        }

        getOuIdentifiers().forEach(identifier -> {
            final PreferenceScopeResolutionStrategyInfo info =
                    workbenchPreferenceScopeResolutionStrategies.getSpaceInfoFor(identifier);

            archetypePreferences.load(info);
            if (removeArchetypePreference(archetype)) {
                archetypePreferences.save(info);
            }
        });
    }

    private boolean removeArchetypePreference(final String archetype) {
        boolean hasEffect = false;

        if (containsArchetype(archetype)) {
            final Map<String, Boolean> archetypeSelectionMap = archetypePreferences.getArchetypeSelectionMap();
            final boolean updateDefault = archetypePreferences.getDefaultSelection().equals(archetype);

            archetypeSelectionMap.remove(archetype);

            if (updateDefault) {
                final Optional<String> firstArchetype = archetypeSelectionMap.keySet().stream().sorted().findFirst();
                archetypePreferences.setDefaultSelection(firstArchetype.orElse(EMPTY));
            }
            hasEffect = true;
        }

        return hasEffect;
    }

    public void setDefaultArchetype(final String archetype) {
        archetypePreferences.load();
        if (setDefaultArchetypePreference(archetype)) {
            archetypePreferences.save(getGlobalScope());
        }

        getOuIdentifiers().forEach(identifier -> {
            final PreferenceScopeResolutionStrategyInfo info =
                    workbenchPreferenceScopeResolutionStrategies.getSpaceInfoFor(identifier);

            archetypePreferences.load(info);
            if (setDefaultArchetypePreference(archetype)) {
                archetypePreferences.save(info);
            }
        });
    }

    private boolean setDefaultArchetypePreference(final String archetype) {
        boolean hasEffect = false;

        if (containsArchetype(archetype)) {
            archetypePreferences.setDefaultSelection(archetype);
            hasEffect = true;
        }

        return hasEffect;
    }

    public void initializeCustomPreferences() {
        getOuIdentifiers().forEach(this::initializeCustomPreference);
    }

    public void initializeCustomPreference(final String identifier) {
        final PreferenceScopeResolutionStrategyInfo info =
                workbenchPreferenceScopeResolutionStrategies.getSpaceInfoFor(identifier);
        archetypePreferences.load(info);
        archetypePreferences.save(info);
    }

    private List<String> getOuIdentifiers() {
        return ouService.getAllOrganizationalUnits()
                .stream()
                .map(Resource::getIdentifier)
                .collect(Collectors.toList());
    }

    private PreferenceScope getGlobalScope() {
        return scopeFactory.createScope(GuvnorPreferenceScopes.GLOBAL);
    }

    public void enableArchetype(final String archetype,
                                final boolean isEnabled,
                                final boolean customIncluded) {
        archetypePreferences.load();
        if (enableArchetypePreference(archetype, isEnabled)) {
            archetypePreferences.save(getGlobalScope());
        }

        if (customIncluded) {
            getOuIdentifiers().forEach(identifier -> {
                final PreferenceScopeResolutionStrategyInfo info =
                        workbenchPreferenceScopeResolutionStrategies.getSpaceInfoFor(identifier);

                archetypePreferences.load(info);

                if (enableArchetypePreference(archetype,
                                              isEnabled)) {
                    archetypePreferences.save(info);
                }
            });
        }
    }

    private boolean enableArchetypePreference(final String archetype,
                                              final boolean isEnabled) {
        boolean hasEffect = false;

        if (containsArchetype(archetype)) {
            final Map<String, Boolean> archetypeSelectionMap = archetypePreferences.getArchetypeSelectionMap();
            archetypeSelectionMap.put(archetype, isEnabled);

            final boolean updateDefault = !isEnabled && archetypePreferences.getDefaultSelection().equals(archetype);

            if (updateDefault) {
                final Optional<String> firstValidArchetype = archetypeSelectionMap.entrySet()
                        .stream()
                        .filter(Map.Entry::getValue)
                        .map(Map.Entry::getKey)
                        .findFirst();
                archetypePreferences.setDefaultSelection(firstValidArchetype.orElse(EMPTY));
            }

            hasEffect = true;
        }

        return hasEffect;
    }

    boolean containsArchetype(final String archetype) {
        return archetypePreferences.getArchetypeSelectionMap().containsKey(archetype);
    }
}
