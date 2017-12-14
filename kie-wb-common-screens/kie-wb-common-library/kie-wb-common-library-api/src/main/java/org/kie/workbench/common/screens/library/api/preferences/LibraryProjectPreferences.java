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

import org.uberfire.preferences.shared.PropertyFormOptions;
import org.uberfire.preferences.shared.annotations.Property;
import org.uberfire.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.preferences.shared.bean.BasePreference;
import org.uberfire.preferences.shared.impl.validation.NotEmptyValidator;

@WorkbenchPreference(identifier = "LibraryProjectPreferences",
        bundleKey = "LibraryProjectPreferences.Label")
public class LibraryProjectPreferences implements BasePreference<LibraryProjectPreferences> {

    @Property(bundleKey = "LibraryProjectPreferences.Version",
            helpBundleKey = "LibraryProjectPreferences.Version.Help",
            validators = {NotEmptyValidator.class, VersionValidator.class})
    String version;

    @Property(bundleKey = "LibraryProjectPreferences.Description",
            helpBundleKey = "LibraryProjectPreferences.Description.Help")
    String description;

    @Property(bundleKey = "LibraryProjectPreferences.Branch",
            helpBundleKey = "LibraryProjectPreferences.Branch.Help",
            formOptions = PropertyFormOptions.DISABLED)
    String branch;

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public String getBranch() {
        return branch;
    }
}
