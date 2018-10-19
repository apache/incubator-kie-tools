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
import java.util.Set;
import java.util.function.Supplier;

import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

/**
 * Used to customize the admin page perspective.
 */
public interface AdminPage {

    /**
     * Adds a new admin screen context to be configured and opened.
     * @param identifier Admin screen identifier.
     * @param title Admin screen title.
     */
    void addScreen(String identifier,
                   String title);

    /**
     * Adds a new admin tool to the admin page (with a counter).
     * @param screen Identifier for the admin screen where the tool will be inserted.
     * @param title Title that will be displayed on the tool accessor.
     * @param iconCss CSS class(es) responsible to stylize the icon.
     * @param category Defines the group inside which the shortcut will be.
     * @param command Command to be executed when the shortcut is accessed.
     * @param counterCommand {@link ParameterizedCommand} that calls its {@link ParameterizedCommand} parameter
     * passing the counter.
     */
    void addTool(String screen,
                 String title,
                 Set<String> iconCss,
                 String category,
                 Command command,
                 ParameterizedCommand<ParameterizedCommand<Integer>> counterCommand);

    /**
     * Adds a new admin tool to the admin page.
     * @param screen Identifier for the admin screen where the tool will be inserted.
     * @param title Title that will be displayed on the tool accessor.
     * @param iconCss CSS class(es) responsible to stylize the icon.
     * @param category Defines the group inside which the shortcut will be.
     * @param command Command to be executed when the shortcut is accessed.
     */
    void addTool(String screen,
                 String title,
                 Set<String> iconCss,
                 String category,
                 Command command);

    /**
     * Adds a new admin tool that links to a preference to the admin page.
     * @param screen Identifier for the admin screen where the preference will be inserted.
     * @param identifier Preference identifier.
     * @param title Preference title that will be displayed on the tool accessor.
     * @param iconCss CSS class related to the shortcut icon.
     * @param category Defines the group inside which the shortcut will be.
     * @param options Defines options to customize the preference accessor.
     */
    void addPreference(String screen,
                       String identifier,
                       String title,
                       Set<String> iconCss,
                       String category,
                       AdminPageOptions... options);

    /**
     * Adds a new admin tool that links to a preference to the admin page.
     * @param screen Identifier for the admin screen where the preference will be inserted.
     * @param identifier Preference identifier.
     * @param title Preference title that will be displayed on the tool accessor.
     * @param iconCss CSS class related to the shortcut icon.
     * @param category Defines the group inside which the shortcut will be.
     * @param customScopeResolutionStrategySupplier Supplier for a custom preference scope resolution strategy.
     * It will be used when the tool is selected.
     * @param options Defines options to customize the preference accessor.
     */
    void addPreference(String screen,
                       String identifier,
                       String title,
                       Set<String> iconCss,
                       String category,
                       Supplier<PreferenceScopeResolutionStrategyInfo> customScopeResolutionStrategySupplier,
                       AdminPageOptions... options);

    /**
     * Adds a new admin tool that links to a preference to the admin page.
     * @param screen Identifier for the admin screen where the preference will be inserted.
     * @param identifier Preference identifier.
     * @param title Preference title that will be displayed on the tool accessor.
     * @param iconCss CSS class related to the shortcut icon.
     * @param category Defines the group inside which the shortcut will be.
     * @param preferenceScope Scope where the preferences will be saved when edited.
     * It will be used when the tool is selected.
     * @param options Defines options to customize the preference accessor.
     */
    void addPreference(String screen,
                       String identifier,
                       String title,
                       Set<String> iconCss,
                       String category,
                       PreferenceScope preferenceScope,
                       AdminPageOptions... options);

    /**
     * Adds a new admin tool that links to a preference to the admin page.
     * @param screen Identifier for the admin screen where the preference will be inserted.
     * @param identifier Preference identifier.
     * @param title Preference title that will be displayed on the tool accessor.
     * @param iconCss CSS class related to the shortcut icon.
     * @param category Defines the group inside which the shortcut will be.
     * @param customScopeResolutionStrategySupplier Supplier for a custom preference scope resolution strategy.
     * @param preferenceScope Scope where the preferences will be saved when edited.
     * It will be used when the tool is selected.
     * @param options Defines options to customize the preference accessor.
     */
    void addPreference(String screen,
                       String identifier,
                       String title,
                       Set<String> iconCss,
                       String category,
                       Supplier<PreferenceScopeResolutionStrategyInfo> customScopeResolutionStrategySupplier,
                       PreferenceScope preferenceScope,
                       AdminPageOptions... options);

    /**
     * Returns all added admin tools, grouped by their category.
     * @param screen Identifier for the admin screen from where the tools will be returned.
     * @return A map containing a list of admin tools by each category of that screen.
     */
    Map<String, List<AdminTool>> getToolsByCategory(String screen);

    /**
     * Returns the screen title to be exhibit in the admin page.
     * @param screen Screen identifier.
     * @return Screen title, as passed when the screen was added.
     */
    String getScreenTitle(String screen);

    /**
     * Returns the default screen to be opened when navigating to the Admin Page perspective.
     * @return Default screen identifier.
     */
    String getDefaultScreen();

    /**
     * Defines the default screen to be opened when navigating to the Admin Page perspective.
     * @param defaultScreen Default screen identifier. Must not be null or empty.
     */
    void setDefaultScreen(String defaultScreen);
}
