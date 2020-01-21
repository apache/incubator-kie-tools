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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.preferences.SpaceScopedResolutionStrategySupplier;
import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.archetype.mgmt.client.modal.AddArchetypeModalPresenter;
import org.kie.workbench.common.screens.archetype.mgmt.client.table.config.ArchetypeTableConfiguration;
import org.kie.workbench.common.screens.archetype.mgmt.client.table.presenters.AbstractArchetypeTablePresenter;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.PaginatedArchetypeList;
import org.kie.workbench.common.screens.archetype.mgmt.shared.preferences.ArchetypePreferences;
import org.kie.workbench.common.screens.archetype.mgmt.shared.services.ArchetypeService;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.settings.annotation.SpaceSettings;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.preferences.shared.PreferenceScopeFactory;

@Dependent
@SpaceSettings
public class SpaceArchetypeTablePresenter extends AbstractArchetypeTablePresenter {

    private final SpaceScopedResolutionStrategySupplier spaceScopedResolutionStrategySupplier;
    private final WorkspaceProjectContext projectContext;
    private final OrganizationalUnitController organizationalUnitController;

    @Inject
    public SpaceArchetypeTablePresenter(final View view,
                                        final ArchetypeListPresenter archetypeListPresenter,
                                        final BusyIndicatorView busyIndicatorView,
                                        final TranslationService ts,
                                        final AddArchetypeModalPresenter addArchetypeModalPresenter,
                                        final ArchetypePreferences archetypePreferences,
                                        final Caller<ArchetypeService> archetypeService,
                                        final PreferenceScopeFactory preferenceScopeFactory,
                                        final Promises promises,
                                        final SpaceScopedResolutionStrategySupplier spaceScopedResolutionStrategySupplier,
                                        final WorkspaceProjectContext projectContext,
                                        final OrganizationalUnitController organizationalUnitController) {
        super(view,
              archetypeListPresenter,
              busyIndicatorView,
              ts,
              addArchetypeModalPresenter,
              archetypePreferences,
              archetypeService,
              preferenceScopeFactory,
              promises);

        this.spaceScopedResolutionStrategySupplier = spaceScopedResolutionStrategySupplier;
        this.projectContext = projectContext;
        this.organizationalUnitController = organizationalUnitController;
    }

    @Override
    public Promise<Void> loadPreferences(final PaginatedArchetypeList paginatedList) {
        return promises.create(
                (resolve, reject) -> archetypePreferences.load(spaceScopedResolutionStrategySupplier.get(),
                                                               loadPreferencesSuccessCallback(paginatedList, resolve),
                                                               loadPreferencesErrorCallback(reject)));
    }

    @Override
    public Promise<Void> makeDefaultValue(final String alias,
                                          final boolean updateList) {
        if (canMakeChanges() && !alias.equals(archetypePreferences.getDefaultSelection())) {
            archetypePreferences.setDefaultSelection(alias);
            if (updateList) {
                updateList();
            }
            super.runOnChangedCallback();
        }

        return promises.resolve();
    }

    @Override
    public ArchetypeTableConfiguration initConfiguration() {
        return new ArchetypeTableConfiguration.Builder()
                .withStatusColumn()
                .withIncludeColumn()
                .build();
    }

    @Override
    public boolean canMakeChanges() {
        return organizationalUnitController.canUpdateOrgUnit(getOrganizationalUnit());
    }

    private OrganizationalUnit getOrganizationalUnit() {
        return projectContext.getActiveOrganizationalUnit().
                orElseThrow(() -> new IllegalStateException("There should be an active organizational unit."));
    }

    @Override
    public Promise<Void> savePreferences(final boolean updateList) {
        return savePreferences(spaceScopedResolutionStrategySupplier.get(),
                               updateList);
    }
}
