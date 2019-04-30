/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.drools.workbench.client.resources.i18n.AppConstants;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.guvnor.common.services.shared.preferences.GuvnorPreferenceScopes;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.kie.soup.commons.util.Sets;
import org.kie.workbench.common.widgets.client.handlers.workbench.configuration.LanguageConfigurationHandler;
import org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationPresenter;
import org.kie.workbench.common.workbench.client.admin.DefaultAdminPageHelper;
import org.kie.workbench.common.workbench.client.entrypoint.DefaultWorkbenchEntryPoint;
import org.kie.workbench.common.workbench.client.error.DefaultWorkbenchErrorCallback;
import org.kie.workbench.common.workbench.client.menu.DefaultWorkbenchFeaturesMenusHelper;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.menu.megamenu.WorkbenchMegaMenuPresenter;
import org.uberfire.ext.preferences.client.admin.page.AdminPage;
import org.uberfire.jsbridge.client.AppFormerJsBridge;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@EntryPoint
public class DroolsWorkbenchEntryPoint extends DefaultWorkbenchEntryPoint {

    protected AppConstants constants = AppConstants.INSTANCE;

    protected PlaceManager placeManager;

    protected DefaultWorkbenchFeaturesMenusHelper menusHelper;

    protected WorkbenchMegaMenuPresenter menuBar;

    protected AdminPage adminPage;

    protected DefaultAdminPageHelper adminPageHelper;

    protected PreferenceScopeFactory scopeFactory;

    protected WorkbenchConfigurationPresenter workbenchConfigurationPresenter;

    protected LanguageConfigurationHandler languageConfigurationHandler;
    private final AppFormerJsBridge appFormerJsBridge;

    @Inject
    public DroolsWorkbenchEntryPoint(final Caller<AppConfigService> appConfigService,
                                     final ActivityBeansCache activityBeansCache,
                                     final PlaceManager placeManager,
                                     final DefaultWorkbenchFeaturesMenusHelper menusHelper,
                                     final WorkbenchMegaMenuPresenter menuBar,
                                     final AdminPage adminPage,
                                     final DefaultAdminPageHelper adminPageHelper,
                                     final PreferenceScopeFactory scopeFactory,
                                     final WorkbenchConfigurationPresenter workbenchConfigurationPresenter,
                                     final LanguageConfigurationHandler languageConfigurationHandler,
                                     final DefaultWorkbenchErrorCallback defaultWorkbenchErrorCallback,
                                     final AppFormerJsBridge appFormerJsBridge) {
        super(appConfigService,
              activityBeansCache,
              defaultWorkbenchErrorCallback);
        this.placeManager = placeManager;
        this.menusHelper = menusHelper;
        this.menuBar = menuBar;
        this.adminPage = adminPage;
        this.adminPageHelper = adminPageHelper;
        this.scopeFactory = scopeFactory;
        this.workbenchConfigurationPresenter = workbenchConfigurationPresenter;
        this.languageConfigurationHandler = languageConfigurationHandler;
        this.appFormerJsBridge = appFormerJsBridge;
    }

    @PostConstruct
    public void preStartSetup() {
        appFormerJsBridge.init("org.drools.workbench.DroolsWorkbench");
    }

    @Override
    public void setupMenu() {
        setupAdminPage();

        menusHelper.addUtilitiesMenuItems();

        final Menus menus = MenuFactory
                .newTopLevelMenu(constants.Perspectives())
                .withItems(menusHelper.getPerspectivesMenuItems())
                .endMenu()
                .build();

        menuBar.addMenus(menus);
    }

    @Override
    public void setupAdminPage() {
        adminPage.addScreen("root",
                            AppConstants.INSTANCE.Settings());
        adminPage.setDefaultScreen("root");

        adminPage.addPreference("root",
                                "LibraryPreferences",
                                AppConstants.INSTANCE.Library(),
                                new Sets.Builder().add("fa").add("fa-cubes").build(),
                                "preferences",
                                scopeFactory.createScope(GuvnorPreferenceScopes.GLOBAL));

        adminPage.addPreference("root",
                                "ArtifactRepositoryPreference",
                                AppConstants.INSTANCE.ArtifactRepository(),
                                new Sets.Builder().add("fa").add("fa-archive").build(),
                                "preferences",
                                scopeFactory.createScope(GuvnorPreferenceScopes.GLOBAL));

        adminPage.addTool("root",
                          "Languages",
                          new Sets.Builder().add("fa").add("fa-cog").build(),
                          "general",
                          () -> {
                              workbenchConfigurationPresenter.show(languageConfigurationHandler);
                          });
    }
}
