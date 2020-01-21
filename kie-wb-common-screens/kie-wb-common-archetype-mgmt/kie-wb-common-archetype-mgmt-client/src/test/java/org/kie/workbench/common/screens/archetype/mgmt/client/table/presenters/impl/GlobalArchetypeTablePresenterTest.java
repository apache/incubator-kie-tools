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

package org.kie.workbench.common.screens.archetype.mgmt.client.table.presenters.impl;

import org.guvnor.common.services.shared.preferences.GuvnorPreferenceScopes;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.archetype.mgmt.client.modal.AddArchetypeModalPresenter;
import org.kie.workbench.common.screens.archetype.mgmt.client.table.config.ArchetypeTableConfiguration;
import org.kie.workbench.common.screens.archetype.mgmt.client.table.presenters.AbstractArchetypeTablePresenter;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.PaginatedArchetypeList;
import org.kie.workbench.common.screens.archetype.mgmt.shared.preferences.ArchetypePreferences;
import org.kie.workbench.common.screens.archetype.mgmt.shared.services.ArchetypeService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.promise.SyncPromises;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GlobalArchetypeTablePresenterTest {

    private GlobalArchetypeTablePresenter presenter;

    @Mock
    private AbstractArchetypeTablePresenter.View view;

    @Mock
    private AbstractArchetypeTablePresenter.ArchetypeListPresenter archetypeListPresenter;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private TranslationService ts;

    @Mock
    private AddArchetypeModalPresenter addArchetypeModalPresenter;

    @Mock
    private ArchetypePreferences archetypePreferences;

    @Mock
    private Caller<ArchetypeService> archetypeService;

    @Mock
    private PreferenceScopeFactory preferenceScopeFactory;

    private Promises promises;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private User user;

    @Before
    public void setup() {
        promises = new SyncPromises();

        presenter = spy(new GlobalArchetypeTablePresenter(view,
                                                          archetypeListPresenter,
                                                          busyIndicatorView,
                                                          ts,
                                                          addArchetypeModalPresenter,
                                                          archetypePreferences,
                                                          archetypeService,
                                                          preferenceScopeFactory,
                                                          promises,
                                                          authorizationManager,
                                                          user));

        doReturn("defaultSelection").when(archetypePreferences).getDefaultSelection();
    }

    @Test
    public void loadPreferencesTest() {
        presenter.loadPreferences(any(PaginatedArchetypeList.class)).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(archetypePreferences).load(any(ParameterizedCommand.class),
                                          any(ParameterizedCommand.class));
    }

    @Test
    public void makeDefaultValueWhenListNotUpdatedTest() {
        doReturn(true).when(presenter).canMakeChanges();
        doReturn(promises.resolve()).when(presenter).savePreferences(false);

        presenter.makeDefaultValue("alias", false).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(archetypePreferences).setDefaultSelection("alias");
        verify(presenter).savePreferences(false);
    }

    @Test
    public void makeDefaultValueWhenListUpdatedTest() {
        doReturn(true).when(presenter).canMakeChanges();
        doReturn(promises.resolve()).when(presenter).savePreferences(true);

        presenter.makeDefaultValue("alias", true).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(archetypePreferences).setDefaultSelection("alias");
        verify(presenter).savePreferences(true);
    }

    @Test
    public void makeDefaultValueWhenNotAllowedTest() {
        doReturn(false).when(presenter).canMakeChanges();
        doReturn(promises.resolve()).when(presenter).savePreferences(false);

        presenter.makeDefaultValue("alias", false).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(archetypePreferences, never()).setDefaultSelection(anyString());
        verify(presenter, never()).savePreferences(anyBoolean());
    }

    @Test
    public void makeDefaultValueWhenDefaultIsNotChangedTest() {
        doReturn(true).when(presenter).canMakeChanges();
        doReturn(promises.resolve()).when(presenter).savePreferences(false);

        presenter.makeDefaultValue("defaultSelection", false).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(archetypePreferences, never()).setDefaultSelection("defaultSelection");
    }

    @Test
    public void canMakeChangesWhenTrueTest() {
        doReturn(true).when(authorizationManager).authorize(any(ResourceRef.class),
                                                            any(User.class));

        final boolean result = presenter.canMakeChanges();

        assertTrue(result);
        verify(authorizationManager).authorize(any(ResourceRef.class),
                                               any(User.class));
    }

    @Test
    public void canMakeChangesWhenFalseTest() {
        doReturn(false).when(authorizationManager).authorize(any(ResourceRef.class),
                                                             any(User.class));

        final boolean result = presenter.canMakeChanges();

        assertFalse(result);
        verify(authorizationManager).authorize(any(ResourceRef.class),
                                               any(User.class));
    }

    @Test
    public void savePreferencesWhenListIsUpdatedTest() {
        doReturn(true).when(presenter).canMakeChanges();
        doReturn(promises.resolve()).when(presenter).savePreferences(any(PreferenceScope.class),
                                                                     eq(true));

        presenter.savePreferences(true).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(preferenceScopeFactory).createScope(GuvnorPreferenceScopes.GLOBAL);
        verify(presenter).savePreferences(any(PreferenceScope.class),
                                          eq(true));
    }

    @Test
    public void savePreferencesWhenListIsNotUpdatedTest() {
        doReturn(true).when(presenter).canMakeChanges();
        doReturn(promises.resolve()).when(presenter).savePreferences(any(PreferenceScope.class),
                                                                     eq(false));

        presenter.savePreferences(false).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(preferenceScopeFactory).createScope(GuvnorPreferenceScopes.GLOBAL);
        verify(presenter).savePreferences(any(PreferenceScope.class),
                                          eq(false));
    }

    @Test
    public void initConfigurationTest() {
        final ArchetypeTableConfiguration expectedConfig = new ArchetypeTableConfiguration.Builder()
                .withAddAction()
                .withDeleteAction()
                .withValidateAction()
                .withStatusColumn()
                .build();

        assertEquals(expectedConfig, presenter.initConfiguration());
    }
}
