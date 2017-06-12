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

package org.kie.workbench.common.screens.library.client.screens;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.library.client.util.ExamplesUtils;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;

@WorkbenchScreen(identifier = LibraryPlaces.EMPTY_LIBRARY_SCREEN)
public class EmptyLibraryScreen {

    public interface View extends UberElement<EmptyLibraryScreen> {

        void setup(String username);

        void addProjectToImport(ExampleProject exampleProject);

        void clearImportExamplesButtonsContainer();

        void clearImportExamplesContainer();
    }

    private View view;

    private User user;

    private LibraryPlaces libraryPlaces;

    private ExamplesUtils examplesUtils;

    private ProjectController projectController;

    @Inject
    public EmptyLibraryScreen(final View view,
                              final User user,
                              final LibraryPlaces libraryPlaces,
                              final ExamplesUtils examplesUtils,
                              final ProjectController projectController) {
        this.view = view;
        this.user = user;
        this.libraryPlaces = libraryPlaces;
        this.examplesUtils = examplesUtils;
        this.projectController = projectController;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
        view.setup(user.getIdentifier());
        examplesUtils.getExampleProjects(exampleProjects -> {
            if (exampleProjects != null && !exampleProjects.isEmpty()) {
                view.clearImportExamplesButtonsContainer();
                for (ExampleProject exampleProject : exampleProjects) {
                    view.addProjectToImport(exampleProject);
                }
            } else {
                view.clearImportExamplesContainer();
            }
        });
    }

    public void newProject() {
        if (userCanCreateProjects()) {
            libraryPlaces.goToNewProject();
        }
    }

    public void importProject(final ExampleProject exampleProject) {
        if (userCanCreateProjects()) {
            examplesUtils.importProject(exampleProject);
        }
    }

    public boolean userCanCreateProjects() {
        return projectController.canCreateProjects();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Empty Library Screen";
    }

    @WorkbenchPartView
    public UberElement<EmptyLibraryScreen> getView() {
        return view;
    }
}
