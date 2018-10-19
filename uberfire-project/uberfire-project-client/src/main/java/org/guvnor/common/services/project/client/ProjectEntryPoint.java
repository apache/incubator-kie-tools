/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.project.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.preferences.ProjectScopedResolutionStrategySupplier;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ui.shared.api.annotations.Bundle;
import org.kie.soup.commons.util.Sets;
import org.uberfire.ext.preferences.client.admin.page.AdminPage;

@EntryPoint
@Bundle("preferences/resources/i18n/ProjectPreferencesConstants.properties")
public class ProjectEntryPoint {

    private AdminPage adminPage;

    private ProjectScopedResolutionStrategySupplier projectScopedResolutionStrategySupplier;

    @Inject
    public ProjectEntryPoint(final AdminPage adminPage,
                             final ProjectScopedResolutionStrategySupplier projectScopedResolutionStrategySupplier) {
        this.adminPage = adminPage;
        this.projectScopedResolutionStrategySupplier = projectScopedResolutionStrategySupplier;
    }

    @PostConstruct
    public void startApp() {
        setupProjectAdminPage();
    }

    private void setupProjectAdminPage() {
        adminPage.addScreen("project",
                            "Project Settings");

        adminPage.addPreference("project",
                                "GeneralPreferences",
                                "General",
                                new Sets.Builder().add("fa").add("fa-gears").build(),
                                "general",
                                projectScopedResolutionStrategySupplier);
    }
}
