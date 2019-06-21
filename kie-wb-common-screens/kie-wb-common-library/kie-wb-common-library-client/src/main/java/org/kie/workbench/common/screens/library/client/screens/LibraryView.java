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

import com.google.gwt.event.dom.client.ClickEvent;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class LibraryView implements LibraryScreen.View,
                                    IsElement {

    private LibraryScreen presenter;

    @Inject
    private TranslationService ts;

    @Inject
    @DataField("title")
    Div title;

    @Inject
    @DataField("actions")
    Div actions;

    @Inject
    @DataField("try-samples")
    Anchor trySamples;

    @Inject
    @DataField("import-project")
    Anchor importProject;

    @Inject
    @DataField("delete-project")
    Anchor deleteProject;

    @Inject
    @DataField("projects-tab-container")
    ListItem projectsTabContainer;

    @Inject
    @DataField("projects-tab")
    Anchor projectsTab;

    @Inject
    @DataField("projects-count")
    Span projectsCount;

    @Inject
    @DataField("contributors-tab-container")
    ListItem contributorsTabContainer;

    @Inject
    @DataField("contributors-tab")
    Anchor contributorsTab;

    @Inject
    @DataField("contributors-count")
    Span contributorsCount;

    @Inject
    @DataField("metrics-tab-container")
    ListItem metricsTabContainer;

    @Inject
    @DataField("metrics-tab")
    Anchor metricsTab;

    @Inject
    @DataField("main-container")
    Div mainContainer;

    @Override
    public void init(final LibraryScreen presenter) {
        this.presenter = presenter;

        final boolean userCanCreateProjects = presenter.userCanCreateProjects();
        final boolean userCanDeleteOrganizationalUnit = presenter.userCanDeleteOrganizationalUnit();

        trySamples.setHidden(!userCanCreateProjects);
        importProject.setHidden(!userCanCreateProjects);
        deleteProject.setHidden(!userCanDeleteOrganizationalUnit);

        actions.setHidden(!userCanCreateProjects && !userCanDeleteOrganizationalUnit);
    }

    @EventHandler("try-samples")
    public void trySamples(final ClickEvent event) {
        presenter.trySamples();
    }

    @EventHandler("import-project")
    public void importProject(final ClickEvent event) {
        presenter.importProject();
    }

    @EventHandler("delete-project")
    public void delete(final ClickEvent event) {
        presenter.delete();
    }

    @EventHandler("projects-tab")
    public void showProjects(final ClickEvent event) {
        projectsTabContainer.getClassList().add("active");
        contributorsTabContainer.getClassList().remove("active");
        metricsTabContainer.getClassList().remove("active");

        presenter.showProjects();
    }

    @EventHandler("contributors-tab")
    public void showContributors(final ClickEvent event) {
        projectsTabContainer.getClassList().remove("active");
        contributorsTabContainer.getClassList().add("active");
        metricsTabContainer.getClassList().remove("active");

        presenter.showContributors();
    }

    @EventHandler("metrics-tab")
    public void showMetrics(final ClickEvent event) {
        projectsTabContainer.getClassList().remove("active");
        contributorsTabContainer.getClassList().remove("active");
        metricsTabContainer.getClassList().add("active");

        presenter.showMetrics();
    }

    @Override
    public void setTitle(final String title) {
        this.title.setTextContent(title);
    }

    @Override
    public void setProjectsCount(int count) {
        projectsCount.setTextContent(String.valueOf(count));
    }

    @Override
    public void setContributorsCount(int count) {
        contributorsCount.setTextContent(String.valueOf(count));
    }

    @Override
    public void updateContent(HTMLElement content) {
        mainContainer.setTextContent("");
        mainContainer.appendChild(content);
    }

    @Override
    public boolean isProjectsTabActive() {
        return projectsTabContainer.getClassList().contains("active");
    }

    @Override
    public boolean isContributorsTabActive() {
        return contributorsTabContainer.getClassList().contains("active");
    }

    @Override
    public boolean isMetricsTabActive() {
        return metricsTabContainer.getClassList().contains("active");
    }
}