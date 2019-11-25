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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.annotations.Customizable;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.preferences.client.admin.AdminPagePerspective;
import org.uberfire.ext.preferences.client.central.PreferencesCentralPerspective;
import org.uberfire.ext.preferences.client.event.PreferencesCentralInitializationEvent;
import org.uberfire.ext.preferences.client.resources.i18n.Constants;
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbs;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

@ApplicationScoped
public class AdminPageImpl implements AdminPage {

    private PlaceManager placeManager;

    private Event<PreferencesCentralInitializationEvent> preferencesCentralInitializationEvent;

    private PreferenceScopeResolutionStrategy resolutionStrategy;

    private UberfireBreadcrumbs breadcrumbs;

    private TranslationService translationService;

    private Map<String, String> screenTitleByIdentifier;

    private Map<String, Map<String, List<AdminTool>>> toolsByCategoryByScreen;

    private String defaultScreen;

    public AdminPageImpl() {
        this(null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public AdminPageImpl(final PlaceManager placeManager,
                         final Event<PreferencesCentralInitializationEvent> preferencesCentralInitializationEvent,
                         @Customizable final PreferenceScopeResolutionStrategy resolutionStrategy,
                         final UberfireBreadcrumbs breadcrumbs,
                         final TranslationService translationService) {
        this.placeManager = placeManager;
        this.preferencesCentralInitializationEvent = preferencesCentralInitializationEvent;
        this.resolutionStrategy = resolutionStrategy;
        this.breadcrumbs = breadcrumbs;
        this.translationService = translationService;
        this.toolsByCategoryByScreen = new HashMap<>();
        this.screenTitleByIdentifier = new HashMap<>();
    }

    @Override
    public void addScreen(final String identifier,
                          final String title) {
        if (identifier == null || identifier.isEmpty()) {
            throw new RuntimeException("The screen identifier must be not empty.");
        }

        screenTitleByIdentifier.put(identifier,
                                    title);
        toolsByCategoryByScreen.put(identifier,
                                    new LinkedHashMap<>());
    }

    @Override
    public void addTool(final String screen,
                        final String title,
                        final Set<String> iconCss,
                        final String category,
                        final Command command,
                        final ParameterizedCommand<ParameterizedCommand<Integer>> counterCommand) {
        if (screen == null || screen.isEmpty()) {
            throw new RuntimeException("The screen identifier must be not empty.");
        }

        if (screenTitleByIdentifier.get(screen) == null) {
            throw new RuntimeException("The screen must be added before it is used.");
        }

        if (category == null || category.isEmpty()) {
            throw new RuntimeException("The category identifier must be not empty.");
        }

        Map<String, List<AdminTool>> toolsByCategory = toolsByCategoryByScreen.get(screen);
        List<AdminTool> tools = toolsByCategory.get(category);

        if (tools == null) {
            tools = new ArrayList<>();
            toolsByCategory.put(category,
                                tools);
        }

        AdminTool tool = new AdminTool(title,
                                       iconCss,
                                       category,
                                       command,
                                       counterCommand);
        tools.add(tool);
    }

    @Override
    public void addTool(final String screen,
                        final String title,
                        final Set<String> iconCss,
                        final String category,
                        final Command command) {
        addTool(screen,
                title,
                iconCss,
                category,
                command,
                null);
    }

    @Override
    public void addPreference(final String screen,
                              final String identifier,
                              final String title,
                              final Set<String> iconCss,
                              final String category,
                              final AdminPageOptions... options) {
        addPreference(screen,
                      identifier,
                      title,
                      iconCss,
                      category,
                      (Supplier<PreferenceScopeResolutionStrategyInfo>) null,
                      options);
    }

    @Override
    public void addPreference(final String screen,
                              final String identifier,
                              final String title,
                              final Set<String> iconCss,
                              final String category,
                              final Supplier<PreferenceScopeResolutionStrategyInfo> customScopeResolutionStrategySupplier,
                              final AdminPageOptions... options) {
        addPreference(screen,
                      identifier,
                      title,
                      iconCss,
                      category,
                      customScopeResolutionStrategySupplier,
                      null,
                      options);
    }

    @Override
    public void addPreference(final String screen,
                              final String identifier,
                              final String title,
                              final Set<String> iconCss,
                              final String category,
                              final PreferenceScope preferenceScope,
                              final AdminPageOptions... options) {
        addPreference(screen,
                      identifier,
                      title,
                      iconCss,
                      category,
                      null,
                      preferenceScope,
                      options);
    }

    @Override
    public void addPreference(final String screen,
                              final String identifier,
                              final String title,
                              final Set<String> iconCss,
                              final String category,
                              final Supplier<PreferenceScopeResolutionStrategyInfo> customScopeResolutionStrategySupplier,
                              final PreferenceScope preferenceScope,
                              final AdminPageOptions... options) {

        addTool(screen,
                title,
                iconCss,
                category,
                () -> {
                    final Command accessCommand = () -> {
                        final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy = customScopeResolutionStrategySupplier != null ? customScopeResolutionStrategySupplier.get() : null;
                        final PreferencesCentralInitializationEvent initEvent = new PreferencesCentralInitializationEvent(identifier,
                                                                                                                          customScopeResolutionStrategy,
                                                                                                                          preferenceScope);
                        placeManager.goTo(new DefaultPlaceRequest(PreferencesCentralPerspective.IDENTIFIER));
                        preferencesCentralInitializationEvent.fire(initEvent);
                    };

                    accessCommand.execute();

                    if (hasOption(options,
                                  AdminPageOptions.WITH_BREADCRUMBS)) {
                        breadcrumbs.clearBreadcrumbs(PreferencesCentralPerspective.IDENTIFIER);
                        breadcrumbs.addBreadCrumb(PreferencesCentralPerspective.IDENTIFIER,
                                                  translationService.format(Constants.Admin),
                                                  new DefaultPlaceRequest(AdminPagePerspective.IDENTIFIER),
                                                  () -> placeManager.goTo(AdminPagePerspective.IDENTIFIER));
                        breadcrumbs.addBreadCrumb(PreferencesCentralPerspective.IDENTIFIER,
                                                  title,
                                                  accessCommand);
                    }
                });
    }

    @Override
    public Map<String, List<AdminTool>> getToolsByCategory(final String screen) {
        return toolsByCategoryByScreen.get(screen);
    }

    @Override
    public String getScreenTitle(final String screen) {
        return screenTitleByIdentifier.get(screen);
    }

    @Override
    public String getDefaultScreen() {
        return defaultScreen;
    }

    @Override
    public void setDefaultScreen(final String defaultScreen) {
        this.defaultScreen = defaultScreen;
    }

    private boolean hasOption(final AdminPageOptions[] options,
                              final AdminPageOptions option) {
        if (options != null) {
            for (final AdminPageOptions o : options) {
                if (o.equals(option)) {
                    return true;
                }
            }
        }

        return false;
    }
}
