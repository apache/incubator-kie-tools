/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import org.uberfire.preferences.shared.PropertyFormType;
import org.uberfire.preferences.shared.annotations.Property;
import org.uberfire.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.preferences.shared.bean.BasePreference;

@WorkbenchPreference(identifier = "ImportProjectsPreferences",
        bundleKey = "ImportProjectsPreferences.Label",
        parents = "LibraryPreferences")
public class ImportProjectsPreferences implements BasePreference<ImportProjectsPreferences> {

    @Property(bundleKey = "ImportProjectsPreferences.MultipleProjectsImportOnClusterEnabled.Label",
            helpBundleKey = "ImportProjectsPreferences.MultipleProjectsImportOnClusterEnabled.Help",
            formType = PropertyFormType.BOOLEAN)
    private boolean multipleProjectsImportOnClusterEnabled;

    @Override
    public ImportProjectsPreferences defaultValue(final ImportProjectsPreferences defaultValue) {
        defaultValue.multipleProjectsImportOnClusterEnabled = false;

        return defaultValue;
    }

    public void setMultipleProjectsImportOnClusterEnabled(boolean multipleProjectsImportOnClusterEnabled) {
        this.multipleProjectsImportOnClusterEnabled = multipleProjectsImportOnClusterEnabled;
    }

    public boolean isMultipleProjectsImportOnClusterEnabled() {
        return multipleProjectsImportOnClusterEnabled;
    }
}
