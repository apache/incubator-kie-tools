/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.contributors.client.screens;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.dataset.events.DataSetModifiedEvent;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerCoordinator;
import org.dashbuilder.displayer.client.DisplayerLocator;
import org.dashbuilder.renderer.client.DefaultRenderer;
import org.kie.workbench.common.screens.contributors.client.resources.i18n.ContributorsI18n;
import org.kie.workbench.common.screens.contributors.model.ContributorsDataSets;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.workbench.events.NotificationEvent;

import static org.dashbuilder.dataset.date.DayOfWeek.SUNDAY;
import static org.dashbuilder.dataset.group.AggregateFunctionType.*;
import static org.dashbuilder.dataset.group.DateIntervalType.*;
import static org.dashbuilder.dataset.sort.SortOrder.*;
import static org.dashbuilder.displayer.Position.RIGHT;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.*;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSets.GIT_CONTRIB;
import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.*;

@Dependent
@WorkbenchScreen(identifier = "ContributorsScreen")
public class ContributorsScreen {

    ContributorsView view;
    DisplayerLocator displayerLocator;
    DisplayerCoordinator displayerCoordinator;
    Event<NotificationEvent> workbenchNotification;
    ContributorsI18n i18n;

    Displayer commitsPerOrganization;
    Displayer commitsEvolutionDisplayer;
    Displayer organizationSelectorDisplayer;
    Displayer repositorySelectorDisplayer;
    Displayer authorSelectorDisplayer;
    Displayer topAuthorSelectorDisplayer;
    Displayer yearsSelectorDisplayer;
    Displayer quarterSelectorDisplayer;
    Displayer dayOfWeekSelectorDisplayer;
    Displayer allCommitsDisplayer;

    @Inject
    public ContributorsScreen(ContributorsView view,
                              DisplayerLocator displayerLocator,
                              DisplayerCoordinator displayerCoordinator, 
                              Event<NotificationEvent> workbenchNotification) {
        this.view = view;
        this.i18n = view.getI18nService();
        this.displayerLocator = displayerLocator;
        this.workbenchNotification = workbenchNotification;
        this.displayerCoordinator = displayerCoordinator;

        this.initDisplayers();
        this.initView();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return i18n.contributorsPerspectiveName();
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    public Displayer getCommitsEvolutionDisplayer() {
        return commitsEvolutionDisplayer;
    }

    public Displayer getYearsSelectorDisplayer() {
        return yearsSelectorDisplayer;
    }

    public Displayer getQuarterSelectorDisplayer() {
        return quarterSelectorDisplayer;
    }

    public Displayer getDayOfWeekSelectorDisplayer() {
        return dayOfWeekSelectorDisplayer;
    }

    public Displayer getCommitsPerOrganization() {
        return commitsPerOrganization;
    }

    public Displayer getAllCommitsDisplayer() {
        return allCommitsDisplayer;
    }

    public Displayer getOrganizationSelectorDisplayer() {
        return organizationSelectorDisplayer;
    }

    public Displayer getRepositorySelectorDisplayer() {
        return repositorySelectorDisplayer;
    }

    public Displayer getAuthorSelectorDisplayer() {
        return authorSelectorDisplayer;
    }

    public Displayer getTopAuthorSelectorDisplayer() {
        return topAuthorSelectorDisplayer;
    }

    private void initDisplayers() {

        // Create the chart definitions

        commitsEvolutionDisplayer = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newAreaChartSettings()
                        .dataset(GIT_CONTRIB)
                        .group(COLUMN_DATE).dynamic(80, MONTH, true)
                        .column(COLUMN_DATE, i18n.date())
                        .column(COUNT, "#commits").format(i18n.numberOfCommits(), "#,##0")
                        .title(i18n.numberOfCommitsEvolution())
                        .titleVisible(true)
                        .width(500).height(200)
                        .margins(10, 60, 50, 0)
                        .filterOff(true)
                        .buildSettings());

        commitsPerOrganization = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newBubbleChartSettings()
                        .dataset(GIT_CONTRIB)
                        .group(COLUMN_ORG)
                        .column(COLUMN_ORG, i18n.organizationalUnit())
                        .column(COLUMN_REPO, DISTINCT).format(i18n.numberOfRepositories(), "#,##0")
                        .column(COUNT, "#commits").format(i18n.numberOfCommits(), "#,##0")
                        .column(COLUMN_ORG, i18n.organizationalUnit())
                        .column(COLUMN_AUTHOR, DISTINCT).format(i18n.numberOfContributors(), "#,##0")
                        .title(i18n.commitsPerOrganization())
                        .titleVisible(true)
                        .width(400).height(200)
                        .margins(10, 50, 50, 0)
                        .filterOn(false, true, true)
                        .buildSettings());

        yearsSelectorDisplayer = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newPieChartSettings()
                        .dataset(GIT_CONTRIB)
                        .group(COLUMN_DATE).dynamic(YEAR, false)
                        .column(COLUMN_DATE)
                        .column(COUNT, "#commits").format(i18n.numberOfCommits(), "#,##0")
                        .sort(COLUMN_DATE, ASCENDING)
                        .title(i18n.years())
                        .titleVisible(false)
                        .width(230).height(170)
                        .margins(0, 0, 10, 5)
                        .legendOn(RIGHT)
                        .filterOn(false, true, false)
                        .buildSettings());

        quarterSelectorDisplayer = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newPieChartSettings()
                        .dataset(GIT_CONTRIB)
                        .group(COLUMN_DATE).fixed(QUARTER, false)
                        .column(COLUMN_DATE)
                        .column(COUNT, "#commits").format(i18n.numberOfCommits(), "#,##0")
                        .title(i18n.quarters())
                        .titleVisible(false)
                        .width(230).height(170)
                        .margins(0, 0, 5, 5)
                        .legendOn(RIGHT)
                        .filterOn(false, true, false)
                        .buildSettings());

        dayOfWeekSelectorDisplayer = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newBarChartSettings()
                        .dataset(GIT_CONTRIB)
                        .group(COLUMN_DATE).fixed(DAY_OF_WEEK, false).firstDay(SUNDAY)
                        .column(COLUMN_DATE)
                        .column(COUNT, "#commits").format(i18n.numberOfCommits(), "#,##0")
                        .title(i18n.dayOfWeek())
                        .titleVisible(false)
                        .width(230).height(170)
                        .margins(0, 10, 70, 0)
                        .subType_Bar()
                        .filterOn(false, true, true)
                        .buildSettings());

        allCommitsDisplayer = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newTableSettings()
                        .dataset(GIT_CONTRIB)
                        .column(COLUMN_AUTHOR, i18n.author())
                        .column(COLUMN_REPO, i18n.repository())
                        .column(COLUMN_DATE, i18n.date())
                        .column(COLUMN_MSG, i18n.commit())
                        .title(i18n.commits())
                        .titleVisible(false)
                        .tablePageSize(5)
                        .tableWidth(950)
                        .tableOrderEnabled(true)
                        .renderer(DefaultRenderer.UUID)
                        .filterOn(true, true, true)
                        .buildSettings());

        organizationSelectorDisplayer = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newSelectorSettings()
                        .dataset(GIT_CONTRIB)
                        .group(COLUMN_ORG)
                        .column(COLUMN_ORG, i18n.organizationalUnit())
                        .column(COUNT, "#commits").format(i18n.numberOfCommits(), "#,##0")
                        .title(i18n.organizationalUnit())
                        .sort(COLUMN_ORG, ASCENDING)
                        .filterOn(false, true, true)
                        .buildSettings());

        repositorySelectorDisplayer = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newSelectorSettings()
                        .dataset(GIT_CONTRIB)
                        .group(COLUMN_REPO)
                        .column(COLUMN_REPO, i18n.repository())
                        .column(COUNT, "#commits").format(i18n.numberOfCommits(), "#,##0")
                        .sort(COLUMN_REPO, ASCENDING)
                        .title(i18n.repository())
                        .filterOn(false, true, true)
                        .buildSettings());

        authorSelectorDisplayer = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newSelectorSettings()
                        .dataset(GIT_CONTRIB)
                        .group(COLUMN_AUTHOR)
                        .column(COLUMN_AUTHOR, i18n.author())
                        .column(COUNT, "#commits").format(i18n.numberOfCommits(), "#,##0")
                        .sort(COLUMN_AUTHOR, ASCENDING)
                        .title(i18n.author())
                        .filterOn(false, true, true)
                        .buildSettings());

        topAuthorSelectorDisplayer = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newSelectorSettings()
                        .dataset(GIT_CONTRIB)
                        .group(COLUMN_AUTHOR)
                        .column(COLUMN_AUTHOR, i18n.topContributor())
                        .column(COUNT, "#commits").format(i18n.numberOfCommits(), "#,##0")
                        .sort("#commits", DESCENDING)
                        .title(i18n.topContributor())
                        .filterOn(false, true, true)
                        .buildSettings());

        // Make the displayers interact among them
        displayerCoordinator.addDisplayer(commitsEvolutionDisplayer);
        displayerCoordinator.addDisplayer(yearsSelectorDisplayer);
        displayerCoordinator.addDisplayer(quarterSelectorDisplayer);
        displayerCoordinator.addDisplayer(dayOfWeekSelectorDisplayer);
        displayerCoordinator.addDisplayer(commitsPerOrganization);
        displayerCoordinator.addDisplayer(allCommitsDisplayer);
        displayerCoordinator.addDisplayer(repositorySelectorDisplayer);
        displayerCoordinator.addDisplayer(organizationSelectorDisplayer);
        displayerCoordinator.addDisplayer(authorSelectorDisplayer);
        displayerCoordinator.addDisplayer(topAuthorSelectorDisplayer);

        // Draw the charts
        displayerCoordinator.drawAll();
    }

    private void initView() {
        view.init(this,
                commitsPerOrganization,
                commitsEvolutionDisplayer,
                organizationSelectorDisplayer,
                repositorySelectorDisplayer,
                authorSelectorDisplayer,
                topAuthorSelectorDisplayer,
                yearsSelectorDisplayer,
                quarterSelectorDisplayer,
                dayOfWeekSelectorDisplayer,
                allCommitsDisplayer);
    }

    public void redraw() {
        displayerCoordinator.redrawAll();
    }

    /**
     * Catch any changes on the contributors data set and update the dashboard properly.
     */
    private void onContributorsDataSetOutdated(@Observes DataSetModifiedEvent event) {
        checkNotNull("event", event);

        String targetUUID = event.getDataSetDef().getUUID();
        if (ContributorsDataSets.GIT_CONTRIB.equals(targetUUID)) {
            workbenchNotification.fire(new NotificationEvent(i18n.contributorsDataSetOutdated(), INFO));
            redraw();
        }
    }
}
