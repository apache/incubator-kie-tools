/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.widgets;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.api.ProjectInfo;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.util.ResourceUtils;
import org.kie.workbench.common.widgets.client.handlers.NewProjectHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class NewProjectButtonWidget {

    public interface View extends UberElement<NewProjectButtonWidget> {

        void addNewProjectHandler(String description,
                                  NewProjectHandler newProjectHandler);

        void addNewProjectHandler(String description,
                                  Command command);

        void addHeader(String title);

        String getDefaultProjectHeaderTitle();

        String getQuickSetupDescription();

        String getAdvancedSetupDescription();

        String getOtherProjectsHeaderTitle();
    }

    private View view;

    private ManagedInstance<NewProjectHandler> newProjectHandlers;

    private org.kie.workbench.common.screens.projecteditor.client.handlers.NewProjectHandler newDefaultProjectHandler;

    private NewResourcePresenter newResourcePresenter;

    private LibraryPlaces libraryPlaces;

    @Inject
    public NewProjectButtonWidget(final View view,
                                  final ManagedInstance<NewProjectHandler> newProjectHandlers,
                                  final org.kie.workbench.common.screens.projecteditor.client.handlers.NewProjectHandler newDefaultProjectHandler,
                                  final NewResourcePresenter newResourcePresenter,
                                  final LibraryPlaces libraryPlaces) {
        this.view = view;
        this.newProjectHandlers = newProjectHandlers;
        this.newDefaultProjectHandler = newDefaultProjectHandler;
        this.newResourcePresenter = newResourcePresenter;
        this.libraryPlaces = libraryPlaces;
    }

    @PostConstruct
    public void init() {
        view.init(this);

        view.addHeader(view.getDefaultProjectHeaderTitle());

        if (newDefaultProjectHandler.canCreate()) {
            addNewProjectHandler(view.getQuickSetupDescription(),
                    () -> libraryPlaces.goToNewProject());
            addNewProjectHandler(view.getAdvancedSetupDescription(),
                    newDefaultProjectHandler);
        }

        view.addHeader(view.getOtherProjectsHeaderTitle());

        for (NewProjectHandler newProjectHandler : getNewProjectHandlers()) {
            if (!ResourceUtils.isDefaultProjectHandler(newProjectHandler) && newProjectHandler.canCreate()) {
                addNewProjectHandler(newProjectHandler.getDescription(),
                        newProjectHandler);
            }
        }
    }

    private void addNewProjectHandler(final String description,
                                      final NewProjectHandler newProjectHandler) {
        newProjectHandler.setOpenEditorOnCreation(false);
        newProjectHandler.setCreationSuccessCallback(project -> {
            if (project != null) {
                final ProjectInfo projectInfo = new ProjectInfo(libraryPlaces.getSelectedOrganizationalUnit(),
                        libraryPlaces.getSelectedRepository(),
                        libraryPlaces.getSelectedBranch(),
                        project);
                libraryPlaces.goToProject(projectInfo);
            }
        });

        view.addNewProjectHandler(description,
                newProjectHandler);
    }

    private void addNewProjectHandler(final String description,
                                      final Command command) {
        view.addNewProjectHandler(description,
                command);
    }

    public NewResourcePresenter getNewResourcePresenter() {
        return newResourcePresenter;
    }

    public View getView() {
        return view;
    }

    Iterable<NewProjectHandler> getNewProjectHandlers() {
        return newProjectHandlers;
    }
}
