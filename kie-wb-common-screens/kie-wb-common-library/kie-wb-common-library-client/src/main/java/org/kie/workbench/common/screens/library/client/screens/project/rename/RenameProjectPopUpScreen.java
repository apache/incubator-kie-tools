/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.project.rename;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.workbench.events.NotificationEvent;

public class RenameProjectPopUpScreen {

    private final Caller<KieModuleService> projectService;
    private View view;
    private ProjectController projectController;
    private TranslationService ts;
    private Event<NotificationEvent> notificationEvent;
    private WorkspaceProject workspaceProject;

    public interface View extends UberElemental<RenameProjectPopUpScreen>,
                                  HasBusyIndicator {

        void show();

        void hide();
    }

    @Inject
    public RenameProjectPopUpScreen(final RenameProjectPopUpScreen.View view,
                                    final Caller<KieModuleService> projectService,
                                    final ProjectController projectController,
                                    final TranslationService ts,
                                    final Event<NotificationEvent> notificationEvent) {
        this.view = view;
        this.projectService = projectService;
        this.projectController = projectController;
        this.ts = ts;
        this.notificationEvent = notificationEvent;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    public void show(final WorkspaceProject projectInfo) {
        if (projectController.canUpdateProject(projectInfo)) {
            this.workspaceProject = projectInfo;
            view.show();
        }
    }

    public void cancel() {
        this.view.hide();
    }

    public void rename(String newName) {
        if (projectController.canUpdateProject(workspaceProject)) {
            this.view.showBusyIndicator(ts.getTranslation(LibraryConstants.Renaming));
            this.projectService.call((Path path) -> {
                this.view.hideBusyIndicator();
                notificationEvent.fire(new NotificationEvent(ts.format(LibraryConstants.RenameSuccess,
                                                                       newName),
                                                             NotificationEvent.NotificationType.SUCCESS));
                this.view.hide();
            }).rename(this.workspaceProject.getMainModule().getPomXMLPath(),
                      newName,
                      "Project renamed to: " + newName);
        }
    }

    public View getView() {
        return this.view;
    }
}
