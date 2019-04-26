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

import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.client.cms.resources.i18n.ContentManagerI18n;
import org.dashbuilder.client.cms.widget.NewPerspectivePopUp;
import org.dashbuilder.client.cms.widget.PerspectivesExplorer;
import org.jboss.errai.common.client.api.IsElement;
import org.uberfire.backend.events.AuthorizationPolicySavedEvent;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.authz.PerspectiveAction;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = PerspectivesExplorerScreen.SCREEN_ID)
public class PerspectivesExplorerScreen {

    public static final String SCREEN_ID = "PerspectivesExplorerScreen";

    PerspectivesExplorer perspectivesExplorer;
    NewPerspectivePopUp newPerspectivePopUp;
    ContentManagerI18n i18n;

    public PerspectivesExplorerScreen() {
    }

    @Inject
    public PerspectivesExplorerScreen(PerspectivesExplorer perspectivesExplorer,
                                 NewPerspectivePopUp newPerspectivePopUp,
                                 ContentManagerI18n i18n) {
        this.perspectivesExplorer = perspectivesExplorer;
        this.newPerspectivePopUp = newPerspectivePopUp;
        this.i18n = i18n;
    }

    @OnStartup
    public void init() {
        perspectivesExplorer.show();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return i18n.capitalizeFirst(i18n.getPerspectivesResourceName());
    }

    @WorkbenchPartView
    public IsElement getView() {
        return perspectivesExplorer;
    }

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(MenuFactory.newTopLevelMenu(i18n.getContentExplorerNew())
                                     .withPermission(ActivityResourceType.PERSPECTIVE, PerspectiveAction.CREATE)
                                     .respondsWith(this::createNewPerspective)
                                     .endMenu()
                                     .build());
    }

    public void createNewPerspective() {
        newPerspectivePopUp.show();
    }

    void onAuthzPolicyChanged(@Observes final AuthorizationPolicySavedEvent event) {
        perspectivesExplorer.show();
    }
}