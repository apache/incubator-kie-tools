/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.client.cms.screen.home;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.cms.resources.i18n.ContentManagerI18n;
import org.dashbuilder.client.cms.screen.explorer.PerspectivesExplorerScreen;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.plugin.client.security.PluginController;

@Dependent
@WorkbenchScreen(identifier = ContentManagerHomeScreen.SCREEN_ID)
public class ContentManagerHomeScreen {

    public static final String SCREEN_ID = "ContentManagerHomeScreen";

    public interface View extends UberView<ContentManagerHomeScreen> {

        void setPerspectiveCreationVisible(boolean visible);
    }

    View view;
    PerspectivesExplorerScreen perspectivesExplorerScreen;
    PluginController pluginController;
    ContentManagerI18n i18n;

    @Inject
    public ContentManagerHomeScreen(View view, PerspectivesExplorerScreen perspectivesExplorerScreen, PluginController pluginController, ContentManagerI18n i18n) {
        this.view = view;
        this.perspectivesExplorerScreen = perspectivesExplorerScreen;
        this.pluginController = pluginController;
        this.i18n = i18n;
    }

    @PostConstruct
    private void init() {
        view.init(this);
        view.setPerspectiveCreationVisible(pluginController.canCreatePerspectives());
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return i18n.getContentManagerHome();
    }

    @WorkbenchPartView
    public Widget getWidget() {
        return view.asWidget();
    }

    public void createNewPerspective() {
        if (pluginController.canCreatePerspectives()) {
            perspectivesExplorerScreen.createNewPerspective();
        }
    }
}
