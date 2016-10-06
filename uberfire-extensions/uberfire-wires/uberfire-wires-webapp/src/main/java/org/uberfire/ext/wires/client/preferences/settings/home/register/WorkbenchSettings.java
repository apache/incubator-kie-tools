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

package org.uberfire.ext.wires.client.preferences.settings.home.register;

import java.util.List;
import java.util.Map;

import org.uberfire.mvp.Command;

/**
 * Used to customize the settings perspective.
 */
public interface WorkbenchSettings {

    /**
     * Adds a new shortcut item to the settings perspective.
     * @param title Title that will be displayed on the shortcut.
     * @param iconCss CSS class related to the shortcut icon.
     * @param category Defines the group inside which the shortcut will be.
     * @param command Command to be executed when the shortcut is accessed.
     */
    void addItem( String title,
                  String iconCss,
                  String category,
                  Command command );

    /**
     * Returns all added shortcuts and root preference beans shortcuts, grouped
     * by their category.
     * @return A map containing a list of shortcuts by each category.
     */
    Map<String, List<SettingShortcut>> getSettingsByCategory();
}
