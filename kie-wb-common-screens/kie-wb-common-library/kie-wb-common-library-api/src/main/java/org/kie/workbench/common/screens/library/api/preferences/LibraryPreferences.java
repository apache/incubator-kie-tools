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

package org.kie.workbench.common.screens.library.api.preferences;

import org.guvnor.common.services.project.preferences.GAVPreferences;
import org.uberfire.preferences.shared.annotations.Property;
import org.uberfire.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.preferences.shared.bean.BasePreference;

@WorkbenchPreference(identifier = "LibraryPreferences",
        bundleKey = "LibraryPreferences.Label")
public class LibraryPreferences implements BasePreference<LibraryPreferences> {

    @Property(bundleKey = "LibraryPreferences.OrganizationalUnitPreferences")
    LibraryOrganizationalUnitPreferences organizationalUnitPreferences;

    @Property(bundleKey = "LibraryPreferences.ProjectPreferences")
    LibraryProjectPreferences projectPreferences;

    @Property(bundleKey = "LibraryPreferences.AdvancedGavPreferences", shared = true)
    GAVPreferences gavPreferences;

    @Override
    public LibraryPreferences defaultValue(final LibraryPreferences defaultValue) {
        defaultValue.organizationalUnitPreferences.name = "MySpace";
        defaultValue.organizationalUnitPreferences.owner = "admin";
        defaultValue.organizationalUnitPreferences.groupId = "com.myspace";
        defaultValue.organizationalUnitPreferences.aliasInSingular = "";
        defaultValue.organizationalUnitPreferences.aliasInPlural = "";

        defaultValue.projectPreferences.version = "1.0.0-SNAPSHOT";
        defaultValue.projectPreferences.description = "";
        defaultValue.projectPreferences.branch = "master";

        return defaultValue;
    }

    public LibraryOrganizationalUnitPreferences getOrganizationalUnitPreferences() {
        return organizationalUnitPreferences;
    }

    public LibraryProjectPreferences getProjectPreferences() {
        return projectPreferences;
    }

    public GAVPreferences getGavPreferences() {
        return gavPreferences;
    }

    public void setGavPreferences(GAVPreferences gavPreferences) {
        this.gavPreferences = gavPreferences;
    }
}
