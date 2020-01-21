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

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.guvnor.common.services.shared.preferences.GuvnorPreferenceScopes;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.archetype.mgmt.client.modal.AddArchetypeModalPresenter;
import org.kie.workbench.common.screens.archetype.mgmt.client.perspectives.ArchetypeManagementPerspective;
import org.kie.workbench.common.screens.archetype.mgmt.client.table.config.ArchetypeTableConfiguration;
import org.kie.workbench.common.screens.archetype.mgmt.client.table.presenters.AbstractArchetypeTablePresenter;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.PaginatedArchetypeList;
import org.kie.workbench.common.screens.archetype.mgmt.shared.preferences.ArchetypePreferences;
import org.kie.workbench.common.screens.archetype.mgmt.shared.services.ArchetypeService;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.ActivityResourceType;

@Dependent
@Default
public class GlobalArchetypeTablePresenter extends AbstractArchetypeTablePresenter {

    private final AuthorizationManager authorizationManager;
    private final User user;

    @Inject
    public GlobalArchetypeTablePresenter(final View view,
                                         final ArchetypeListPresenter archetypeListPresenter,
                                         final BusyIndicatorView busyIndicatorView,
                                         final TranslationService ts,
                                         final AddArchetypeModalPresenter addArchetypeModalPresenter,
                                         final ArchetypePreferences archetypePreferences,
                                         final Caller<ArchetypeService> archetypeService,
                                         final PreferenceScopeFactory preferenceScopeFactory,
                                         final Promises promises,
                                         final AuthorizationManager authorizationManager,
                                         final User user) {
        super(view,
              archetypeListPresenter,
              busyIndicatorView,
              ts,
              addArchetypeModalPresenter,
              archetypePreferences,
              archetypeService,
              preferenceScopeFactory,
              promises);

        this.authorizationManager = authorizationManager;
        this.user = user;
    }

    @Override
    public Promise<Void> loadPreferences(final PaginatedArchetypeList paginatedList) {
        return promises.create(
                (resolve, reject) -> archetypePreferences.load(loadPreferencesSuccessCallback(paginatedList, resolve),
                                                               loadPreferencesErrorCallback(reject)));
    }

    @Override
    public Promise<Void> makeDefaultValue(final String alias,
                                          final boolean updateList) {
        if (canMakeChanges() && !alias.equals(archetypePreferences.getDefaultSelection())) {
            archetypePreferences.setDefaultSelection(alias);
            return savePreferences(updateList);
        }

        return promises.resolve();
    }

    @Override
    public ArchetypeTableConfiguration initConfiguration() {
        return new ArchetypeTableConfiguration.Builder()
                .withAddAction()
                .withDeleteAction()
                .withValidateAction()
                .withStatusColumn()
                .build();
    }

    @Override
    public boolean canMakeChanges() {
        final ResourceRef resourceRef = new ResourceRef(ArchetypeManagementPerspective.IDENTIFIER,
                                                        ActivityResourceType.PERSPECTIVE);
        return authorizationManager.authorize(resourceRef,
                                              user);
    }

    @Override
    public Promise<Void> savePreferences(final boolean updateList) {
        final PreferenceScope globalScope = preferenceScopeFactory.createScope(GuvnorPreferenceScopes.GLOBAL);
        return savePreferences(globalScope, updateList).then(v -> {
            runOnChangedCallback();
            return promises.resolve();
        });
    }
}
