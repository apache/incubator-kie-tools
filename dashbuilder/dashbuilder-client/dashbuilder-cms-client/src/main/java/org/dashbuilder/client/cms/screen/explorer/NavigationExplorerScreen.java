/*
 * Copyright 2017 JBoss, by Red Hat, Inc
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
package org.dashbuilder.client.cms.screen.explorer;

import java.util.function.Consumer;

import org.dashbuilder.client.cms.resources.i18n.ContentManagerI18n;
import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.client.navigation.event.NavTreeLoadedEvent;
import org.dashbuilder.navigation.event.NavTreeChangedEvent;
import org.dashbuilder.navigation.event.PerspectivePluginsChangedEvent;
import org.dashbuilder.client.navigation.widget.editor.NavTreeEditor;
import org.dashbuilder.navigation.NavTree;
import org.jboss.errai.common.client.api.IsElement;
import org.uberfire.backend.events.AuthorizationPolicySavedEvent;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
@WorkbenchScreen(identifier = NavigationExplorerScreen.SCREEN_ID)
public class NavigationExplorerScreen {

    public static final String SCREEN_ID = "NavigationExplorerScreen";

    NavigationManager navigationManager;
    NavTreeEditor navTreeEditor;
    ContentManagerI18n i18n;
    Event<NotificationEvent> workbenchNotification;

    public NavigationExplorerScreen() {
    }

    @Inject
    public NavigationExplorerScreen(NavigationManager navigationManager,
                                 NavTreeEditor navTreeEditor,
                                 ContentManagerI18n i18n,
                                 Event<NotificationEvent> workbenchNotification) {
        this.navigationManager = navigationManager;
        this.navTreeEditor = navTreeEditor;
        this.i18n = i18n;
        this.workbenchNotification = workbenchNotification;
    }

    @PostConstruct
    void init() {
        navTreeEditor.setOnSaveCommand(this::onNavTreeSaved);
        navTreeEditor.getSettings().setLiteralPerspective(i18n.capitalizeFirst(i18n.getPerspectiveResourceName()));
        navTreeEditor.getSettings().setGotoPerspectiveEnabled(true);

        if (navTreeEditor.getNavTree() == null && navigationManager.getNavTree() != null) {
            navTreeEditor.edit(navigationManager.getNavTree());
        }
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return i18n.getContentExplorerNavigation();
    }

    @WorkbenchPartView
    public IsElement getView() {
        return navTreeEditor;
    }

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(MenuFactory.newTopLevelMenu(i18n.getContentExplorerNew())
                                     .respondsWith(this::createNewNavigationTree)
                                     .endMenu()
                                     .build());
    }

    public NavTreeEditor getNavTreeEditor() {
        return navTreeEditor;
    }

    public void createNewNavigationTree() {
        navTreeEditor.newTree();
    }

    void onNavTreeLoaded(@Observes NavTreeLoadedEvent event) {
        NavTree navTree = event.getNavTree();
        if (navTree != null) {
            navTreeEditor.edit(navTree);
        }
    }

    void onPerspectivesChanged(@Observes PerspectivePluginsChangedEvent event) {
        NavTree navTree = navigationManager.getNavTree();
        if (navTree != null) {
            navTreeEditor.edit(navTree);
        }
    }

    public void onNavTreeChanged(@Observes final NavTreeChangedEvent event) {
        NavTree navTree = event.getNavTree();
        if (navTree != null) {
            navigationManager.update(navTree);
            navTreeEditor.edit(navTree);
        }
    }

    void onNavTreeSaved() {
        workbenchNotification.fire(new NotificationEvent(i18n.getContentManagerNavigationChanged(), NotificationEvent.NotificationType.SUCCESS));
    }

    void onAuthzPolicyChanged(@Observes final AuthorizationPolicySavedEvent event) {
        NavTree navTree = navigationManager.getNavTree();
        if (navTree != null) {
            navTreeEditor.edit(navTree);
        }
    }
}
