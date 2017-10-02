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

package org.kie.workbench.common.screens.library.client.widgets.library;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.library.client.util.ExamplesUtils;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

public class ImportProjectButtonWidget {

    public interface View extends UberElement<ImportProjectButtonWidget> {

        void clearDropdown();

        void addOption(String description,
                       Command command);

        void addOption(String description,
                       String tooltip,
                       Command command);

        void addHeader(String title);

        void addSeparator();

        String getImportProjectsHeaderTitle();

        String getAdvancedImportDescription();

        String getImportExamplesHeaderTitle();
    }

    private View view;

    private LibraryPlaces libraryPlaces;

    private ProjectController projectController;

    private ExamplesUtils examplesUtils;

    @Inject
    public ImportProjectButtonWidget(final View view,
                                     final LibraryPlaces libraryPlaces,
                                     final ProjectController projectController,
                                     final ExamplesUtils examplesUtils) {
        this.view = view;
        this.libraryPlaces = libraryPlaces;
        this.projectController = projectController;
        this.examplesUtils = examplesUtils;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        initImportProjectDropdown();
    }

    private void initImportProjectDropdown() {
        if (projectController.canCreateProjects()) {
            examplesUtils.getExampleProjects(exampleProjects -> {
                view.clearDropdown();

                view.addHeader(view.getImportProjectsHeaderTitle());

                view.addOption(view.getAdvancedImportDescription(),
                               this::openImportWizard);

                if (exampleProjects != null && !exampleProjects.isEmpty()) {
                    view.addSeparator();
                    view.addHeader(view.getImportExamplesHeaderTitle());

                    for (ExampleProject exampleProject : exampleProjects) {
                        view.addOption(exampleProject.getName(),
                                       exampleProject.getDescription(),
                                       () -> examplesUtils.importProject(exampleProject));
                    }
                }
            });
        }
    }

    private void openImportWizard() {
        libraryPlaces.goToImportProjectWizard();
    }

    public View getView() {
        return view;
    }
}
