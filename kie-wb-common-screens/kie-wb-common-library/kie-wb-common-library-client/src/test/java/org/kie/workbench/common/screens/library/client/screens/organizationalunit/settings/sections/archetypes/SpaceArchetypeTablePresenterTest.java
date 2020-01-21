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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.settings.sections.archetypes;

import java.util.Optional;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.preferences.SpaceScopedResolutionStrategySupplier;
import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
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
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;
import org.uberfire.promise.SyncPromises;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SpaceArchetypeTablePresenterTest {

    private SpaceArchetypeTablePresenter presenter;

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
    private SpaceScopedResolutionStrategySupplier spaceScopedResolutionStrategySupplier;

    @Mock
    private WorkspaceProjectContext projectContext;

    @Mock
    private OrganizationalUnitController organizationalUnitController;

    @Mock
    private OrganizationalUnit organizationalUnit;

    @Before
    public void setup() {
        promises = new SyncPromises();

        presenter = spy(new SpaceArchetypeTablePresenter(view,
                                                         archetypeListPresenter,
                                                         busyIndicatorView,
                                                         ts,
                                                         addArchetypeModalPresenter,
                                                         archetypePreferences,
                                                         archetypeService,
                                                         preferenceScopeFactory,
                                                         promises,
                                                         spaceScopedResolutionStrategySupplier,
                                                         projectContext,
                                                         organizationalUnitController));

        doReturn(Optional.of(organizationalUnit)).when(projectContext).getActiveOrganizationalUnit();
        doReturn("defaultSelection").when(archetypePreferences).getDefaultSelection();
    }

    @Test
    public void loadPreferencesTest() {
        presenter.loadPreferences(any(PaginatedArchetypeList.class));

        verify(archetypePreferences).load(any(PreferenceScopeResolutionStrategyInfo.class),
                                          any(),
                                          any());
    }

    @Test
    public void makeDefaultValueTest() {
        doReturn(true).when(presenter).canMakeChanges();

        presenter.makeDefaultValue("alias", false);

        verify(archetypePreferences).setDefaultSelection("alias");
    }

    @Test
    public void makeDefaultValueWhenNotAllowedTest() {
        doReturn(false).when(presenter).canMakeChanges();

        presenter.makeDefaultValue("alias", false);

        verify(archetypePreferences, never()).setDefaultSelection("alias");
    }

    @Test
    public void makeDefaultValueWhenDefaultIsNotChangedTest() {
        doReturn(true).when(presenter).canMakeChanges();

        presenter.makeDefaultValue("defaultSelection", false);

        verify(archetypePreferences, never()).setDefaultSelection("defaultSelection");
    }

    @Test
    public void canMakeChangesTest() {
        presenter.canMakeChanges();

        verify(organizationalUnitController).canUpdateOrgUnit(organizationalUnit);
    }

    @Test
    public void savePreferencesWhenListIsUpdatedTest() {
        doReturn(true).when(presenter).canMakeChanges();

        presenter.savePreferences(true);

        verify(presenter).savePreferences(true);
    }

    @Test
    public void savePreferencesWhenListIsNotUpdatedTest() {
        doReturn(true).when(presenter).canMakeChanges();

        presenter.savePreferences(false);

        verify(presenter).savePreferences(false);
    }

    @Test
    public void initConfigurationTest() {
        final ArchetypeTableConfiguration expectedConfig = new ArchetypeTableConfiguration.Builder()
                .withStatusColumn()
                .withIncludeColumn()
                .build();

        assertEquals(expectedConfig, presenter.initConfiguration());
    }
}
