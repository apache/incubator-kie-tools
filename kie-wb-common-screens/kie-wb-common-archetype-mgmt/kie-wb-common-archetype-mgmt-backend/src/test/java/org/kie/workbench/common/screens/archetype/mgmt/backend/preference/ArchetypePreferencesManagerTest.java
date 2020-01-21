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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guvnor.common.services.shared.preferences.WorkbenchPreferenceScopeResolutionStrategies;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.archetype.mgmt.shared.preferences.ArchetypePreferences;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ArchetypePreferencesManagerTest {

    private static final String ARCHETYPE_ALIAS = "archetype";

    private ArchetypePreferencesManager archetypePreferencesManager;

    @Mock
    private PreferenceScopeFactory scopeFactory;

    @Mock
    private WorkbenchPreferenceScopeResolutionStrategies workbenchPreferenceScopeResolutionStrategies;

    @Mock
    private ArchetypePreferences archetypePreferences;

    @Mock
    private OrganizationalUnitService ouService;

    @Before
    public void setup() {
        archetypePreferencesManager = spy(new ArchetypePreferencesManager(scopeFactory,
                                                                          workbenchPreferenceScopeResolutionStrategies,
                                                                          archetypePreferences,
                                                                          ouService));

        final List<OrganizationalUnit> ouList = Arrays.asList(CreateOrganizationalUnit("ou1"),
                                                              CreateOrganizationalUnit("ou2"),
                                                              CreateOrganizationalUnit("ou3"));
        doReturn(ouList).when(ouService).getAllOrganizationalUnits();

        doReturn("defaultArchetype").when(archetypePreferences).getDefaultSelection();
    }

    @Test
    public void addArchetypeTest() {
        doReturn(false).when(archetypePreferencesManager).containsArchetype(ARCHETYPE_ALIAS);

        archetypePreferencesManager.addArchetype(ARCHETYPE_ALIAS);

        verify(archetypePreferences).load();
        verify(archetypePreferences).save(any(PreferenceScope.class));

        verify(workbenchPreferenceScopeResolutionStrategies, times(3)).getSpaceInfoFor(anyString());
        verify(archetypePreferences, times(3)).load(any(PreferenceScopeResolutionStrategyInfo.class));
        verify(archetypePreferences, times(3)).save(any(PreferenceScopeResolutionStrategyInfo.class));
    }

    @Test
    public void addArchetypeWhenAlreadyExistsTest() {
        doReturn(true).when(archetypePreferencesManager).containsArchetype(ARCHETYPE_ALIAS);

        archetypePreferencesManager.addArchetype(ARCHETYPE_ALIAS);

        verify(archetypePreferences).load();
        verify(archetypePreferences, never()).save(any(PreferenceScope.class));

        verify(workbenchPreferenceScopeResolutionStrategies, times(3)).getSpaceInfoFor(anyString());
        verify(archetypePreferences, times(3)).load(any(PreferenceScopeResolutionStrategyInfo.class));
        verify(archetypePreferences, never()).save(any(PreferenceScopeResolutionStrategyInfo.class));
    }

    @Test
    public void addFirstArchetypeTest() {
        doReturn(false).when(archetypePreferencesManager).containsArchetype(ARCHETYPE_ALIAS);

        archetypePreferencesManager.addArchetype(ARCHETYPE_ALIAS);

        verify(archetypePreferences).load();
        verify(archetypePreferences).save(any(PreferenceScope.class));

        verify(workbenchPreferenceScopeResolutionStrategies, times(3)).getSpaceInfoFor(anyString());
        verify(archetypePreferences, times(3)).load(any(PreferenceScopeResolutionStrategyInfo.class));
        verify(archetypePreferences, times(3)).save(any(PreferenceScopeResolutionStrategyInfo.class));

        verify(archetypePreferences, times(4)).setDefaultSelection(ARCHETYPE_ALIAS);
    }

    @Test
    public void removeArchetypeTest() {
        doReturn(true).when(archetypePreferencesManager).containsArchetype(ARCHETYPE_ALIAS);

        archetypePreferencesManager.removeArchetype(ARCHETYPE_ALIAS);

        verify(archetypePreferences).load();
        verify(archetypePreferences).save(any(PreferenceScope.class));

        verify(workbenchPreferenceScopeResolutionStrategies, times(3)).getSpaceInfoFor(anyString());
        verify(archetypePreferences, times(3)).load(any(PreferenceScopeResolutionStrategyInfo.class));
        verify(archetypePreferences, times(3)).save(any(PreferenceScopeResolutionStrategyInfo.class));
    }

    @Test
    public void removeDefaultArchetypeTest() {
        doReturn(true).when(archetypePreferencesManager).containsArchetype(ARCHETYPE_ALIAS);
        doReturn(ARCHETYPE_ALIAS).when(archetypePreferences).getDefaultSelection();

        archetypePreferencesManager.removeArchetype(ARCHETYPE_ALIAS);

        verify(archetypePreferences).load();
        verify(archetypePreferences).save(any(PreferenceScope.class));

        verify(workbenchPreferenceScopeResolutionStrategies, times(3)).getSpaceInfoFor(anyString());
        verify(archetypePreferences, times(3)).load(any(PreferenceScopeResolutionStrategyInfo.class));
        verify(archetypePreferences, times(3)).save(any(PreferenceScopeResolutionStrategyInfo.class));

        verify(archetypePreferences, times(4)).setDefaultSelection(anyString());
    }

    @Test
    public void setDefaultArchetypeTest() {
        doReturn(true).when(archetypePreferencesManager).containsArchetype(ARCHETYPE_ALIAS);

        archetypePreferencesManager.setDefaultArchetype(ARCHETYPE_ALIAS);

        verify(archetypePreferences).load();
        verify(archetypePreferences).save(any(PreferenceScope.class));

        verify(workbenchPreferenceScopeResolutionStrategies, times(3)).getSpaceInfoFor(anyString());
        verify(archetypePreferences, times(3)).load(any(PreferenceScopeResolutionStrategyInfo.class));
        verify(archetypePreferences, times(3)).save(any(PreferenceScopeResolutionStrategyInfo.class));

        verify(archetypePreferences, times(4)).setDefaultSelection(anyString());
    }

    @Test
    public void setDefaultArchetypeWhenNotPresentTest() {
        doReturn(false).when(archetypePreferencesManager).containsArchetype(ARCHETYPE_ALIAS);

        archetypePreferencesManager.setDefaultArchetype(ARCHETYPE_ALIAS);

        verify(archetypePreferences).load();
        verify(archetypePreferences, never()).save(any(PreferenceScope.class));

        verify(workbenchPreferenceScopeResolutionStrategies, times(3)).getSpaceInfoFor(anyString());
        verify(archetypePreferences, times(3)).load(any(PreferenceScopeResolutionStrategyInfo.class));
        verify(archetypePreferences, never()).save(any(PreferenceScopeResolutionStrategyInfo.class));

        verify(archetypePreferences, never()).setDefaultSelection(anyString());
    }

    @Test
    public void initializeCustomPreferencesTest() {
        archetypePreferencesManager.initializeCustomPreferences();

        verify(archetypePreferencesManager, times(3)).initializeCustomPreference(anyString());
    }

    @Test
    public void initializeCustomPreferenceTest() {
        archetypePreferencesManager.initializeCustomPreference("identifier");

        verify(workbenchPreferenceScopeResolutionStrategies).getSpaceInfoFor(anyString());
        verify(archetypePreferences).load(any(PreferenceScopeResolutionStrategyInfo.class));
        verify(archetypePreferences).save(any(PreferenceScopeResolutionStrategyInfo.class));
    }

    @Test
    public void enableArchetypeTest() {
        doReturn(true).when(archetypePreferencesManager).containsArchetype(ARCHETYPE_ALIAS);

        archetypePreferencesManager.enableArchetype(ARCHETYPE_ALIAS, true, true);

        verify(archetypePreferences).load();
        verify(archetypePreferences).save(any(PreferenceScope.class));

        verify(workbenchPreferenceScopeResolutionStrategies, times(3)).getSpaceInfoFor(anyString());
        verify(archetypePreferences, times(3)).load(any(PreferenceScopeResolutionStrategyInfo.class));
        verify(archetypePreferences, times(3)).save(any(PreferenceScopeResolutionStrategyInfo.class));
    }

    @Test
    public void enableArchetypeGlobalOnlyTest() {
        doReturn(true).when(archetypePreferencesManager).containsArchetype(ARCHETYPE_ALIAS);

        archetypePreferencesManager.enableArchetype(ARCHETYPE_ALIAS, true, false);

        verify(archetypePreferences).load();
        verify(archetypePreferences).save(any(PreferenceScope.class));

        verify(workbenchPreferenceScopeResolutionStrategies, never()).getSpaceInfoFor(anyString());
        verify(archetypePreferences, never()).load(any(PreferenceScopeResolutionStrategyInfo.class));
        verify(archetypePreferences, never()).save(any(PreferenceScopeResolutionStrategyInfo.class));
    }

    @Test
    public void enableArchetypeWhenNotPresentTest() {
        doReturn(false).when(archetypePreferencesManager).containsArchetype(ARCHETYPE_ALIAS);

        archetypePreferencesManager.enableArchetype(ARCHETYPE_ALIAS, true, true);

        verify(archetypePreferences).load();
        verify(archetypePreferences, never()).save(any(PreferenceScope.class));

        verify(workbenchPreferenceScopeResolutionStrategies, times(3)).getSpaceInfoFor(anyString());
        verify(archetypePreferences, times(3)).load(any(PreferenceScopeResolutionStrategyInfo.class));
        verify(archetypePreferences, never()).save(any(PreferenceScopeResolutionStrategyInfo.class));
    }

    @Test
    public void disableArchetypeWhenDefaultTest() {
        doReturn(ARCHETYPE_ALIAS).when(archetypePreferences).getDefaultSelection();
        doReturn(true).when(archetypePreferencesManager).containsArchetype(ARCHETYPE_ALIAS);

        archetypePreferencesManager.enableArchetype(ARCHETYPE_ALIAS, false, true);

        verify(archetypePreferences).load();
        verify(archetypePreferences).save(any(PreferenceScope.class));

        verify(workbenchPreferenceScopeResolutionStrategies, times(3)).getSpaceInfoFor(anyString());
        verify(archetypePreferences, times(3)).load(any(PreferenceScopeResolutionStrategyInfo.class));
        verify(archetypePreferences, times(3)).save(any(PreferenceScopeResolutionStrategyInfo.class));

        verify(archetypePreferences, times(4)).setDefaultSelection(anyString());
    }

    @Test
    public void containsArchetypeTrueTest() {
        final Map<String, Boolean> archetypeSelectionMap = new HashMap<>();
        archetypeSelectionMap.put("archetype", true);
        doReturn(archetypeSelectionMap).when(archetypePreferences).getArchetypeSelectionMap();

        assertTrue(archetypePreferencesManager.containsArchetype("archetype"));
    }

    @Test
    public void containsArchetypeFalseTest() {
        final Map<String, Boolean> archetypeSelectionMap = new HashMap<>();
        archetypeSelectionMap.put("archetype", true);
        doReturn(archetypeSelectionMap).when(archetypePreferences).getArchetypeSelectionMap();

        assertFalse(archetypePreferencesManager.containsArchetype("other"));
    }

    private OrganizationalUnit CreateOrganizationalUnit(final String identifier) {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        doReturn(identifier).when(organizationalUnit).getIdentifier();
        return organizationalUnit;
    }
}