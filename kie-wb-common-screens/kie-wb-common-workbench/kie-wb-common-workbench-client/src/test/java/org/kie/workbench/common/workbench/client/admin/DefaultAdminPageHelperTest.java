/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.workbench.client.admin;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.shared.preferences.GuvnorPreferenceScopes;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.profile.api.preferences.ProfileService;
import org.kie.workbench.common.workbench.client.authz.WorkbenchFeatures;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.uberfire.experimental.client.service.ClientExperimentalFeaturesRegistryService;
import org.uberfire.experimental.service.registry.ExperimentalFeature;
import org.uberfire.experimental.service.registry.ExperimentalFeaturesRegistry;
import org.uberfire.ext.preferences.client.admin.page.AdminPage;
import org.uberfire.ext.preferences.client.admin.page.AdminPageOptions;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.RETURNS_DEFAULTS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DefaultAdminPageHelperTest {

    private static String LIBRARY_PREFERENCES = "LibraryPreferences";
    private static String PROFILE_PREFERENCES = "ProfilePreferences";
    private static String ARTIFACT_REPOSITORY_PREFERENCES = "ArtifactRepositoryPreference";
    private static String STUNNER_PREFERENCES = "StunnerPreferences";
    private static String EXPERIMENTAL_SETTINGS = "ExperimentalSettings";
    private static String MANAGE_PREFERENCES = "ManagePreferences";
    private static String SERTIVCE_TASKS_ADMIN = "ServiceTasksAdministration";
    private static String DATA_TRANSFER = "DataTransfer";

    @Mock
    private AdminPage adminPage;

    @Mock
    private TranslationService translationService;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private PreferenceScopeFactory scopeFactory;

    @Mock
    private ClientExperimentalFeaturesRegistryService experimentalFeaturesRegistryService;

    @InjectMocks
    private DefaultAdminPageHelper defaultAdminPageHelper;

    @Mock
    private PreferenceScope globalScope;

    @Mock
    ProfileService profileService;

    CallerMock<ProfileService> profileServiceCaller;

    @Before
    public void setup() {
        mockConstants();
        mockProfileService();
    }

    @Test
    public void generalToolsAreAdded() {
        defaultAdminPageHelper.setup();

        final String languages = defaultAdminPageHelper.constants.Languages();
        verify(adminPage).addTool(eq("root"),
                                  eq(languages),
                                  any(),
                                  eq("preferences"),
                                  any());
    }

    @Test
    public void securityShortcutsAreAddedWhenUserHasPermission() {
        doReturn(true).when(authorizationManager).authorize(any(ResourceRef.class),
                                                            any(User.class));

        defaultAdminPageHelper.setup();

        final String roles = defaultAdminPageHelper.constants.Roles();
        verify(adminPage).addTool(eq("root"),
                                  eq(roles),
                                  any(),
                                  eq("security"),
                                  any(),
                                  any());

        final String groups = defaultAdminPageHelper.constants.Groups();
        verify(adminPage).addTool(eq("root"),
                                  eq(groups),
                                  any(),
                                  eq("security"),
                                  any(),
                                  any());

        final String users = defaultAdminPageHelper.constants.Users();
        verify(adminPage).addTool(eq("root"),
                                  eq(users),
                                  any(),
                                  eq("security"),
                                  any(),
                                  any());
    }

    @Test
    public void securityShortcutsAreNotAddedWhenUserHasNoPermission() {
        doReturn(false).when(authorizationManager).authorize(any(ResourceRef.class),
                                                             any(User.class));

        defaultAdminPageHelper.setup();

        final String roles = defaultAdminPageHelper.constants.Roles();
        verify(adminPage,
               never()).addTool(eq("root"),
                                eq(roles),
                                any(),
                                any(),
                                any(),
                                any());

        final String groups = defaultAdminPageHelper.constants.Groups();
        verify(adminPage,
               never()).addTool(eq("root"),
                                eq(groups),
                                any(),
                                any(),
                                any(),
                                any());

        final String users = defaultAdminPageHelper.constants.Users();
        verify(adminPage,
               never()).addTool(eq("root"),
                                eq(users),
                                any(),
                                any(),
                                any(),
                                any());
    }

    @Test
    public void perspectivesAreAddedWhenUserHasPermission() {
        doReturn(true).when(authorizationManager).authorize(any(ResourceRef.class),
                                                            any(User.class));

        defaultAdminPageHelper.setup();

        final String artifacts = defaultAdminPageHelper.constants.Artifacts();
        verify(adminPage).addTool(eq("root"),
                                  eq(artifacts),
                                  any(),
                                  eq("perspectives"),
                                  any());

        final String dataSources = defaultAdminPageHelper.constants.DataSources();
        verify(adminPage).addTool(eq("root"),
                                  eq(dataSources),
                                  any(),
                                  eq("perspectives"),
                                  any());

        final String dataSets = defaultAdminPageHelper.constants.DataSets();
        verify(adminPage).addTool(eq("root"),
                                  eq(dataSets),
                                  any(),
                                  eq("perspectives"),
                                  any());
    }

    @Test
    public void perspectivesAreNotAddedWhenUserHasNoPermission() {
        doReturn(false).when(authorizationManager).authorize(any(ResourceRef.class),
                                                             any(User.class));

        defaultAdminPageHelper.setup();

        final String artifacts = defaultAdminPageHelper.constants.Artifacts();
        verify(adminPage,
               never()).addTool(eq("root"),
                                eq(artifacts),
                                any(),
                                eq("perspectives"),
                                any());

        final String dataSources = defaultAdminPageHelper.constants.DataSources();
        verify(adminPage,
               never()).addTool(eq("root"),
                                eq(dataSources),
                                any(),
                                eq("perspectives"),
                                any());

        final String dataSets = defaultAdminPageHelper.constants.DataSets();
        verify(adminPage,
               never()).addTool(eq("root"),
                                eq(dataSets),
                                any(),
                                eq("perspectives"),
                                any());
    }

    @Test
    public void preferencesShouldBeSavedOnGlobalScopeWhenUserHasPermissionTest() {
        doReturn(globalScope).when(scopeFactory).createScope(GuvnorPreferenceScopes.GLOBAL);

        doReturn(true).when(authorizationManager).authorize(eq(WorkbenchFeatures.EDIT_GLOBAL_PREFERENCES),
                                                            any());

        defaultAdminPageHelper.setup();
        verifyLibraryPreferencesWasAddedInGlobalScope();
        verifyArtifactRepositoryPreferencesWasAddedInGlobalScope();
        verifyManagePreferencesWasAddedInGlobalScope();
        verifyServiceTasksAdminWasAdded();
    }

    @Test
    public void preferencesShouldBeSavedOnGlobalScopeWhenUserHasPermissionAndEnabledTest() {
        doReturn(globalScope).when(scopeFactory).createScope(GuvnorPreferenceScopes.GLOBAL);

        doReturn(true).when(authorizationManager).authorize(eq(WorkbenchFeatures.EDIT_GLOBAL_PREFERENCES),
                                                            any());

        defaultAdminPageHelper.setup(true,
                                     true,
                                     true);
        verifyLibraryPreferencesWasAddedInGlobalScope();
        verifyArtifactRepositoryPreferencesWasAddedInGlobalScope();
        verifyManagePreferencesWasAddedInGlobalScope();
        verifyServiceTasksAdminWasAdded();
    }

    @Test
    public void preferencesShouldNotBeAddedWhenUserHasPermissionAndDisabledTest() {
        doReturn(globalScope).when(scopeFactory).createScope(GuvnorPreferenceScopes.GLOBAL);
        doReturn(true).when(authorizationManager).authorize(eq(WorkbenchFeatures.EDIT_GLOBAL_PREFERENCES),
                                                            any());

        defaultAdminPageHelper.setup(false,
                                     false,
                                     false);
        verifyLibraryPreferencesWasNotAdded();
        verifyArtifactRepositoryPreferencesWasNotAdded();
        verifyManagePreferencesWasAddedInGlobalScope();
        verifyServiceTasksAdminWasAdded();
    }

    @Test
    public void preferencesShouldNotBeAddedWhenUserHasNoPermissionTest() {
        doReturn(false).when(authorizationManager).authorize(eq(WorkbenchFeatures.EDIT_GLOBAL_PREFERENCES),
                                                             any());

        defaultAdminPageHelper.setup();
        verifyLibraryPreferencesWasNotAdded();
        verifyArtifactRepositoryPreferencesWasNotAdded();
        verifyManagePreferencesWasNotAdded();
        verifyServiceTasksAdminWasNotAdded();
    }

    @Test
    public void preferencesShouldNotBeAddedWhenUserHasNoPermissionAndDisabledTest() {
        doReturn(false).when(authorizationManager).authorize(eq(WorkbenchFeatures.EDIT_GLOBAL_PREFERENCES),
                                                             any());

        defaultAdminPageHelper.setup(false,
                                     false,
                                     false);

        verifyLibraryPreferencesWasNotAdded();
        verifyArtifactRepositoryPreferencesWasNotAdded();
        verifyManagePreferencesWasNotAdded();
        verifyServiceTasksAdminWasNotAdded();
    }

    @Test
    public void sshKeysAdded() {
        defaultAdminPageHelper.setup();

        final String title = defaultAdminPageHelper.constants.SSHKeys();

        verify(adminPage).addTool(eq("root"),
                                  eq(title),
                                  any(),
                                  eq("general"),
                                  any(Command.class));
    }

    @Test
    public void profilePreferencesWasAddedWithPermissionTest() {
        doReturn(true).when(authorizationManager).authorize(eq(WorkbenchFeatures.EDIT_PROFILE_PREFERENCES),
                any());
        defaultAdminPageHelper.setup();
        verifyProfilePreferenceAdded(true);
    }

    @Test
    public void profilePreferencesWasNotAddedTest() {
        doReturn(false).when(authorizationManager).authorize(eq(WorkbenchFeatures.EDIT_PROFILE_PREFERENCES),
                any());

        defaultAdminPageHelper.setup();
        verifyProfilePreferenceAdded(false);
    }

    @Test
    public void profilePreferencesWasNotAddedWithPermissionWithForceTest() {
        when(profileService.isForce()).thenReturn(true);
        doReturn(true).when(authorizationManager).authorize(eq(WorkbenchFeatures.EDIT_PROFILE_PREFERENCES),
                any());
        defaultAdminPageHelper.setup();
        verifyProfilePreferenceAdded(false);
    }

    @Test
    public void stunnerPreferencesWasAddedTest() {
        defaultAdminPageHelper.setup();
        defaultAdminPageHelper.setup(false,
                                     false,
                                     false);
        defaultAdminPageHelper.setup(true,
                                     true,
                                     true);
        verifyStunnerPreferencesWasAdded(2);
    }

    @Test
    public void stunnerPreferencesWasNotAddedTest() {
        defaultAdminPageHelper.setup(true,
                                     true,
                                     false);
        verifyStunnerPreferencesWasAdded(0);
    }

    @Test
    public void experimentalFeaturesAddedTest() {
        verifyExperimentalFeatureAdded(true, true);
    }

    @Test
    public void experimentalFeaturesAddedTestWithoutFeatures() {
        verifyExperimentalFeatureAdded(true, false);
    }

    @Test
    public void experimentalFeaturesWasNotAddedTest() {
        verifyExperimentalFeatureAdded(false, false);
    }

    @Test
    public void dataTransferAddedTest() {
        doReturn(true).when(authorizationManager).authorize(eq(WorkbenchFeatures.ACCESS_DATA_TRANSFER), any());
        defaultAdminPageHelper.setup();
        verifyDataTransferAdded(true);
    }

    @Test
    public void dataTransferNotAddedTest() {
        doReturn(false).when(authorizationManager).authorize(eq(WorkbenchFeatures.ACCESS_DATA_TRANSFER), any());
        defaultAdminPageHelper.setup();
        verifyDataTransferAdded(false);
    }

    private void verifyDataTransferAdded(boolean expected) {
        verify(adminPage, expected ? times(1) : never())
            .addTool(eq("root"), eq(DATA_TRANSFER), any(), eq("services"), any(Command.class));
    }

    private void verifyExperimentalFeatureAdded(final boolean addExperimental, final boolean addFeatures) {
        doReturn(true).when(authorizationManager).authorize(any(ResourceRef.class), any(User.class));

        List<ExperimentalFeature> definitions = new ArrayList<>();
        ExperimentalFeaturesRegistry registry = mock(ExperimentalFeaturesRegistry.class);

        when(registry.getAllFeatures()).thenReturn(definitions);

        when(experimentalFeaturesRegistryService.getFeaturesRegistry()).thenReturn(registry);

        if (addFeatures) {
            definitions.add(mock(ExperimentalFeature.class));
            definitions.add(mock(ExperimentalFeature.class));
        }

        when(experimentalFeaturesRegistryService.isExperimentalEnabled()).thenReturn(addExperimental);

        defaultAdminPageHelper.setup();

        boolean shouldAppear = addExperimental && addFeatures;

        verify(adminPage, shouldAppear ? times(1) : never()).addTool(eq("root"),
                                                                        eq(EXPERIMENTAL_SETTINGS),
                                                                        any(),
                                                                        eq("advanced"),
                                                                        any(Command.class));
    }

    private void verifyProfilePreferenceAdded(boolean authorized) {
        verify(adminPage, authorized ? times(1) : never()).addPreference(eq("root"),
                                        eq(PROFILE_PREFERENCES),
                                        any(),
                                        any(),
                                        eq("advanced"),
                                        any(PreferenceScope.class),
                                        eq(AdminPageOptions.WITH_BREADCRUMBS));
    }

    private void verifyLibraryPreferencesWasAddedInGlobalScope() {
        verify(adminPage,
               times(1)).addPreference(eq("root"),
                                       eq(LIBRARY_PREFERENCES),
                                       any(),
                                       any(),
                                       any(),
                                       eq(globalScope),
                                       eq(AdminPageOptions.WITH_BREADCRUMBS));
    }

    private void verifyLibraryPreferencesWasNotAdded() {
        verify(adminPage,
               never()).addPreference(eq("root"),
                                      eq(LIBRARY_PREFERENCES),
                                      any(),
                                      any(),
                                      any(),
                                      any(PreferenceScope.class),
                                      eq(AdminPageOptions.WITH_BREADCRUMBS));
    }

    private void verifyArtifactRepositoryPreferencesWasAddedInGlobalScope() {
        verify(adminPage,
               times(1)).addPreference(eq("root"),
                                       eq(ARTIFACT_REPOSITORY_PREFERENCES),
                                       any(),
                                       any(),
                                       any(),
                                       eq(globalScope),
                                       eq(AdminPageOptions.WITH_BREADCRUMBS));
    }

    private void verifyManagePreferencesWasAddedInGlobalScope() {
        verify(adminPage,
               times(1)).addPreference(eq("root"),
                                       eq(MANAGE_PREFERENCES),
                                       any(),
                                       any(),
                                       any(),
                                       eq(globalScope),
                                       eq(AdminPageOptions.WITH_BREADCRUMBS));
    }

    private void verifyManagePreferencesWasNotAdded() {
        verify(adminPage,
               never()).addPreference(eq("root"),
                                      eq(MANAGE_PREFERENCES),
                                      any(),
                                      any(),
                                      any(),
                                      eq(globalScope),
                                      eq(AdminPageOptions.WITH_BREADCRUMBS));
    }

    private void verifyArtifactRepositoryPreferencesWasNotAdded() {
        verify(adminPage,
               never()).addPreference(eq("root"),
                                      eq(ARTIFACT_REPOSITORY_PREFERENCES),
                                      any(),
                                      any(),
                                      any(),
                                      any(PreferenceScope.class),
                                      eq(AdminPageOptions.WITH_BREADCRUMBS));
    }

    private void verifyStunnerPreferencesWasAdded(int timesAdded) {
        verify(adminPage,
               times(timesAdded)).addPreference(eq("root"),
                                                eq(STUNNER_PREFERENCES),
                                                any(),
                                                any(),
                                                any(),
                                                any(PreferenceScope.class),
                                                eq(AdminPageOptions.WITH_BREADCRUMBS));
    }


    private void verifyServiceTasksAdminWasAdded() {
        verify(adminPage,
               times(1)).addTool(eq("root"),
                                                eq(SERTIVCE_TASKS_ADMIN),
                                                any(),
                                                eq("services"),
                                                any());
    }

    private void verifyServiceTasksAdminWasNotAdded() {
        verify(adminPage,
               never()).addTool(eq("root"),
                                                eq(SERTIVCE_TASKS_ADMIN),
                                                any(),
                                                eq("services"),
                                                any());
    }

    private void mockConstants() {
        defaultAdminPageHelper.constants = mock(DefaultWorkbenchConstants.class,
                                                (Answer) invocation -> {
                                                    if (String.class.equals(invocation.getMethod().getReturnType())) {
                                                        return invocation.getMethod().getName();
                                                    } else {
                                                        return RETURNS_DEFAULTS.answer(invocation);
                                                    }
                                                });
    }

    private void mockProfileService() {
        defaultAdminPageHelper.profileService = new CallerMock<ProfileService>(profileService);
    }
}
