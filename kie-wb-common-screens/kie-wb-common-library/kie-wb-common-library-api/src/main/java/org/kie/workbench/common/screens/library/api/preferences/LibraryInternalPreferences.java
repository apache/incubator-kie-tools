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

import org.uberfire.preferences.shared.annotations.Property;
import org.uberfire.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.preferences.shared.bean.BasePreference;

@WorkbenchPreference(identifier = "LibraryInternalPreferences")
public class LibraryInternalPreferences implements BasePreference<LibraryInternalPreferences> {

    @Property
    boolean projectExplorerExpanded;

    @Property
    String lastOpenedOrganizationalUnit;

    @Property
    String lastOpenedRepository;

    @Override
    public LibraryInternalPreferences defaultValue(final LibraryInternalPreferences defaultValue) {
        defaultValue.projectExplorerExpanded = false;

        return defaultValue;
    }

    public boolean isProjectExplorerExpanded() {
        return projectExplorerExpanded;
    }

    public void setProjectExplorerExpanded(boolean projectExplorerExpanded) {
        this.projectExplorerExpanded = projectExplorerExpanded;
    }

    public String getLastOpenedOrganizationalUnit() {
        return lastOpenedOrganizationalUnit;
    }

    public void setLastOpenedOrganizationalUnit(String lastOpenedOrganizationalUnit) {
        this.lastOpenedOrganizationalUnit = lastOpenedOrganizationalUnit;
    }

    public String getLastOpenedRepository() {
        return lastOpenedRepository;
    }

    public void setLastOpenedRepository(String lastOpenedRepository) {
        this.lastOpenedRepository = lastOpenedRepository;
    }
}
