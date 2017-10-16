/**
 * Copyright (C) 2014 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerCoordinator;
import org.dashbuilder.displayer.client.DisplayerLocator;
import org.kie.workbench.common.screens.contributors.client.resources.i18n.ContributorsI18n;
import org.kie.workbench.common.screens.contributors.model.ContributorsDataSets;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.*;

@Dependent
@WorkbenchScreen(identifier = "ContributorsScreen")
public class ContributorsScreen {

    ContributorsView view;
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

        this(view,
             displayerLocator.lookupDisplayer(ContributorsKPIs.getCommitsPerOrganization(view.getI18nService())),
             displayerLocator.lookupDisplayer(ContributorsKPIs.getCommitsEvolution(view.getI18nService())),
             displayerLocator.lookupDisplayer(ContributorsKPIs.getOrgUnitSelector(view.getI18nService())),
             displayerLocator.lookupDisplayer(ContributorsKPIs.getRepoSelector(view.getI18nService())),
             displayerLocator.lookupDisplayer(ContributorsKPIs.getAuthorSelector(view.getI18nService())),
             displayerLocator.lookupDisplayer(ContributorsKPIs.getTopAuthorSelector(view.getI18nService())),
             displayerLocator.lookupDisplayer(ContributorsKPIs.getYears(view.getI18nService())),
             displayerLocator.lookupDisplayer(ContributorsKPIs.getQuarters(view.getI18nService())),
             displayerLocator.lookupDisplayer(ContributorsKPIs.getDaysOfWeek(view.getI18nService())),
             displayerLocator.lookupDisplayer(ContributorsKPIs.getAllCommits(view.getI18nService())),
             displayerCoordinator,
             workbenchNotification);
    }

    public ContributorsScreen(ContributorsView view,
                              Displayer commitsPerOrganization,
                              Displayer commitsEvolutionDisplayer,
                              Displayer organizationSelectorDisplayer,
                              Displayer repositorySelectorDisplayer,
                              Displayer authorSelectorDisplayer,
                              Displayer topAuthorSelectorDisplayer,
                              Displayer yearsSelectorDisplayer,
                              Displayer quarterSelectorDisplayer,
                              Displayer dayOfWeekSelectorDisplayer,
                              Displayer allCommitsDisplayer,
                              DisplayerCoordinator displayerCoordinator,
                              Event<NotificationEvent> workbenchNotification) {

        this.view = view;
        this.i18n = view.getI18nService();
        this.workbenchNotification = workbenchNotification;
        this.displayerCoordinator = displayerCoordinator;
        this.commitsPerOrganization = commitsPerOrganization;
        this.commitsEvolutionDisplayer = commitsEvolutionDisplayer;
        this.organizationSelectorDisplayer = organizationSelectorDisplayer;
        this.repositorySelectorDisplayer = repositorySelectorDisplayer;
        this.authorSelectorDisplayer = authorSelectorDisplayer;
        this.topAuthorSelectorDisplayer = topAuthorSelectorDisplayer;
        this.yearsSelectorDisplayer = yearsSelectorDisplayer;
        this.quarterSelectorDisplayer = quarterSelectorDisplayer;
        this.dayOfWeekSelectorDisplayer = dayOfWeekSelectorDisplayer;
        this.allCommitsDisplayer = allCommitsDisplayer;

        // Make the displayers interact among them
        displayerCoordinator.addDisplayer(commitsPerOrganization);
        displayerCoordinator.addDisplayer(commitsEvolutionDisplayer);
        displayerCoordinator.addDisplayer(organizationSelectorDisplayer);
        displayerCoordinator.addDisplayer(repositorySelectorDisplayer);
        displayerCoordinator.addDisplayer(authorSelectorDisplayer);
        displayerCoordinator.addDisplayer(topAuthorSelectorDisplayer);
        displayerCoordinator.addDisplayer(yearsSelectorDisplayer);
        displayerCoordinator.addDisplayer(quarterSelectorDisplayer);
        displayerCoordinator.addDisplayer(dayOfWeekSelectorDisplayer);
        displayerCoordinator.addDisplayer(allCommitsDisplayer);

        // Draw everything
        displayerCoordinator.drawAll();

        // Init the view
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

    public void redraw() {
        displayerCoordinator.redrawAll();
    }

    /**
     * Catch any changes on the contributors data set and update the dashboard properly.
     */
    private void onContributorsDataSetOutdated(@Observes DataSetModifiedEvent event) {
        checkNotNull("event",
                     event);

        String targetUUID = event.getDataSetDef().getUUID();
        if (ContributorsDataSets.GIT_CONTRIB.equals(targetUUID)) {
            workbenchNotification.fire(new NotificationEvent(i18n.contributorsDataSetOutdated(),
                                                             INFO));
            redraw();
        }
    }
}
