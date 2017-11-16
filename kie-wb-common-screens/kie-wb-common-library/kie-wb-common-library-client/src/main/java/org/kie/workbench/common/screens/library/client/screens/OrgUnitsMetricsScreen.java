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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerCoordinator;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.util.OrgUnitsMetricsFactory;
import org.uberfire.client.mvp.UberElement;

public class OrgUnitsMetricsScreen {

    public interface View extends UberElement<OrgUnitsMetricsScreen> {

        void setCommitsOverTimeDisplayer(Displayer displayer);

        void setCommitsPerAuthorDisplayer(Displayer displayer);

        void setCommitsPerProjectDisplayer(Displayer displayer);

        void setCommitsByYearDisplayer(Displayer displayer);

        void setCommitsByQuarterDisplayer(Displayer displayer);

        void setCommitsByDayOfWeekDisplayer(Displayer displayer);

        void setTopContribSelectorDisplayer(Displayer displayer);

        void setProjectSelectorDisplayer(Displayer displayer);

        void setDateSelectorDisplayer(Displayer displayer);

        void setAllCommitsDisplayer(Displayer displayer);

        void clear();
    }

    View view;
    TranslationService translationService;
    OrgUnitsMetricsFactory metricsFactory;
    DisplayerCoordinator displayerCoordinator;
    LibraryPlaces libraryPlaces;

    OrganizationalUnit organizationalUnit;

    Displayer commitsOverTimeDisplayer;
    Displayer commitsPerAuthorDisplayer;
    Displayer commitsPerProjectDisplayer;
    Displayer commitsByYearDisplayer;
    Displayer commitsByQuarterDisplayer;
    Displayer commitsByDayOfWeekDisplayer;
    Displayer topAuthorSelectorDisplayer;
    Displayer projectSelectorDisplayer;
    Displayer dateSelectorDisplayer;
    Displayer allCommitsDisplayer;

    @Inject
    public OrgUnitsMetricsScreen(final View view,
                                 final TranslationService translationService,
                                 final OrgUnitsMetricsFactory metricsFactory,
                                 final DisplayerCoordinator displayerCoordinator,
                                 final LibraryPlaces libraryPlaces) {
        this.view = view;
        this.translationService = translationService;
        this.metricsFactory = metricsFactory;
        this.displayerCoordinator = displayerCoordinator;
        this.libraryPlaces = libraryPlaces;
    }

    @PostConstruct
    public void init() {
        this.view.init(this);

        this.organizationalUnit = libraryPlaces.getSelectedOrganizationalUnit();

        this.commitsOverTimeDisplayer = metricsFactory.lookupCommitsOverTimeDisplayer(organizationalUnit);
        this.commitsPerAuthorDisplayer = metricsFactory.lookupCommitsPerAuthorDisplayer(organizationalUnit);
        this.commitsPerProjectDisplayer = metricsFactory.lookupCommitsPerProjectDisplayer(organizationalUnit);
        this.commitsByYearDisplayer = metricsFactory.lookupCommitsByYearDisplayer(organizationalUnit);
        this.commitsByQuarterDisplayer = metricsFactory.lookupCommitsByQuarterDisplayer(organizationalUnit);
        this.commitsByDayOfWeekDisplayer = metricsFactory.lookupCommitsByDayOfWeekDisplayer(organizationalUnit);
        this.allCommitsDisplayer = metricsFactory.lookupAllCommitsDisplayer(organizationalUnit);
        this.projectSelectorDisplayer = metricsFactory.lookupProjectSelectorDisplayer(organizationalUnit);
        this.topAuthorSelectorDisplayer = metricsFactory.lookupTopContributorSelectorDisplayer(organizationalUnit);
        this.dateSelectorDisplayer = metricsFactory.lookupDateSelectorDisplayer(organizationalUnit);

        view.clear();
        view.setCommitsPerAuthorDisplayer(commitsPerAuthorDisplayer);
        view.setCommitsPerProjectDisplayer(commitsPerProjectDisplayer);
        view.setCommitsOverTimeDisplayer(commitsOverTimeDisplayer);
        view.setCommitsByYearDisplayer(commitsByYearDisplayer);
        view.setCommitsByQuarterDisplayer(commitsByQuarterDisplayer);
        view.setCommitsByDayOfWeekDisplayer(commitsByDayOfWeekDisplayer);
        view.setAllCommitsDisplayer(allCommitsDisplayer);
        view.setProjectSelectorDisplayer(projectSelectorDisplayer);
        view.setTopContribSelectorDisplayer(topAuthorSelectorDisplayer);
        view.setDateSelectorDisplayer(dateSelectorDisplayer);

        displayerCoordinator.addDisplayer(commitsPerAuthorDisplayer);
        displayerCoordinator.addDisplayer(commitsPerProjectDisplayer);
        displayerCoordinator.addDisplayer(commitsOverTimeDisplayer);
        displayerCoordinator.addDisplayer(commitsByYearDisplayer);
        displayerCoordinator.addDisplayer(commitsByQuarterDisplayer);
        displayerCoordinator.addDisplayer(commitsByDayOfWeekDisplayer);
        displayerCoordinator.addDisplayer(allCommitsDisplayer);
        displayerCoordinator.addDisplayer(topAuthorSelectorDisplayer);
        displayerCoordinator.addDisplayer(projectSelectorDisplayer);
        displayerCoordinator.addDisplayer(dateSelectorDisplayer);
        displayerCoordinator.drawAll();
    }

    public void onClose() {
        displayerCoordinator.closeAll();
        view.clear();
    }

    public Displayer getCommitsOverTimeDisplayer() {
        return commitsOverTimeDisplayer;
    }

    public Displayer getCommitsPerProjectDisplayer() {
        return commitsPerProjectDisplayer;
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

    public Displayer getProjectSelectorDisplayer() {
        return projectSelectorDisplayer;
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

    public View getView() {
        return view;
    }
}
