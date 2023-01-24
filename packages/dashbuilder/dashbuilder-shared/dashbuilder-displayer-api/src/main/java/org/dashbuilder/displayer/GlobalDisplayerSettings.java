/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer;

import java.util.ArrayList;
import java.util.Optional;

public interface GlobalDisplayerSettings {

    void setDisplayerSettings(DisplayerSettings settings);

    default Optional<DisplayerSettings> getSettings() {
        return Optional.empty();
    }

    default void apply(DisplayerSettings settings) {
        getSettings().ifPresent(globalSettings -> {
            var globalLookup = globalSettings.getDataSetLookup();
            var lookup = settings.getDataSetLookup();
            var globalColumnsSettings = globalSettings.getColumnSettingsList();
            // Copy Settings
            globalSettings.getSettingsFlatMap().forEach((k, v) -> {
                if (!settings.getSettingsFlatMap().containsKey(k)) {
                    settings.setDisplayerSetting(k, v);
                }
            });

            // Copy Lookup
            if (globalLookup != null) {
                if (lookup == null) {
                    settings.setDataSetLookup(globalLookup.cloneInstance());
                } else {
                    if (lookup.getDataSetUUID() == null) {
                        lookup.setDataSetUUID(globalLookup.getDataSetUUID());
                    }
                    if (lookup.getRowOffset() == 0) {
                        lookup.setRowOffset(globalLookup.getRowOffset());
                    }
                    if (lookup.getNumberOfRows() == -1) {
                        lookup.setNumberOfRows(globalLookup.getNumberOfRows());
                    }
                    // Operations can't be overriden
                    globalLookup.getOperationList().forEach(lookup::addOperation);
                }
            }

            // Copy column Settings, but user settings are added last so they should override global settings
            var newSettings = new ArrayList<ColumnSettings>();
            newSettings.addAll(globalSettings.getColumnSettingsList());
            settings.getColumnSettingsList().forEach(newSettings::add);
            settings.setColumnSettingsList(newSettings);
        });
    }
}
