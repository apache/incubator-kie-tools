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

package org.kie.workbench.common.screens.library.client.util;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.api.preferences.LibraryPreferences;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.uberfire.mvp.Command;

@ApplicationScoped
public class TranslationUtils {

    private LibraryPreferences libraryPreferences;

    private TranslationService ts;

    private String organizationalUnitAliasInSingular;

    private String organizationalUnitAliasInPlural;

    @Inject
    public TranslationUtils(LibraryPreferences libraryPreferences,
                            TranslationService ts) {
        this.libraryPreferences = libraryPreferences;
        this.ts = ts;
    }

    public void refresh(Command callback) {
        libraryPreferences.load(loadedLibraryPreferences -> {
                                    organizationalUnitAliasInSingular = loadedLibraryPreferences.getOrganizationalUnitPreferences().getAliasInSingular();
                                    if (organizationalUnitAliasInSingular == null || organizationalUnitAliasInSingular.isEmpty()) {
                                        organizationalUnitAliasInSingular = getOrganizationalUnitDefaultAliasInSingular();
                                    }
                                    organizationalUnitAliasInPlural = loadedLibraryPreferences.getOrganizationalUnitPreferences().getAliasInPlural();
                                    if (organizationalUnitAliasInPlural == null || organizationalUnitAliasInPlural.isEmpty()) {
                                        organizationalUnitAliasInPlural = getOrganizationalUnitDefaultAliasInPlural();
                                    }
                                    callback.execute();
                                },
                                error -> {
                                    organizationalUnitAliasInSingular = getOrganizationalUnitDefaultAliasInSingular();
                                    organizationalUnitAliasInPlural = getOrganizationalUnitDefaultAliasInPlural();
                                    callback.execute();
                                });
    }

    private String getOrganizationalUnitDefaultAliasInPlural() {
        return ts.format(LibraryConstants.OrganizationalUnitDefaultAliasInPlural);
    }

    private String getOrganizationalUnitDefaultAliasInSingular() {
        return ts.format(LibraryConstants.OrganizationalUnitDefaultAliasInSingular);
    }

    public String getOrganizationalUnitAliasInSingular() {
        return organizationalUnitAliasInSingular;
    }

    public String getOrganizationalUnitAliasInPlural() {
        return organizationalUnitAliasInPlural;
    }

    public String getOrgUnitsMetrics() {
        return ts.getTranslation(LibraryConstants.Metrics);
    }

    public String getProjectMetrics() {
        return ts.getTranslation(LibraryConstants.Metrics);
    }
}
