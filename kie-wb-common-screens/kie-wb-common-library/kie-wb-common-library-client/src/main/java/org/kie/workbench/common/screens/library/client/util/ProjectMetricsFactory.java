/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.library.client.util;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerLocator;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;

import static org.dashbuilder.dataset.date.DayOfWeek.SUNDAY;
import static org.dashbuilder.dataset.filter.FilterFactory.AND;
import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.dashbuilder.dataset.group.AggregateFunctionType.COUNT;
import static org.dashbuilder.dataset.group.AggregateFunctionType.DISTINCT;
import static org.dashbuilder.dataset.group.DateIntervalType.DAY_OF_WEEK;
import static org.dashbuilder.dataset.group.DateIntervalType.MONTH;
import static org.dashbuilder.dataset.group.DateIntervalType.QUARTER;
import static org.dashbuilder.dataset.group.DateIntervalType.YEAR;
import static org.dashbuilder.dataset.sort.SortOrder.ASCENDING;
import static org.dashbuilder.dataset.sort.SortOrder.DESCENDING;
import static org.dashbuilder.displayer.Position.RIGHT;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_AUTHOR;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_DATE;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_MSG;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_ORG;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_PROJECT;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.COLUMN_REPO;
import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSets.GIT_CONTRIB;

@ApplicationScoped
public class ProjectMetricsFactory {

    private TranslationService translationService;
    private DisplayerLocator displayerLocator;

    @Inject
    public ProjectMetricsFactory(TranslationService translationService,
                                 DisplayerLocator displayerLocator) {
        this.translationService = translationService;
        this.displayerLocator = displayerLocator;
    }

    public Displayer lookupCommitsOverTimeDisplayer(WorkspaceProject project) {
        DisplayerSettings settings = buildCommitsOverTimeSettings(project);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupCommitsOverTimeDisplayer_small(WorkspaceProject project) {
        DisplayerSettings settings = buildCommitsOverTimeSettings(project);
        settings.setChartWidth(300);
        settings.setChartHeight(80);
        settings.setChartMarginTop(5);
        settings.setChartMarginBottom(5);
        settings.setTitleVisible(false);
        settings.setYAxisTitle(null);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupCommitsPerAuthorDisplayer(WorkspaceProject project) {
        DisplayerSettings settings = buildCommitsPerAuthorSettings(project);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupCommitsByYearDisplayer(WorkspaceProject project) {
        DisplayerSettings settings = buildCommitsByYearSettings(project);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupCommitsByQuarterDisplayer(WorkspaceProject project) {
        DisplayerSettings settings = buildCommitsByQuarterSettings(project);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupCommitsByDayOfWeekDisplayer(WorkspaceProject project) {
        DisplayerSettings settings = buildCommitsByDayOfWeekSettings(project);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupTopContributorSelectorDisplayer(WorkspaceProject project) {
        DisplayerSettings settings = buildTopContributorSelectorSettings(project);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupDateSelectorDisplayer(WorkspaceProject project) {
        DisplayerSettings settings = buildDateSelectorSettings(project);
        return displayerLocator.lookupDisplayer(settings);
    }

    public Displayer lookupAllCommitsDisplayer(WorkspaceProject project) {
        DisplayerSettings settings = buildAllCommitsSettings(project);
        return displayerLocator.lookupDisplayer(settings);
    }

    protected ColumnFilter createProjectFilter(WorkspaceProject project) {
        String repoAlias = project.getRepository().getAlias();
        String projectName = project.getName();
        String space = project.getOrganizationalUnit().getName();
        return AND(equalsTo(COLUMN_REPO,
                            repoAlias),
                   equalsTo(COLUMN_PROJECT,
                            projectName),
                   equalsTo(COLUMN_ORG,
                            space));
    }

    public DisplayerSettings buildCommitsPerAuthorSettings(WorkspaceProject project) {
        return DisplayerSettingsFactory.newBubbleChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(createProjectFilter(project))
                .group(COLUMN_AUTHOR)
                .column(COLUMN_AUTHOR, translationService.getTranslation(LibraryConstants.Author))
                .column(COUNT, "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits), "#,##0")
                .column(COUNT, "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits), "#,##0")
                .column(COLUMN_AUTHOR, translationService.getTranslation(LibraryConstants.Author))
                .column(COLUMN_AUTHOR, DISTINCT).format(translationService.getTranslation(LibraryConstants.Author), "#,##0")
                .titleVisible(false)
                .width(450).height(200)
                .margins(10, 40, 40, 0)
                .xAxisTitle(translationService.getTranslation(LibraryConstants.NumberOfCommits))
                .yAxisTitle(translationService.getTranslation(LibraryConstants.NumberOfCommits))
                .filterOff(true)
                .buildSettings();
    }

    public DisplayerSettings buildCommitsOverTimeSettings(WorkspaceProject project) {
        return DisplayerSettingsFactory.newAreaChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(createProjectFilter(project))
                .group(COLUMN_DATE).dynamic(80,
                                            MONTH,
                                            true)
                .column(COLUMN_DATE,
                        translationService.getTranslation(LibraryConstants.Date))
                .column(COUNT,
                        "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits),
                                           "#,##0")
                .titleVisible(false)
                .width(450).height(145)
                .margins(10,
                         5,
                         40,
                         0)
                .yAxisTitle(translationService.getTranslation(LibraryConstants.NumberOfCommits))
                .filterOff(true)
                .buildSettings();
    }

    public DisplayerSettings buildCommitsByYearSettings(WorkspaceProject project) {
        return DisplayerSettingsFactory.newPieChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(COLUMN_DATE,
                        FilterFactory.notNull())
                .filter(createProjectFilter(project))
                .group(COLUMN_DATE).dynamic(YEAR,
                                            false)
                .column(COLUMN_DATE)
                .column(COUNT,
                        "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits),
                                           "#,##0")
                .sort(COLUMN_DATE,
                      ASCENDING)
                .title(translationService.getTranslation(LibraryConstants.Years))
                .titleVisible(false)
                .width(250).height(170)
                .margins(10,
                         0,
                         10,
                         5)
                .legendOn(RIGHT)
                .filterOn(false, true, true)
                .buildSettings();
    }

    public DisplayerSettings buildCommitsByQuarterSettings(WorkspaceProject project) {
        return DisplayerSettingsFactory.newPieChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(COLUMN_DATE,
                        FilterFactory.notNull())
                .filter(createProjectFilter(project))
                .group(COLUMN_DATE).fixed(QUARTER,
                                          false)
                .column(COLUMN_DATE)
                .column(COUNT,
                        "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits),
                                           "#,##0")
                .title(translationService.getTranslation(LibraryConstants.Quarters))
                .titleVisible(false)
                .width(250).height(170)
                .margins(10,
                         0,
                         5,
                         5)
                .legendOn(RIGHT)
                .filterOn(false, true, true)
                .buildSettings();
    }

    public DisplayerSettings buildCommitsByDayOfWeekSettings(WorkspaceProject project) {
        return DisplayerSettingsFactory.newBarChartSettings()
                .dataset(GIT_CONTRIB)
                .filter(COLUMN_DATE,
                        FilterFactory.notNull())
                .filter(createProjectFilter(project))
                .group(COLUMN_DATE).fixed(DAY_OF_WEEK,
                                          true).firstDay(SUNDAY)
                .column(COLUMN_DATE)
                .column(COUNT,
                        "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits),
                                           "#,##0")
                .title(translationService.getTranslation(LibraryConstants.DayOfWeek))
                .titleVisible(false)
                .width(250).height(170)
                .margins(10, 40, 70, 0)
                .subType_Bar()
                .xAxisTitle(translationService.getTranslation(LibraryConstants.NumberOfCommits))
                .filterOn(false,
                          true,
                          true)
                .buildSettings();
    }

    public DisplayerSettings buildTopContributorSelectorSettings(WorkspaceProject project) {
        return DisplayerSettingsFactory.newSelectorSettings()
                .dataset(GIT_CONTRIB)
                .filter(createProjectFilter(project))
                .group(COLUMN_AUTHOR)
                .column(COLUMN_AUTHOR,
                        translationService.getTranslation(LibraryConstants.TopContributor))
                .column(COUNT,
                        "#commits").format(translationService.getTranslation(LibraryConstants.NumberOfCommits),
                                           "#,##0")
                .sort("#commits",
                      DESCENDING)
                .subtype(DisplayerSubType.SELECTOR_DROPDOWN).multiple(true)
                .titleVisible(false)
                .margins(0,
                         0,
                         10,
                         0)
                .width(200)
                .filterOn(false,
                          true,
                          true)
                .buildSettings();
    }

    public DisplayerSettings buildDateSelectorSettings(WorkspaceProject project) {
        return DisplayerSettingsFactory.newSelectorSettings()
                .dataset(GIT_CONTRIB)
                .filter(createProjectFilter(project))
                .column(COLUMN_DATE).format(translationService.getTranslation(LibraryConstants.Date), "dd MMM, yyyy HH:mm")
                .subtype(DisplayerSubType.SELECTOR_SLIDER)
                .titleVisible(false)
                .width(420)
                .margins(0,
                         0,
                         20,
                         10)
                .filterOn(false,
                          true,
                          true)
                .buildSettings();
    }

    public DisplayerSettings buildAllCommitsSettings(WorkspaceProject project) {
        return DisplayerSettingsFactory.newTableSettings()
                .dataset(GIT_CONTRIB)
                .filter(createProjectFilter(project))
                .column(COLUMN_AUTHOR,
                        translationService.getTranslation(LibraryConstants.Author))
                .column(COLUMN_DATE,
                        translationService.getTranslation(LibraryConstants.Date))
                .column(COLUMN_MSG,
                        translationService.getTranslation(LibraryConstants.Message))
                .sort(COLUMN_DATE,
                      DESCENDING)
                .tablePageSize(10)
                .tableWidth(950)
                .tableOrderEnabled(true)
                .tableColumnPickerEnabled(true)
                .allowExcelExport(true)
                .allowCsvExport(true)
                .filterOff(true)
                .buildSettings();
    }
}
