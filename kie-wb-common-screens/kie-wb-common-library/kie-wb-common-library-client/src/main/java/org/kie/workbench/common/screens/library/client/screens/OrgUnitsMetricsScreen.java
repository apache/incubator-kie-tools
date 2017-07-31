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
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerCoordinator;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.util.OrgUnitsMetricsFactory;
import org.kie.workbench.common.screens.library.client.util.TranslationUtils;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.lifecycle.OnClose;

@Dependent
@WorkbenchScreen(identifier = LibraryPlaces.ORG_UNITS_METRICS_SCREEN)
public class OrgUnitsMetricsScreen {

    public interface View extends UberElement<OrgUnitsMetricsScreen> {

        void setHeaderTitle(String title);

        void setCommitsOverTimeDisplayer(Displayer displayer);

        void setCommitsPerAuthorDisplayer(Displayer displayer);

        void setCommitsPerOrgUnitDisplayer(Displayer displayer, String title);

        void setCommitsPerProjectDisplayer(Displayer displayer);

        void setCommitsByYearDisplayer(Displayer displayer);

        void setCommitsByQuarterDisplayer(Displayer displayer);

        void setCommitsByDayOfWeekDisplayer(Displayer displayer);

        void setTopContribSelectorDisplayer(Displayer displayer);

        void setOrgUnitSelectorDisplayer(Displayer displayer);

        void setProjectSelectorDisplayer(Displayer displayer);

        void setDateSelectorDisplayer(Displayer displayer);

        void setAllCommitsDisplayer(Displayer displayer);

        void clear();

    }

    View view;
    TranslationService translationService;
    TranslationUtils translationUtils;
    DisplayerCoordinator displayerCoordinator;
    OrgUnitsMetricsFactory metricsFactory;
    Displayer commitsOverTimeDisplayer;
    Displayer commitsPerAuthorDisplayer;
    Displayer commitsPerOrgUnitDisplayer;
    Displayer commitsPerProjectDisplayer;
    Displayer commitsByYearDisplayer;
    Displayer commitsByQuarterDisplayer;
    Displayer commitsByDayOfWeekDisplayer;
    Displayer topAuthorSelectorDisplayer;
    Displayer orgUnitSelectorDisplayer;
    Displayer projectSelectorDisplayer;
    Displayer dateSelectorDisplayer;
    Displayer allCommitsDisplayer;

    @Inject
    public OrgUnitsMetricsScreen(View view,
                                 TranslationService translationService,
                                 TranslationUtils translationUtils,
                                 OrgUnitsMetricsFactory metricsFactory,
                                 DisplayerCoordinator displayerCoordinator) {
        this.view = view;
        this.translationService = translationService;
        this.translationUtils = translationUtils;
        this.metricsFactory = metricsFactory;
        this.displayerCoordinator = displayerCoordinator;
        this.view.init(this);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return translationService.format(LibraryConstants.OrgUnitsMetrics, translationUtils.getOrganizationalUnitAliasInPlural());
    }

    @WorkbenchPartView
    public UberElement<OrgUnitsMetricsScreen> getView() {
        return view;
    }

    @PostConstruct
    public void init() {
        String orgUnitsAlias = translationUtils.getOrganizationalUnitAliasInPlural();
        String orgUnitAlias = translationUtils.getOrganizationalUnitAliasInSingular();

        this.commitsOverTimeDisplayer = metricsFactory.lookupCommitsOverTimeDisplayer();
        this.commitsPerOrgUnitDisplayer = metricsFactory.lookupCommitsPerOrgUnitDisplayer();
        this.commitsPerAuthorDisplayer = metricsFactory.lookupCommitsPerAuthorDisplayer();
        this.commitsPerProjectDisplayer = metricsFactory.lookupCommitsPerProjectDisplayer();
        this.commitsByYearDisplayer = metricsFactory.lookupCommitsByYearDisplayer();
        this.commitsByQuarterDisplayer = metricsFactory.lookupCommitsByQuarterDisplayer();
        this.commitsByDayOfWeekDisplayer = metricsFactory.lookupCommitsByDayOfWeekDisplayer();
        this.allCommitsDisplayer = metricsFactory.lookupAllCommitsDisplayer();
        this.projectSelectorDisplayer = metricsFactory.lookupProjectSelectorDisplayer();
        this.orgUnitSelectorDisplayer = metricsFactory.lookupOrgUnitSelectorDisplayer();
        this.topAuthorSelectorDisplayer = metricsFactory.lookupTopContributorSelectorDisplayer();
        this.dateSelectorDisplayer = metricsFactory.lookupDateSelectorDisplayer();

        view.clear();
        view.setHeaderTitle(translationService.format(LibraryConstants.MetricsTitle, orgUnitsAlias));
        view.setCommitsPerAuthorDisplayer(commitsPerAuthorDisplayer);
        view.setCommitsPerOrgUnitDisplayer(commitsPerOrgUnitDisplayer, translationService.format(LibraryConstants.PerOrgUnit, orgUnitAlias));
        view.setCommitsPerProjectDisplayer(commitsPerProjectDisplayer);
        view.setCommitsOverTimeDisplayer(commitsOverTimeDisplayer);
        view.setCommitsByYearDisplayer(commitsByYearDisplayer);
        view.setCommitsByQuarterDisplayer(commitsByQuarterDisplayer);
        view.setCommitsByDayOfWeekDisplayer(commitsByDayOfWeekDisplayer);
        view.setAllCommitsDisplayer(allCommitsDisplayer);
        view.setOrgUnitSelectorDisplayer(orgUnitSelectorDisplayer);
        view.setProjectSelectorDisplayer(projectSelectorDisplayer);
        view.setTopContribSelectorDisplayer(topAuthorSelectorDisplayer);
        view.setDateSelectorDisplayer(dateSelectorDisplayer);

        displayerCoordinator.addDisplayer(commitsPerAuthorDisplayer);
        displayerCoordinator.addDisplayer(commitsPerOrgUnitDisplayer);
        displayerCoordinator.addDisplayer(commitsPerProjectDisplayer);
        displayerCoordinator.addDisplayer(commitsOverTimeDisplayer);
        displayerCoordinator.addDisplayer(commitsByYearDisplayer);
        displayerCoordinator.addDisplayer(commitsByQuarterDisplayer);
        displayerCoordinator.addDisplayer(commitsByDayOfWeekDisplayer);
        displayerCoordinator.addDisplayer(allCommitsDisplayer);
        displayerCoordinator.addDisplayer(topAuthorSelectorDisplayer);
        displayerCoordinator.addDisplayer(orgUnitSelectorDisplayer);
        displayerCoordinator.addDisplayer(projectSelectorDisplayer);
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
}
