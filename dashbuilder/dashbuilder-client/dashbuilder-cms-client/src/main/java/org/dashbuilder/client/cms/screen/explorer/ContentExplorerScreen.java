/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.client.cms.resources.i18n.ContentManagerI18n;
import org.dashbuilder.client.cms.widget.PerspectivesExplorer;
import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.client.navigation.event.NavTreeLoadedEvent;
import org.dashbuilder.client.navigation.event.PerspectivePluginsChangedEvent;
import org.dashbuilder.client.navigation.widget.editor.NavTreeEditor;
import org.dashbuilder.navigation.NavTree;
import org.jboss.errai.common.client.api.IsElement;
import org.uberfire.backend.events.AuthorizationPolicySavedEvent;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.workbench.events.NotificationEvent;

import com.google.gwt.user.client.ui.IsWidget;

@ApplicationScoped
@WorkbenchScreen(identifier = ContentExplorerScreen.SCREEN_ID)
public class ContentExplorerScreen {

    public static final String SCREEN_ID = "ContentExplorerScreen";

    public interface View extends UberView<ContentExplorerScreen> {

        void show(IsElement perspectivesExplorer, IsElement navExplorer);
    }

    View view;
    NavigationManager navigationManager;
    PerspectivesExplorer perspectiveExplorer;
    NavTreeEditor navTreeEditor;
    ContentManagerI18n i18n;
    Event<NotificationEvent> workbenchNotification;

    public ContentExplorerScreen() {
    }

    @Inject
    public ContentExplorerScreen(View view,
                                 NavigationManager navigationManager,
                                 PerspectivesExplorer perspectiveExplorer,
                                 NavTreeEditor navTreeEditor,
                                 ContentManagerI18n i18n,
                                 Event<NotificationEvent> workbenchNotification) {
        this.view = view;
        this.navigationManager = navigationManager;
        this.perspectiveExplorer = perspectiveExplorer;
        this.navTreeEditor = navTreeEditor;
        this.i18n = i18n;
        this.workbenchNotification = workbenchNotification;
        this.view.init(this);
    }

    @PostConstruct
    void init() {
        perspectiveExplorer.setOnExpandCommand(this::onPerspectivesExpanded);
        perspectiveExplorer.show();

        navTreeEditor.setOnExpandCommand(this::onNavTreeExpanded);
        navTreeEditor.setOnSaveCommand(this::onNavTreeSaved);
        navTreeEditor.getSettings().setLiteralPerspective(i18n.capitalizeFirst(i18n.getPerspectiveResourceName()));
        navTreeEditor.getSettings().setGotoPerspectiveEnabled(true);

        if (navTreeEditor.getNavTree() == null && navigationManager.getNavTree() != null) {
            navTreeEditor.edit(navigationManager.getNavTree());
        }
        view.show(perspectiveExplorer, navTreeEditor);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return i18n.getContentExplorer();
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

    public NavTreeEditor getNavTreeEditor() {
        return navTreeEditor;
    }

    public void createNewPerspective() {
        perspectiveExplorer.createNewPerspective();
    }

    void onPerspectivesExpanded() {
        perspectiveExplorer.setMaximized(perspectiveExplorer.isExpanded() && !navTreeEditor.isExpanded());
        navTreeEditor.setMaximized(!perspectiveExplorer.isExpanded() && navTreeEditor.isExpanded());
    }

    void onNavTreeExpanded() {
        perspectiveExplorer.setMaximized(perspectiveExplorer.isExpanded() && !navTreeEditor.isExpanded());
        navTreeEditor.setMaximized(!perspectiveExplorer.isExpanded() && navTreeEditor.isExpanded());
    }

    void onNavTreeLoaded(@Observes NavTreeLoadedEvent event) {
        NavTree navTree = event.getNavTree();
        navTreeEditor.edit(navTree);
    }

    void onPerspectivesChanged(@Observes PerspectivePluginsChangedEvent event) {
        NavTree navTree = navigationManager.getNavTree();
        navTreeEditor.edit(navTree);
    }

    void onNavTreeSaved() {
        workbenchNotification.fire(new NotificationEvent(i18n.getContentManagerNavigationChanged(), NotificationEvent.NotificationType.SUCCESS));
    }

    void onAuthzPolicyChanged(@Observes final AuthorizationPolicySavedEvent event) {
        NavTree navTree = navigationManager.getNavTree();
        navTreeEditor.edit(navTree);
        perspectiveExplorer.show();
    }
}