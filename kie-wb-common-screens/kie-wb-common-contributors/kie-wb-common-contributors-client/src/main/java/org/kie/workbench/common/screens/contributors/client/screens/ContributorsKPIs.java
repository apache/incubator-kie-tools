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
package org.kie.workbench.common.screens.contributors.client.screens;

import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.renderer.client.DefaultRenderer;
import org.kie.workbench.common.screens.contributors.client.resources.i18n.ContributorsI18n;

import static org.dashbuilder.dataset.date.DayOfWeek.SUNDAY;
import static org.dashbuilder.dataset.group.AggregateFunctionType.*;
import static org.dashbuilder.dataset.group.DateIntervalType.*;
import static org.dashbuilder.dataset.sort.SortOrder.*;
import static org.dashbuilder.displayer.Position.RIGHT;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.*;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSets.GIT_CONTRIB;

public class ContributorsKPIs {

    public static DisplayerSettings getCommitsEvolution(ContributorsI18n i18n) {
        return DisplayerSettingsFactory.newAreaChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(COLUMN_DATE, FilterFactory.notNull())
                .group(COLUMN_DATE).dynamic(80, MONTH, true)
                .column(COLUMN_DATE, i18n.date())
                .column(COUNT, "#commits").format(i18n.numberOfCommits(), "#,##0")
                .title(i18n.numberOfCommitsEvolution())
                .titleVisible(true)
                .width(500).height(200)
                .margins(10, 60, 50, 0)
                .filterOff(true)
                .buildSettings();
    }

    public static DisplayerSettings getCommitsPerOrganization(ContributorsI18n i18n) {
        return DisplayerSettingsFactory.newBubbleChartSettings()
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
                .buildSettings();
    }

    public static DisplayerSettings getYears(ContributorsI18n i18n) {
        return DisplayerSettingsFactory.newPieChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(COLUMN_DATE, FilterFactory.notNull())
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
                .buildSettings();
    }

    public static DisplayerSettings getQuarters(ContributorsI18n i18n) {
        return DisplayerSettingsFactory.newPieChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(COLUMN_DATE, FilterFactory.notNull())
                .group(COLUMN_DATE).fixed(QUARTER, false)
                .column(COLUMN_DATE)
                .column(COUNT, "#commits").format(i18n.numberOfCommits(), "#,##0")
                .title(i18n.quarters())
                .titleVisible(false)
                .width(230).height(170)
                .margins(0, 0, 5, 5)
                .legendOn(RIGHT)
                .filterOn(false, true, false)
                .buildSettings();
    }

    public static DisplayerSettings getDaysOfWeek(ContributorsI18n i18n) {
        return DisplayerSettingsFactory.newBarChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(COLUMN_DATE, FilterFactory.notNull())
                .group(COLUMN_DATE).fixed(DAY_OF_WEEK, true).firstDay(SUNDAY)
                .column(COLUMN_DATE)
                .column(COUNT, "#commits").format(i18n.numberOfCommits(), "#,##0")
                .title(i18n.dayOfWeek())
                .titleVisible(false)
                .width(230).height(170)
                .margins(0, 10, 70, 0)
                .subType_Bar()
                .filterOn(false, true, true)
                .buildSettings();
    }

    public static DisplayerSettings getAllCommits(ContributorsI18n i18n) {
        return DisplayerSettingsFactory.newTableSettings()
                .dataset(GIT_CONTRIB)
                .column(COLUMN_AUTHOR, i18n.author())
                .column(COLUMN_REPO, i18n.repository())
                .column(COLUMN_DATE, i18n.date())
                .column(COLUMN_MSG, i18n.commit())
                .title(i18n.commits())
                .titleVisible(false)
                .tablePageSize(10)
                .tableWidth(950)
                .tableOrderEnabled(true)
                .renderer(DefaultRenderer.UUID)
                .filterOn(true, true, true)
                .buildSettings();
    }

    public static DisplayerSettings getOrgUnitSelector(ContributorsI18n i18n) {
        return DisplayerSettingsFactory.newSelectorSettings()
                .dataset(GIT_CONTRIB)
                .group(COLUMN_ORG)
                .column(COLUMN_ORG, i18n.organizationalUnit())
                .column(COUNT, "#commits").format(i18n.numberOfCommits(), "#,##0")
                .title(i18n.organizationalUnit())
                .sort(COLUMN_ORG, ASCENDING)
                .filterOn(false, true, true)
                .buildSettings();
    }

    public static DisplayerSettings getRepoSelector(ContributorsI18n i18n) {
        return DisplayerSettingsFactory.newSelectorSettings()
                .dataset(GIT_CONTRIB)
                .group(COLUMN_REPO)
                .column(COLUMN_REPO, i18n.repository())
                .column(COUNT, "#commits").format(i18n.numberOfCommits(), "#,##0")
                .sort(COLUMN_REPO, ASCENDING)
                .title(i18n.repository())
                .filterOn(false, true, true)
                .buildSettings();
    }

    public static DisplayerSettings getAuthorSelector(ContributorsI18n i18n) {
        return DisplayerSettingsFactory.newSelectorSettings()
                .dataset(GIT_CONTRIB)
                .group(COLUMN_AUTHOR)
                .column(COLUMN_AUTHOR, i18n.author())
                .column(COUNT, "#commits").format(i18n.numberOfCommits(), "#,##0")
                .sort(COLUMN_AUTHOR, ASCENDING)
                .title(i18n.author())
                .filterOn(false, true, true)
                .buildSettings();
    }

    public static DisplayerSettings getTopAuthorSelector(ContributorsI18n i18n) {
        return DisplayerSettingsFactory.newSelectorSettings()
                .dataset(GIT_CONTRIB)
                .group(COLUMN_AUTHOR)
                .column(COLUMN_AUTHOR, i18n.topContributor())
                .column(COUNT, "#commits").format(i18n.numberOfCommits(), "#,##0")
                .sort("#commits", DESCENDING)
                .title(i18n.topContributor())
                .filterOn(false, true, true)
                .buildSettings();
    }
}
