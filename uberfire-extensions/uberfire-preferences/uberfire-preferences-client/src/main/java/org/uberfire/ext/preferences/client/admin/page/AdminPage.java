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

package org.uberfire.ext.preferences.client.admin.page;

import java.util.List;
import java.util.Map;

import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

/**
 * Used to customize the admin page perspective.
 */
public interface AdminPage {

    void addScreen( String identifier,
                    String title );

    /**
     * Adds a new admin tool to the admin page (with a counter).
     * @param title Title that will be displayed on the tool accessor.
     * @param iconCss CSS class(es) responsible to stylize the icon.
     * @param category Defines the group inside which the shortcut will be.
     * @param command Command to be executed when the shortcut is accessed.
     * @param counterCommand {@link ParameterizedCommand} that calls its {@link ParameterizedCommand} parameter
     * passing the counter.
     */
    void addTool( String screen,
                  String title,
                  String iconCss,
                  String category,
                  Command command,
                  ParameterizedCommand<ParameterizedCommand<Integer>> counterCommand );

    /**
     * Adds a new admin tool to the admin page.
     * @param title Title that will be displayed on the tool accessor.
     * @param iconCss CSS class(es) responsible to stylize the icon.
     * @param category Defines the group inside which the shortcut will be.
     * @param command Command to be executed when the shortcut is accessed.
     */
    void addTool( String screen,
                  String title,
                  String iconCss,
                  String category,
                  Command command );

    /**
     * Adds a new admin tool that links to a preference to the admin page.
     * @param title Preference title that will be displayed on the tool accessor.
     * @param iconCss CSS class related to the shortcut icon.
     * @param category Defines the group inside which the shortcut will be.
     */
    void addPreference( String screen,
                        String identifier,
                        String title,
                        String iconCss,
                        String category );

    void addPreference( String screen,
                        String identifier,
                        String title,
                        String iconCss,
                        String category,
                        Map<String, String> customScopeResolutionStrategyParams );

    /**
     * Returns all added admin tools, grouped by their category.
     * @return A map containing a list of admin tools by each category.
     */
    Map<String, List<AdminTool>> getToolsByCategory( String screen );

    String getScreenTitle( String screen );
}
