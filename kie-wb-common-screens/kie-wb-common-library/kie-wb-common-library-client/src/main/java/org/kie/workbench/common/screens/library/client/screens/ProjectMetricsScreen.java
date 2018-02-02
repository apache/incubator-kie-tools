/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerCoordinator;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.events.WorkbenchProjectMetricsEvent;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.util.ProjectMetricsFactory;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.lifecycle.OnClose;

@Dependent
@WorkbenchScreen(identifier = LibraryPlaces.PROJECT_METRICS_SCREEN)
public class ProjectMetricsScreen {

    public interface View extends UberElement<ProjectMetricsScreen> {

        void setHeaderTitle(String title);

        void setTopContribSelectorDisplayer(Displayer displayer);

        void setDateSelectorDisplayer(Displayer displayer);

        void setCommitsOverTimeDisplayer(Displayer displayer);

        void setCommitsPerAuthorDisplayer(Displayer displayer);

        void setCommitsByYearDisplayer(Displayer displayer);

        void setCommitsByQuarterDisplayer(Displayer displayer);

        void setCommitsByDayOfWeekDisplayer(Displayer displayer);

        void setAllCommitsDisplayer(Displayer displayer);

        void clear();
    }

    View view;
    TranslationService translationService;
    ProjectMetricsFactory metricsFactory;
    DisplayerCoordinator displayerCoordinator;

    WorkspaceProject workspaceProject;

    Displayer commitsOverTimeDisplayer;
    Displayer commitsPerAuthorDisplayer;
    Displayer commitsByYearDisplayer;
    Displayer commitsByQuarterDisplayer;
    Displayer commitsByDayOfWeekDisplayer;
    Displayer topAuthorSelectorDisplayer;
    Displayer dateSelectorDisplayer;
    Displayer allCommitsDisplayer;

    @Inject
    public ProjectMetricsScreen(View view,
                                TranslationService translationService,
                                ProjectMetricsFactory metricsFactory,
                                DisplayerCoordinator displayerCoordinator) {
        this.view = view;
        this.translationService = translationService;
        this.metricsFactory = metricsFactory;
        this.displayerCoordinator = displayerCoordinator;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return translationService.getTranslation(LibraryConstants.ProjectMetrics);
    }

    @WorkbenchPartView
    public UberElement<ProjectMetricsScreen> getView() {
        return view;
    }

    public void onStartup(@Observes final WorkbenchProjectMetricsEvent event) {
        this.view.init(this);

        this.workspaceProject = event.getProject();

        this.commitsOverTimeDisplayer = metricsFactory.lookupCommitsOverTimeDisplayer(workspaceProject);
        this.commitsPerAuthorDisplayer = metricsFactory.lookupCommitsPerAuthorDisplayer(workspaceProject);
        this.commitsByYearDisplayer = metricsFactory.lookupCommitsByYearDisplayer(workspaceProject);
        this.commitsByQuarterDisplayer = metricsFactory.lookupCommitsByQuarterDisplayer(workspaceProject);
        this.commitsByDayOfWeekDisplayer = metricsFactory.lookupCommitsByDayOfWeekDisplayer(workspaceProject);
        this.allCommitsDisplayer = metricsFactory.lookupAllCommitsDisplayer(workspaceProject);
        this.topAuthorSelectorDisplayer = metricsFactory.lookupTopContributorSelectorDisplayer(workspaceProject);
        this.dateSelectorDisplayer = metricsFactory.lookupDateSelectorDisplayer(workspaceProject);

        view.clear();
        view.setCommitsPerAuthorDisplayer(commitsPerAuthorDisplayer);
        view.setCommitsOverTimeDisplayer(commitsOverTimeDisplayer);
        view.setCommitsByYearDisplayer(commitsByYearDisplayer);
        view.setCommitsByQuarterDisplayer(commitsByQuarterDisplayer);
        view.setCommitsByDayOfWeekDisplayer(commitsByDayOfWeekDisplayer);
        view.setAllCommitsDisplayer(allCommitsDisplayer);
        view.setTopContribSelectorDisplayer(topAuthorSelectorDisplayer);
        view.setDateSelectorDisplayer(dateSelectorDisplayer);

        displayerCoordinator.addDisplayer(commitsPerAuthorDisplayer);
        displayerCoordinator.addDisplayer(commitsOverTimeDisplayer);
        displayerCoordinator.addDisplayer(commitsByYearDisplayer);
        displayerCoordinator.addDisplayer(commitsByQuarterDisplayer);
        displayerCoordinator.addDisplayer(commitsByDayOfWeekDisplayer);
        displayerCoordinator.addDisplayer(allCommitsDisplayer);
        displayerCoordinator.addDisplayer(topAuthorSelectorDisplayer);
        displayerCoordinator.addDisplayer(dateSelectorDisplayer);
        displayerCoordinator.drawAll();
    }

    @OnClose
    public void onClose() {
        displayerCoordinator.closeAll();
        view.clear();
    }

    public Displayer getCommitsOverTimeDisplayer() {
        return commitsOverTimeDisplayer;
    }

    public Displayer getCommitsPerAuthorDisplayer() {
        return commitsPerAuthorDisplayer;
    }

    public Displayer getCommitsByYearDisplayer() {
        return commitsByYearDisplayer;
    }

    public Displayer getCommitsByQuarterDisplayer() {
        return commitsByQuarterDisplayer;
    }

    public Displayer getCommitsByDayOfWeekDisplayer() {
        return commitsByDayOfWeekDisplayer;
    }

    public Displayer getTopAuthorSelectorDisplayer() {
        return topAuthorSelectorDisplayer;
    }

    public Displayer getDateSelectorDisplayer() {
        return dateSelectorDisplayer;
    }

    public Displayer getAllCommitsDisplayer() {
        return allCommitsDisplayer;
    }
}
