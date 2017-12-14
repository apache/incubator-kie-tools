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
import org.uberfire.preferences.shared.impl.validation.NotEmptyValidator;

@WorkbenchPreference(identifier = "LibraryOrganizationalUnitPreferences",
        bundleKey = "LibraryOrganizationalUnitPreferences.Label")
public class LibraryOrganizationalUnitPreferences implements BasePreference<LibraryOrganizationalUnitPreferences> {

    @Property(bundleKey = "LibraryOrganizationalUnitPreferences.Name",
            helpBundleKey = "LibraryOrganizationalUnitPreferences.Name.Help",
            validators = NotEmptyValidator.class)
    String name;

    @Property(bundleKey = "LibraryOrganizationalUnitPreferences.Owner",
            helpBundleKey = "LibraryOrganizationalUnitPreferences.Owner.Help",
            validators = NotEmptyValidator.class)
    String owner;

    @Property(bundleKey = "LibraryOrganizationalUnitPreferences.GroupId",
            helpBundleKey = "LibraryOrganizationalUnitPreferences.GroupId.Help",
            validators = {NotEmptyValidator.class, GroupIdValidator.class})
    String groupId;

    @Property(bundleKey = "LibraryOrganizationalUnitPreferences.AliasInSingular",
            helpBundleKey = "LibraryOrganizationalUnitPreferences.AliasInSingular.Help")
    String aliasInSingular;

    @Property(bundleKey = "LibraryOrganizationalUnitPreferences.AliasInPlural",
            helpBundleKey = "LibraryOrganizationalUnitPreferences.AliasInPlural.Help")
    String aliasInPlural;

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getAliasInSingular() {
        return aliasInSingular;
    }

    public String getAliasInPlural() {
        return aliasInPlural;
    }
}
