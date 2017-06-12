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

import javax.inject.Inject;

import com.google.gwt.user.client.Event;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.widgets.library.ImportProjectButtonWidget;
import org.kie.workbench.common.screens.library.client.widgets.library.NewProjectButtonWidget;
import org.kie.workbench.common.screens.library.client.widgets.library.ProjectItemWidget;
import org.uberfire.mvp.Command;

@Templated
public class LibraryView implements LibraryScreen.View,
                                    IsElement {

    private LibraryScreen presenter;

    @Inject
    private ProjectsDetailScreen projectsDetailScreen;

    @Inject
    private TranslationService ts;

    @Inject
    private ManagedInstance<ProjectItemWidget> itemWidgetsInstances;

    @Inject
    private NewProjectButtonWidget newProjectButtonWidget;

    @Inject
    private ImportProjectButtonWidget importProjectButtonWidget;

    @Inject
    @DataField("main-container")
    Div mainContainer;

    @Inject
    @DataField("details-container")
    Div detailsContainer;

    @Inject
    @DataField("project-list")
    Div projectList;

    @Inject
    @DataField("filter-text")
    Input filterText;

    @Inject
    @DataField("create-project-container")
    Span createProjectContainer;

    private ProjectItemWidget selectedProjectItemWidget;

    private boolean importProjectLoaded = false;

    @Override
    public void init(LibraryScreen presenter) {
        this.presenter = presenter;
        this.selectedProjectItemWidget = null;
        filterText.setAttribute("placeholder",
                                ts.getTranslation(LibraryConstants.FilterByName));
        detailsContainer.appendChild(projectsDetailScreen.getView().getElement());
        if (presenter.userCanCreateProjects()) {
            createProjectContainer.appendChild(newProjectButtonWidget.getView().getElement());
            createProjectContainer.appendChild(importProjectButtonWidget.getView().getElement());
        }
    }

    @Override
    public void clearProjects() {
        DOMUtil.removeAllChildren(projectList);
    }

    @Override
    public void addProject(final String project,
                           final Command details,
                           final Command select) {
        ProjectItemWidget projectItemWidget = itemWidgetsInstances.get();
        projectItemWidget.init(project,
                               detailsCommand(details,
                                              projectItemWidget),
                               select);
        projectList.appendChild(projectItemWidget.getElement());
    }

    private Command detailsCommand(final Command details,
                                   final ProjectItemWidget projectItemWidget) {
        return () -> {
            details.execute();

            if (projectItemWidget.equals(selectedProjectItemWidget)) {
                mainContainer.getClassList().add("col-md-12");
                mainContainer.getClassList().add("col-lg-12");
                mainContainer.getClassList().remove("col-md-8");
                mainContainer.getClassList().remove("col-lg-9");
                detailsContainer.getClassList().add("hidden");
                detailsContainer.getClassList().remove("col-md-4");
                detailsContainer.getClassList().remove("col-lg-3");
                projectItemWidget.unselect();
                this.selectedProjectItemWidget = null;
            } else {
                if (selectedProjectItemWidget == null) {
                    mainContainer.getClassList().remove("col-md-12");
                    mainContainer.getClassList().remove("col-lg-12");
                    mainContainer.getClassList().add("col-md-8");
                    mainContainer.getClassList().add("col-lg-9");
                    detailsContainer.getClassList().remove("hidden");
                    detailsContainer.getClassList().add("col-md-4");
                    detailsContainer.getClassList().add("col-lg-3");
                } else {
                    selectedProjectItemWidget.unselect();
                }

                projectItemWidget.select();
                this.selectedProjectItemWidget = projectItemWidget;
            }
        };
    }

    @Override
    public void clearFilterText() {
        this.filterText.setValue("");
    }

    @Override
    public void setFilterName(String name) {
        this.filterText.setValue(name);
    }

    @SinkNative(Event.ONKEYUP)
    @EventHandler("filter-text")
    public void filterTextChange(Event e) {
        presenter.filterProjects(filterText.getValue());
    }
}