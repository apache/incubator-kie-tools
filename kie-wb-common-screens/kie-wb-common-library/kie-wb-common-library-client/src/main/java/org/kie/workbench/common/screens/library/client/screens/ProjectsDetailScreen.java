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

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.dataset.events.DataSetModifiedEvent;
import org.dashbuilder.displayer.client.Displayer;
import org.guvnor.common.services.project.model.POM;
import org.kie.workbench.common.screens.contributors.model.ContributorsDataSets;
import org.kie.workbench.common.screens.library.api.ProjectInfo;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.util.ProjectMetricsFactory;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.lifecycle.OnClose;

@WorkbenchScreen(identifier = LibraryPlaces.PROJECT_DETAIL_SCREEN)
public class ProjectsDetailScreen {

    public interface View extends UberElement<ProjectsDetailScreen> {

        void updateDescription(String description);

        void updateContributionsMetric(Displayer metric);

        void setMetricsEnabled(boolean enabled);
    }

    private View view;
    private ProjectMetricsFactory projectMetricsFactory;
    private LibraryPlaces libraryPlaces;
    private ProjectInfo projectInfo;
    private Displayer commitsDisplayer;

    @Inject
    public ProjectsDetailScreen(final View view,
                                final ProjectMetricsFactory projectMetricsFactory,
                                final LibraryPlaces libraryPlaces) {
        this.view = view;
        this.projectMetricsFactory = projectMetricsFactory;
        this.libraryPlaces = libraryPlaces;
        this.view.init(this);
    }

    public void update(@Observes final ProjectDetailEvent event) {
        this.projectInfo = event.getProjectInfo();

        // Update the description
        final POM pom = projectInfo.getProject().getPom();
        if (pom != null && pom.getDescription() != null) {
            view.updateDescription(pom.getDescription());
        } else {
            view.updateDescription("");
        }
        // Update the metrics card
        commitsDisplayer = projectMetricsFactory.lookupCommitsOverTimeDisplayer_small(projectInfo);
        commitsDisplayer.draw();
        view.updateContributionsMetric(commitsDisplayer);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Project Detail Screen";
    }

    @WorkbenchPartView
    public UberElement<ProjectsDetailScreen> getView() {
        return view;
    }

    @OnClose
    public void dispose() {
        if (commitsDisplayer != null) {
            commitsDisplayer.close();
        }
    }

    public void onContributionsUpdated(@Observes DataSetModifiedEvent event) {
        String dsetId = event.getDataSetDef().getUUID();
        if (ContributorsDataSets.GIT_CONTRIB.equals(dsetId) && commitsDisplayer != null) {
            commitsDisplayer.redraw();
        }
    }

    public void setMetricsEnabled(boolean enabled) {
        view.setMetricsEnabled(enabled);
    }

    public void gotoProjectMetrics() {
        if (projectInfo != null) {
            libraryPlaces.goToProjectMetrics(projectInfo);
        }
    }
}
