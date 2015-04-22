/**
 * Copyright (C) 2014 JBoss Inc
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerCoordinator;
import org.dashbuilder.displayer.client.DisplayerHelper;
import org.dashbuilder.renderer.client.DefaultRenderer;
import org.kie.workbench.common.screens.contributors.client.resources.i18n.ContributorsConstants;

import static org.dashbuilder.dataset.date.DayOfWeek.*;
import static org.dashbuilder.dataset.group.DateIntervalType.*;
import static org.dashbuilder.dataset.sort.SortOrder.*;
import static org.dashbuilder.dataset.group.AggregateFunctionType.*;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSets.*;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.*;

/**
 * A dashboard showing KPIs for all the commits over any of the GIT managed repos.
 */
public class ContributorsView extends Composite {

    interface ContributorsViewBinder extends UiBinder<Widget, ContributorsView>{}
    private static final ContributorsViewBinder uiBinder = GWT.create(ContributorsViewBinder.class);

    @UiField(provided = true)
    Displayer areaChartByDate;

    @UiField(provided = true)
    Displayer pieChartYears;

    @UiField(provided = true)
    Displayer pieChartQuarters;

    @UiField(provided = true)
    Displayer barChartDayOfWeek;

    @UiField(provided = true)
    Displayer bubbleChartByOrg;

    @UiField(provided = true)
    Displayer tableAll;

    @UiField(provided = true)
    Displayer orgSelector;

    @UiField(provided = true)
    Displayer repoSelector;

    @UiField(provided = true)
    Displayer authorSelector;

    @UiField(provided = true)
    Displayer topAuthorSelector;

    DisplayerCoordinator dashboard = new DisplayerCoordinator();

    public ContributorsView() {

        // Create the chart definitions

        areaChartByDate = DisplayerHelper.lookupDisplayer(
                DisplayerSettingsFactory.newAreaChartSettings()
                .dataset(ALL)
                .group(COLUMN_DATE).dynamic(80, MONTH, true)
                .column(COLUMN_DATE, ContributorsConstants.INSTANCE.date())
                .column(COUNT, "#commits").format(ContributorsConstants.INSTANCE.numberOfCommits(), "#,##0")
                .title(ContributorsConstants.INSTANCE.numberOfCommitsEvolution())
                .titleVisible(true)
                .width(600).height(200)
                .margins(10, 60, 70, 0)
                .filterOff(true)
                .buildSettings());

        bubbleChartByOrg = DisplayerHelper.lookupDisplayer(
                DisplayerSettingsFactory.newBubbleChartSettings()
                .dataset(ALL)
                .group(COLUMN_ORG)
                .column(COLUMN_ORG, ContributorsConstants.INSTANCE.organizationalUnit())
                .column(COLUMN_REPO, DISTINCT).format(ContributorsConstants.INSTANCE.numberOfRepositories(), "#,##0")
                .column(COUNT, "#commits").format(ContributorsConstants.INSTANCE.numberOfCommits(), "#,##0")
                .column(COLUMN_ORG, ContributorsConstants.INSTANCE.organizationalUnit())
                .column(COLUMN_AUTHOR, DISTINCT).format(ContributorsConstants.INSTANCE.numberOfContributors(), "#,##0")
                .title(ContributorsConstants.INSTANCE.commitsPerOrganization())
                .titleVisible(true)
                .width(400).height(220)
                .margins(10, 50, 70, 0)
                .filterOn(false, true, true)
                .buildSettings());

        pieChartYears = DisplayerHelper.lookupDisplayer(
                DisplayerSettingsFactory.newPieChartSettings()
                .dataset(ALL)
                .group(COLUMN_DATE).dynamic(YEAR, false)
                .column(COLUMN_DATE)
                .column(COUNT, "#commits").format(ContributorsConstants.INSTANCE.numberOfCommits(), "#,##0")
                .sort(COLUMN_DATE, ASCENDING)
                .title(ContributorsConstants.INSTANCE.years())
                .titleVisible(false)
                .width(230).height(170)
                .margins(0, 0, 10, 5)
                .filterOn(false, true, false)
                .buildSettings());

        pieChartQuarters = DisplayerHelper.lookupDisplayer(
                DisplayerSettingsFactory.newPieChartSettings()
                .dataset(ALL)
                .group(COLUMN_DATE).fixed(QUARTER, false)
                .column(COLUMN_DATE)
                .column(COUNT, "#commits").format(ContributorsConstants.INSTANCE.numberOfCommits(), "#,##0")
                .title(ContributorsConstants.INSTANCE.quarters())
                .titleVisible(false)
                .width(230).height(170)
                .margins(0, 0, 5, 5)
                .filterOn(false, true, false)
                .buildSettings());

        barChartDayOfWeek = DisplayerHelper.lookupDisplayer(
                DisplayerSettingsFactory.newBarChartSettings()
                .dataset(ALL)
                .group(COLUMN_DATE).fixed(DAY_OF_WEEK, false).firstDay(SUNDAY)
                .column(COLUMN_DATE)
                .column(COUNT, "#commits").format(ContributorsConstants.INSTANCE.numberOfCommits(), "#,##0")
                .title(ContributorsConstants.INSTANCE.dayOfWeek())
                .titleVisible(false)
                .width(230).height(170)
                .margins(0, 10, 70, 0)
                .filterOn(false, true, true)
                .buildSettings());

        tableAll = DisplayerHelper.lookupDisplayer(
                DisplayerSettingsFactory.newTableSettings()
                .dataset(ALL)
                .column(COLUMN_AUTHOR, ContributorsConstants.INSTANCE.author())
                .column(COLUMN_REPO, ContributorsConstants.INSTANCE.repository())
                .column(COLUMN_DATE, ContributorsConstants.INSTANCE.date())
                .column(COLUMN_MSG, ContributorsConstants.INSTANCE.commit())
                .title(ContributorsConstants.INSTANCE.commits())
                .titleVisible(false)
                .tablePageSize(5)
                .tableWidth(1000)
                .tableOrderEnabled(true)
                .renderer(DefaultRenderer.UUID)
                .filterOn(true, true, true)
                .buildSettings());

        orgSelector = DisplayerHelper.lookupDisplayer(
                DisplayerSettingsFactory.newSelectorSettings()
                .dataset(ALL)
                .group(COLUMN_ORG)
                .column(COLUMN_ORG, ContributorsConstants.INSTANCE.organizationalUnit())
                .column(COUNT, "#commits").format(ContributorsConstants.INSTANCE.numberOfCommits(), "#,##0")
                .title(ContributorsConstants.INSTANCE.organizationalUnit())
                .sort(COLUMN_ORG, ASCENDING)
                .filterOn(false, true, true)
                .buildSettings());

        repoSelector = DisplayerHelper.lookupDisplayer(
                DisplayerSettingsFactory.newSelectorSettings()
                .dataset(ALL)
                .group(COLUMN_REPO)
                .column(COLUMN_REPO, ContributorsConstants.INSTANCE.repository())
                .column(COUNT, "#commits").format(ContributorsConstants.INSTANCE.numberOfCommits(), "#,##0")
                .sort(COLUMN_REPO, ASCENDING)
                .title(ContributorsConstants.INSTANCE.repository())
                .filterOn(false, true, true)
                .buildSettings());

        authorSelector = DisplayerHelper.lookupDisplayer(
                DisplayerSettingsFactory.newSelectorSettings()
                .dataset(ALL)
                .group(COLUMN_AUTHOR)
                .column(COLUMN_AUTHOR, ContributorsConstants.INSTANCE.author())
                .column(COUNT, "#commits").format(ContributorsConstants.INSTANCE.numberOfCommits(), "#,##0")
                .sort(COLUMN_AUTHOR, ASCENDING)
                .title(ContributorsConstants.INSTANCE.author())
                .filterOn(false, true, true)
                .buildSettings());

        topAuthorSelector = DisplayerHelper.lookupDisplayer(
                DisplayerSettingsFactory.newSelectorSettings()
                .dataset(ALL)
                .group(COLUMN_AUTHOR)
                .column(COLUMN_AUTHOR, ContributorsConstants.INSTANCE.topContributor())
                .column(COUNT, "#commits").format(ContributorsConstants.INSTANCE.numberOfCommits(), "#,##0")
                .sort(ContributorsConstants.INSTANCE.numberOfCommits(), DESCENDING)
                .title(ContributorsConstants.INSTANCE.topContributor())
                .filterOn(false, true, true)
                .buildSettings());

        // Make the displayers interact among them
        dashboard.addDisplayer(areaChartByDate);
        dashboard.addDisplayer(pieChartYears);
        dashboard.addDisplayer(pieChartQuarters);
        dashboard.addDisplayer(barChartDayOfWeek);
        dashboard.addDisplayer(bubbleChartByOrg);
        dashboard.addDisplayer(tableAll);
        dashboard.addDisplayer(repoSelector);
        dashboard.addDisplayer(orgSelector);
        dashboard.addDisplayer(authorSelector);
        dashboard.addDisplayer(topAuthorSelector);

        // Init the dashboard from the UI Binder template
        initWidget(uiBinder.createAndBindUi(this));

        // Draw the charts
        dashboard.drawAll();
    }

    public void redraw() {
        dashboard.redrawAll();
    }
}
